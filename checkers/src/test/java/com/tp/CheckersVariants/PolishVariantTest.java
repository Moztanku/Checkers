package com.tp.CheckersVariants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tp.Checkers;
import com.tp.CheckersTest;
import com.tp.CheckersVariants.Polish.PolishChekersFactory;
import com.tp.Exceptions.InvalidMoveException;
import com.tp.GameStates.WhiteTurn;
import com.tp.Model.Board;
import com.tp.Model.Move;
import com.tp.Model.Piece;
import com.tp.Model.Player;

// write test for Polish variant of checkers

public class PolishVariantTest extends CheckersTest {
    @Test
    public void testInitialization(){
        Checkers checkers = getPolishCheckers();
        Board board = checkers.getBoard();

        for(int x = 0; x < 10; x+=2){
            assertPiece(board.getPiece(x, 0), x, 0, Player.WHITE);
            assertPiece(board.getPiece(x+1, 1), x+1, 1, Player.WHITE);
            assertPiece(board.getPiece(x, 2), x, 2, Player.WHITE);
            assertPiece(board.getPiece(x+1, 3), x+1, 3, Player.WHITE);

            assertPiece(board.getPiece(x, 6), x, 6, Player.BLACK);
            assertPiece(board.getPiece(x+1, 7), x+1, 7, Player.BLACK);
            assertPiece(board.getPiece(x, 8), x, 8, Player.BLACK);
            assertPiece(board.getPiece(x+1, 9), x+1, 9, Player.BLACK);
        }

        assertEquals(board.getPieces().size(), 40);
        assertEquals(board.getPieces(Player.WHITE).size(), 20);
        assertEquals(board.getPieceCount(Player.WHITE), 20);
        assertEquals(board.getPieces(Player.BLACK).size(), 20);
        assertEquals(board.getPieceCount(Player.BLACK), 20);

        assertTrue(checkers.getState() instanceof WhiteTurn);
    }

    @Test
    public void testCorrectMoves(){
        Checkers checkers = getPolishCheckers();
        Board board = checkers.getBoard();

        try{
            Move move = new Move(
                board.getPiece(1, 3),
                new Piece(0, 4, Player.WHITE)
            );
            checkers.move(
                move, Player.WHITE
            );

            checkers.move(
                new Move(
                    board.getPiece(2, 6),
                    new Piece(1, 5, Player.BLACK)
                ), Player.BLACK
            );

            checkers.move(
                new Move(
                    board.getPiece(0, 4),
                    new Piece(2, 6, Player.WHITE),
                    true,
                    new Piece[] { board.getPiece(1, 5) }
                ), Player.WHITE
            );
        } catch(Exception e){
            assertEquals(null, e);
        }

        assertEquals(20,board.getPieceCount(Player.WHITE));
        assertEquals( 19,board.getPieceCount(Player.BLACK));
    }

    @Test
    public void testOutOfBounds(){
        Checkers checkers = getPolishCheckers();
        Board board = checkers.getBoard();

        try{
            Move move = new Move(board.getPiece(0, 0), new Piece(-1,-1,Player.WHITE));
            checkers.move(move,Player.WHITE);
        } catch(InvalidMoveException e){
            assertEquals("Move out of bounds", e.getMessage());
        }
        try{
            Move move = new Move(new Piece(-1,-1,Player.WHITE), new Piece(0,0,Player.WHITE));
            checkers.move(move,Player.WHITE);
        } catch(InvalidMoveException e){
            assertEquals("Move out of bounds", e.getMessage());
        }
        try{
            Move move = new Move(board.getPiece(1,3), new Piece(0,4,Player.WHITE));
            checkers.move(move,Player.WHITE);
        } catch(InvalidMoveException e){
            assertEquals(null, e);
        }
        try{
            Move move = new Move(board.getPiece(1, 9), new Piece(0,10,Player.BLACK));
            checkers.move(move,Player.BLACK);
        } catch(InvalidMoveException e){
            assertEquals("Move out of bounds", e.getMessage());
        }
        makeMove(board, 8, 6, 9, 5);
        makeMove(board, 9, 3, 8, 4);
        makeMove(board, 8, 8, 0, 0);
        checkers.getState().nextTurn();
        try{
            Move move = new Move(
                board.getPiece(8,4),
                new Piece(8,8,Player.WHITE),
                true,
                new Piece[] { board.getPiece(9,5), board.getPiece(9,7) }
            );
            checkers.move(move,Player.WHITE);
        } catch(InvalidMoveException e){
            assertEquals("Move out of bounds", e.getMessage());
        }
    }

    @Test
    public void testJumpOverOwnPieces(){
        Checkers checkers = getPolishCheckers();
        Board board = checkers.getBoard();

        try{
            Move move = new Move(
                board.getPiece(0,2),
                new Piece(2,4,Player.WHITE),
                true,
                new Piece[] { board.getPiece(1,3) }
            );
            checkers.move(move,Player.WHITE);
        } catch(InvalidMoveException e){
            assertEquals("Cannot jump over own piece", e.getMessage());
        }
    }

    @Test
    public void testSpaceOccupied(){
        Checkers checkers = getPolishCheckers();
        Board board = checkers.getBoard();

        try{
            Move move = new Move(
                board.getPiece(0,2),
                new Piece(1,3,Player.WHITE)
            );
            checkers.move(move,Player.WHITE);
        } catch(InvalidMoveException e){
            assertEquals("Space occupied", e.getMessage());
        }
    }

    @Test
    public void testJumpedExists(){
        Checkers checkers = getPolishCheckers();
        Board board = checkers.getBoard();

        try{
            Move move = new Move(
                board.getPiece(1,3),
                new Piece(3,5,Player.WHITE),
                true,
                new Piece[] { new Piece(2,4,Player.BLACK) }
            );
            checkers.move(move,Player.WHITE);
        } catch(InvalidMoveException e){
            assertEquals("Jumped piece does not exist", e.getMessage());
        }
    }

    @Test
    public void testJumpPossible(){
        /* TODO */
    }

    @Test
    public void testJumpRequired(){
        /* TODO */
    }

    @Test
    public void testPromotion(){
        /* TODO */
    }

    @Test
    public void testInvalidMove(){
        /* TODO */
    }
}