package com.secondmarket.daoimpl;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.model.Company;
import com.secondmarket.model.EdgarCompanyDetail;
import com.secondmarket.model.FundingRound;
import com.secondmarket.properties.SMProperties;
import com.secondmarket.utility.DataAggregator;

/**
 * 
 * @author Ming Li
 * 
 */
public final class CompanyDAOImpl implements CompanyDAO {
	private DB db;
	private Mongo mongo;
	private DBCollection dbCollection;
	private Gson gson = new Gson();// Using Gson to format the JSON object
	private Datastore ds;
	private Morphia morphia;

	private DataAggregator aggregator;

	public CompanyDAOImpl() {
		try {

			mongo = new Mongo("localhost", 27017);
			db = mongo.getDB("secondmarket");
			dbCollection = db.getCollection("company");
//			dbCollection.drop();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			System.out.println("MongoDB is not running!");
			e.printStackTrace();
		}
		morphia = new Morphia();
		morphia.map(Company.class);
		ds = morphia.createDatastore(mongo, "secondmarket");
	}

	public CompanyDAOImpl(SMProperties wikiProperty) {
		try {

			mongo = new Mongo("localhost", 27017);
			db = mongo.getDB("secondmarket");
			dbCollection = db.getCollection("company");
//			dbCollection.drop();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			System.out.println("MongoDB is not running!");
			e.printStackTrace();
		}
		morphia = new Morphia();
		morphia.map(Company.class);
		ds = morphia.createDatastore(mongo, "secondmarket");
		aggregator = new DataAggregator(wikiProperty);
	}

	public void saveMasterlist(List<Object> masterList) {
		// Using different collection
		DBCollection coll = db.getCollection("companies");
		coll.drop();
		BasicDBObject doc = new BasicDBObject();
		doc.put("masterlist", masterList);
		coll.insert(doc);
	}

	public List<Object> getMasterList() {
		DBCollection coll = db.getCollection("companies");
		DBObject companies = coll.findOne();
		List<Object> masterList = (List<Object>) companies.get("masterlist");
		return masterList;
	}

	public void saveCompany(String companyName,
			Map<String, String> crunchbaseDoc, Map<String, String> wikipediaDoc,
			Map<String, EdgarCompanyDetail> edgarDoc) {
		Company company = new Company();
		BasicDBObject cbBasicDBObject = (BasicDBObject) JSON.parse(gson
				.toJson(crunchbaseDoc));
		BasicDBObject wikiBasicDBObject = (BasicDBObject) JSON.parse(gson
				.toJson(wikipediaDoc));
		aggregator.filterAndSetCompanyData(cbBasicDBObject, wikiBasicDBObject,
				company);
		company.setCrunchbaseDoc(crunchbaseDoc);
		company.setWikipediaDoc(wikipediaDoc);
		company.setEdgarDoc(edgarDoc);
		ds.save(company);
	}

	public Company findCompanyByName(String companyName) {
		Company company = ds.find(Company.class).field("companyName")
				.equal(companyName).get();

		/*
		 * System.out.println("*************");
		 * System.out.println(company.getCountry());
		 * System.out.println(company.getFunding());
		 * System.out.println(company.getFundingAmount());
		 * System.out.println(company.getIndustry());
		 * System.out.println(company.getLocation());
		 * System.out.println(company.getOverview()); List<FundingRound> list =
		 * company.getFundings(); for (FundingRound round : list) {
		 * System.out.println("==========");
		 * System.out.println(round.getRoundCode());
		 * System.out.println(round.getRaisedAmount());
		 * System.out.println(round.getRaisedCurrencyCode());
		 * System.out.println(round.getFundedDate()); List<String> investorList
		 * = round.getInvestorList(); if (investorList != null) { for (String
		 * investorName : investorList) { System.out.println(investorName); } }
		 * 
		 * System.out.println("=========="); }
		 * 
		 * System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		 */

		return company;
	}

	public List<Company> findCompaniesByImpreciseName(String companyName) {
		List<Company> companyList = ds.find(Company.class).field("companyName")
				.equal(Pattern.compile(companyName, Pattern.CASE_INSENSITIVE))
				.asList();
		return companyList;
	}

	public List<Company> findCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage) {
		List<Company> companyList = null;
		if (pageIndex != 0) {
			companyList = ds.createQuery(Company.class)
					.offset(pageIndex * numberOfElementsPerPage)
					.limit(numberOfElementsPerPage).asList();
		} else {
			companyList = ds.createQuery(Company.class)
					.limit(numberOfElementsPerPage).asList();
		}

		return companyList;
	}

	public int getPageAmount(int companiesPerPage) {
		long numberOfCompanies = ds.createQuery(Company.class).countAll();
		int numberOfPages = (int) Math.ceil(((double) numberOfCompanies)
				/ companiesPerPage);
		return numberOfPages;
	}

	public List<Company> findSortedCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage, String sortByField,
			boolean isDescending) {
		String orderStr = (isDescending) ? ("-" + sortByField) : sortByField;

		List<Company> companyList = null;
		if (pageIndex != 0) {
			companyList = ds.createQuery(Company.class).order(orderStr)
					.offset(pageIndex * numberOfElementsPerPage)
					.limit(numberOfElementsPerPage).asList();
		} else {
			companyList = ds.createQuery(Company.class).order(orderStr)
					.limit(numberOfElementsPerPage).asList();
		}

		return companyList;
	}

	public List<Company> findCompaniesByPage(int numberOfElementsPerPage,
			int pageIndex, String sortByField, boolean isDescending,
			String selectedCountry, String companyName, String industry,
			int minFunding, int maxFunding, int employees) {
		
		// pageIndex: 0
		// sortByField: fundingAmount
		// isDescending: true;
		// selectedCountry: all
		// companyName:
		// industry:
		// minFunding: 500000
		// maxFunding: 10000000
		// employees: -1
		
		
		
		return null;
	}

}
