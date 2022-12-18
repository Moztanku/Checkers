package com.tp;

import com.tp.Model.Board;
import com.tp.Model.MovementChecker;
import com.tp.Model.IGameState;

/**
 * Factory interface responsible for creating different variations of Checkers boards and movement
 */
public interface ICheckersFactory {
    Board createBoard();
    MovementChecker createMovement();
    IGameState createState(Checkers checkers);
}
