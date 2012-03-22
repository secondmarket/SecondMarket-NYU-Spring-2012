package com.secondmarket.importer;

import java.util.List;

import com.secondmarket.model.Company;

public interface Importer {

	void storeAllCompaniess();

	List<Company> retrieveAllCompanies();

	Company retrieveCompanyByName(String companyName);

	List<Company> retrieveCompaniesByImpreciseName(String companyName);

	List<Company> retrieveCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage);

	List<Company> retrieveSortedCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage, String sortByField,
			boolean isDescending);

	String getPaginatedDataInJson(List<Company> paginatedList);

	String getExistingPageAmount(int companiesPerPage);
}
