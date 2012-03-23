package com.secondmarket.importerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.secondmarket.dao.CompanyDAO;
import com.secondmarket.daoimpl.CompanyDAOImpl;
import com.secondmarket.model.Company;
import com.secondmarket.utility.DataFilter;
import com.secondmarket.utility.DataMapper;

public final class MasterListGenerator {

	private final String url_CrunchBaseCompanies = "http://api.crunchbase.com/v/1/companies.js";
	private CompanyDAO orgDao;
	private Gson gson;

	public MasterListGenerator() {
		orgDao = new CompanyDAOImpl();
		gson = new Gson();
	}

	public void storeMasterList() {

		List<Object> allCompaniesList = DataMapper
				.getDataInListFromCrunchBase(url_CrunchBaseCompanies);

		List<Object> list = allCompaniesList.subList(0, 300);

		List<Object> eligibleCompanyList = new ArrayList<Object>();
		Map<String, String> nameAndPermalinkMap = null;
		Map<String, String> tempResponseJSONMap = null;
		String companyUrl = null;
		boolean isEligible;

		for (int i = 0; i < list.size(); i++) {
			nameAndPermalinkMap = (Map<String, String>) list.get(i);
			if (nameAndPermalinkMap.containsKey("name")
					&& nameAndPermalinkMap.containsKey("permalink")) {
				companyUrl = "http://api.crunchbase.com/v/1/company/"
						+ nameAndPermalinkMap.get("permalink") + ".js";
				tempResponseJSONMap = DataMapper
						.getDataInMapFromCrunchBase(companyUrl);

				BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(gson
						.toJson(tempResponseJSONMap));

				isEligible = DataFilter.checkCompanyEligibility(basicDBObject);
				if (isEligible) {
					eligibleCompanyList.add(list.get(i));
				} else {
					continue;
				}

			} else {
				System.out.println("No permalink found!");
			}
		}

		orgDao.saveMasterlist(eligibleCompanyList);
		System.out.println("Master company list generated");
	}

	public List<Company> retrieveMasterList() {

		return null;
	}
}
