package com.secondmarket.dao;

import java.util.List;
import java.util.Map;

import com.secondmarket.model.Company;

public interface CompanyDAO {

	void saveCompany(Map<String, Object> map);
	void findCompanyByName(String companyName);
	Company findCompany();
	Company filterCompanyDataAndCreateModel(Object object);
	List<Company> findAllCompanies();
}
