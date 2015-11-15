package db61b;

import java.util.Arrays;
import java.util.List;

/**
 * A single row of a database.
 * @author William Hebgen Guss Guss.
 */
class Row {
    /**
     * A Row whose column values are DATA. The array DATA must not be altered
     * subsequently.
     */
    Row(String[] data) {
        this._data = data;
    }

    /**
     * Return a Row formed from the current values of COLUMNS (in order).
     * COLUMNS must all have been resolved to non-empty TableIterators.
     */
    static Row make(List<Column> columns) {
        return new Row(columns);
    }

    /**
     * A Row whose column values are extracted by COLUMNS from ROWS (see
     * {@link db61b.Column#Column}).
     */
    Row(List<Column> columns) {
        this(Arrays.copyOf(columns.stream().map(x -> x.value()).toArray(),
                columns.size(), String[].class));
    }

    /** Return my number of columns. */
    int size() {
        return this._data.length;
    }

    /** Return the value of my Kth column. Requires that 0 <= K < size(). */
    String get(int k) {
        return this._data[k];
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return Arrays.equals(this._data, ((Row) obj)._data);
        } catch (ClassCastException e) {
            return false;
        }
    }

    /*
     * NOTE: Whenever you override the .equals() method for a class, you should
     * also override hashCode so as to insure that if two objects are supposed
     * to be equal, they also return the same .hashCode() value (the converse
     * need not be true: unequal objects MAY also return the same .hashCode()).
     * The hash code is used by certain Java library classes to expedite
     * searches (see Chapter 7 of Data Structures (Into Java)).
     */

    @Override
    public int hashCode() {
        return Arrays.hashCode(this._data);
    }

    @Override
    public String toString() {
        String dbF = "";
        for(int i = 0; i < _data.length; i++){
            dbF += _data[i];
            if(i != _data.length-1)
                dbF += " ";
        }
        
        return dbF;
    }

    /**
     * Converts the Row to DBFormat.
     * @return The DBFormat string of the Row.
     */
    public String toDBFormat() {
        String dbF = "";
        for(int i = 0; i < _data.length; i++){
            dbF += _data[i];
            if(i != _data.length-1)
                dbF += ",";
        }
        
        return dbF;
        
    }

    /** Contents of this row. */
    private String[] _data;
}
