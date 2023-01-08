package com.tp;

import com.tp.Model.Board;
import com.tp.Model.Ruleset;
import com.tp.Model.IGameState;

/**
 * Factory interface responsible for creating different variations of Checkers boards and movement
 */
public interface ICheckersFactory {
    Board createBoard();
    Ruleset createMovement();
    IGameState createState(Checkers checkers);
}
