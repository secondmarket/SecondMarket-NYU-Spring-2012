package com.secondmarket.model;

import com.google.code.morphia.annotations.Embedded;

/**
 * 
 * @author Ming Li
 *
 */
@Embedded
public class Relationship {

	private String title;
	private String name;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
