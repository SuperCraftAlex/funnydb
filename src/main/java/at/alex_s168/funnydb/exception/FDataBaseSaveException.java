package at.alex_s168.funnydb.exception;

public class FDataBaseSaveException extends Exception {

    public FDataBaseSaveException(Exception exception) {
        super("Error whilest saving: " + exception);
    }

}
