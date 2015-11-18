package loa.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import loa.Board;
import loa.Game;
import loa.Move;
import loa.Piece;
import loa.players.AcyclicMachinePlayer;

/**
 * Tests the internal private cycle checker of the acyclic machine player.
 * @author william
 */
public class AcyclicMachinePlayerTest extends AcyclicMachinePlayer{
    /**
     * Sets up the general test ype.
     * @throws Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        game = new Game( "AcyclicMachinePlayerTest");
        game.start();
        board = new Board(game);
        s = new Move[] {
                Move.create("b1-b2", board),
                Move.create("b1-b3", board),
                Move.create("b1-b4", board),
                Move.create("b1-b5", board),
                Move.create("b1-b6", board),
                Move.create("b1-b7", board)
        };
        
        testBed = new Move[][]{
            {s[0]},
            {s[0],s[1],s[0],s[1]},
            {s[0],s[1],s[0],s[2]},
            {s[0],s[1],s[3],s[0],s[1],s[3]},
            {s[0],s[1],s[3],s[0],s[1],s[1]},
            {s[0],s[0],s[0],s[0],s[0],s[0]},
            {s[0],s[1],s[3],s[0],s[1]}
        };
        
    }
    
    private static Move[][] testBed;
    private static Move[] s;
    private static Game game;
    private static Board board;
    

    /**
     * Constructs the test (not necisarry).
     * @param team The piece team on which the player is.
     * @param initScore The iniital score of the test.
     * @param container The game container of the test.
     */
    public AcyclicMachinePlayerTest() {
        super(Piece.BP, 0, game);
    }




    @Test
    public void cyclicMoveTest() {
        AcyclicMachinePlayerTest testPlayer
        = new AcyclicMachinePlayerTest();
        
        for(int i = 0; i < testBed.length; i++){
            this.history.clear();
            ArrayList<Move> test = new ArrayList<>(Arrays.asList(testBed[i]));
            test.remove(test.size()-1);
            this.history.addAll(test);
 
            assertEquals(i % 2 != 0, cyclicMove(testBed[i][test.size()]));
        }
    }

}
