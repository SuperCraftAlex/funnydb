package at.alex_s168.funnydb.encryption;

import at.alex_s168.funnydb.exception.FEncryptionException;

public interface EncryptionHandler {

    byte[] encrypt(byte[] in) throws FEncryptionException;

    byte[] decrypt(byte[] in) throws FEncryptionException;

}
