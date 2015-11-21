/**
 *
 */
package loa.exceptions;

/**
 * @author William Hebgen Guss
 */
public class GameException extends Exception {
    private boolean error = false;

    /**
     * @param message
     */
    public GameException(String message) {
        super(message);
        this.setError(true);

    }

    public boolean isError() {
        return this.error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
