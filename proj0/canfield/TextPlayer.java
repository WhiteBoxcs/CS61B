package canfield;

import java.util.Scanner;
import java.util.NoSuchElementException;

/** A type of player that gets input from System.stdin, reports
 *  game positions on System.stdout, and reports errors on System.err.
 *  @author P. N. Hilfinger
 */
class TextPlayer extends Player {

    /** Name of help file containing usage information. */
    private static final String HELP_FILE = "help.txt";

    /** A TextPlayer that makes moves on GAME. */
    TextPlayer(Game game) {
        super(game);
        _inp = new Scanner(System.in);
    }

    /** Display informational message MSG, using additional arguments ARGS
     *  as for String.format. */
    private void message(String msg, Object... args) {
        System.err.printf(msg, args);
        System.err.println();
    }

    /** Display error message MSG, using additional arguments ARGS as for
     *  String.format. */
    private void error(String msg, Object... args) {
        message("Error: " + msg, args);
    }

    /** Prompt for and read a command, ignoring comments and blank lines.
     *  Return line after removing leading and trailing whitespace, or
     *  return null if there are no more lines. */
    private String getCommand() {
        while (true) {
            System.err.print("> ");
            System.err.flush();
            if (!_inp.hasNext()) {
                break;
            }
            String line = _inp.nextLine();
            line = line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    /** Announce a win if there is one, ask if user wants another, and if so,
     *  set it up.  Returns true iff user wants to quit. */
    private boolean endGame() {
        if (_game.isWon()) {
            if (!yorn("You won! Another game?")) {
                return true;
            }
        } else {
            if (!yorn("Another game?")) {
                return true;
            }
        }
        _game.deal();
        display();
        return false;
    }

    @Override
    void play() {
        _game.deal();
        while (true) {
            display();
            if (_game.isWon() && endGame()) {
                return;
            }
            String line = getCommand();
            if (line == null) {
                break;
            }
            Scanner inp = new Scanner(line);
            try {
                switch (inp.next().toLowerCase()) {
                case "card": case "c":
                    _game.stockToWaste();
                    break;
                case "resfnd": case "rf":
                    _game.reserveToFoundation();
                    break;
                case "wstfnd": case "wf":
                    _game.wasteToFoundation();
                    break;
                case "tabfnd": case "tf":
                    _game.tableauToFoundation(inp.nextInt());
                    break;
                case "restab": case "rt":
                    _game.reserveToTableau(inp.nextInt());
                    break;
                case "wsttab": case "wt":
                    _game.wasteToTableau(inp.nextInt());
                    break;
                case "tabtab": case "tt":
                    _game.tableauToTableau(inp.nextInt(), inp.nextInt());
                    break;
                case "fndtab": case "ft":
                    _game.foundationToTableau(inp.nextInt(), inp.nextInt());
                    break;
                case "help": case "h": case "?":
                    help();
                    break;
                case "quit": case "q":
                    if (endGame()) {
                        return;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown command");
                }
            } catch (IllegalArgumentException excp) {
                error(String.format(excp.getMessage()));
            } catch (NoSuchElementException excp) {
                error("Invalid or missing argument");
            }
        }
    }

    /** Respond to QUESTION.  Returns true iff answer is yes. */
    boolean yorn(String question) {
        while (true) {
            System.err.print(question + " [yn] ");
            System.err.flush();
            if (!_inp.hasNext()) {
                return false;
            }
            String resp = _inp.next();
            switch (resp.toLowerCase()) {
            case "y": case "yes":
                return true;
            case "n": case "no":
                return false;
            default:
                System.err.println("Please answer yes or no.");
            }
        }
    }

    /** Display the current state of the board. */
    private void display() {
        System.out.printf("%10s", "");
        for (int i = 1; i <= Card.NUM_SUITS; i += 1) {
            System.out.printf(" %3s", str(_game.topFoundation(i)));
        }
        System.out.println();

        System.out.printf("%n %3s      ", str(_game.topReserve()));
        displayTableau(0);
        System.out.println();

        if (_game.stockEmpty()) {
            System.out.print(" --- ");
        } else {
            System.out.print(" ### ");
        }
        System.out.printf("%3s  ", str(_game.topWaste()));

        int tabLen;
        tabLen = 0;
        for (int i = 1; i <= Game.TABLEAU_SIZE; i += 1) {
            tabLen = Math.max(tabLen, _game.tableauSize(i));
        }

        if (tabLen > 1) {
            displayTableau(1);
        }
        System.out.println();

        for (int j = 2; j < tabLen; j += 1) {
            System.out.printf("%10s", "");
            displayTableau(j);
            System.out.println();
        }
    }

    /** Display help message summarizing usage. */
    private void help() {
        Scanner inp = new Scanner(Utils.getFileStream(HELP_FILE));
        while (inp.hasNextLine()) {
            System.err.println(inp.nextLine());
        }
    }

    /** Display row #ROW of the tableau, counting from 0 as the top. */
    private void displayTableau(int row) {
        for (int i = 1; i <= Game.TABLEAU_SIZE; i += 1) {
            Card c = _game.getTableau(i, _game.tableauSize(i) - row - 1);
            System.out.printf(" %3s", str(c));
        }
    }

    /** Return an external representation of C, which may be null. */
    private String str(Card c) {
        return c == null ? "---" : c.toString();
    }

    /** Source of input commands from the user. */
    private Scanner _inp;
}
