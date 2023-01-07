package com.tp;

import com.tp.Exceptions.InvalidMoveException;
import com.tp.GameStates.GameEnded;
import com.tp.Model.Board;
import com.tp.Model.Player;
import com.tp.Model.Ruleset;
import com.tp.Model.IGameState;
import com.tp.Model.Move;

/**
 * Class representing a game of checkers, the M of MVC model
 * Facade class for the game
 */
public class Checkers {
    private Board board;
    private Ruleset movement;
    private IGameState state;

    private static Checkers instance;

    /**
     * Private constructor, use createInstance to create an instance
     * @param factory Factory to create the board, movement checker and game state
     */
    private Checkers(ICheckersFactory factory) {
        board = factory.createBoard();
        movement = factory.createMovement();
        state = factory.createState(this);
    }

    /**
     * Creates an instance of the game
     * @param factory Factory to create the board, movement checker and game state
     */
    public static void createInstance(ICheckersFactory factory){
        instance = new Checkers(factory);
    }
    /**
     * Gets the instance of the game
     * @return Instance of the game
     */
    public static Checkers getInstance(){
        if(instance == null)
            throw new IllegalStateException("Instance not created");

        return instance;
    }

    /**
     * Checks if move is correct and makes it
     * @param move Move to make
     * @param player Player making the move
     * @throws InvalidMoveException If the move is invalid
     */
    public void move(Move move, Player player) throws InvalidMoveException {
        if(move == null || move.before == null || move.after == null){
            throw new InvalidMoveException("Move cannot be null");
        }
        if(move.before.color != player || move.after.color != player){
            throw new InvalidMoveException("You can only move your own pieces");
        }

        state.verifyPlayer(player); // throws InvalidMoveException if player is not allowed to move
        movement.verifyMove(move, board); // throws InvalidMoveException if move is invalid
        board.makeMove(move); // commits the move

        if(board.getPieceCount(Player.WHITE) == 0){
            state = new GameEnded(this, Player.BLACK);
        } else if(board.getPieceCount(Player.BLACK) == 0){
            state = new GameEnded(this, Player.WHITE);
        }

        state.nextTurn();   // changes the state to the next turn
    }

    /**
     * Gets the board
     * @return Board
     */
    public Board getBoard() {
        return board;
    }
    /**
     * Gets the game state
     * @return Game state
     */
    public IGameState getState() {
        return state;
    }
    /**
     * Sets the game state
     * @param state Game state
     */
    public void setState(IGameState state) {
        this.state = state;
    }
}
