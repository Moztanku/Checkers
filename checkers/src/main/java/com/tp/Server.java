package com.tp;
import javax.json.*;
/**
 * Class responsible for establishing connection, and handling requests from clients, the C in MVC.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import com.tp.CheckersVariants.Polish.PolishChekersFactory;
import com.tp.Exceptions.InvalidMoveException;
import com.tp.GameStates.GameEnded;
import com.tp.Model.Board;
import com.tp.Model.Move;
import com.tp.Model.Piece;

public class Server {

    public static void main(String[] args) throws Exception {
        try (var listener = createServerSocket(58901)) {
            System.out.println("Checkers server is running...");
            var pool = Executors.newFixedThreadPool(200);
            while (true) {
                Game game = new Game();
                //System.out.println("Game Created");
                pool.execute(game.new Player(listener.accept(), '1'));
                System.out.println("Player joined");
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
    
    public static ServerSocket createServerSocket(int port) throws IOException {
    	return new ServerSocket(port);
    }
}

class Game {

	ICheckersFactory factory;
    Checkers checkers;
    Board board;

    Player currentPlayer;

    class Player implements Runnable {
        char mark;
        Player opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;
        ObjectOutputStream objOutput;
        ObjectInputStream objInput;
        Move move;
        JsonWrapper wrapper;
       

        public Player(Socket socket, char mark) {
            this.socket = socket;
            this.mark = mark;
        }

        @Override
        public void run() {
            try {
                setup();
                //processCommands();
                processObjects();
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (opponent != null && opponent.output != null) {
                    opponent.output.println("OTHER_PLAYER_LEFT");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private void setup() throws IOException {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            objOutput = new ObjectOutputStream(socket.getOutputStream());
            objInput = new ObjectInputStream(socket.getInputStream());
            
            //Only one player need to implement checkers.
            if(mark == '1') {
            	setUpCheckers();
            }
            
            //Sending to client board size
            output.println("SIZE " + getBoardSize());
            
            output.println("WELCOME " + mark);
            if (mark == '1') {
                currentPlayer = this;
                output.println("MESSAGE Waiting for opponent to connect");
            } else {
                opponent = currentPlayer;
                opponent.opponent = this;
                opponent.output.println("MESSAGE Your move");
            }
        }
        /*
         * Used to communicate with server.
         */
        private void processCommands() {
            while (input.hasNextLine()) {
                var command = input.nextLine();
                if (command.startsWith("QUIT")) {
                    return;
                }
            }
        }
        /**
         * Read JsonObject from input to process changes.
         * Create move object for checkers to process changes.
         * If there is a InvalidMoveException send JsonObject to
         * client with error message.
         */
        private void processObjects() {
        	while(true) {
        		if(checkers.getState() instanceof GameEnded) {
        			break;
        		}
        		try {
        			try {
						wrapper = JsonSerDe.deserialize(objInput);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
        			JsonObject obj = wrapper.getJsonObject();
					//JsonObject obj = (JsonObject) objInput.readObject();
					JsonObject OldPiece = obj.getJsonObject("old");
					int xOld = OldPiece.getInt("x");
					int yOld = OldPiece.getInt("y");
					Piece oldPiece = board.getPiece(xOld, yOld);
					Piece newPiece;
					
					JsonObject NewPiece = obj.getJsonObject("new");
					int xNew = NewPiece.getInt("x");
					int yNew = NewPiece.getInt("y");
					boolean isQueen = NewPiece.getBoolean("isQueen");
					
					if(mark == '1') {
						newPiece = new Piece(xNew, yNew, isQueen, com.tp.Model.Player.WHITE);
					} else {
						newPiece = new Piece(xNew, yNew, isQueen, com.tp.Model.Player.BLACK);
					}
					
					JsonArray array = obj.getJsonArray("jumped");
					Piece captured[] = new Piece[array.size()];
					int i = 0;
					
					for(Object o: array) {
			        	int x =((JsonObject) o).getInt("x");
			        	int y =((JsonObject) o).getInt("y");
			        	
			        	captured[i] = board.getPiece(x, y);
			        	
			        	i++;
			        }
					
					move = new Move(oldPiece, newPiece, isQueen, captured);
					try {
						if(mark == '1') {
							checkers.move(move, com.tp.Model.Player.WHITE);
						} else {
							checkers.move(move, com.tp.Model.Player.BLACK);
						}
						
					} catch (InvalidMoveException e) {
						// TODO Auto-generated catch block
						JsonObject error = Json.createObjectBuilder()
											.add("error", e.getMessage()).build();
						wrapper.setJsonObject(error , "Error");
						
						objOutput.writeObject(wrapper);
						e.printStackTrace();
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        private int getBoardSize() {
        	return board.getSize();
        }
        /*
         * Using a wrapper for JsonObject because it isn't serializable.
         * Method used to deduce what type of checkers player wants to play.
         */
        private void setUpCheckers() {
        	
			//JsonSerDe stream = new JsonSerDe();
			
			try {
				wrapper = JsonSerDe.deserialize(objInput);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JsonObject jsonFactory = wrapper.getJsonObject();
			String Sfactory = jsonFactory.getString("Factory");
			
			if(Sfactory == "polishCheckers") {
				factory = new PolishChekersFactory();
				checkers = new Checkers(factory);
				board = checkers.getBoard();
			}
        }

    }
}
