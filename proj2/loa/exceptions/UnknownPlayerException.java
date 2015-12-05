/**
 *
 */
package loa.exceptions;

import loa.Piece;

/**
 * @author William Hebgen Guss
 */
public class UnknownPlayerException extends GameException {

    /**
     * Creates a new unknown player exception.
     * @param player
     *            the player which is not known to the game.
     */
    public UnknownPlayerException(Piece player) {
        super("unknown player: " + player.fullName());
    }

}
