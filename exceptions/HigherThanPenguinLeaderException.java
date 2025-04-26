package lee_tsayeg_rotem_boltanski.exceptions;

public class HigherThanPenguinLeaderException extends Exception {
    private static final String PRE_ERROR = "The height you entered is bigger than the leader height.";

    public HigherThanPenguinLeaderException(String message) {
        super(PRE_ERROR + message);
    }
}
