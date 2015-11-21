package loa.util;

/**
 * Represents a log listener.
 * @author william
 */
public interface LogListener {
    /**
     * Called when a logger to which the listener is attached sends a message.
     * @param logger
     *            the logger who sent the message.
     * @param message
     *            the message sent.
     */
    void receive(Logger logger, String message);
}
