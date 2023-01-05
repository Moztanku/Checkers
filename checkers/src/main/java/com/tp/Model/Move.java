package com.tp.Model;

import com.google.gson.JsonObject;

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

    public Move(JsonObject json){
        this.before = new Piece(json.get("Start").getAsJsonObject());
        this.after = new Piece(json.get("End").getAsJsonObject());

        this.isJump = json.get("IsJump").getAsBoolean();

        var jumpedPieces = json.get("JumpedPieces").getAsJsonArray();
        
        this.jumped = new Piece[jumpedPieces.size()];
        for(int i = 0; i < jumpedPieces.size(); i++){
            this.jumped[i] = new Piece(jumpedPieces.get(i).getAsJsonObject());
        }
    }

    public Piece before;
    public Piece after;

    public boolean isJump;
    public Piece[] jumped;
}
