/**
 * 
 */
package loa;

import java.util.ArrayList;

/**
 * @author william
 * Represents an actual game of league of legends.
 */
public class Game {
    private Board _board;
    private boolean _playing;
    private ArrayList<Player> players;
    
    public static final String VERSION = "1.0";


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
     * either the move is invalid or the game is not started.
     * @returns Whether or not a move is expected.
     */
    public boolean play(Move move) throws InvalidMoveException
    {        
        if(move == null && inputExpected())
            return true;
        else if(move != null){
            if(!_playing )
                throw new InvalidMoveException("game not started.", move);
            if(move.isInvalid())
                throw new InvalidMoveException(move);
            if(!inputExpected())
                throw new InvalidMoveException("not expecting move.", move);
        }
   
        
        
        
        return inputExpected();
    }
    
    /**
     * Plays without giving input.
     * @param move
     * @throws InvalidMoveException Throws an exception iff
     * either the move is invalid or the game is not started.
     * @returns Whether or not a move is expected.
     */
    public boolean play() throws InvalidMoveException
    {
        return this.play(null);
    }


    /**
     * Starts a game.
     * @returns Whether or not a move is expected
     */
    public boolean start(){
        _playing = true;
        return inputExpected();
    }
    
    /**
     * Clears the game board and stops playign the game.
     */
    public void clear(){
        _playing = false;
        _board.initialize();
    }
    
    /**
     * @return whether or not a game is being played.
     */
    public boolean playing(){
        return _playing;
    }
    
    /**
     * @return whether or not a move is expected.
     */
    public boolean inputExpected(){
        
        return !playing() || currentPlayer().moveExpected();
    }


    public Player currentPlayer() {
        // TODO Auto-generated method stub
        return null;
    }


    public Board getBoard() {
        // TODO Auto-generated method stub
        return null;
    }
}
