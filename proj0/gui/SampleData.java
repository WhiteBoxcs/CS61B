package gui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
 * SampleData is the model part of the Model-View-Controller pattern. It
 * represents data that is to be displayed and manipulated. It is operated on
 * by the controller (SampleGUI), and displayed by the view (DataDisplay).
 */

/**
 * A set of points and line segments joining some of them in 2-dimensional
 * space.
 * @author P. N. Hilfinger
 */
class SampleData {

    /** A new, empty object. */
    SampleData() {
    }

    /** Clear all points and lines. */
    void clear() {
        this._points.clear();
        this._lines.clear();
    }

    /** Add a new point at coordinates (X, Y). */
    void addPoint(int x, int y) {
        this._points.add(new Point(x, y));
    }

    /**
     * Add a new line from P0 to P1, which each must be == (pointer equality,
     * not equality of coordinates) to one of my points. Has no effect if the
     * segment (P0, P1) or (P1, P0) already exists.
     */
    void addLine(Point p0, Point p1) {
        if (!this._points.contains(p0) || !this._points.contains(p1)) {
            throw new IllegalArgumentException("point is not mine");
        }
        this._lines.add(new Segment(p0, p1));
    }

    /** Return current number of points. */
    int numPoints() {
        return this._points.size();
    }

    /** Return list of all my Points. */
    Set<Point> getPoints() {
        return Collections.unmodifiableSet(this._points);
    }

    /** Return current number of lines. */
    int numLines() {
        return this._lines.size();
    }

    /** Return list of all my segments. */
    Set<Segment> getSegments() {
        return Collections.unmodifiableSet(this._lines);
    }

    /**
     * Return any point that is within DELTA units of distance from (X, Y), or
     * null if there are none.
     * @param x the x of other.
     * @param y the y of other.
     * @param delta the tolerance.
     * @return the return.
     */
    Point findPoint(int x, int y, double delta) {
        double d2 = delta * delta;
        for (Point p : this._points) {
            if (p.dist2(x, y) <= d2) {
                return p;
            }
        }
        return null;
    }

    /** List of my points. */
    private final HashSet<Point> _points = new HashSet<>();
    /** List of my line segments. */
    private final HashSet<Segment> _lines = new HashSet<>();

}
