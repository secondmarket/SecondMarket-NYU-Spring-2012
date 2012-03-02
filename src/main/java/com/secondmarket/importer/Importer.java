package com.secondmarket.importer;

import java.util.List;

import com.secondmarket.model.Company;

public interface Importer {

	void storeOneCompany(String companyName);

	Company retrieveOneCompany();

	void storeAllCompaniess();

	List<Company> retrieveAllCompanies();
}
