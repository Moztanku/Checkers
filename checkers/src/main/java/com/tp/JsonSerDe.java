package com.tp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/*
 * Used to handle serialization and deserialization of Objects.
 */
public class JsonSerDe {

	static String fileName = "json.ser";
	
	public static void serialize(JsonWrapper json, ObjectOutputStream out) throws Exception {
		
		//FileOutputStream file = new FileOutputStream(fileName);
		//ObjectOutputStream out = new ObjectOutputStream(file);
		
		out.writeObject(json);
		
		//file.close();
		//out.close();
	}
	
	public static JsonWrapper deserialize(ObjectInputStream in) throws Exception {
		
		//FileInputStream file = new FileInputStream(fileName);
		//ObjectInputStream in = new ObjectInputStream(file);
		
		JsonWrapper json = (JsonWrapper) in.readObject();
		
		//file.close();
		//in.close();
		
		return json;
	}
	
	
	
}
