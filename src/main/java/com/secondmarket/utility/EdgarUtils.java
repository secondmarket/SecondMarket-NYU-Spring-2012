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

		if(companyName.length() < 2){
			return null;
		}
		// get company link first
		List<EdgarDocDetail> detailList = new ArrayList<EdgarDocDetail>();
		List<EdgarDocDetail> multiDocList;
		List<EdgarCompanyDetail> nameList = new ArrayList<EdgarCompanyDetail>();
		EdgarCompanyDetail item;
		EdgarDocDetail temp;
		String url2;
		Elements tds;

		String url = "http://www.sec.gov/cgi-bin/browse-edgar?company="
				+ companyName
				+ "&match=contains&CIK=&filenum=&State="
				+ state
				+ "&Country=&SIC=&owner=exclude&Find=Find+Companies&action=getcompany";
	//	System.out.println(url);
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			for (Element table : doc.select("table.tableFile2")) {
				for (Element row : table.select("tr")) {
					tds = row.select("td");
					if (tds.size() == 3) {
						item = getCompanyTitlebySearch(tds);
						if (item != null)
							nameList.add(item);
					} else if (tds.size() == 5) {
						temp = getCompanyDetailDocListbySearch(tds);
						if (temp != null) {
							detailList.add(temp);
						}
					}
				}
			}
			if (nameList.size() > 0) {
			//	System.out.println("--------Multiple Company Names --------");
				for (EdgarCompanyDetail name : nameList) {
					url2 = preUrl + name.getCompanyLink();
					Document doc2;
					doc2 = Jsoup.connect(url2).get();
					multiDocList = new ArrayList<EdgarDocDetail>();
					for (Element table : doc2.select("table.tableFile2")) {
						for (Element row : table.select("tr")) {
							tds = row.select("td");
							if (tds.size() == 5) {
								temp = getCompanyDetailDocListbySearch(tds);
								if (temp != null) {
									multiDocList.add(temp);
								}
							}
						}
					}
					name.setDetailList(multiDocList);
				}
			} else {
				//System.out.println("--------One Company Names --------");
				if (detailList.size() > 0) {
					item = new EdgarCompanyDetail();
					item.setCompanyName(companyName);
					item.setCompanyLink(url);
					item.setLocation(state);
					item.setDetailList(detailList);
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
	 * 
	 * @param nameList
	 * @return
	 */
	public Map<String, EdgarCompanyDetail> setEdgarFilingDetail(
			List<EdgarCompanyDetail> nameList) {
		
		Map<String, EdgarCompanyDetail> map = new TreeMap<String, EdgarCompanyDetail>();
		List<EdgarFilingDetail> filingList;
		List<EdgarDocDetail> removedList = new ArrayList<EdgarDocDetail>();
		int companyNum = 1;
		int docNum = 1;
		int rmNum = 1;
		String url;
		boolean flag;
		EdgarFilingDetail item;
		Elements tds;
		
		for (EdgarCompanyDetail name : nameList) {
//			System.out.println("COMPANY " + (companyNum++) + " -> "
//					+ name.getCompanyName() + " -- " + name.getLocation());
			docNum = 1;
			flag = false;
			for (EdgarDocDetail entry : name.getDetailList()) {
//				System.out.println("DOC " + (docNum++) + " "
//						+ entry.getFilings() + " " + entry.getFileDate());
				url = preUrl + entry.getFormatLink();
				filingList = new ArrayList<EdgarFilingDetail>();
				Document doc;
				try {
					doc = Jsoup.connect(url).get();
					for (Element table : doc.select("table.tableFile")) {
						for (Element row : table.select("tr")) {
							tds = row.select("td");
							if (tds.size() == 5) {
								item = getDocsbyCompany(tds);
								if (item != null) {
									filingList.add(item);
									flag = true;
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				entry.setDocList(filingList);
				if(!flag){
					removedList.add(entry);
				}
			}
			
			if(removedList.size()>0){
				rmNum = 1;
				//removed the EdgarDocs do not have filing details
				for(EdgarDocDetail rmItem: removedList){
					name.getDetailList().remove(rmItem);
//					System.out.println("REMOVED " + (rmNum++) + " "
//							+ rmItem.getFilings() + " " + rmItem.getFileDate());
				}
			}
			removedList.clear();
			if (!map.containsKey(name.getCompanyName())&&name.getDetailList().size()>0 && flag) {
	//			System.out.println("PUT INTO MAP : "+name.getCompanyName().replace('.', '#'));
				map.put(name.getCompanyName().replace('.', '#'), name);
			}
		}
		return map;
	}
	

	/***
	 * Get the filing detail info for the specified the doc type.
	 * 
	 * @param tds
	 * @return
	 */
	private EdgarFilingDetail getDocsbyCompany(Elements tds) {

		EdgarFilingDetail item = null;

		String filingName = tds.get(2).getElementsByTag("a").get(0).ownText()
				.toLowerCase();
		if (filingName.endsWith(".html") || filingName.endsWith(".pdf") || filingName.endsWith(".htm")) { 
			
			item = new EdgarFilingDetail();

			item.setSeq(tds.get(0).ownText());
			item.setDescr(tds.get(1).ownText());
			item.setDocName(tds.get(2).getElementsByTag("a").get(0).ownText());
			item.setDocLink(tds.get(2).getElementsByTag("a").get(0)
					.attr("href"));
			item.setType(tds.get(3).ownText());
			item.setSize(tds.get(4).ownText());
			// for test
//			System.out.println("SEQ " + tds.get(0).ownText() + " -> "
//					+ tds.get(2).getElementsByTag("a").get(0).ownText());
		}
//		else{
//		System.out.println("----SEQ " + tds.get(0).ownText() + " -> "
//				+ tds.get(2).getElementsByTag("a").get(0).ownText());
//		}
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
		if (tds.get(1).getElementsByTag("a").size() == 0
				&& !tds.get(2).text().equals("")) {

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

	//web crawler
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
		Map<String, EdgarCompanyDetail> titleList = dataImporter.getEdgarDoc(
				"Geni", "CA");
	}
}
