/**
 * @author MadcowD
 */
package canfield;

import java.awt.Image;
import java.awt.Point;

/**
 * @author MadcowD
 * The empty card placeholder
 */
public class GUIEmptyCard extends GUICard {

    public GUIEmptyCard(CardType type, Point pos) {
        super(Card.C10, type, pos, -10);
    }

    @Override
    public Image getImage() {
        return GameDisplay.getImage("playing-cards/EMPTY.png");
    }

}
