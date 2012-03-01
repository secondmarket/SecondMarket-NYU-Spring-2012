package com.secondmarket.importerImpl;

import junit.framework.TestCase;

import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;

public class CrunchBaseImporterTest extends TestCase {

	public void testCallCrunchBaseImporter() throws Exception {
		CrunchBaseImporter importer = new CrunchBaseImporter();
		importer.storeDataByCompanyName("facebook");
		assertNotNull("Testing");
		
	}

	/*
	 * public static void main(String[] args) throws Exception { String url =
	 * "http://api.crunchbase.com/v/1/company/facebook.js"; URL resource = new
	 * URL(url); ObjectMapper mapper = new ObjectMapper();
	 * 
	 * mapper.getDeserializationConfig().setDateFormat( new
	 * SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));
	 * 
	 * mapper.getDeserializationConfig().withDateFormat( new
	 * SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy"));
	 * 
	 * mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
	 * 
	 * InputStream input = null; try { input = resource.openStream();
	 * Map<String, Object> map = (Map<String, Object>) mapper.readValue( input,
	 * Map.class); System.out.println(map); } finally { if (input != null)
	 * input.close(); } }
	 */
}
