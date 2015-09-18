package gui;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/* SampleGUI is the controller part of the model-view-controller
 * pattern.  It sets up the view, and manipulates the model
 * (SampleData) in response to user commands conveyed by the mouse. */

/* The ucb.gui package is a highly simplified interface to the Java
 * GUI classes.  A TopLevel (ucb.gui.TopLevel) is intended to
 * represent an application window.  It contains buttons, labels,
 * menus, and assorted Widgets.
 *
 * The menus are all drop-down menus on the menu bar at the top of the
 * window.  Each entry in the menus is identified by a String label that
 * uses a common notation to identify it:  "A->B->C", for example,
 * denotes the menu button C in submenu B of main menu tab A.
 *
 * Buttons and labels are identified by arbitrary Strings.  Radio
 * buttons (which can also appear in menus) are likewise identified by
 * labels, plus another String, a group name, that groups together
 * buttons that are mutually exclusive.
 *
 * Actions may be associated with buttons by specifying the <i>name</i> of
 * a method (a String) and an object on which the method is called.
 * For example, <tt>addMenuButton("Data->Clear", obj, "clear")</tt>
 * will call the <tt>.clear</tt> method of object <tt>obj</tt> when
 * the menu button Data->Clear is pushed.  The call
 * <tt>addMenuButton("Data->Clear", "clear")</tt> is short for
 * <tt>addMenuButton("Data->Clear", this, "clear")</tt>.  Several
 * other methods use a similar way to denote actions.
 *
 * [API Design Note: Wnile this technique of specifying actions is
 * convenient in Java 7, Java 8 has introduced <i>method
 * references</i>, which are better a option, and will eventually
 * replace the current interface.]
 *
 * Methods whose names start with "add" place buttons, labels, and
 * Widgets (basically, everything else) into a TopLevel window as
 * specified by a LayoutSpec.  Conceptually, the window is divided
 * into an irregular grid (that is, one whose columns and rows may
 * each have different sizes).  One may specify where a given item is
 * added on this grid by specifying grid coordinates, widths,
 * alignments, and padding.
 */

/** A sample GUI.
 *  @author P. N. Hilfinger
 */
class SampleGUI extends TopLevel {

    /** Size of the area displaying points and lines. */
    static final int WIDTH = 300, HEIGHT = 300;

    /** A new window with given TITLE and displaying DATA. */
    SampleGUI(String title, SampleData data) {
        super(title, true);
        _data = data;
        _display = new DataDisplay(data, WIDTH, HEIGHT);
        addMenuButton("Data->Clear", "clear");
        addMenuButton("Data->Quit", "quit");
        add(_display, new LayoutSpec("y", 0, "width", 2));
        addLabel("Points: 0", "pointCount",
                 new LayoutSpec("y", 1, "x", 0));
        addLabel("Lines: 0", "lineCount",
                 new LayoutSpec("y", 1, "x", 1));
        _display.setMouseHandler("click", this, "mouseClicked");
        _display.setMouseHandler("move", this, "mouseMoved");
        _display.setMouseHandler("drag", this, "mouseDragged");
        _display.setMouseHandler("release", this, "mouseReleased");
        display(true);
    }

    /** Response to "Quit" menu item.. */
    public void quit(String dummy) {
        if (showOptions("Really quit?", "Quit?", "question",
                        "Yes", "Yes", "No") == 0) {
            System.exit(1);
        }
    }

    /** Response to "Clear" menu item. */
    public void clear(String dummy) {
        _data.clear();
        updateCounts();
        _display.repaint();
    }

    /** Action in response to mouse-clicking event EVENT. */
    public void mouseClicked(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        Point existing =
            _data.findPoint(x, y, DataDisplay.MOUSE_TOLERANCE);
        if (existing == null) {
            if (_clickedPoint == null) {
                _data.addPoint(x, y);
            }
            _clickedPoint = null;
            _display.removeIncompleteSegment();
        } else if (_clickedPoint == null) {
            _clickedPoint = existing;
            _display.setIncompleteSegment(_clickedPoint, _clickedPoint);
        } else if (_clickedPoint != existing) {
            _data.addLine(_clickedPoint, existing);
            _clickedPoint = null;
            _display.removeIncompleteSegment();
        } else {
            _clickedPoint = null;
            _display.removeIncompleteSegment();
        }
        updateCounts();
        _display.repaint();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public void mouseDragged(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        Point dragged;

        if (_draggedPoint == null) {
            _draggedPoint =
                _data.findPoint(x, y, DataDisplay.MOUSE_TOLERANCE);
        } else {
            _draggedPoint.move(event.getX(), event.getY());
        }

        _clickedPoint = null;
        _display.removeIncompleteSegment();
        _display.repaint();
    }

    /** Action in response to release of mouse button. */
    public void mouseReleased(MouseEvent ignored) {
        _draggedPoint = null;
    }

    /** Action in response to mouse-moving event EVENT. */
    public void mouseMoved(MouseEvent event) {
        if (_clickedPoint != null) {
            _display.setIncompleteSegment(_clickedPoint,
                                          new Point(event.getX(),
                                                    event.getY()));
        }
        _display.repaint();
    }

    /** Display number of points and lines. */
    void updateCounts() {
        setLabel("pointCount",
                 String.format("Points: %d", _data.numPoints()));
        setLabel("lineCount",
                 String.format("Lines: %d", _data.numLines()));
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
