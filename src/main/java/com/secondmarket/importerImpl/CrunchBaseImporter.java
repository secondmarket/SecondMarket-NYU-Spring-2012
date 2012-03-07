package com.secondmarket.importerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
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

public final class CrunchBaseImporter implements Importer {

	private CompanyDAO orgDao;
	private Gson gson;
	private DataFilter filter;// Filter data

	public CrunchBaseImporter() {
		orgDao = new CompanyDAOImpl();
		gson = new Gson();
		filter = new DataFilter();
	}

	/**
	 * Calls the CrunchBase API and returns the data in a map
	 * 
	 * @param url
	 * @return Map<String, Object> map
	 */
	private Map<String, String> getDataMapFromCrunchBase(String url) {
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
		Map<String, String> map = null;

		try {
			input = resource.openStream();
			InputStreamReader isr = new InputStreamReader(input, "UTF-8");
			BufferedReader bd = new BufferedReader(isr);

			map = (Map<String, String>) mapper.readValue(bd, Map.class);
			// Use gson to format the data
			map = (Map<String, String>) mapper.readValue(gson.toJson(map),
					Map.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
			input = resource.openStream();
			InputStreamReader isr = new InputStreamReader(input, "UTF-8");
			BufferedReader bd = new BufferedReader(isr);
			list = mapper.readValue(bd, List.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public void storeAllCompaniess() {
		String url = "http://api.crunchbase.com/v/1/companies.js";
		List<Object> allCompaniesList = getDataListFromCrunchBase(url);
		// Get the first one-hundred companies in a list
		List<Object> list = allCompaniesList.subList(0, 50);

		Map<String, String> nameAndPermalinkMap = null;
		Map<String, String> map = null;
		String companyUrl = null;
		boolean hasFunding;

		for (int i = 0; i < list.size(); i++) {
			nameAndPermalinkMap = (Map<String, String>) list.get(i);
			if (nameAndPermalinkMap.containsKey("name")
					&& nameAndPermalinkMap.containsKey("permalink")) {
				companyUrl = "http://api.crunchbase.com/v/1/company/"
						+ nameAndPermalinkMap.get("permalink") + ".js";
				map = getDataMapFromCrunchBase(companyUrl);

				BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(gson
						.toJson(map));

				hasFunding = filter.checkCompanyFundings(basicDBObject);
				if (hasFunding) {
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

}
