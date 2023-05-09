package at.alex_s168.funnydb.exception;

public class FEncryptionException extends Exception {

    public FEncryptionException(Exception exception) {
        super("Error whilest encoding/decoding database: " + exception);
    }

}
