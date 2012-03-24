package com.secondmarket.daoimpl;

import java.util.List;
import java.net.UnknownHostException;
import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import com.secondmarket.daoimpl.CompanyDAOImpl;

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
				// System.out.println(s);
			}

			Assert.assertTrue(result.toString().length() != 0);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}

	}
		
	public void testsaveMasterlist() {
		//drop collection then call saveMasterlist then check database not null
		try{
		Mongo mongo = new Mongo("localhost", 27017);
		DB db = mongo.getDB("SecondMarketTest");
		DBCollection coll = db.getCollection("companies");
		coll.drop();
		
		List<Object> testList = new ArrayList<Object>();
		testList.add("test1");
		testList.add("test2");
		CompanyDAOImpl c = new CompanyDAOImpl();
		c.saveMasterlist(testList);		
		testList=c.getMasterList();
		Assert.assertFalse(testList.isEmpty());
		coll.drop();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
	}

	public void testgetMasterList() {
		try{
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("SecondMarketTest");
			DBCollection coll = db.getCollection("companies");
			coll.drop();
			
			List<Object> testList = new ArrayList<Object>();
			testList.add("test1");
			testList.add("test2");
			CompanyDAOImpl c = new CompanyDAOImpl();
			c.saveMasterlist(testList);		
			testList=c.getMasterList();
			Assert.assertFalse(testList.isEmpty());
			coll.drop();
			
		    } catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (MongoException e) {
				e.printStackTrace();
			}
	}

	public void testsaveCompany(){
			
	}
		
	/*public void testfindAllCompanies() {
		try{	
		
		Mongo mongo = new Mongo("localhost", 27017);
		DB db = mongo.getDB("SecondMarketTest");
		DBCollection collection = db.getCollection("company");

		BasicDBObject doc = new BasicDBObject();
		doc.put("companyName", "AAAA");
		doc.put("location", "NY");
		doc.put("country", "USA");
		doc.put("industry", "web");
		
		BasicDBObject info = new BasicDBObject();
		info.put("x", 203);
		info.put("y", 102);

		doc.put("info", info);
		
		collection.insert(doc);
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		List<com.secondmarket.model.Company> testList;
		CompanyDAOImpl c = new CompanyDAOImpl();
		
		testList=c.findAllCompanies();
	    
		Assert.assertFalse(testList.isEmpty());
	
	}
	*/ 
	/*public void testfindComoanyByName(){
        
		CompanyDAOImpl c = new CompanyDAOImpl();
        com.secondmarket.model.Company test = c.findCompanyByName("Facebook");
        //String companyname = test.getCompanyName();      
      //  Assert.assertNotNull(test);	
    */   
	
        
	}








	
	
	
	
	




