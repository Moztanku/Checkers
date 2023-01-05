package com.tp;

import static org.junit.Assert.assertEquals;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;

public class JsonWrapperTest {
	
	JsonObject obj;
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
	}
	
	@Test
	public void testJsonWrapper() {
		JsonWrapper wrapper = new JsonWrapper();
		
		wrapper.setJsonObject(obj, "Move");
		
		JsonObject newObj;
		newObj = wrapper.getJsonObject();
		JsonObject prep = (JsonObject) newObj.getJsonArray("jumped").get(0);
		int ha = prep.getInt("x");
		assertEquals(ha, 4);
	}

}
