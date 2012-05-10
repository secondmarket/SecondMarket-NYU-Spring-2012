package com.secondmarket.biz;

import java.util.List;

import com.secondmarket.model.Company;

/**
 * Importer Interface. Defines all the Importer related functions
 * 
 * @author Ming Li
 * 
 */
public interface Importer {

	void storeAllCompanies();

	Company retrieveCompanyByName(String companyName);
	
	Company retrieveCompanyByIndex(int companyIndex);

	List<Company> retrieveCompaniesByImpreciseName(String companyName);

	List<Company> retrieveCompaniesByPage(int numberOfElementsPerPage,
			int pageIndex, String sortByField, boolean isDescending,
			String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees);

	String getPageAmount(int numberOfElementsPerPage, String sortByField,
			boolean isDescending, String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees);

	String jsonizeDataForCompanyMainPage(List<Company> paginatedList);

	String jsonizeOffices(Company company);

	String getExistingPageAmount(int companiesPerPage);

	Company searchCompanyByName(String companyName);
}
