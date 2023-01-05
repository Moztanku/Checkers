package com.tp;

import java.io.Serializable;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.tp.Model.Piece;
/*
 * Wrapper for JsonObject because it isn't serializable
 * 
 * When it gets an object it dismantles it and saves it's fields in variables.
 * When it sends JsonObject it assembles a JsonObject. 
 */
public class JsonWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient JsonObject jsonObject = null;
	
	private String factory;
	private String name;
	
	private int xOld;
	private int yOld;
	private int xNew;
	private int yNew;
	private boolean isQueenOld;
	private boolean isQueenNew;
	private int pieces[][];
	private String Sfactory;
	private boolean isJump;
	private boolean queens[];
	private String error;
	
	private void dismantle() {
		
		if(name == "Move") {
		JsonObject OldPiece = jsonObject.getJsonObject("old");
		int xOld = OldPiece.getInt("x");
		int yOld = OldPiece.getInt("y");
		boolean isQueenOld = OldPiece.getBoolean("isQueen");
	
		JsonObject NewPiece = jsonObject.getJsonObject("new");
		int xNew = NewPiece.getInt("x");
		int yNew = NewPiece.getInt("y");
		boolean isQueenNew = NewPiece.getBoolean("isQueen");
		boolean isJump = jsonObject.getBoolean("isJump");
		
		JsonArray array = jsonObject.getJsonArray("jumped");
		pieces = new int[array.size()][2];
		queens = new boolean[array.size()];
		int i = 0;
		
		for(Object o: array) {
        	int x =((JsonObject) o).getInt("x");
        	int y =((JsonObject) o).getInt("y");
        	pieces[i][0] = x;
        	pieces[i][1] = y;
        	
        	queens[i] = ((JsonObject) o).getBoolean("isQueen"); 
        	i++;
        }
		
		} else if(name == "Factory") {
			Sfactory = jsonObject.getString("Factory");
		} else if(name == "Error") {
			error = jsonObject.getString("error");
		}
	}
	
	private void assemble() {
		if(name == "Move") {
			
			JsonArray array = makeJsonArray();
			
			jsonObject = Json.createObjectBuilder()
				      .add("old", Json.createObjectBuilder()
				      	.add("x", xOld)
				      	.add("y", yOld)
				      	.add("isQueen", isQueenOld))
				      .add("new", Json.createObjectBuilder()
				        .add("x", xNew)
				        .add("y", yNew)
				        .add("isQueen", isQueenNew))
				      .add("isJump", isJump)
				      .add("jumped", array)
				        .build();
			
		}
		if(name == "Factory") {
			jsonObject = Json.createObjectBuilder().add("Factory", "polishCheckers").build();
			System.out.println("jsonObject is set");
		}
		if(name == "Error") {
			jsonObject = Json.createObjectBuilder().add("error", error).build();
		} else {
			//System.out.println("name is null in JsonWrapper: " + name);
			//System.out.println(jsonObject.getString("Factory"));
		}
	}
	
	private JsonArray makeJsonArray() {
		int a = 0;
		JsonArrayBuilder arrayToBuild;
		arrayToBuild = Json.createArrayBuilder();
		for(int[] i: pieces) {
			arrayToBuild.add(Json.createObjectBuilder()
						.add("x", i[0])
						.add("y", i[1])
						.add("isQueen", queens[a]));
			 
			a++;
		}
		
		return arrayToBuild.build();
	}
	
	public void setJsonObject(JsonObject jsonObject, String name) {
		this.jsonObject = jsonObject;
		this.name = name;
		dismantle();
	}
	
	public JsonObject getJsonObject() {
		assemble();
		return jsonObject;
	}

}
