package com.tp.CheckersVariants.Italian;

import com.tp.Model.Board;
import com.tp.Model.Piece;
import com.tp.Model.Player;

/*
 * Board for Italian variant of checkers
 */
public class ItalianBoard extends Board {
    public ItalianBoard(){
        for(int x = 0; x < 8; x+=2){
            this.addPiece(new Piece(x, 0, Player.WHITE));
            this.addPiece(new Piece(x+1, 1, Player.WHITE));
            this.addPiece(new Piece(x, 2, Player.WHITE));

            this.addPiece(new Piece(x+1, 5, Player.BLACK));
            this.addPiece(new Piece(x, 6, Player.BLACK));
            this.addPiece(new Piece(x+1, 7, Player.BLACK));
        }
    }

    public ItalianBoard(Board board){
        for(Piece piece : board.getPieces()){
            this.addPiece(new Piece(piece.X, piece.Y, piece.color));
        }
    }

    @Override
    public int getSize() {
        return 8;
    }
    
}
