package com.tp.CheckersVariants.Polish;

import com.tp.Checkers;
import com.tp.ICheckersFactory;
import com.tp.GameStates.WhiteTurn;
import com.tp.Model.Board;
import com.tp.Model.Ruleset;
import com.tp.Model.IGameState;

/**
 * Factory for creating a Polish checkers game
 */
public class PolishChekersFactory implements ICheckersFactory {

    @Override
    public Board createBoard() {
        return new PolishBoard();
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
