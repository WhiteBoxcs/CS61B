// Remove all comments that begin with //, and replace appropriately.
// Feel free to modify ANYTHING in this file.
package loa;

import static loa.Piece.*;

import loa.exceptions.InvalidMoveException;

/** A move in Lines of Action.
 *  @author P. N. Hilfinger */
public class Move {

    /* Implementation note: We create moves by means of static "factory
     * methods" all named create, which in turn use the single (private)
     * constructor.  There is a unique Move for each combination of arguments.
     * As a result the default equality operation (same as ==) will
     * work. */

    /** Return a move on BOARD denoted by a prefix of S (after trimming),
     *  or invalid move if S denotes no valid move. Returns null iff
     *  the string matches in no way. */
    public static Move create(String s, Board board) {
        s = s.trim();
        if (s.matches("[a-h][1-9]-[a-h][1-9]\\b.*")) {
            String p1 = s.substring(0, 2);
            String p2 = s.substring(3);
            return create(s, board.toColPos(p1), board.toRowPos(p1),
                          board.toColPos(p2), board.toRowPos(p2), board);
        } else {
            return new Move(s);
        }
    }

    /** Return a move of the piece at COLUMN0, ROW0 to COLUMN1, ROW1, on
     *  BOARD or null if this move is always invalid. */
    public static Move create(String s, int column0, int row0, int column1, int row1,
                       Board board) {
        if (!inBounds(column0, row0) || !inBounds(column1, row1)) {
            return new Move(s);
        }
        int moved = board.get(row0, column0).ordinal();
        int replaced = board.get(row1, column1).ordinal();
        return _moves[column0][row0][column1][row1][moved][replaced];
    }

    /** Return a K step move from (COLUMN0, ROW0) in the direction DIR on
     *  BOARD. */
    static Move create(int column0, int row0, int k, Direction dir,
                       Board board) {
        return create("", column0, row0, column0 + dir.dc * k, row0 + dir.dr * k,
                      board);
    }

    private boolean _invalid;
    
    public boolean isInvalid() {
        return _invalid;
    }

    private String _creationString;

    /** A new Move of the piece at COL0, ROW0 to COL1, ROW1. MOVED is the
     *  piece being moved from COL0, ROW0, and REPLACED is the piece (or EMP)
     *  that it replaces. */
    private Move(int col0, int row0, int col1, int row1,
                 Piece moved, Piece replaced) {
        assert 1 <= col0 && col0 <= Board.SIZE && 1 <= row0 && row0 <= Board.SIZE
            && 1 <= col1 && col1 <= Board.SIZE && 1 <= row1 && row1 <= Board.SIZE
            && (col0 == col1 || row0 == row1 || col0 + row0 == col1 + row1
                || col0 - row0 == col1 - row1)
            && moved != EMP && moved != null && replaced != null;
        _col0 = col0;
        _row0 = row0;
        _col1 = col1;
        _row1 = row1;
        _moved = moved;
        _replaced = replaced;
        _invalid = false;
    }
    
    private Move(String creationString){
        _col0 = -1;
        _row0= -1;
        _col1 = -1;
        _row1 = -1;
        _moved = null;
        _replaced = null;
        _invalid = true;
        _creationString = creationString;
    }

    /** Return the column at which this move starts, as an index in 1--8. */
    public int getCol0() {
        return _col0;
    }

    /** Return the row at which this move starts, as an index in 1--8. */
    public int getRow0() {
   
        return _row0;
    }

    /** Return the column at which this move ends, as an index in 1--8. */
    public int getCol1() {
        
        
        return _col1;
    }

    /** Return the row at which this move ends, as an index in 1--8. */
    public int getRow1() {
        
        return _row1;
    }

    /** Return the piece on BOARD that is moved by THIS. */
    public Piece movedPiece() {
        
        return _moved;
    }

    /** Return the piece on BOARD that is replaced by THIS (or EMP
     *  if none). */
    public Piece replacedPiece() {
        
        return _replaced;
    }

    /** Return the length of this move (number of squares moved). */
    int length() {
//        if(_invalid)
//            throw new InvalidMoveException(this);
        
        return Math.max(Math.abs(_row1 - _row0), Math.abs(_col1 - _col0));
    }

    /** Return true IFF (C, R) denotes a square on the board, that is if
     *  1 <= C <= M, 1 <= R <= M. */
    public static boolean inBounds(int c, int r) {
        return 1 <= c && c <= Board.SIZE && 1 <= r && r <= Board.SIZE;
    }

    @Override
    public String toString() {
        if(_invalid)
            return _creationString;
        else
            return String.format("%c%d-%c%d", (char) (_col0 - 1 + 'a'), _row0,
                             (char) (_col1 - 1 + 'a'), _row1);
    }

    /** Column and row numbers of starting and ending points. */
    private final int _col0, _row0, _col1, _row1;
    /** Piece moved. */
    private final Piece _moved;
    /** Piece replaced. */
    private final Piece _replaced;

    /** The set of all possible Moves, indexed by row and column of
     *  start, row and column of destination, piece moved and piece replaced. */
    private static Move[][][][][][] _moves =
        new Move[Board.SIZE + 1][Board.SIZE + 1][Board.SIZE + 1][Board.SIZE + 1][2][3];

    static {
        for (int m = 0; m <= 1; m += 1) {
            for (int r = 0; r <= 2; r += 1) {
                Piece pm = Piece.values()[m], pr = Piece.values()[r];
                if (pm == pr || pm == EMP) {
                    continue;
                }
                for (int r0 = 1; r0 <= Board.SIZE; r0 += 1) {
                    for (int c0 = 1; c0 <= Board.SIZE; c0 += 1) {
                        for (int k = 1; k <= Board.SIZE; k += 1) {
                            if (k != r0) {
                                _moves[c0][r0][c0][k][m][r] =
                                    new Move(c0, r0, c0, k, pm, pr);
                                if ((char) (c0 - r0 + k - 1) < Board.SIZE) {
                                    _moves[c0][r0][c0 - r0 + k][k][m][r]
                                        = new Move(c0, r0, c0 - r0 + k, k,
                                                   pm, pr);
                                }
                            }
                            if (k != c0) {
                                _moves[c0][r0][k][r0][m][r] =
                                    new Move(c0, r0, k, r0, pm, pr);
                                if ((char) (c0 + r0 - k - 1) < Board.SIZE) {
                                    _moves[c0][r0][k][c0 + r0 - k][m][r]
                                        = new Move(c0, r0, k, c0 + r0 - k,
                                                   pm, pr);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
