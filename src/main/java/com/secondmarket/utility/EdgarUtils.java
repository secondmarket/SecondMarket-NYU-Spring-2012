package com.secondmarket.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.secondmarket.model.EdgarCompanyDetail;
import com.secondmarket.model.EdgarDocDetail;
import com.secondmarket.model.EdgarFilingDetail;

/***
 * 
 * @author Danjuan Ye
 * 
 */
public class EdgarUtils {

	public static final String preUrl = "http://www.sec.gov";

	public EdgarUtils() {
	}

	/**
	 * Get the Edgar Doc according the crunchbase CompanyName and State
	 * 
	 * @param companyName
	 * @param state
	 * @return
	 */
	public Map<String, EdgarCompanyDetail> getEdgarDoc(String companyName,
			String state) {

		// get company link first
		List<EdgarDocDetail> detailList = new ArrayList<EdgarDocDetail>();
		List<EdgarCompanyDetail> nameList = new ArrayList<EdgarCompanyDetail>();

		String url = "http://www.sec.gov/cgi-bin/browse-edgar?company="
				+ companyName
				+ "&match=&CIK=&filenum=&State="
				+ state
				+ "&Country=&SIC=&owner=exclude&Find=Find+Companies&action=getcompany";
		Document doc;
		try {
			doc = Jsoup.connect(url).get();

			for (Element table : doc.select("table.tableFile2")) {
				for (Element row : table.select("tr")) {
					Elements tds = row.select("td");
					if (tds.size() == 3) {
						EdgarCompanyDetail item = getCompanyTitlebySearch(tds);
						if (item != null)
							nameList.add(item);
					} else if (tds.size() == 5) {
						detailList.add(getCompanyDetailDocListbySearch(tds));
					}
				}
			}
			if (nameList.size() > 0) {
				System.out.println("--------Multiple Company Names --------");
				for (EdgarCompanyDetail name : nameList) {
					String url2 = preUrl + name.getCompanyLink();
					Document doc2;
					doc2 = Jsoup.connect(url2).get();
					for (Element table : doc2.select("table.tableFile2")) {
						for (Element row : table.select("tr")) {
							Elements tds = row.select("td");
							if (tds.size() == 5) {
								name.addDetail(getCompanyDetailDocListbySearch(tds));
							}
						}
					}
				}
			} else {
				System.out.println("--------One Company Names --------");
				if (detailList.size() > 0) {
					EdgarCompanyDetail item = new EdgarCompanyDetail();
					item.setCompanyName(companyName);
					item.setCompanyLink(url);
					item.setLocation(state);
					item.addDetailList(detailList);
					nameList.add(item);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return setEdgarFilingDetail(nameList);
	}

	/***
	 * Set the filing detail info for the specified the doc type.
	 * @param nameList
	 * @return
	 */
	public Map<String, EdgarCompanyDetail> setEdgarFilingDetail(
			List<EdgarCompanyDetail> nameList) {
		Map<String, EdgarCompanyDetail> map = new TreeMap<String, EdgarCompanyDetail>();
		int companyNum = 1;
		int docNum = 1;
		for (EdgarCompanyDetail name : nameList) {
			System.out.println("COMPANY "+(companyNum++) +" -> " + name.getCompanyName() + " -- " + name.getLocation());
			docNum = 1;
			for (EdgarDocDetail entry : name.getDetailList()) {
				System.out.println("DOC " +(docNum++) + " " + entry.getFilings() + " " + entry.getFileDate());
				String url = preUrl + entry.getFormatLink();
				Document doc;
				try {
					doc = Jsoup.connect(url).get();
					
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
			if (!map.containsKey(name.getCompanyName())) {
				map.put(name.getCompanyName(), name);
			}
		}
		return map;
	}

	/***
	 * Get the filing detail info for the specified the doc type.
	 * @param tds
	 * @return
	 */
	private EdgarFilingDetail getDocsbyCompany(Elements tds) {

		EdgarFilingDetail item = null;

		String filingName = tds.get(2).getElementsByTag("a").get(0).ownText().toLowerCase();
		if (filingName.endsWith(".html") || filingName.endsWith(".pdf")) { // only get the html files
			item = new EdgarFilingDetail();

			item.setSeq(tds.get(0).ownText());
			item.setDescr(tds.get(1).ownText());
			item.setDocName(tds.get(2).getElementsByTag("a").get(0).ownText());
			item.setDocLink(tds.get(2).getElementsByTag("a").get(0)
					.attr("href"));
			item.setType(tds.get(3).ownText());
			item.setSize(tds.get(4).ownText());
			//for test
			System.out.println("SEQ " + tds.get(0).ownText() +" -> "+tds.get(2).getElementsByTag("a").get(0).ownText());
		}
		System.out.println("----SEQ " + tds.get(0).ownText() +" -> "+tds.get(2).getElementsByTag("a").get(0).ownText());
		return item;
	}

	/***
	 * 
	 * @param tds
	 * @return
	 */
	private EdgarDocDetail getCompanyDetailDocListbySearch(Elements tds) {

		EdgarDocDetail item = null;
		item = new EdgarDocDetail();
		
		if (tds.get(4).hasText()) {
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

	/***
	 * 
	 */
	private EdgarCompanyDetail getCompanyTitlebySearch(Elements tds) {

		EdgarCompanyDetail item = null;
		if (tds.get(1).getElementsByTag("a").size() == 0) {
			
			// looking for the company do not have a SIC (private company)
			item = new EdgarCompanyDetail();
			item.setCompanyLink(tds.get(0).getElementsByTag("a").get(0)
					.attr("href"));
			item.setCompanyName(tds.get(1).ownText());
			// item.setSICNum(tds.get(1).getElementsByTag("a").get(0).ownText());
			// item.setSICLink(tds.get(1).getElementsByTag("a").get(0)
			// .attr("href"));
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

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// foursquare, secondMarket
		EdgarUtils dataImporter = new EdgarUtils();
		Map<String, EdgarCompanyDetail> titleList = dataImporter
				.getEdgarDoc("SecondMarket", "NY");
	}
}
