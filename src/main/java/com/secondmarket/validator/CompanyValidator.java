package com.secondmarket.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.secondmarket.model.Company;

public class CompanyValidator implements Validator {

	public boolean supports(Class clazz) {
		return Company.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "companyName",
				"required.companyName",
				"Company name is required for importing data");

		// Company company = (Company) target;
		// if (!"facebook".equals(company.getCompanyName())) {
		// errors.rejectValue("companyName", "notmatch.companyName");
		// }

	}
}
