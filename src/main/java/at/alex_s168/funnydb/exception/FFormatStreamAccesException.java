package at.alex_s168.funnydb.exception;

public class FFormatStreamAccesException extends Exception {

    public FFormatStreamAccesException(String where) {
        super("You are not allowed to use this Method in '"+where+"'!");
    }

}
