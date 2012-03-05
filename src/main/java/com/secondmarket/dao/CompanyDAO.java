package com.secondmarket.dao;

import java.util.List;
import java.util.Map;

import com.secondmarket.model.Company;

public interface CompanyDAO {

	void saveCompany(String companyName, Map<String, String> map);

	void findCompanyByName(String companyName);

	List<Company> findAllCompanies();
}
