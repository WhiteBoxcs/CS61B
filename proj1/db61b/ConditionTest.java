package db61b;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConditionTest {

    private static Table _tab;
    private static List<Row> _rows;
    private static final String[] COLS =
            new String[] { "SID", "NAME", "LEVEL", "CLASS" };
    private static final String NAME = "TEST";

    private static Column _col1;
    private static Column col2;
    private TableIterator tabIt;

    @BeforeClass
    public static void setUpOnce() {
        _rows = new ArrayList<Row>();
        _rows.add(new Row(new String[] { "1", "Timmy", "10", "Mage" }));
        _rows.add(new Row(new String[] { "23", "Basd", "1", "Magec" }));
        _rows.add(new Row(new String[] { "45", "Geortge", "10", "Maper" }));
        _rows.add(new Row(new String[] { "231", "Halo", "10", "Warrior" }));

        _tab = new Table(NAME, COLS);

        for (Row row : _rows) {
            _tab.add(row);
        }

        _col1 = new Column(_tab, COLS[0]);
        col2 = new Column(_tab, COLS[1]);

    }

    @Before
    public void setUp() {
        this.tabIt = _tab.tableIterator();
        List<TableIterator> tList = new ArrayList<TableIterator>();
        tList.add(this.tabIt);
        _col1.resolve(tList);
        col2.resolve(tList);
    }

    @Test
    public void testTest() {
        Condition gt = new Condition(_col1, ">", col2);
        Condition lt = new Condition(_col1, "<", col2);
        Condition geq = new Condition(_col1, ">=", col2);
        Condition leq = new Condition(_col1, "<=", col2);
        Condition neq = new Condition(_col1, "!=", col2);
        Condition eq = new Condition(_col1, "=", col2);

        do {
            assertFalse(gt.test());
            assertTrue(lt.test());
            assertFalse(eq.test());
            assertTrue(leq.test());
            assertFalse(geq.test());
            assertTrue(neq.test());

            this.tabIt.next();
        } while (this.tabIt.hasRow());

    }

}
