/**
c * The Action interface.
 */
package canfield;

/**
 * The action interface for bijective functions on gamestates.
 *
 * @author MadcowD
 *
 */
public interface Action {
    /**
     * Acts in the forward direction (with respect to time).
     */
    void act();

    /**
     * The direct inverse of Action.act (with respect to time).
     */
    void undo();
}
