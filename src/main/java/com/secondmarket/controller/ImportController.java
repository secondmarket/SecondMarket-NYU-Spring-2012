package com.secondmarket.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.secondmarket.importer.Importer;
import com.secondmarket.importerImpl.CrunchBaseImporter;
import com.secondmarket.model.Company;
import com.secondmarket.validator.CompanyValidator;

@Controller
public class ImportController {
	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	private Importer dataImporter;
	private CompanyValidator companyValidator;

	@RequestMapping(value = "/SecondMarket/import.htm", method = RequestMethod.GET)
	public String initForm(ModelMap model) {
		Company company = new Company();
		model.addAttribute("company", company);
		logger.info("Returning ImportForm");
		return "ImportForm";
	}

	/*
	 * @RequestMapping("/SecondMarket/import.htm") public ModelAndView
	 * displayImportPage() { return new ModelAndView("ImportForm", "company",
	 * new Company()); }
	 */

	@Autowired
	public ImportController(CompanyValidator companyValidator) {
		this.companyValidator = companyValidator;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@ModelAttribute("company") Company company,
			BindingResult result, SessionStatus status,
			HttpServletRequest request, HttpServletResponse response) {
		companyValidator.validate(company, result);
		if (result.hasErrors()) {
			logger.info("Returning ImportForm With Error");
			return "ImportForm";
		} else {
			status.setComplete();
			dataImporter = new CrunchBaseImporter();
			dataImporter.storeDataByCompanyName(company.getCompanyName());
			logger.info("Returning ImportSuccess");
			return "ImportSuccess";
		}
	}

	@RequestMapping(value = "/SecondMarket/DisplayAll.htm", method = RequestMethod.GET)
	public String displayAll(ModelMap model) {
		Company company = dataImporter.retrieveCompanyData();
		model.addAttribute("company", company);
		logger.info("Returning DisplayCompany");
		return "DisplayCompany";
	}

}
