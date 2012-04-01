package com.secondmarket.model;

import java.util.List;

import com.google.code.morphia.annotations.Embedded;

@Embedded
public class FundingRound {

	private String roundCode;
	private double raisedAmount;
	private String raisedCurrencyCode;
	private String fundedDate;
	private List<String> investorList;

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public double getRaisedAmount() {
		return raisedAmount;
	}

	public void setRaisedAmount(double raisedAmount) {
		this.raisedAmount = raisedAmount;
	}

	public String getRaisedCurrencyCode() {
		return raisedCurrencyCode;
	}

	public void setRaisedCurrencyCode(String raisedCurrencyCode) {
		this.raisedCurrencyCode = raisedCurrencyCode;
	}

	public String getFundedDate() {
		return fundedDate;
	}

	public void setFundedDate(String fundedDate) {
		this.fundedDate = fundedDate;
	}

	public List<String> getInvestorList() {
		return investorList;
	}

	public void setInvestorList(List<String> investorList) {
		this.investorList = investorList;
	}

}
