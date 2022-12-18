package com.tp.GameStates;

import com.tp.Checkers;
import com.tp.Exceptions.InvalidMoveException;
import com.tp.Model.IGameState;
import com.tp.Model.Player;

public class WhiteTurn implements IGameState {
    private Checkers checkers;

    public WhiteTurn(Checkers checkers) {
        this.checkers = checkers;
    }

    @Override
    public Player getTurn() {
        return Player.WHITE;
    }

    @Override
    public void nextTurn() {
        checkers.setState(new BlackTurn(checkers));
    }
    
    @Override
    public void verifyPlayer(Player player) throws InvalidMoveException {
        if(player != Player.WHITE){
            throw new InvalidMoveException("Not your turn");
        }
    }
}
