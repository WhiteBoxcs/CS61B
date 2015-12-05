package loa.tests;

import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import loa.Board;
import loa.Game;
import loa.Move;
import loa.Piece;

public class BoardTest {

    static final Piece[][] TEST_BOARD =
        { { EMP, BP, BP, BP, BP, BP, BP, EMP },
        { WP, EMP, EMP, BP, EMP, EMP, EMP, WP },
        { WP, EMP, EMP, BP, EMP, EMP, EMP, WP },
        { WP, EMP, EMP, BP, EMP, EMP, EMP, WP },
        { WP, EMP, EMP, BP, EMP, EMP, EMP, WP },
        { WP, EMP, EMP, BP, EMP, EMP, EMP, WP },
        { WP, EMP, EMP, BP, EMP, EMP, EMP, WP },
        { EMP, BP, BP, BP, BP, BP, BP, EMP } };

    static final Piece[][] EMPTY_BOARD =
        { { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
        { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP } };


    /**
     * The standard board for testing.
     */
    private Board standardBoard;
    /**
     * The contiguous board for testing.
     */
    private Board contiguousBoard;
    /**
     * The blank board.
     */
    private Board blankBoard;

    /**
     * Sets up thwe test.
     * @throws Exception An interesting exception/.
     */
    @Before
    public void setUp() throws Exception {
        Game tester = new Game("tester");
        this.standardBoard = new Board(tester);
        this.contiguousBoard = new Board(tester);
        this.contiguousBoard.clear(TEST_BOARD);
        this.blankBoard = new Board(tester);
        this.blankBoard.clear(EMPTY_BOARD);

    }

    /**
     * Tests the clearing mechanisms.
     */
    @Test
    public void testClear() {
        this.standardBoard.clear(EMPTY_BOARD);
        assertEquals(this.standardBoard, this.blankBoard);

        this.standardBoard.clear(Board.INITIAL_PIECES);
        this.blankBoard.clear();
        assertEquals(this.standardBoard, this.blankBoard);
    }

    @Test
    public void testToColPos() {
        assertEquals(1, this.standardBoard.toColPos("a1"));
        assertEquals(2, this.standardBoard.toColPos("b1"));
        assertEquals(3, this.standardBoard.toColPos("c1"));
        assertEquals(4, this.standardBoard.toColPos("d1"));
        assertEquals(5, this.standardBoard.toColPos("e1"));
        assertEquals(6, this.standardBoard.toColPos("f1"));
        assertEquals(7, this.standardBoard.toColPos("g1"));
        assertEquals(8, this.standardBoard.toColPos("h1"));

        assertEquals(1, this.standardBoard.toColPos("a8"));
    }

    @Test
    public void testToRowPos() {
        assertEquals(1, this.standardBoard.toRowPos("a1"));
        assertEquals(2, this.standardBoard.toRowPos("b2"));
        assertEquals(3, this.standardBoard.toRowPos("c3"));
        assertEquals(4, this.standardBoard.toRowPos("d4"));
        assertEquals(5, this.standardBoard.toRowPos("e5"));
        assertEquals(6, this.standardBoard.toRowPos("f6"));
        assertEquals(7, this.standardBoard.toRowPos("g7"));
        assertEquals(8, this.standardBoard.toRowPos("h8"));

        assertEquals(1, this.standardBoard.toRowPos("c1"));
    }

    @Test
    public void testGet() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertEquals(TEST_BOARD[i][j],
                        this.contiguousBoard.get(i + 1, j + 1));
            }
        }
    }

    @Test
    public void testSet() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.blankBoard.set(i + 1, j + 1, TEST_BOARD[i][j]);
                assertEquals(TEST_BOARD[i][j],
                        this.blankBoard.get(i + 1, j + 1));
            }
        }
    }

    @Test
    public void testContiguityScore() {
        assertEquals(1.0, this.contiguousBoard.contiguityScore(Piece.BP), 0.1);
        assertEquals(0.5, this.standardBoard.contiguityScore(Piece.BP), 0.1);
        assertEquals(0.0, this.blankBoard.contiguityScore(Piece.BP), 0.1);
    }

    @Test
    public void testPossibleMoves() {
        List<Move> possible = this.standardBoard.possibleMoves(1, 2);
        assertTrue(
                possible.contains(Move.create("b1-b3", this.standardBoard)));
        assertTrue(
                possible.contains(Move.create("b1-d3", this.standardBoard)));
        assertTrue(
                possible.contains(Move.create("b1-h1", this.standardBoard)));
        assertEquals(3, possible.size());

        possible = this.standardBoard.possibleMoves(2, 1);
        assertTrue(
                possible.contains(Move.create("a2-c2", this.standardBoard)));
        assertTrue(
                possible.contains(Move.create("a2-c4", this.standardBoard)));
        assertTrue(
                possible.contains(Move.create("a2-a8", this.standardBoard)));
        assertEquals(3, possible.size());
    }

}
