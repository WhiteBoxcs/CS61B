package canfield;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        
    }
    
    
    /**
     * Paints the game.
     */
    @Override
    public synchronized void paintComponent(Graphics2D g) {
    	/*paint the background */
    	 Rectangle b = g.getClipBounds();
    	 g.drawImage(background,0,0,b.width,b.height,null);

        
    	 
        // Spaids Hearts Diamonds Clubs
    	 
    	 /* RESERVE */
    	 Card reserve = _game.topReserve();
    	 if(reserve != null)
    		 this.paintCard(g, reserve, RESERVE_POS);
    	 
    	 /* STOCK */
    	 if(!_game.stockEmpty())
    		 this.paintBack(g, STOCK_POS);
    	 
    	 /* WASTE */
    	 Card waste = _game.topWaste();
    	 if(waste != null)
    		 this.paintCard(g,waste, WASTE_POS);

    	 /* TABLEAU */
    	 for(int x = 1; x <= 4; x++){
    		 Point basis = cctp(-1+x,0);
    		 
    		 for(int i = 0; i < _game.tableauSize(x); i++){
    			 
    			 this.paintCard(g, _game.getTableau(x, i), 
    					 new Point((int)basis.getX(),
							 (int)basis.getY()+CARD_REVEAL*i) );
    		 }
    		 
    	 }
    	 
    	 /* FOUNDATION */
    	 for(int x = 1; x <= 4; x++){
    		 Card found = _game.topFoundation(x);
    		 if(found != null)
    			 this.paintCard(g, found, cctp(-1+x,-1));
    	 }
        
    }
    
    /**
     * Rebuilds the display using the given game.
     */
    public void rebuild(){
        cards.clear();
        tableau.clear();
        
        /* RESERVE */
        Card reserve = _game.topReserve();
        if(reserve != null)
            cards.add(
                new GUIMoveableCard(reserve,CardType.RESERVE,RESERVE_POS,0));

        
        /* STOCK */
        if(!_game.stockEmpty())
            cards.add(
                    new GUICard(Card.BACK, CardType.STOCK, STOCK_POS,0));
        
        /* WASTE */
        Card waste = _game.topWaste();
        if(waste != null)
            cards.add(new GUIMoveableCard(waste,CardType.WASTE,WASTE_POS,0));
        
        /* FOUNDATION */
        for(int x = 1; x <= 4; x++){
            Card found = _game.topFoundation(x);
            if(found != null)
                cards.add(
                        new GUICard(found, CardType.FOUNDATION, cctp(-1+x,-1),0));
        }
        
        
        /* TABLEAU */
        for(int x = 1; x <= 4; x++){
            /* PILE INFO */
            ArrayList<GUICard> tabPile = new ArrayList<GUICard>();
            tableau.add(tabPile);
            
            int tabSize = _game.tableauSize(x);
            
            if(tabSize <= 0)
                continue;
            
            Point basis = cctp(-1+x,0);
            Point top = cctp(-1 + x, 0 + tabSize*CARD_REVEAL/GUICard.HEIGHT);
            
            
            /* TOP CARD */
            GUIStackedCard head = new GUIStackedCard(
                    _game.topTableau(x),
                    CardType.TABLEAU_HEAD,
                    top,
                    true);
            tabPile.add(head);
            
            /* NORM/BASE CARDS */
            for(int i = _game.tableauSize(x) -2; i >= 0; i--){
                
                /* The calculated position of said card */
                Point cPos = new Point((int)basis.getX(),
                        (int)basis.getY()+CARD_REVEAL*i);
                
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
            
            cards.addAll(tableau.get(x-1));
            
        }

    }
    
    
    /* ======= HELPERS ====== */
    
    /**
     * Gets the card at a certain position.
     * @param pos The test position
     * @param except But this card.
     * @return The list of cards at a certain position.
     */
    public ArrayList<GUICard> getCardAt(Point pos, GUICard except){
        ArrayList<GUICard> satisfying = new ArrayList<GUICard>();
        
        for(GUICard card : cards)
            if(card.getBoundingBox().contains(pos) && card != except)
                satisfying.add(card);
        
        return satisfying;
    }
    
    /**
     * Gets the card at a certain position.
     * @param pos The test position
     * @return The list of cards at a certain position.
     */
    public ArrayList<GUICard> getCardAt(Point pos){
        return getCardAt(pos, null);
    }
    
    
    /**
     * Returns the tableau pile number of the given card
     * @param card The card to check
     * @return A pile number in 1 <= K <= 4. or -1 if not in any pile.
     */
    public int checkTableau(GUICard card){
        for(int i =0; i < 4; i++)
        {
            if(tableau.get(i).contains(card))
                return i+1;
        }
        
        return -1;
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
    private ArrayList<ArrayList<GUICard>> tableau;
    private final Image background;

}
