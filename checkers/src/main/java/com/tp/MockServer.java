package com.tp;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tp.CheckersVariants.Polish.PolishChekersFactory;
import com.tp.GameStates.GameEnded;
import com.tp.Model.Player;

public class MockServer {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080...");

        var pool = Executors.newFixedThreadPool(2);
        ServerThread[] players = new ServerThread[2];

        try{
            players[0] = new ServerThread(serverSocket.accept());
            pool.execute(players[0]);
            players[1] = new ServerThread(serverSocket.accept());
            pool.execute(players[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // wait for players to set their color and variant
        while(players[0].getPlayer() == null || players[1].getPlayer() == null ||
            players[0].getVariant() == null || players[1].getVariant() == null){
            try{
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // get randomly variant from players
        String variant = Math.round(Math.random()) == 0 ? players[0].getVariant() : players[1].getVariant();
        for(ServerThread player : players){
            player.setVariant(variant);
        }

        if(players[0].getPlayer() == players[1].getPlayer()){   // First player has priority
            players[1].setPlayer(
                players[0].getPlayer().getOpponent()
            );
        }

        // create game
        Checkers.createInstance(
            variants.get(variant)
        );

        // notify players about initialization
        for(ServerThread player : players){
            notifyPlayer(player);
        }

        Checkers checkers = Checkers.getInstance();

        while(!(checkers.getState() instanceof GameEnded)){
            try{
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game ended");
        System.out.println("Winner: " + ((GameEnded)checkers.getState()).getWinner());

        serverSocket.close();
    }

    static private void notifyPlayer(ServerThread player){
        var gson = new GsonBuilder().create();
        var json = new JsonObject();
        json.addProperty("RequestType","Init");

        var content = new JsonObject();
        content.addProperty("Color", player.getPlayer().toString().toLowerCase());
        content.addProperty("Variant", player.getVariant());

        json.add("Content", content);

        try{
            player.notify(
                gson.toJson(json)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private Map<String, ICheckersFactory> variants = new HashMap<String, ICheckersFactory>(){
        {
            put("Standard", new PolishChekersFactory());
        }
    };
        
}

class ServerThread implements Runnable {

    private Socket client = null;

    private Player player = null;
    private String variant = null;

    public ServerThread(Socket client) throws IOException {
        System.out.println("Connected: " + client);

        this.client = client;
    }

    public Player getPlayer(){
        return this.player;
    };
    public void setPlayer(Player player){
        this.player = player;
    };

    public String getVariant(){
        return this.variant;
    };
    public void setVariant(String variant){
        this.variant = variant;
    };

    @Override
    public void run() {
        try{
            var in = client.getInputStream();
            var out = client.getOutputStream();

            if(!websocketHandshake(new Scanner(in, "UTF-8"), out)){
                return;
            }

            byte[] bytes = new byte[1024];
            while((in.read(bytes,0,bytes.length)) != -1){
                String request = decode(bytes);
                processRequest(request, out);
            }

            System.out.println("Closing connection...");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequest(String request, OutputStream output) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = null;
        try{
            json = gson.fromJson(request, JsonObject.class);
            // System.out.println(json);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        var requestType = json.get("RequestType").getAsString();
        var content = json.get("Content").getAsJsonObject();

        if(requestType == null || content == null){
            System.out.println("Error: RequestType or Content is null");
            return;
        } else {
            JsonObject responseJson = handleRequest(requestType, content);

            if(responseJson == null){
                return;
            }

            try{
                sendResponse(output, responseJson.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JsonObject handleRequest(String requestType, JsonObject content) {
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
                return InvalidRequest();
        }
    }

    private JsonObject SetVariant(JsonObject content) {
        try{
            this.variant = content.get("Variant").getAsString();
            return null;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return InvalidRequest();
        }
    }

    private JsonObject SetColor(JsonObject content) {
        try{
            this.player = Player.valueOf(content.get("Color").getAsString().toUpperCase());
            return null;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return InvalidRequest();
        }
    }

    private JsonObject Move(JsonObject content) {
        return null;
    }

    private JsonObject GetState() {
        return null;
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
    
    private JsonObject InvalidRequest() {
        return null;
    }

    private boolean websocketHandshake(Scanner in, OutputStream out) throws IOException {
        try{
            String data = in.useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);

            if(get.find()){
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                + "Connection: Upgrade\r\n"
                + "Upgrade: websocket\r\n"
                + "Sec-WebSocket-Accept: "
                + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                + "\r\n\r\n").getBytes("UTF-8");

                out.write(response, 0, response.length);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String decode(byte[] bytes) {
        int b1 = bytes[0] & 0xFF;   // TODO: multiple frames support
        int b2 = bytes[1] & 0xFF;

        int len = b2 & 127;
        byte[] mask = new byte[4];
        int maskindex = 0;

        if(len == 126){ // 2 bytes length message
            len = (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
            maskindex = 4;
        } else if(len == 127){ // 8 bytes length message
            len = (bytes[2] & 0xFF) << 56 | (bytes[3] & 0xFF) << 48 | (bytes[4] & 0xFF) << 40 | (bytes[5] & 0xFF) << 32 | (bytes[6] & 0xFF) << 24 | (bytes[7] & 0xFF) << 16 | (bytes[8] & 0xFF) << 8 | (bytes[9] & 0xFF);
            maskindex = 10;
        } else { // 7 bits length message
            maskindex = 2;
        }

        mask[0] = bytes[maskindex]; mask[1] = bytes[maskindex+1];
        mask[2] = bytes[maskindex+2]; mask[3] = bytes[maskindex+3];

        int dataindex = maskindex + 4;
        byte[] data = new byte[len];

        for(int i = 0; i < len; i++){
            data[i] = (byte) (bytes[dataindex+i] ^ mask[i % 4]);
        }

        return new String(data);
    }

    private void sendResponse(OutputStream out, String response) throws IOException {
        byte[] bytes = response.getBytes("UTF-8");
        byte[] frame = null;

        int len = bytes.length;

        if(len <= 125){ // 7 bits length message
            frame = new byte[2];

            frame[1] = (byte) len;
        } else if(len >= 126 && len <= 65535){ // 2 bytes length message
            frame = new byte[4];

            frame[1] = (byte) 126;
            frame[2] = (byte) ((len >> 8) & 0xFF);
            frame[3] = (byte) (len & 0xFF);
        } else { // 8 bytes length message
            frame = new byte[10];

            frame[1] = (byte) 127; 
            frame[2] = (byte) ((len >> 56) & 0xFF); frame[3] = (byte) ((len >> 48) & 0xFF);
            frame[4] = (byte) ((len >> 40) & 0xFF); frame[5] = (byte) ((len >> 32) & 0xFF);
            frame[6] = (byte) ((len >> 24) & 0xFF); frame[7] = (byte) ((len >> 16) & 0xFF);
            frame[8] = (byte) ((len >> 8) & 0xFF);  frame[9] = (byte) (len & 0xFF);
        }
        frame[0] = (byte) 129;

        out.write(frame);
        out.write(bytes);
    }

    public void notify(String message) throws IOException {
        sendResponse(client.getOutputStream(), message);
    }
}