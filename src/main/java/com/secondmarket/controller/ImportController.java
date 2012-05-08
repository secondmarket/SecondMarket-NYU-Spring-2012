package com.secondmarket.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.secondmarket.biz.Importer;
import com.secondmarket.bizimpl.ImporterImpl;
import com.secondmarket.bizimpl.MasterListGenerator;
import com.secondmarket.model.Company;

/**
 * Spring MVC framework controller, provides access to the application behavior
 * which is typically defined by a service interface
 * 
 * @author Ming Li
 * 
 */
@Controller
public class ImportController {
	/** Logger for this class and subclasses */
	private MasterListGenerator generator = new MasterListGenerator();
	private Importer dataImporter = new ImporterImpl();

	/**
	 * Initialize the first page with newly created Company model
	 * 
	 * Note: returning ModelAndView instance also works
	 * 
	 * @param model
	 * @return view string "ImportAll"
	 */
	@RequestMapping(value = "/SecondMarket/importall.htm", method = RequestMethod.GET)
	public String initForm(ModelMap model) {
		Company company = new Company();
		model.addAttribute("company", company);
		return "ImportAll";
	}

	/**
	 * Handles the "IMPORTALL" action, first generates the master company list
	 * and saves the master list in database.
	 * 
	 * Secondly, brings in all the company data by referencing the master list
	 * 
	 * @param company
	 * @param result
	 * @param status
	 * @param request
	 * @param response
	 * @return ModelAndView "CompanyMain"
	 */
	@RequestMapping(value = "/SecondMarket/importall.htm", method = RequestMethod.POST)
	public String importAll(@ModelAttribute("company") Company company,
			BindingResult result, SessionStatus status) {
		status.setComplete();

		// First store the master list from CruchBase
		// TODO Master company list generated, includes 13862 companies
		// generator.storeMasterList();

		// Then import CrunchBase data
		dataImporter.storeAllCompanies();

		return "CompanyMain";

	}

	/**
	 * Handles the request from html "<img>" tag loading company logo
	 * 
	 * @param companyName
	 * @return image data in binary
	 */
	@RequestMapping(value = "/SecondMarket/getLogo.htm", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getCompanyLogo(
			@RequestParam("companyIndex") int companyIndex) {
		Company company = dataImporter.retrieveCompanyByIndex(companyIndex);
		byte[] companyLogo = company.getLogo();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.IMAGE_PNG);
		responseHeaders.setContentLength(companyLogo.length);
		return new ResponseEntity<byte[]>(companyLogo, responseHeaders,
				HttpStatus.OK);
	}

	/**
	 * Handles the request from html "<img>" tag loading company profile image
	 * logo
	 * 
	 * @param companyName
	 * @return image data in binary
	 */
	@RequestMapping(value = "/SecondMarket/getProfileLogo.htm", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getProfileLogo(
			@RequestParam("companyIndex") int companyIndex) {
		Company company = dataImporter.retrieveCompanyByIndex(companyIndex);
		byte[] profileLogo = company.getProfileLogo();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.IMAGE_PNG);
		responseHeaders.setContentLength(profileLogo.length);
		return new ResponseEntity<byte[]>(profileLogo, responseHeaders,
				HttpStatus.OK);
	}

	/**
	 * Returns the view "CompanyMain" with Company data object
	 * 
	 * @param model
	 * @return view string "CompanyMain"
	 */
	@RequestMapping(value = "/SecondMarket/CompanyMain.htm", method = RequestMethod.GET)
	public String initCompanyMain(Model model) {
		Company company = new Company();
		model.addAttribute("company", company);
		return "CompanyMain";
	}

	/**
	 * Handles the AJAX request from paging.js, returns all the company data in
	 * JSON based on the multiple parameters
	 * 
	 * @param pageIndex
	 * @param sortByField
	 * @param isDescending
	 * @param selectedCountry
	 * @param companyName
	 * @param industry
	 * @param minFunding
	 * @param maxFunding
	 * @param employees
	 * @return JSONIZED data in String
	 */
	@RequestMapping(value = "/SecondMarket/loadcompany.htm", method = RequestMethod.GET)
	public @ResponseBody
	String load(@RequestParam("pageIndex") int pageIndex,
			@RequestParam("sortByField") String sortByField,
			@RequestParam("isDescending") boolean isDescending,
			@RequestParam("selectedCountry") String selectedCountry,
			@RequestParam("companyName") String companyName,
			@RequestParam("industry") String industry,
			@RequestParam("minFunding") int minFunding,
			@RequestParam("maxFunding") int maxFunding,
			@RequestParam("employees") int employees) {

		String[] industryArray = industry.split("\\,");
		List<String> industryList = new ArrayList<String>(
				Arrays.asList(industryArray));

		List<Company> paginatedList = dataImporter.retrieveCompaniesByPage(10,
				pageIndex, sortByField, isDescending, selectedCountry,
				companyName, industryList, minFunding, maxFunding, employees);

		String result = dataImporter
				.jsonizeDataForCompanyMainPage(paginatedList);
		return result;
	}

	/**
	 * Handles the AJAX request from paging.js to return the page amount of the
	 * companies based on the multiple parameters
	 * 
	 * @param sortByField
	 * @param isDescending
	 * @param selectedCountry
	 * @param companyName
	 * @param industry
	 * @param minFunding
	 * @param maxFunding
	 * @param employees
	 * @return
	 */
	@RequestMapping(value = "/SecondMarket/getPageAmount.htm", method = RequestMethod.GET)
	public @ResponseBody
	String getPageAmount(@RequestParam("sortByField") String sortByField,
			@RequestParam("isDescending") boolean isDescending,
			@RequestParam("selectedCountry") String selectedCountry,
			@RequestParam("companyName") String companyName,
			@RequestParam("industry") String industry,
			@RequestParam("minFunding") int minFunding,
			@RequestParam("maxFunding") int maxFunding,
			@RequestParam("employees") int employees) {
		String[] industryArray = industry.split("\\,");
		List<String> industryList = new ArrayList<String>(
				Arrays.asList(industryArray));
		String result = dataImporter.getPageAmount(10, sortByField,
				isDescending, selectedCountry, companyName, industryList,
				minFunding, maxFunding, employees);
		return result;
	}

	/**
	 * Returns the model and view "CompanyProfile" for the company profile page,
	 * to display all the specified company data
	 * 
	 * @param companyName
	 * @return
	 */
	@RequestMapping(value = "/SecondMarket/viewcompanyprofile.htm", method = RequestMethod.GET)
	public ModelAndView companyProfilePage(
			@RequestParam("companyIndex") int companyIndex) {
		Company company = dataImporter.retrieveCompanyByIndex(companyIndex);

		ModelAndView modelAndView = new ModelAndView("CompanyProfile",
				"company", company);
		return modelAndView;
	}

	/**
	 * Returns the JSONIZED offices data in string for the specified company to
	 * feed the GoogleMap API function
	 * 
	 * @param companyName
	 * @return
	 */
	@RequestMapping(value = "/SecondMarket/getOfficesByCompanyIndex.htm", method = RequestMethod.GET)
	public @ResponseBody
	String retrieveOfficesData(@RequestParam("companyIndex") int companyIndex) {
		Company company = dataImporter.retrieveCompanyByIndex(companyIndex);
		String result = dataImporter.jsonizeOffices(company);
		return result;
	}
}
