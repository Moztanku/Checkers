package com.tp.CheckersVariants.Testing;

import com.tp.Checkers;
import com.tp.ICheckersFactory;
import com.tp.CheckersVariants.Polish.PolishBoard;
import com.tp.CheckersVariants.Polish.PolishRuleset;
import com.tp.GameStates.WhiteTurn;
import com.tp.Model.Board;
import com.tp.Model.Ruleset;
import com.tp.Model.IGameState;

/**
 * Factory for creating a Polish checkers game
 */
public class TestCheckersFactory implements ICheckersFactory {

    @Override
    public Board createBoard() {
        return new TestBoard();
    }

    @Override
    public Ruleset createMovement() {
        return new PolishRuleset();
    }

    @Override
    public IGameState createState(Checkers checkers) {
        return new WhiteTurn(checkers);
    }
    
    
}
