package gui;

import ucb.gui.Pad;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.IOException;

/* DataDisplay is the view part of the Model-View-Controller pattern.
 * It displays information present in the model (SampleData). */

/* A Pad is kind of Widget (something displayable in a TopLevel).  It
 * has a number of methods for intercepting mouse and keyboard events
 * that happen within it. Calling the method
 * <tt>setMouseHandler(T, OBJ, NAME)</tt> on a Pad arranges that
 * whenever a mouse event of type T happens within the Pad, the method
 * named NAME is called on object OBJ.  T is a String that identifies
 * a type of event (mouse pressed, mouse released, mouse clicked,
 * mouse moved, or mouse dragged).
 *
 * By overriding the method paintComponent, the implementor can
 * specify what to draw on the Pad when the system determines it is
 * necessary to do so.  This happens automatically whenever a portion
 * of a window is uncovered or a window is uniconified. It is up to
 * the implementor to signal when something has changed that requires
 * redrawing the picture.  The .repaint() method on Pad gives this
 * signal, and should be called when data used by the
 * .paintComponent() method changes.
 *
 * Repainting does not happen immediately; a separate thread (a kind
 * of mini-program that runs independently) executes .paintComponent on
 * whatever components need it in an orderly, serial fashion.  It
 * creates a special kind of object (a subtype of java.awt.Graphics)
 * that defines numerous methods for drawing on bitmapped displays.
 * The same thread executes the specified action routines for mouse
 * and keyboard events.
 */


/** A widget that displays a set of points and connecting line segments.
 *  @author P. N. Hilfinger
 */
class DataDisplay extends Pad {

    /** Name resource file that denotes points. */
    private static final String POINT_IMAGE_NAME = "bear.gif";

    /** Image to use for points. */
    private static final Image POINT_IMAGE = getImage(POINT_IMAGE_NAME);

    /** Width in which POINT_IMAGE is displayed. */
    private static final int POINT_WIDTH = POINT_IMAGE.getWidth(null) / 4;
    /** Height in which POINT_IMAGE is displayed. */
    private static final int POINT_HEIGHT = POINT_IMAGE.getHeight(null) / 4;

    /** Distance from point accepted as designating the point. */
    static final double MOUSE_TOLERANCE = 0.5 * POINT_WIDTH;

    /** Shape of line stroke. */
    private static final Stroke LINE_STROKE = new BasicStroke(3);
    /** Shape of dashed line stroke. */
    private static final Stroke DASHED_STROKE
        = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND,
                          1.0f, new float[] { 4.0f, 4.0f }, 0.0f);

    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.white;
    /** Color of line segments. */
    private static final Color LINE_COLOR = Color.red;

    /** A graphical representation of DATA on a field of size WIDTH x HEIGHT
     *  pixels. */
    public DataDisplay(SampleData data, int width, int height) {
        _data = data;
        setPreferredSize(width, height);
    }

    /** Sets endpoints of incomplete segment being constructed to be
     *  FIRST and LAST.  FIRST is assumed to be immutable until
     *  removeIncompleteSegment() is called.  The incomplete segment displays
     *  as a dashed line. */
    void setIncompleteSegment(Point first, Point last) {
        _firstPoint.setLocation(first);
        _lastPoint.setLocation(last);
    }

    /** Removes the incomplete segment (sets it to a 0-length segment). */
    void removeIncompleteSegment() {
        _firstPoint.setLocation(0, 0);
        _lastPoint.setLocation(0, 0);
    }

    /** Return an Image read from the resource named NAME. */
    private static Image getImage(String name) {
        InputStream in =
            DataDisplay.class.getResourceAsStream("/gui/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    @Override
    public void paintComponent(Graphics2D g) {
        Rectangle b = g.getClipBounds();
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, b.width, b.height);
        g.setStroke(LINE_STROKE);
        g.setColor(LINE_COLOR);
        for (Segment L : _data.getSegments()) {
            g.drawLine(L.p0().x, L.p0().y, L.p1().x, L.p1().y);
        }
        g.setStroke(DASHED_STROKE);
        if (!_firstPoint.equals(_lastPoint)) {
            g.drawLine(_firstPoint.x,  _firstPoint.y,
                       _lastPoint.x, _lastPoint.y);
        }
        for (Point p : _data.getPoints()) {
            g.drawImage(POINT_IMAGE,
                        p.x - POINT_WIDTH / 2, p.y - POINT_HEIGHT / 2,
                        POINT_WIDTH, POINT_HEIGHT, null);
        }
    }

    /** Data I am displaying. */
    private final SampleData _data;

    /** First point in an incomplete line segment being built, to be displayed
     *  as a dashed line. Same as _lastPoint if no segment is to be
     *  displayed. */
    private final Point _firstPoint = new Point(0, 0);
    /** Second point in an incomplete line segment.  */
    private final Point _lastPoint = new Point(0, 0);
}
