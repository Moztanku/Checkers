package com.tp.Model;

/**
 * Class describing one piece, or checker, or draught.
 */
public class Piece {
    public Piece(int X,int Y,boolean isQueen, Player color){
        this.X = X;
        this.Y = Y;
        this.isQueen = isQueen;
        this.color = color;
    }
    public Piece(int X, int Y, Player color){
        this(X, Y, false, color);
    }

    public int X;
    public int Y;
    public boolean isQueen;
    public Player color;
}
