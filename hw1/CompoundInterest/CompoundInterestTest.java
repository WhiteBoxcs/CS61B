import org.junit.Test;
import static org.junit.Assert.*;


public class CompoundInterestTest {
	
	/**
	 * Evaluates if two values are within in a certain distance of one another
	 * @param x 
	 * @param y
	 * @param tolerance
	 * @return If the two values are contained within a bounded interval of length tolerance.
	 */
	private static boolean isBounded(double x,double y, double tolerance)
	{
		return Math.abs(x-y) < tolerance;
	}
	
	
    @Test
    public void testNumYears() {
        /** Sample assert statement for comparing integers.

        assertEquals(0, 0); */
    	//Edge cases
    	assertEquals(0, CompoundInterest.numYears(2015));
    	
    	// Standard case.
    	assertEquals(1, CompoundInterest.numYears(2016));
    	assertEquals(2, CompoundInterest.numYears(2017));
    }
    
    @Test
    public void testFutureValue() {
        double tolerance = 0.01;
        
        //Edge cases
        //No change
        assertTrue(isBounded(CompoundInterest.futureValue(10, 12, 2015), 10, tolerance));
        assertTrue(isBounded(CompoundInterest.futureValue(10, 0, 2015), 10, tolerance));
        
        //Negative change
        assertTrue(isBounded(CompoundInterest.futureValue(10, -10, 2017),8.1, tolerance));
        
        //General case.
        assertTrue(isBounded(CompoundInterest.futureValue(10, 12, 2017), 12.544, tolerance));
        
    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        //Edge cases
        //No change
        assertTrue(isBounded(CompoundInterest.futureValueReal(10, 12, 2015,0), 10, tolerance));
        assertTrue(isBounded(CompoundInterest.futureValueReal(10, 0, 2015,0), 10, tolerance));
        
        //Negative change
        assertTrue(isBounded(CompoundInterest.futureValueReal(10, -10, 2017,3),7.62129, tolerance));

        assertTrue(isBounded(CompoundInterest.futureValueReal(10, -10, 2017,-11.111111111111),10.0, tolerance));
        
        //General case
        assertTrue(isBounded(CompoundInterest.futureValueReal(10, 12, 2017,3), 11.8026496, tolerance));
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        
        //Edge case
        assertTrue(isBounded(CompoundInterest.totalSavings(5000, 2015, 10),5000, tolerance));
        
        //Negative change
        assertTrue(isBounded(CompoundInterest.totalSavings(5000, 2016, -10),9500, tolerance));
        
        //General case
        assertTrue(isBounded(CompoundInterest.totalSavings(5000, 2017, 10),16550, tolerance));
    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        
        //Edge case
        assertTrue(isBounded(CompoundInterest.totalSavingsReal(5000, 2015, 10,10),5000, tolerance));
        
        //Negative change
        assertTrue(isBounded(CompoundInterest.totalSavingsReal(5000, 2016, -10,10),8550, tolerance));
        
        //General case
        assertTrue(isBounded(CompoundInterest.totalSavingsReal(5000, 2017, 10,10),13405.5, tolerance));
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
       // System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
