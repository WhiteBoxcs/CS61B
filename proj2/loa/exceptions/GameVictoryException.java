/**
 *
 */
package loa.exceptions;

import loa.players.Player;
import loa.util.StringTools;

/**
 * @author william
 */
public class GameVictoryException extends GameException {

    /**
     * @param message
     */
    public GameVictoryException(Player victor) {
        super(StringTools.capitalizeFirstLetter(victor.team().fullName())
                + " wins.");
        this.setError(false);
    }

}
