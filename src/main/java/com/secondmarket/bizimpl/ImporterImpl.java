package com.secondmarket.bizimpl;

import java.io.IOException;
import java.util.ArrayList;
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
 * Importer Implementation class.
 * 
 * @author Ming Li & Danjuan Ye
 * 
 */
public final class ImporterImpl implements Importer {

	private CompanyDAO companyDao;
	private Gson gson;
	private CrunchBaseUtils crunchbaseUtils;
	private WikipediaUtils wikiUtils;
	private EdgarUtils edgarUtils;
	private SMProperties wikiProperty;
	private SMProperties edgarProperty;

	/**
	 * ImporterImpl constructor, initializes the injected class instances.
	 * Create objects of Property to get the Criteria for Wikipedia and Edgar
	 */
	public ImporterImpl() {
		gson = new Gson();
		try {
			wikiProperty = SMProperties.getInstance("WIKI");
			edgarProperty = SMProperties.getInstance("EDGAR");

		} catch (Exception e) {
			e.printStackTrace();
		}
		wikiUtils = new WikipediaUtils(wikiProperty);
		crunchbaseUtils = new CrunchBaseUtils();
		edgarUtils = new EdgarUtils(edgarProperty);
		companyDao = new CompanyDAOImpl(wikiProperty);
	}

	/**
	 * Loops through the company master list and imports the company data from
	 * CrunchBase, WIKIPEDIA and EDGAR respectively
	 * 
	 * Only using sublist with 111 companies for testing, use the commented out
	 * line to import all
	 */
	public void storeAllCompanies() {
		companyDao.deleteCompanyCollection();

		List<Object> masterList = companyDao.getMasterList();
		System.out.println(masterList.size() + "*********");

//		List<Object> list = masterList.subList(0, 300);
		List<Object> list = masterList;

		Map<String, String> nameAndPermalinkMap = null;
		Map<String, String> crunchbaseDoc = null;
		Map<String, String> wikipediaDoc = null;
		Map<String, EdgarCompanyDetail> edgarDoc = null;
		String url_CrunchBase = null;
		String url_Wikipedia = null;
		String wikiUrl = null;
		String companyName = null;
		String state = null;

		String title = null;
		String name = null;
		// int count = 0;

		for (int i = 0; i < list.size(); i++) {
			nameAndPermalinkMap = (Map<String, String>) list.get(i);
			companyName = nameAndPermalinkMap.get("permalink");
			url_CrunchBase = "http://api.crunchbase.com/v/1/company/"
					+ companyName + ".js";
			crunchbaseDoc = DataMapper.getDataInMapFromAPI(url_CrunchBase);

			state = crunchbaseUtils.getCompanyState(crunchbaseDoc);
			name = crunchbaseUtils.getCompanyName(crunchbaseDoc);

			if (name == null || name.length() == 0) {

				title = wikiUtils.findCompanyUrl(companyName);
				edgarDoc = edgarUtils.getEdgarDoc(companyName, state);
				// System.out.println("OLD::" + companyName +
				// "****************"+state);
			} else {
				title = wikiUtils.findCompanyUrl(name);
				edgarDoc = edgarUtils.getEdgarDoc(name, state);
				// System.out.println("NEW::" + name + "+****************+"+
				// companyName+ "***"+state);
			}

			if (title == null) {
				url_Wikipedia = null;
				wikiUrl = null;
				// count++;
			} else {
				url_Wikipedia = "http://en.wikipedia.org/w/api.php?action=query&titles="
						+ title + "&prop=revisions&rvprop=content&format=json";

				wikiUrl = "http://en.wikipedia.org/wiki/" + title;

				wikipediaDoc = DataMapper.getDataInMapFromAPI(url_Wikipedia);
			}

			// TODO pass one more map for wikipedia doc
			companyDao.saveCompany(nameAndPermalinkMap.get("name"),
					crunchbaseDoc, wikipediaDoc, edgarDoc, wikiUrl, i);
			wikipediaDoc = null;
			edgarDoc = null;
		}
		// System.out.println(count);
	}

	/**
	 * Calls CompanyDAOImpl function to retrieve the company object by company
	 * name from database
	 */
	public Company retrieveCompanyByName(String companyName) {
		Company company = companyDao.findCompanyByName(companyName);
		return company;
	}

	/**
	 * Calls CompanyDAOImple function to retrieve the company object by
	 * imprecise company name from database
	 */
	public List<Company> retrieveCompaniesByImpreciseName(String companyName) {
		List<Company> list = companyDao
				.findCompaniesByImpreciseName(companyName);
		return list;
	}

	/**
	 * Retrieves the page amount for the existing companies
	 */
	public String getExistingPageAmount(int companiesPerPage) {
		int numberOfPages = companyDao.getPageAmount(companiesPerPage);
		return String.valueOf(numberOfPages);
	}

	/**
	 * Retrieves the company object by company name
	 */
	public Company searchCompanyByName(String companyName) {
		Company company = companyDao.findCompanyByName(companyName);
		return company;
	}

	/**
	 * Jsonizes the paginated company list which contains all the displaying
	 * data for the company main page
	 */
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
			if (company.getOffices() != null) {
				Office office = company.getOffices().get(0);
				if (office != null) {
					String address = "";
					if (!"undefined".equals(office.getCity())) {
						address = address + office.getCity();
					}
					if (!"undefined".equals(office.getStatecode())) {
						if ("".equals(address)) {
							address = office.getStatecode();
						} else {
							address = address + ", " + office.getStatecode();
						}

					}
					if (!"undefined".equals(office.getCountrycode())) {
						if ("".equals(address)) {
							address = office.getCountrycode();
						} else {
							address = address + ", " + office.getCountrycode();
						}
					}
					companyJson.put("address", address);
				} else {
					companyJson.put("address", "Unknown");
				}
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

			// Jsonize company index
			companyJson.put("companyIndex", company.getCompanyIndex());

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

	/**
	 * Retrieves the companies by page based on the number of companies per
	 * page, pageIndex as well as other parameters
	 */
	public List<Company> retrieveCompaniesByPage(int numberOfElementsPerPage,
			int pageIndex, String sortByField, boolean isDescending,
			String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees) {
		List<Company> paginatedList = companyDao.findCompaniesByPage(
				numberOfElementsPerPage, pageIndex, sortByField, isDescending,
				selectedCountry, companyName, industryList, minFunding,
				maxFunding, employees);
		return paginatedList;
	}

	/**
	 * Returns the page amount in string format
	 */
	public String getPageAmount(int numberOfElementsPerPage,
			String sortByField, boolean isDescending, String selectedCountry,
			String companyName, List<String> industryList, int minFunding,
			int maxFunding, int employees) {
		int pageAmount = companyDao.countPages(numberOfElementsPerPage,
				sortByField, isDescending, selectedCountry, companyName,
				industryList, minFunding, maxFunding, employees);
		return String.valueOf(pageAmount);
	}

	/**
	 * Jsonizes the offices data
	 */
	public String jsonizeOffices(Company company) {
		Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
		List<Office> offices = company.getOffices();
		if (offices != null) {
			List<Map<String, Object>> officeJSONList = new ArrayList<Map<String, Object>>();
			Iterator<Office> it = offices.iterator();
			while (it.hasNext()) {
				Office office = it.next();
				Map<String, Object> officeMap = new HashMap<String, Object>();
				officeMap.put("address1", office.getAddress1());
				officeMap.put("address2", office.getAddress2());
				officeMap.put("zipcode", office.getZipcode());
				officeMap.put("city", office.getCity());
				officeMap.put("statecode", office.getStatecode());
				officeMap.put("countrycode", office.getCountrycode());
				officeMap.put("latitude", office.getLatitude());
				officeMap.put("longitude", office.getLongitude());
				officeJSONList.add(officeMap);
			}
			jsonMap.put("offices", officeJSONList);
		} else {
			List<Map<String, Object>> officeJSONList = new ArrayList<Map<String, Object>>();
			Map<String, Object> officeMap = new HashMap<String, Object>();
			officeMap.put("address1", "");
			officeMap.put("address2", "");
			officeMap.put("zipcode", "");
			officeMap.put("city", "");
			officeMap.put("statecode", "");
			officeMap.put("countrycode", "");
			officeMap.put("latitude", 0.0);
			officeMap.put("longitude", 0.0);
			officeJSONList.add(officeMap);
			jsonMap.put("offices", officeJSONList);
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

	public Company retrieveCompanyByIndex(int companyIndex) {
		Company company = companyDao.findCompanyByIndex(companyIndex);
		return company;
	}
}
