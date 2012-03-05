package com.secondmarket.randomTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class CrunchBaseAPICallTest {

	public void testParser() throws Exception {

		String url = "http://api.crunchbase.com/v/1/companies/permalink?name=Google";
		URL facebook = new URL(url);
		URLConnection fb = facebook.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				fb.getInputStream()));

		ObjectMapper mapper = new ObjectMapper();

		mapper.getDeserializationConfig().withDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));

		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		Map<String, Object> map = (Map<String, Object>) mapper.readValue(in,
				Map.class);
		
		String escapedString = StringEscapeUtils.unescapeHtml(map.toString());

		DBObject dbObject = (DBObject) JSON.parse(escapedString);
//		DBObject dbObject = (DBObject) JSON.parse(map.toString());

		System.out.println(dbObject);
		in.close();
	}

	public static void main(String[] args) {
		CrunchBaseAPICallTest test = new CrunchBaseAPICallTest();
		try {
			test.testParser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
