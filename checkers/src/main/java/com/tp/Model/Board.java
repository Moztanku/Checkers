package com.tp.Model;

import java.util.ArrayList;

/**
 * Interface describing checkers board, its size, pieces etc.
 */
public abstract class Board {
    private ArrayList<Piece> pieces = new ArrayList<Piece>();

    public void addPiece(Piece piece) {
        this.pieces.add(piece);
    }
    
    public Piece getPiece(int x, int y) {
        for(Piece piece : this.pieces){
            if(piece.X == x && piece.Y == y){
                return piece;
            }
        }
        return null;
    }

    public ArrayList<Piece> getPieces() {
        return this.pieces;
    }
    public ArrayList<Piece> getPieces(Player color) {
        ArrayList<Piece> pieces = new ArrayList<Piece>();
        for(Piece piece : this.pieces){
            if(piece.color == color){
                pieces.add(piece);
            }
        }
        return pieces;
    }

    abstract public int getSize();

    public void makeMove(Move move) {
        this.pieces.remove(move.before);
        addPiece(move.after);

        if(move.isJump){
            for(Piece piece : move.jumped){
                this.pieces.remove(piece);
            }
        }
    }

    public int getPieceCount(Player color) {
        int count = 0;
        for(Piece piece : this.pieces){
            if(piece.color == color){
                count++;
            }
        }
        return count;
    }
}
