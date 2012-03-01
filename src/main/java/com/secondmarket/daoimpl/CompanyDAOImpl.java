package com.secondmarket.daoimpl;

import java.net.UnknownHostException;
import java.util.Map;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.model.Company;

public class CompanyDAOImpl implements CompanyDAO {
	private DB db;
	private Mongo mongo;

	public CompanyDAOImpl() {
		try {
			mongo = new Mongo("localhost", 27017);
			db = mongo.getDB("secondmarket");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public void saveCompany(Map<String, Object> map) {
		DBCollection collection = db.getCollection("company");
		// Drop the old collections
		collection.drop();

		// Formatting the JSON format
		Gson gson = new Gson();

		BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(gson
				.toJson(map));

		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(mongo, "secondmarket");
		morphia.map(Company.class);
		Company company = filterCompanyDataAndCreateModel(basicDBObject);
		ds.save(company);
	}

	public void findCompanyByName(String companyName) {
		System.out.println("finding the specified company: " + companyName);

	}

	//Should return a List in future
	public Company findCompanies() {
		/*DBCollection collection = db.getCollection("company");
		DBCursor cursorDoc = collection.find();
		while (cursorDoc.hasNext()) {
			System.out.println(cursorDoc.next());
		}*/
		
		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(mongo, "secondmarket");
//		Company company = ds.find(Company.class, "companyName", "Foursquare").get();
		Company importedCompany = ds.find(Company.class).get(); //For now, only the first document of the collection will be returned
//		return company;
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

		// Set Funding
		if (obj.containsField("total_money_raised")) {
			company.setFunding(obj.get("total_money_raised").toString());

			System.out.println("->" + obj.get("total_money_raised").toString());
		} else {
			company.setFunding("Not provided");
		}

		// Set industry
		if (obj.containsField("category_code")){
			company.setIndustry(obj.get("category_code").toString());
			
			System.out.println("->" + obj.get("category_code").toString());
		} else {
			company.setIndustry("Not provided");
		}
		
		return company;
	}

}
