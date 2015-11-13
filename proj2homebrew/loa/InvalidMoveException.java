/**
 * 
 */
package loa;

/**
 * @author william
 * An invalid move exception    
 */
public class InvalidMoveException extends Exception {
    private Move _move;

    /**
     * Creates an invalid move exception.
     * @param move The move which caused the error.
     */
    public InvalidMoveException(String exception, Move move){
        super("Error: " + exception);
        this._move = move;
    }
    
    /**
     * Causes an invalid move exception.
     * @param move The move which caused the error.
     */
    public InvalidMoveException(Move move){
        super("Error: invalid move: " + move.toString());
        this._move = move;
    }

    /**
     * Gets the move that caused the exception.
     * @return
     */
    public Move getMove() {
        return _move;
    }
    
    
}
