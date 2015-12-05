package loa;

import static loa.Piece.EMP;

/**
 * A move in Lines of Action.
 * @author P. N. Hilfinger
 */
public class Move {
    /**
     * The hash prime for equivalency.
     */
    private static final int HASHCOPRIME = 1237;

    /**
     * The hash prime for equivalency.
     */
    private static final int HASHPRIME = 1231;

    /**
     * Return a move on BOARD denoted by a prefix of S (after trimming), or
     * invalid move if S denotes no valid move. Returns null iff the string
     * matches in no way.
     * @param s
     *            the string to create the move from.
     * @param board
     *            the board on which to create a string.
     */
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

    /**
     * Return a move of the piece at COLUMN0, ROW0 to COLUMN1, ROW1, on BOARD
     * or null if this move is always invalid.
     * @param s
     *            The creation string.
     * @param column0
     *            The first coplum
     * @param row0
     *            The row
     * @param column1
     *            The second colunmn
     * @param row1
     *            The second row.
     * @param board
     *            The board on whic the mvoe is created.
     * @return The created move.
     */
    public static Move create(String s, int column0,
            int row0, int column1, int row1, Board board) {
        if (!board.inBounds(column0, row0) || !board.inBounds(column1, row1)) {
            return new Move(s);
        }
        int moved = board.get(row0, column0).ordinal();
        int replaced = board.get(row1, column1).ordinal();
        return _moves[column0][row0][column1][row1][moved][replaced];
    }

    /**
     * Return a K step move from (COLUMN0, ROW0) in the direction DIR on BOARD.
     */
    static Move create(int column0, int row0,
            int k, Direction dir, Board board) {
        return create("", column0, row0, column0 + dir.dc * k,
                row0 + dir.dr * k, board);
    }

    /**
     * If the move was invalid.
     */
    private boolean _invalid;

    /**
     * If the move was invalid or not.
     * @return The invalidity of the move.
     */
    public boolean isInvalid() {
        return this._invalid;
    }

    /**
     * The creati9on string.
     */
    private String _creationString;

    /**
     * A new Move of the piece at COL0, ROW0 to COL1, ROW1. MOVED is the piece
     * being moved from COL0, ROW0, and REPLACED is the piece (or EMP) that it
     * replaces.
     */
    private Move(int col0, int row0, int col1, int row1, Piece moved,
            Piece replaced) {
        assert 1 <= col0 && col0 <= Board.SIZE && 1 <= row0
                && row0 <= Board.SIZE && 1 <= col1 && col1 <= Board.SIZE
                && 1 <= row1 && row1 <= Board.SIZE
                && (col0 == col1 || row0 == row1 || col0 + row0 == col1 + row1
                        || col0 - row0 == col1 - row1)
                && moved != EMP && moved != null && replaced != null;
        this._col0 = col0;
        this._row0 = row0;
        this._col1 = col1;
        this._row1 = row1;
        this._moved = moved;
        this._replaced = replaced;
        this._invalid = false;
    }

    /**
     * Creates an invalid move.
     * @param creationString
     *            The string with which to invalidate the move.
     */
    private Move(String creationString) {
        this._col0 = -1;
        this._row0 = -1;
        this._col1 = -1;
        this._row1 = -1;
        this._moved = null;
        this._replaced = null;
        this._invalid = true;
        this._creationString = creationString;
    }

    /** Return the column at which this move starts, as an index in 1--8. */
    public int getCol0() {
        return this._col0;
    }

    /** Return the row at which this move starts, as an index in 1--8. */
    public int getRow0() {

        return this._row0;
    }

    /** Return the column at which this move ends, as an index in 1--8. */
    public int getCol1() {

        return this._col1;
    }

    /** Return the row at which this move ends, as an index in 1--8. */
    public int getRow1() {

        return this._row1;
    }

    /** Return the piece on BOARD that is moved by THIS. */
    public Piece movedPiece() {

        return this._moved;
    }

    /**
     * Return the piece on BOARD that is replaced by THIS (or EMP if none).
     */
    public Piece replacedPiece() {

        return this._replaced;
    }

    /** Return the length of this move (number of squares moved). */
    int length() {

        return Math.max(Math.abs(this._row1 - this._row0),
                Math.abs(this._col1 - this._col0));
    }

    @Override
    public String toString() {
        if (this._invalid) {
            return this._creationString;
        } else {
            return String.format("%c%d-%c%d", (char) (this._col0 - 1 + 'a'),
                    this._row0, (char) (this._col1 - 1 + 'a'), this._row1);
        }
    }

    /** Column and row numbers of starting and ending points. */
    private final int _col0, _row0, _col1, _row1;
    /** Piece moved. */
    private final Piece _moved;
    /** Piece replaced. */
    private final Piece _replaced;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this._col0;
        result = prime * result + this._col1;
        result = prime * result + (this._creationString == null ? 0
                : this._creationString.hashCode());
        result = prime * result + (this._invalid ? HASHPRIME : HASHCOPRIME);
        result = prime * result
                + (this._moved == null ? 0 : this._moved.hashCode());
        result = prime * result
                + (this._replaced == null ? 0 : this._replaced.hashCode());
        result = prime * result + this._row0;
        result = prime * result + this._row1;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Move other = (Move) obj;
        if (this._col0 != other._col0) {
            return false;
        }
        if (this._col1 != other._col1) {
            return false;
        }
        if (this._creationString == null) {
            if (other._creationString != null) {
                return false;
            }
        } else if (!this._creationString.equals(other._creationString)) {
            return false;
        }
        if (this._invalid != other._invalid) {
            return false;
        }
        if (this._moved != other._moved) {
            return false;
        }
        if (this._replaced != other._replaced) {
            return false;
        }
        if (this._row0 != other._row0) {
            return false;
        }
        if (this._row1 != other._row1) {
            return false;
        }
        return true;
    }

    /**
     * The set of all possible Moves, indexed by row and column of start, row
     * and column of destination, piece moved and piece replaced.
     */
    private static Move[][][][][][] _moves = new Move[Board.SIZE
            + 1][Board.SIZE + 1][Board.SIZE + 1][Board.SIZE + 1][2][3];

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
                                    _moves[c0][r0][c0 - r0 + k][k][m][r] =
                                            new Move(c0, r0, c0 - r0 + k, k,
                                                    pm, pr);
                                }
                            }
                            if (k != c0) {
                                _moves[c0][r0][k][r0][m][r] =
                                        new Move(c0, r0, k, r0, pm, pr);
                                if ((char) (c0 + r0 - k - 1) < Board.SIZE) {
                                    _moves[c0][r0][k][c0 + r0 - k][m][r] =
                                            new Move(c0, r0, k, c0 + r0 - k,
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
