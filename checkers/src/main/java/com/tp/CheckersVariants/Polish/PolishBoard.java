package com.tp.CheckersVariants.Polish;

import com.tp.Model.Board;
import com.tp.Model.Player;
import com.tp.Model.Piece;

public class PolishBoard extends Board {
    public PolishBoard(){
        for(int x = 0; x < 10; x+=2){
            this.addPiece(new Piece(x, 0, Player.WHITE));
            this.addPiece(new Piece(x+1, 1, Player.WHITE));
            this.addPiece(new Piece(x, 2, Player.WHITE));
            this.addPiece(new Piece(x+1, 3, Player.WHITE));

            this.addPiece(new Piece(x, 6, Player.BLACK));
            this.addPiece(new Piece(x+1, 7, Player.BLACK));
            this.addPiece(new Piece(x, 8, Player.BLACK));
            this.addPiece(new Piece(x+1, 9, Player.BLACK));
        }
    }

    public PolishBoard(Board board){
        for(Piece piece : board.getPieces()){
            this.addPiece(new Piece(piece.X, piece.Y, piece.color));
        }
    }

    @Override
    public int getSize() {
        return 10;
    }
    
}
