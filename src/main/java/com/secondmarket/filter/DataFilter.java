package com.secondmarket.filter;

import com.mongodb.BasicDBObject;

public interface DataFilter {

	String getCompanyName(BasicDBObject basicDBObject);

	String getFunding(BasicDBObject basicDBObject);

	double getFundingAmount(BasicDBObject basicDBObject);

	String getLocation(BasicDBObject basicDBObject);

	String getCounrty(BasicDBObject basicDBObject);

	String getIndustry(BasicDBObject basicDBObject);

	String getOverview(BasicDBObject basicDBObject);
}
