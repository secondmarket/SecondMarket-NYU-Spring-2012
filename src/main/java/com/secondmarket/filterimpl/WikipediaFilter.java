package com.secondmarket.filterimpl;

import com.mongodb.BasicDBObject;
import com.secondmarket.filter.Filter;

/**
 * 
 * @author
 *
 */
public class WikipediaFilter implements Filter {

	public String getCompanyName(BasicDBObject basicDBObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFunding(BasicDBObject basicDBObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getFundingAmount(BasicDBObject basicDBObject) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getLocation(BasicDBObject basicDBObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCounrty(BasicDBObject basicDBObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIndustry(BasicDBObject basicDBObject) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOverview(BasicDBObject basicDBObject) {
		
		/**
		 * Use regular expression to get the overview paragraph--- e.g.
		 * 
		 * From{'''SecondMarket'''} to  {\n\n==History==}, also need to replaceAll the http links
		 * 
		 */
		
		return null;
	}

}
