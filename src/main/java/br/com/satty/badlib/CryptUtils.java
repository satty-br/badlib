package br.com.satty.badlib;

import br.com.satty.badlib.dto.CryptObj;
import br.com.satty.badlib.dto.UserConfig;


import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Cipher;

import static br.com.satty.badlib.FileUtils.getStringConentFromFile;

public class CryptUtils {
    protected static CryptObj cryptObject(UserConfig config, String id, String ret) throws Exception {
        return new CryptObj(id, crypt(config,ret));
    }

    private static String crypt(UserConfig config, String ret) throws Exception {
        PublicKey pbk = getPublicKey(config);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pbk);
        byte[] encryptedBytes = cipher.doFinal(ret.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    private static PublicKey getPublicKey(UserConfig config)  throws Exception {
        if (config.getPbkey() == null) {
            throw new RuntimeException("Public key has not been generated or loaded.");
        }
        byte[] publicKeyBytes = Base64.getDecoder().decode(readBase64File(config.getPbkey()));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey getPrivateKey(UserConfig config) throws Exception {
        if (config.getPbkey() == null) {
            throw new RuntimeException("Public key has not been generated or loaded.");
        }
        byte[] privateKeyBytes = Base64.getDecoder().decode(readBase64File(config.getPbkey()));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    protected static String decrypt(UserConfig config, String ciphertext)  throws Exception {
        PrivateKey privateKey = getPrivateKey(config);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
        return new String(decryptedBytes);
    }


    private static String readBase64File(String file)  throws Exception {
        return Objects.requireNonNull(getStringConentFromFile(new File(file))).toString();
    }

    protected static KeyPair generateKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static String prkTobase64(PrivateKey privateKey) {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    public static String pbkTobase64(PublicKey aPublic) {
        byte[] publicbytes = aPublic.getEncoded();
        return Base64.getEncoder().encodeToString(publicbytes);
    }

    public static String stringTobase64(String key) {
        byte[] bytes = key.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String cryptObject(UserConfig config, String ret) throws Exception {
        return crypt(config,ret);
    }
}
