package com.secondmarket.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

public final class DataMapper {
	private static Gson gson = new Gson();

	// private constructor to avoid unnecessary instantiation
	private DataMapper() {
	}

	/**
	 * Calls the CrunchBase API and returns the data in a map
	 * 
	 * @param url
	 * @return Map<String, Object> map
	 */
	public static Map<String, String> getDataInMapFromAPI(String url) {
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
		Map<String, String> map = null;

		try {
			input = resource.openStream();
			InputStreamReader isr = new InputStreamReader(input, "UTF-8");
			BufferedReader bd = new BufferedReader(isr);

			map = (Map<String, String>) mapper.readValue(bd, Map.class);
			// Use gson to format the data
			map = (Map<String, String>) mapper.readValue(gson.toJson(map),
					Map.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException fnfe){
			return null;
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	/**
	 * Calls the CrunchBase API and returns the data in a list
	 * 
	 * @param url
	 * @return List<Object> list
	 */
	public static List<Object> getDataInListFromAPI(String url) {
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
		List<Object> list = null;

		try {
			input = resource.openStream();
			InputStreamReader isr = new InputStreamReader(input, "UTF-8");
			BufferedReader bd = new BufferedReader(isr);
			list = mapper.readValue(bd, List.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
