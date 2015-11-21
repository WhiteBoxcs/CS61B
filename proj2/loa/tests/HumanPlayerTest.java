package loa.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import loa.Board;
import loa.Game;
import loa.Move;
import loa.Piece;
import loa.exceptions.InvalidMoveException;
import loa.players.HumanPlayer;
import loa.players.Player;

public class HumanPlayerTest {

    private Player player;
    private Board board;

    @Before
    public void setUp() throws Exception {
        this.player = new HumanPlayer(Piece.BP, 0);
        this.board = new Board(new Game("test"));
    }

    @Test
    public void testInputExpected() {
        assertTrue(this.player.inputExpected());
    }

    @Test
    public void testAct() {
        Move c = Move.create("b1-b3", this.board);
        assertEquals(c, this.player.act(c));
    }

    @Test
    public void testTurn() {
        Move validMove = Move.create("b1-b3", this.board);
        try {
            assertEquals(validMove, this.player.turn(validMove));
        } catch (InvalidMoveException e) {
            assertTrue(false);
        }

        Move invalidMove = Move.create("a3-c3", this.board);
        try {
            this.player.turn(invalidMove);
            assertTrue(false);
        } catch (InvalidMoveException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testTeam() {
        assertEquals(Piece.BP, this.player.team());
    }

}
