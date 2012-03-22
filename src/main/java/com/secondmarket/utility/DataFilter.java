package com.secondmarket.utility;

import com.mongodb.util.JSON;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.secondmarket.model.Company;

public class DataFilter {

	/**
	 * Checks if the company has funding by comparing the value of
	 * "total_money_raised" field, return false if the value is "$0";
	 * 
	 * Checks if the company has been dead-pooled by comparing the value of
	 * "deadpooled_***" fields, return false if any of the values is not null;
	 * 
	 * Checks if the company has gone IPO by comparing the value of "ipo" field,
	 * return false if the value is not null;
	 * 
	 * Checks if the company has been acquired by other companies or
	 * organizations by comparing the value of "acquisition" field, return false
	 * if the value is not null;
	 * 
	 * Otherwise, return true
	 * 
	 * @param basicDBObject
	 * @return boolean
	 */
	public boolean checkCompanyEligibility(BasicDBObject basicDBObject) {
		// Zero-funding companies are not considered
		if (basicDBObject.containsField("total_money_raised")
				&& "$0".equals(basicDBObject.get("total_money_raised")
						.toString().trim())) {
			return false;
		}

		// Dead-pooled companies are not considered
		if ((basicDBObject.containsField("deadpooled_year") && basicDBObject
				.get("deadpooled_year") != null)
				|| (basicDBObject.containsField("deadpooled_month") && basicDBObject
						.get("deadpooled_month") != null)
				|| (basicDBObject.containsField("deadpooled_day") && basicDBObject
						.get("deadpooled_day") != null)) {
			return false;
		}

		// Gone public (IPO) companies are not considered
		if (basicDBObject.containsField("ipo")
				&& basicDBObject.get("ipo") != null) {
			return false;
		}

		// Acquired companies are not considered
		if (basicDBObject.containsField("acquisition")
				&& basicDBObject.get("acquisition") != null) {
			return false;
		}

		return true;
	}

	/**
	 * Retrieves the basic information and set the attributes to the company
	 * object
	 * 
	 * @param basicDBObject
	 * @param company
	 */
	public void getAndSetCompanyBasicInfo(BasicDBObject basicDBObject,
			Company company) {

		// Set company name
		if (basicDBObject.containsField("name")) {
			company.setCompanyName(basicDBObject.get("name").toString());

			System.out.println("->" + basicDBObject.get("name").toString());
		} else {
			company.setCompanyName("Not provided");
		}

		// Set location and country
		if (basicDBObject.containsField("offices")) {
			BasicDBList value = (BasicDBList) JSON.parse(basicDBObject.get(
					"offices").toString());
			if (value.size() > 0) {
				BasicDBObject valueObj = (BasicDBObject) value.get(0);

				if (valueObj.containsField("state_code")) {
					company.setLocation(valueObj.get("state_code").toString());

					// System.out
					// .println("->" + valueObj.get("state_code").toString());
				} else {
					company.setLocation("N/A");
				}

				if (valueObj.containsField("country_code")) {
					company.setCountry(valueObj.get("country_code").toString());

					// System.out.println("->"
					// + valueObj.get("country_code").toString());
				} else {
					company.setCountry("N/A");
				}
			} else {
				company.setLocation("N/A");
				company.setCountry("N/A");
			}

		} else {
			company.setLocation("No offices");
			company.setCountry("No offices");
		}

		// Set funding
		if (basicDBObject.containsField("total_money_raised")) {
			company.setFunding(basicDBObject.get("total_money_raised")
					.toString());

			// System.out.println("->"
			// + basicDBObject.get("total_money_raised").toString());
		} else {
			company.setFunding("Not provided");
		}

		// Set industry
		if (basicDBObject.containsField("category_code")) {
			company.setIndustry(basicDBObject.get("category_code").toString());

			// System.out.println("->"
			// + basicDBObject.get("category_code").toString());
		} else {
			company.setIndustry("Not provided");
		}
		// System.out.println("======================");
	}

	public void getAndSetCompanyDetailedInfo(BasicDBObject basicDBObject,
			Company company) {
		this.getAndSetCompanyBasicInfo(basicDBObject, company);

		// Set overview
		if (basicDBObject.containsField("overview")) {
			company.setOverview(basicDBObject.get("overview").toString());
		} else {
			company.setIndustry("Not provided");
		}
	}
}
