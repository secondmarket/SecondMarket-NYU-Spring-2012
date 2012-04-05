package com.secondmarket.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.secondmarket.model.edgar.EdgarCompanyTitle;
import com.secondmarket.model.edgar.EdgarCompanyDetail;
import com.secondmarket.model.edgar.EdgarDoc;

/***
 * 
 * @author Danjuan Ye
 * 
 */
public class EdgarUtils {

	public static final String preUrl = "http://www.sec.gov";

	public EdgarUtils() {
	}

	private List<EdgarCompanyDetail> getDocumentList(String companyName, String state) {

		// get company link first
		List<EdgarCompanyTitle> tmpList = new ArrayList<EdgarCompanyTitle>();
		List<EdgarCompanyDetail> detailList = new ArrayList<EdgarCompanyDetail>();
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
						EdgarCompanyTitle item = getCompanyTitlebySearch(tds);
						flag = true;
						if (item != null)
							tmpList.add(item);
					} else if (tds.size() == 5) {
						detailList.add(getCompanyDetailDocListbySearch(tds));
					}
				}
			}
			if (flag && tmpList.size() > 0) {
				EdgarCompanyTitle firstItem = tmpList.get(0);
				String url2 = preUrl + firstItem.getCompanyLink();
				System.out.println(url2);
				Document doc2;
				doc2 = Jsoup.connect(url2).get();
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
		setEdgarDoc(detailList);
		return detailList;
	}
	
	public void setEdgarDoc(List<EdgarCompanyDetail> list){
		for(EdgarCompanyDetail entry : list){
			String url = preUrl + entry.getFormatLink();
			System.out.println(url);
			Document doc;
			try {
				doc = Jsoup.connect(url).get();
				System.out.println("Title: " + doc.title());
				for (Element table : doc.select("table.tableFile")) {
					for (Element row : table.select("tr")) {
						Elements tds = row.select("td");
						if (tds.size() == 5) {
							entry.addDocToList(getDocsbyCompany(tds));
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private EdgarDoc getDocsbyCompany(Elements tds) {

		EdgarDoc item = null;
		item = new EdgarDoc();
		
		System.out.println(tds.get(0).ownText());
		System.out.println(tds.get(1).ownText());
		System.out.println(tds.get(2).getElementsByTag("a").get(0).ownText());
		System.out.println(tds.get(2).getElementsByTag("a").get(0).attr("href"));
		System.out.println(tds.get(3).ownText());
		System.out.println(tds.get(4).ownText());
		
		item.setSeq(tds.get(0).ownText());
		item.setDescr(tds.get(1).ownText());
		item.setDocName(tds.get(2).getElementsByTag("a").get(0).ownText());
		item.setDocLink(tds.get(2).getElementsByTag("a").get(0).attr("href"));
		item.setType(tds.get(3).ownText());
		item.setSize(tds.get(4).ownText());
		return item;
	}

	private EdgarCompanyDetail getCompanyDetailDocListbySearch(Elements tds) {

		EdgarCompanyDetail item = null;
		item = new EdgarCompanyDetail();
//		System.out.println(tds.get(0).ownText());
//		System.out.println(tds.get(1).getElementsByTag("a").get(0).attr("href")
//				+ ":   " + tds.get(1).getElementsByTag("a").get(0).ownText());
//		System.out.println(tds.get(2).text());
//		System.out.println(tds.get(3).text());
		if (tds.get(4).hasText()) {
//			System.out.println(tds.get(4).text() + ":"
//					+ tds.get(4).getElementsByTag("a").get(0).attr("href"));
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

	private EdgarCompanyTitle getCompanyTitlebySearch(Elements tds) {

		EdgarCompanyTitle item = null;
		if (tds.get(1).getElementsByTag("a").size() != 0) {
			// filter the company do not have a SIC
//			System.out.println(tds.get(0).text() + ":"
//					+ tds.get(0).getElementsByTag("a").get(0).attr("href"));
//			System.out.println(tds.get(1).ownText() + ":"
//					+ tds.get(1).getElementsByTag("a").get(0).attr("href")
//					+ ":   "
//					+ tds.get(1).getElementsByTag("a").get(0).ownText());
//			System.out.println(tds.get(2).text());

			item = new EdgarCompanyTitle();
			item.setCompanyLink(tds.get(0).getElementsByTag("a").get(0).attr("href"));
			item.setCompanyName(tds.get(1).ownText());
			item.setSICNum(tds.get(1).getElementsByTag("a").get(0).ownText());
			item.setSICLink(tds.get(1).getElementsByTag("a").get(0).attr("href"));
			item.setLocation(tds.get(2).text());
			item.setLocationLink(tds.get(2).getElementsByTag("a").get(0).attr("href"));
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

	/**
	 * Get the Edgar Doc according the crunchbase CompanyName and State 
	 * 
	 * @param companyName
	 * @param state
	 * @return
	 */
//	 public String findEdgarDoc(String companyName, String state) {
//	
//	 // Get the possible page titles by search
//	 // System.out.println(companyName);
//	 List<DetailList> titleList = getDocumentList(companyName, state);
//	 // String title = getCompanyTitle(titleList);
//	
//	 return title;
//	 }

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		EdgarUtils dataImporter = new EdgarUtils();
		List<EdgarCompanyDetail> titleList = dataImporter.getDocumentList("google",
				"CA");
	}
}
