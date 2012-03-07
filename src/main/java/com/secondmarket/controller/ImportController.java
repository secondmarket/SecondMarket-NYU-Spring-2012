package com.secondmarket.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.secondmarket.importer.Importer;
import com.secondmarket.importerImpl.CrunchBaseImporter;
import com.secondmarket.model.Company;

@Controller
public class ImportController {
	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());
	private Importer dataImporter = new CrunchBaseImporter();

	/**
	 * Initialize the first form page with newly created Company model
	 * 
	 * Note: returning ModelAndView instance also works
	 * 
	 * @param model
	 * @return view string
	 */
	@RequestMapping(value = "/SecondMarket/importall.htm", method = RequestMethod.GET)
	public String initForm(ModelMap model) {
		logger.info("Returning ImportForm");
		Company company = new Company();
		model.addAttribute("company", company);
		return "ImportAll";
	}

	/*
	 * @RequestMapping("/SecondMarket/import.htm") public ModelAndView
	 * displayImportPage() { return new ModelAndView("ImportForm", "company",
	 * new Company()); }
	 */

	/**
	 * Handles the form submission, if the validation fails, returns the form
	 * page with error message; if the validation passes, returns the new page
	 * and display the imported data
	 * 
	 * @param company
	 * @param result
	 * @param status
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/SecondMarket/importall.htm", method = RequestMethod.POST)
	public ModelAndView importAll(@ModelAttribute("company") Company company,
			BindingResult result, SessionStatus status) {
		logger.info("Returning ImportSuccess");
		status.setComplete();

		dataImporter.storeAllCompaniess();

		List<Company> list = dataImporter.retrieveAllCompanies();

		ModelAndView modelAndView = new ModelAndView("MainPage", "company",
				company);
		modelAndView.addObject("companies", list);
		return modelAndView;
	}

	@RequestMapping(value = "/SecondMarket/viewcompanyinfo.htm", method = RequestMethod.GET)
	public ModelAndView checkCompanyDetailedInfo(
			@RequestParam("companyName") String companyName) {
		System.out.println("<-- " + companyName + " -->");

		Company company = dataImporter.retrieveCompanyByName(companyName);

		ModelAndView modelAndView = new ModelAndView("CompanyPage", "company",
				company);
		return modelAndView;
	}

}
