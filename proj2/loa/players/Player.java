/**
 * 
 */
package loa.players;

import loa.Game;
import loa.Move;
import loa.Piece;
import loa.exceptions.InvalidMoveException;

/**
 * @author William Hebgen Guss
 * Represents an abstract player class.
 */
public abstract class Player {
    private Piece _team;
    private double _score;

    public Player(Piece team, double initScore){
        this._team = team;
        setScore(initScore);
    }
    
    /**
     * If input is required to make a turn.
     * @return IF INPUT IS REQUIRED.
     */
    public abstract boolean inputExpected();
    
    /**
     * If the turns actions should be verboseley notified.
     * @return If the move is verbose.
     */
    public abstract Game.LogLevel verbose();
    
    /**
     * Makes a move given an optional input parameter.
     * @param input
     * @return
     * @throws InvalidMoveException 
     */
    public abstract Move act(Move input);
    
    /**
     * Enacts the turn of a player. DO not override.
     * @param input
     * @return
     * @throws InvalidMoveException If the player is moving a type of piece
     * not of the same team.
     */
    public Move turn(Move input) throws InvalidMoveException{
        Move action = act(input);
        
        if(action.movedPiece() != team())
            throw new InvalidMoveException(action);
        
        return action;
    }

    /**
     * @return The team on which the player resides.
     */
    public Piece team() {
        // TODO Auto-generated method stub
        return this._team;
    }

    /**
     * Sets the players contiguity score.
     * @param contScore The contiguity score to set.
     */
    public void setScore(double contScore) {
        this._score =  contScore;
    }

    /**
     * Gets the players contiguity score.
     * @return the player
     */
    public double getScore() {
        // TODO Auto-generated method stub
        return _score;
    }

}
