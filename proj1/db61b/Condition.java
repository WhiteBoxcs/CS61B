package db61b;

import java.util.List;

/**
 * Represents a single 'where' condition in a 'select' command.
 * @author William Hebgen Guss Guss
 */
class Condition {

    /**
     * Internally, we represent our relation as a 3-bit value whose bits denote
     * whether the relation allows the left value to be greater than the right
     * (GT), equal to it (EQ), or less than it (LT).
     */
    private static final int GT = 0b001, EQ = 0b010, LT = 0b100;

    /**
     * A Condition representing COL1 RELATION COL2, where COL1 and COL2 are
     * column designators. and RELATION is one of the strings "<", ">", "<=",
     * ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, Column col2) {
        this._col1 = col1;
        this._col2 = col2;
        this.compRep = 0;

        switch (relation) {
        case "<=":
            this.compRep |= EQ;
            this.compRep |= LT;
            break;
        case "<":
            this.compRep |= LT;
            break;
        case ">=":
            this.compRep |= EQ;
            this.compRep |= GT;
            break;
        case ">":
            this.compRep |= GT;
            break;
        case "=":
            this.compRep |= EQ;
            break;
        case "!=":
            this.compRep = (GT | LT) & ~EQ;
            break;
        default:
            throw new DBException("Error: invalid relation.");
        }

    }

    /**
     * A Condition representing COL1 RELATION 'VAL2', where COL1 is a column
     * designator, VAL2 is a literal value (without the quotes), and RELATION
     * is one of the strings "<", ">", "<=", ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, new Literal(val2));
    }

    /**
     * Assuming that ROWS are rows from the respective tables from which my
     * columns are selected, returns the result of performing the test I
     * denote.
     */
    boolean test() {
        int comp = this._col1.value().compareTo(this._col2.value());
        if (comp < 0 && (this.compRep & LT) != 0
                || comp > 0 && (this.compRep & GT) != 0
                || comp == 0 && (this.compRep & EQ) != 0) {
            return true;
        }
        return false;
    }

    /** Return true iff all CONDITIONS are satified. */
    static boolean test(List<Condition> conditions) {
        for (Condition cond : conditions) {
            if (!cond.test()) {
                return false;
            }
        }
        return true;
    }

    /** The first column. */
    private Column _col1;

    /** The second column. */
    private Column _col2;
    /** The comparison representation. */
    private int compRep;
}
