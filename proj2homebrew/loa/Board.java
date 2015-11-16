package loa;

import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;

import java.util.BitSet;

import loa.exceptions.InvalidMoveException;
import loa.util.BitMatrix;

/**
 * Represents a board class which stores the main data for the game.
 * @author William Hebgen Guss
 */
public class Board {
    public static final int SIZE =8;
    
    private Piece[][] data;
    private int[] pieceCount;
    private Game _owner; 
    
    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };
    
    static final int INITIAL_BPC = 12;
    static final int INITIAL_WPC = 12;
    
    /**
     * Creates a new board object with default initialization.
     */
    public Board(Game owner){
        this._owner = owner;
        clear();
    }
    
    /**
     * Clears the board to its initital state.
     */
    public void clear() {
        data = INITIAL_PIECES.clone();
        pieceCount = new int[Piece.values().length];
        pieceCount[Piece.WP.ordinal()] = INITIAL_WPC;
        pieceCount[Piece.BP.ordinal()] = INITIAL_BPC;
    }

    /**
     * Gets the column based off of a alphabet character.
     * @param col The character.
     * @return The column/
     */
    public int toColPos(String col) {
        return col.charAt(0) - 96;
    }

    /**
     * Gets the row posisito0n of a string containing a single digit integer. 
     * @param p1 The string containing the digit. Assumed that it is not wrong.
     * @return The row position.
     */
    public int toRowPos(String p1) {
        return Integer.parseInt(p1.substring(1,2));
    }

    
    /**
     * Gets the piece at a row and column.
     * @param row The row.
     * @param col The column.
     * @return the piece.
     */
    public Piece get(int row, int col){
        return data[row-1][col-1];
    }
    
    /**
     * Sets a piece at a target position. Assumes newPiece is not null.
     * @param newPiece the new Piece.
     * @param row the row.
     * @param col the column.
     */
    public void set(Piece newPiece, int row, int col){
        this.pieceCount[this.data[row-1][col-1].ordinal()]--;
        this.data[row-1][col-1] = newPiece;
        this.pieceCount[newPiece.ordinal()]++;
    }


    public double contiguityScore(Piece team) {
        BitMatrix explored = new BitMatrix(SIZE,SIZE);
        
        
        // TODO Auto-generated method stub
        return 0;
    }


    /**
     * Performs a given move.
     * @param todo The move to do.
     * @throws InvalidMoveException If the move is illegal by the rules of the game.
     * @return Move for chaining.
     */
    public Move performMove(Move todo) throws InvalidMoveException{
        // TODO Auto-generated method stub
        return null;
    }

}
