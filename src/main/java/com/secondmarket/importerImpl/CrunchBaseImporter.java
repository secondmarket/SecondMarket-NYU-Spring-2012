package com.secondmarket.importerImpl;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.secondmarket.dao.OrganizationDao;
import com.secondmarket.importer.DataImporter;

public class CrunchBaseImporter implements DataImporter {

	private OrganizationDao orgDao;

	public void setOrganizationDao(OrganizationDao orgDao) {
		this.orgDao = orgDao;
	}

	public void importOrganizationDataByName(String organizationName)
			throws Exception {
		String url = "http://api.crunchbase.com/v/1/company/"
				+ organizationName + ".js";
		URL resource = new URL(url);
		ObjectMapper mapper = new ObjectMapper();

		mapper.getDeserializationConfig().withDateFormat(
				new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));

		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

		InputStream input = null;
		try {
			input = resource.openStream();
			Map<String, Object> map = (Map<String, Object>) mapper.readValue(
					input, Map.class);
			System.out.println(map);

			orgDao.saveOrganization();

		} finally {
			if (input != null)
				input.close();
		}
	}
}
