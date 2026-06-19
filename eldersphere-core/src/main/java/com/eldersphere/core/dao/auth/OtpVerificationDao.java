package com.eldersphere.core.dao.auth;

import com.eldersphere.core.entities.OtpVerification;
import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.eldersphere.core.enums.OtpPurpose;
import com.eldersphere.core.exceptions.BadRequestException;
import com.eldersphere.core.exceptions.ElderSphereException;
import com.eldersphere.core.repository.OtpVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@Slf4j
public class OtpVerificationDao {

    private final OtpVerificationRepository otpVerificationRepository;

    public OtpVerificationDao(OtpVerificationRepository otpVerificationRepository) {
        this.otpVerificationRepository = otpVerificationRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public OtpVerification saveOtp(OtpVerification otp) throws ElderSphereException {
        try {
            return otpVerificationRepository.save(otp);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate OTP entry for identifier {}: {}", otp.getIdentifier(), e.getMessage());
            throw BadRequestException.conflict("An active OTP already exists for this identifier and purpose");
        } catch (Exception e) {
            log.error("Failed to save OTP: {}", e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.INTERNAL_SERVER_ERROR, "Failed to generate OTP");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public OtpVerification markAsUsed(OtpVerification otp) throws ElderSphereException {
        try {
            otp.setUsed(true);
            return otpVerificationRepository.save(otp);
        } catch (Exception e) {
            log.error("Failed to mark OTP as used for identifier {}: {}", otp.getIdentifier(), e.getMessage());
            throw new ElderSphereException(ExceptionCodeEnum.FAILED_TO_UPDATE, "Failed to verify OTP");
        }
    }

    public Optional<OtpVerification> findValidOtp(String identifier, OtpPurpose purpose) {
        return otpVerificationRepository.findByIdentifierAndPurposeAndUsedFalseAndExpiresAtAfter(
                identifier, purpose, Instant.now());
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteByIdentifierAndPurpose(String identifier, OtpPurpose purpose) {
        try {
            otpVerificationRepository.deleteByIdentifierAndPurpose(identifier, purpose);
        } catch (Exception e) {
            log.warn("Failed to delete OTP for identifier {} purpose {}: {}", identifier, purpose, e.getMessage());
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteAllExpired() {
        try {
            otpVerificationRepository.deleteAllExpired(Instant.now());
        } catch (Exception e) {
            log.warn("Failed to purge expired OTPs: {}", e.getMessage());
        }
    }
}
