/**
 * @author MadcowD
 */
package canfield;

import java.awt.Point;

/**
 * @author MadcowD
 * Generates a new stacked GUI card. These horrible naming convention is namely 
 * because there aren't sub packages.
 */
public class GUIStackedCard extends GUIMoveableCard {
    
    /** The distance of reveal for cars **/
    private static final int REVEAL = 30;
    
    /**
     * Full fledged constructor specifying further cards on the stack
     * @param repr
     * @param type
     * @param pos
     * @param nextCard
     * @param base
     */
    public GUIStackedCard(Card repr, CardType type, Point pos, GUIStackedCard nextCard, boolean base) {
        super(repr, type, pos, nextCard != null ? nextCard.getLayer() + 1 : 0 );
        this.base = base;
        this.nextCard = nextCard;
    }
    
    /***
     * Alternative constructor for last card on stack (or single stacks).
     * @param repr
     * @param type
     * @param pos
     * @param base
     */
    public GUIStackedCard(Card repr, CardType type, Point pos, boolean base){
        this(repr, type, pos, null, base);
        this.nextCard = null;
    }
    
    /* ==== Movement of Stacked Cards ==== */
    
    @Override
    public void onClick(Point pos, boolean justified){
        super.onClick(pos, justified);
        if(nextCard != null)
            nextCard.onClick(pos, justified);
    }
    
    @Override
    public void onDrag(Point pos, boolean justified) {
        if(base)
            this.pileDrag(pos, justified);
    };
    
    @Override
    public void onRelease(Point pos, boolean justified) {
        super.onRelease(pos, justified);
        if(base && nextCard != null)
            nextCard.onRelease(pos, justified);
    };
    
    /**
     * Drags a pile card recursively
     * @param pos the position of the mouse
     * @param justified if the drag is justified.
     */
    private void pileDrag(Point pos, boolean justified) {
        super.onDrag(pos,justified);
        if(nextCard != null)
            nextCard.pileDrag(pos, justified);
        
    }

    @Override
    public void setLayer(int layer) {
        super.setLayer(layer);
        if(nextCard != null)
            nextCard.setLayer(layer-1);
    };
    
    
    /* ==== Fields ==== */
    
    private boolean base;
    private GUIStackedCard nextCard;


}
