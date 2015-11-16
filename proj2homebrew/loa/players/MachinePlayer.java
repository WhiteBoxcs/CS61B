package loa.players;

import loa.Game.LogLevel;

import java.util.ArrayList;

import loa.Board;
import loa.Game;
import loa.Move;
import loa.Piece;
import loa.exceptions.InvalidMoveException;

public class MachinePlayer extends Player {

    private Game _game;

    public MachinePlayer(Piece team, double initScore, Game container) {
        super(team, initScore);
        this._game = container;
    }

    @Override
    public boolean inputExpected() {
        return false;
    }

    @Override
    public LogLevel verbose() {
        return Game.LogLevel.MOVES_AI;
    }

    /**
     * Simply finds one of its pieces and then finds a possible move and does it.
     * @param input
     * @return
     */
    @Override
    public Move act(Move input) {
        Board b = _game.getBoard();
        for(int row = 1; b.inBounds(1,row); row++){
            for(int col = 1; b.inBounds(col,row); col++){
                if(b.get(row, col) == team()){
                    ArrayList<Move> possible = b.possibleMoves(row, col);
                    if(!possible.isEmpty())
                        return possible.get(0);
                }
            }
        }
        
        return null;
    }

}
