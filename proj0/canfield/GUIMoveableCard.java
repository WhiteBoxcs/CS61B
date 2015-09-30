/**
 * @author MadcowD
 */
package canfield;

import java.awt.Point;

/**
 * @author MadcowD
 */
public class GUIMoveableCard extends GUICard {

    /**
     * @param repr
     *            The card's representation
     * @param type
     *            The type of the moveable card.
     * @param pos
     *            The initial position of the moveable card.
     * @param layer
     *            The layer of the card.
     */
    public GUIMoveableCard(Card repr, CardType type, Point pos, int layer) {
        super(repr, type, pos, layer);
        this.oldPos = null;
        this.offset = null;
    }

    @Override
    public void onClick(Point pos) {
    };

    @Override
    public void onDrag(Point pos) {

        if (this.oldPos == null) {
            this.oldPos = this.getPos();

            double xOff = pos.getX() - this.oldPos.getX();
            double yOff = pos.getY() - this.oldPos.getY();
            this.offset = new Point((int) xOff, (int) yOff);
        }

        double xPos = pos.getX() - this.offset.getX();
        double yPos = pos.getY() - this.offset.getY();

        this.setPos(new Point((int) xPos, (int) yPos));
    };

    @Override
    public void onRelease(Point pos) {
        if (this.oldPos != null) {
            this.setPos(this.oldPos);
        }
        this.oldPos = null;
        this.offset = null;
    };

    /* ==== Fields ==== */

    /**
     * THe old position of the card befoire it was moved.
     */
    private Point oldPos;

    /**
     * The offset of the click from the card.
     */
    private Point offset;

}
