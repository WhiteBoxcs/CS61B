package db61b;

import java.util.Iterator;

/**
 * An iterator through the rows of a Table. Rather than providing the usual
 * Java Iterator interface, a TableIterator acts more like a C++ STL iterator:
 * at any given time, there is a notion of the current Row and a separate
 * method to proceed to the next.
 * @author William Hebgen Guss Guss.
 */
class TableIterator {

    /** An STL-style iterator through the rows of TABLE. */
    TableIterator(Table table) {
        this._table = table;
        this.reset();
    }

    /** Reinitialize me to the first row. */
    void reset() {
        this._rowIterator = this._table.iterator();
        this.next();

    }

    /** Return the Table over which I am iterating. */
    Table table() {
        return this._table;
    }

    /**
     * Return true iff there is a current Row (that is, we have not exhausted
     * the iterator).
     */
    boolean hasRow() {
        return this._row != null;
    }

    /**
     * Proceed to the next row. Assumes that hasRow() is true. Return the new
     * value of hasRow().
     */
    Row next() {
        if (this._rowIterator.hasNext()) {
            this._row = this._rowIterator.next();
        } else {
            this._row = null;
        }
        return this._row;
    }

    /**
     * Return the index of column NAME in my Table, or -1 if there is no such
     * column.
     */
    int columnIndex(String name) {
        return this._table.columnIndex(name);
    }

    /** Return the value of column #K in the current row. */
    String value(int k) {
        if (!this.hasRow()) {
            throw new IllegalStateException("Past the last row.");
        }
        return this._row.get(k);
    }

    /** My table. */
    private final Table _table;
    /** The current Row of my Table, or null if there are no more. */
    private Row _row;

    /** The Row iterator which we wrap. **/
    private Iterator<Row> _rowIterator;

}
