package lee_tsayeg_rotem_boltanski.exceptions;

public class InvalidGenderException extends Exception {
    private static final String PRE_ERROR = "The gender you entered is not valid.";

    public InvalidGenderException(String message) {
        super(PRE_ERROR + message);
    }
}
