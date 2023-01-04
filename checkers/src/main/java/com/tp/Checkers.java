package com.tp;

import com.tp.Exceptions.InvalidMoveException;
import com.tp.GameStates.GameEnded;
import com.tp.Model.Board;
import com.tp.Model.Player;
import com.tp.Model.MovementChecker;
import com.tp.Model.IGameState;
import com.tp.Model.Move;

/**
 * Class representing a game of checkers, the M of MVC model
 */
public class Checkers {
    private Board board;
    private MovementChecker movement;
    private IGameState state;

    private static Checkers instance;

    private Checkers(ICheckersFactory factory) {
        board = factory.createBoard();
        movement = factory.createMovement();
        state = factory.createState(this);
    }

    public static void createInstance(ICheckersFactory factory){
        instance = new Checkers(factory);
    }
    public static Checkers getInstance(){
        if(instance == null)
            throw new IllegalStateException("Instance not created");

        return instance;
    }

    public void move(Move move, Player player) throws InvalidMoveException {
        if(move == null || move.before == null || move.after == null){
            throw new InvalidMoveException("Move cannot be null");
        }
        if(move.before.color != player || move.after.color != player){
            throw new InvalidMoveException("You can only move your own pieces");
        }

        state.verifyPlayer(player);
        movement.verifyMove(move, board);
        board.makeMove(move);

        if(board.getPieceCount(Player.WHITE) == 0){
            state = new GameEnded(this, Player.BLACK);
        } else if(board.getPieceCount(Player.BLACK) == 0){
            state = new GameEnded(this, Player.WHITE);
        }

        state.nextTurn();
    }

    public Board getBoard() {
        return board;
    }

    public IGameState getState() {
        return state;
    }
    public void setState(IGameState state) {
        this.state = state;
    }
}
