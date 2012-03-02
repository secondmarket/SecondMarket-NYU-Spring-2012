package com.secondmarket.daoimpl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.model.Company;

public final class CompanyDAOImpl implements CompanyDAO {
	private DB db;
	private Mongo mongo;
	private DBCollection dbCollection;
	private Datastore ds;
	private BasicDBObject basicDBObject;
	private Gson gson = new Gson();// Using Gson to format the JSON object

	public CompanyDAOImpl() {
		try {
			mongo = new Mongo("localhost", 27017);
			// Database name: secondmarket
			db = mongo.getDB("secondmarket");
			// Collection name: company
			dbCollection = db.getCollection("company");
			// Drop all the existing collections
			// dbCollection.drop();
			// Using Morphia to map the model
			Morphia morphia = new Morphia();
			// Initialize Datastore
			ds = morphia.createDatastore(mongo, "secondmarket");
			// Declare the mapping model class
			morphia.map(Company.class);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			System.out.println("MongoDB is not running!");
			e.printStackTrace();
		}
	}

	public void saveCompany(Map<String, Object> map) {
		basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));
		Company company = filterCompanyDataAndCreateModel(basicDBObject);
		ds.save(company);
	}

	public void findCompanyByName(String companyName) {
		System.out.println("finding the specified company: " + companyName);

	}

	public Company findCompany() {
		/*
		 * DBCollection collection = db.getCollection("company"); DBCursor
		 * cursorDoc = collection.find(); while (cursorDoc.hasNext()) {
		 * System.out.println(cursorDoc.next()); }
		 */

		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(mongo, "secondmarket");
		// ds.find(Company.class, "companyName","Foursquare").get();
		Company importedCompany = ds.find(Company.class).get();
		return importedCompany;
	}

	public Company filterCompanyDataAndCreateModel(Object object) {
		BasicDBObject obj = (BasicDBObject) object;

		Company company = new Company();

		// Set company name
		if (obj.containsField("name")) {
			company.setCompanyName(obj.get("name").toString());

			System.out.println("->" + obj.get("name").toString());
		} else {
			company.setCompanyName("Not provided");
		}

		// Set location and country
		if (obj.containsField("offices")) {
			BasicDBList value = (BasicDBList) obj.get("offices");
			BasicDBObject valueObj = (BasicDBObject) value.get(0);

			if (valueObj.containsField("state_code")) {
				company.setLocation(valueObj.get("state_code").toString());

				System.out
						.println("->" + valueObj.get("state_code").toString());
			} else {
				company.setLocation("N/A");
			}

			if (valueObj.containsField("country_code")) {
				company.setCountry(valueObj.get("country_code").toString());

				System.out.println("->"
						+ valueObj.get("country_code").toString());
			} else {
				company.setCountry("N/A");
			}

		} else {
			company.setLocation("No offices");
			company.setCountry("No offices");
		}

		// Set funding
		if (obj.containsField("total_money_raised")) {
			company.setFunding(obj.get("total_money_raised").toString());

			System.out.println("->" + obj.get("total_money_raised").toString());
		} else {
			company.setFunding("Not provided");
		}

		// Set industry
		if (obj.containsField("category_code")) {
			company.setIndustry(obj.get("category_code").toString());

			System.out.println("->" + obj.get("category_code").toString());
		} else {
			company.setIndustry("Not provided");
		}

		return company;
	}

	public List<Company> findAllCompanies() {
		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(mongo, "secondmarket");
		List<Company> list = ds.find(Company.class).asList();
		return list;
	}

}
