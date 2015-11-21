package loa.players;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import loa.Game;
import loa.Move;
import loa.Piece;

/**
 * @author William
 * An acyclic machine algorith.
 */
public class AcyclicMachinePlayer extends MachinePlayer {
    /**
     * The depth at which to explore for cycles.
     */
    private static final int SEARCH_HISTORY_DEPTH = 10;
    /**
     * The history of previous moves.
     */
    protected LinkedList<Move> history;

    /**
     * Coinstructs anb acyclic machine learning algorithm.
     * @param team The team.
     * @param initScore The inital score.
     * @param container The container.
     */
    public AcyclicMachinePlayer(Piece team, double initScore, Game container) {
        super(team, initScore, container);
        this.history = new LinkedList<>();
    }

    /**
     * Performs the machine player algorithm while avoiding cycles.
     * @param input
     * @return Lol.
     * @throws InvalidMoveException
     */
    @Override
    public Move act(Move input) {
        TreeMap<Double, Move> moves =
                this.suggestedMoves(this._game.getBoard());
        Entry<Double, Move> bestMove = moves.lastEntry();

        Move finalMove;
        if (this.cyclicMove(bestMove.getValue()) && moves.size() > 1) {
            moves.remove(bestMove.getKey());
            finalMove = moves.lastEntry().getValue();
        } else {
            finalMove = moves.lastEntry().getValue();
        }

        this.history.add(finalMove);
        if (this.history.size() > SEARCH_HISTORY_DEPTH) {
            this.history.remove();
        }

        return finalMove;
    }

    /**
     * Performs an O(n) search of the array to find a cycle.
     * @param value The value.
     * @return False if only 3 moves are made since there couldn't possibly be
     *         a cycle. False if there is no cycle.
     */
    protected boolean cyclicMove(Move value) {
        ArrayList<Move> quickHist = new ArrayList<Move>(this.history);

        int endCycle = quickHist.size() - 2;
        while (endCycle >= 0 && !quickHist.get(endCycle).equals(value)) {
            endCycle--;
        }

        if (endCycle < quickHist.size() / 2) {
            return false;
        } else {
            for (int i = 1; i < quickHist.size() - endCycle
                    && endCycle - i >= 0; i++) {
                if (!quickHist.get(endCycle - i)
                        .equals(quickHist.get(quickHist.size() - i))) {
                    return false;
                }
            }

            return true;
        }

    }

}
