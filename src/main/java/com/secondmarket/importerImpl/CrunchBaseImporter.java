package com.secondmarket.importerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.importer.Importer;
import com.secondmarket.model.Company;
import com.secondmarket.utility.DataFilter;
import com.secondmarket.utility.DataMapper;

public final class CrunchBaseImporter implements Importer {

	private CompanyDAO orgDao;
	private Gson gson;
	private DataFilter filter;// Filter data
	private DataMapper mapper;

	public CrunchBaseImporter() {
		orgDao = new CompanyDAOImpl();
		gson = new Gson();
		filter = new DataFilter();
		mapper = new DataMapper();
	}

	public void storeAllCompaniess() {
		String url = "http://api.crunchbase.com/v/1/companies.js";
		List<Object> allCompaniesList = mapper.getDataInListFromCrunchBase(url);

		List<Object> list = allCompaniesList.subList(0, 300);

		Map<String, String> nameAndPermalinkMap = null;
		Map<String, String> map = null;
		String companyUrl = null;
		boolean isEligible;

		for (int i = 0; i < list.size(); i++) {
			nameAndPermalinkMap = (Map<String, String>) list.get(i);
			if (nameAndPermalinkMap.containsKey("name")
					&& nameAndPermalinkMap.containsKey("permalink")) {
				companyUrl = "http://api.crunchbase.com/v/1/company/"
						+ nameAndPermalinkMap.get("permalink") + ".js";
				map = mapper.getDataInMapFromCrunchBase(companyUrl);

				BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(gson
						.toJson(map));

				isEligible = filter.checkCompanyEligibility(basicDBObject);
				if (isEligible) {
					orgDao.saveCompany(nameAndPermalinkMap.get("name"), map);
				} else {
					continue;
				}

			} else {
				System.out.println("No permalink found!");
			}
		}

	}

	public List<Company> retrieveAllCompanies() {
		List<Company> list = orgDao.findAllCompanies();
		return list;
	}

	public Company retrieveCompanyByName(String companyName) {
		Company company = orgDao.findCompanyByName(companyName);
		return company;
	}

	public List<Company> retrieveCompaniesByImpreciseName(String companyName) {
		List<Company> list = orgDao.findCompaniesByImpreciseName(companyName);
		return list;
	}

	public List<Company> retrieveCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage) {
		List<Company> paginatedList = orgDao.findCompaniesInPage(pageIndex,
				numberOfElementsPerPage);
		return paginatedList;
	}

	public String getPaginatedDataInJson(List<Company> paginatedList) {
		// Notice: use LinkedHashMap instead of HashMap
		// To maintain the order of JSON data
		Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();

		// Loop through the list and generate JSON string
		Iterator<Company> companyIterator = paginatedList.iterator();
		while (companyIterator.hasNext()) {
			Company company = companyIterator.next();
			Map<String, Object> companyJson = new HashMap<String, Object>();
			companyJson.put("name", company.getCompanyName());
			companyJson.put("location", company.getLocation());
			companyJson.put("country", company.getCountry());
			companyJson.put("funding", company.getFunding());
			companyJson.put("industry", company.getIndustry());
			jsonMap.put(company.getCompanyName(), companyJson);
		}

		String jsonMapString = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonMapString = mapper.writeValueAsString(jsonMap);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Using GSON to format the data
		return gson.toJson(jsonMapString);
	}

	public String getExistingPageAmount(int companiesPerPage) {
		int numberOfPages = orgDao.getPageAmount(companiesPerPage);
		return String.valueOf(numberOfPages);
	}

	public List<Company> retrieveSortedCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage, String sortByField,
			boolean isDescending) {
		List<Company> paginatedList = orgDao.findSortedCompaniesInPage(
				pageIndex, numberOfElementsPerPage, sortByField, isDescending);
		return paginatedList;
	}

}
