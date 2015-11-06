package db61b;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TableIteratorTest {

    private Table testTable;
    private TableIterator tabIt;
    private List<Row> rows;

    @Before
    public void setUp() throws Exception {
        this.testTable = new Table("Test",
                new String[] { "SID", "NAME", "LEVEL", "CLASS" });

        this.rows = new ArrayList<Row>();
        this.rows.add(new Row(new String[] { "1", "Timmy", "10", "Mage" }));
        this.rows.add(new Row(new String[] { "23", "Basd", "1", "Magec" }));
        this.rows
                .add(new Row(new String[] { "45", "Geortge", "10", "Maper" }));
        this.rows
                .add(new Row(new String[] { "231", "Halo", "10", "Warrior" }));

        for (Row row : this.rows) {
            this.testTable.add(row);
        }

        this.tabIt = this.testTable.tableIterator();

    }

    public void assertRowEqual(int k) {
        for (int i = 0; i < this.testTable.numColumns(); i++) {
            assertEquals(this.rows.get(k).get(i), this.tabIt.value(i));
        }
    }

    @Test
    public void testReset() {
        this.tabIt.next();
        this.tabIt.next();
        this.tabIt.reset();
        this.assertRowEqual(0);
        this.tabIt.next();
        this.tabIt.reset();
        this.assertRowEqual(0);

    }

    @Test
    public void testTable() {
        assertEquals(this.testTable, this.tabIt.table());
    }

    @Test
    public void testHasRow() {
        for (int i = 0; i < this.rows.size(); i++) {
            assertTrue(this.tabIt.hasRow());
            this.tabIt.next();
        }
        assertFalse(this.tabIt.hasRow());
    }

    @Test
    public void testNext() {
        for (int i = 0; i < this.rows.size(); i++) {
            this.assertRowEqual(i);
            this.tabIt.next();
        }
    }

    @Test
    public void testColumnIndex() {
        for (int i = 0; i < this.rows.size(); i++) {
            for (int col = 0; col < this.testTable.numColumns(); col++) {
                int colIndex =
                        this.tabIt.columnIndex(this.testTable.title(col));
                assertEquals(col, colIndex);
                assertEquals(-1, this.tabIt.columnIndex("asdasdasd"));
                assertEquals(-1, this.tabIt.columnIndex(null));
            }
            this.tabIt.next();
        }
    }

    @Test
    public void testValue() {
        this.assertRowEqual(0);
    }

}
