package com.secondmarket.importer;

import com.secondmarket.model.Company;



public interface Importer {

	void storeDataByCompanyName(String companyName);
	Company retrieveCompanyData();
}
