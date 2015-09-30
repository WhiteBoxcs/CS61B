/**
 * @author MadcowD
 */
package canfield;

import java.awt.Image;
import java.awt.Point;

/**
 * @author MadcowD The empty card placeholder
 */
public class GUIEmptyCard extends GUICard {
    /**
     * The default card.
     */
    public static final Card DEFAULT = Card.C10;

    /**
     * @param type
     *            the type of empty card
     * @param pos
     *            the position of the empty card.
     */
    public GUIEmptyCard(CardType type, Point pos) {
        super(DEFAULT, type, pos, -10);
    }

    @Override
    public Image getImage() {
        return GameDisplay.getImage("playing-cards/EMPTY.png");
    }

}
