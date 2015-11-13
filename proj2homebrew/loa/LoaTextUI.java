/**
 * 
 */
package loa;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author william
 *
 */
public class LoaTextUI extends GameUI {

    protected LoaTextUI(Game game) {
        super(game);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see loa.GameUI#play()
     * Generally, this method will parse normal commands until 
     * a game is started.
     */
    @Override
    public void open() {
    }
    
    

    /** Describes a command with up to two arguments. */
    private static final Pattern COMMAND_PATN =
        Pattern.compile("(#|\\S+)\\s*(\\S*)\\s*(\\S*).*");

    /** If LINE is a recognized command other than a move, process it
     *  and return true.  Otherwise, return false. */
    private boolean processCommand(String line) {
        if (line.length() == 0) {
            return true;
        }
        Matcher command = COMMAND_PATN.matcher(line);
        if (command.matches()) {
            switch (command.group(1).toLowerCase()) {
            case "#":
                return true;
            case "manual":
                manualCommand(command.group(2).toLowerCase());
                return true;
            case "auto":
                autoCommand(command.group(2).toLowerCase());
                return true;
            case "seed":
                seedCommand(command.group(2));
                return true;
            case "clear":
                clearCommand();
                return true;
            case "start":
                startCommand();
                return true;
            case "set":
                setCommand(command.group(2), command.group(3));
                return true;
                
            case "dump":
                dumpCommand();
                return true;
            case "?":
            case "help":
                help();
                return true;
            default:
                return false;
            }
        }
        return false;
    }

    private void help() {
        // TODO Auto-generated method stub
        
    }

    private void dumpCommand() {
        // TODO Auto-generated method stub
        
    }

    private void setCommand(String group, String group2) {
        // TODO Auto-generated method stub
        
    }

    private void startCommand() {
        // TODO Auto-generated method stub
        
    }

    private void clearCommand() {
        // TODO Auto-generated method stub
        
    }

    private void seedCommand(String group) {
        // TODO Auto-generated method stub
        
    }

    private void autoCommand(String lowerCase) {
        // TODO Auto-generated method stub
        
    }

    private void manualCommand(String lowerCase) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void error(String format, Object... args) {
            System.err.print("Error: ");
            System.err.printf(format, args);
    }
}
