package com.secondmarket.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.secondmarket.importer.Importer;
import com.secondmarket.importerImpl.CrunchBaseImporter;
import com.secondmarket.model.Company;
import com.secondmarket.validator.CompanyValidator;

@Controller
@RequestMapping(value = "/SecondMarket/import.htm")
public class ImportController {
	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private CompanyValidator companyValidator;

	/**
	 * Initialize the first form page with newly created Company model
	 * 
	 * Note: returning ModelAndView instance also works
	 * 
	 * @param model
	 * @return view string
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String initForm(ModelMap model) {
		logger.info("Returning ImportForm");
		Company company = new Company();
		model.addAttribute("company", company);
		return "ImportForm";
	}

	/*
	 * @RequestMapping("/SecondMarket/import.htm") public ModelAndView
	 * displayImportPage() { return new ModelAndView("ImportForm", "company",
	 * new Company()); }
	 */

	/**
	 * Declare the validator for controller, validating requirements declared in
	 * /WEB-INF/classes/messages.properties
	 * 
	 * @param companyValidator
	 */
	@Autowired
	public ImportController(CompanyValidator companyValidator) {
		this.companyValidator = companyValidator;
	}

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
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(
			@ModelAttribute("company") Company company, BindingResult result,
			SessionStatus status) {
		companyValidator.validate(company, result);
		if (result.hasErrors()) {
			logger.info("Returning ImportForm With Error");
			return new ModelAndView("ImportForm");
		} else {
			logger.info("Returning ImportSuccess");
			status.setComplete();
			Importer dataImporter = new CrunchBaseImporter();
			dataImporter.storeOneCompany(company.getCompanyName());
			Company retrievedCompany = dataImporter.retrieveOneCompany();
			return new ModelAndView("DisplayCompany", "company",
					retrievedCompany);
		}
	}
	
}
