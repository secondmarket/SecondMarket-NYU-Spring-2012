package com.secondmarket.importerImpl;

import com.secondmarket.importer.Importer;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestCrunchBaseImporter extends TestCase{
	
	public void testGetAllCompaniesData(){
		Importer dataImporter = new CrunchBaseImporter();
		dataImporter.storeAllCompaniess();
		Assert.assertTrue(true);
	}
}
