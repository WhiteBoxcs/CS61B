package canfield;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A type of player that gets input from System.stdin, reports game positions
 * on System.stdout, and reports errors on System.err.
 * @author P. N. Hilfinger
 */
class TextPlayer extends Player {

    /** Name of help file containing usage information. */
    private static final String HELP_FILE = "help.txt";

    /**
     * A TextPlayer that makes moves on GAME.
     * @param game
     *            The game.
     */
    TextPlayer(Game game) {
        super(game);
        this._inp = new Scanner(System.in);
    }

    /**
     * Display informational message MSG, using additional arguments ARGS as
     * for String.format.
     * @param msg
     *            the message.
     * @param args
     *            the args.
     */
    private void message(String msg, Object... args) {
        System.err.printf(msg, args);
        System.err.println();
    }

    /**
     * Display error message MSG, using additional arguments ARGS as for
     * String.format.
     * @param msg
     *            the message,
     * @param args
     *            the args.
     */
    private void error(String msg, Object... args) {
        this.message("Error: " + msg, args);
    }

    /**
     * Prompt for and read a command, ignoring comments and blank lines. Return
     * line after removing leading and trailing whitespace, or return null if
     * there are no more lines.
     */
    private String getCommand() {
        while (true) {
            System.err.print("> ");
            System.err.flush();
            if (!this._inp.hasNext()) {
                break;
            }
            String line = this._inp.nextLine();
            line = line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    /**
     * Announce a win if there is one, ask if user wants another, and if so,
     * set it up. Returns true iff user wants to quit.
     */
    private boolean endGame() {
        if (this._game.isWon()) {
            if (!this.yorn("You won! Another game?")) {
                return true;
            }
        } else {
            if (!this.yorn("Another game?")) {
                return true;
            }
        }
        this._game.deal();
        this.display();
        return false;
    }

    @Override
    void play() {
        this._game.deal();
        while (true) {
            this.display();
            if (this._game.isWon() && this.endGame()) {
                return;
            }
            String line = this.getCommand();
            if (line == null) {
                break;
            }
            Scanner inp = new Scanner(line);
            try {
                if (process(inp)) {
                    return;
                }
            } catch (IllegalArgumentException excp) {
                this.error(String.format(excp.getMessage()));
            } catch (NoSuchElementException excp) {
                this.error("Invalid or missing argument");
            }
        }

    }

    /**
     * the input processor.
     * @param inp
     *            the inpit.
     * @return if it quits.
     */
    private boolean process(Scanner inp) {
        switch (inp.next().toLowerCase()) {
        case "card":
        case "c":
            this._game.stockToWaste();
            break;
        case "resfnd":
        case "rf":
            this._game.reserveToFoundation();
            break;
        case "wstfnd":
        case "wf":
            this._game.wasteToFoundation();
            break;
        case "tabfnd":
        case "tf":
            this._game.tableauToFoundation(inp.nextInt());
            break;
        case "restab":
        case "rt":
            this._game.reserveToTableau(inp.nextInt());
            break;
        case "wsttab":
        case "wt":
            this._game.wasteToTableau(inp.nextInt());
            break;
        case "tabtab":
        case "tt":
            this._game.tableauToTableau(inp.nextInt(), inp.nextInt());
            break;
        case "fndtab":
        case "ft":
            this._game.foundationToTableau(inp.nextInt(), inp.nextInt());
            break;
        case "help":
        case "h":
        case "?":
            this.help();
            break;
        case "quit":
        case "q":
            if (this.endGame()) {
                return true;
            }
            break;
        case "undo":
        case "u":
            this._game.undo();

            break;
        default:
            throw new IllegalArgumentException("Unknown command");
        }
        return false;
    }

    /** Respond to QUESTION. Returns true iff answer is yes. */
    boolean yorn(String question) {
        while (true) {
            System.err.print(question + " [yn] ");
            System.err.flush();
            if (!this._inp.hasNext()) {
                return false;
            }
            String resp = this._inp.next();
            switch (resp.toLowerCase()) {
            case "y":
            case "yes":
                return true;
            case "n":
            case "no":
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
            System.out.printf(" %3s", this.str(this._game.topFoundation(i)));
        }
        System.out.println();

        System.out.printf("%n %3s      ", this.str(this._game.topReserve()));
        this.displayTableau(0);
        System.out.println();

        if (this._game.stockEmpty()) {
            System.out.print(" --- ");
        } else {
            System.out.print(" ### ");
        }
        System.out.printf("%3s  ", this.str(this._game.topWaste()));

        int tabLen;
        tabLen = 0;
        for (int i = 1; i <= Game.TABLEAU_SIZE; i += 1) {
            tabLen = Math.max(tabLen, this._game.tableauSize(i));
        }

        if (tabLen > 1) {
            this.displayTableau(1);
        }
        System.out.println();

        for (int j = 2; j < tabLen; j += 1) {
            System.out.printf("%10s", "");
            this.displayTableau(j);
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
            Card c = this._game.getTableau(i,
                    this._game.tableauSize(i) - row - 1);
            System.out.printf(" %3s", this.str(c));
        }
    }

    /**
     * Return an external representation of C, which may be null.
     * @param c
     *            the card to get string for.
     * @return the string of the card.
     */
    private String str(Card c) {
        return c == null ? "---" : c.toString();
    }

    /** Source of input commands from the user. */
    private Scanner _inp;

    /**
     * Returns an IllegalArgumentException with specified message. Arguments
     * MSG and ARGS are as for String.format.
     * @param msg
     *            the message.
     * @param args
     *            the args.
     * @return the error exception.
     */
    static IllegalArgumentException err(String msg, Object... args) {
        return new IllegalArgumentException(String.format(msg, args));
    }
}
