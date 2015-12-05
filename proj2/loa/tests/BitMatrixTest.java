package loa.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import loa.util.BitMatrix;

public class BitMatrixTest {
    /**
     * The test matrix.
     */
    private static boolean[][] testMatrix;
    /**
     * The number of rows.
     */
    private static int numRows;
    /**
     * The number of columns.
     */
    private static int numCols;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testMatrix = new boolean[][] { { true, false, false, true },
            { false, true, false, true }, { false, false, true, true },
            { false, false, true, false } };

        numRows = testMatrix.length;
        numCols = testMatrix[0].length;

    }

    /**
     * The normal bit matrix.
     */
    private BitMatrix normalBM;
    /**
     * Emoty bit matrix.
     */
    private BitMatrix emptyBM;

    /**
     * Sets up the test.
     * @throws Exception Throws an exceptioin.
     */
    @Before
    public void setUp() throws Exception {
        this.normalBM = new BitMatrix(numRows, numCols);
        this.emptyBM = new BitMatrix(0, 0);

    }

    /**
     * Test clearing.
     */
    @Test
    public void testClear() {
        this.testSetGet();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numRows; col++) {
                this.normalBM.clear(row, col);
                assertFalse(this.normalBM.get(row, col));
            }
        }
    }

    /**
     * Test the set get feature.
     */
    @Test
    public void testSetGet() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numRows; col++) {
                if (testMatrix[row][col]) {
                    this.normalBM.set(row, col);
                }
                assertEquals(testMatrix[row][col],
                        this.normalBM.get(row, col));
            }
        }
    }

}
