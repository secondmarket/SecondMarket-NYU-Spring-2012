package com.secondmarket.dao;

import java.util.List;
import java.util.Map;

import com.secondmarket.model.Company;
import com.secondmarket.model.EdgarCompanyDetail;

/**
 * Company Data Access Object interface class, defines all the necessary
 * functions
 * 
 * @author Ming Li
 * 
 */
public interface CompanyDAO {

	void saveCompany(String companyName, Map<String, String> map,
			Map<String, String> wikiMap,
			Map<String, EdgarCompanyDetail> edgarMap, String wikiUrl);

	void deleteCompanyCollection();

	Company findCompanyByName(String companyName);

	List<Company> findCompaniesByImpreciseName(String companyName);

	List<Company> findCompaniesByPage(int numberOfElementsPerPage,
			int pageIndex, String sortByField, boolean isDescending,
			String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees);

	int countPages(int numberOfElementsPerPage, String sortByField,
			boolean isDescending, String selectedCountry, String companyName,
			List<String> industryList, int minFunding, int maxFunding,
			int employees);

	int getPageAmount(int companiesPerPage);

	void saveMasterlist(List<Object> masterList);

	List<Object> getMasterList();
}
