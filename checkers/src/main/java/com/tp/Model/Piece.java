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
    public Piece(Piece piece){
        this(piece.X, piece.Y, piece.isQueen, piece.color);
    }

    public int X;
    public int Y;
    public boolean isQueen;
    public Player color;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Piece){
            Piece piece = (Piece) obj;
            return piece.X == this.X && piece.Y == this.Y && piece.color == this.color && piece.isQueen == this.isQueen;
        }
        return false;
    }
}
