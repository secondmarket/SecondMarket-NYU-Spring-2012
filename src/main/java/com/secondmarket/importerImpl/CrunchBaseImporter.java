package com.secondmarket.importerImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.importer.Importer;
import com.secondmarket.model.Company;

public class CrunchBaseImporter implements Importer {

	private CompanyDAO orgDao;

	public void storeDataByCompanyName(String companyName) {
		String url = "http://api.crunchbase.com/v/1/company/" + companyName
				+ ".js";

		// String url =
		// "http://api.crunchbase.com/v/1/person/mark-zuckerberg.js";

		// String url = "http://api.crunchbase.com/v/1/company/Pinterest.js";

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
		try {
			try {
				input = resource.openStream();
				Map<String, Object> map = (Map<String, Object>) mapper
						.readValue(input, Map.class);

				orgDao = new CompanyDAOImpl();
				orgDao.saveCompany(map);
			} finally {
				if (input != null) {
					input.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Company retrieveCompanyData() {
		Company company = orgDao.findCompanies();
		return company;
	}
}
