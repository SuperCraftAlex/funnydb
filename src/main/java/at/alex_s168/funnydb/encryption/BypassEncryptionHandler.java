package at.alex_s168.funnydb.encryption;

public class BypassEncryptionHandler implements EncryptionHandler {
    @Override
    public byte[] encrypt(byte[] in) {
        return in;
    }

    @Override
    public byte[] decrypt(byte[] in) {
        return in;
    }
}
