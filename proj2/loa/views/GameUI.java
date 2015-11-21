package loa.views;

import loa.Game;

/**
 * A class representing the abstract game user interface.
 * @author William Hebgen Guss
 */
public abstract class GameUI {
    /**
     * The game.
     */
    private Game _game;

    /**
     * Constructs a game user interface with respect to a game.
     * @param game
     *            The game.
     */
    protected GameUI(Game game) {
        this._game = game;
    }

    /**
     * Plays a game using the interface. This command essentially runs the user
     * interface and a sub game may some how be started internally. This as
     * opposed to having every move be parsed as a move, makes more sense from
     * a design perspective.
     */
    public abstract void open();

    /**
     * Propagates an error message to the user interface.
     * @param format
     *            The format of the error.
     * @param args
     *            The arguments to format within the error.
     */
    public abstract void error(String format, Object... args);

    /**
     * Quits the user interface.
     */
    public void close() {
        System.exit(0);
    }

    /**
     * Gets the game from the view.
     * @return
     */
    public Game game() {
        return this._game;
    }
}
