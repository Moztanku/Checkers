package com.tp.Model;

/**
 * Class describing one piece, or checker, or draught.
 */
public class Piece {
    Piece(int X,int Y,boolean isQueen, Color color){
        this.X = X;
        this.Y = Y;
        this.isQueen = isQueen;
        this.color = color;
    }

    enum Color{
        WHITE, BLACK
    }
    
    int X;
    int Y;
    boolean isQueen;
    Color color;
}
