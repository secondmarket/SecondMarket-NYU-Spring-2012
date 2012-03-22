package com.secondmarket.daoimpl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
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

	private BasicDBObject basicDBObject;
	private Gson gson = new Gson();// Using Gson to format the JSON object
	private DataFilter filter = new DataFilter();// Filter data

	private Datastore ds;
	private Morphia morphia;

	public CompanyDAOImpl() {
		try {

			mongo = new Mongo("localhost", 27017);
			db = mongo.getDB("secondmarket");
			dbCollection = db.getCollection("company");
			dbCollection.drop();
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

	public void saveCompany(String companyName, Map<String, String> map) {
		Company company = new Company();
		basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));
		filter.getAndSetCompanyBasicInfo(basicDBObject, company);
		company.setData(map);
		ds.save(company);
	}

	public List<Company> findAllCompanies() {
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

	public Company findCompanyByName(String companyName) {
		Company company = ds.find(Company.class).field("companyName")
				.equal(companyName).get();

		Map<String, String> map = company.getData();
		basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));
		filter.getAndSetCompanyDetailedInfo(basicDBObject, company);
		return company;
	}

	public List<Company> findCompaniesByImpreciseName(String companyName) {
		List<Company> companyList = ds.find(Company.class).field("companyName")
				.equal(Pattern.compile(companyName, Pattern.CASE_INSENSITIVE))
				.asList();

		Iterator<Company> it = companyList.iterator();
		while (it.hasNext()) {
			Company company = it.next();
			Map<String, String> map = company.getData();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));
			filter.getAndSetCompanyDetailedInfo(basicDBObject, company);
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
			Map<String, String> map = company.getData();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));

			// Filter the data and update the company model
			filter.getAndSetCompanyBasicInfo(basicDBObject, company);
			paginatedList.add(company);
		}

		return paginatedList;
	}

	public int getPageAmount(int companiesPerPage) {
		long numberOfCompanies = ds.createQuery(Company.class).countAll();
		int numberOfPages = (int) Math.ceil(((double) numberOfCompanies) / 10);
		return numberOfPages;
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
			Map<String, String> map = company.getData();
			basicDBObject = (BasicDBObject) JSON.parse(gson.toJson(map));

			// Filter the data and update the company model
			filter.getAndSetCompanyBasicInfo(basicDBObject, company);
			paginatedList.add(company);
		}

		return paginatedList;
	}

}
