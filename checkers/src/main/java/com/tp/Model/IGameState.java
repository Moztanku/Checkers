package com.tp.Model;

import com.tp.Exceptions.InvalidMoveException;

/**
 * Interface describing current state of game, if the game is started, whose turn it is
 */
public interface IGameState {
    public Player getTurn();
    public void verifyPlayer(Player player) throws InvalidMoveException;
    public void nextTurn();
}
