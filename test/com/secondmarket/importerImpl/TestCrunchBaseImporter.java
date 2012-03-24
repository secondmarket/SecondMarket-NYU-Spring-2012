package com.secondmarket.importerImpl;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.secondmarket.biz.Importer;
import com.secondmarket.bizimpl.ImporterImpl;

public class TestCrunchBaseImporter extends TestCase {

	public void testGetAllCompaniesData() {
		Importer dataImporter = new ImporterImpl();
		dataImporter.storeAllCompanies();
		Assert.assertTrue(true);
	}

}
