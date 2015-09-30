/**
 * c * The Action interface.
 */
package canfield.actions;

/**
 * The action abstract class for bijective functions on gamestates.
 * @author MadcowD
 */
public abstract class Action {
    /**
     * If the action was applied.
     */
    private boolean applied = false;

    /**
     * Applies the action and marks the action applied.
     */
    public final void apply() {
        if (this.applied) {
            throw new IllegalArgumentException();
        }
        this.act();
        this.applied = true;
    }

    /**
     * Applies the inverse action IF AND ONLY IF the action has been previously
     * applied.
     */
    public final void inverseApply() {
        if (!this.applied) {
            throw new IllegalArgumentException();
        }
        this.undo();
        this.applied = false;
    }

    /**
     * Acts in the forward direction (with respect to time).
     */
    protected abstract void act();

    /**
     * The direct inverse of Action.act (with respect to time).
     */
    protected abstract void undo();
}
