package com.secondmarket.daoimpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.secondmarket.model.Company;
import com.secondmarket.utility.DataFilter;

public class TestCompanyDAOImpl extends TestCase {

	public void testMongoDatabase() {
		try {
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("SecondMarketTest");
			DBCollection collection = db.getCollection("company");

			BasicDBObject doc = new BasicDBObject();
			doc.put("name", "MongoDB");
			doc.put("type", "database");
			doc.put("count", 1);

			BasicDBObject info = new BasicDBObject();
			info.put("x", 203);
			info.put("y", 102);

			doc.put("info", info);

			collection.insert(doc);

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("name", "MongoDB");

			// DBCursor cursorAll = collection.find();
			DBCursor cursor = collection.find(searchQuery);

			StringBuffer result = new StringBuffer();
			while (cursor.hasNext()) {
				result.append(cursor.next());
			}
			
			Assert.assertTrue(result.toString().length() != 0);
		  
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public void testAvailableDatabase() {
		try {
			Mongo mongo = new Mongo("localhost", 27017);

			StringBuffer result = new StringBuffer();
			for (String s : mongo.getDatabaseNames()) {
				result.append(s);
			//	System.out.println(s);
			}

			Assert.assertTrue(result.toString().length() != 0);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}

	}
		
	/*public void testsaveMasterlist(List<Object> masterList) {
		// create a new list save to the database then get the list out again
		List<Object> testList = new ArrayList<Object>()
		testList=testList.add(test1);
		testList=testList.add(test2);
		DBCollection collection  = db.getCollection("companies");
		collection.drop();
		BasicDBObject doc = new BasicDBObject();
		doc.put("MasterList",testList);
		collection.insert(doc);
		testList=testList.removeAll(c);
		DBObject companies = collection.findOne();
		List<Object> testList = (List<Object>) companies.get("MasterList");
		Assert.assertTrue(testList.toString().length()!=0);
	}
	*/
	public void testsaveMasterlist(List<Object> masterList) {
		//drop collection then call saveMasterlist then check database not null
		DBCollection coll = db.getCollection("companies");
		coll.drop();
		saveMasterlist(List<Object> masterList);
		List<Object> masterList = (List<Object>) companies.get("masterlist");
		Assert.assertTrue(masterList.toString().length()!=0);
	}

	public void testgetMasterList() {
	    // put the getlist to testList then check length
		List<Object> testList = new ArrayList<Object>()
	    testList=getMasterList();
	    Assert.assertTrue(testList.toString().length()!=0);
	
	}

	public void saveCompany(String companyName, Map<String, String> map) {
		//看不懂ＭＡＰ ＮＯＷ
		Company company = new Company();
		basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));
		DataFilter.retrieveAndSetCompanyBasicInfo(basicDBObject, company);
		company.setCrunchbaseDoc(map);
		ds.save(company);
	}

	/*
	 * public List<Company> findAllCompanies() {
		List<Company> rawDataList = ds.find(Company.class).asList();
		List<Company> companyList = new ArrayList<Company>();

		Iterator<Company> it = rawDataList.iterator();
		while (it.hasNext()) {
			Company company = it.next();
			Map<String, String> map = company.getCrunchbaseDoc();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));

			// Filter the data and update the company model
			DataFilter.retrieveAndSetCompanyBasicInfo(basicDBObject, company);

			companyList.add(company);
		}
		return companyList;
	}
   */
	public void testfindAllCompanies() {
		List<Object> testList = new ArrayList<Object>();
		testList=testfindAllCompanies();
		Assert.assertNotNull(testList);
	}
	
	public void testfindCompanyByName() {
		String result=findCompanyByName(Facebook).companyName;
		String expect="Facebook";
		Assert.assertEquals(expect, result);
		String result=findCompanyByName(Facebook).location;
		String expect="New York";
		Assert.assertEquals(expect, result);
		String result=findCompanyByName(Facebook).country;
		String expect="USA";
		Assert.assertEquals(expect, result);
		String result=findCompanyByName(Facebook).industry;
		String expect="Consumer Web";
		Assert.assertEquals(expect, result);
	}
	public List<Company> findCompaniesByImpreciseName(String companyName) {
		List<Company> companyList = ds.find(Company.class).field("companyName")
				.equal(Pattern.compile(companyName, Pattern.CASE_INSENSITIVE))
				.asList();

		Iterator<Company> it = companyList.iterator();
		while (it.hasNext()) {
			Company company = it.next();
			Map<String, String> map = company.getCrunchbaseDoc();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));
			DataFilter
					.retrieveAndSetCompanyDetailedInfo(basicDBObject, company);
		}
		return companyList;
	}

	public List<Company> findCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage) {
		List<Company> tempList = null;
		if (pageIndex != 0) {
			tempList = ds.createQuery(Company.class)
					.offset(pageIndex * numberOfElementsPerPage)
					.limit(numberOfElementsPerPage).asList();
		} else {
			tempList = ds.createQuery(Company.class)
					.limit(numberOfElementsPerPage).asList();
		}

		// Filter out basic info
		List<Company> paginatedList = new ArrayList<Company>();
		Iterator<Company> it = tempList.iterator();
		while (it.hasNext()) {
			Company company = it.next();
			Map<String, String> map = company.getCrunchbaseDoc();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));

			// Filter the data and update the company model
			DataFilter.retrieveAndSetCompanyBasicInfo(basicDBObject, company);
			paginatedList.add(company);
		}

		return paginatedList;
	}
	public List<Company> findCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage) {
		List testList = new ArrayList<Company>();
		
		return paginatedList;
	}
	public void TestgetPageAmount() {
		
		int answer=getPageAmount(10);
		long numberOfCompanies = ds.createQuery(Company.class).countAll();
		int expect = (int) Math.ceil(((double) numberOfCompanies) / 10);
		Assert.assertEquals(expect, answer);
	}
	
	
	
	public List<Company> findSortedCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage, String sortByField,
			boolean isDescending) {
		String orderStr = (isDescending) ? ("-" + sortByField) : sortByField;

		List<Company> tempList = null;
		if (pageIndex != 0) {
			tempList = ds.createQuery(Company.class).order(orderStr)
					.offset(pageIndex * numberOfElementsPerPage)
					.limit(numberOfElementsPerPage).asList();
		} else {
			tempList = ds.createQuery(Company.class).order(orderStr)
					.limit(numberOfElementsPerPage).asList();
		}

		// Filter out basic info
		List<Company> paginatedList = new ArrayList<Company>();
		Iterator<Company> it = tempList.iterator();
		while (it.hasNext()) {
			Company company = it.next();
			Map<String, String> map = company.getCrunchbaseDoc();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));

			// Filter the data and update the company model
			DataFilter.retrieveAndSetCompanyBasicInfo(basicDBObject, company);
			paginatedList.add(company);
		}

		return paginatedList;
	}

	
	
	
	
}
