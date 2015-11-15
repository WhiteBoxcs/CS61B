/**
 * 
 */
package loa.exceptions;

/**
 * @author William Hebgen Guss
 *
 */
public class UnknownPlayerException extends GameException {

    /**
     * Creates a new unknown player exception.
     * @param player the player which is not known to the game.
     */
    public UnknownPlayerException(String player) {
        super("unknown player: " + player);
        // TODO Auto-generated constructor stub
    }

}
