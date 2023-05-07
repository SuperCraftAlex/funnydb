package at.alex_s168.funnydb.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class AESEncryptionHandler implements EncryptionHandler {

    private final SecretKey key;
    private final Cipher cipher;

    public AESEncryptionHandler(SecretKey key) throws Exception {
        this.key = key;
        cipher = Cipher.getInstance("AES");
    }

    @Override
    public byte[] encrypt(byte[] in) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(in);
    }

    @Override
    public byte[] decrypt(byte[] in) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(in);
    }

    /**
     * Recommended keysize: 128
     */
    public static SecretKey genKey(int keysize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

}
