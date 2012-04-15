package com.secondmarket.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jetty.util.URIUtil;

import com.google.gson.Gson;
import com.secondmarket.properties.SMProperties;

/***
 * 
 * @author Danjuan Ye
 * 
 */
public class WikipediaUtils {

	private Gson gson;
	private SMProperties p;

	public WikipediaUtils(SMProperties p) {
		gson = new Gson();
		this.p = p;
	}

	private String percentEncodeReservedCharacters(String title) {
		return URIUtil.encodePath(title);
	}

	/***
	 * Using Wikipedia Search API to get possible titles according to the
	 * specialized Crunchbase Company Name
	 * 
	 * @param companyName
	 * @return Possible titles
	 */
	@SuppressWarnings("unchecked")
	private List<String> getPossibleTitlesbySearch(String companyName) {

		List<String> tmpList = new ArrayList<String>();
		String url = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="
				+ this.percentEncodeReservedCharacters(companyName)
				+ "&srprop=timestamp&format=json";

		Map<String, String> titlesMap = DataMapper.getDataInMapFromAPI(url);

		if (titlesMap.containsKey("query")) {
			// System.out.println(titlesMap);
			Object entry = titlesMap.get("query");
			if (entry instanceof Map) {
				Map<String, String> newEntry = (Map<String, String>) entry;
				if (newEntry.containsKey("search")) {
					Object searchItems = newEntry.get("search");
					if (searchItems instanceof List) {
						for (Object item : (List<String>) searchItems) {
							if (item instanceof Map) {
								Map<String, String> newItem = (Map<String, String>) item;
								if (newItem.containsKey("title")) {
									String title = (String) newItem
											.get("title");
									// System.out.println(title);
									if (title.toLowerCase().charAt(0) == companyName
											.toLowerCase().charAt(0)
											&& compareTwoStrings(title,
													companyName)) {
										tmpList.add(title);
//										System.out.println("Filted titles -> "
//												+ title);
									}
								}
							}
						}
					}
				}
			}
		} else {
			// System.out.println("ERROR : " + titlesMap);
		}
		return tmpList;
	}

	/**
	 * Delete the no meaning characters in a string. For example: $sea son& ->
	 * season
	 * 
	 * @param str
	 * @return
	 */
	private String deleteNonMeaningChars(String str) {
		StringBuilder sb = new StringBuilder();
		str = str.toLowerCase();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) >= 'a' && str.charAt(i) <= 'z') {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	/***
	 * Compare two strings
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	private boolean compareTwoStrings(String str1, String str2) {
		String newStr1 = deleteNonMeaningChars(str1);
		String newStr2 = deleteNonMeaningChars(str2);
		if (newStr1.contains(newStr2) || newStr2.contains(newStr1)) {
			return true;
		}
		return false;
	}

	/***
	 * Get the most possible title for wikipedia Filter criteria: to check
	 * whether it contains infobox or not if the possible titles size is bigger
	 * than 1. if the size is 1, return it directly.
	 * 
	 * @param titleList
	 * @return
	 */
	public String getCompanyTitle(List<String> titleList) {

		if (titleList.size() == 1) {
			String trueTitle = percentEncodeReservedCharacters(titleList.get(0));
//			System.out.println("Wiki -> " + trueTitle);
			return trueTitle;
		}
		List<Pattern> patternList = getInfoboxPriorityPattern();
		patternList.addAll(getInfoboxPattern());
		for (Pattern pattern : patternList) {
			for (String title : titleList) {
				String newTitle = percentEncodeReservedCharacters(title);
				// System.out.println(title);
				String pageUrl = "http://en.wikipedia.org/w/api.php?action=query&titles="
						+ newTitle
						+ "&prop=revisions&rvprop=content&format=json";

				Map<String, String> tmpMap = DataMapper
						.getDataInMapFromAPI(pageUrl);

				if (tmpMap.containsKey("query")) {

					Object str = tmpMap.get("query");
					String str1 = gson.toJson(str).toLowerCase();

					if (checkPatternMatch(pattern, str1)) {

//						System.out.println("Wiki -> " + newTitle);
						return newTitle;
					}
				}
			}
		}

		return null;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PATTERN MATCH
	// /////////////////////////////////////////////////////////////////////////
	public List<Pattern> getInfoboxPattern() {
		return p.getValues("INFOBOX", "OPTIONS");
	}
	
	public List<Pattern> getInfoboxPriorityPattern() {
		return p.getValues("INFOBOX", "PRIORITYOPTIONS");
	}

	public Pattern getInfoboxSpecifiedPattern() {
		Pattern myPattern = null;
		try {
			myPattern = p.getValue("INFOBOX", "OPTIONS", "OPTION1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myPattern;
	}

	public static boolean checkPatternMatch(Pattern myPattern, String text) {
		return myPattern.matcher(text).matches();
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

		SMProperties wikiProperty = null;
		try {
			wikiProperty = SMProperties.getInstance("WIKI");

		} catch (Exception e) {
			e.printStackTrace();
		}

		WikipediaUtils dataImporter = new WikipediaUtils(wikiProperty);
		List<String> titleList = dataImporter
				.getPossibleTitlesbySearch("Hyphen 8");
		String title = dataImporter.getCompanyTitle(titleList);

		if (title != null) {
			// System.out.println(title + "      &&&&&&&&&&&&&");
		} else {
			// System.out.println("NULL");
		}

		// System.out.print(dataImporter
		// .percentEncodeReservedCharacters("@main page!$"));
		// String pageUrl =
		// "http://en.wikipedia.org/w/api.php?action=query&titles=amazon@&prop=revisions&rvprop=content&format=json";
		//
		// Map<String, String> tmpMap = DataMapper.getDataInMapFromAPI(pageUrl);
		// System.out.println(tmpMap);
	}
}
