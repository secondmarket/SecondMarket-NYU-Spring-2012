package com.secondmarket.importer;

import java.util.List;

import com.secondmarket.model.Company;

public interface Importer {

	void storeAllCompaniess();

	List<Company> retrieveAllCompanies();

	Company retrieveCompanyByName(String companyName);
}
