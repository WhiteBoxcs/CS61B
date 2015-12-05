package loa.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a logger class for the game.
 * @author william
 */
public class Logger {
    /**
     * THe name of the logger.
     */
    private String _logName;

    /**
     * THe log.
     */
    private String _log;

    /**
     * The listeners.
     */
    private TreeMap<Integer, ArrayList<LogListener>> _listeners;

    /**
     * Constructs the logger with a new name.
     * @param name
     *            The name of the logger.
     */
    public Logger(String name) {
        this.setName(name);
        this._listeners = new TreeMap<Integer, ArrayList<LogListener>>();
        this._log = "";
    }

    /**
     * Logs a message to all log listeners of level lower or equal. Performs a
     * range query.
     * @param level
     *            The level (which log listeners will receive the message).
     * @param message
     *            The message to send.
     */
    public void log(String message, int level) {
        Map.Entry<Integer, ArrayList<LogListener>> entry;
        level += 1;
        while ((entry = this._listeners.lowerEntry(level)) != null) {
            level = entry.getKey();
            entry.getValue().forEach(x -> x.receive(this, message));
        }
        this._log += message + "\n";
    }

    /**
     * Attaches a listener to THIS logger.
     * @param listener
     *            the LogListener to attach.
     * @param level
     *            the level below and at which the LISTENER will receive
     *            messages.
     */
    public void attach(LogListener listener, int level) {
        ArrayList<LogListener> leveledList = this._listeners.get(level);

        if (leveledList == null) {
            leveledList = new ArrayList<LogListener>();
            this._listeners.put(level, leveledList);
        }

        leveledList.add(listener);
    }

    /**
     * Detaches LISTENER from THIS Logger.
     * @param listener
     *            the LogListener to detach.
     */
    public void detach(LogListener listener) {
        for (Map.Entry<Integer, ArrayList<LogListener>> entry : this._listeners
                .entrySet()) {
            entry.getValue().remove(listener);
        }

    }

    /**
     * Returns the whole log delimited by \n per entry.
     * @return the string of the log.
     */
    public String getLog() {
        return this._log;
    }

    /**
     * Gets the name of the logger.
     * @return The name of the logger.
     */
    public String getName() {
        return this._logName;
    }

    /**
     * Sets the name of the logger.
     * @param name
     *            The new name.
     */
    public void setName(String name) {
        this._logName = name;
    }

}
