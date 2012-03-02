package com.secondmarket.daoimpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Map;

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

//			DBCursor cursorAll = collection.find();
			DBCursor cursor = collection.find(searchQuery);
			
			
			StringBuffer result = new StringBuffer();
			while (cursor.hasNext()) {
				result.append(cursor.next());
			}
			System.out.println(result.toString());
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
				System.out.println(s);
			}

			Assert.assertTrue(result.toString().length() != 0);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
	}
	
	public void importDataByCompanyName(String companyName) {
/*		 String url = "http://api.crunchbase.com/v/1/company/"
		 + organizationName + ".js";
*/
		String url = "http://api.crunchbase.com/v/1/person/mark-zuckerberg.js";

		URL resource = null;
		try {
			resource = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();

		mapper.getDeserializationConfig().withDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));

		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		InputStream input = null;
		try {
			try {
				input = resource.openStream();
				Map<String, Object> map = (Map<String, Object>) mapper
						.readValue(input, Map.class);

				DBObject dbObject = (DBObject) JSON.parse(map.toString());

			} finally {
				if (input != null) {
					input.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testFindAllCompanies(){
		CompanyDAOImpl testDaoImpl = new CompanyDAOImpl();
		testDaoImpl.findAllCompanies();
	}
}
