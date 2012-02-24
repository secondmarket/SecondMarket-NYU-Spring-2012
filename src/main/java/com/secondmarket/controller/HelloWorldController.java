package com.secondmarket.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class HelloWorldController implements Controller {

	protected final Log logger = LogFactory.getLog(getClass());

	public ModelAndView handleRequest(HttpServletRequest arg0,
			HttpServletResponse arg1) throws Exception {

		ModelAndView model = new ModelAndView("HelloWorld");
		model.addObject("msg", ", my first Spring test");

		logger.info("Returning HelloWorld");

		return model;
	}
}
