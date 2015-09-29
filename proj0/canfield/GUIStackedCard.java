/**
 * @author MadcowD
 */
package canfield;

import java.awt.Point;

/**
 * @author MadcowD Generates a new stacked GUI card. These horrible naming
 *         convention is namely because there aren't sub packages.
 */
public class GUIStackedCard extends GUIMoveableCard {

    /** The distance of reveal for cars **/
    private static final int REVEAL = 30;

    /**
     * Full fledged constructor specifying further cards on the stack
     * 
     * @param repr
     * @param type
     * @param pos
     * @param nextCard
     * @param base
     */
    public GUIStackedCard(Card repr, CardType type, Point pos, GUIStackedCard nextCard,
            boolean base) {
        super(repr, type, pos, nextCard != null ? nextCard.getLayer() - 1 : 13);
        this.base = base;
        this.nextCard = nextCard;
    }

    /***
     * Alternative constructor for last card on stack (or single stacks).
     * 
     * @param repr
     * @param type
     * @param pos
     * @param base
     */
    public GUIStackedCard(Card repr, CardType type, Point pos, boolean base) {
        this(repr, type, pos, null, base);
        this.nextCard = null;
    }

    /* ==== Movement of Stacked Cards ==== */

    @Override
    public void onClick(Point pos) {
        super.onClick(pos);
        if (this.nextCard != null) {
            this.nextCard.onClick(pos);
        }
    }

    @Override
    public void onDrag(Point pos) {
        if (this.base) {
            this.pileDrag(pos);
        }
    };

    @Override
    public void onRelease(Point pos) {
        super.onRelease(pos);
        if (this.nextCard != null) {
            this.nextCard.onRelease(pos);
        }
    };

    /**
     * Drags a pile card recursively
     * 
     * @param pos
     *            the position of the mouse
     * @param justified
     *            if the drag is justified.
     */
    private void pileDrag(Point pos) {
        super.onDrag(pos);
        if (this.nextCard != null) {
            this.nextCard.pileDrag(pos);
        }

    }

    @Override
    public void setLayer(int layer) {
        super.setLayer(layer);
        if (this.nextCard != null) {
            this.nextCard.setLayer(layer + 1);
        }
    };

    /* ==== Fields ==== */

    private boolean base;
    private GUIStackedCard nextCard;

}
