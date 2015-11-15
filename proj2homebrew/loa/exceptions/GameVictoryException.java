/**
 * 
 */
package loa.exceptions;

import loa.Player;

/**
 * @author william
 *
 */
public class GameVictoryException extends GameException {

    /**
     * @param message
     */
    public GameVictoryException(Player victor) {
        super(victor.team().toString() + "was victorius.");
        // TODO Auto-generated constructor stub
    }

}
