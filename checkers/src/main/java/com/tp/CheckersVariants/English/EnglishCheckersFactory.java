package com.tp.CheckersVariants.English;

import com.tp.Checkers;
import com.tp.ICheckersFactory;
import com.tp.GameStates.WhiteTurn;
import com.tp.Model.Board;
import com.tp.Model.IGameState;
import com.tp.Model.Ruleset;

/**
 * Factory for creating an English checkers game
 */
public class EnglishCheckersFactory implements ICheckersFactory {
    @Override
    public Board createBoard() {
        return new EnglishBoard();
    }

    @Override
    public Ruleset createMovement() {
        return new EnglishRuleset();
    }

    @Override
    public IGameState createState(Checkers checkers) {
        return new WhiteTurn(checkers);
    }
}
