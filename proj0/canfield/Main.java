package canfield;

import ucb.util.CommandArgs;

/** The main class for Canfield solitaire.
 *  @author P. N. Hilfinger
 */
public class Main {

    /** The main program.  ARGS may contain the options --seed=NUM,
     *  (random seed); and --text (use textual commands). */
    public static void main(String... args) {
        String spec = "--seed=(\\d+) --text";
        CommandArgs options = new CommandArgs(spec, args);
        if (!options.ok()) {
            System.err.printf("Usage: java canfield.Main [ --seed=N ] "
                              + "[ --text ]");
            System.exit(1);
        }

        Main main = new Main(options);
        main.run();
    }

    /** Set up and play Canfield. */
    void run() {
        Game game = new Game();
        Player player;

        player = null;
        if (_options.contains("--text")) {
            player = new TextPlayer(game);
        } else {
            player = new GUIPlayer(game);
        }

        if (_options.contains("--seed")) {
            game.seed(_options.getLong("--seed"));
        }

        player.play();
    }

    /** A new Main object using OPTIONS as options (as for main). */
    Main(CommandArgs options) {
        _options = options;
    }

    /** Command-line arguments. */
    private CommandArgs _options;

}
