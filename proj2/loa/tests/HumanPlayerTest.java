package loa.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
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
        player = new HumanPlayer(Piece.BP, 0);
        board = new Board(new Game("test"));
    }

    @Test
    public void testInputExpected() {
        assertTrue(player.inputExpected());
    }

    @Test
    public void testAct() {
        Move c = Move.create("b1-b3", board);
        assertEquals(c, player.act(c));
    }

    @Test
    public void testTurn() {
        Move validMove = Move.create("b1-b3", board);
        try {
            assertEquals(validMove, player.turn(validMove));
        } catch (InvalidMoveException e) {
            assertTrue(false);
        }
        
        Move invalidMove = Move.create("a3-c3", board);
        try {
            player.turn(invalidMove);
            assertTrue(false);
        } catch (InvalidMoveException e) {
            assertTrue(true);
        }  
    }

    @Test
    public void testTeam() {
        assertEquals(Piece.BP, player.team());
    }

}
