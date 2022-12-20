package com.tp;

import static org.junit.Assert.assertEquals;

import com.tp.Model.Piece;
import com.tp.Model.Player;

public abstract class CheckersTest {
    public void assertPiece(Piece piece, int x, int y, Player player){
        assertEquals(x, piece.X);
        assertEquals(y, piece.Y);
        assertEquals(player, piece.color);
    }
}
