package com.tp.Network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tp.Checkers;
import com.tp.Model.Player;

public class ServerThread implements Runnable {

    private Socket client = null;
    private ArrayList<Socket> opponents = new ArrayList<>();

    private Player player = null;
    private String variant = null;

    public ServerThread(Socket client) throws IOException {
        System.out.println("Connected: " + client);

        this.client = client;
    }

    public Player getPlayer(){
        return this.player;
    }
    public void setPlayer(Player player){
        this.player = player;
    }

    public String getVariant(){
        return this.variant;
    }
    public void setVariant(String variant){
        this.variant = variant;
    }

    public void addOpponent(Socket opponent){
        this.opponents.add(opponent);
    }
    public Socket getSocket(){
        return this.client;
    }

    @Override
    public void run() {
        try{
            var in = client.getInputStream();
            var out = client.getOutputStream();

            try{
                WebsocketFrameCoder.handshake(in, out);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().create();
            byte[] bytes = new byte[1024];
            while((in.read(bytes,0,bytes.length)) != -1){
                String request = WebsocketFrameCoder.decode(bytes);
                JsonObject response = processRequest(request, out);

                if(response != null){
                    String responseString = gson.toJson(response);
                    sendResponse(out, responseString);
                }
            }
            System.out.println("Closing connection...");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonObject processRequest(String request, OutputStream output) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = null;
        try{
            json = gson.fromJson(request, JsonObject.class);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Request: " + request);
            return null;
        }

        var requestType = json.get("RequestType").getAsString();
        var content = json.get("Content").getAsJsonObject();

        if(requestType == null || content == null){
            System.out.println("Error: RequestType or Content is null");
            return null;
        } 

        switch(requestType){
            case "SetVariant":
                return SetVariant(content);
            case "SetColor":
                return SetColor(content);
            case "Move":
                return Move(content);
            case "GetState":
                return GetState();
            case "GetBoard":
                return GetBoard();
            default:
                return InvalidRequest(new Exception("Invalid request type"));
        }
    }

    private JsonObject SetVariant(JsonObject content) {
        try{
            this.variant = content.get("Variant").getAsString();
            return null;
        } catch (Exception e) {
            return InvalidRequest(e);
        }
    }

    private JsonObject SetColor(JsonObject content) {
        try{
            this.player = Player.valueOf(content.get("Color").getAsString().toUpperCase());
            return null;
        } catch (Exception e) {
            return InvalidRequest(e);
        }
    }

    private JsonObject Move(JsonObject content) {
        var move = new com.tp.Model.Move(content);

        Checkers checkers = Checkers.getInstance();
        try{
            checkers.move(move, player);
        } catch (Exception e){
            return InvalidRequest(e);
        }
        try{
            for (var opponent : opponents) {
                sendResponse(opponent.getOutputStream(), GetBoard().toString());
                sendResponse(opponent.getOutputStream(), GetState().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GetState();
    }

    private JsonObject GetState() {
        Checkers checkers = Checkers.getInstance();

        var json = new JsonObject();
        json.addProperty("RequestType", "GetState");

        var content = new JsonObject();
        content.addProperty("State", checkers.getState().getTurn().toString().toLowerCase());

        json.add("Content", content);
        return json;
    }

    private JsonObject GetBoard() {
        Checkers checkers = Checkers.getInstance();

        var json = new JsonObject();

        json.addProperty("RequestType", "GetBoard");

        var content = new JsonObject();
        var pieces = new JsonArray();
        
        for (var piece : checkers.getBoard().getPieces()) {
            var pieceJson = new JsonObject();
            pieceJson.addProperty("Color", piece.color.toString().toLowerCase());
            pieceJson.addProperty("x",piece.X);
            pieceJson.addProperty("y",piece.Y);
            pieceJson.addProperty("isQueen",piece.isQueen);

            pieces.add(pieceJson);
        }

        content.add("Pieces", pieces);
        json.add("Content", content);

        return json;
    }
    
    private JsonObject InvalidRequest(Exception e) {
        var json = new JsonObject();
        json.addProperty("RequestType", "Exception");

        var exception = new JsonObject();
        exception.addProperty("Message", e.getMessage());
        exception.addProperty("StackTrace", e.getStackTrace().toString());

        json.add("Content", exception);
        return json;
    }

    private void sendResponse(OutputStream out, String response) throws IOException {
        byte[] responseBytes = WebsocketFrameCoder.encode(response);
        out.write(responseBytes);
    }

    public void notify(String message) throws IOException {
        sendResponse(client.getOutputStream(), message);
    }
}