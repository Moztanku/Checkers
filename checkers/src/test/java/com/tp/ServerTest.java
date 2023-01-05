package com.tp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.json.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
/*
 * Class used to test server's communication with client via JsonObjects.
 */
public class ServerTest {
	
	JsonObject obj;
	JsonObject factory;
	@Before
	public void setup() {
		obj = Json.createObjectBuilder()
			      .add("old", Json.createObjectBuilder()
			      	.add("x", 2)
			      	.add("y", 2)
			      	.add("isQueen", false))
			      .add("new", Json.createObjectBuilder()
			        .add("x", 3)
			        .add("y", 3)
			        .add("isQueen", false))
			      .add("isJump", false)
			      .add("jumped", Json.createArrayBuilder()
			        .add(Json.createObjectBuilder()
			          .add("x", 4)
			          .add("y", 4)
			          .add("isQueen", false))
			        .add(Json.createObjectBuilder()
			          .add("x", 6)
			          .add("y", 6)
			          .add("isQueen", false)))
			        .build();
		
		factory = Json.createObjectBuilder().add("Factory", "polishCheckers").build();
	}
	
	/*
	@Test
	public void testServerSocketIsCreated() throws IOException {
		assertNotNull(createServerSocket(58901));
	}
	*/
	
	@Test
	public void testServerSocketIsCreatedWithSpecificPort() throws IOException {
		final int testPort = 58902;
		ServerSocket testServerSocket = createServerSocket(testPort);
		
		assertEquals(testServerSocket.getLocalPort(), testPort);
		
	}
	
	/*
	@Test
	public void testClientSocketGetsCreated() throws IOException {
		ServerSocket mockServerSocket = mock(ServerSocket.class);
		when(mockServerSocket.accept()).thenReturn(new Socket());
		
		assertNotNull(createClientSocket(mockServerSocket));
	}
	*/
	
	public static ServerSocket createServerSocket(int port) throws IOException {
	    return new ServerSocket(port);
	}
	
	public static Socket createClientSocket(ServerSocket socket) throws IOException {
	    return socket.accept();
	}
	
	@Mock
	Socket clientSocket;
	
	@Mock
	InputStream inputStream;
	
	@Mock
	OutputStream outputStream;
	
	@Mock
	ObjectInputStream objInputStream;
	
	@Mock
	ObjectOutputStream objOutputStream;
	
	JsonWrapper wrapper = new JsonWrapper();
	
	public static Scanner createSocketReader(Socket socket) throws IOException {
		return new Scanner(new InputStreamReader(socket.getInputStream()));
	}
	
	public static PrintWriter createSocketWriter(Socket socket) throws IOException {
		return new PrintWriter(socket.getOutputStream(), true);
	}
	
	
	/**
	 * 
	 * @param input read from input stream.
	 * @return Read messages from server.
	 */
	public static String readFromInputStream(Scanner input) {
	    return input.nextLine();
	}
	/**
	 * 
	 * @param output write to output stream.
	 * @param data data to send to server.
	 */
	public static void writeToOutputStream(PrintWriter output, String data) {
	    output.println(data);
	}
	
	/**
	 * 
	 * @param input read from Object input stream.
	 * @return Read sent object.
	 */
	public static JsonWrapper readFromObjectInputStream(ObjectInputStream input) {
		try {
			return JsonSerDe.deserialize(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param output send object to server.
	 * @param data object desired to send.
	 */
	public static void writeToObjectOutputStream(ObjectOutputStream output, JsonWrapper wrapper) {
		try {
			JsonSerDe.serialize(wrapper, output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testServerResponse() {
		//Server server = new Server();
		setup();
		try {
			try {
				Server.main(null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Socket socket = new Socket("127.0.0.1" ,58901);
			objInputStream = new ObjectInputStream(socket.getInputStream());
			objOutputStream = new ObjectOutputStream(socket.getOutputStream());
			wrapper.setJsonObject(factory, "Factory");
			try {
				JsonSerDe.serialize(wrapper, objOutputStream);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			wrapper.setJsonObject(obj, "Move");
			writeToObjectOutputStream(objOutputStream, wrapper);
			
			//Read error message.
			JsonObject objRead;
			wrapper = readFromObjectInputStream(objInputStream);
			objRead = wrapper.getJsonObject();
			
			System.out.println(objRead.getString("error"));
			assertNotNull(objRead);
			
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
