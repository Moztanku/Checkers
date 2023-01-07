package com.tp.Model;

import com.tp.CheckersVariants.Polish.PolishBoard;
import com.tp.Exceptions.InvalidMoveException;

/**
 * Abstract class containing rules of checkers
 */
public abstract class Ruleset {
    /**
     * Verifies that a move is valid
     * @param move - Move to verify
     * @param board - Board to verify move on
     * @throws InvalidMoveException - If move is invalid
     */
    public void verifyMove(Move move, Board board) throws InvalidMoveException{
        insideBoundsCheck(move.before.X, move.before.Y, board.getSize());
        insideBoundsCheck(move.after.X, move.after.Y, board.getSize());
        jumpOverOwnPieceCheck(move);
        spaceOccupiedCheck(move, board);
        jumpedExistsCheck(move, board);
        jumpPossibleCheck(move, board);
        requiredJumpCheck(move, board);
        requiredMaxJumpCheck(move, board);
        promotionCheck(move, board);
        MoveCheck(move);
    }
    /**
     * Check if x and y are inside the bounds of the board
     */
    protected void insideBoundsCheck(int x, int y, int boardSize) throws InvalidMoveException{
        if(x < 0 || x >= boardSize || y < 0 || y >= boardSize){
            throw new InvalidMoveException("Move out of bounds");
        }
    }
    /**
     * Check if a move is jumping over own piece
     */
    protected void jumpOverOwnPieceCheck(Move move) throws InvalidMoveException{
        for(Piece piece : move.jumped){
            if(piece.color == move.before.color){
                throw new InvalidMoveException("Cannot jump over own piece");
            }
        }
    }
    /**
     * Check if a move is to a space occupied by a piece
     */
    protected void spaceOccupiedCheck(Move move, Board board) throws InvalidMoveException{
        if(board.getPiece(move.after.X, move.after.Y) != null){
            throw new InvalidMoveException("Space occupied");
        }
    }
    /**
     * Check if a move is jumping over a piece that exists
     */
    protected void jumpedExistsCheck(Move move, Board board) throws InvalidMoveException{
        for(Piece piece : move.jumped){
            if(board.getPiece(piece.X, piece.Y) == null){
                throw new InvalidMoveException("Jumped piece does not exist");
            }
        }
    }
    /**
     * Check if a move is possible
     */
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
    /**
     * Check if a jump is required
     */
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
    /**
     * Check if jump is possible for a piece
     */
    private boolean isJumpPossible(Piece piece, Board board){
        for(int x = -1; x <= 1; x += 2){
            for(int y = -1; y <= 1; y += 2){
                int diagonal = 1;
                do{
                    Piece jumped = board.getPiece(piece.X + diagonal*x, piece.Y + diagonal*y);
                    if(jumped == null){
                        continue;
                    }
                    if(jumped.color == piece.color ||
                        board.getPiece(piece.X + (diagonal+1)*x, piece.Y + (diagonal+1)*y) != null ||
                        piece.X + (diagonal+1)*x < 0 || piece.X + (diagonal+1)*x >= board.getSize() ||
                        piece.Y + (diagonal+1)*y < 0 || piece.Y + (diagonal+1)*y >= board.getSize()
                    ){
                        break;
                    }
                    return true;
                } while( piece.isQueen && ++diagonal < board.getSize() );
            }
        }
        return false;
    }
    /**
     * Check if jump is maximum possible
     */
    protected void requiredMaxJumpCheck(Move move, Board board) throws InvalidMoveException{
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
        if(maxJumpLength != move.jumped.length){
            throw new InvalidMoveException("Max jump required");
        }
    }
    /**
     * Calculate maximum possible jump length for a piece
     */
    private int maxJumps(Piece piece, Board board, int jumps){
        if(!isJumpPossible(piece, board)){
            return jumps;
        }
        int maxJumps = jumps;
        for(int x = -1; x <= 1; x += 2){
            for(int y = -1; y <= 1; y += 2){
                int diagonal = 1;
                do{
                    Piece jumped = board.getPiece(piece.X + diagonal*x, piece.Y + diagonal*y);
                    if(jumped == null){
                        continue;
                    }
                    if(jumped.color == piece.color ||
                        board.getPiece(piece.X + (diagonal+1)*x, piece.Y + (diagonal+1)*y) != null ||
                        piece.X + (diagonal+1)*x < 0 || piece.X + (diagonal+1)*x >= board.getSize() ||
                        piece.Y + (diagonal+1)*y < 0 || piece.Y + (diagonal+1)*y >= board.getSize()
                    ){
                        break;
                    }
                    Piece moved = new Piece(
                        piece.X + (diagonal+1)*x,
                        piece.Y + (diagonal+1)*y,
                        piece.isQueen,
                        piece.color
                    );
                    Board newBoard = new PolishBoard(board);
                    newBoard.makeMove(new Move(piece, moved, true, new Piece[]{jumped}));

                    int jumpLength = maxJumps(moved, newBoard, jumps + 1);
                    if(jumpLength > maxJumps){
                        maxJumps = jumpLength;
                    }
                } while( piece.isQueen && ++diagonal < board.getSize() );
            }
        }
        return maxJumps;
    }
    /**
     * Check if promotion is possible
     */
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
    /**
     * Check if move corresponds to the rules
     */
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