package com.tp.Model;

import com.tp.Exceptions.InvalidMoveException;

/**
 * Interface describing current state of game, if the game is started, whose turn it is
 */
public interface IGameState {
    /**
     * Get the player whose turn it is
     * @return - player whose turn it is
     */
    public Player getTurn();
    /**
     * Check if the player is allowed to make a move
     */
    public void verifyPlayer(Player player) throws InvalidMoveException;
    /**
     * Advance the game to the next turn
     */
    public void nextTurn();
}
