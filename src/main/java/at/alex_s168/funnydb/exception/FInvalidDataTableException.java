package at.alex_s168.funnydb.exception;

public class FInvalidDataTableException extends Exception {

    public FInvalidDataTableException(String t) {
        super("Requested Table '"+t+"' was not found in the Database!");
    }

}
