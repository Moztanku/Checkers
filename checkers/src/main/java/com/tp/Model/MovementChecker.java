package com.tp.Model;

import com.tp.Exceptions.InvalidMoveException;

/**
 * Abstract class containing rules of checkers
 */
public abstract class MovementChecker {
    public void verifyMove(Move move, Board board) throws InvalidMoveException{
        insideBounds(move.before.X, move.before.Y, board.getSize());
        insideBounds(move.after.X, move.after.Y, board.getSize());
        jumpOverOwnPiece(move);
        spaceOccupied(move, board);
        jumpedExists(move, board);
        jumpPossible(move, board);
        maxJumps(move, board);
    }

    protected void insideBounds(int x, int y, int boardSize) throws InvalidMoveException{
        if(x < 0 || x >= boardSize || y < 0 || y >= boardSize){
            throw new InvalidMoveException("Move out of bounds");
        }
    }

    protected void jumpOverOwnPiece(Move move) throws InvalidMoveException{
        for(Piece piece : move.jumped){
            if(piece.color == move.before.color){
                throw new InvalidMoveException("Cannot jump over own piece");
            }
        }
    }

    protected void spaceOccupied(Move move, Board board) throws InvalidMoveException{
        if(board.getPiece(move.after.X, move.after.Y) != null){
            throw new InvalidMoveException("Space occupied");
        }
    }

    protected void jumpedExists(Move move, Board board) throws InvalidMoveException{
        for(Piece piece : move.jumped){
            if(board.getPiece(piece.X, piece.Y) == null){
                throw new InvalidMoveException("Jumped piece does not exist");
            }
        }
    }

    protected void jumpPossible(Move move, Board board) throws InvalidMoveException{
        Piece p = move.before;
        for(Piece piece : move.jumped){
            
        }
    }

    protected void maxJumps(Move move, Board board) throws InvalidMoveException{
        /* TODO */
    }
}