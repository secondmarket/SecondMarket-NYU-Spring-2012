package com.secondmarket.importerImpl;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.secondmarket.importer.Importer;

public class TestCrunchBaseImporter extends TestCase {

	public void testGetAllCompaniesData() {
		Importer dataImporter = new CrunchBaseImporter();
		dataImporter.storeAllCompanies();
		Assert.assertTrue(true);
	}

}
