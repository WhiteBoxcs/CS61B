/**
 *
 */
package loa.exceptions;

import loa.Move;

/**
 * @author William Hebgen Guss An invalid move exception
 */
public class InvalidMoveException extends GameException {
    private Move _move;

    /**
     * Creates an invalid move exception.
     * @param move
     *            The move which caused the error.
     */
    public InvalidMoveException(String exception, Move move) {
        super(exception);
        this._move = move;
    }

    /**
     * Causes an invalid move exception.
     * @param move
     *            The move which caused the error.
     */
    public InvalidMoveException(Move move) {
        super("invalid move: " + move.toString());
        this._move = move;
    }

    /**
     * Gets the move that caused the exception.
     * @return
     */
    public Move getMove() {
        return this._move;
    }

}
