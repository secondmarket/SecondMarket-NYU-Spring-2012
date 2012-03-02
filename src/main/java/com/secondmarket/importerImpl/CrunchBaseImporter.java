package com.secondmarket.importerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.importer.Importer;
import com.secondmarket.model.Company;

public final class CrunchBaseImporter implements Importer {

	private CompanyDAO orgDao;

	public CrunchBaseImporter() {
		orgDao = new CompanyDAOImpl();
	}

	/**
	 * Calls the CrunchBase API and returns the data in a map
	 * 
	 * @param url
	 * @return Map<String, Object> map
	 */
	private Map<String, Object> getDataMapFromCrunchBase(String url) {
		URL resource = null;
		try {
			resource = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().withDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		InputStream input = null;
		Map<String, Object> map = null;
		try {
			try {
				input = resource.openStream();
				map = (Map<String, Object>) mapper.readValue(input, Map.class);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private List<Object> getDataListFromCrunchBase(String url) {
		URL resource = null;
		try {
			resource = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().withDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		InputStream input = null;
		List<Object> list = null;
		try {
			try {
				input = resource.openStream();
				list = mapper.readValue(input, List.class);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Stores the company data in database
	 */
	public void storeOneCompany(String companyName) {
		String url = "http://api.crunchbase.com/v/1/company/" + companyName
				+ ".js";
		Map<String, Object> map = getDataMapFromCrunchBase(url);
		orgDao.saveCompany(map);
	}

	public Company retrieveOneCompany() {
		Company company = orgDao.findCompany();
		return company;
	}

	public void storeAllCompaniess() {
		String url = "http://api.crunchbase.com/v/1/companies.js";
		List<Object> allCompaniesList = getDataListFromCrunchBase(url);
		// Get the first one-hundred companies in a list
		List<Object> list = allCompaniesList.subList(0, 10);

		Map<String, Object> tempMap = null;
		Map<String, Object> map = null;
		String companyUrl = null;

		for (int i = 0; i < list.size(); i++) {
			tempMap = (Map<String, Object>) list.get(i);
			if (tempMap.containsKey("permalink")) {
				companyUrl = "http://api.crunchbase.com/v/1/company/"
						+ tempMap.get("permalink") + ".js";
				map = getDataMapFromCrunchBase(companyUrl);
				orgDao.saveCompany(map);
			} else {
				System.out.println("No permalink found!");
			}
		}

		System.out.println("There are " + allCompaniesList.size()
				+ " companies!!!!!");
	}

	public List<Company> retrieveAllCompanies() {
		List<Company> list = orgDao.findAllCompanies();
		return list;
	}
	
}
