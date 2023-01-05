package com.tp.GameStates;

import com.tp.Checkers;
import com.tp.Exceptions.InvalidMoveException;
import com.tp.Model.IGameState;
import com.tp.Model.Player;

public class GameEnded implements IGameState {
    Checkers checkers;
    Player winner;

    public GameEnded(Checkers checkers, Player winner) {
        this.checkers = checkers;
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }

    @Override
    public Player getTurn() {
        return null;
    }

    @Override
    public void nextTurn() {
    }

    @Override
    public void verifyPlayer(Player player) throws InvalidMoveException {
        throw new InvalidMoveException("Game ended");
    }
    
}
