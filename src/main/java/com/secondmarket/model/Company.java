package com.secondmarket.model;

import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Transient;

/**
 * 
 * @author Ming Li
 * 
 */
@Entity(value = "company", concern = "safe")
public class Company {
	@Id
	private ObjectId id;

	private String companyName;

	private Map<String, String> crunchbaseDoc;

	private Map<String, String> wikipediaDoc;

	private String location;

	private String country;

	private String funding;

	private double fundingAmount;

	private String industry;

	@Transient
	private String overview;

	public Company() {

	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Map<String, String> getCrunchbaseDoc() {
		return crunchbaseDoc;
	}

	public Map<String, String> getWikipediaDoc() {
		return wikipediaDoc;
	}

	public void setWikipediaDoc(Map<String, String> wikipediaDoc) {
		this.wikipediaDoc = wikipediaDoc;
	}

	public void setCrunchbaseDoc(Map<String, String> crunchbaseDoc) {
		this.crunchbaseDoc = crunchbaseDoc;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFunding() {
		return funding;
	}

	public void setFunding(String funding) {
		this.funding = funding;
	}

	public double getFundingAmount() {
		return fundingAmount;
	}

	public void setFundingAmount(double fundingAmount) {
		this.fundingAmount = fundingAmount;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

}
