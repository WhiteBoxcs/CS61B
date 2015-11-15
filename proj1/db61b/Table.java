package db61b;

import static db61b.Utils.error;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A single table in a database.
 * @author William Hebgen Guss Guss.
 */
class Table implements Iterable<Row> {
    /**
     * A new Table named NAME whose columns are give by COLUMNTITLES, which
     * must be distinct (else exception thrown).
     */
    Table(String name, String[] columnTitles) {
        this._name = name;
        this._rows = new ArrayList<Row>();

        for (int i = 0; i < columnTitles.length; i++) {
            for (int j = i + 1; j < columnTitles.length; j++) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error(
                            "non-distinct column"
                            + " title (%s), table not created.",
                            columnTitles[i]);
                }
            }
        }

        this._titles = columnTitles;

    }

    /** A new Table named NAME whose column names are give by COLUMNTITLES. */
    Table(String name, List<String> columnTitles) {
        this(name, columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    int numColumns() {
        return this._titles.length;
    }

    /** Returns my name. */
    String name() {
        return this._name;
    }

    /** Returns a TableIterator over my rows in an unspecified order. */
    TableIterator tableIterator() {
        return new TableIterator(this);
    }

    /** Returns an iterator that returns my rows in an unspecfied order. */
    @Override
    public Iterator<Row> iterator() {
        return this._rows.iterator();
    }

    /** Return the title of the Kth column. Requires 0 <= K < columns(). */
    String title(int k) {
        return this._titles[k];
    }

    /**
     * Return the number of the column whose title is TITLE, or -1 if there
     * isn't one.
     */
    int columnIndex(String title) {
        for (int i = 0; i < this._titles.length; i++) {
            if (this._titles[i].equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /** Return the number of Rows in this table. */
    int size() {
        return this._rows.size();
    }

    /**
     * Add ROW to THIS if no equal row already exists. Return true if anything
     * was added, false otherwise.
     */
    boolean add(Row row) {
        if (this._rows.contains(row)) {
            return false;
        }

        if (row.size() != this.numColumns()) {
            throw error("row length (%s) and" + " number of columns "
                    + "(%s) unequal.", row.size(), this.numColumns());
        }

        this._rows.add(row);

        return true;
    }

    /**
     * Read the contents of the file NAME.db, and return as a Table. Format
     * errors in the .db file cause a DBException.
     */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(name, columnNames);

            String cur;
            while ((cur = input.readLine()) != null) {
                String[] data = cur.split(",");

                if (data.length != columnNames.length) {
                    throw error(
                            "unmathched row content"
                            + " and collumn count in DB file.");
                }

                table.add(new Row(data));
            }

        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /**
     * Write the contents of TABLE into the file NAME.db. Any I/O errors cause
     * a DBException.
     */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            output = new PrintStream(name + ".db");

            output.println(Arrays.toString(this._titles).replace(" ", "")
                    .replace("[", "").replace("]", ""));

            for (Row row : this._rows) {
                output.println(row.toDBFormat());
            }

        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * Print my contents on the standard output, separated by spaces and
     * indented by two spaces.
     */
    void print() {

        for (Row row : this._rows) {
            System.out.println("  " + row.toString());
        }
    }

    /** My name. */
    private final String _name;
    /** My column titles. */
    private String[] _titles;

    /** The rows contained in the table. */
    private List<Row> _rows;
}
