package com.tp.Model;

/**
 * Represents a player in the game.
 */
public enum Player{
    WHITE, BLACK;

    public Player getOpponent(){
        return this == WHITE ? BLACK : WHITE;
    }
}