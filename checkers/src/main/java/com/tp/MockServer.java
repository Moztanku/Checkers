package com.tp;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.Thread.State;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.WebSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.tp.CheckersVariants.Polish.PolishChekersFactory;
import com.tp.GameStates.GameEnded;
import com.tp.Model.Player;

public class MockServer {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080...");

        Checkers checkers = new Checkers(new PolishChekersFactory());

        var pool = Executors.newFixedThreadPool(2);
        try{
            while(!(checkers.getState() instanceof GameEnded)){
                pool.execute(new ServerThread(serverSocket.accept(), Player.WHITE));
                pool.execute(new ServerThread(serverSocket.accept(), Player.BLACK));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ServerThread implements Runnable {
    private Socket client;

    private Player player;

    public ServerThread(Socket client, Player player) throws IOException {
        System.out.println("Connected: " + client);

        this.client = client;
        this.player = player;
    }

    @Override
    public void run() {
        try{
            var in = client.getInputStream();
            var out = client.getOutputStream();

            if(!websocketHandshake(new Scanner(in, "UTF-8"), out)){
                return;
            }

            System.out.println("Handshake done");

            int b = 0;
            byte[] bytes = new byte[1024];
            while((b = in.read(bytes,0,bytes.length)) != -1){
                String request = decode(bytes);
                handleRequest(request, out);
            }

            System.out.println("Closing connection...");
            client.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String decode(byte[] bytes) {
        int b1 = bytes[0] & 0xFF;
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

    private void handleRequest(String request, OutputStream writer) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = gson.fromJson(request, JsonObject.class);

        System.out.println("Request: " + json.get("RequestType"));
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

    private void sendResponse(OutputStream out, String response) throws IOException {
        out.write(response.getBytes("UTF-8"));
    }
}