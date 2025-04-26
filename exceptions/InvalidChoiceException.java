package lee_tsayeg_rotem_boltanski.exceptions;

public class InvalidChoiceException extends Exception {
    private static final String PRE_ERROR = "The Choice you entered is not valid.";

    public InvalidChoiceException(String message) {
        super(PRE_ERROR + message);
    }
}
