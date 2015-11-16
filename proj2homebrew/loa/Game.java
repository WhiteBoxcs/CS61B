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
import loa.players.HumanPlayer;
import loa.players.Player;
import loa.util.Logger;
import loa.exceptions.GameNotStartedException;
import loa.exceptions.GameVictoryException;

/**
 * @author William Hebgen Guss
 * Represents an actual game of league of legends.
 */
public class Game extends Logger {
    private Board _board;
    private boolean _playing;
    private ArrayList<Player> _players;
    int playerIndex = 0;
    
    public static final String VERSION = "1.0";
    public static final Random RANDOM = new Random();

    /**
     * Initializes with a new board.
     */
    public Game(String name){
        super(name);
        
        this._board = new Board(this);
        this._playing = false;
        this._players = new ArrayList<Player>();
        
        this._players.add(new HumanPlayer(Piece.BP,0));
        this._players.add(new HumanPlayer(Piece.WP, 0));
    }
    
    /**
     * Plays a move in the game. 
     * @param input
     * @throws GameException Throws an exception iff
     * either the move is invalid or the game is not started.
     * @returns Whether or not a move is expected.
     */
    public void play(Move input) throws GameException
    {        
        if(!playing()){
            if(input == null )
                return;
            else
                throw new GameNotStartedException();
        } 
        else if(currentPlayer()!= null) {
            checkVictory();
            
            if(input == null && inputExpected())
                return;
            else if(input != null ){
                if(input.isInvalid())
                    throw new InvalidMoveException(input);
                else if(!inputExpected())
                    throw new InvalidMoveException("not expecting move.", input);
            }
            
            this.logMove(_board.performMove(currentPlayer().turn(input)));
            
            checkVictory();
            
            playerIndex = (playerIndex + 1) %_players.size();

        }
    }
   

    /**
     * Plays without giving input.
     * @param move
     * @throws InvalidMoveException Throws an exception iff
     * either the move is invalid or the game is not started.
     * @returns Whether or not a move is expected.
     */
    public void play() throws GameException
    {
        this.play(null);
    }


    /**
     * Starts a game.
     * @returns Whether or not a move is expected
     */
    public boolean start(){
        setPlaying(true);
        return inputExpected();
    }
    
    /**
     * Clears the game board and stops playign the game.
     */
    public void clear(){
        setPlaying(false);
        _board.clear();
        this.log("Board cleared.", LogLevel.GAME_STATE);
    }
    
    /**
     * @param playing the _playing to set.
     */
    protected void setPlaying(boolean playing) {
        if(_playing != playing){
            this._playing = playing;
            if(_playing)
                this.log("Game started.", LogLevel.GAME_STATE);
            else
                this.log("Game stopped.", LogLevel.GAME_STATE);
        }
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
                    && currentPlayer().inputExpected());
    }

    public Player currentPlayer() {
        if(!_players.isEmpty())
            return _players.get(playerIndex);
        return null;
    }

    public Board getBoard() {
        return this._board;
    }

    /**
     * Sets a given player to a specified plah styel.
     * @param player The player to set.
     * @param auto The play style.
     */
    public void setPlayer(String player, boolean auto) throws UnknownPlayerException
    {
        //TODO:
    }

    /**
     * Sets a piece within the game.
     * @param row The row to set.
     * @param col The collumn to set.
     * @param piece The piece type to set.
     */
    public void setPiece( Piece piece, int row, int col) {
        this._board.set(row, col, piece);
    }
    
    /**
     * Checks the game for victory.
     * @throws GameVictoryException Throws a game victory exception if victory has been reached.
     */
    private void checkVictory() throws GameVictoryException{
        double contScore = 0;
        if(currentPlayer().getScore() == 1 || 
                (contScore = _board.contiguityScore(currentPlayer().team())) == 1){
            currentPlayer().setScore(contScore);
            
            this.log(currentPlayer().team().fullName() + " won!", LogLevel.GAME_STATE);
            this.setPlaying(false);
            throw new GameVictoryException(currentPlayer());
        }
        
        currentPlayer().setScore(contScore);
    }

    
    /**
     * Logs a move.
     * @param finalMove The move to log.
     */
    private void logMove(Move finalMove) {
        this.log(currentPlayer()
                    .team()
                    .abbrev()
                    .toUpperCase()
                    + "::"
                    + finalMove.toString(), 
                    currentPlayer().verbose());
    }

    /**
     * Logs a message with a log level.
     * @param message Them essage to log.
     * @param level the log level.
     */
    private void log(String message, LogLevel level){
        this.log(message, level.getLevel());
    }
    /**
     * Contains the log levels for the game
     * @author William
     */
    public enum LogLevel{
        DEBUG(0),
        GAME_STATE(1),
        MOVES(2), 
        MOVES_AI(3);

        private final int _level;

        /**
         * Creates an enum type.
         * @param level The level of the type.
         */
        private LogLevel(int level)
        {
            this._level = level;
        }

        /**
         * Gets the level of the LOgLEVEL/
         * @return the level.
         */
        public int getLevel()
        {
            return _level;
        }
    }
}
