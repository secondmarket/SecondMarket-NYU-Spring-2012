package com.secondmarket.model;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

/**
 * 
 * @author Ming Li & Danjuan Ye
 * 
 */
@Entity(value = "company", concern = "safe")
public class Company {
	@Id
	private ObjectId id;

	private int companyIndex;

	private String companyName;

	private String homepageurl;

	private String location;

	private String country;

	private String funding;

	@Indexed(value=IndexDirection.DESC, name="fundingAmount", unique=false, dropDups=false) 
	private double fundingAmount;

	private String industry;

	private String cboverview;

	private List<String> wikiContentTopics;

	private List<String> wikiContentValues;

	private int employees;

	private String foundedDate;

	private byte[] logo;

	private byte[] profileLogo;

	private List<String> videos;

	private Map<String, String> crunchbaseDoc;

	private Map<String, String> wikipediaDoc;

	private String wikiUrl;

	@Embedded
	private Map<String, EdgarCompanyDetail> edgarDoc;

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

	public Map<String, EdgarCompanyDetail> getEdgarDoc() {
		return this.edgarDoc;
	}

	public void setWikipediaDoc(Map<String, String> wikipediaDoc) {
		this.wikipediaDoc = wikipediaDoc;
	}

	public void setCrunchbaseDoc(Map<String, String> crunchbaseDoc) {
		this.crunchbaseDoc = crunchbaseDoc;
	}

	public void setEdgarDoc(Map<String, EdgarCompanyDetail> edgarDoc) {
		this.edgarDoc = edgarDoc;
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

	public String getCboverview() {
		return cboverview;
	}

	public void setCboverview(String cboverview) {
		this.cboverview = cboverview;
	}

	public List<String> getWikiContentTopics() {
		return wikiContentTopics;
	}

	public void setWikiContentTopics(List<String> wikiContentTopics) {
		this.wikiContentTopics = wikiContentTopics;
	}

	public List<String> getWikiContentValues() {
		return wikiContentValues;
	}

	public void setWikiContentValues(List<String> wikiContentValues) {
		this.wikiContentValues = wikiContentValues;
	}

	public int getEmployees() {
		return employees;
	}

	public void setEmployees(int employees) {
		this.employees = employees;
	}

	public String getFoundedDate() {
		return foundedDate;
	}

	public void setFoundedDate(String foundedDate) {
		this.foundedDate = foundedDate;
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

	public List<String> getVideos() {
		return videos;
	}

	public void setVideos(List<String> videos) {
		this.videos = videos;
	}

	public String getWikiUrl() {
		return wikiUrl;
	}

	public void setWikiUrl(String url) {
		this.wikiUrl = url;
	}

	public byte[] getProfileLogo() {
		return profileLogo;
	}

	public void setProfileLogo(byte[] profileLogo) {
		this.profileLogo = profileLogo;
	}

	public int getCompanyIndex() {
		return companyIndex;
	}

	public void setCompanyIndex(int companyIndex) {
		this.companyIndex = companyIndex;
	}

}
