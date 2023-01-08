package com.tp;

import static org.junit.Assert.assertEquals;

import com.tp.CheckersVariants.English.EnglishCheckersFactory;
import com.tp.CheckersVariants.Italian.ItalianCheckersFactory;
import com.tp.CheckersVariants.Polish.PolishChekersFactory;
import com.tp.Model.Board;
import com.tp.Model.Move;
import com.tp.Model.Piece;
import com.tp.Model.Player;

public abstract class CheckersTest {
    protected void assertPiece(Piece piece, int x, int y, Player player){
        assertEquals(x, piece.X);
        assertEquals(y, piece.Y);
        assertEquals(player, piece.color);
    }
    protected void makeMove(Board board, int x1, int y1, int x2, int y2){
        Move move = new Move(
            board.getPiece(x1, y1),
            new Piece(x2, y2, board.getPiece(x1, y1).color)
        );
        board.makeMove(move);
    }

    protected Checkers getPolishCheckers(){
        Checkers.createInstance(new PolishChekersFactory());
        return Checkers.getInstance();
    }
    protected Checkers getEnglishCheckers(){
        Checkers.createInstance(new EnglishCheckersFactory());
        return Checkers.getInstance();
    }
    protected Checkers getItalianCheckers(){
        Checkers.createInstance(new ItalianCheckersFactory());
        return Checkers.getInstance();
    }

    protected void emptyBoard(Board board){
        board.getPieces().clear();
    }

    protected void addPiece(Board board, Piece piece){
        board.getPieces().add(piece);
    }
}
