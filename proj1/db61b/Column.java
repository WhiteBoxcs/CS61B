package db61b;

import static db61b.Utils.error;

import java.util.List;

/**
 * A Column accesses a specific, named column in a TableIterator, which in turn
 * is an object that iterates through the rows of a Table.
 * @author William Hebgen Guss Guss.
 */
public class Column {

    /**
     * A Column named NAME selected from TABLE. The Column is initially
     * unresolved; that is, it is not attached to a particular TableIterator.
     * TABLE may be null, indicating that it is unspecified. Otherwise, NAME
     * must be the name of a column in TABLE.
     */
    Column(Table table, String name) {
        this._columnName = name;
        this._table = table;
        if (this._table != null && this._table.columnIndex(name) == -1) {
            throw error("%s is not a column in %s", name, table.name());
        }
        this._value = null;
    }

    /**
     * A degenerate, anonymous, resolved Column whose get() method always
     * returns VALUE. This is useful for representing literals. VALUE must not
     * be null.
     */
    Column(String value) {
        assert value != null;
        this._columnName = null;
        this._table = null;
        this._value = value;
    }

    /** Return my name. */
    String name() {
        return this._columnName;
    }

    /**
     * Attach me to an appropriate TableIterator out of ITERATORS. If my Table
     * is unspecified, there must be a unique TableIterator with a column
     * having my name. Otherwise, my Table must be the table of one of
     * ITERATORS.
     */
    void resolve(List<TableIterator> iterators) {
        if (this._value != null) {
            return;
        }
        if (this._table == null) {
            this._index = -1;
            for (TableIterator it : iterators) {
                int k = it.columnIndex(this._columnName);
                if (k >= 0) {
                    if (this._rowSource != null) {
                        throw error("%s is ambiguous", this._columnName);
                    }
                    this._index = k;
                    this._rowSource = it;
                }
            }
            if (this._index == -1) {
                throw error("unknown column: %s", this._columnName);
            }
        } else {
            for (TableIterator it : iterators) {
                if (it.table() == this._table) {
                    this._rowSource = it;
                    this._index = it.columnIndex(this._columnName);
                    return;
                }
            }
            throw error("%s is not being selected from", this._table.name());
        }
    }

    /**
     * Return my column value from the current row of my TableIterator. This
     * Column must be resolved.
     */
    String value() {
        if (this._value != null) {
            return this._value;
        }
        assert this._rowSource != null;
        return this._rowSource.value(this._index);
    }

    /**
     * Gets the alias of the Column.
     * @return The alias if there is one, otherwise the name of the Column.
     */
    public String alias() {
        if (this._alias == null) {
            return this._columnName;
        } else {
            return this._alias;
        }

    }

    /**
     * Sets the alias.
     * @param alias The alias to be set.
     */
    public void setAlias(String alias) {
        this._alias = alias;
    }

    /** Column name denoted by THIS. */
    private String _columnName;
    /** Index of the column from which to extract a value. */
    private int _index;
    /** The Table of which I am a Column. */
    private Table _table;
    /** Source for rows of the table. */
    private TableIterator _rowSource;
    /** If non-null, the (fixed) value of this Column. */
    private String _value;
    /** If non-null, the name of this Column. */
    private String _alias = null;
}
