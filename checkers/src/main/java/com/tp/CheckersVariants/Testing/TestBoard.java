package com.tp.CheckersVariants.Testing;

import com.tp.Model.Board;
import com.tp.Model.Piece;
import com.tp.Model.Player;

public class TestBoard extends Board {
    public TestBoard(){
        this.addPiece(new Piece(1, 8, Player.WHITE));
        this.addPiece(new Piece(5, 4, Player.BLACK));
        this.addPiece(new Piece(0, 1, Player.BLACK));
    }

    public TestBoard(Board board){
        for(Piece piece : board.getPieces()){
            this.addPiece(new Piece(piece.X, piece.Y, piece.color));
        }
    }

    @Override
    public int getSize() {
        return 10;
    }
    
}
