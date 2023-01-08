package com.tp.CheckersVariants.Italian;

import com.tp.Exceptions.InvalidMoveException;
import com.tp.Model.Board;
import com.tp.Model.Move;
import com.tp.Model.Ruleset;
import com.tp.Model.Piece;
import com.tp.Model.Player;

/**
 * Ruleset for Italian variant of checkers
 */
public class ItalianRuleset extends Ruleset {
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
        if(!piece.isQueen){
            if((piece.color == Player.WHITE && piece.Y == board.getSize()-1) ||
            (piece.color == Player.BLACK && piece.X == 0)){
                return false;
            }
        }

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
                    if(piece.isQueen || !jumped.isQueen)
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Italian checkers require the player to jump the maximum possible number of pieces
     */
    @Override
    protected void requiredMaxJumpCheck(Move move, Board board) throws InvalidMoveException {
        if(!move.isJump){
            return;
        }
        int maxJumpLength = 0;
        for(Piece piece : board.getPieces(move.before.color)){
            int jumpLength = maxJumps(piece, board, 0);
            if(jumpLength > maxJumpLength){
                maxJumpLength = jumpLength;
            }
        }
        
        if(move.jumped.length < maxJumpLength){
            throw new InvalidMoveException("Maximum jump required");
        } else if (move.jumped.length > maxJumpLength){
            throw new InvalidMoveException("Jump not possible");
        }
    }


    private int maxJumps(Piece piece, Board board, int jumps){
        if(!isJumpPossible(piece, board)){
            return jumps;
        }
        int dy = piece.color == Player.WHITE ? 1 : -1;
        int maxJumps = jumps;
        for(int x = -1; x <= 1; x += 2){
            for(int y = -1; x <= 1; x += 2){
                if(!piece.isQueen && y != dy){
                    continue;
                }
                Piece jumped = board.getPiece(piece.X + x, piece.Y + y);
                if(jumped == null || jumped.color == piece.color || (jumped.isQueen && !piece.isQueen)){
                    continue;
                }
                if(board.getPiece(piece.X + 2*x, piece.Y + 2*y) != null ||
                    outOfBounds(piece.X + 2*x, piece.Y + 2*y, board)){
                        continue;
                }
                Board newBoard = new ItalianBoard(board);
                Piece newPiece = new Piece(piece.X + 2*x, piece.Y + 2*y, piece.isQueen, piece.color);
                newBoard.makeMove(
                    new Move(
                        piece,
                        newPiece,
                        true,
                        new Piece[]{jumped}
                    )
                );
                int newJumps = maxJumps(newPiece, newBoard, jumps + 1);
                if(newJumps > maxJumps){
                    maxJumps = newJumps;
                }
            }
        }
        return maxJumps;
    }

    private boolean outOfBounds(int x, int y, Board board){
        return x < 0 || x >= board.getSize() || y < 0 || y >= board.getSize();
    };

    /**
     * Italian checkers cannot move backwards, and queen can move only one space diagonally
     */
    @Override
    protected void MoveCheck(Move move) throws InvalidMoveException {
        if(move.isJump){
            return;
        }
        int dy = move.before.color == Player.WHITE ? 1 : -1;
        if(!move.before.isQueen && move.after.Y - move.before.Y != dy)
            throw new InvalidMoveException("Move back not allowed");
        if(Math.abs(move.after.X - move.before.X) != 1 || Math.abs(move.after.Y - move.before.Y) != 1)
            throw new InvalidMoveException("Move must be one space diagonally");
    }
}

