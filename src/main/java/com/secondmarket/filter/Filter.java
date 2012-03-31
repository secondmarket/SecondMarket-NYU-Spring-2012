package com.secondmarket.filter;

import java.util.Map;

import com.mongodb.BasicDBObject;

/**
 * 
 * @author Ming Li
 * 
 */
public interface Filter {
	// TODO remove this interface?

	String getCompanyName(BasicDBObject basicDBObject);

	String getFunding(BasicDBObject basicDBObject);

	double getFundingAmount(BasicDBObject basicDBObject);

	String getLocation(BasicDBObject basicDBObject);

	String getCounrty(BasicDBObject basicDBObject);

	String getIndustry(BasicDBObject basicDBObject);

	String getOverview(BasicDBObject basicDBObject);

}
