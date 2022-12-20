package com.tp.Model;

public class Move {
    public Move(Piece before, Piece after, boolean isJump, Piece[] jumped){
        this.before = before;
        this.after = after;

        this.isJump = isJump;
        this.jumped = jumped;
    }

    public Move(Piece before, Piece after){
        this(before, after, false, new Piece[0]);
    }

    public Piece before;
    public Piece after;

    public boolean isJump;
    public Piece[] jumped;
}
