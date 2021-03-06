package com.secondmarket.filter;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.springframework.web.util.HtmlUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.secondmarket.model.FundingRound;
import com.secondmarket.model.Office;
import com.secondmarket.model.Relationship;
import com.secondmarket.utility.ImageProcessor;

/**
 * 
 * @author Ming Li
 * 
 */
public final class CrunchBaseFilter {
	// For converting the integer month number to String month name
	private String[] months = new DateFormatSymbols().getMonths();
	private NumberFormat numberFormat = NumberFormat.getInstance();
	private ImageProcessor imageProcessor = new ImageProcessor();;

	public String getCompanyName(BasicDBObject basicDBObject) {
		if (basicDBObject == null) {
			return "undefined";
		}
		if (basicDBObject.containsField("name")
				&& basicDBObject.get("name") != null) {
			return basicDBObject.get("name").toString().trim();
		} else {
			return "undefined";
		}
	}

	public String getHomepageUrl(BasicDBObject basicDBObject) {
		if (basicDBObject == null) {
			return "undefined";
		}
		if (basicDBObject.containsField("homepage_url")
				&& basicDBObject.get("homepage_url") != null) {
			return basicDBObject.get("homepage_url").toString().trim();
		} else {
			return "undefined";
		}
	}

	public String getFunding(BasicDBObject basicDBObject) {
		if (basicDBObject == null) {
			return "undefined";
		}
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
		if (basicDBObject == null) {
			return Double.NaN;
		}
		if (basicDBObject.containsField("total_money_raised")
				&& basicDBObject.get("total_money_raised") != null) {
			String funding = basicDBObject.get("total_money_raised").toString()
					.trim();

			Pattern pattern = Pattern.compile("-?\\d+");
			Matcher matcher = pattern.matcher(funding);
			if (matcher.find()) {
				if (funding.endsWith("k") || funding.endsWith("K")) {
					fundingAmount = Double.parseDouble(matcher.group());
				} else if (funding.endsWith("M") || funding.endsWith("m")) {
					fundingAmount = Double.parseDouble(matcher.group()) * 1000.0;
				} else if (funding.endsWith("B") || funding.endsWith("b")) {
					fundingAmount = Double.parseDouble(matcher.group()) * 1000000.0;
				} else if (funding.endsWith("T") || funding.endsWith("t")) {
					fundingAmount = Double.parseDouble(matcher.group()) * 1000000000.0;
				} else {
					fundingAmount = Double.MAX_VALUE;
				}
			} else {
				fundingAmount = Double.NaN;
			}

		} else {
			fundingAmount = Double.NaN;
		}

		return fundingAmount;
	}

	public String getLocation(BasicDBObject basicDBObject) {
		if (basicDBObject == null) {
			return "undefined";
		}
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
		if (basicDBObject == null) {
			return "undefined";
		}
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
		if (basicDBObject == null) {
			return "undefined";
		}
		if (basicDBObject.containsField("category_code")
				&& basicDBObject.get("category_code") != null) {
			String category = basicDBObject.get("category_code").toString()
					.trim();
			return WordUtils.capitalizeFully(category);
		} else {
			return "undefined";
		}
	}

	public String getOverview(BasicDBObject basicDBObject) {
		if (basicDBObject == null) {
			return "undefined";
		}
		if (basicDBObject.containsField("overview")
				&& basicDBObject.get("overview") != null) {
			return basicDBObject.get("overview").toString().trim();
		} else {
			return "undefined";
		}
	}

	public List<FundingRound> getFundings(BasicDBObject basicDBObject) {
		List<FundingRound> fundings = new ArrayList<FundingRound>();

		if (basicDBObject == null) {
			return fundings;
		}
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

					// System.out.println("Raised amount string --> "
					// + raisedAmountString);
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
					try {
						Currency currency = Currency
								.getInstance(raisedCurrencyCode);
						// Currency "NIS" cannot be recognized, should be "ILS"
						fundingRound
								.setRaisedCurrencyCode(currency.getSymbol());
					} catch (IllegalArgumentException e) {
						fundingRound.setRaisedCurrencyCode("unrecognized");
					}
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

	public List<Office> getOffices(BasicDBObject basicDBObject) {
		List<Office> offices = new ArrayList<Office>();

		if (basicDBObject == null)
			return offices;
		if (basicDBObject.containsField("offices")
				&& basicDBObject.get("offices") != null) {
			BasicDBList officeList = (BasicDBList) JSON.parse(basicDBObject
					.get("offices").toString().trim());
			Iterator<Object> officeListIterator = officeList.iterator();
			while (officeListIterator.hasNext()) {
				Office office = new Office();
				BasicDBObject officeObj = (BasicDBObject) officeListIterator
						.next();
				// Set address1
				if (officeObj.containsField("address1")
						&& officeObj.get("address1") != null) {
					office.setAddress1(officeObj.get("address1").toString()
							.trim());
					// System.out.println("Address1 --> "
					// + officeObj.get("address1").toString().trim());
				} else {
					office.setAddress1("undefined");
				}
				// Set address2
				if (officeObj.containsField("address2")
						&& officeObj.get("address2") != null) {
					office.setAddress2(officeObj.get("address2").toString()
							.trim());
					// System.out.println("Address2 --> "
					// + officeObj.get("address2").toString().trim());
				} else {
					office.setAddress2("undefined");
				}
				// Set zip_code
				if (officeObj.containsField("zip_code")
						&& officeObj.get("zip_code") != null) {
					office.setZipcode(officeObj.get("zip_code").toString()
							.trim());
					// System.out.println("Zipcode --> "
					// + officeObj.get("zip_code").toString().trim());
				} else {
					office.setZipcode("undefined");
				}
				// Set city
				if (officeObj.containsField("city")
						&& officeObj.get("city") != null) {
					office.setCity(officeObj.get("city").toString().trim());
					// System.out.println("City --> "
					// + officeObj.get("city").toString().trim());
				} else {
					office.setCity("undefined");
				}
				// Set state_code
				if (officeObj.containsField("state_code")
						&& officeObj.get("state_code") != null) {
					office.setStatecode(officeObj.get("state_code").toString()
							.trim());
					// System.out.println("state_code --> "
					// + officeObj.get("state_code").toString().trim());
				} else {
					office.setStatecode("undefined");
				}
				// Set country_code
				if (officeObj.containsField("country_code")
						&& officeObj.get("country_code") != null) {
					office.setCountrycode(officeObj.get("country_code")
							.toString().trim());
					// System.out.println("country_code --> "
					// + officeObj.get("country_code").toString().trim());
				} else {
					office.setCountrycode("undefined");
				}
				// Set latitude
				if (officeObj.containsField("latitude")
						&& officeObj.get("latitude") != null) {
					office.setLatitude(Double.parseDouble(officeObj
							.get("latitude").toString().trim()));
				} else {
					office.setLatitude(0.0);
				}

				// Set longitude
				if (officeObj.containsField("longitude")
						&& officeObj.get("longitude") != null) {
					office.setLongitude(Double.parseDouble(officeObj
							.get("longitude").toString().trim()));
				} else {
					office.setLongitude(0.0);
				}

				offices.add(office);
			}

		}

		return offices;
	}

	public List<Relationship> getRelationships(BasicDBObject basicDBObject) {
		List<Relationship> relationships = new ArrayList<Relationship>();

		if (basicDBObject == null)
			return relationships;
		if (basicDBObject.containsField("relationships")
				&& basicDBObject.get("relationships") != null) {
			BasicDBList relationshipList = (BasicDBList) JSON
					.parse(basicDBObject.get("relationships").toString().trim());
			Iterator<Object> relationshipIterator = relationshipList.iterator();
			while (relationshipIterator.hasNext()) {
				Relationship relationship = new Relationship();
				BasicDBObject relationshipObj = (BasicDBObject) relationshipIterator
						.next();

				if (relationshipObj.containsField("is_past")
						&& relationshipObj.get("is_past") != null
						&& relationshipObj.getBoolean("is_past") == true) {
					continue;
				} else {
					// Set title
					if (relationshipObj.containsField("title")
							&& relationshipObj.get("title") != null) {
						relationship.setTitle(relationshipObj.get("title")
								.toString().trim());
						// System.out.println(relationshipObj.getString("title"));
					} else {
						relationship.setTitle("");
					}

					// Set name
					if (relationshipObj.containsField("person")
							&& relationshipObj.get("person") != null) {
						BasicDBObject personObj = (BasicDBObject) relationshipObj
								.get("person");
						String name = "";
						String firstName = "";
						String lastName = "";
						if (personObj.containsField("first_name")
								&& personObj.get("first_name") != null) {
							firstName = personObj.get("first_name").toString()
									.trim();
						}
						if (personObj.containsField("last_name")
								&& personObj.get("last_name") != null) {
							lastName = personObj.get("last_name").toString()
									.trim();
						}
						name = firstName + " " + lastName;
						relationship.setName(name);
						// System.out.println(name);

					} else {
						relationship.setName("");
					}
				}
				relationships.add(relationship);
			}
		} else {
			Relationship relationship = new Relationship();
			relationship.setTitle("undefined");
			relationship.setName("undefined");
			relationships.add(relationship);
		}

		return relationships;
	}

	public List<byte[]> getResizedLogos(BasicDBObject basicDBObject) {
		String imageURL = this.getCompanyImageUrl(basicDBObject);
		if (imageURL.length() == 0) {
			imageURL = "https://dbr2dggbe4ycd.cloudfront.net/company/default_150.png";
		}

		List<byte[]> logos = imageProcessor.processImage(imageURL);
		return logos;
	}

	public String getCompanyImageUrl(BasicDBObject basicDBObject) {
		String companyURL = "";
		if (basicDBObject == null)
			return companyURL;
		if (basicDBObject.containsField("image")
				&& basicDBObject.get("image") != null) {
			BasicDBObject availablesizeObj = (BasicDBObject) JSON
					.parse(basicDBObject.get("image").toString().trim());
			if (availablesizeObj.containsField("available_sizes")
					&& availablesizeObj.get("available_sizes") != null) {
				BasicDBList imageList = (BasicDBList) JSON
						.parse(availablesizeObj.get("available_sizes")
								.toString().trim());
				BasicDBList listObj = (BasicDBList) imageList.get(0);
				if (listObj.get(1) != null
						&& (listObj.get(1)) instanceof String) {
					companyURL = "http://www.crunchbase.com/" + listObj.get(1);
				}
			}
		}

		return companyURL;
	}

	public int getNumberOfEmployees(BasicDBObject basicDBObject) {
		if (basicDBObject == null)
			return 0;
		if (basicDBObject.containsField("number_of_employees")
				&& basicDBObject.get("number_of_employees") != null) {
			return basicDBObject.getInt("number_of_employees");
		} else {
			return 0;
		}
	}

	public String getFoundedDate(BasicDBObject basicDBObject) {
		String foundedYear = "";
		String foundedMonth = "";
		if (basicDBObject == null)
			return "";
		if (basicDBObject.containsField("founded_year")
				&& basicDBObject.get("founded_year") != null) {
			foundedYear = basicDBObject.get("founded_year").toString().trim();
		}
		if (basicDBObject.containsField("founded_month")
				&& basicDBObject.get("founded_month") != null) {
			int monthNumber = basicDBObject.getInt("founded_month");
			if (monthNumber <= 12 && monthNumber >= 1) {
				foundedMonth = months[monthNumber - 1];
			}
		}
		return foundedMonth + " " + foundedYear;
	}

	public List<String> getEmbedVideoSrcs(BasicDBObject basicDBObject) {
		List<String> embedsVideoUrlList = new ArrayList<String>();
		if (basicDBObject == null) {
			return embedsVideoUrlList;
		}
		if (basicDBObject.containsField("video_embeds")
				&& basicDBObject.get("video_embeds") != null) {
			BasicDBList videoCodeList = (BasicDBList) JSON.parse(basicDBObject
					.getString("video_embeds"));
			Iterator<Object> it = videoCodeList.iterator();
			while (it.hasNext()) {
				String embedVideoUrl = "";
				BasicDBObject embedCodeObj = (BasicDBObject) it.next();
				if (embedCodeObj.containsField("embed_code")
						&& embedCodeObj.get("embed_code") != null) {
					String embedCode = embedCodeObj.get("embed_code")
							.toString().trim();
					int beginIndex = embedCode.indexOf("<embed");
					int endIndex = embedCode.indexOf("</embed>");
					// System.out
					// .println("From " + beginIndex + " to " + endIndex);
					if (beginIndex != -1 && endIndex != -1) {
						String embedVideoSrc = embedCode.substring(beginIndex,
								endIndex + 8);
						// System.out.println(embedCode.substring(beginIndex,
						// endIndex + 8));
						if (embedVideoSrc.contains("src")) {
							String subEmbedVideoSrc = embedVideoSrc
									.substring(embedVideoSrc.indexOf("src"));
							Pattern pattern = Pattern
									.compile("\"(\\s*?.*?)*?\"");
							Matcher srcUrlMatcher = pattern
									.matcher(subEmbedVideoSrc);
							// Look for the src url
							if (srcUrlMatcher.find()) {
								embedVideoUrl = srcUrlMatcher.group(0).replace(
										"\"", "");
							}

							// if the video src ends with "swf" (flash video),
							// need "FlashVars" for the src url parameters
							if (embedVideoUrl.endsWith(".swf")) {
								String lowerCaseEmbedVideoSrc = embedVideoSrc
										.toLowerCase(Locale.US);
								int startIndex = lowerCaseEmbedVideoSrc
										.indexOf("flashvars");
								if (startIndex != -1) {
									String flashVarsStr = embedVideoSrc
											.substring(startIndex);
									Matcher varMatcher = pattern
											.matcher(flashVarsStr);
									if (varMatcher.find()) {
										embedVideoUrl = embedVideoUrl
												+ "?"
												+ varMatcher.group(0).replace(
														"\"", "");
										// System.out.println(embedVideoUrl);
									}
								} else {
									continue;
								}

							}

						} else {
							continue;
						}

					} else {
						continue;
					}

				}
				embedsVideoUrlList.add(embedVideoUrl);
			}

		}

		if (embedsVideoUrlList.size() == 0) {
			embedsVideoUrlList.add("");
		}

		return embedsVideoUrlList;
	}
}