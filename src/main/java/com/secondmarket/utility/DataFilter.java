package com.secondmarket.utility;

import org.springframework.web.util.HtmlUtils;

import com.mongodb.util.JSON;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.secondmarket.model.Company;

/**
 * 
 * @author Ming Li
 *
 */
public final class DataFilter {

	// private constructor to avoid unnecessary instantiation
	private DataFilter() {
	}

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
	public static boolean checkCompanyEligibility(BasicDBObject basicDBObject) {
		// Zero-funding companies are not considered
		if (basicDBObject.containsField("total_money_raised")
				&& basicDBObject.get("total_money_raised").toString().trim()
						.length() == 2
				&& basicDBObject.get("total_money_raised").toString().trim()
						.endsWith("0")) {
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
	public static void retrieveAndSetCompanyBasicInfo(
			BasicDBObject basicDBObject, Company company) {

		// Set company name
		if (basicDBObject.containsField("name")) {
			company.setCompanyName(basicDBObject.get("name").toString().trim());

		//	System.out.println("->"
				//	+ basicDBObject.get("name").toString().trim());
		} else {
			company.setCompanyName("unexisted");
			System.out.println("Company name not found!" + "\n"
					+ basicDBObject.toString() + "\n");
		}

		// Set location and country
		if (basicDBObject.containsField("offices")) {
			BasicDBList value = (BasicDBList) JSON.parse(basicDBObject
					.get("offices").toString().trim());
			if (value.size() > 0) {
				BasicDBObject valueObj = (BasicDBObject) value.get(0);

				if (valueObj.containsField("state_code")) {
					company.setLocation(valueObj.get("state_code").toString()
							.trim());

					// System.out
					// .println("->" + valueObj.get("state_code").toString());
				} else {
					company.setLocation("unknown");
				}

				if (valueObj.containsField("country_code")) {
					company.setCountry(valueObj.get("country_code").toString()
							.trim());

					// System.out.println("->"
					// + valueObj.get("country_code").toString());
				} else {
					company.setCountry("unknown");
				}
			} else {
				company.setLocation("unknown");
				company.setCountry("unknown");
			}

		} else {
			company.setLocation("no offices");
			company.setCountry("no offices");
		}

		// Set funding and fundingAmount
		if (basicDBObject.containsField("total_money_raised")) {
			company.setFunding(HtmlUtils.htmlEscapeHex(basicDBObject
					.get("total_money_raised").toString().trim()));

			String funding = basicDBObject.get("total_money_raised").toString()
					.trim();
			double fundingAmount;
			if (funding.endsWith("k") || funding.endsWith("K")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1));
			} else if (funding.endsWith("M") || funding.endsWith("m")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1)) * 1000.0;
			} else if (funding.endsWith("B") || funding.endsWith("b")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1)) * 1000000.0;
			} else if (funding.endsWith("T") || funding.endsWith("t")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1)) * 1000000000.0;
			} else {
				fundingAmount = Double.MAX_VALUE;
			}
			company.setFundingAmount(fundingAmount);

			// System.out.println("->"
			// + basicDBObject.get("total_money_raised").toString());
		} else {
			company.setFunding("n/a");
		}

		// Set industry
		if (basicDBObject.containsField("category_code")) {
			company.setIndustry(basicDBObject.get("category_code").toString()
					.trim());

			// System.out.println("->"
			// + basicDBObject.get("category_code").toString());
		} else {
			company.setIndustry("undefined");
		}
		// System.out.println("======================");
	}

	public static void retrieveAndSetCompanyDetailedInfo(
			BasicDBObject basicDBObject, Company company) {
		// Set basic information
		retrieveAndSetCompanyBasicInfo(basicDBObject, company);

		// Set overview for detailed page
		if (basicDBObject.containsField("overview")) {
			company.setOverview(basicDBObject.get("overview").toString().trim());
		} else {
			company.setOverview("undefined");
		}
	}
}
