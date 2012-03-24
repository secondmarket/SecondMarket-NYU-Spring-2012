package com.secondmarket.filter;

import com.mongodb.BasicDBObject;

/**
 * 
 * @author Ming Li
 *
 */
public interface Filter {

	String getCompanyName(BasicDBObject basicDBObject);

	String getFunding(BasicDBObject basicDBObject);

	double getFundingAmount(BasicDBObject basicDBObject);

	String getLocation(BasicDBObject basicDBObject);

	String getCounrty(BasicDBObject basicDBObject);

	String getIndustry(BasicDBObject basicDBObject);

	String getOverview(BasicDBObject basicDBObject);
}
