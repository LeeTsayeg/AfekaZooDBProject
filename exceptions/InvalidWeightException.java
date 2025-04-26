package lee_tsayeg_rotem_boltanski.exceptions;

public class InvalidWeightException extends Exception {
    private static final String PRE_ERROR = "The weight you entered is out of range.";

    public InvalidWeightException(String message) {
        super(PRE_ERROR + message);
    }
}
