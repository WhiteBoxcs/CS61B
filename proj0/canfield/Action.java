/**
 * 
 */
package canfield;

import java.util.function.UnaryOperator;

/**
 * @author MadcowD
 *
 */
public class Action {
	UnaryOperator<Game> act;
	UnaryOperator<Game> undo;
	
	/**
	 * Initializes an action with respect to a game.
	 * @param game The game with which to initialize the action.
	 */
	public Action(UnaryOperator<Game> act, UnaryOperator<Game> undo){
		this.act = act;
		this.undo = undo;
	}
	
	/***
	 * Applies the action to the game.
	 * @param args The args required to apply the action.
	 */
	public void apply(Game game){
		act.apply(game);
	}
	
	/**
	 * Inverts a given action with respect to a game.
	 */
	public void inverseApply(Game game){
		undo.apply(game);
	}
}
