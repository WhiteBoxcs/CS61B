package loa.players;

import java.util.ArrayList;
import java.util.TreeMap;

import loa.Board;
import loa.Game;
import loa.Game.LogLevel;
import loa.Move;
import loa.Piece;
import loa.exceptions.InvalidMoveException;

/**
 * Creates a new machine player.
 * @author William
 */
public class MachinePlayer extends Player {
    /**
     * The search depth.
     */
    protected static final int SEARCH_DEPTH = 2;
    /**
     * The game.
     */
    protected Game _game;

    /**
     * Creates a new machine player.
     * @param team The team.
     * @param initScore The init score.
     * @param container The container.
     */
    public MachinePlayer(Piece team, double initScore, Game container) {
        super(team, initScore);
        this._game = container;
    }

    @Override
    public boolean inputExpected() {
        return false;
    }

    @Override
    public LogLevel verbose() {
        return Game.LogLevel.MOVES_AI;
    }

    /**
     * Performs the best possible move based on the minimax algorithm.
     * @param input
     *            The input given to the player (should be null).
     */
    @Override
    public Move act(Move input) {
        TreeMap<Double, Move> suggested =
                this.suggestedMoves(this._game.getBoard());
        return suggested.lastEntry().getValue();
    }

    /**
     * Calculates suggested moves based on a board.
     * @param b
     *            The board on which the suggested moves are calculated.
     * @return Hold in there.
     */
    protected TreeMap<Double, Move> suggestedMoves(Board b) {
        TreeMap<Double, Move> actions = new TreeMap<>();

        for (int row = 1; b.inBounds(1, row); row++) {
            for (int col = 1; b.inBounds(col, row); col++) {
                if (b.get(row, col) == this.team()) {
                    for (Move m : b.possibleMoves(row, col)) {
                        actions.put(this.deepMinimax(m, b, SEARCH_DEPTH), m);
                    }
                }
            }
        }

        return actions;
    }

    /**
     * Performs a minimax algorithm yielding a score for persuing an action.
     * @param move
     *            The action to persure.
     * @param b
     *            The board on which that action is persued.
     * @param depth
     *            The depth to which the minimax is performed (2 is best).
     * @return The score for the minimax algorithm.
     */
    protected double deepMinimax(Move move, Board b, int depth) {
        Board pb = b.clone();
        Piece curPlayer =
                depth % 2 == 0 ? this.team() : this.team().opposite();
        ArrayList<Double> scores = new ArrayList<Double>();

        try {
            pb.performMove(move);

            if (depth <= 0) {
                return this.calculateRegret(b);
            }

            for (int row = 1; pb.inBounds(1, row); row++) {
                for (int col = 1; pb.inBounds(col, row); col++) {
                    if (pb.get(row, col) == curPlayer) {
                        for (Move m : pb.possibleMoves(row, col)) {
                            scores.add(this.deepMinimax(m, pb, depth - 1));
                        }
                    }
                }
            }

            if (curPlayer == this.team()) {
                return scores.stream().max((x, y) -> x.compareTo(y)).get();
            } else {
                return scores.stream().min((x, y) -> x.compareTo(y)).get();
            }
        } catch (InvalidMoveException e) {
            return -Double.MAX_VALUE;
        } catch (java.util.NoSuchElementException e) {
            return -Double.MAX_VALUE;
        }
    }

    /**
     * Calculates the regret on a board.
     * @param pb
     *            The board on which we caluclate regret.
     * @return The regret.
     */
    protected double calculateRegret(Board pb) {
        double contScore = pb.contiguityScore(this.team())
                - pb.contiguityScore(this.team().opposite());
        return contScore;
    }

}
