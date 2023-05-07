package at.alex_s168.funnydb.encryption;

public interface EncryptionHandler {

    byte[] encrypt(byte[] in) throws Exception;

    byte[] decrypt(byte[] in) throws Exception;

}
