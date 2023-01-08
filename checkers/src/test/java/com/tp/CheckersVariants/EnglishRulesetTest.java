package com.tp.CheckersVariants;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tp.Checkers;
import com.tp.CheckersTest;
import com.tp.Model.Board;
import com.tp.Model.Move;
import com.tp.Model.Piece;
import com.tp.Model.Player;

public class EnglishRulesetTest extends CheckersTest {
    @Test
    public void testCaptureBackwards(){
        Checkers checkers = getEnglishCheckers();
        Board board = checkers.getBoard();

        emptyBoard(board);
        addPiece(board, new Piece(3, 3, Player.WHITE));
        addPiece(board, new Piece(4, 4, Player.BLACK));
        addPiece(board, new Piece(6, 4, Player.BLACK));
        boolean exceptionThrown = false;

        try{
            Move move = new Move(
                board.getPiece(3, 3),
                new Piece(7, 3, Player.WHITE),
                true,
                new Piece[]{board.getPiece(4, 4), board.getPiece(6, 4)}
            );
            checkers.move(move, Player.WHITE);
        } catch(Exception e){
            exceptionThrown = true;
            assertEquals("Jump backwards not possible", e.getMessage());
        }

        assertEquals(true, exceptionThrown);
        
        try{
            Move move = new Move(
                board.getPiece(3, 3),
                new Piece(5, 5, Player.WHITE),
                true,
                new Piece[]{board.getPiece(4, 4)}
            );
            checkers.move(move, Player.WHITE);
        } catch(Exception e){
            assertEquals(null, e);
        }
    }

}