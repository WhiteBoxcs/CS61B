/**
 * 
 */
package loa.players;

import loa.Move;
import loa.Piece;

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
    public abstract boolean verbose();
    
    /**
     * Makes a move given an optional input parameter.
     * @param input
     * @return
     */
    public abstract Move turn(Move input);

    /**
     * @return The team on which the player resides.
     */
    public Piece team() {
        // TODO Auto-generated method stub
        return Piece.BP;
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
