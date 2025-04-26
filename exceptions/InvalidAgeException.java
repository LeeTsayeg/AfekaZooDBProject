package lee_tsayeg_rotem_boltanski.exceptions;

public class InvalidAgeException extends Exception {
    private static final String PRE_ERROR = "The age you entered is out of range.";

    public InvalidAgeException(String message) {
        super(PRE_ERROR + message);
    }
}
