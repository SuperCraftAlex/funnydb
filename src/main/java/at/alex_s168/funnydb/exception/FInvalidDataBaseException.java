package at.alex_s168.funnydb.exception;

public class FInvalidDataBaseException extends Exception {

    public FInvalidDataBaseException(String path) {
        super("Requested Database "+path+" does not exist!");
    }

}
