package com.tp.CheckersVariants.English;

import com.tp.Exceptions.InvalidMoveException;
import com.tp.Model.Board;
import com.tp.Model.Move;
import com.tp.Model.Ruleset;
import com.tp.Model.Piece;
import com.tp.Model.Player;

/**
 * Ruleset for English variant of checkers
 */
public class EnglishRuleset extends Ruleset {
    /**
     * Pieces can be captured only by moving forward
     */
    @Override
    protected void requiredJumpCheck(Move move, Board board) throws InvalidMoveException {
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
        int dy = piece.color == Player.WHITE ? 1 : -1;

        for(int x = -1; x <= 1; x += 2){
            for(int y = -1; x <= 1; x += 2){
                if(!piece.isQueen && y != dy){
                    continue;
                }
                Piece jumped = board.getPiece(piece.X + x, piece.Y + y);
                if(jumped == null || jumped.color == piece.color){
                    continue;
                }
                if(board.getPiece(piece.X + 2*x, piece.Y + 2*y) == null &&
                    piece.X + 2*x >= 0 && piece.X + 2*x < board.getSize() &&
                    piece.Y + 2*y >= 0 && piece.Y + 2*y < board.getSize()){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * No requirement for maximum number of jumps
     */
    @Override
    protected void requiredMaxJumpCheck(Move move, Board board) throws InvalidMoveException {
        return;
    }

    @Override
    protected void jumpPossibleCheck(Move move, Board board) throws InvalidMoveException{
        if(!move.isJump){
            return;
        }
        if(move.jumped.length == 0){
            throw new InvalidMoveException("Jump of length 0 not possible");
        }
        Piece moved = new Piece(move.before);
        if(moved.isQueen)
            return;
        for(Piece piece : move.jumped){
            int dy = moved.color == Player.WHITE ? 1 : -1;
            int xDiff = piece.X - moved.X;
            int yDiff = piece.Y - moved.Y;
            if(yDiff != dy){
                throw new InvalidMoveException("Jump backwards not possible");
            }
            if(!moved.isQueen && (Math.abs(xDiff) != 1 || Math.abs(yDiff) != 1)){
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
}

