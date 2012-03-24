package com.secondmarket.dao;

import java.util.List;
import java.util.Map;

import com.secondmarket.model.Company;

/**
 * 
 * @author Ming Li
 *
 */
public interface CompanyDAO {

	void saveCompany(String companyName, Map<String, String> map, Map<String, String> wikiMap);

	List<Company> findAllCompanies();

	Company findCompanyByName(String companyName);

	List<Company> findCompaniesByImpreciseName(String companyName);

	List<Company> findCompaniesInPage(int pageIndex, int numberOfElementsPerPage);

	List<Company> findSortedCompaniesInPage(int pageIndex,
			int numberOfElementsPerPage, String sortByField,
			boolean isDescending);

	int getPageAmount(int companiesPerPage);

	void saveMasterlist(List<Object> masterList);

	List<Object> getMasterList();
}
