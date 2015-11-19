package loa.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import loa.util.BitMatrix;

public class BitMatrixTest {
    private static boolean[][] testMatrix;
    private static int numRows;
    private static int numCols;
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testMatrix = new boolean[][]{
            {true,  false, false, true},
            {false, true,  false, true},
            {false, false, true,  true},
            {false, false, true,  false}
        };
        
        numRows = testMatrix.length;
        numCols= testMatrix[0].length;
        
    }

    private BitMatrix normalBM;
    private BitMatrix emptyBM;
    
    @Before
    public void setUp() throws Exception {
        normalBM = new BitMatrix(numRows, numCols);
        emptyBM  = new BitMatrix(0,0);
        
    }

    @Test
    public void testClear() {
        testSetGet();
        
        for(int row = 0; row < numRows; row++)
            for(int col = 0; col < numRows; col++){
                normalBM.clear(row, col);
                assertFalse(normalBM.get(row, col));
            }
    }

    @Test
    public void testSetGet() {
        for(int row = 0; row < numRows; row++)
            for(int col = 0; col < numRows; col++){
                if(testMatrix[row][col])
                    normalBM.set(row, col);
                assertEquals(testMatrix[row][col], normalBM.get(row, col));
            }
    }

}
