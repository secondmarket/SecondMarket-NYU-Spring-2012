package com.secondmarket.controller;

import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

public class HelloWorldControllerTest extends TestCase {

	public void testHandleRequestView() throws Exception {
		HelloWorldController controller = new HelloWorldController();
		ModelAndView modelAndView = controller.handleRequest(null, null);
		assertEquals("HelloWorld", modelAndView.getViewName());
	}

}
