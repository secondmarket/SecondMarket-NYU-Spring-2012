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
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.importer.Importer;
import com.secondmarket.model.Company;
import com.secondmarket.utility.DataMapper;

public final class CrunchBaseImporter implements Importer {

	private CompanyDAO companyDao;
	private Gson gson;

	public CrunchBaseImporter() {
		companyDao = new CompanyDAOImpl();
		gson = new Gson();
	}

	public void storeAllCompanies() {
		List<Object> list = companyDao.getMasterList();
		Map<String, String> nameAndPermalinkMap = null;
		Map<String, String> map = null;
		String companyUrl = null;

		for (int i = 0; i < list.size(); i++) {
			nameAndPermalinkMap = (Map<String, String>) list.get(i);
			companyUrl = "http://api.crunchbase.com/v/1/company/"
					+ nameAndPermalinkMap.get("permalink") + ".js";
			map = DataMapper.getDataInMapFromAPI(companyUrl);
			companyDao.saveCompany(nameAndPermalinkMap.get("name"), map);
		}
	}

	public List<Company> retrieveAllCompanies() {
		List<Company> list = companyDao.findAllCompanies();
		return list;
	}

	public Company retrieveCompanyByName(String companyName) {
		Company company = companyDao.findCompanyByName(companyName);
		return company;
	}

	public List<Company> retrieveCompaniesByImpreciseName(String companyName) {
		List<Company> list = companyDao
				.findCompaniesByImpreciseName(companyName);
		return list;
	}

	public List<Company> retrieveCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage) {
		List<Company> paginatedList = companyDao.findCompaniesInPage(pageIndex,
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
		int numberOfPages = companyDao.getPageAmount(companiesPerPage);
		return String.valueOf(numberOfPages);
	}

	public List<Company> retrieveSortedCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage, String sortByField,
			boolean isDescending) {
		List<Company> paginatedList = companyDao.findSortedCompaniesInPage(
				pageIndex, numberOfElementsPerPage, sortByField, isDescending);
		return paginatedList;
	}

}
