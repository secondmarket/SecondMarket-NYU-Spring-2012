package com.secondmarket.utility;

import com.mongodb.util.JSON;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.secondmarket.model.Company;

public class DataFilter {

	/**
	 * Sees if the company has funding by checking "total_money_raised"
	 * attribute
	 * 
	 * @param basicDBObject
	 * @return true if the attribute is not equal with "$0"
	 */
	public boolean checkCompanyFundings(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("total_money_raised")
				&& !"$0".equals(basicDBObject.get("total_money_raised")
						.toString().trim())) {
			return true;
		} else {
			return false;
		}
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
			BasicDBObject valueObj = (BasicDBObject) value.get(0);

			if (valueObj.containsField("state_code")) {
				company.setLocation(valueObj.get("state_code").toString());

				System.out
						.println("->" + valueObj.get("state_code").toString());
			} else {
				company.setLocation("N/A");
			}

			if (valueObj.containsField("country_code")) {
				company.setCountry(valueObj.get("country_code").toString());

				System.out.println("->"
						+ valueObj.get("country_code").toString());
			} else {
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

			System.out.println("->"
					+ basicDBObject.get("total_money_raised").toString());
		} else {
			company.setFunding("Not provided");
		}

		// Set industry
		if (basicDBObject.containsField("category_code")) {
			company.setIndustry(basicDBObject.get("category_code").toString());

			System.out.println("->"
					+ basicDBObject.get("category_code").toString());
		} else {
			company.setIndustry("Not provided");
		}
		System.out.println("======================");
	}

	public void getAndSetCompanyDetailedInfo(BasicDBObject basicDBObject,
			Company company) {
		this.getAndSetCompanyBasicInfo(basicDBObject, company);
	}
}
