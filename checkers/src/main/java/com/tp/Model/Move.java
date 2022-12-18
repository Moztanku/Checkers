package com.tp.Model;

public class Move {
    Move(Piece before, Piece after, boolean isJump, Piece[] jumped){
        this.before = before;
        this.after = after;

        this.isJump = isJump;
        this.jumped = jumped;
    }

    Move(Piece before, Piece after){
        this(before, after, false, null);
    }

    public Piece before;
    public Piece after;

    public boolean isJump;
    public Piece[] jumped;
}
