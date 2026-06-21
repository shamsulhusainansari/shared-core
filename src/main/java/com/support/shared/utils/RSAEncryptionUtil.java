package com.support.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Utility class for RSA encryption and decryption operations.
 * Supports RSA key pair generation, encryption, decryption, and key management.
 * 
 * @author Shared Core Team
 * @version 1.0.0
 */
public class RSAEncryptionUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(RSAEncryptionUtil.class);
    
    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String RSA_OAEP_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int RSA_KEY_SIZE = 2048;
    
    /**
     * Generates an RSA key pair with 2048-bit key size.
     * 
     * @return KeyPair containing public and private keys
     * @throws Exception if key generation fails
     */
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
    
    /**
     * Generates an RSA key pair with custom key size.
     * 
     * @param keySize the key size in bits (recommended: 2048 or 4096)
     * @return KeyPair containing public and private keys
     * @throws Exception if key generation fails
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }
    
    /**
     * Converts a public key to Base64 encoded string.
     * 
     * @param publicKey the public key
     * @return Base64 encoded public key
     */
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    /**
     * Converts a private key to Base64 encoded string.
     * 
     * @param privateKey the private key
     * @return Base64 encoded private key
     */
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
    
    /**
     * Converts a Base64 encoded string to PublicKey.
     * 
     * @param publicKeyString Base64 encoded public key
     * @return PublicKey object
     * @throws Exception if conversion fails
     */
    public static PublicKey stringToPublicKey(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(spec);
    }
    
    /**
     * Converts a Base64 encoded string to PrivateKey.
     * 
     * @param privateKeyString Base64 encoded private key
     * @return PrivateKey object
     * @throws Exception if conversion fails
     */
    public static PrivateKey stringToPrivateKey(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }
    
    /**
     * Encrypts plaintext using RSA public key with PKCS1 padding.
     * 
     * @param plaintext the text to encrypt
     * @param publicKey the public key
     * @return Base64 encoded encrypted text
     * @throws Exception if encryption fails
     */
    public static String encrypt(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * Decrypts ciphertext using RSA private key with PKCS1 padding.
     * 
     * @param ciphertext Base64 encoded encrypted text
     * @param privateKey the private key
     * @return decrypted plaintext
     * @throws Exception if decryption fails
     */
    public static String decrypt(String ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Encrypts plaintext using RSA public key with OAEP padding (more secure).
     * 
     * @param plaintext the text to encrypt
     * @param publicKey the public key
     * @return Base64 encoded encrypted text
     * @throws Exception if encryption fails
     */
    public static String encryptOAEP(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * Decrypts ciphertext using RSA private key with OAEP padding.
     * 
     * @param ciphertext Base64 encoded encrypted text
     * @param privateKey the private key
     * @return decrypted plaintext
     * @throws Exception if decryption fails
     */
    public static String decryptOAEP(String ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Encrypts plaintext using RSA public key string with PKCS1 padding.
     * 
     * @param plaintext the text to encrypt
     * @param publicKeyString Base64 encoded public key
     * @return Base64 encoded encrypted text
     * @throws Exception if encryption fails
     */
    public static String encryptWithStringKey(String plaintext, String publicKeyString) throws Exception {
        PublicKey publicKey = stringToPublicKey(publicKeyString);
        return encrypt(plaintext, publicKey);
    }
    
    /**
     * Decrypts ciphertext using RSA private key string with PKCS1 padding.
     * 
     * @param ciphertext Base64 encoded encrypted text
     * @param privateKeyString Base64 encoded private key
     * @return decrypted plaintext
     * @throws Exception if decryption fails
     */
    public static String decryptWithStringKey(String ciphertext, String privateKeyString) throws Exception {
        PrivateKey privateKey = stringToPrivateKey(privateKeyString);
        return decrypt(ciphertext, privateKey);
    }
    
    /**
     * Signs data using RSA private key with SHA-256.
     * 
     * @param data the data to sign
     * @param privateKey the private key
     * @return Base64 encoded signature
     * @throws Exception if signing fails
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
    
    /**
     * Verifies a signature using RSA public key with SHA-256.
     * 
     * @param data the original data
     * @param signatureString Base64 encoded signature
     * @param publicKey the public key
     * @return true if signature is valid, false otherwise
     * @throws Exception if verification fails
     */
    public static boolean verify(String data, String signatureString, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signatureString);
        return signature.verify(signatureBytes);
    }
    
    /**
     * Container class for storing RSA key pair as strings.
     */
    public static class RSAKeyPairStrings {
        private final String publicKey;
        private final String privateKey;
        
        public RSAKeyPairStrings(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
        
        public String getPublicKey() {
            return publicKey;
        }
        
        public String getPrivateKey() {
            return privateKey;
        }
    }
    
    /**
     * Generates an RSA key pair and returns as Base64 encoded strings.
     * 
     * @return RSAKeyPairStrings containing public and private keys as strings
     * @throws Exception if key generation fails
     */
    public static RSAKeyPairStrings generateRSAKeyPairStrings() throws Exception {
        KeyPair keyPair = generateRSAKeyPair();
        String publicKey = publicKeyToString(keyPair.getPublic());
        String privateKey = privateKeyToString(keyPair.getPrivate());
        return new RSAKeyPairStrings(publicKey, privateKey);
    }
}

