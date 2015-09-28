package canfield;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
    
    private static final int CARD_REVEAL = 22;
    
    /** The positions of cards **/
    private static final Point RESERVE_POS = cctp(-3,0.25);
	private static final Point STOCK_POS = cctp(-3,1.4);
	private static final Point WASTE_POS = cctp(-1.75,1.4);

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
    	 
    	 /* Print cards from highest layer to lowest */
    	 for(int i = 0; i < cards.size(); i++)
    	     this.paintCard(g,cards.get(i));
    }
    
    /* =======  REBUILD ======= */
    
    /**
     * Rebuilds the display using the given game.
     */
    public void rebuild(){
        cards.clear();
        
        /* rebuild different card types */
        rebuildReserve();
        rebuildStock();
        rebuildWaste();
        rebuildFoundation();
        rebuildTableau();
        
        /* sort cards */
        cards.sort(new GUICard.LayerComparator());
        
        

    }
    
    /**
     * Rebuilds the reserve.
     */
    private void rebuildReserve(){
        Card reserve = _game.topReserve();
        if(reserve != null){
            ArrayList<GUIStackedCard> reserveStack = new ArrayList<GUIStackedCard>();
            
            Point rTop = new Point(
                    (int)RESERVE_POS.getX()+(_game.reserveSize()-1)*13,
                    (int)RESERVE_POS.getY());
            
            reserveStack.add(
                new GUIStackedCard(reserve,CardType.RESERVE,rTop,true)) ;
        
            /* bottom reserve cards */
            for(int i =1; i < _game.reserveSize(); i++){
                Point wPos = new Point((int)rTop.getX()-i*13,
                        (int)rTop.getY());
                
                GUIStackedCard resCard = new GUIStackedCard(_game.getReserve(i),
                        CardType.RESERVE,
                        wPos,reserveStack.get(reserveStack.size()-1),
                        false);
                resCard.flip();
                
                reserveStack.add(resCard);
            }
            
            cards.addAll(reserveStack);
        }
    }
    
    /**
     * Rebuilds the stock.
     */
    private void rebuildStock(){
        if(!_game.stockEmpty()){
            GUICard stock = new GUICard(Card.C10, CardType.STOCK, STOCK_POS,0);
            stock.flip();
            cards.add(stock);
        }
        else
            cards.add(new GUIEmptyCard(CardType.STOCK,STOCK_POS));

    }
    
    /**
     * Rebuilds the waste.
     */
    private void rebuildWaste(){
        Card waste = _game.topWaste();
        if(waste != null){
            ArrayList<GUIStackedCard> wasteStack = new ArrayList<GUIStackedCard>();
            wasteStack.add(new GUIStackedCard(waste,CardType.WASTE,WASTE_POS,true));
            /* bottom waste cards */
            for(int i =1; i < Math.min(_game.wasteSize(), 3); i++){
                Point wPos = new Point((int)WASTE_POS.getX()-i*13,
                        (int)WASTE_POS.getY());
                
                wasteStack.add(new GUIStackedCard(_game.getWaste(i),
                        CardType.WASTE,
                        wPos,wasteStack.get(wasteStack.size()-1),
                        false));
            }
            
            cards.addAll(wasteStack);
        }
        
        
    }
    
    /**
     * Rebuilds the foundation.
     */
    private void rebuildFoundation(){
        for(int x = 1; x <= Card.NUM_SUITS; x++){
            Card found = _game.topFoundation(x);
            if(found != null)
                cards.add(
                        new GUIMoveableCard(found, CardType.FOUNDATION, cctp(-1+x,-1),0));
            else
                cards.add(new GUIEmptyCard(CardType.FOUNDATION,cctp(-1+x,-1)));
        }
        
    }
    
    /**
     * Rebuilds the tableau
     */
    private void rebuildTableau(){
        /* TABLEAU */
        for(int x = 1; x <= Game.TABLEAU_SIZE; x++){
            /* PILE INFO */
            ArrayList<GUIStackedCard> tabPile = new ArrayList<GUIStackedCard>();
            
            int tabSize = _game.tableauSize(x);

            Point basis = cctp(-1+x,0);
            Point top = cctp(-1 + x, 0 + (tabSize)*((double)CARD_REVEAL/(double)GUICard.HEIGHT));

            
            /* IF THERE IS AN EMPTY TAB */
            if(tabSize <= 0)
            {
                cards.add(new GUIEmptyCard(CardType.TABLEAU_EMPTY,basis));
                continue;
            }
            
                        
            
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
            for(int i = 1 ; i <= _game.tableauSize(x)-1; i++){
                
                /* The calculated position of said card */
                Point cPos = new Point((int)top.getX(),
                        (int)top.getY()-CARD_REVEAL*(i));
                
                /* Add the card to the tab pile */
                boolean base = i == _game.tableauSize(x)-1;
                tabPile.add(
                    new GUIStackedCard( 
                        _game.getTableau(x, i),
                        base ? CardType.TABLEAU_BASE
                            : CardType.TABLEAU_NORM,
                        cPos,
                        tabPile.get(tabPile.size()-1),
                        base
                ));
            }
            if(!tabPile.isEmpty())
                cards.addAll(tabPile);
            
        }
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
        if(!satisfying.isEmpty())
            return satisfying.get(satisfying.size() -1);
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
    public ArrayList<GUICard> getCollision(GUICard with){
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

        return satisfying;
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
