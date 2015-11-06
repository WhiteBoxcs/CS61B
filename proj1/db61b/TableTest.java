package db61b;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TableTest {

    private Table tab;
    private static List<Row> _rows;
    private static final String[] COLS =
            new String[] { "SID", "NAME", "LEVEL", "CLASS" };
    private static final String NAME = "TEST";

    @BeforeClass
    public static void setUpOnce() {
        _rows = new ArrayList<Row>();
        _rows.add(new Row(new String[] { "1", "Timmy", "10", "Mage" }));
        _rows.add(new Row(new String[] { "23", "Basd", "1", "Magec" }));
        _rows.add(new Row(new String[] { "45", "Geortge", "10", "Maper" }));
        _rows.add(new Row(new String[] { "231", "Halo", "10", "Warrior" }));
    }

    @Before
    public void setUp() throws Exception {
        this.tab = new Table(NAME, COLS);
    }

    @Test
    public void testNumColumns() {
        assertEquals(COLS.length, this.tab.numColumns());
    }

    @Test
    public void testName() {
        assertEquals(NAME, this.tab.name());
    }

    @Test
    public void testTitle() {
        for (int i = 0; i < COLS.length; i++) {
            assertEquals(COLS[i], this.tab.title(i));
        }
    }

    @Test
    public void testColumnIndex() {
        for (int i = 0; i < COLS.length; i++) {
            assertEquals(i, this.tab.columnIndex(COLS[i]));
        }
        assertEquals(-1, this.tab.columnIndex("asdasd"));
        assertEquals(-1, this.tab.columnIndex(""));
        assertEquals(-1, this.tab.columnIndex(null));
    }

    @Test
    public void testSize() {
        for (Row row : _rows) {
            this.tab.add(row);
        }

        assertEquals(_rows.size(), this.tab.size());
    }

    @Test
    public void testAdd() {
        for (int i = 0; i < _rows.size(); i++) {
            this.tab.add(_rows.get(i));
            assertEquals(i + 1, this.tab.size());
        }
    }

}
