package com.tp.Network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for encoding and decoding websocket frames and handshaking
 */
public class WebsocketFrameCoder{
    /**
     * Perform handshake with client
     * @param in - input stream
     * @param out - output stream
     * @throws IOException - network error
     * @throws NoSuchAlgorithmException - algorithm not found
     */
    public static void handshake(InputStream in, OutputStream out) throws IOException, NoSuchAlgorithmException{
        byte[] buffer = new byte[1024]; // Space for message

        if(in.read(buffer) == -1)   // Read message
            throw new IOException("Invalid handshake");

        String data = new String(buffer);   // Convert to string

        Matcher get = Pattern.compile("^GET").matcher(data);    // Matcher to check if it's a GET request

        if(get.find()){
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);   // Matcher to get key
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"+  // Construct response
                    "Connection: Upgrade\r\n"+
                    "Upgrade: websocket\r\n"+
                    "Sec-WebSocket-Accept: "+
                    Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))+ // Calculate key
                    "\r\n\r\n"
                ).getBytes("UTF-8");

            out.write(response, 0, response.length);    // Send response
        } else{
            throw new IOException("Invalid handshake");
        }
    }

    /**
     * Decode websocket frame
     * @param bytes - message to decode
     * @return - decoded message
     */
    public static String decode(byte[] bytes){
        // int b1 = bytes[0] & 0xFF;    // first byte   // TODO: multiple frames support
        int b2 = bytes[1] & 0xFF;   // second byte

        int len = b2 & 127; // length of message (7 bits)
        byte[] mask = new byte[4]; // mask for decoding with XOR
        int maskindex = 0;  // index of mask

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
        mask[2] = bytes[maskindex+2]; mask[3] = bytes[maskindex+3]; // get mask

        int dataindex = maskindex + 4;  // index of data (4 bytes after mask)
        byte[] data = new byte[len];    // buffer for decoded data

        for(int i = 0; i < len; i++){
            data[i] = (byte) (bytes[dataindex+i] ^ mask[i % 4]);    // decode data with XOR
        }

        return new String(data);    // return as string
    }

    /**
     * Encode message to websocket frame
     * @param message - message to encode
     * @return - encoded message
     */
    public static byte[] encode(String message){
        byte[] payload = message.getBytes(); // get bytes of message
        byte[] header = null;    // buffer for frame header

        int len = payload.length;

        if(len <= 125){ // 7 bits length message
            header = new byte[2];
            header[1] = (byte) len;
        } else if(len >= 126 && len <= 65535){ // 2 bytes length message
            header = new byte[4];
            header[1] = (byte) 126;
            header[2] = (byte) ((len >> 8) & 0xFF);
            header[3] = (byte) (len & 0xFF);
        } else { // 8 bytes length message
            header = new byte[10];
            header[1] = (byte) 127; 
            header[2] = (byte) ((len >> 56) & 0xFF); header[3] = (byte) ((len >> 48) & 0xFF);
            header[4] = (byte) ((len >> 40) & 0xFF); header[5] = (byte) ((len >> 32) & 0xFF);
            header[6] = (byte) ((len >> 24) & 0xFF); header[7] = (byte) ((len >> 16) & 0xFF);
            header[8] = (byte) ((len >> 8) & 0xFF);  header[9] = (byte) (len & 0xFF);
        }
        header[0] = (byte) 129; // FIN + TEXT

        byte[] result = new byte[header.length + payload.length];    // buffer for result
        System.arraycopy(header, 0, result, 0, header.length);    // copy frame to result
        System.arraycopy(payload, 0, result, header.length, payload.length); // copy payload to result

        return result;
    }
}
