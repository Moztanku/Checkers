package com.tp.Model;

import com.tp.Exceptions.InvalidMoveException;

/**
 * Abstract class containing rules of checkers
 */
public abstract class MovementChecker {
    public void verifyMove(Move move, Board board) throws InvalidMoveException{
        insideBoundsCheck(move.before.X, move.before.Y, board.getSize());
        insideBoundsCheck(move.after.X, move.after.Y, board.getSize());
        jumpOverOwnPieceCheck(move);
        spaceOccupiedCheck(move, board);
        jumpedExistsCheck(move, board);
        jumpPossibleCheck(move, board);
        requiredJumpCheck(move, board);
        promotionCheck(move, board);
        MoveCheck(move);
    }

    protected void insideBoundsCheck(int x, int y, int boardSize) throws InvalidMoveException{
        if(x < 0 || x >= boardSize || y < 0 || y >= boardSize){
            throw new InvalidMoveException("Move out of bounds");
        }
    }

    protected void jumpOverOwnPieceCheck(Move move) throws InvalidMoveException{
        for(Piece piece : move.jumped){
            if(piece.color == move.before.color){
                throw new InvalidMoveException("Cannot jump over own piece");
            }
        }
    }

    protected void spaceOccupiedCheck(Move move, Board board) throws InvalidMoveException{
        if(board.getPiece(move.after.X, move.after.Y) != null){
            throw new InvalidMoveException("Space occupied");
        }
    }

    protected void jumpedExistsCheck(Move move, Board board) throws InvalidMoveException{
        for(Piece piece : move.jumped){
            if(board.getPiece(piece.X, piece.Y) == null){
                throw new InvalidMoveException("Jumped piece does not exist");
            }
        }
    }

    protected void jumpPossibleCheck(Move move, Board board) throws InvalidMoveException{
        if(!move.isJump){
            return;
        }
        if(move.jumped.length == 0){
            throw new InvalidMoveException("Jump of length 0 not possible");
        }
        Piece moved = new Piece(move.before);
        for(Piece piece : move.jumped){
            int xDiff = piece.X - moved.X;
            int yDiff = piece.Y - moved.Y;
            if(Math.abs(xDiff) != 1 || Math.abs(yDiff) != 1){
                throw new InvalidMoveException("Jump longer than 1 not possible");
            }
            moved.X += 2*xDiff;
            moved.Y += 2*yDiff;
            insideBoundsCheck(moved.X, moved.Y, board.getSize());
            if(board.getPiece(moved.X, moved.Y) != null){
                throw new InvalidMoveException("Jump to occupied space not possible");
            }
        }
        if(moved.X != move.after.X || moved.Y != move.after.Y){
            throw new InvalidMoveException("Jump not possible");
        }
    }

    protected void requiredJumpCheck(Move move, Board board) throws InvalidMoveException{
        if(move.isJump){
            return;
        }
        for(Piece piece : board.getPieces(move.before.color)){
            if(isJumpPossible(piece, board)){
                throw new InvalidMoveException("Jump required");
            }
        }
    }

    private boolean isJumpPossible(Piece piece, Board board){
        for(int x = -1; x <= 1; x += 2){
            for(int y = -1; y <= 1; y += 2){
                if(board.getPiece(piece.X + x, piece.Y + y) != null &&
                    board.getPiece(piece.X + x, piece.Y + y).color != piece.color && 
                    board.getPiece(piece.X + 2*x, piece.Y + 2*y) == null &&
                    piece.X + 2*x >= 0 && piece.X + 2*x < board.getSize() &&
                    piece.Y + 2*y >= 0 && piece.Y + 2*y < board.getSize()){
                    return true;
                }
            }
        }
        return false;
    }

    protected void promotionCheck(Move move, Board board) throws InvalidMoveException{
        if(move.before.isQueen  == false && move.after.isQueen == true){
            if(move.after.color == Player.WHITE && move.after.Y != board.getSize() - 1){
                throw new InvalidMoveException("Promotion not possible");
            }
            if(move.after.color == Player.BLACK && move.after.Y != 0){
                throw new InvalidMoveException("Promotion not possible");
            }
        }
    }

    protected void MoveCheck(Move move) throws InvalidMoveException{
        if(move.isJump){
            return;
        }
        if(move.before.isQueen == false){
            if(move.before.color == Player.WHITE && move.before.Y - move.after.Y != -1){
                throw new InvalidMoveException("Move back not possible");
            }
            if(move.before.color == Player.BLACK && move.before.Y - move.after.Y != 1){
                throw new InvalidMoveException("Move back not possible");
            }
        } else{
            if(Math.abs(move.before.Y - move.after.Y) != 1){
                throw new InvalidMoveException("Move longer than 1 not possible");
            }
        }

        if(Math.abs(move.before.X - move.after.X) != 1){
            throw new InvalidMoveException("Move longer than 1 not possible");
        }
    }
}