/**
 * 
 */
package loa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import loa.exceptions.GameException;
import loa.exceptions.InvalidMoveException;
import loa.exceptions.UnknownPlayerException;
import loa.exceptions.GameNotStartedException;
import loa.exceptions.GameVictoryException;

/**
 * @author William Hebgen Guss
 * Represents an actual game of league of legends.
 */
public class Game {
    private Board _board;
    private boolean _playing;
    private ArrayList<Player> _players;
    int playerIndex = 0;
    
    public static final String VERSION = "1.0";
    public static final Random RANDOM = new Random();

    /**
     * Initializes with a new board.
     */
    public Game(){
        this._board =new Board();
        this._playing = false;
        this._players = new ArrayList<Player>();
    }
    
    
    /**
     * Plays a move in the game. 
     * @param input
     * @throws GameException Throws an exception iff
     * either the move is invalid or the game is not started.
     * @returns Whether or not a move is expected.
     */
    public boolean play(Move input) throws GameException
    {        
        if(!playing()){
            if(input == null )
                return true;
            else
                throw new GameNotStartedException();
        } else if(currentPlayer()!= null) {
            checkVictory();
            
            if(input == null && inputExpected())
                return true;
            else if(input != null ){
                if(input.isInvalid())
                    throw new InvalidMoveException(input);
                else if(!inputExpected())
                    throw new InvalidMoveException("not expecting move.", input);
            }
            
            _board.performMove(currentPlayer().turn(input));
            
            checkVictory();
            
            playerIndex = (playerIndex + 1)%_players.size();
            
            return inputExpected();
        }
        
        return true;
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
        _board.clear();
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
        
        return !playing() || (currentPlayer() != null
                    && currentPlayer().moveExpected());
    }


    public Player currentPlayer() {
        if(!_players.isEmpty())
            return _players.get(playerIndex);
        return null;
    }


    public Board getBoard() {
        // TODO Auto-generated method stub
        return this._board;
    }

    /**
     * Sets a given player to a specified plah styel.
     * @param player The player to set.
     * @param auto The play style.
     */
    public void setPlayer(String player, boolean auto) throws UnknownPlayerException
    {
        
    }


    /**
     * Sets a piece within the game.
     * @param row The row to set.
     * @param col The collumn to set.
     * @param piece The piece type to set.
     */
    public void setPiece(int row, int col, Piece piece) {
        //TODO:
    }
    
    /**
     * Checks the game for victory.
     * @throws GameVictoryException Throws a game victory exception if victory has been reached.
     */
    private void checkVictory() throws GameVictoryException{
        double contScore = 0;
        if(currentPlayer().score() == 1 || 
                (contScore = _board.contiguityScore(currentPlayer().team())) == 1){
            currentPlayer().updateScore(contScore);
            _playing = false;
            throw new GameVictoryException(currentPlayer());
        }
    }
}
