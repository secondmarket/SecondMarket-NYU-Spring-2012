package com.secondmarket.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.URIUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

/***
 * 
 * @author Danjuan Ye
 * 
 */
public class EdgarUtils {

	private Gson gson;

	public EdgarUtils() {
		gson = new Gson();
	}

	private String percentEncodeReservedCharacters(String title) {
		return URIUtil.encodePath(title);
	}

	private List<String> getPossibleTitlesbySearch(String companyName) {

		List<String> tmpList = new ArrayList<String>();
		String url = "http://www.sec.gov/cgi-bin/browse-edgar?company="
				+ companyName
				+ "&match=&CIK=&filenum=&State=&Country=&SIC=&owner=exclude&Find=Find+Companies&action=getcompany";

		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			String title = doc.title();
			System.out.println("Title: " + title);
			for (Element table : doc.select("table.tablehead")) {
		        for (Element row : table.select("tr")) {
		            Elements tds = row.select("td");
		            if (tds.size() > 6) {
		                System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
		            }
		        }
		    }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
//		URL resource = null;
//		try {
//			resource = new URL(url);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//
//		InputStream input = null;
//
//		try {
//			input = resource.openStream();
//			InputStreamReader isr;
//			try {
//				isr = new InputStreamReader(input, "UTF-8");
//				BufferedReader br = new BufferedReader(isr);
//				String strTemp = "";
//				while(null != (strTemp = br.readLine())){
//					System.out.println(strTemp);
//				}
//
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (input != null) {
//				try {
//					input.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}

		
	public String getCompanyTitle(List<String> titleList) {

		if (titleList.size() == 1) {
			String trueTitle = percentEncodeReservedCharacters(titleList.get(0));
			System.out.println("Wiki -> " + trueTitle);
			return trueTitle;
		}
		// List<String> patternList = getInfoboxPattern();
		for (String title : titleList) {
			String newTitle = percentEncodeReservedCharacters(title);
			// System.out.println(title);
			String pageUrl = "http://en.wikipedia.org/w/api.php?action=query&titles="
					+ newTitle + "&prop=revisions&rvprop=content&format=json";

			Map<String, String> tmpMap = DataMapper
					.getDataInMapFromAPI(pageUrl);
		}
		return null;
	}

	/**
	 * Get the Wikipedia URL according the cruchbase URL
	 * 
	 * @param companyName
	 * @return
	 */
	public String findCompanyUrl(String companyName) {

		// Get the possible page titles by search
		// System.out.println(companyName);
		List<String> titleList = getPossibleTitlesbySearch(companyName);
		String title = getCompanyTitle(titleList);

		return title;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		EdgarUtils dataImporter = new EdgarUtils();
		List<String> titleList = dataImporter
				.getPossibleTitlesbySearch("amazon");
		// String title = dataImporter.getCompanyTitle(titleList);
	}
}
