package loa;

import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;

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

    public int toColPos(String col) {
        // TODO Auto-generated method stub
        return 1;
    }

    public int toRowPos(String p1) {
        // TODO Auto-generated method stub
        return 1;
    }

    public Enum<Direction> get(int column0, int row0) {
        // TODO Auto-generated method stub
        return null;
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

}
