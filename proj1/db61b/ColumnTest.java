package db61b;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ColumnTest {
	private static Table TAB;
	private static List<Row> ROWS;
	private static final String[] COLS =new String[] {"SID", "NAME", "LEVEL", "CLASS"};
	private static final String NAME = "TEST";
	
	private static Column COL1;
	private static Column COL2;
	
	@BeforeClass
	public static void setUpOnce(){
		ROWS = new ArrayList<Row>();
		ROWS.add(new Row(new String[] {"1", "Timmy", "10", "Mage"}));
		ROWS.add(new Row(new String[] {"23", "Basd", "1", "Magec"}));
		ROWS.add(new Row(new String[] {"45", "Geortge", "10", "Maper"}));
		ROWS.add(new Row(new String[] {"231", "Halo", "10", "Warrior"}));		
		
		TAB = new Table(NAME, COLS);
		
		for(Row row : ROWS){
			TAB.add(row);
		}
	}
	
	@Before
	public void setUp(){
		COL1  = new Column(TAB, COLS[0]);
		COL2 = new Column(null, COLS[1]);
	}

	@Test
	public void testName() {
		assertEquals(COLS[0], COL1.name());
		assertEquals(COLS[1], COL2.name());
		Column anon = new Column("ANON");
		assertEquals(null, anon.name());
		anon.setAlias("ASDASD");
		assertEquals(null, anon.name());
		
		COL1.setAlias("qwe");
		assertEquals(COLS[0], COL1.name());
	}

	@Test
	public void testResolve() {
		Table other1 = new Table("LOL", new String[] {});
		Table other2 = new Table("CATS!", new String[] {});
		List<TableIterator> tabIts = new ArrayList<TableIterator>();

		tabIts.add(other1.tableIterator());
		tabIts.add(other2.tableIterator());
		tabIts.add(TAB.tableIterator());
		
		COL1.resolve(tabIts);
		assertEquals("1", COL1.value());
		COL2.resolve(tabIts);
		assertEquals("Timmy", COL2.value());
	}

	@Test
	public void testValue() {
		
		List<TableIterator> tList  = new ArrayList<TableIterator>();
		tList.add(TAB.tableIterator());
		COL1.resolve(tList);
		COL2.resolve(tList);
		
		int i = 0;
		do{
			assertEquals(ROWS.get(i).get(0), COL1.value());
			assertEquals(ROWS.get(i).get(1), COL2.value());
			
			tList.get(0).next();
			i++;
		} while(tList.get(0).hasRow());
	}

	@Test
	public void testAlias() {
		assertEquals(COLS[0], COL1.alias());
		assertEquals(COLS[1], COL2.alias());
		Column anon = new Column("ANON");
		assertEquals(null, anon.alias());
		anon.setAlias("ASDASD");
		assertEquals("ASDASD", anon.alias());
		assertEquals(null, anon.name());
		
		
		COL1.setAlias("qwe");
		assertEquals(COLS[0], COL1.name());
		assertEquals("qwe", COL1.alias());
	}

}
