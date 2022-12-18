package com.tp.GameStates;

import com.tp.Checkers;
import com.tp.Exceptions.InvalidMoveException;
import com.tp.Model.IGameState;
import com.tp.Model.Player;

public class BlackTurn implements IGameState {
    private Checkers checkers;

    public BlackTurn(Checkers checkers) {
        this.checkers = checkers;
    }

    @Override
    public Player getTurn() {
        return Player.BLACK;
    }

    @Override
    public void nextTurn() {
        checkers.setState(new WhiteTurn(checkers));
    }

    @Override
    public void verifyPlayer(Player player) throws InvalidMoveException {
        if(player != Player.BLACK){
            throw new InvalidMoveException("Not your turn");
        }
    }
}
