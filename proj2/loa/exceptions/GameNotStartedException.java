/**
 *
 */
package loa.exceptions;

/**
 * @author William Hebgen Guss
 */
public class GameNotStartedException extends GameException {

    /**
     * The default serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new not started exception.
     */
    public GameNotStartedException() {
        super("game not started.");
    }

}
