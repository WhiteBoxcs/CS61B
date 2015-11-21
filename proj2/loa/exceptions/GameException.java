/**
 *
 */
package loa.exceptions;

/**
 * The gewneral game exception which views handle.
 * @author William Hebgen Guss
 */
public class GameException extends Exception {
    /**
     * Is the exception an error.
     */
    private boolean error = false;

    /**
     * Builds a new game exception.
     * @param message The message to except.
     */
    public GameException(String message) {
        super(message);
        this.setError(true);

    }

    /**
     * Returns if there is an error.
     * @return
     */
    public boolean isError() {
        return this.error;
    }

    /**
     * Sets if it is an error.
     * @param isError If the exception is an error.
     */
    public void setError(boolean isError) {
        this.error = isError;
    }

}
