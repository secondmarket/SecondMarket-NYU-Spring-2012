package com.secondmarket.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.importer.Importer;
import com.secondmarket.model.Company;

public class WikipediaUtils{

	private Gson gson;

	public WikipediaUtils() {
		gson = new Gson();
	}

	/**
	 * Calls the Wikipedia API and returns the data in a map
	 * 
	 * @param url
	 * @return Map<String, Object> map
	 */
	private static Map<String, String> getDataMapFromWikipedia(String url) {
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
//			map = (Map<String, String>) mapper.readValue(gson.toJson(map),
//					Map.class);
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
		return map;
	}

	private static List<Object> getDataListFromWikipedia(String url) {
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
			list = mapper.readValue(input, List.class);
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
	
	private static List<String> getPossibleTitles(Map<String, String> map){
		List<String> tmpList = new ArrayList<String>();
		if(map.containsKey("query")){
			Object entry =  map.get("query");
			if(entry instanceof Map){
				Map newEntry = (Map) entry;
				if(newEntry.containsKey("search")){
					Object searchItems = newEntry.get("search");
					if(searchItems instanceof List){
						for(Object item: (List<String>)searchItems){
							if(item instanceof Map){
								Map newItem = (Map) item;
								if(newItem.containsKey("title")){
									String title = (String) newItem.get("title");
								//	System.out.println(title);
									tmpList.add(title);
								}
							}
						}
					}
						
				}
			}
		}
		return tmpList;
	}
	
	public String getCompanyTitle(List<String> titleList){
		if(titleList.size() == 1)
			return titleList.get(0);
		for(String title: titleList){
			System.out.println(title);
	    	String pageUrl = "http://en.wikipedia.org/w/api.php?action=query&titles=" + title + "&prop=revisions&rvprop=content&format=json";
	    	Map<String, String> tmp = DataMapper.getDataInMapFromAPI(pageUrl);
	    	if(tmp.containsKey("query")){
	    		Object str = tmp.get("query");
	    		if(str instanceof Map){
	    			
	    			String str1 = gson.toJson(str);
	    			if(str1.contains("{{Infobox")){
	    				System.out.println("!!!!!!!!!!!!!");
	    				return title;
		    			
		    		}
	    		}
	    	}
		}
		return null;
	}
	
	public String findCompanyUrl(String name){
		String url = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" + name + "&srprop=timestamp&format=json";
		Map<String, String> result = DataMapper.getDataInMapFromAPI(url);
	    List<String> titleList = getPossibleTitles(result);
	    
	    String title = getCompanyTitle(titleList);
	    return title;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		WikipediaUtils dataImporter = new WikipediaUtils();
		String url = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=amazon&srprop=timestamp&format=json";
		System.out.println(getDataMapFromWikipedia(url));
	    List<String> titleList = getPossibleTitles(getDataMapFromWikipedia(url));
	    
	    String title = dataImporter.getCompanyTitle(titleList);
	    if(title != null){
	    	System.out.println(title);
	    }
	}
}