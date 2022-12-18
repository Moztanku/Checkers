package com.tp.CheckersVariants.Polish;

import com.tp.Checkers;
import com.tp.ICheckersFactory;
import com.tp.GameStates.WhiteTurn;
import com.tp.Model.Board;
import com.tp.Model.MovementChecker;
import com.tp.Model.IGameState;

public class PolishChekersFactory implements ICheckersFactory {

    @Override
    public Board createBoard() {
        return new PolishBoard();
    }

    @Override
    public MovementChecker createMovement() {
        return new PolishMovementChecker();
    }

    @Override
    public IGameState createState(Checkers checkers) {
        return new WhiteTurn(checkers);
    }
    
    
}
