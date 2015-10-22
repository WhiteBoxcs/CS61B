package db61b;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ColumnTest {
    private static Table _TAB;
    private static List<Row> _ROWS;
    private static final String[] _COLS =
            new String[] { "SID", "NAME", "LEVEL", "CLASS" };
    private static final String _NAME = "TEST";

    private static Column COL1;
    private static Column COL2;

    @BeforeClass
    public static void setUpOnce() {
        _ROWS = new ArrayList<Row>();
        _ROWS.add(new Row(new String[] { "1", "Timmy", "10", "Mage" }));
        _ROWS.add(new Row(new String[] { "23", "Basd", "1", "Magec" }));
        _ROWS.add(new Row(new String[] { "45", "Geortge", "10", "Maper" }));
        _ROWS.add(new Row(new String[] { "231", "Halo", "10", "Warrior" }));

        _TAB = new Table(_NAME, _COLS);

        for (Row row : _ROWS) {
            _TAB.add(row);
        }
    }

    @Before
    public void setUp() {
        COL1 = new Column(_TAB, _COLS[0]);
        COL2 = new Column(null, _COLS[1]);
    }

    @Test
    public void testName() {
        assertEquals(_COLS[0], COL1.name());
        assertEquals(_COLS[1], COL2.name());
        Column anon = new Column("ANON");
        assertEquals(null, anon.name());
        anon.setAlias("ASDASD");
        assertEquals(null, anon.name());

        COL1.setAlias("qwe");
        assertEquals(_COLS[0], COL1.name());
    }

    @Test
    public void testResolve() {
        Table other1 = new Table("LOL", new String[] {});
        Table other2 = new Table("CATS!", new String[] {});
        List<TableIterator> tabIts = new ArrayList<TableIterator>();

        tabIts.add(other1.tableIterator());
        tabIts.add(other2.tableIterator());
        tabIts.add(_TAB.tableIterator());

        COL1.resolve(tabIts);
        assertEquals("1", COL1.value());
        COL2.resolve(tabIts);
        assertEquals("Timmy", COL2.value());
    }

    @Test
    public void testValue() {

        List<TableIterator> tList = new ArrayList<TableIterator>();
        tList.add(_TAB.tableIterator());
        COL1.resolve(tList);
        COL2.resolve(tList);

        int i = 0;
        do {
            assertEquals(_ROWS.get(i).get(0), COL1.value());
            assertEquals(_ROWS.get(i).get(1), COL2.value());

            tList.get(0).next();
            i++;
        } while (tList.get(0).hasRow());
    }

    @Test
    public void testAlias() {
        assertEquals(_COLS[0], COL1.alias());
        assertEquals(_COLS[1], COL2.alias());
        Column anon = new Column("ANON");
        assertEquals(null, anon.alias());
        anon.setAlias("ASDASD");
        assertEquals("ASDASD", anon.alias());
        assertEquals(null, anon.name());

        COL1.setAlias("qwe");
        assertEquals(_COLS[0], COL1.name());
        assertEquals("qwe", COL1.alias());
    }

}
