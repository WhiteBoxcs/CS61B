/**
 * 
 */
package loa;

import java.util.ArrayList;
import java.util.Random;

import loa.exceptions.GameException;
import loa.exceptions.InvalidMoveException;
import loa.exceptions.UnknownPlayerException;
import loa.exceptions.GameNotStartedException;

/**
 * @author william
 * Represents an actual game of league of legends.
 */
public class Game {
    private Board _board;
    private boolean _playing;
    private ArrayList<Player> players;
    
    public static final String VERSION = "1.0";
    public static final Random RANDOM = new Random();

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
    public boolean play(Move move) throws GameException
    {        
        if(move == null && inputExpected())
            return true;
        else if(move != null){
            if(move.isInvalid())
                throw new InvalidMoveException(move);
            if(!_playing )
                throw new GameNotStartedException();
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
    public boolean play() throws GameException
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

    /**
     * Sets a given player to a specified plah styel.
     * @param player The player to set.
     * @param auto The play style.
     */
    public void setPlayer(String player, boolean auto) throws UnknownPlayerException
    {
        
    }
}
