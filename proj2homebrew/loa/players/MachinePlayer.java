package loa.players;

import loa.Game.LogLevel;
import loa.Game;
import loa.Move;
import loa.Piece;
import loa.exceptions.InvalidMoveException;

public class MachinePlayer extends Player {

    public MachinePlayer(Piece team, double initScore) {
        super(team, initScore);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean inputExpected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public LogLevel verbose() {
        return Game.LogLevel.MOVES_AI;
    }

}
