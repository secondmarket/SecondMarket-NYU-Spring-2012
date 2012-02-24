package com.secondmarket.importer;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class CrunchBaseImporterTest {

	public static void main(String[] args) throws Exception {
		String url = "http://api.crunchbase.com/v/1/company/facebook.js";
		URL resource = new URL(url);
		ObjectMapper mapper = new ObjectMapper();

		/*mapper.getDeserializationConfig().setDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));*/
		
		mapper.getDeserializationConfig().withDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));

		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		InputStream input = null;
		try {
			input = resource.openStream();
			Map<String, Object> map = (Map<String, Object>) mapper.readValue(
					input, Map.class);
			System.out.println(map);
		} finally {
			if (input != null)
				input.close();
		}
	}
}
