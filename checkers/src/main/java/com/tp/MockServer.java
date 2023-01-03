package com.tp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class MockServer{
    private int port = 8080;

    public static void main(String[] args) throws Exception {
        new MockServer().run();
    }

    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception{
                        ch.pipeline().addLast(new MockServerHandler());
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.bind(port).sync();
                f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

class MockServerHandler extends ChannelInboundHandlerAdapter {

}

// public class MockServer {
//     public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//         ServerSocket serverSocket = new ServerSocket(8080);
//         System.out.println("Server started on port 8080...");

//         Checkers checkers = new Checkers(new PolishChekersFactory());

//         var pool = Executors.newFixedThreadPool(2);
//         try{
//             while(!(checkers.getState() instanceof GameEnded)){
//                 pool.execute(new ServerThread(serverSocket.accept(), Player.WHITE));
//                 pool.execute(new ServerThread(serverSocket.accept(), Player.BLACK));
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }

// class ServerThread implements Runnable {
//     private Socket client;

//     private Player player;

//     public ServerThread(Socket client, Player player) throws IOException {
//         System.out.println("Connected: " + client);

//         this.client = client;
//         this.player = player;
//     }

//     @Override
//     public void run() {
//         try{
//             var in = new Scanner(client.getInputStream(), "UTF-8");
//             var out = client.getOutputStream();

//             if(!websocketHandshake(in, out)){
//                 return;
//             }

//             System.out.println("Handshake done");
//             while(in.useDelimiter("\\r\\n\\r\\n").hasNextLine()){
//                 String line = in.useDelimiter("\\r\\n\\r\\n").nextLine();
//                 System.out.println(client+": "+line);
                
//                 handleRequest(line, out);
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private void handleRequest(String request, OutputStream writer) {
//         // GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();

//         // String json = gsonBuilder.create().toJson(request);
//         // System.out.println(json);
//     }

//     private boolean websocketHandshake(Scanner in, OutputStream out) throws IOException {
//         try{
//             String data = in.useDelimiter("\\r\\n\\r\\n").next();
//             Matcher get = Pattern.compile("^GET").matcher(data);

//             Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
//             match.find();
//             byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
//             + "Connection: Upgrade\r\n"
//             + "Upgrade: websocket\r\n"
//             + "Sec-WebSocket-Accept: "
//             + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
//             + "\r\n\r\n").getBytes("UTF-8");

//             out.write(response, 0, response.length);
//         } catch (Exception e) {
//             e.printStackTrace();
//             return false;
//         }

//         return true;
//     }

//     private void sendResponse(OutputStream out, String response) throws IOException {
//         out.write(response.getBytes("UTF-8"));
//     }
// }