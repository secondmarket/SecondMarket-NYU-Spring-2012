package com.secondmarket.filterimpl;

import org.springframework.web.util.HtmlUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.secondmarket.filter.Filter;

/**
 * 
 * @author Ming Li
 *
 */
public final class CrunchBaseFilter implements Filter {
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

	public String getCompanyName(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("name")) {
			return basicDBObject.get("name").toString().trim();
		} else {
			return "undefined";
		}
	}

	public String getFunding(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("total_money_raised")) {
			return (HtmlUtils.htmlEscapeHex(basicDBObject
					.get("total_money_raised").toString().trim()));
		} else {
			return "undefined";
		}

	}

	public double getFundingAmount(BasicDBObject basicDBObject) {
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
		return fundingAmount;
	}

	public String getLocation(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("offices")) {
			BasicDBList value = (BasicDBList) JSON.parse(basicDBObject
					.get("offices").toString().trim());
			if (value.size() > 0) {
				BasicDBObject valueObj = (BasicDBObject) value.get(0);

				if (valueObj.containsField("state_code")) {
					return valueObj.get("state_code").toString().trim();
				} else {
					return "undefined";
				}
			} else {
				return "undefined";
			}
		} else {
			return "undefined";
		}
	}

	public String getCounrty(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("offices")) {
			BasicDBList value = (BasicDBList) JSON.parse(basicDBObject
					.get("offices").toString().trim());
			if (value.size() > 0) {
				BasicDBObject valueObj = (BasicDBObject) value.get(0);

				if (valueObj.containsField("country_code")) {
					return valueObj.get("country_code").toString().trim();
				} else {
					return "undefined";
				}
			} else {
				return "undefined";
			}
		} else {
			return "undefined";
		}
	}

	public String getIndustry(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("category_code")) {
			return basicDBObject.get("category_code").toString().trim();
		} else {
			return "undefined";
		}
	}

	public String getOverview(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("overview")) {
			return basicDBObject.get("overview").toString().trim();
		} else {
			return "undefined";
		}
	}
}
