package com.secondmarket.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.URIUtil;

import com.google.gson.Gson;

public class WikipediaUtils {

	private Gson gson;

	public WikipediaUtils() {
		gson = new Gson();
	}

	private String percentEncodeReservedCharacters(String title) {
		return URIUtil.encodePath(title);
	}

	private List<String> getPossibleTitlesbySearch(String companyName) {

		List<String> tmpList = new ArrayList<String>();
		String url = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch="
				+ companyName + "&srprop=timestamp&format=json";

		Map<String, String> titlesMap = DataMapper.getDataInMapFromAPI(url);

		if (titlesMap.containsKey("query")) {
			Object entry = titlesMap.get("query");
			if (entry instanceof Map) {
				Map newEntry = (Map) entry;
				if (newEntry.containsKey("search")) {
					Object searchItems = newEntry.get("search");
					if (searchItems instanceof List) {
						for (Object item : (List<String>) searchItems) {
							if (item instanceof Map) {
								Map newItem = (Map) item;
								if (newItem.containsKey("title")) {
									String title = (String) newItem
											.get("title");
									// System.out.println(title);
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

	public String getCompanyTitle(List<String> titleList) {

		if (titleList.size() == 1)
			return titleList.get(0);
		for (String title : titleList) {
			String newTitle = percentEncodeReservedCharacters(title);
			// System.out.println(title);
			String pageUrl = "http://en.wikipedia.org/w/api.php?action=query&titles="
					+ newTitle + "&prop=revisions&rvprop=content&format=json";

			Map<String, String> tmpMap = DataMapper
					.getDataInMapFromAPI(pageUrl);

			if (tmpMap.containsKey("query")) {

				Object str = tmpMap.get("query");
				String str1 = gson.toJson(str);
				if (str1.contains("{{Infobox")) {
					System.out.println("Wiki -> " + newTitle);
					return newTitle;
				}

			}
		}
		return null;
	}

	public String findCompanyUrl(String companyName) {

		// Get the possible page titles by search
		List<String> titleList = getPossibleTitlesbySearch(companyName);
		String title = getCompanyTitle(titleList);

		return title;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		WikipediaUtils dataImporter = new WikipediaUtils();

		List<String> titleList = dataImporter
				.getPossibleTitlesbySearch("amazon");
		String title = dataImporter.getCompanyTitle(titleList);

		if (title != null) {
			System.out.println(title);
		} else {
			System.out.println("NULL");
		}

		System.out.print(dataImporter
				.percentEncodeReservedCharacters("@main page!$"));
		String pageUrl = "http://en.wikipedia.org/w/api.php?action=query&titles=amazon@&prop=revisions&rvprop=content&format=json";

		Map<String, String> tmpMap = DataMapper.getDataInMapFromAPI(pageUrl);
		System.out.println(tmpMap);
	}
}