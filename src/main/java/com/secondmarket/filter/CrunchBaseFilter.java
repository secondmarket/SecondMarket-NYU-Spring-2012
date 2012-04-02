package com.secondmarket.filter;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.util.HtmlUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.secondmarket.model.FundingRound;

/**
 * 
 * @author Ming Li
 * 
 */
public final class CrunchBaseFilter {
	// For converting the integer month number to String month name
	private String[] months = new DateFormatSymbols().getMonths();
	private NumberFormat numberFormat = NumberFormat.getInstance();

	public String getCompanyName(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("name")
				&& basicDBObject.get("name") != null) {
			return basicDBObject.get("name").toString().trim();
		} else {
			return "undefined";
		}
	}

	public String getFunding(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("total_money_raised")
				&& basicDBObject.get("total_money_raised") != null) {
			return (HtmlUtils.htmlEscapeHex(basicDBObject
					.get("total_money_raised").toString().trim()));
		} else {
			return "undefined";
		}

	}

	public double getFundingAmount(BasicDBObject basicDBObject) {
		double fundingAmount;
		if (basicDBObject.containsField("total_money_raised")
				&& basicDBObject.get("total_money_raised") != null) {
			String funding = basicDBObject.get("total_money_raised").toString()
					.trim();

			if (funding.endsWith("k") || funding.endsWith("K")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1));
			} else if (funding.endsWith("M") || funding.endsWith("m")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1)) * 1000.0;
			} else if (funding.endsWith("B") || funding.endsWith("b")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1)) * 1000000.0;
			} else if (funding.endsWith("T") || funding.endsWith("t")) {
				fundingAmount = Double.parseDouble(funding.substring(1,
						funding.length() - 1)) * 1000000000.0;
			} else {
				fundingAmount = Double.MAX_VALUE;
			}

		} else {
			fundingAmount = Double.NaN;
		}

		return fundingAmount;
	}

	public String getLocation(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("offices")
				&& basicDBObject.get("offices") != null) {
			BasicDBList value = (BasicDBList) JSON.parse(basicDBObject
					.get("offices").toString().trim());
			if (value.size() > 0) {
				BasicDBObject valueObj = (BasicDBObject) value.get(0);

				if (valueObj.containsField("state_code")
						&& valueObj.get("state_code") != null) {
					return valueObj.get("state_code").toString().trim();
				} else {
					return "undefined";
				}
			} else {
				return "undefined";
			}
		} else {
			return "undefined";
		}
	}

	public String getCounrty(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("offices")
				&& basicDBObject.get("offices") != null) {
			BasicDBList value = (BasicDBList) JSON.parse(basicDBObject
					.get("offices").toString().trim());
			if (value.size() > 0) {
				BasicDBObject valueObj = (BasicDBObject) value.get(0);

				if (valueObj.containsField("country_code")
						&& valueObj.get("country_code") != null) {
					return valueObj.get("country_code").toString().trim();
				} else {
					return "undefined";
				}
			} else {
				return "undefined";
			}
		} else {
			return "undefined";
		}
	}

	public String getIndustry(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("category_code")
				&& basicDBObject.get("category_code") != null) {
			return basicDBObject.get("category_code").toString().trim();
		} else {
			return "undefined";
		}
	}

	public String getOverview(BasicDBObject basicDBObject) {
		if (basicDBObject.containsField("overview")
				&& basicDBObject.get("overview") != null) {
			return basicDBObject.get("overview").toString().trim();
		} else {
			return "undefined";
		}
	}

	public List<FundingRound> getFundings(BasicDBObject basicDBObject) {
		List<FundingRound> fundings = new ArrayList<FundingRound>();

		if (basicDBObject.containsField("funding_rounds")
				&& basicDBObject.get("funding_rounds") != null) {
			BasicDBList fundingRoundsList = (BasicDBList) JSON
					.parse(basicDBObject.get("funding_rounds").toString()
							.trim());
			Iterator<Object> fundingRoundIterator = fundingRoundsList
					.iterator();
			while (fundingRoundIterator.hasNext()) {
				FundingRound fundingRound = new FundingRound();
				BasicDBObject round = (BasicDBObject) fundingRoundIterator
						.next();
				// System.out.println(round);
				// Set round_code
				if (round.containsField("round_code")
						&& round.get("round_code") != null) {
					String roundCode = round.get("round_code").toString()
							.trim();
					fundingRound.setRoundCode(roundCode);
					// System.out.println(roundCode);
				} else {
					fundingRound.setRoundCode("undefined");
				}
				// Set raised_amount
				if (round.containsField("raised_amount")
						&& round.get("raised_amount") != null) {
					String raisedAmountValue = round.get("raised_amount")
							.toString().trim();
					double raisedAmount = Double.parseDouble(raisedAmountValue);
					String raisedAmountString = "";

					numberFormat.setMaximumFractionDigits(0); // No decimal
																// points
					String tempRaisedAmountString = numberFormat.format(
							raisedAmount).replace(",", "");

					if (tempRaisedAmountString.length() <= 6) {
						raisedAmountString = String.format("%.1fK",
								raisedAmount / 1000.0);
					} else if (tempRaisedAmountString.length() > 6
							&& tempRaisedAmountString.length() <= 9) {
						raisedAmountString = String.format("%.1fM",
								raisedAmount / 1000000.0);
					} else {
						raisedAmountString = String.format("%.1fB",
								raisedAmount / 1000000000.0);
					}

					System.out.println("Raised amount string --> "
							+ raisedAmountString);
					fundingRound.setRaisedAmount(raisedAmount);
					fundingRound.setRaisedAmountString(raisedAmountString);
					// System.out.println(raisedAmount);
				} else {
					fundingRound.setRaisedAmount(0.00);
				}
				// Set raised_currency_code
				if (round.containsField("raised_currency_code")
						&& round.get("raised_currency_code") != null) {
					String raisedCurrencyCode = round
							.get("raised_currency_code").toString().trim();
					Currency currency = Currency
							.getInstance(raisedCurrencyCode);
					fundingRound.setRaisedCurrencyCode(currency.getSymbol());
					// System.out.println(currency.getSymbol());
				} else {
					fundingRound.setRaisedCurrencyCode("undefined");
				}
				// Set funded_date
				String fundedYear = "";
				String fundedMonth = "";
				int monthNumber;

				if (round.containsField("funded_year")
						&& round.get("funded_year") != null) {
					fundedYear = round.get("funded_year").toString().trim();
				} else {
					fundedYear = "undefined";
				}
				if (round.containsField("funded_month")
						&& round.get("funded_month") != null) {
					monthNumber = Integer.parseInt(round.get("funded_month")
							.toString().trim());
					if (monthNumber <= 12 && monthNumber >= 1) {
						fundedMonth = months[monthNumber - 1];
					} else {
						fundedMonth = "undefined";
					}
				} else {
					monthNumber = 0;
				}
				fundingRound.setFundedDate(fundedMonth + " " + fundedYear);
				// System.out.println(fundedMonth + " " + fundedYear);

				// Set investments
				List<String> investorList = new ArrayList<String>();
				if (round.containsField("investments")
						&& round.get("investments") != null) {
					BasicDBList investmentsList = (BasicDBList) JSON
							.parse(round.get("investments").toString().trim());
					Iterator<Object> investmentsIterator = investmentsList
							.iterator();
					while (investmentsIterator.hasNext()) {
						BasicDBObject investment = (BasicDBObject) investmentsIterator
								.next();

						if (investment.containsField("company")
								&& investment.get("company") != null) {
							BasicDBObject companyInvestment = (BasicDBObject) investment
									.get("company");
							if (companyInvestment.containsField("name")
									&& companyInvestment.get("name") != null) {
								investorList.add(companyInvestment.get("name")
										.toString().trim());
								// System.out.println(companyInvestment
								// .get("name").toString().trim());
							}
						} else if (investment.containsField("financial_org")
								&& investment.get("financial_org") != null) {
							BasicDBObject financialOrgInvestment = (BasicDBObject) investment
									.get("financial_org");
							if (financialOrgInvestment.containsField("name")
									&& financialOrgInvestment.get("name") != null) {
								investorList.add(financialOrgInvestment
										.get("name").toString().trim());
								// System.out.println(financialOrgInvestment
								// .get("name").toString().trim());
							}
						} else if (investment.containsField("person")
								&& investment.get("person") != null) {
							BasicDBObject personInvestment = (BasicDBObject) investment
									.get("person");
							String firstName = "";
							String lastName = "";
							if (personInvestment.containsField("first_name")
									&& personInvestment.get("first_name") != null) {
								firstName = personInvestment.get("first_name")
										.toString().trim();
							}
							if (personInvestment.containsField("last_name")
									&& personInvestment.get("last_name") != null) {
								lastName = personInvestment.get("last_name")
										.toString().trim();
							}
							investorList.add(firstName + " " + lastName);
							// System.out.println(firstName + " " + lastName);
						} else {
							investorList.add("undefined");
						}
					}
					fundingRound.setInvestorList(investorList);
				} else {
					investorList.add("undefined");
					fundingRound.setInvestorList(investorList);
				}

				fundings.add(fundingRound);
			}

		}

		return fundings;
	}

}
