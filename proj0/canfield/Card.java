package canfield;

/** The cards in a deck, ordered first by suit (Club, Diamond, Heart,
 *  Spade) and then by rank (Ace through King).
 *  @author P. N. Hilfinger
 */
enum Card {
    /** The cards.  Names start with one character for the suit,
     *  followed by the rank (to jibe with Java syntax). */
    CA, C2, C3, C4, C5, C6, C7, C8, C9, C10, CJ, CQ, CK,
    DA, D2, D3, D4, D5, D6, D7, D8, D9, D10, DJ, DQ, DK,
    HA, H2, H3, H4, H5, H6, H7, H8, H9, H10, HJ, HQ, HK,
    SA, S2, S3, S4, S5, S6, S7, S8, S9, S10, SJ, SQ, SK;

    /** Number of card suits. */
    static final int NUM_SUITS = 4;
    /** Number of card ranks. */
    static final int NUM_RANKS = 13;

    /** Rank names for face cards and ace. */
    static final int ACE = 1, JACK = 11, QUEEN = 12, KING = 13;

    /** Return my name in standard notation. */
    @Override
    public String toString() {
        String name = name();
        return name.substring(1) + name.charAt(0);
    }

    /** Return my rank as an integer from 1 (Ace) to 13 (King). */
    int rank() {
        return ordinal() % NUM_RANKS + 1;
    }


    /** Return my suit as an integer from 1 (Club) to 4 (Spade). */
    int suit() {
        return ordinal() / NUM_RANKS + 1;
    }

    /** Return my suit as a single-character abbreviation. */
    String suitAbbrev() {
        return name().substring(0, 1);
    }

    /** Return my abbreviated rank. */
    String rankAbbrev() {
        return name().substring(1);
    }

    /** Return the full name of my suit (singular). */
    String suitName() {
        switch (suit()) {
        case 1:
            return "Club";
        case 2:
            return "Diamond";
        case 3:
            return "Heart";
        case 4:
            return "Spade";
        default:
            assert false;
            return null;
        }
    }

    /** Return the full name of my rank. */
    String rankName() {
        switch (rank()) {
        case ACE:
            return "Ace";
        case JACK:
            return "Jack";
        case QUEEN:
            return "Queen";
        case KING:
            return "King";
        default:
            return Integer.toString(rank());
        }
    }

    /** Return true iff this is a red card. */
    boolean isRed() {
        return suit() == 2 || suit() == 3;
    }

    /** Return the inverse of toString: the Card whose toString value
     *  is NAME. */
    public static Card toCard(String name) {
        return valueOf(name.substring(name.length() - 1)
                       + name.substring(0, name.length() - 1));
    }
}
