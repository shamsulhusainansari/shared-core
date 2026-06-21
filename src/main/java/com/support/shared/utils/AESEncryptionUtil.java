package com.support.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for AES encryption and decryption operations.
 * Supports AES-256-GCM and AES-256-CBC modes.
 * 
 * @author Shared Core Team
 * @version 1.0.0
 */
public class AESEncryptionUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(AESEncryptionUtil.class);
    
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String AES_CBC_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int CBC_IV_LENGTH = 16;
    
    /**
     * Generates a new AES-256 secret key.
     * 
     * @return SecretKey object
     * @throws Exception if key generation fails
     */
    public static SecretKey generateAES256Key() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE, new SecureRandom());
        return keyGenerator.generateKey();
    }
    
    /**
     * Converts a string key to SecretKey using SHA-256 hash.
     * 
     * @param key the string key
     * @return SecretKey object
     * @throws Exception if conversion fails
     */
    public static SecretKey getKeyFromString(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(key.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
    
    /**
     * Encrypts plaintext using AES-256-GCM mode.
     * GCM mode provides both confidentiality and authenticity.
     * 
     * @param plaintext the text to encrypt
     * @param secretKey the secret key
     * @return Base64 encoded encrypted text with IV prepended
     * @throws Exception if encryption fails
     */
    public static String encryptAES256GCM(String plaintext, SecretKey secretKey) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        
        // Prepend IV to encrypted data
        byte[] encryptedWithIv = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedWithIv, iv.length, encryptedBytes.length);
        
        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }
    
    /**
     * Decrypts ciphertext using AES-256-GCM mode.
     * 
     * @param ciphertext Base64 encoded encrypted text with IV prepended
     * @param secretKey the secret key
     * @return decrypted plaintext
     * @throws Exception if decryption fails
     */
    public static String decryptAES256GCM(String ciphertext, SecretKey secretKey) throws Exception {
        byte[] encryptedWithIv = Base64.getDecoder().decode(ciphertext);
        
        // Extract IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
        
        // Extract encrypted data
        byte[] encryptedBytes = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);
        
        Cipher cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Encrypts plaintext using AES-256-CBC mode.
     * 
     * @param plaintext the text to encrypt
     * @param secretKey the secret key
     * @return Base64 encoded encrypted text with IV prepended
     * @throws Exception if encryption fails
     */
    public static String encryptAES256CBC(String plaintext, SecretKey secretKey) throws Exception {
        byte[] iv = new byte[CBC_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(AES_CBC_TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        
        // Prepend IV to encrypted data
        byte[] encryptedWithIv = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedWithIv, iv.length, encryptedBytes.length);
        
        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }
    
    /**
     * Decrypts ciphertext using AES-256-CBC mode.
     * 
     * @param ciphertext Base64 encoded encrypted text with IV prepended
     * @param secretKey the secret key
     * @return decrypted plaintext
     * @throws Exception if decryption fails
     */
    public static String decryptAES256CBC(String ciphertext, SecretKey secretKey) throws Exception {
        byte[] encryptedWithIv = Base64.getDecoder().decode(ciphertext);
        
        // Extract IV
        byte[] iv = new byte[CBC_IV_LENGTH];
        System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
        
        // Extract encrypted data
        byte[] encryptedBytes = new byte[encryptedWithIv.length - CBC_IV_LENGTH];
        System.arraycopy(encryptedWithIv, CBC_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);
        
        Cipher cipher = Cipher.getInstance(AES_CBC_TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Encrypts plaintext using AES-256-GCM with a string key.
     * 
     * @param plaintext the text to encrypt
     * @param key the string key (will be hashed to 256 bits)
     * @return Base64 encoded encrypted text
     * @throws Exception if encryption fails
     */
    public static String encryptWithStringKey(String plaintext, String key) throws Exception {
        SecretKey secretKey = getKeyFromString(key);
        return encryptAES256GCM(plaintext, secretKey);
    }
    
    /**
     * Decrypts ciphertext using AES-256-GCM with a string key.
     * 
     * @param ciphertext Base64 encoded encrypted text
     * @param key the string key (will be hashed to 256 bits)
     * @return decrypted plaintext
     * @throws Exception if decryption fails
     */
    public static String decryptWithStringKey(String ciphertext, String key) throws Exception {
        SecretKey secretKey = getKeyFromString(key);
        return decryptAES256GCM(ciphertext, secretKey);
    }
    
    /**
     * Generates a SHA-256 hash of the input string.
     * 
     * @param input the input string
     * @return Base64 encoded hash
     * @throws Exception if hashing fails
     */
    public static String generateSHA256Hash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
    
    /**
     * Generates a SHA-256 hash in hexadecimal format.
     * 
     * @param input the input string
     * @return hexadecimal hash string
     * @throws Exception if hashing fails
     */
    public static String generateSHA256HashHex(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

