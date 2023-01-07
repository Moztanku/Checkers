package com.tp.CheckersVariants.Italian;

import com.tp.Checkers;
import com.tp.ICheckersFactory;
import com.tp.GameStates.WhiteTurn;
import com.tp.Model.Board;
import com.tp.Model.IGameState;
import com.tp.Model.Ruleset;

/**
 * Factory for creating an Italian checkers game
 */
public class ItalianCheckersFactory implements ICheckersFactory {
    @Override
    public Board createBoard() {
        return new ItalianBoard();
    }

    @Override
    public Ruleset createMovement() {
        return new ItalianRuleset();
    }

    @Override
    public IGameState createState(Checkers checkers) {
        return new WhiteTurn(checkers);
    }
}
