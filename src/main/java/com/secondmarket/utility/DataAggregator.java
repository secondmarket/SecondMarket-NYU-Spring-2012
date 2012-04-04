package com.secondmarket.utility;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		String overview;
		int employees;
		String foundedDate;
		List<FundingRound> fundings;
		List<Office> offices;
		List<Relationship> relationships;

		companyName = cbFilter.getCompanyName(cbBasicDBObject);
		homepageurl = cbFilter.getHomepageUrl(cbBasicDBObject);
		funding = cbFilter.getFunding(cbBasicDBObject);
		fundingAmount = cbFilter.getFundingAmount(cbBasicDBObject);
		location = cbFilter.getLocation(cbBasicDBObject);
		country = cbFilter.getCounrty(cbBasicDBObject);
		industry = cbFilter.getIndustry(cbBasicDBObject);
		overview = cbFilter.getOverview(cbBasicDBObject);
		String aggregatedOverview = this.appendWikipediaContent(overview,
				wikiBasicDBObject, company);

		employees = cbFilter.getNumberOfEmployees(cbBasicDBObject);
		foundedDate = cbFilter.getFoundedDate(cbBasicDBObject);
		fundings = cbFilter.getFundings(cbBasicDBObject);
		offices = cbFilter.getOffices(cbBasicDBObject);
		relationships = cbFilter.getRelationships(cbBasicDBObject);
		byte[] imagebyte = cbFilter.getCompanyLogo(cbBasicDBObject);

		company.setCompanyName(companyName);
		System.out.println(companyName);
		company.setHomepageurl(homepageurl);
		company.setFunding(funding);
		company.setFundingAmount(fundingAmount);
		company.setLocation(location);
		company.setCountry(country);
		company.setIndustry(industry);
		// company.setOverview(overview);
		company.setOverview(aggregatedOverview);
		company.setEmployees(employees);
		company.setFoundedDate(foundedDate);
		company.setFundings(fundings);
		company.setOffices(offices);
		company.setRelationships(relationships);
		company.setLogo(imagebyte);

	}

	private String appendWikipediaContent(String cbOverview,
			BasicDBObject wikiBasicDBObject, Company company) {
		StringBuffer wikipediaContentStringBuffer = new StringBuffer();
		if (wikiBasicDBObject != null) {
			// Map<String, List<String>> contentMap = wikiFilter.extractText(
			// wikiBasicDBObject, company);
			/*
			 * Map<String, List<String>> contentMap = wikiFilter
			 * .getFilteredWikipediaDoc(wikiBasicDBObject, company);
			 */
			Map<String, String> contentMap = wikiFilter.extractText(
					wikiBasicDBObject, company);

			// TODO append all the extracted sentences for testing
			Set<String> keySet = contentMap.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String sectionTopic = it.next();
				if (contentMap.get(sectionTopic).length() == 0) {
					continue;
				} else {
					wikipediaContentStringBuffer.append("<h5>" + sectionTopic
							+ "</h5>");
					wikipediaContentStringBuffer.append(" -- "
							+ contentMap.get(sectionTopic) + "<br/>");
					/*
					 * List<String> sentenceList = contentMap.get(sectionTopic);
					 * for (String sentence : sentenceList) {
					 * wikipediaContentStringBuffer.append(" -- " + sentence +
					 * "<br/>"); }
					 */
				}

			}
		}

		String aggregatedOverview = "<h4 style=\"color: red\">CrunchBase:</h4>"
				+ cbOverview + "\n"
				+ "<h4 style=\"color: red\">Wikipedia:</h4>" + "\n"
				+ wikipediaContentStringBuffer.toString();
		return aggregatedOverview;
	}

}
