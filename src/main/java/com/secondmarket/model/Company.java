package com.secondmarket.model;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

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

	private String homepageurl;

	private String location;

	private String country;

	private String funding;

	private double fundingAmount;

	private String industry;

	private String overview;

	private byte[] logo;

	private Map<String, String> crunchbaseDoc;

	private Map<String, String> wikipediaDoc;

	@Embedded
	private List<FundingRound> fundings;

	@Embedded
	private List<Office> offices;

	@Embedded
	private List<Relationship> relationships;

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

	public String getHomepageurl() {
		return homepageurl;
	}

	public void setHomepageurl(String homepageurl) {
		this.homepageurl = homepageurl;
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

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public List<FundingRound> getFundings() {
		return fundings;
	}

	public void setFundings(List<FundingRound> fundings) {
		this.fundings = fundings;
	}

	public List<Office> getOffices() {
		return offices;
	}

	public void setOffices(List<Office> offices) {
		this.offices = offices;
	}

	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}

}
