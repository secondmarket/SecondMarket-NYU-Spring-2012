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
import com.secondmarket.properties.SMProperties;
import com.secondmarket.utility.DataAggregator;

/**
 * Company Data Access Object implementation class
 * 
 * @author Ming Li & Danjuan Ye
 * 
 */
public final class CompanyDAOImpl implements CompanyDAO {
	private DB db;
	private Mongo mongo;
	private DBCollection companyCollection;
	private Gson gson = new Gson();// Using Gson to format the JSON object
	private Datastore ds;
	private Morphia morphia;

	private DataAggregator aggregator;

	/**
	 * CompanyDAOImpl class constructor (no argument), initializes the injected
	 * utility class instances for master list generation
	 */
	public CompanyDAOImpl() {
		try {

			mongo = new Mongo("localhost", 27017);
			db = mongo.getDB("secondmarket");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			System.out.println("MongoDB is not running!");
			e.printStackTrace();
		}
		morphia = new Morphia();
		morphia.map(Company.class);
		ds = morphia.createDatastore(mongo, "secondmarket");
		ds.ensureIndex(Company.class, "fundingAmount");
		ds.ensureCaps();
	}

	/**
	 * CompanyDAOImpl class constructor (with argument), initializes the
	 * injected utility class instances for extracting data from data sources
	 * for every companies
	 * 
	 * @param wikiProperty
	 *            wikipedia criteria
	 */
	public CompanyDAOImpl(SMProperties wikiProperty) {
		try {

			mongo = new Mongo("localhost", 27017);
			db = mongo.getDB("secondmarket");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			System.out.println("MongoDB is not running!");
			e.printStackTrace();
		}
		morphia = new Morphia();
		morphia.map(Company.class);
		ds = morphia.createDatastore(mongo, "secondmarket");
		ds.ensureIndex(Company.class, "fundingAmount");
		ds.ensureCaps();
		aggregator = new DataAggregator(wikiProperty);
	}

	/**
	 * Applies the index operation for fundingAmount field in MongoDB, necessary
	 * for sorting
	 */
	public void ensureIndex() {
		ds.ensureIndex(Company.class, "fundingAmount");
		ds.ensureCaps();
	}

	/**
	 * Persists the master list in database
	 */
	public void saveMasterlist(List<Object> masterList) {
		// Using different collection
		DBCollection coll = db.getCollection("companies");
		coll.drop();
		BasicDBObject doc = new BasicDBObject();
		doc.put("masterlist", masterList);
		coll.insert(doc);
	}

	/**
	 * Retrieves the master list from database
	 */
	public List<Object> getMasterList() {
		DBCollection coll = db.getCollection("companies");
		DBObject companies = coll.findOne();
		List<Object> masterList = (List<Object>) companies.get("masterlist");
		return masterList;
	}

	/**
	 * Deletes the existing company collection Being called when importing all
	 * the company data
	 */
	public void deleteCompanyCollection() {
		companyCollection = db.getCollection("company");
		companyCollection.drop();
	}

	/**
	 * Applies the data filter for respective data source, and aggregates the
	 * data by calling model functions which maps the values into the MongoDB
	 * fields using MORPHIA, persists all the data for one company
	 */
	public void saveCompany(String companyName,
			Map<String, String> crunchbaseDoc,
			Map<String, String> wikipediaDoc,
			Map<String, EdgarCompanyDetail> edgarDoc, String wikiUrl,
			int companyIndex) {
		Company company = new Company();
		BasicDBObject cbBasicDBObject = (BasicDBObject) JSON.parse(gson
				.toJson(crunchbaseDoc));

		companyName = cbBasicDBObject.get("name").toString().trim();
		System.out.println(companyIndex + "\t" + companyName);

		BasicDBObject wikiBasicDBObject = (BasicDBObject) JSON.parse(gson
				.toJson(wikipediaDoc));
		aggregator.filterAndSetCompanyData(cbBasicDBObject, wikiBasicDBObject,
				company, companyIndex);
		company.setCrunchbaseDoc(crunchbaseDoc);
		company.setWikipediaDoc(wikipediaDoc);
		company.setEdgarDoc(edgarDoc);
		company.setWikiUrl(wikiUrl);
		ds.save(company);
	}

	/**
	 * Retrieves the company object by company name field
	 */
	public Company findCompanyByName(String companyName) {
		Company company = ds.find(Company.class).field("companyName")
				.equal(companyName).get();
		return company;
	}

	/**
	 * Retrieves the company object by imprecise company name
	 */
	public List<Company> findCompaniesByImpreciseName(String companyName) {
		List<Company> companyList = ds.find(Company.class).field("companyName")
				.equal(Pattern.compile(companyName, Pattern.CASE_INSENSITIVE))
				.asList();
		return companyList;
	}

	/**
	 * Returns the page amount for existing companies
	 */
	public int getPageAmount(int companiesPerPage) {
		long numberOfCompanies = ds.createQuery(Company.class).countAll();
		int numberOfPages = (int) Math.ceil(((double) numberOfCompanies)
				/ companiesPerPage);
		return numberOfPages;
	}

	/**
	 * Retrieves the companies by page, based on the multiple parameters
	 */
	public List<Company> findCompaniesByPage(int numberOfElementsPerPage,
			int pageIndex, String sortByField, boolean isDescending,
			String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees) {
		List<Company> companyList = null;
		String orderStr = (isDescending) ? ("-" + sortByField) : sortByField;

		if (pageIndex != 0) {
			if ("all".equals(selectedCountry)) {
				if (maxFunding == -1) {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.order(orderStr)
							.offset(pageIndex * numberOfElementsPerPage)
							.limit(numberOfElementsPerPage).asList();

				} else {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.filter("fundingAmount <=", maxFunding)
							.order(orderStr)
							.offset(pageIndex * numberOfElementsPerPage)
							.limit(numberOfElementsPerPage).asList();
				}
			} else {
				if (maxFunding == -1) {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.field("country").equal(selectedCountry)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.order(orderStr)
							.offset(pageIndex * numberOfElementsPerPage)
							.limit(numberOfElementsPerPage).asList();
				} else {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.field("country").equal(selectedCountry)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.filter("fundingAmount <=", maxFunding)
							.order(orderStr)
							.offset(pageIndex * numberOfElementsPerPage)
							.limit(numberOfElementsPerPage).asList();
				}
			}
		} else {
			if ("all".equals(selectedCountry)) {
				if (maxFunding == -1) {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.order(orderStr).limit(numberOfElementsPerPage)
							.asList();
				} else {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.filter("fundingAmount <=", maxFunding)
							.order(orderStr).limit(numberOfElementsPerPage)
							.asList();
				}
			} else {
				if (maxFunding == -1) {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.field("country").equal(selectedCountry)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.order(orderStr).limit(numberOfElementsPerPage)
							.asList();
				} else {
					companyList = ds
							.createQuery(Company.class)
							.field("companyName")
							.equal(Pattern.compile(companyName,
									Pattern.CASE_INSENSITIVE))
							.filter("industry in", industryList)
							.field("country").equal(selectedCountry)
							.filter("employees >=", employees)
							.filter("fundingAmount >=", minFunding)
							.filter("fundingAmount <=", maxFunding)
							.order(orderStr).limit(numberOfElementsPerPage)
							.asList();
				}
			}
		}

		return companyList;
	}

	/**
	 * Counts the page amount by applying the multiple parameters for the
	 * MONGODB query filters
	 */
	public int countPages(int numberOfElementsPerPage, String sortByField,
			boolean isDescending, String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees) {
		// long numberOfCompanies = ds.createQuery(Company.class).countAll();
		String orderStr = (isDescending) ? ("-" + sortByField) : sortByField;
		long numberOfCompanies = 0L;
		if ("all".equals(selectedCountry)) {
			if (maxFunding == -1) {
				numberOfCompanies = ds
						.createQuery(Company.class)
						.field("companyName")
						.equal(Pattern.compile(companyName,
								Pattern.CASE_INSENSITIVE))
						.filter("industry in", industryList)
						.filter("employees >=", employees)
						.filter("fundingAmount >=", minFunding).order(orderStr)
						.limit(numberOfElementsPerPage).countAll();
			} else {
				numberOfCompanies = ds
						.createQuery(Company.class)
						.field("companyName")
						.equal(Pattern.compile(companyName,
								Pattern.CASE_INSENSITIVE))
						.filter("industry in", industryList)
						.filter("employees >=", employees)
						.filter("fundingAmount >=", minFunding)
						.filter("fundingAmount <=", maxFunding).order(orderStr)
						.limit(numberOfElementsPerPage).countAll();
			}
		} else {
			if (maxFunding == -1) {
				numberOfCompanies = ds
						.createQuery(Company.class)
						.field("companyName")
						.equal(Pattern.compile(companyName,
								Pattern.CASE_INSENSITIVE))
						.filter("industry in", industryList).field("country")
						.equal(selectedCountry)
						.filter("employees >=", employees)
						.filter("fundingAmount >=", minFunding).order(orderStr)
						.limit(numberOfElementsPerPage).countAll();
			} else {
				numberOfCompanies = ds
						.createQuery(Company.class)
						.field("companyName")
						.equal(Pattern.compile(companyName,
								Pattern.CASE_INSENSITIVE))
						.filter("industry in", industryList).field("country")
						.equal(selectedCountry)
						.filter("employees >=", employees)
						.filter("fundingAmount >=", minFunding)
						.filter("fundingAmount <=", maxFunding).order(orderStr)
						.limit(numberOfElementsPerPage).countAll();
			}
		}

		int numberOfPages = (int) Math.ceil(((double) numberOfCompanies)
				/ numberOfElementsPerPage);

		return numberOfPages;
	}

	public Company findCompanyByIndex(int companyIndex) {
		Company company = ds.find(Company.class).field("companyIndex")
				.equal(companyIndex).get();
		return company;
	}

}
