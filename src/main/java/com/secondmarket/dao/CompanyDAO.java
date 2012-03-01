package com.secondmarket.dao;

import java.util.Map;

import com.secondmarket.model.Company;

public interface CompanyDAO {

	void saveCompany(Map<String, Object> map);
	void findCompanyByName(String companyName);
	Company findCompanies();
	Company filterCompanyDataAndCreateModel(Object object);
}
