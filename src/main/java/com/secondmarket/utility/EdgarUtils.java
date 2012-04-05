package com.secondmarket.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.secondmarket.model.edgar.CompanyList;
import com.secondmarket.model.edgar.DetailList;

/***
 * 
 * @author Danjuan Ye
 * 
 */
public class EdgarUtils {

	public static final String preUrl = "http://www.sec.gov";
	private Gson gson;

	public EdgarUtils() {
		gson = new Gson();
	}

	private List<DetailList> getDocumentList(String companyName, String state) {

		// get company link first
		List<CompanyList> tmpList = new ArrayList<CompanyList>();
		List<DetailList> detailList = new ArrayList<DetailList>();
		String url = "http://www.sec.gov/cgi-bin/browse-edgar?company="
				+ companyName
				+ "&match=&CIK=&filenum=&State="
				+ state
				+ "&Country=&SIC=&owner=exclude&Find=Find+Companies&action=getcompany";
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			String title = doc.title();
			System.out.println("Title: " + title);
			boolean flag = false;
			for (Element table : doc.select("table.tableFile2")) {
				for (Element row : table.select("tr")) {
					Elements tds = row.select("td");
					if (tds.size() == 3) {
						CompanyList item = getCompanyTitlebySearch(tds);
						flag = true;
						if (item != null)
							tmpList.add(item);
					} else if (tds.size() == 5) {
						detailList.add(getCompanyDetailDocListbySearch(tds));
					}
				}
			}
			if (flag && tmpList.size() > 0) {
				CompanyList firstItem = tmpList.get(0);
				String url2 = preUrl + firstItem.getCompanyLink();
				System.out.println(url2);
				Document doc2;
				doc2 = Jsoup.connect(url2).get();
				System.out.println("Title: " + doc.title());
				for (Element table : doc2.select("table.tableFile2")) {
					for (Element row : table.select("tr")) {
						Elements tds = row.select("td");
						if (tds.size() == 5) {
							detailList.add(getCompanyDetailDocListbySearch(tds));
						}
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return detailList;
	}

	private DetailList getCompanyDetailDocListbySearch(Elements tds) {

		DetailList item = null;
		item = new DetailList();
		System.out.println(tds.get(0).ownText());
		System.out.println(tds.get(1).getElementsByTag("a").get(0).attr("href")
				+ ":   " + tds.get(1).getElementsByTag("a").get(0).ownText());
		System.out.println(tds.get(2).text());
		System.out.println(tds.get(3).text());
		if (tds.get(4).hasText()) {
			System.out.println(tds.get(4).text() + ":"
					+ tds.get(4).getElementsByTag("a").get(0).attr("href"));
			item.setFileNum(tds.get(4).text());
			item.setFileNumLink(tds.get(4).getElementsByTag("a").get(0)
					.attr("href"));
		}

		item.setFilings(tds.get(0).ownText());
		item.setFormat(tds.get(1).getElementsByTag("a").get(0).ownText());
		item.setFormatLink(tds.get(1).getElementsByTag("a").get(0).attr("href"));
		item.setDescr(tds.get(2).text());
		item.setFileDate(tds.get(3).text());

		return item;
	}

	private CompanyList getCompanyTitlebySearch(Elements tds) {

		CompanyList item = null;
		if (tds.get(1).getElementsByTag("a").size() != 0) {
			// filter the company do not have a SIC
			System.out.println(tds.get(0).text() + ":"
					+ tds.get(0).getElementsByTag("a").get(0).attr("href"));
			System.out.println(tds.get(1).ownText() + ":"
					+ tds.get(1).getElementsByTag("a").get(0).attr("href")
					+ ":   "
					+ tds.get(1).getElementsByTag("a").get(0).ownText());
			System.out.println(tds.get(2).text());

			item = new CompanyList();
			item.setCompanyLink(tds.get(0).getElementsByTag("a").get(0)
					.attr("href"));
			item.setCompanyName(tds.get(1).ownText());
			item.setSICNum(tds.get(1).getElementsByTag("a").get(0).ownText());
			item.setSICLink(tds.get(1).getElementsByTag("a").get(0)
					.attr("href"));
			item.setLocation(tds.get(2).text());
			item.setLocationLink(tds.get(2).getElementsByTag("a").get(0)
					.attr("href"));
		}
		return item;
	}

	// URL resource = null;
	// try {
	// resource = new URL(url);
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	//
	// InputStream input = null;
	//
	// try {
	// input = resource.openStream();
	// InputStreamReader isr;
	// try {
	// isr = new InputStreamReader(input, "UTF-8");
	// BufferedReader br = new BufferedReader(isr);
	// String strTemp = "";
	// while(null != (strTemp = br.readLine())){
	// System.out.println(strTemp);
	// }
	//
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// if (input != null) {
	// try {
	// input.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

//	public String getCompanyTitle(List<String> titleList) {
//
//		if (titleList.size() == 1) {
//			String trueTitle = percentEncodeReservedCharacters(titleList.get(0));
//			System.out.println("Wiki -> " + trueTitle);
//			return trueTitle;
//		}
//		// List<String> patternList = getInfoboxPattern();
//		for (String title : titleList) {
//			String newTitle = percentEncodeReservedCharacters(title);
//			// System.out.println(title);
//			String pageUrl = "http://en.wikipedia.org/w/api.php?action=query&titles="
//					+ newTitle + "&prop=revisions&rvprop=content&format=json";
//
//			Map<String, String> tmpMap = DataMapper
//					.getDataInMapFromAPI(pageUrl);
//		}
//		return null;
//	}

	/**
	 * Get the Wikipedia URL according the cruchbase URL
	 * 
	 * @param companyName
	 * @return
	 */
	// public String findCompanyUrl(String companyName, String state) {
	//
	// // Get the possible page titles by search
	// // System.out.println(companyName);
	// List<DetailList> titleList = getDocumentList(companyName, state);
	// // String title = getCompanyTitle(titleList);
	//
	// return title;
	// }

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		EdgarUtils dataImporter = new EdgarUtils();
		List<DetailList> titleList = dataImporter.getDocumentList("google",
				"CA");
		// String title = dataImporter.getCompanyTitle(titleList);
	}
}
