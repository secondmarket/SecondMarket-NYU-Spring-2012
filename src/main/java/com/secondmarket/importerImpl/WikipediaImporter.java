package com.secondmarket.importerImpl;

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
import com.secondmarket.utility.DataFilter;

public class WikipediaImporter{

	private CompanyDAO orgDao;
	private Gson gson;

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

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String url = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=amazon&srprop=timestamp&format=json";
		System.out.println(getDataMapFromWikipedia(url));
	    for(String str: getPossibleTitles(getDataMapFromWikipedia(url))){
	    	System.out.println(str);
	    }
	    
	    String url2 = "http://en.wikipedia.org/w/api.php?action=query&titles=SecondMarket&prop=revisions&rvprop=content&format=json";
	    Map<String, String> tmp = getDataMapFromWikipedia(url2);
	 //   System.out.println(tmp);
	    Iterator<String> it = tmp.keySet().iterator();
	    while(it.hasNext()){
	    	System.out.println(it.next());
	    	Object entry = tmp.get("query");
	    	if(entry instanceof Map){
	    		Map newEntry = (Map) entry;
	    		if(newEntry.containsKey("infobox")){
	    			System.out.print("YES");
	    		}
	    		Object pages = newEntry.get("pages");
	    		Map newPages = (Map) pages;
	    		
	    //		Object a1 = newEntry.get("27749988");
	    //		Map a2 = (Map) a1;
	    		
	    	}
	    }
	    
	}
}