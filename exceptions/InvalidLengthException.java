package lee_tsayeg_rotem_boltanski.exceptions;

public class InvalidLengthException extends Exception {
    private static final String PRE_ERROR = "The length you entered is out of range.";

    public InvalidLengthException(String message) {
        super(PRE_ERROR + message);
    }
}
