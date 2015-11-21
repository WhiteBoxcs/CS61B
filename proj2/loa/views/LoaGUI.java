package loa.views;

import loa.Game;

/**
 * Represents an uncompleted LOA GUI view.
 * @author william
 */
public class LoaGUI extends GameUI {

    /**
     * Creates a new Loa GUI View.
     * @param game
     *            The game to view.
     */
    public LoaGUI(Game game) {
        super(game);
    }

    /**
     * Opens the GUI view.
     */
    @Override
    public void open() {
    }

    /**
     * Prints a GUI error.
     */
    @Override
    public void error(String format, Object... args) {
    }

}
