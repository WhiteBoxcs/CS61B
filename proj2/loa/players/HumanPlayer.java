package loa.players;

import loa.Game;
import loa.Game.LogLevel;
import loa.Move;
import loa.Piece;

/**
 * Represents a human plawyer class.
 * @author william
 */
public class HumanPlayer extends Player {

    /**
     * Constructs a human player.
     * @param team
     *            The team of the player.
     * @param initScore
     *            THe initial score of the palyer.
     */
    public HumanPlayer(Piece team, double initScore) {
        super(team, initScore);
    }

    /**
     * Input is expected for human players.
     */
    @Override
    public boolean inputExpected() {
        return true;
    }

    @Override
    public LogLevel verbose() {
        return Game.LogLevel.MOVES;
    }

    @Override
    public Move act(Move input) {
        return input;
    }

}
