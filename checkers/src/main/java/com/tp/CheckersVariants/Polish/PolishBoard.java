package com.tp.CheckersVariants.Polish;

import com.tp.Model.Board;
import com.tp.Model.Player;
import com.tp.Model.Piece;

/**
 * Board for Polish checkers
 */
public class PolishBoard extends Board {
    public PolishBoard(){
        for(int x = 0; x < 10; x+=2){
            this.addPiece(new Piece(x+1, 0, Player.WHITE));
            this.addPiece(new Piece(x, 1, Player.WHITE));
            this.addPiece(new Piece(x+1, 2, Player.WHITE));
            this.addPiece(new Piece(x, 3, Player.WHITE));

            this.addPiece(new Piece(x+1, 6, Player.BLACK));
            this.addPiece(new Piece(x, 7, Player.BLACK));
            this.addPiece(new Piece(x+1, 8, Player.BLACK));
            this.addPiece(new Piece(x, 9, Player.BLACK));
        }
    }

    public PolishBoard(Board board){
        for(Piece piece : board.getPieces()){
            this.addPiece(new Piece(piece.X, piece.Y,piece.isQueen, piece.color));
        }
    }

    @Override
    public int getSize() {
        return 10;
    }
    
}
