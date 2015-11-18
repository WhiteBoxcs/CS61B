/**
 * 
 */
package loa.views;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loa.Game;
import loa.Game.LogLevel;
import loa.Main;
import loa.Move;
import loa.Piece;
import loa.exceptions.GameException;
import loa.exceptions.InvalidMoveException;
import loa.exceptions.UnknownPlayerException;
import loa.players.AcyclicMachinePlayer;
import loa.players.DensityMachinePlayer;
import loa.players.HumanPlayer;
import loa.players.MachinePlayer;
import loa.util.LogListener;
import loa.util.Logger;

/**
 * @author William Hebgen Guss
 *
 */
public class LoaTextUI extends GameUI implements LogListener {
    private static final LogLevel LOG_LEVEL = LogLevel.MOVES_AI;
    private BufferedReader _input;

    /**
     * Builds a LoaTextUI and attaches it to the game at a MOVES_AI level.
     * @param game
     */
    public LoaTextUI(Game game) {
        super(game);
        game.attach(this, LOG_LEVEL.getLevel());
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
                if(game().inputExpected()){
                    game().play(input());
                }
                else{
                    game().play();
                }
                
            } catch (GameException e) {
                if(e.isError())
                    error(e.getMessage());
                else
                    System.out.println(e.getMessage());
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
    private Move input() throws GameException{
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
        else{
            return Move.create(line, game().getBoard());
        }
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
            System.out.print("Error: ");
            System.out.printf(format, args);
            System.out.print("\n");
    }
  
    /** Describes a command with up to two arguments. */
    private static final Pattern COMMAND_PATN =
        Pattern.compile("(#|\\S+)\\s*(\\S*)\\s*(\\S*).*");

    /** If LINE is a recognized command other than a move, process it
     *  and return true.  Otherwise, return false. */
    private boolean processCommand(String line) throws GameException{
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
            case "quit":
                close();
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
        System.out.println("Next move: " + game().currentPlayer().team().fullName());
        System.out.println("===");
    }

    /**
     * Sets a given square.
     * @param pos The position to set.
     * @param setValue The set value.
     */
    private void setCommand(String pos, String setValue) {
        try{
            if(pos == null || !pos.matches("[a-h][1-9]") || setValue == "")
                throw new IllegalArgumentException();
            
            

            int col = game().getBoard().toColPos(pos);
            int row = game().getBoard().toRowPos(pos);
            
            Piece piece = Piece.setValueOf(setValue);
            
            game().setPiece(piece, row, col);
            
            
        }catch(IllegalArgumentException exp){
            error("invalid arguments to set: " + pos +", " + setValue);
        }
    }

    /**
     * Starts the game.
     */
    private void startCommand() {
        game().start();
    }

    /**
     * Clears the game.
     */
    private void clearCommand() {
        game().clear();
    }

    /**
     * Sets the general seed for all games.
     * @param seed
     */
    private void seedCommand(String seed) {
        try {
            Game.RANDOM.setSeed(Long.parseLong(seed));
        } catch (NumberFormatException excp) {
            error("Invalid number: %s", seed);
        }
    }

    /**
     * Sets PLAYER to be a machine.
     * @param player The player to set.
     */
    private void autoCommand(String player) throws UnknownPlayerException {
        Piece team = Piece.playerValueOf(player);
        
        game().setPlayer(team, 
                new DensityMachinePlayer(team,0, game()));
    }

    /**
     * Sets PLAYER to be a human player taking input.
     * @param player the player to change.
     */
    private void manualCommand(String player) throws UnknownPlayerException {
        Piece team = Piece.playerValueOf(player);
        game().setPlayer(team, new HumanPlayer(team,0));
    }

    /**
     * Prints a message from the log.
     */
    @Override
    public void receive(Logger logger, String message) {
        System.out.println(message);
    }

}
