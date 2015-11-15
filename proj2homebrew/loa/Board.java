package loa;

import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;

import loa.exceptions.InvalidMoveException;

/**
 * Represents a board class which stores the main data for the game.
 * @author William Hebgen Guss
 */
public class Board {
    public static final int SIZE =8;
    
    private Piece[][] data; 
    
    /**
     * Creates a new board object with default initialization.
     */
    public Board(){
        clear();
    }
    

    public void clear() {
        data = INITIAL_PIECES.clone();
        
    }

    /**
     * Gets the column based off of a alphabet character.
     * @param col The character.
     * @return The column/
     */
    public int toColPos(String col) {
        return col.charAt(0) - 97;
    }

    /**
     * Gets the row posisito0n of a string containing a single digit integer. 
     * @param p1 The string containing the digit. Assumed that it is not wrong.
     * @return The row position.
     */
    public int toRowPos(String p1) {
        return Integer.parseInt(p1.substring(1,2)) - 1;
    }

    
    /**
     * Gets the piece at a row and column.
     * @param row The row.
     * @param col The column.
     * @return the piece.
     */
    public Piece get(int row, int col){
        return data[row][col];
    }
    

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

    public double contiguityScore(Piece team) {
        // TODO Auto-generated method stub
        return 0;
    }


    /**
     * Performs a given move.
     * @param todo The move to do.
     * @throws InvalidMoveException If the move is illegal by the rules of the game.
     */
    public void performMove(Move todo) throws InvalidMoveException{
        // TODO Auto-generated method stub
        
    }

}
