package com.secondmarket.utility;

import com.mongodb.BasicDBObject;
import com.secondmarket.filter.Filter;
import com.secondmarket.filterimpl.CrunchBaseFilter;
import com.secondmarket.filterimpl.WikipediaFilter;
import com.secondmarket.model.Company;

public class DataAggregator {
	private Filter cbFilter;
	private Filter wikiFilter;

	public DataAggregator() {
		cbFilter = new CrunchBaseFilter();
		wikiFilter = new WikipediaFilter();
	}

	public void filterAndSetCompanyBasicInfo(BasicDBObject basicDBObject,
			Company company) {
		String companyName;
		String funding;
		double fundingAmount;
		String location;
		String country;
		String industry;

		companyName = cbFilter.getCompanyName(basicDBObject);
		funding = cbFilter.getFunding(basicDBObject);
		fundingAmount = cbFilter.getFundingAmount(basicDBObject);
		location = cbFilter.getLocation(basicDBObject);
		country = cbFilter.getCounrty(basicDBObject);
		industry = cbFilter.getIndustry(basicDBObject);

		company.setCompanyName(companyName);
		System.out.println(companyName);
		company.setFunding(funding);
		company.setFundingAmount(fundingAmount);
		company.setLocation(location);
		company.setCountry(country);
		company.setIndustry(industry);

	}

	public void filterAndSetCompanyDetailedInfo(BasicDBObject basicDBObject,
			Company company) {
		this.filterAndSetCompanyBasicInfo(basicDBObject, company);
		String overview;
		overview = cbFilter.getOverview(basicDBObject);
		company.setOverview(overview);
	}
}
