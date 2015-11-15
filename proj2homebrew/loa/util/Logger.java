package loa.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Logger {
    private String _name;
    private String _log;
    private TreeMap<Integer, ArrayList<LogListener>> _listeners;

    public Logger(String name){
        this._name = name;
        this._listeners = new TreeMap<Integer, ArrayList<LogListener>>();
    }
    
    /**
     * Logs a message to all log listeners of level lower or equal. Performs a range query.
     * @param level The level (which log listeners will receive the message).
     * @param message The  message to send.
     */
    public void log( String message, int level){
        Map.Entry<Integer, ArrayList<LogListener>> entry;
        while((entry =_listeners.lowerEntry(level)) != null){
            level = entry.getKey();
            entry.getValue().forEach(x -> x.receive(this, message));
        }
    }
    
    /**
     * Attaches a listener to THIS logger.
     * @param listener the LogListener to attach.
     * @param level the level below and at which the LISTENER will receive messages.
     */
    public void attach(LogListener listener, int level){
        ArrayList<LogListener> leveledList = this._listeners.get(level);
        
        if(leveledList == null){
            leveledList = new ArrayList<LogListener>();
            this._listeners.put(level, new ArrayList<LogListener>());
        }
        
        leveledList.add(listener);
    }
    
    /**
     * Detaches LISTENER from THIS Logger.
     * @param listener the LogListener to detach.
     */
    public void detach(LogListener listener){
        for(Map.Entry<Integer, ArrayList<LogListener>> entry : _listeners.entrySet()){
            entry.getValue().remove(listener);
        }
        
    }
    
    
    /**
     * Returns the log.
     * @return The log.
     */
    public String toString(){
        return getLog();
    }
    
    /**
     * Returns the whole log delimited by \n per entry.
     * @return the string of the log.
     */
    public String getLog(){
        return this._log;
    }
    
    
}
