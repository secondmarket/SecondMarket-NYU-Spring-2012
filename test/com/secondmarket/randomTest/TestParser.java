package com.secondmarket.randomTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class TestParser {

	public void testParser() {
//		 String url = "http://api.crunchbase.com/v/1/company/facebook.js";
//		String url = "http://api.crunchbase.com/v/1/companies/permalink?name=Google";
		 String url =
		 "http://api.crunchbase.com/v/1/person/mark-zuckerberg.js";

		URL resource = null;
		try {
			resource = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();

		mapper.getDeserializationConfig().withDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));

		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		InputStream input = null;
		try {
			try {
				input = resource.openStream();
				Map<String, Object> map = (Map<String, Object>) mapper
						.readValue(input, Map.class);

				
				DBObject dbObject = (DBObject) JSON.parse(new Gson().toJson(map));
				System.out.println(dbObject);

			} finally {
				if (input != null) {
					input.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public Map<String, Object> formatMap(Map<String, Object> map){
		Map<String, Object> newMap = new HashMap<String, Object>();
		String newKey = "";
		String newValue = "";
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			newKey = "\"" + entry.getKey() + "\"";
			newValue = "\"" + entry.getValue() + "\"";
			newMap.put(newKey, (Object) newValue);
		}
		return newMap;
	}

	public static void main(String[] args) {
		TestParser test = new TestParser();
		test.testParser();
	}

}
