package com.secondmarket.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.secondmarket.filter.CrunchBaseFilter;
import com.secondmarket.filter.WikipediaFilter;
import com.secondmarket.model.Company;
import com.secondmarket.model.FundingRound;
import com.secondmarket.model.Office;
import com.secondmarket.model.Relationship;
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

	public void filterAndSetCompanyData(BasicDBObject cbBasicDBObject,
			BasicDBObject wikiBasicDBObject, Company company) {
		String companyName;
		String homepageurl;
		String funding;
		double fundingAmount;
		String location;
		String country;
		String industry;
		String cboverview;
		Map<String, String> wikiContentMap;
		List<String> wikiContentTopics;
		List<String> wikiContentValues;

		int employees;
		String foundedDate;
		List<FundingRound> fundings;
		List<Office> offices;
		List<Relationship> relationships;
		List<String> embedsVideoUrlList;

		companyName = cbFilter.getCompanyName(cbBasicDBObject);
		System.out.println("==============================" + companyName
				+ "==============================");

		homepageurl = cbFilter.getHomepageUrl(cbBasicDBObject);
		funding = cbFilter.getFunding(cbBasicDBObject);
		fundingAmount = cbFilter.getFundingAmount(cbBasicDBObject);
		location = cbFilter.getLocation(cbBasicDBObject);
		country = cbFilter.getCounrty(cbBasicDBObject);
		industry = cbFilter.getIndustry(cbBasicDBObject);
		cboverview = cbFilter.getOverview(cbBasicDBObject);
		if (wikiBasicDBObject != null) {
			wikiContentMap = wikiFilter.getFilteredWikipediaDoc(
					wikiBasicDBObject, company);
			Iterator<String> it = wikiContentMap.keySet().iterator();
			wikiContentTopics = new ArrayList<String>();
			wikiContentValues = new ArrayList<String>();
			while (it.hasNext()) {
				String key = it.next();
				String value = wikiContentMap.get(key);
				wikiContentTopics.add(key);
				wikiContentValues.add(value);
			}
		} else {
			wikiContentTopics = new ArrayList<String>();
			wikiContentValues = new ArrayList<String>();
		}

		employees = cbFilter.getNumberOfEmployees(cbBasicDBObject);
		foundedDate = cbFilter.getFoundedDate(cbBasicDBObject);
		fundings = cbFilter.getFundings(cbBasicDBObject);
		offices = cbFilter.getOffices(cbBasicDBObject);
		relationships = cbFilter.getRelationships(cbBasicDBObject);
		embedsVideoUrlList = cbFilter.getEmbedVideoSrcs(cbBasicDBObject);
		List<byte[]> logos = cbFilter.getResizedLogos(cbBasicDBObject);
		byte[] iconLogo = logos.get(0);
		byte[] profileLogo = logos.get(1);

		company.setCompanyName(companyName);
		company.setHomepageurl(homepageurl);
		company.setFunding(funding);
		company.setFundingAmount(fundingAmount);
		company.setLocation(location);
		company.setCountry(country);
		company.setIndustry(industry);
		company.setCboverview(cboverview);
		company.setWikiContentTopics(wikiContentTopics);
		company.setWikiContentValues(wikiContentValues);
		company.setEmployees(employees);
		company.setFoundedDate(foundedDate);
		company.setFundings(fundings);
		company.setOffices(offices);
		company.setRelationships(relationships);
		company.setLogo(iconLogo);
		company.setVideos(embedsVideoUrlList);
		company.setProfileLogo(profileLogo);
	}

}
