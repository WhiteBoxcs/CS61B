/**
 * 
 */
package loa;

import static loa.Main.error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author william
 *
 */
public class LoaTextUI extends GameUI {

    private BufferedReader _input;

    protected LoaTextUI(Game game) {
        super(game);
        _input = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Represents the main loop for the view.
     * The loop essentially plays the game and gets input if
     * the game requires input.
     * Clearly if the game is not being played then expect input.
     */
    @Override
    public void open() {
        System.out.println("Lines of Action.  Version " + Game.VERSION + ".");
        System.out.println("Type ? for help.");
        
        while(true){
            try {
                if(game().inputExpected())
                    game().play(input());
                else
                    game().play();
                
            } catch (InvalidMoveException e) {
                if(!game().playing())
                    error(e.getMessage());

            }
        }
    }

    /**
     * Gets input. As opposed to the skeleton version,
     * we check to see if a command is a command before we see if it is a move.
     * It furthermore could be such that invalid moves are processed and 
     * if an input is nowhere close to a move command lexigraphically, a
     * invalid command error message is produced, but we wish to be as close
     * to staff-loa as possible.
     * @return The move gathered (iff it is a move). Otherwise if another command processes
     * return null.
     */
    private Move input() {
        String line = null;
        
        try {
            prompt();
            line = _input.readLine();
            if (line == null) {
                close();
            }
        } catch (IOException e) {
            error("unexpected I/O error on input");
            close();
        }
        
        if(processCommand(line))
            return null;
        else
            return Move.create(line, game().getBoard());
    }

    /** Print a prompt for a move. */
    private void prompt() {
        
        String indicator = "-";
        if(game().playing())
            indicator = game()
            .currentPlayer().team().abbrev();
        
        System.out.print(indicator + "> ");
        System.out.flush();
    }

    /**
     * Prints an error message to the standard error.
     */
    @Override
    public void error(String format, Object... args) {
            System.err.print("Error: ");
            System.err.printf(format, args);
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

    /**
     * Prints the help file.
     */
    private void help() {
        Main.printResource("loa/help", false);
    }

    /**
     * Prints the board using the formatting described in the specification.
     */
    private void dumpCommand() {
        String[] boardString = game().getBoard().toString().split("\n");
        System.out.println("===");
        for(String line : boardString){
            System.out.println("    " + line);
        }
        System.out.println("===");
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

}
