package com.tp.CheckersVariants.English;

import com.tp.Model.Board;
import com.tp.Model.Piece;
import com.tp.Model.Player;

/*
 * Board for English variant of checkers
 */
public class EnglishBoard extends Board {
    public EnglishBoard(){
        for(int x = 0; x < 8; x+=2){
            this.addPiece(new Piece(x+1, 0, Player.WHITE));
            this.addPiece(new Piece(x, 1, Player.WHITE));
            this.addPiece(new Piece(x+1, 2, Player.WHITE));

            this.addPiece(new Piece(x, 5, Player.BLACK));
            this.addPiece(new Piece(x+1, 6, Player.BLACK));
            this.addPiece(new Piece(x, 7, Player.BLACK));
        }
    }

    public EnglishBoard(Board board){
        for(Piece piece : board.getPieces()){
            this.addPiece(new Piece(piece.X, piece.Y, piece.color));
        }
    }

    @Override
    public int getSize() {
        return 8;
    }
    
}
