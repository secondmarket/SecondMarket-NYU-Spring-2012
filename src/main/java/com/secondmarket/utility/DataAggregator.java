package com.secondmarket.utility;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.secondmarket.filter.CrunchBaseFilter;
import com.secondmarket.filter.WikipediaFilter;
import com.secondmarket.model.Company;
import com.secondmarket.properties.SMProperties;

/**
 * 
 * @author Ming Li
 * 
 */
public class DataAggregator {
	private CrunchBaseFilter cbFilter;
	private WikipediaFilter wikiFilter;

	public DataAggregator(SMProperties wikiProperty) {
		cbFilter = new CrunchBaseFilter();
		wikiFilter = new WikipediaFilter(wikiProperty);
	}

	public void filterAndSetCompanyBasicInfo(BasicDBObject cbBasicDBObject,
			BasicDBObject wikiBasicDBObject, Company company) {
		String companyName;
		String funding;
		double fundingAmount;
		String location;
		String country;
		String industry;

		companyName = cbFilter.getCompanyName(cbBasicDBObject);
		funding = cbFilter.getFunding(cbBasicDBObject);
		fundingAmount = cbFilter.getFundingAmount(cbBasicDBObject);
		location = cbFilter.getLocation(cbBasicDBObject);
		country = cbFilter.getCounrty(cbBasicDBObject);
		industry = cbFilter.getIndustry(cbBasicDBObject);

		company.setCompanyName(companyName);
		System.out.println(companyName);
		company.setFunding(funding);
		company.setFundingAmount(fundingAmount);
		company.setLocation(location);
		company.setCountry(country);
		company.setIndustry(industry);

	}

	public void filterAndSetCompanyDetailedInfo(BasicDBObject cbBasicDBObject,
			BasicDBObject wikiBasicDBObject, Company company) {
		this.filterAndSetCompanyBasicInfo(cbBasicDBObject, wikiBasicDBObject,
				company);
		String cbOverview = "";
		String wikiOverview = "";
		cbOverview = cbFilter.getOverview(cbBasicDBObject);
		
		StringBuffer wikipediaContentStringBuffer = new StringBuffer();
		if (wikiBasicDBObject != null) {
			// wikiOverview = wikiFilter.getOverview(wikiBasicDBObject);
			// TODO dump the info-box data for now
			// wikiFilter.getInfoboxData(wikiBasicDBObject);

//			Map<String, List<String>> contentMap = wikiFilter.extractText(
//					wikiBasicDBObject, company);
			Map<String, List<String>> contentMap = wikiFilter.getFilteredWikipediaDoc(wikiBasicDBObject, company);
			
			// TODO append all the extracted sentences for testing
			Set<String> keySet = contentMap.keySet();
			Iterator<String> it = keySet.iterator();
			while(it.hasNext()){
				String sectionTopic = it.next();
				wikipediaContentStringBuffer.append("<h5>" + sectionTopic + "</h5>");
				List<String> sentenceList = contentMap.get(sectionTopic);
				for (String sentence : sentenceList){
					wikipediaContentStringBuffer.append(" -- " + sentence + "<br/>");
				}
			}
		}

		String aggregatedOverview = "<h4 style=\"color: red\">CrunchBase:</h4>"
				+ cbOverview + "\n"
				+ "<h4 style=\"color: red\">Wikipedia:</h4>" + "\n"
				+ wikipediaContentStringBuffer.toString();

		company.setOverview(aggregatedOverview);
	}

	public void filterAndSetCompanyInfobox(BasicDBObject cbBasicDBObject,
			BasicDBObject wikiBasicDBObject, Company company) {

	}
}
