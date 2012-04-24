package com.secondmarket.biz;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import com.secondmarket.model.Company;

/**
 * 
 * @author Ming Li
 * 
 */
public interface Importer {

	void storeAllCompanies();

	Company retrieveCompanyByName(String companyName);

	List<Company> retrieveCompaniesByImpreciseName(String companyName);

	List<Company> retrieveCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage);

	List<Company> retrieveCompaniesByPage(int numberOfElementsPerPage,
			int pageIndex, String sortByField, boolean isDescending,
			String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees);

	String getPageAmount(int numberOfElementsPerPage, String sortByField,
			boolean isDescending, String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees);

	List<Company> retrieveSortedCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage, String sortByField,
			boolean isDescending);

	String getPaginatedDataInJson(List<Company> paginatedList);

	String jsonizeDataForCompanyMainPage(List<Company> paginatedList);

	String jsonizeOffices(Company company);

	String getExistingPageAmount(int companiesPerPage);

	Company searchCompanyByName(String companyName);
}
