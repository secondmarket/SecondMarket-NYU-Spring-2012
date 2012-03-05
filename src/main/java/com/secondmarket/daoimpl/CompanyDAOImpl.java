package com.secondmarket.daoimpl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.MapperOptions;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.model.Company;
import com.secondmarket.utility.DataFilter;

public final class CompanyDAOImpl implements CompanyDAO {
	private DB db;
	private Mongo mongo;
	private DBCollection dbCollection;
	private Datastore ds;
	private BasicDBObject basicDBObject;
	private Gson gson = new Gson();// Using Gson to format the JSON object
	private DataFilter filter = new DataFilter();// Filter data

	public CompanyDAOImpl() {
		try {
			mongo = new Mongo("localhost", 27017);
			// Database name: secondmarket
			db = mongo.getDB("secondmarket");
			// Collection name: company
			dbCollection = db.getCollection("company");
			// Drop all the existing collections
			dbCollection.drop();
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

	public void saveCompany(String companyName, Map<String, String> map) {
		Company company = new Company();
		company.setCompanyName(companyName);
		company.setData(map);
		ds.save(company);
	}

	public void findCompanyByName(String companyName) {
		System.out.println("finding the specified company: " + companyName);

	}

	public List<Company> findAllCompanies() {
		Morphia morphia = new Morphia();
		Datastore ds = morphia.createDatastore(mongo, "secondmarket");
		List<Company> rawDataList = ds.find(Company.class).asList();
		List<Company> companyList = new ArrayList<Company>();

		Iterator<Company> it = rawDataList.iterator();
		while (it.hasNext()) {
			Company company = it.next();
			Map<String, String> map = company.getData();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));

			// Filter the data and update the company model
			filter.getAndSetCompanyBasicInfo(basicDBObject, company);

			companyList.add(company);
		}

		return companyList;
	}

}
