package com.eldersphere.core.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

public class AesEncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public static String encrypt(String plaintext, String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes());
            byte[] combined = ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv).put(encrypted).array();
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String ciphertext, String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            byte[] combined = Base64.getDecoder().decode(ciphertext);
            ByteBuffer bb = ByteBuffer.wrap(combined);
            byte[] iv = new byte[GCM_IV_LENGTH];
            bb.get(iv);
            byte[] encrypted = new byte[bb.remaining()];
            bb.get(encrypted);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
