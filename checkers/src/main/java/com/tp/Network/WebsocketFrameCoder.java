package com.tp.Network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsocketFrameCoder{
    public static void handshake(InputStream in, OutputStream out) throws IOException, NoSuchAlgorithmException{
        byte[] buffer = new byte[1024];

        if(in.read(buffer) == -1)
            throw new IOException("Invalid handshake");

        String data = new String(buffer);

        Matcher get = Pattern.compile("^GET").matcher(data);

        if(get.find()){
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"+
                    "Connection: Upgrade\r\n"+
                    "Upgrade: websocket\r\n"+
                    "Sec-WebSocket-Accept: "+
                    Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))+
                    "\r\n\r\n"
                ).getBytes("UTF-8");

            out.write(response, 0, response.length);
        } else{
            throw new IOException("Invalid handshake");
        }
    }

    public static String decode(byte[] bytes){
        // int b1 = bytes[0] & 0xFF;   // TODO: multiple frames support
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

    public static byte[] encode(String string){
        byte[] payload = string.getBytes();
        byte[] frame = null;

        int len = payload.length;

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
        frame[0] = (byte) 129; // FIN + TEXT

        byte[] result = new byte[frame.length + payload.length];
        System.arraycopy(frame, 0, result, 0, frame.length);
        System.arraycopy(payload, 0, result, frame.length, payload.length);

        return result;
    }
}
