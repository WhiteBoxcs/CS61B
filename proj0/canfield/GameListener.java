/**
 * @author MadcowD
 */
package canfield;

/**
 * @author MadcowD A game event listener.
 */
interface GameListener {
    /**
     * Called whe nthe game is changed.
     * @param changedGame
     *            The game which was changed.
     */
    void onGameChange(Game changedGame);
}
