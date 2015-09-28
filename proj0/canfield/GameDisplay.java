package canfield;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;

import javax.imageio.ImageIO;

import ucb.gui.Pad;

/**
 * A widget that displays a Pinball playfield.
 *
 * @author P. N. Hilfinger
 */
class GameDisplay extends Pad {

    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    /* Coordinates and lengths in pixels unless otherwise stated. */

    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 800, BOARD_HEIGHT = 600;
    private static final Point ORIGIN = new Point(BOARD_WIDTH/2, BOARD_HEIGHT/2);
    
    private static final int CARD_REVEAL = 30;
    
    /** The positions of cards **/
    private static final Point RESERVE_POS = cctp(-3,0);
	private static final Point STOCK_POS = cctp(-3,1);
	private static final Point WASTE_POS = cctp(-2,1);

    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        this._game = game;
        this.setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
        background = this.getImage("bg.jpg");
        
        rebuild();
        
    }
    
    
    /**
     * Paints the game.
     */
    @Override
    public synchronized void paintComponent(Graphics2D g) {
    	/*paint the background */
        cards.sort(new GUICard.LayerComparator());
    	 Rectangle b = g.getClipBounds();
    	 g.drawImage(background,0,0,b.width,b.height,null);
    	 
    	 for(GUICard card : cards)
    	     this.paintCard(g, card);
    }
    
    /**
     * Rebuilds the display using the given game.
     */
    public void rebuild(){
        cards.clear();
        
        /* RESERVE */
        Card reserve = _game.topReserve();
        if(reserve != null)
            cards.add(
                new GUIMoveableCard(reserve,CardType.RESERVE,RESERVE_POS,0));

        
        /* STOCK */
        if(!_game.stockEmpty()){
            GUICard stock = new GUICard(Card.C10, CardType.STOCK, STOCK_POS,0);
            stock.flip();
            cards.add(stock);
        }
        else
            cards.add(new GUIEmptyCard(CardType.STOCK,STOCK_POS));
            
        
        /* WASTE */
        Card waste = _game.topWaste();
        if(waste != null)
            cards.add(new GUIMoveableCard(waste,CardType.WASTE,WASTE_POS,0));
        
        /* FOUNDATION */
        for(int x = 1; x <= 4; x++){
            Card found = _game.topFoundation(x);
            if(found != null)
                cards.add(
                        new GUIMoveableCard(found, CardType.FOUNDATION, cctp(-1+x,-1),0));
            else
                cards.add(new GUIEmptyCard(CardType.FOUNDATION,cctp(-1+x,-1)));
        }
        
        
        /* TABLEAU */
        for(int x = 1; x <= 4; x++){
            /* PILE INFO */
            ArrayList<GUICard> tabPile = new ArrayList<GUICard>();
            
            int tabSize = _game.tableauSize(x);
            
            if(tabSize <= 0)
                continue;
            
            Point basis = cctp(-1+x,0);
            Point top = cctp(-1 + x, 0 + (tabSize)*((double)CARD_REVEAL/(double)GUICard.HEIGHT));
            
            
            /* TOP CARD */
            GUIStackedCard head = new GUIStackedCard(
                    _game.topTableau(x),
                    _game.tableauSize(x) > 1?
                            CardType.TABLEAU_HEAD :
                            CardType.TABLEAU_BASE,
                    top,
                    true);
            tabPile.add(head);
            
            /* NORM/BASE CARDS */
            for(int i = _game.tableauSize(x)-1 ; i >= 1; i--){
                
                /* The calculated position of said card */
                Point cPos = new Point((int)basis.getX(),
                        (int)basis.getY()+CARD_REVEAL*(i));
                
                /* Add the card to the tab pile */
                boolean base = i == 0;
                tabPile.add(
                    new GUIStackedCard(
                        _game.getTableau(x, i),
                        base ? CardType.TABLEAU_BASE
                            : CardType.TABLEAU_NORM,
                        cPos,
                        base
                ));
            }
            if(!tabPile.isEmpty())
                cards.addAll(tabPile);
            
        }
        
        /* sort cards */
        cards.sort(new GUICard.LayerComparator());

    }
    
    
    /* ======= HELPERS ====== */
    
    /**
     * Gets the card at a certain position.
     * @param pos The test position
     * @param except But this card.
     * @return The list of cards at a certain position sorted by layer.
     */
    public ArrayList<GUICard> getCardAt(Point pos, GUICard except){
        ArrayList<GUICard> satisfying = new ArrayList<GUICard>();
        
        for(GUICard card : cards)
            if(card.getBoundingBox().contains(pos) && card != except)
                satisfying.add(card);
        
        satisfying.sort(new GUICard.LayerComparator());
        
        return satisfying;
    }
    
    /**
     * Gets the card at a certain position.
     * @param pos The test position
     * @return The list of cards at a certain position sorted by layer.
     */
    public ArrayList<GUICard> getCardAt(Point pos){
        return getCardAt(pos, null);
    }
    
    /**
     * Gets the top card at a given position.
     * @param pos The POS to check.
     * @param except but this card.
     * @return the top card or NULL if there is no card.
     */
    public GUICard getTopCardAt(Point pos, GUICard except){
        ArrayList<GUICard> satisfying = getCardAt(pos,except);
        if(satisfying.size() != 0)
            return satisfying.get(0);
        else
            return null;
    }
    
    /**
     * Gets the top card at a given position.
     * @param pos The POS to check.
     * @return the top card or NULL if there is no card.
     */
    public GUICard getTopCardAt(Point pos){
        return getTopCardAt(pos,null);
    }
    
    /**
     * Gets the top card colliding with a given GUI card
     * @param with The card with which another card may collide.
     * @return the top card colliding with WITH
     */
    public GUICard getCollision(GUICard with){
        ArrayList<GUICard> satisfying = new ArrayList<GUICard>();
        
        for(GUICard card : cards)
            if(card.getBoundingBox().intersects(with.getBoundingBox()) && card != with)
                satisfying.add(card);
        satisfying.sort(new Comparator<GUICard>(){

            @Override
            public int compare(GUICard o1, GUICard o2) {
                double px = with.getCenter().getX();
                double py = with.getCenter().getY();
                return  (int) o2.getPos().distance(px, py)
                        - (int) o1.getPos().distance(px,py);
            }
            
        });
        if(satisfying.isEmpty())
            return null;
        else
            return satisfying.get(satisfying.size()-1);
    }
    
    
    public static Image getImage(String name) {
        InputStream in = GameDisplay.class.getResourceAsStream(
                "/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    
    /** Draw CARD at P on G. */
    private void paintCard(Graphics2D g, GUICard card) {
        g.drawImage(card.getImage(),
                (int)card.getPos().getX(),
                (int)card.getPos().getY(),
                (int)card.getBoundingBox().getWidth(),
                (int)card.getBoundingBox().getHeight(),
                null);
    }
    
    
    /* ================ Positional attributes ================ */
       
    
    /**
     * Converts card coords to pixels
     * @param x the grid position in X of a given card
     * @param y the grid position in Y of a given card
     * @return the point where the cards pos is.
     */
    public static Point cctp(double x,double y){
    	int paddedWidth = GUICard.WIDTH + GUICard.PADDING;
    	int paddedHeight = GUICard.HEIGHT + GUICard.PADDING;
    	
    	return new Point((int)((x - 0.5)*paddedWidth)+(int)ORIGIN.getX(),
    			(int)((y-0.5)*paddedHeight) + (int)ORIGIN.getY());
    }
    
    

    /** Game I am displaying. */
    private final Game _game;
    private ArrayList<GUICard> cards = new ArrayList<GUICard>();
    private final Image background;

}
