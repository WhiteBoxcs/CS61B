package gui;

/**
 * A type of Point that uses pointer equality (==) for equality, rather than
 * equality of coordinates.
 * @author P. N. Hilfinger
 * @author William Guss
 */
class Point extends java.awt.Point {

    /** A new Point with coordiates (X, Y). */
    Point(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /** Return square of distance from me to (X, Y). */
    double dist2(int x, int y) {
        return (x - this.getX()) * (x - this.getX())
                + (y - this.getY()) * (y - this.getY());
    }

    /**
     * Gets the supremum norm of a point sitance.
     * @param x the second point.
     * @param y the first point.
     * @return the norm.
     */
    double supremumNorm(int x, int y) {
        return Math.max(Math.abs(x - this.getX()), Math.abs(y - this.getY()));
    }
}
