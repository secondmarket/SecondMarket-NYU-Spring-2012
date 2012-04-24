package com.secondmarket.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * 
 * @author Ming Li
 * 
 */
@Controller
public class ImportController {
	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());
	private MasterListGenerator generator = new MasterListGenerator();
	private Importer dataImporter = new ImporterImpl();

	/**
	 * Initialize the first form page with newly created Company model
	 * 
	 * Note: returning ModelAndView instance also works
	 * 
	 * @param model
	 * @return view string "ImportAll"
	 */
	@RequestMapping(value = "/SecondMarket/importall.htm", method = RequestMethod.GET)
	public String initForm(ModelMap model) {
		logger.info("Returning ImportAll page");
		Company company = new Company();
		model.addAttribute("company", company);
		return "ImportAll";
	}

	/**
	 * Handles the import action, first generates the master company list and
	 * persist in database, then import CrunchBase company data
	 * 
	 * @param company
	 * @param result
	 * @param status
	 * @param request
	 * @param response
	 * @return ModelAndView "main"
	 */
	@RequestMapping(value = "/SecondMarket/importall.htm", method = RequestMethod.POST)
	public String importAll(@ModelAttribute("company") Company company,
			BindingResult result, SessionStatus status) {
		logger.info("Returning main page");
		status.setComplete();

		// First store the master list from CruchBase
		// TODO Master company list generated, includes 13862 companies
		// generator.storeMasterList();

		// Then import CrunchBase data
		dataImporter.storeAllCompanies();

		return "CompanyMain";

	}

	/**
	 * Displays the company detailed information when user clicks on one
	 * specific company name
	 * 
	 * @param companyName
	 * @return ModelAndView "CompanyPage"
	 */
	@RequestMapping(value = "/SecondMarket/viewcompanyinfo.htm", method = RequestMethod.GET)
	public ModelAndView checkCompanyDetailedInfo(
			@RequestParam("companyName") String companyName) {
		logger.info("Returning CompanyPage");
		Company company = dataImporter.retrieveCompanyByName(companyName);

		ModelAndView modelAndView = new ModelAndView("CompanyPage", "company",
				company);
		return modelAndView;
	}

	/**
	 * Displays the main page directly by skipping the "ImportAll" page
	 * 
	 * @return ModelAndView "main"
	 */
	@RequestMapping(value = "/SecondMarket/mainpage.htm", method = RequestMethod.GET)
	public String init(Model model) {
		logger.info("Returning main page");
		Company company = new Company();
		model.addAttribute("company", company);
		return "main";
	}

	/**
	 * Handles the AJAX request from front end page ("main.jsp"), loading the
	 * paginated companies as 10 records per page based on the page index
	 * 
	 * @param pageIndex
	 * @return the company data string in JSON
	 */
	@RequestMapping(value = "/SecondMarket/loadcompanies.htm", method = RequestMethod.GET)
	public @ResponseBody
	String getPaginatedCompaniesInJson(@RequestParam("pageIndex") int pageIndex) {
		logger.info("Loading paginated companies");
		List<Company> paginatedList = dataImporter.retrieveCompaniesInPage(
				pageIndex, 10);
		String result = dataImporter.getPaginatedDataInJson(paginatedList);
		return result;
	}

	/**
	 * Handles the AJAX request from front end page ("main.jsp"), counting the
	 * pages amount based on the number of records per page (displaying 10
	 * records here)
	 * 
	 * @return the amount of pages as JSON string (e.g. {"pageamount":"20"})
	 */
	@RequestMapping(value = "/SecondMarket/countpages.htm", method = RequestMethod.GET)
	public @ResponseBody
	String countPages() {
		logger.info("Returning pages amount");
		String result = dataImporter.getExistingPageAmount(10);
		return result;
	}

	/**
	 * Handles the search functionality of the main page If a company can be
	 * found by the exact name, display the detailed company page directly,
	 * otherwise display a list of matching companies in a new paginated page
	 * 
	 * @param company
	 * @param result
	 * @param status
	 * @return ModelAndView "CompanyPage" or "SearchMain"
	 */
	@RequestMapping(value = "/SecondMarket/search.htm", method = RequestMethod.POST)
	public ModelAndView search(@ModelAttribute("company") Company company,
			BindingResult result, SessionStatus status) {
		// clear the command object from the session
		status.setComplete();

		String companyName = company.getCompanyName();

		// if the company exists, display the company detailed page directly,
		// else display new page
		List<Company> list = dataImporter
				.retrieveCompaniesByImpreciseName(companyName);
		if (list.size() == 1) {
			Company retrievedCompany = list.get(0);
			ModelAndView modelAndView = new ModelAndView("CompanyPage",
					"company", retrievedCompany);
			return modelAndView;
		} else {
			return new ModelAndView("SearchMain", "searchedname", companyName);
		}
	}

	/**
	 * Handles the AJAX request from front end page ("SearchMain.jsp"), loading
	 * the searched-paginated companies as 10 records per page based on the page
	 * index
	 * 
	 * @param pageIndex
	 * @param searchedname
	 * @return the searched company data string in JSON
	 */
	@RequestMapping(value = "/SecondMarket/loadsearchedcompanies.htm", method = RequestMethod.GET)
	public @ResponseBody
	String getPaginatedSearchedCompaniesInJson(
			@RequestParam("pageIndex") int pageIndex,
			@RequestParam("searchedname") String searchedname) {
		logger.info("Loading searched-paginated companies");

		List<Company> paginatedList = dataImporter
				.retrieveCompaniesByImpreciseName(searchedname);

		List<Company> subPaginatedList = null;

		int numberOfPages = (int) Math
				.ceil(((double) paginatedList.size()) / 10);
		if (pageIndex < numberOfPages) {
			if (paginatedList.size() < 10) {
				subPaginatedList = paginatedList.subList(0,
						paginatedList.size());
			} else {
				if ((pageIndex * 10 + 10) < paginatedList.size()) {
					subPaginatedList = paginatedList.subList(pageIndex * 10,
							pageIndex * 10 + 10);
				} else {
					subPaginatedList = paginatedList.subList(pageIndex * 10,
							paginatedList.size());
				}
			}

			String result = dataImporter
					.getPaginatedDataInJson(subPaginatedList);
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Handles the AJAX request from front end page ("SearchMain.jsp"),
	 * calculating the page amount based on the number of records per page
	 * (displaying 10 records here)
	 * 
	 * @param searchedname
	 * @return the number of pages as string
	 */
	@RequestMapping(value = "/SecondMarket/countsearchedpages.htm", method = RequestMethod.GET)
	public @ResponseBody
	String countSearchedPages(@RequestParam("searchedname") String searchedname) {
		logger.info("Returning searched pages amount");

		List<Company> paginatedList = dataImporter
				.retrieveCompaniesByImpreciseName(searchedname);
		int numberOfPages = (int) Math
				.ceil(((double) paginatedList.size()) / 10);

		return String.valueOf(numberOfPages);
	}

	/**
	 * Handles the AJAX request from front end page ("main.jsp"), loading the
	 * sorted companies as 10 records per page based on descending/ascending
	 * order flag (displaying 10 records here)
	 * 
	 * @param pageIndex
	 * @param sortByField
	 * @param isDescending
	 * @return the sorted companies data string in JSON
	 */
	@RequestMapping(value = "/SecondMarket/loadsortedcompanies.htm", method = RequestMethod.GET)
	public @ResponseBody
	String getSortedAndPaginatedCompaniesInJson(
			@RequestParam("pageIndex") int pageIndex,
			@RequestParam("sortByField") String sortByField,
			@RequestParam("isDescending") String isDescending) {
		logger.info("Loading sorted paginated companies");

		List<Company> paginatedList = dataImporter
				.retrieveSortedCompaniesInPage(pageIndex, 10, sortByField,
						Boolean.parseBoolean(isDescending));
		// String result = dataImporter.getPaginatedDataInJson(paginatedList);
		String result = dataImporter
				.jsonizeDataForCompanyMainPage(paginatedList);
		return result;
	}

	/**
	 * Handles the request from html "<img>" tag loading company logo
	 * 
	 * @param companyName
	 * @return
	 */
	@RequestMapping(value = "/SecondMarket/getLogo.htm", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getCompanyLogo(
			@RequestParam("companyName") String companyName) {
		Company company = dataImporter.searchCompanyByName(companyName);
		byte[] companyLogo = company.getLogo();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.IMAGE_PNG);
		responseHeaders.setContentLength(companyLogo.length);
		return new ResponseEntity<byte[]>(companyLogo, responseHeaders,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/SecondMarket/CompanyMain.htm", method = RequestMethod.GET)
	public String initCompanyMain(Model model) {
		Company company = new Company();
		model.addAttribute("company", company);
		return "CompanyMain";
	}

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

	@RequestMapping(value = "/SecondMarket/viewcompanyprofile.htm", method = RequestMethod.GET)
	public ModelAndView companyProfilePage(
			@RequestParam("companyName") String companyName) {
		Company company = dataImporter.retrieveCompanyByName(companyName);

		ModelAndView modelAndView = new ModelAndView("CompanyProfile",
				"company", company);
		return modelAndView;
	}

	@RequestMapping(value = "/SecondMarket/getOfficesByCompanyName.htm", method = RequestMethod.GET)
	public @ResponseBody
	String retrieveOfficesData(@RequestParam("companyName") String companyName) {
		Company company = dataImporter.retrieveCompanyByName(companyName);
		String result = dataImporter.jsonizeOffices(company);
		return result;
	}
}
