/**
 * 
 */
package loa;

/**
 * @author william
 * Represents an actual game of league of legends.
 */
public class Game {
    private Board _board;
    private boolean _playing;


    /**
     * Initializes with a new board.
     */
    public Game(){
        this._board =new Board();
        this._playing = false;
    }
    
    
    /**
     * Plays a move in the game. 
     * @param move
     * @throws InvalidMoveException Throws an exception iff
     * either the move is invalid or c 
     */
    public void play(Move move) throws InvalidMoveException
    {
        if(!_playing)
            throw new InvalidMoveException("game not started.", move);
        
    }
    
    /**
     * Starts a game.
     */
    public void start(){
        _playing = true;
    }
    
    
    public void clear(){
        _playing = false;
        _board.initialize();
    }
}
