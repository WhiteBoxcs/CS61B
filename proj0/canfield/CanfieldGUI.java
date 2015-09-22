package canfield;

import java.awt.event.MouseEvent;

import ucb.gui.LayoutSpec;
import ucb.gui.TopLevel;

/**
 * A top-level GUI for Canfield solitaire.
 *
 * @author
 */
class CanfieldGUI extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        this._game = game;
        this.addLabel("Sorry, no graphical interface yet", new LayoutSpec("y",
                0, "x", 0));
        this.addButton("Quit", "quit", new LayoutSpec("y", 0, "x", 1));

        this._display = new GameDisplay(game);
        this.add(this._display, new LayoutSpec("y", 2, "width", 2));
        this._display.setMouseHandler("click", this, "mouseClicked");
        this._display.setMouseHandler("release", this, "mouseReleased");
        this._display.setMouseHandler("drag", this, "mouseDragged");

        this.display(true);
    }

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        System.exit(1);
    }

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        // FIXME
        this._display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        // FIXME
        this._display.repaint();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        this._display.repaint();
    }

    /** The board widget. */
    private final GameDisplay _display;

    /** The game I am consulting. */
    private final Game _game;

}
