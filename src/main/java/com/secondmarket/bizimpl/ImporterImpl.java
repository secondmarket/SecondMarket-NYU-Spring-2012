package com.secondmarket.bizimpl;

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
import com.secondmarket.biz.Importer;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.model.Company;
import com.secondmarket.model.EdgarCompanyDetail;
import com.secondmarket.model.Office;
import com.secondmarket.properties.SMProperties;
import com.secondmarket.utility.CrunchBaseUtils;
import com.secondmarket.utility.DataMapper;
import com.secondmarket.utility.EdgarUtils;
import com.secondmarket.utility.WikipediaUtils;

/**
 * 
 * @author Ming Li
 * 
 */
public final class ImporterImpl implements Importer {

	private CompanyDAO companyDao;
	private Gson gson;
	private CrunchBaseUtils crunchbaseUtils;
	private WikipediaUtils wikiUtils;
	private EdgarUtils edgarUtils;
	private SMProperties wikiProperty;

	public ImporterImpl() {
		gson = new Gson();
		try {
			wikiProperty = SMProperties.getInstance("WIKI");

		} catch (Exception e) {
			e.printStackTrace();
		}
		wikiUtils = new WikipediaUtils(wikiProperty);
		crunchbaseUtils = new CrunchBaseUtils();
		edgarUtils = new EdgarUtils();
		companyDao = new CompanyDAOImpl(wikiProperty);
	}

	public void storeAllCompanies() {
		List<Object> masterList = companyDao.getMasterList();

		List<Object> list = masterList.subList(0, 111);

		Map<String, String> nameAndPermalinkMap = null;
		Map<String, String> crunchbaseDoc = null;
		Map<String, String> wikipediaDoc = null;
		Map<String, EdgarCompanyDetail> edgarDoc = null;
		String url_CrunchBase = null;
		String url_Wikipedia = null;
		String companyName = null;
		String state = null;

		String title = null;
		String name = null;
		int count = 0;

		for (int i = 0; i < list.size(); i++) {
			nameAndPermalinkMap = (Map<String, String>) list.get(i);
			companyName = nameAndPermalinkMap.get("permalink");
			url_CrunchBase = "http://api.crunchbase.com/v/1/company/"
					+ companyName + ".js";
			crunchbaseDoc = DataMapper.getDataInMapFromAPI(url_CrunchBase);
			
			state = crunchbaseUtils.getCompanyLocationState(crunchbaseDoc)[0];
			name = crunchbaseUtils.getCompanyLocationState(crunchbaseDoc)[1];
			
			if(name == null || name.length() == 0 ){
				title = wikiUtils.findCompanyUrl(companyName);
				edgarDoc = edgarUtils.getEdgarDoc(companyName, state);
//				System.out.println("OLD::" + companyName + "****************"+state);
			}else{
				title = wikiUtils.findCompanyUrl(name);
				edgarDoc = edgarUtils.getEdgarDoc(name, state);
//				System.out.println("NEW::" + name + "+****************+"+ companyName+ "***"+state);
			}
			
			if (title == null) {
				count++;
			} else {
				url_Wikipedia = "http://en.wikipedia.org/w/api.php?action=query&titles="
						+ title + "&prop=revisions&rvprop=content&format=json";

				wikipediaDoc = DataMapper.getDataInMapFromAPI(url_Wikipedia);
			}

			// TODO pass one more map for wikipedia doc
			companyDao.saveCompany(nameAndPermalinkMap.get("name"),
					crunchbaseDoc, wikipediaDoc, edgarDoc);
			wikipediaDoc = null;
			edgarDoc = null;
		}
//		System.out.println(count);
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

	public Company searchCompanyByName(String companyName) {
		Company company = companyDao.findCompanyByName(companyName);
		return company;
	}

	public String jsonizeDataForCompanyMainPage(List<Company> paginatedList) {
		// Notice: use LinkedHashMap instead of HashMap
		// To maintain the order of JSON data
		Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();

		// Loop through the list and generate JSON string
		Iterator<Company> companyIterator = paginatedList.iterator();
		while (companyIterator.hasNext()) {
			Company company = companyIterator.next();
			Map<String, Object> companyJson = new HashMap<String, Object>();
			// Jsonize company name
			companyJson.put("name", company.getCompanyName());

			// Jsonize company address
			Office office = company.getOffices().get(0);
			if (office != null) {
				String address = "";
				if (!"undefined".equals(office.getCity())) {
					address = address + office.getCity();
				}
				if (!"undefined".equals(office.getStatecode())) {
					address = address + ", " + office.getStatecode();
				}
				if (!"undefined".equals(office.getZipcode())) {
					address = address + " " + office.getZipcode();
				}
				if (!"undefined".equals(office.getCountrycode())) {
					address = address + ", " + office.getCountrycode();
				}
				companyJson.put("address", address);
			} else {
				companyJson.put("address", "Unknown");
			}

			// Jsonize company total funding
			companyJson.put("funding", company.getFunding());

			// Jsonize company category (industry)
			companyJson.put("industry", company.getIndustry());

			// Jsonize company employees
			companyJson.put("employees", company.getEmployees());

			// Jsonize company founded date
			companyJson.put("foundedDate", company.getFoundedDate());

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

}
