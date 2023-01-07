package com.tp;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tp.CheckersVariants.English.EnglishCheckersFactory;
import com.tp.CheckersVariants.Italian.ItalianCheckersFactory;
import com.tp.CheckersVariants.Polish.PolishChekersFactory;
import com.tp.GameStates.GameEnded;
import com.tp.Network.ServerThread;

/**
 * Server class for checkers game
 * Controller in MVC pattern
 */
public class Server {
    /**
     * Constructor
     * @param args - first argument is (optional) port number
     * @throws IOException - network error
     * @throws NoSuchAlgorithmException - algorithm not found
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        final int MAX_PLAYERS = 2;
        int PORT = 8080;

        if(args.length > 0){
            PORT = Integer.parseInt(args[0]);
        }

        ServerSocket serverSocket = new ServerSocket(PORT); // create server socket
        System.out.println("Server started on port " + PORT + "...");

        var pool = Executors.newFixedThreadPool(MAX_PLAYERS);   // create thread pool for player threads
        ServerThread[] players = new ServerThread[MAX_PLAYERS];

        // accept connections 
        try{
            for(int i = 0; i < MAX_PLAYERS; i++){
                players[i] = new ServerThread(serverSocket.accept());   // accept connection
                pool.execute(players[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set opponents in threads
        for(var player : players){
            for(var opponent : players){
                if(player != opponent){
                    player.addOpponent(opponent.getSocket());
                }
            }
        }

        // wait for players to set their color and variant
        for(int i = 0; i < MAX_PLAYERS; i++){
            while(players[i].getPlayer() == null || players[i].getVariant() == null){   // wait for player to set color and variant
                try{
                    if(players[i].getPlayer() == null)
                        notifyPlayer(players[i], NotifyType.Color); // notify player about missing color
                    else
                        notifyPlayer(players[i], NotifyType.Variant); // notify player about missing variant
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            notifyPlayer(player, NotifyType.Init);
        }

        Checkers checkers = Checkers.getInstance();

        while(!(checkers.getState() instanceof GameEnded)){ // wait for game to end
            try{
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game ended");
        System.out.println("Winner: " + ((GameEnded)checkers.getState()).getWinner());

        serverSocket.close();   // close server socket
    }

    /**
     * Send notification to player
     * @param player - player to notify
     * @param type - type of notification (init/color/variant)
     */
    static private void notifyPlayer(ServerThread player, NotifyType type){
        var gson = new GsonBuilder().create();
        var json = new JsonObject();

        json.addProperty("RequestType", type.toString());

        var content = new JsonObject();
        if(type == NotifyType.Init){
            content.addProperty("Color", player.getPlayer().toString().toLowerCase());
            content.addProperty("Variant", player.getVariant());
        }

        json.add("Content", content);

        try{
            player.notify(
                gson.toJson(json)   // create json from string and send it to player
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Map of checkers variants
     */
    static private Map<String, ICheckersFactory> variants = new HashMap<String, ICheckersFactory>(){
        {
            put("Polish", new PolishChekersFactory());
            put("English", new EnglishCheckersFactory());
            put("Italian", new ItalianCheckersFactory());
        }
    };
      
    /**
     * Types of notifications
     * Init - initialization
     * Color - player color missing
     * Variant - player variant missing
     */
    private enum NotifyType{
        Init,
        Color,
        Variant
    }
}