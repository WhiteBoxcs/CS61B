package gui;

import java.awt.event.MouseEvent;

import ucb.gui.LayoutSpec;
import ucb.gui.TopLevel;

/*
 * SampleGUI is the controller part of the model-view-controller pattern. It
 * sets up the view, and manipulates the model (SampleData) in response to user
 * commands conveyed by the mouse.
 */

/*
 * The ucb.gui package is a highly simplified interface to the Java GUI
 * classes. A TopLevel (ucb.gui.TopLevel) is intended to represent an
 * application window. It contains buttons, labels, menus, and assorted
 * Widgets. The menus are all drop-down menus on the menu bar at the top of the
 * window. Each entry in the menus is identified by a String label that uses a
 * common notation to identify it: "A->B->C", for example, denotes the menu
 * button C in submenu B of main menu tab A. Buttons and labels are identified
 * by arbitrary Strings. Radio buttons (which can also appear in menus) are
 * likewise identified by labels, plus another String, a group name, that
 * groups together buttons that are mutually exclusive. Actions may be
 * associated with buttons by specifying the <i>name</i> of a method (a String)
 * and an object on which the method is called. For example,
 * <tt>addMenuButton("Data->Clear", obj, "clear")</tt> will call the
 * <tt>.clear</tt> method of object <tt>obj</tt> when the menu button
 * Data->Clear is pushed. The call <tt>addMenuButton("Data->Clear",
 * "clear")</tt> is short for <tt>addMenuButton("Data->Clear", this,
 * "clear")</tt>. Several other methods use a similar way to denote actions.
 * [API Design Note: Wnile this technique of specifying actions is convenient
 * in Java 7, Java 8 has introduced <i>method references</i>, which are better
 * a option, and will eventually replace the current interface.] Methods whose
 * names start with "add" place buttons, labels, and Widgets (basically,
 * everything else) into a TopLevel window as specified by a LayoutSpec.
 * Conceptually, the window is divided into an irregular grid (that is, one
 * whose columns and rows may each have different sizes). One may specify where
 * a given item is added on this grid by specifying grid coordinates, widths,
 * alignments, and padding.
 */

/**
 * A sample GUI.
 * @author P. N. Hilfinger
 */
class SampleGUI extends TopLevel {

    /** Size of the area displaying points and lines. */
    static final int WIDTH = 300, HEIGHT = 300;

    /**
     * A new window with given TITLE and displaying DATA.
     * @param title
     *            the tile of the GUI.
     * @param data
     *            The data of the GUI.
     */
    SampleGUI(String title, SampleData data) {
        super(title, true);
        this._data = data;
        this._display = new DataDisplay(data, WIDTH, HEIGHT);
        this.addMenuButton("Data->Clear", "clear");
        this.addMenuButton("Data->Quit", "quit");
        this.add(this._display, new LayoutSpec("y", 0, "width", 2));
        this.addLabel("Points: 0", "pointCount",
                new LayoutSpec("y", 1, "x", 0));
        this.addLabel("Lines: 0", "lineCount", new LayoutSpec("y", 1, "x", 1));
        this._display.setMouseHandler("click", this, "mouseClicked");
        this._display.setMouseHandler("move", this, "mouseMoved");
        this._display.setMouseHandler("drag", this, "mouseDragged");
        this._display.setMouseHandler("release", this, "mouseReleased");
        this.display(true);
    }

    /**
     * Response to "Quit" menu item..
     * @param dummy
     *            The dummy string.
     */
    public void quit(String dummy) {
        if (this.showOptions("Really quit?", "Quit?", "question", "Yes", "Yes",
                "No") == 0) {
            System.exit(1);
        }
    }

    /**
     * Response to "Clear" menu item.
     * @param dummy
     *            The dummy string.
     */
    public void clear(String dummy) {
        this._data.clear();
        this.updateCounts();
        this._display.repaint();
    }

    /**
     * Action in response to mouse-clicking event EVENT.
     * @param event
     *            the mouse event.
     */
    public void mouseClicked(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        Point existing = this._data.findPoint(x, y,
                DataDisplay.MOUSE_TOLERANCE);
        if (existing == null) {
            if (this._clickedPoint == null) {
                this._data.addPoint(x, y);
            }
            this._clickedPoint = null;
            this._display.removeIncompleteSegment();
        } else if (this._clickedPoint == null) {
            this._clickedPoint = existing;
            this._display.setIncompleteSegment(this._clickedPoint,
                    this._clickedPoint);
        } else if (this._clickedPoint != existing) {
            this._data.addLine(this._clickedPoint, existing);
            this._clickedPoint = null;
            this._display.removeIncompleteSegment();
        } else {
            this._clickedPoint = null;
            this._display.removeIncompleteSegment();
        }
        this.updateCounts();
        this._display.repaint();
    }

    /**
     * Action in response to mouse-dragging event EVENT.
     * @param event
     *            The mouse event.
     */
    public void mouseDragged(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        if (this._draggedPoint == null) {
            this._draggedPoint = this._data.findPoint(x, y,
                    DataDisplay.MOUSE_TOLERANCE);
        } else {
            this._draggedPoint.move(event.getX(), event.getY());
        }

        this._clickedPoint = null;
        this._display.removeIncompleteSegment();
        this._display.repaint();
    }

    /**
     * Action in response to release of mouse button.
     * @param ignored
     *            the ignored args.
     */
    public void mouseReleased(MouseEvent ignored) {
        this._draggedPoint = null;
    }

    /**
     * Action in response to mouse-moving event EVENT.
     * @param event
     *            The mouse event.
     */
    public void mouseMoved(MouseEvent event) {
        if (this._clickedPoint != null) {
            this._display.setIncompleteSegment(this._clickedPoint,
                    new Point(event.getX(), event.getY()));
        }
        this._display.repaint();
    }

    /** Display number of points and lines. */
    void updateCounts() {
        this.setLabel("pointCount",
                String.format("Points: %d", this._data.numPoints()));
        this.setLabel("lineCount",
                String.format("Lines: %d", this._data.numLines()));
    }

    /** Point being dragged. */
    private Point _draggedPoint;

    /** Last point clicked on (indicating start of new segment). */
    private Point _clickedPoint;

    /** The board widget. */
    private final DataDisplay _display;

    /** The data I am consulting. */
    private final SampleData _data;

}
