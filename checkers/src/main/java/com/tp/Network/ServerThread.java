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
/**
 * ServerThread class is responsible for handling the communication with clients.
 */
public class ServerThread implements Runnable {

    private Socket client = null;
    private ArrayList<Socket> opponents = new ArrayList<>();

    private Player player = null;
    private String variant = null;

    /**
     * Constructor for ServerThread
     * @param client - client socket
     */
    public ServerThread(Socket client){
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

    /**
     * Establishes connection with client, and communicates with it.
     */
    @Override
    public void run() {
        try{
            var in = client.getInputStream();
            var out = client.getOutputStream();

            try{
                WebsocketFrameCoder.handshake(in, out); // Perform handshake
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().create(); // Create gson object for json parsing
            byte[] bytes = new byte[1024];
            while((in.read(bytes,0,bytes.length)) != -1){   // Read messages
                String request = WebsocketFrameCoder.decode(bytes); // Decode message
                JsonObject response = processRequest(request); // Process message

                if(response != null){
                    String responseString = gson.toJson(response);  // Convert response to json 
                    sendResponse(out, responseString);  // Send response
                }
            }
            System.out.println("Closing connection...");
            client.close(); // Close connection
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Process request from client
     * @param request - request from client
     * @return - response to client in json format
     */
    private JsonObject processRequest(String request) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = null;
        try{
            json = gson.fromJson(request, JsonObject.class);    // Try to parse request
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Request: " + request);
            return null;
        }

        var requestType = json.get("RequestType").getAsString();    // Get request type (SetVariant, SetColor, Move, GetState, GetBoard)
        var content = json.get("Content").getAsJsonObject();    // Get content of request

        if(requestType == null || content == null){
            System.out.println("Error: RequestType or Content is null");
            return null;
        } 

        switch(requestType){    // Process request based on request type
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
    /**
     * Client requests to set variant
     * @param content - content of request
     * @return - response to client in json format
     */
    private JsonObject SetVariant(JsonObject content) {
        try{
            this.variant = content.get("Variant").getAsString();
            return null;
        } catch (Exception e) {
            return InvalidRequest(e);
        }
    }
    /**
     * Client requests to set color
     * @param content - content of request
     * @return - response to client in json format
     */
    private JsonObject SetColor(JsonObject content) {
        try{
            this.player = Player.valueOf(content.get("Color").getAsString().toUpperCase());
            return null;
        } catch (Exception e) {
            return InvalidRequest(e);
        }
    }
    /**
     * Client requests to move
     * @param content - content of request
     * @return - response to client in json format
     */
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
    /**
     * Client requests to get game state
     * @return - response to client in json format
     */
    private JsonObject GetState() {
        Checkers checkers = Checkers.getInstance();

        var json = new JsonObject();
        json.addProperty("RequestType", "GetState");

        var content = new JsonObject();
        content.addProperty("State", checkers.getState().getTurn().toString().toLowerCase());

        json.add("Content", content);
        return json;
    }
    /**
     * Client requests to get board
     * @return - response to client in json format
     */
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
        content.addProperty("Width", checkers.getBoard().getSize());
        content.addProperty("Height", checkers.getBoard().getSize());
        json.add("Content", content);

        return json;
    }
    /**
     * Client send invalid request
     * @param e - exception
     * @return - response to client in json format with exception
     */
    private JsonObject InvalidRequest(Exception e) {
        var json = new JsonObject();
        json.addProperty("RequestType", "Exception");

        var exception = new JsonObject();
        exception.addProperty("Message", e.getMessage());
        exception.addProperty("StackTrace", e.getStackTrace().toString());

        json.add("Content", exception);
        return json;
    }
    /**
     * Send response to client
     * @param out - output stream
     * @param response - response to client
     * @throws IOException - network error
     */
    private void sendResponse(OutputStream out, String response) throws IOException {
        byte[] responseBytes = WebsocketFrameCoder.encode(response);
        out.write(responseBytes);
    }
    /**
     * Public method to send response to client
     * @param message - response to client
     * @throws IOException - network error
     */
    public void notify(String message) throws IOException {
        sendResponse(client.getOutputStream(), message);
    }
}