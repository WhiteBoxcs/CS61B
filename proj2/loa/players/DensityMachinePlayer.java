/**
 *
 */
package loa.players;

import loa.Board;
import loa.Game;
import loa.Piece;

/**
 * @author william
 */
public class DensityMachinePlayer extends AcyclicMachinePlayer {

    /**
     * Builds a density machine player.
     * @param team
     *            Tghe team/
     * @param initScore
     *            the initial scor.we
     * @param container
     *            t he copnmtainer.
     */
    public DensityMachinePlayer(Piece team, double initScore, Game container) {
        super(team, initScore, container);
    }

    @Override
    protected double calculateRegret(Board pb) {
        double denseScore = 0;
        return super.calculateRegret(pb) + denseScore;
    }

}
