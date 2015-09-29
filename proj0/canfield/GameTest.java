/**
 * @author MadcowD
 */
package canfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author MadcowD
 */
public class GameTest {
    /** Example. */
    @Test
    public void testInitialScore() {
        Game g = this.setUpGame();
        assertEquals(5, g.getScore());
    }

    /**
     * Test method for {@link canfield.Game#stockEmpty()}.
     */
    @Test
    public void testStockEmpty() {
        Game g = this.setUpGame(200);
        int deckSize = Card.NUM_SUITS * Card.NUM_RANKS;
        int stockSize = deckSize - Game.TABLEAU_SIZE - Game.RESERVE_SIZE - 1;
        for (int i = 0; i < stockSize / 3 + 1; i++) {
            g.stockToWaste();
        }
        assertTrue(g.stockEmpty());
    }

    /**
     * Test method for {@link canfield.Game#topWaste()}.
     */
    @Test
    public void testTopWaste() {
        Game g = this.setUpGame(200);
        assertEquals(g.topWaste(), null);
        g.stockToWaste();
        assertEquals(g.topWaste(), Card.DA);
    }

    /**
     * Test method for {@link canfield.Game#topReserve()}.
     */
    @Test
    public void testTopReserve() {
        Game g = this.setUpGame(200);
        assertEquals(g.topReserve(), Card.D5);
    }

    /**
     * Test method for {@link canfield.Game#getTableau(int, int)}.
     */
    @Test
    public void testGetTableau() {
        Game g = this.setUpGame(200);

        assertEquals(g.getTableau(1, 0), Card.C8);

        assertEquals(g.getTableau(2, 0), Card.S4);

        assertEquals(g.getTableau(3, 0), Card.S6);

        assertEquals(g.getTableau(4, 0), Card.S2);

        g.reserveToTableau(3);

        assertEquals(g.getTableau(3, 1), Card.S6);
        assertEquals(g.getTableau(3, 0), Card.D5);

    }

    /**
     * Test method for {@link canfield.Game#topTableau(int)}.
     */
    @Test
    public void testTopTableau() {
        Game g = this.setUpGame(200);

        assertEquals(g.topTableau(1), Card.C8);

        assertEquals(g.topTableau(2), Card.S4);

        assertEquals(g.topTableau(3), Card.S6);

        assertEquals(g.topTableau(4), Card.S2);
    }

    /**
     * Test method for {@link canfield.Game#tableauSize(int)}.
     */
    @Test
    public void testTableauSize() {
        Game g = this.setUpGame(200);

        assertEquals(g.tableauSize(1), 1);
        assertEquals(g.tableauSize(2), 1);
        assertEquals(g.tableauSize(3), 1);
        assertEquals(g.tableauSize(4), 1);

        g.reserveToTableau(3);

        assertEquals(g.tableauSize(3), 2);
    }

    /**
     * Test method for {@link canfield.Game#foundationSize(int)}.
     */
    @Test
    public void testFoundationSize() {
        Game g = this.setUpGame(200);
        assertEquals(g.foundationSize(1), 1);
        assertEquals(g.foundationSize(2), 0);
        assertEquals(g.foundationSize(3), 0);
        assertEquals(g.foundationSize(4), 0);
    }

    /**
     * Test method for {@link canfield.Game#topFoundation(int)}.
     */
    @Test
    public void testTopFoundation() {
        Game g = this.setUpGame(200);

        assertEquals(g.topFoundation(1), Card.H2);

        assertEquals(g.topFoundation(2), null);

        assertEquals(g.topFoundation(3), null);

        assertEquals(g.topFoundation(4), null);
    }

    /**
     * Test method for {@link canfield.Game#foundationPileOf(canfield.Card)}.
     */
    @Test
    public void testFoundationPileOf() {
        Game g = this.setUpGame(200);

        assertEquals(g.foundationPileOf(Card.H2), 1);

        assertEquals(g.foundationPileOf(Card.H10), -1);
        assertEquals(g.foundationPileOf(Card.H3), -1);
        assertEquals(g.foundationPileOf(Card.D3), -1);

        g.tableauToFoundation(4);
        assertEquals(g.foundationPileOf(Card.S2), 2);
    }

    /**
     * Test method for {@link canfield.Game#tableauPileOf(canfield.Card)}.
     */
    @Test
    public void testTableauPileOf() {
        Game g = this.setUpGame(200);

        assertEquals(g.tableauPileOf(Card.C8), 1);
        assertEquals(g.tableauPileOf(Card.S4), 2);
        assertEquals(g.tableauPileOf(Card.S6), 3);
        assertEquals(g.tableauPileOf(Card.S2), 4);

        g.reserveToTableau(3);
        assertEquals(g.tableauPileOf(Card.C8), 1);
        assertEquals(g.tableauPileOf(Card.S4), 2);
        assertEquals(g.tableauPileOf(Card.S6), 3);
        assertEquals(g.tableauPileOf(Card.S2), 4);

        g.tableauToFoundation(4);
        assertEquals(g.tableauPileOf(Card.C8), 1);
        assertEquals(g.tableauPileOf(Card.S4), 2);
        assertEquals(g.tableauPileOf(Card.S6), 3);
        assertEquals(g.tableauPileOf(Card.S2), -1);
        assertEquals(g.tableauPileOf(Card.D5), 3);
        assertEquals(g.tableauPileOf(Card.D10), 4);

    }

    /**
     * Test method for {@link canfield.Game#getWaste(int)}.
     */
    @Test
    public void testGetWaste() {
        Game g = this.setUpGame(200);
        g.stockToWaste();
        assertEquals(g.getWaste(0), Card.DA);
        assertEquals(g.getWaste(1), Card.HQ);
        assertEquals(g.getWaste(2), Card.HJ);

        g.stockToWaste();
        assertEquals(g.getWaste(0), Card.S10);
        assertEquals(g.getWaste(1), Card.D3);
        assertEquals(g.getWaste(2), Card.H6);
        assertEquals(g.getWaste(3), Card.DA);
        assertEquals(g.getWaste(4), Card.HQ);
        assertEquals(g.getWaste(5), Card.HJ);
    }

    /**
     * Test method for {@link canfield.Game#getReserve(int)}.
     */
    @Test
    public void testGetReserve() {
        Game g = this.setUpGame(200);
        assertEquals(g.getReserve(0), Card.D5);
        assertEquals(g.getReserve(1), Card.D10);
    }

    /**
     * Test method for {@link canfield.Game#reserveSize()}.
     */
    @Test
    public void testReserveSize() {
        Game g = this.setUpGame(200);
        assertEquals(g.reserveSize(), Game.RESERVE_SIZE);
        g.reserveToTableau(3);
        assertEquals(g.reserveSize(), Game.RESERVE_SIZE - 1);
        g.tableauToFoundation(4);
        assertEquals(g.reserveSize(), Game.RESERVE_SIZE - 2);
    }

    /**
     * Test method for {@link canfield.Game#apply(canfield.actions.Action)}.
     */
    @Test
    public void testApply() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#undo()}.
     */
    @Test
    public void testUndo() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#stockToWaste()}.
     */
    @Test
    public void testStockToWaste() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#wasteToFoundation()}.
     */
    @Test
    public void testWasteToFoundation() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#reserveToFoundation()}.
     */
    @Test
    public void testReserveToFoundation() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#tableauToFoundation(int)}.
     */
    @Test
    public void testTableauToFoundation() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#tableauToTableau(int, int)}.
     */
    @Test
    public void testTableauToTableau() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#foundationToTableau(int, int)}.
     */
    @Test
    public void testFoundationToTableau() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#wasteToTableau(int)}.
     */
    @Test
    public void testWasteToTableau() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link canfield.Game#reserveToTableau(int)}.
     */
    @Test
    public void testReserveToTableau() {
        fail("Not yet implemented"); // TODO
    }

    /**
     * @param seed
     *            the SEED for the game.
     * @return A made game with SEED.
     */
    private Game setUpGame(long seed) {
        Game g = new Game();
        g.seed(seed);
        g.deal();
        return g;
    }

    /**
     * @return a set up game.
     */
    private Game setUpGame() {
        Game g = new Game();
        g.deal();
        return g;
    }

}
