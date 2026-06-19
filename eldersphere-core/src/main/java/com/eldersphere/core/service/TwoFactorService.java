package com.eldersphere.core.service;

import com.eldersphere.core.dao.auth.OtpVerificationDao;
import com.eldersphere.core.dao.auth.UserSecuritySettingsDao;
import com.eldersphere.core.entities.OtpVerification;
import com.eldersphere.core.entities.User;
import com.eldersphere.core.entities.UserSecuritySettings;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.enums.OtpPurpose;
import com.eldersphere.core.enums.TwoFactorMethod;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.exceptions.UnauthorizedException;
import com.eldersphere.core.utils.AesEncryptionUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwoFactorService {

    private final UserSecuritySettingsDao userSecuritySettingsDao;
    private final OtpVerificationDao otpVerificationDao;
    private final SmsService smsService;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder otpEncoder = new BCryptPasswordEncoder();

    @Value("${totp.encryption-key}")
    private String totpEncryptionKey;

    @Value("${totp.issuer:ElderSphere}")
    private String issuer;

    private static final String PENDING_PREFIX = "2fa_pending:";
    private static final long PENDING_TTL_SECONDS = 300;
    private static final long OTP_EXPIRY_MINUTES = 10;

    // ── TOTP ─────────────────────────────────────────────────────────────

    /** Step 1 of TOTP setup: generate secret + QR URI. Does NOT enable yet. */
    public TotpSetupResult initiateTotp(User user, UserSecuritySettings settings) {
        String secret = new DefaultSecretGenerator().generate();
        settings.setTotpSecretEncrypted(AesEncryptionUtil.encrypt(secret, totpEncryptionKey));
        settings.setTwoFactorMethod(TwoFactorMethod.TOTP);
        userSecuritySettingsDao.updateSettings(settings);

        String label = user.getEmail() != null ? user.getEmail() : user.getPhone();
        QrData qrData = new QrData.Builder()
                .label(label)
                .secret(secret)
                .issuer(issuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        String qrCodeBase64 = generateQrCodeBase64(qrData.getUri());
        return new TotpSetupResult(secret, qrData.getUri(), qrCodeBase64);
    }

    private String generateQrCodeBase64(String uri) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(
                    uri,
                    BarcodeFormat.QR_CODE,
                    300, 300,
                    Map.of(EncodeHintType.MARGIN, 1)
            );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            log.error("Failed to generate QR code image: {}", e.getMessage());
            return null;
        }
    }

    /** Step 2 of TOTP setup: verify code and enable. */
    public void confirmTotp(UserSecuritySettings settings, String code) {
        if (settings.getTotpSecretEncrypted() == null) {
            throw BadRequestException.badRequest("TOTP setup not initiated. Call setup first.");
        }
        String secret = AesEncryptionUtil.decrypt(settings.getTotpSecretEncrypted(), totpEncryptionKey);
        if (!verifyRawTotpCode(secret, code)) {
            throw BadRequestException.badRequest("Invalid TOTP code");
        }
        settings.setTotpEnabled(true);
        userSecuritySettingsDao.updateSettings(settings);
    }

    /**
     * Verify TOTP code during login.
     * Accepts the encrypted secret from the DB.
     */
    public boolean verifyTotpCode(String encryptedSecret, String code) {
        String secret;
        try {
            secret = AesEncryptionUtil.decrypt(encryptedSecret, totpEncryptionKey);
        } catch (Exception e) {
            return false;
        }
        return verifyRawTotpCode(secret, code);
    }

    private boolean verifyRawTotpCode(String rawSecret, String code) {
        try {
            DefaultCodeVerifier verifier = new DefaultCodeVerifier(
                    new DefaultCodeGenerator(), new SystemTimeProvider());
            return verifier.isValidCode(rawSecret, code);
        } catch (Exception e) {
            return false;
        }
    }

    // ── OTP (SMS / EMAIL) ─────────────────────────────────────────────────

    /** Initiate SMS or EMAIL OTP setup — sends the code. */
    public void initiateOtpSetup(User user, UserSecuritySettings settings, TwoFactorMethod method) {
        if (method == TwoFactorMethod.TOTP) {
            throw BadRequestException.badRequest("Use /setup/totp for TOTP setup");
        }
        String identifier = resolveIdentifier(user, method);
        otpVerificationDao.deleteByIdentifierAndPurpose(identifier, OtpPurpose.TWO_FA_SETUP);

        String otp = generateOtp();
        saveOtp(identifier, otp, OtpPurpose.TWO_FA_SETUP);
        sendOtp(user, method, otp, "ElderSphere 2FA Setup");

        settings.setTwoFactorMethod(method);
        userSecuritySettingsDao.updateSettings(settings);
    }

    /** Confirm SMS/EMAIL OTP setup — enables the method. */
    public void confirmOtpSetup(User user, UserSecuritySettings settings, String code) {
        TwoFactorMethod method = settings.getTwoFactorMethod();
        if (method == null || method == TwoFactorMethod.TOTP) {
            throw BadRequestException.badRequest("No SMS/EMAIL setup in progress");
        }
        String identifier = resolveIdentifier(user, method);
        verifyStoredOtp(identifier, code, OtpPurpose.TWO_FA_SETUP);
        settings.setTotpEnabled(true);
        userSecuritySettingsDao.updateSettings(settings);
    }

    /** Send OTP for login (called when SMS/EMAIL 2FA is enabled and user logs in). */
    public void sendLoginOtp(User user, TwoFactorMethod method) {
        String identifier = resolveIdentifier(user, method);
        otpVerificationDao.deleteByIdentifierAndPurpose(identifier, OtpPurpose.LOGIN);
        String otp = generateOtp();
        saveOtp(identifier, otp, OtpPurpose.LOGIN);
        sendOtp(user, method, otp, "ElderSphere Login Code");
    }

    /** Verify login OTP (SMS/EMAIL) during second factor step. */
    public void verifyLoginOtp(User user, TwoFactorMethod method, String code) {
        String identifier = resolveIdentifier(user, method);
        verifyStoredOtp(identifier, code, OtpPurpose.LOGIN);
    }

    // ── Pending token (mid-login Redis token) ────────────────────────────

    public String storePendingToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                PENDING_PREFIX + token,
                String.valueOf(userId),
                PENDING_TTL_SECONDS,
                TimeUnit.SECONDS);
        return token;
    }

    public Long resolvePendingToken(String token) {
        String val = redisTemplate.opsForValue().get(PENDING_PREFIX + token);
        if (val == null) {
            throw UnauthorizedException.of("Invalid or expired 2FA session. Please log in again.");
        }
        return Long.parseLong(val);
    }

    public void deletePendingToken(String token) {
        redisTemplate.delete(PENDING_PREFIX + token);
    }

    // ── Disable ───────────────────────────────────────────────────────────

    public void disable(UserSecuritySettings settings) {
        settings.setTotpEnabled(false);
        settings.setTotpSecretEncrypted(null);
        settings.setTwoFactorMethod(null);
        userSecuritySettingsDao.updateSettings(settings);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    public boolean isTwoFactorEnabled(UserSecuritySettings settings) {
        return settings != null && settings.isTotpEnabled();
    }

    private String resolveIdentifier(User user, TwoFactorMethod method) {
        if (method == TwoFactorMethod.SMS) {
            if (user.getPhone() == null) throw BadRequestException.badRequest("No phone number on account");
            return user.getPhone();
        }
        if (user.getEmail() == null) throw BadRequestException.badRequest("No email on account");
        return user.getEmail();
    }

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    private void saveOtp(String identifier, String rawOtp, OtpPurpose purpose) {
        OtpVerification otp = OtpVerification.builder()
                .identifier(identifier)
                .otpHash(otpEncoder.encode(rawOtp))
                .purpose(purpose)
                .expiresAt(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60))
                .used(false)
                .build();
        otpVerificationDao.saveOtp(otp);
    }

    private void verifyStoredOtp(String identifier, String code, OtpPurpose purpose) {
        OtpVerification record = otpVerificationDao.findValidOtp(identifier, purpose)
                .orElseThrow(() -> BadRequestException.badRequest("OTP not found or expired"));
        if (!otpEncoder.matches(code, record.getOtpHash())) {
            throw BadRequestException.badRequest("Invalid OTP");
        }
        otpVerificationDao.markAsUsed(record);
    }

    private void sendOtp(User user, TwoFactorMethod method, String otp, String subject) {
        String message = "Your " + issuer + " verification code is: " + otp +
                ". Valid for " + OTP_EXPIRY_MINUTES + " minutes.";
        if (method == TwoFactorMethod.SMS) {
            smsService.send(user.getPhone(), message);
        } else {
            sendEmail(user.getEmail(), subject, message);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.PROCESSING_ERROR, "Failed to send verification email");
        }
    }
}
