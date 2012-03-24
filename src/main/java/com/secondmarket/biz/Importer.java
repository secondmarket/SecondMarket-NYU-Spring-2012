package com.secondmarket.biz;

import java.util.List;

import com.secondmarket.model.Company;

/**
 * 
 * @author Ming Li
 *
 */
public interface Importer {

	void storeAllCompanies();

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
