package com.secondmarket.filter;

import org.springframework.web.util.HtmlUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

/**
 * 
 * @author Ming Li
 * 
 */
public final class CrunchBaseFilter{
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
