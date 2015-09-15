package canfield;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/** A top-level GUI for Canfield solitaire.
 *  @author
 */
class CanfieldGUI extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        _game = game;
        addLabel("Sorry, no graphical interface yet",
                 new LayoutSpec("y", 0, "x", 0));
        addButton("Quit", "quit", new LayoutSpec("y", 0, "x", 1));

        _display = new GameDisplay(game);
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        _display.setMouseHandler("release", this, "mouseReleased");
        _display.setMouseHandler("drag", this, "mouseDragged");

        display(true);
    }

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        System.exit(1);
    }

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        // FIXME
        _display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        // FIXME
        _display.repaint();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        // FIXME
        _display.repaint();  // Not needed if picture does not change.
    }

    /** The board widget. */
    private final GameDisplay _display;

    /** The game I am consulting. */
    private final Game _game;

}
