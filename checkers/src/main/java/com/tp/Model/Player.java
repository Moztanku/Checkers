package com.tp.Model;

public enum Player{
    WHITE, BLACK;

    public Player getOpponent(){
        return this == WHITE ? BLACK : WHITE;
    }
}