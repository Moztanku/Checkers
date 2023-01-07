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
}

