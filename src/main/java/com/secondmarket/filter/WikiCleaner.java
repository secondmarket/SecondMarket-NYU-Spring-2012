package com.secondmarket.filter;

import java.util.Map;

import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.properties.SMProperties;
import com.secondmarket.utility.DataMapper;

/***
 * 
 * @author Danjuan Ye
 * This class is for view the wikipedia markups without running the jsp for one company.
 *
 */
public class WikiCleaner {

	public CompanyDAO companyDao;
	private SMProperties wikiProperty;

	public WikiCleaner() {
		try {
			wikiProperty = SMProperties.getInstance("WIKI");

		} catch (Exception e) {
			e.printStackTrace();
		}
		companyDao = new CompanyDAOImpl(wikiProperty);
	}

	public static void main(String[] args) {
		String title = "Lotame";
		String url_Wikipedia = "http://en.wikipedia.org/w/api.php?action=query&titles="
				+ title + "&prop=revisions&rvprop=content&format=json";

		Map<String,String> wikipediaDoc = DataMapper.getDataInMapFromAPI(url_Wikipedia);

		WikiCleaner test = new WikiCleaner();
	    test.companyDao.saveCompany("Lotame",
			null, wikipediaDoc, null, url_Wikipedia, 1);
	}

}
