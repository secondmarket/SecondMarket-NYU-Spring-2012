package com.secondmarket.utility;

import com.mongodb.BasicDBObject;

/**
 * 
 * @author Ming Li
 * 
 */
public final class CrunchBaseUtils {

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
				&& basicDBObject.get("total_money_raised") != null
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

}
