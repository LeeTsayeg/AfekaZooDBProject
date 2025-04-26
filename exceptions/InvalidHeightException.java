package lee_tsayeg_rotem_boltanski.exceptions;

public class InvalidHeightException extends Exception {
    private static final String PRE_ERROR = "The height you entered is out of range.";

    public InvalidHeightException(String message) {
        super(PRE_ERROR + message);
    }
}
