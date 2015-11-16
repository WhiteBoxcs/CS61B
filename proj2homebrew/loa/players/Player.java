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
     * @throws InvalidMoveException Does yes.
     */
    public Move turn(Move input) throws InvalidMoveException{
        if(input.movedPiece() != team())
            throw new InvalidMoveException(input);
        return input;
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
