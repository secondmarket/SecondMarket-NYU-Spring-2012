<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.secondmarket.model.*"%>
<%
	Company company = (Company) request.getAttribute("company");
%>

<!DOCTYPE html>
<html lang="en" xmlns:fb="http://www.facebook.com/2008/fbml"
	xmlns:og="http://opengraphprotocol.org/schema/">
<head>

<script type="text/javascript" src="/js/simpletreemenu.js">
</script>
<link rel="stylesheet" type="text/css" href="/css/simpletree.css" />



<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<meta name="apple-mobile-web-app-capable" content="yes" />

<link rel="image_src"
	href="https://dbr2dggbe4ycd.cloudfront.net/company/facebook_150.png" />


<title>
	<%
		out.print(company.getCompanyName());
	%> - SecondMarket
</title>

<script type="text/javascript">
    var contextPath = "/";
</script>

<link rel="stylesheet" type="text/css" media="all" href="/css/all.css" />
<link rel="stylesheet" type="text/css" media="all"
	href="/css/plugin.css" />
<link rel="stylesheet" type="text/css" media="print"
	href="/css/print.css" />
<!--[if IE]><link rel="stylesheet" href="/static/css/blueprint/ie.css" type="text/css" media="screen, projection" /><![endif]-->
<!--[if IE 6]><link rel="stylesheet" href="/static/css/ie-6.css" type="text/css" media="screen, projection" /><![endif]-->
<!--[if IE 7]><link rel="stylesheet" href="/static/css/ie-7.css" type="text/css" media="screen, projection" /><![endif]-->
<!--[if IE 8]><link rel="stylesheet" href="/static/css/ie-8.css" type="text/css" media="screen, projection" /><![endif]-->
<script type="text/javascript">
var http = ('https:' == document.location.protocol) ? 'https' : 'http';
</script>
<script type="text/javascript">
document.write(unescape('%3Cscript src="' + http + '://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js" type="text/javascript"%3E%3C/script%3E'));
</script>
<script type="text/javascript">
if (typeof jQuery == 'undefined') {
document.write(unescape('%3Cscript src="/static/javascript/jquery-1.5.min.js" type="text/javascript"%3E%3C/script%3E'));
}
</script>
<script type="text/javascript">
document.write(unescape('%3Cscript src="' + http + '://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js" type="text/javascript"%3E%3C/script%3E'));
</script>
<script type="text/javascript">
if (typeof jQuery.ui == 'undefined') {
document.write(unescape('%3Cscript src="/static/javascript/jquery-ui-1.8.7.min.js" type="text/javascript"%3E%3C/script%3E'));
}
</script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/all.js"></script>
<script type="text/javascript" src="/js/organization.js"></script>
<script type="text/javascript" src="/js/often.js"></script>
<script type="text/javascript" src="/js/jquery.jstree.js"></script>
<script type="text/javascript" src="/js/plusone.js"></script>

<script type="text/javascript" src="/js/CeeBoxjs/ceeboxjquery.js"></script>
<script type="text/javascript" src="/js/CeeBoxjs/jquery.ceebox-min.js"></script>
<script type="text/javascript" src="/js/CeeBoxjs/jquery.swfobject.js"></script>
<script type="text/javascript">
 $(document).ready(function(){
	$(".videoclass").ceebox({
		borderColor:'#666',
		boxColor: "#000",
		titles: false,
		fadeIn: 100,
		fadeOut: 100
	});
	
	$("map").ceebox({
		borderColor:'#666',
		boxColor: "#000",
		titles: false,
		fadeIn: 100,
		fadeOut: 100
	});
	
});
  
</script>

<link rel="stylesheet" type="text/css" href="/css/ceebox-min.css" />
</head>
<body>
	<div id="wrap">
		<div id="header">
			<div class="container">
				<div id="sm-header-logo" class="span-6 append-1 sm-mts">
					<a href="https://www.secondmarket.com/?t=lg"><img
						src="/images/sm-logo-small.png" alt="SecondMarket" /></a>
				</div>
				<div class="span-5">
					<div class="sm-l sm-member-of">
						Member &nbsp;<a href="http://www.finra.org/index.htm?t=hli"
							target="_blank">FINRA</a> | <a
							href="http://www.msrb.org/msrb1/?t=hli" target="_blank">MSRB</a>
						| <a href="http://www.sipc.org/?t=hli" target="_blank">SIPC</a>
					</div>
				</div>
				<nav>
					<div class="span-24">
						<div class="sm-nav-bar">
							<div class="span-16">
								<ul id="sm-navigation"></ul>
							</div>
						</div>
					</div>
					<hr class="space" />
				</nav>
			</div>
		</div>


		<div id="main">
			<div class="container">

				<h5 class="sm-mb">
					<a href="/SecondMarket/CompanyMain.htm">Main Search</a>&nbsp;|&nbsp;<%
						out.print(company.getCompanyName());
					%>
				</h5>
				<div class="span-24 last clearfix" id="sm-main-content">
					<hr />

					<div class="span-24 last">
						<div class="clearfix">

							<div class="span-16 append-1">

								<div id="companyLeftSide" class="span-4">
									<div class="sm-icon-border">
										<%
											out.print("<img src=\"/SecondMarket/getLogo.htm?companyName="
													+ company.getCompanyName() + "\" border=\"0\" alt=\""
													+ company.getCompanyName() + "\">");
										%>
									</div>
									<div></div>
									<div class="clear"></div>
								</div>

								<div class="span-12 last">
									<div class="sm-gray-box sm-overview-info">
										<div class="sm-r" id="profileUrls">
											<h5>
												Total funding:&nbsp;
												<%
													out.print(company.getFunding());
												%>
											</h5>
										</div>

										<h2>
											<%
												out.print(company.getCompanyName());
											%>
										</h2>
										<div class="ellipsis">
											<a href="<%out.print(company.getHomepageurl());%>"
												target="_blank" class="ellipsis"> <%
 	out.print(company.getHomepageurl());
 %>
											</a>
										</div>
										<div class="sm-l">
											<%
												out.print(company.getIndustry());
											%>
											Industry
										</div>
										<div class="clear"></div>

										<div class="sm-l sm-mt sm-db sm-mr">
											<%
												if (company.getFoundedDate() != null
														&& company.getFoundedDate().trim().length() != 0) {
													out.print("Since " + company.getFoundedDate());
												}
											%>
										</div>


										<div class="sm-r sm-mt sm-db sm-mr" id="profileUrls">
											<%
												if (company.getWikiUrl() != null
														&& company.getWikiUrl().trim().length() != 0) {
													out.print("<a href=\"#\"><img src=\"/images/wiki.png\" usemap=\"#wikiurlmap\" /></a>");
												}
											%>
											<!-- <a href="#"><img src="/images/wiki.png"
												usemap="#planetmap" /></a> this line works with <map name="wikiurlmap" class="iframe"></map> -->
											<a target="_blank" id="profileFacebookIcon"
												href="http://www.facebook.com"><img
												src="/images/pro-facebook-icon.png"></a> <a
												target="_blank" id="profileTwitterIcon"
												href="http://www.twitter.com"><img
												src="/images/pro-twitter-icon.png"></a> <a target="_blank"
												id="profileLinkedInIcon" href="http://www.linkedin.com"><img
												src="/images/pro-linkedin-icon.png"></a>
										</div>
										<div class="videoclass">
											<%
												List<String> videoSrcUrl = company.getVideos();
												if (videoSrcUrl != null) {
													int number = 1;
													for (String url : videoSrcUrl) {
														if (url != null && url.length() != 0) {
															if (number == 1) {
																out.println("<a href=\""
																		+ url
																		+ "\" rel=\"width:420 height:380\" style=\"text-decoration: none\">"
																		+ "<img src=\"/images/video.png\" /></a>");
															} else {
																out.println("<a href=\""
																		+ url
																		+ "\" rel=\"width:420 height:380\" style=\"text-decoration: none\"></a>");
															}
															number++;
														}
													}
												}
											%>
										</div>
										<!-- <div class="sm-r sm-mt sm-db sm-mr">
											<a href="#"><img
												src="/images/wikipedia_icon.png" width="30" height="30" /></a>
										</div> -->

										<!-- <div class="videoclass">
											<a href="http://blip.tv/play/goRrgqjkfQI"
												rel="width:500 height:400" style="text-decoration: none"><img
												src="/images/video_icon.png" width="36" height="36" /></a>
											&nbsp; <a href="http://blip.tv/play/hRaTlyAA"
												rel="width:500 height:400" style="text-decoration: none"></a>
											<a
												href="http://www.vator.tv/embed/vpembed.swf?v=3970_twitter-Int-2-08-13.flv&b=2&i=3970&o=embed&vp=1&l=http://vator.tv/news/show/2008-08-17-is-there-anybody-out-there"
												rel="width:480 height:300" style="text-decoration: none"></a>
										</div> -->
										<div class="clear"></div>
									</div>
								</div>

								<%
									if (company.getWikiUrl() != null
											&& company.getWikiUrl().trim().length() != 0) {
										out.print("<map name=\"wikiurlmap\" class=\"iframe\">");
										out.print("<area shape=\"rect\" coords=\"0,0,82,126\" alt=\"Wikipedia Article\" href=\""
												+ company.getWikiUrl() + "\" />");
										out.print("</map>");
									}
								%>

								<%-- <map name="wikiurlmap" class="iframe">
									<area shape="rect" coords="0,0,82,126" alt="Wikipedia Article"
										href="<%out.print(company.getWikiUrl());%>" />
									<area shape="rect" coords="0,0,82,126" alt="Mercury"
										href="http://en.wikipedia.org/wiki/Mercury_(planet)" />
									<area shape="rect" coords="0,0,82,126" alt="Venus"
										href="http://en.wikipedia.org/wiki/Venus" />
								</map> --%>

								<hr class="space" />

								<div class="sm-r bbq-loading" style="display: none">&nbsp;</div>

								<div class="span-16 sm-more-block sm-data">
									<h4>Company Overview</h4>
									<div class="expandable">
										<%
											out.print(company.getCboverview());
										%>
									</div>

									<!-- <p>Facebook is the world's
										largest social network, with over 840 million users.</p>
									<p>Facebook was founded by
										Mark Zuckerberg in February 2004, initially as an exclusive
										network for Harvard students. It was a huge hit: in 2 weeks,
										half of the schools in the Boston area began demanding a
										Facebook network. Zuckerberg immediately recruited his friends
										Dustin Moskowitz and Chris Hughes to help build Facebook, and
										within four months, Facebook added 30 more college networks.</p>
									<p class="sm-ellipsis sm-more-item">Facebook's competitors
										include MySpace, Bebo, Friendster, LinkedIn, Tagged, Hi5,
										Piczo, and Open Social.</p> -->
								</div>


								<div class="sm-r bbq-loading" style="display: none">&nbsp;</div>


								<%
									Map<String, String> wikiContentMap = company.getWikiContentMap();
									if (wikiContentMap != null) {
										Set<String> keySet = wikiContentMap.keySet();
										for (String key : keySet) {
											String topic = key;
											String content = wikiContentMap.get(key);
											out.print("<div class=\"span-16 sm-more-block sm-data\">");
											out.print("<h4>" + topic + "</h4>");
											out.print("<div class=\"expandablediv\">");
											out.print("<p>" + content + "</p>");
											out.print("</div></div>");
										}

									} else {
										out.print("<div class=\"span-16 sm-more-block sm-data\">");
										out.print("<h4>No wikipedia content</h4>");
										out.print("<div class=\"expandablediv\">");
										out.print("<p></p>");
										out.print("</div></div>");
									}
								%>
								<!-- <div class="span-16 sm-more-block sm-data">
									<h4>Wikipeida</h4>
									<div class="expandablediv">
										<p>Facebook was founded by Mark Zuckerberg in February
											2004, initially as an exclusive network for Harvard students.
											It was a huge hit: in 2 weeks, half of the schools in the
											Boston area began demanding a Facebook network. Zuckerberg
											immediately recruited his friends Dustin Moskowitz and Chris
											Hughes to help build Facebook, and within four months,
											Facebook added 30 more college networks.</p>
										<p>The original idea for the term Facebook came from
											Zuckerberg's high school (Phillips Exeter Academy). The
											Exeter Face Book was passed around to every student as a way
											for students to get to know their classmates for the
											following year. It was a physical paper book until Zuckerberg
											brought it to the internet.</p>
									</div>
								</div> -->


								<div class="span-8 append-1 sm-table-style sm-data">
									<hr class="space" />
									<h4 class="clear">Offices</h4>

									<%
										List<Office> offices = company.getOffices();
										if (offices != null) {
											for (Office office : offices) {
												out.print("<div class=\"sm-ellipsis\">"
														+ office.getAddress1() + "</div>");
												out.print("<div class=\"sm-ellipsis\">"
														+ office.getAddress2() + "</div>");
												if (!"undefined".equals(office.getStatecode())
														&& !"undefined".equals(office.getZipcode())) {
													out.print("<div class=\"span-7 sm-ellipsis\">"
															+ office.getCity() + ", "
															+ office.getStatecode() + " "
															+ office.getZipcode() + "</div>");
												} else {
													out.print("<div class=\"span-7 sm-ellipsis\">"
															+ office.getCity() + "</div>");
												}
												out.print("<div class=\"span-7 sm-ellipsis\">"
														+ office.getCountrycode() + "</div>");
												out.print("<hr class=\"space\" />");
											}
										} else {
											out.print("<div class=\"sm-ellipsis\">no office information</div>");
											out.print("<div class=\"sm-ellipsis\">N/A</div>");
											out.print("<div class=\"span-7 sm-ellipsis\"></div>");
											out.print("<div class=\"span-7 sm-ellipsis\"></div>");
										}
									%>

									<!-- <div class="sm-ellipsis">Hanover Reach</div>
									<div class="sm-ellipsis">Grand Canal Harbour</div>
									<div class="span-7 sm-ellipsis">Dublin</div>
									<div class="span-7 sm-ellipsis">Ireland</div>
									<hr class="space" /> -->

									<hr class="space" />

									<ul class="span-7 sm-ellipsis sm-more-block sm-oh">
										<li
											style="margin: 0; padding: 0; margin-left: -40px; list-style-type: none;">
											<a style="display: none;"
											class="sm-link sm-more-toggle sm-more-open">View More</a> <a
											style="display: none;"
											class="sm-link sm-more-toggle sm-more-close">View Less</a>
										</li>
									</ul>


									<hr class="space" />
									<div>
										Source: <a href="http://www.crunchbase.com" target="_blank">Crunchbase</a>,
										<a href="http://www.wikipedia.org/" target="_blank">Wikipedia</a>,
										<a
											href="http://www.sec.gov/edgar/searchedgar/companysearch.html"
											target="_blank">EDGAR</a>
									</div>
									<div></div>
								</div>


								<div class="span-7 last sm-table-style sm-data">
									<hr class="space" />

									<h4>Current Affiliated People</h4>
									<ul class="sm-more-block sm-clear-ul">
										<%
											List<Relationship> relationships = company.getRelationships();
											if (relationships != null) {
												for (Relationship person : relationships) {
													if (!"".equals(person.getName())
															&& !"".equals(person.getTitle())) {
														out.print("<li class=\"sm-more-item\">");
														out.print("<div class=\"span-7 sm-ellipsis\">"
																+ person.getName() + "</div>");
														out.print("<div class=\"sm-mb small sm-ellipses quiet\">"
																+ person.getTitle() + "</div>");
														out.print("</li>");
													} else {
														continue;
													}
												}
											} else {
												out.print("<li class=\"sm-more-item\"><div class=\"span-7 sm-ellipsis\">"
														+ "no affiliated people information"
														+ "</div><div class=\"sm-mb small sm-ellipses quiet\">N/A</div></li>");
											}
										%>

										<!-- <li class="sm-more-item">
											<div class="span-7 sm-ellipsis">Peter Thiel</div>
											<div class="sm-mb small sm-ellipses quiet">Board Of
												Directors</div>
										</li> -->

										<li><a style="display: none;"
											class="sm-link sm-more-toggle sm-more-open">View More</a> <a
											style="display: none;"
											class="sm-link sm-more-toggle sm-more-close">View Less</a></li>
									</ul>
									<hr class="space" />

								</div>

								<hr class="space" />
								<hr class="space" />
							</div>

							<div class="span-7 last sm-table-style sm-data">

								<h3>Funding</h3>
								<div class="sm-shadow-box">
									<table border="0" cellspacing="0" cellpadding="0"
										class="span- financings sm-more-block">
										<%
											List<FundingRound> fundings = company.getFundings();
											if (fundings != null) {
												Collections.reverse(fundings);
												for (FundingRound funding : fundings) {
													out.print("<tr class=\"sm-more-item\">");
													out.print("<td width=\"190\"><strong>"
															+ funding.getRoundCode()
															+ "</strong>, <span class=\"small\">"
															+ funding.getFundedDate() + "</span>");
													List<String> investors = funding.getInvestorList();
													if (investors != null) {
														for (String name : investors) {
															out.print("<div class=\"small sm-ellipsis quiet sm-mbs\">"
																	+ name + "</div>");
														}
													}
													out.print("<td width=\"48\" class=\"sm-r sm-b\" align=\"right\">"
															+ funding.getRaisedCurrencyCode()
															+ funding.getRaisedAmountString() + "</td>");
													out.print("</tr>");
												}
											} else {
												out.print("<tr class=\"sm-more-item\">");
												out.print("<td width=\"195\"><strong>no funding rounds</strong></td>");
												out.print("<td width=\"48\" class=\"sm-r sm-b\" align=\"right\">N/A</td></tr>");
											}
										%>

										<!-- <tr class="sm-more-item">
											<td width="195"><strong>Unattributed</strong>, <span
												class="small">Jun 2010</span>
												<div class="small sm-ellipsis quiet ">Accel Partners</div>
												<div class="small sm-ellipsis quiet ">Mark Pincus</div>
												<div class="small sm-ellipsis quiet sm-mbs">Reid
													Hoffman</div></td>
											<td width="48" class="sm-r sm-b" align="right">$120M</td>
										</tr> -->

										<tr>
											<td colspan="2"><a style="display: none;"
												class="sm-link sm-more-toggle sm-more-open">View More</a> <a
												style="display: none;"
												class="sm-link sm-more-toggle sm-more-close">View Less</a></td>
										</tr>

									</table>

									<hr class="space" />
								</div>

								<hr class="space" />


								<h3>Edgar Filings</h3>
								<div class="sm-shadow-box">
									<div class="sm-underline">
										<a href="javascript:ddtreemenu.flatten('treemenu1', 'expand')">Expand
											All</a> | <a
											href="javascript:ddtreemenu.flatten('treemenu1', 'contact')">Collapse
											All</a>
									</div>

									<%%>
									<ul id="treemenu1" class="treeview">
										<li></li>
										<!-- <li><a href="#">Item1</a></li>
										<li><a href="#">Item2</a></li> -->
										<li>Folder 1
											<ul>
												<li><a href="#">Sub Item 1.1</a></li>
												<li><a href="#">Sub Item 1.2</a></li>
											</ul>
										</li>
										<li>Folder 2
											<ul>
												<li><a href="#">Sub Item 2.1</a></li>
												<li>Folder 2.1
													<ul>
														<li><a href="#">Sub Item 2.1.1</a></li>
														<li><a href="#">Sub Item 2.1.2</a></li>
													</ul>
												</li>
											</ul>
										</li>
									</ul>
									<script type="text/javascript">
										ddtreemenu.createTree("treemenu1", true)
									</script>
								</div>


							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<hr class="space" />
	</div>
	<div id="footer">
		<hr class="space" />
		<div class="container">
			<div class="span-6 small">
				<a href="https://www.secondmarket.com/?t=lgf"><img
					src="/images/sm-footer.png" alt="SecondMarket" /></a>
				<p>
					Member&nbsp;<a class="footer_link"
						href="http://www.finra.org/index.htm?t=fl" target="_blank">FINRA</a>
					| <a class="footer_link" href="http://www.msrb.org/msrb1/?t=fl"
						target="_blank">MSRB</a> | <a class="footer_link"
						href="http://www.sipc.org/?t=fl" target="_blank">SIPC</a>
				</p>
				<p>
					<a class="footer_link sm-db"
						href="http://support.secondmarket.com/entries/20053063-what-is-an-alternative-trading-system/?t=fl"
						target="_blank">Registered with the SEC as an alternative
						trading system for trading in private company shares.</a>
				</p>
				<a class="footer_link"
					href="https://www.secondmarket.com/discover/reports/sec-606-info?t=fl"
					target="_blank">SEC 606 Info</a>
				<p>
					<a class="footer_link"
						href="https://www.secondmarket.com/business-plan?t=fl">Business
						Continuity Plan</a>
				</p>
				<p>
					Usage of this site constitutes your consent to our <a
						class="footer_link"
						href="https://www.secondmarket.com/privacy-policy?t=fl">Privacy
						Policy</a> and <a class="footer_link"
						href="https://www.secondmarket.com/terms?t=fl">Terms of
						Service</a>
				</p>
				<div>
					&copy; 2012 SecondMarket Holdings, Inc.<br /> All Rights Reserved.
				</div>
			</div>
			<div class="footer_box">
				<h1>Our Markets</h1>
				<ul class="sm-clear-ul sm-pbb sm-line-bottom">
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/private-company?t=fl">Private
							Company Stock</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/community-banks?t=fl">Community
							Bank Stock</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/fixed-income?t=fl">Fixed
							Income</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/bankruptcy-claims?t=fl">Bankruptcy
							Claims</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/public-equity?t=fl">Restricted
							Public Equity</a></li>
				</ul>
				<ul class="sm-clear-ul sm-line-top">
					<li class="small sm-mts"><a class="footer_link"
						href="https://www.secondmarket.com/sm4c/?t=fl">Solutions for
							Companies</a></li>
				</ul>
			</div>
			<div class="footer_box">
				<h1>Resources</h1>
				<ul class="sm-clear-ul">
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/discover/news?t=fl">Press</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/discover/events?t=fl">Events</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="http://alchemy.secondmarket.com?t=fl" target="_blank">Alchemy
							Magazine</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/labs?t=fl">SecondMarket
							Labs</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/discover/learn/legal-learning-center?t=fl">Legal
							Learning Center</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/discover/learn/pc-learning-center?t=fl">Private
							Company Learning Center</a></li>
				</ul>
			</div>
			<div class="footer_box">
				<h1>About Us</h1>
				<ul class="sm-clear-ul">
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/about-us?t=fl">Company
							Overview</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/management?t=fl">Management</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/careers?t=fl">Careers</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/internships?t=fl">Internships</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="http://blog.secondmarket.com?t=fl" target="_blank">Blog</a>
					</li>
					<li class="small sm-mbs"><a class="footer_link"
						href="http://engineering.secondmarket.com?t=fl" target="_blank">Engineering
							Blog</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/impact?t=fl">SecondMarket
							Impact</a></li>
					<li class="small sm-mbs"><a class="footer_link"
						href="https://www.secondmarket.com/contact?t=fl">Contact us</a></li>
				</ul>
			</div>
			<div class="footer_box">
				<h1>Stay Connected</h1>
				<ul class="sm-clear-ul stay-connected">
					<li id="facebook" class="small sm-mbs"><a class="footer_link"
						href="http://www.facebook.com/pages/SecondMarket/68893429074?v=app_4949752878&t=fl"
						target="_blank">Facebook</a></li>
					<li id="twitter" class="small sm-mbs"><a class="footer_link"
						href="http://twitter.com/SecondMarket?t=fl" target="_blank">Twitter</a>
					</li>
					<li id="linkedin" class="small sm-mbs"><a class="footer_link"
						href="http://www.linkedin.com/company/secondmarket-inc.?t=fl"
						target="_blank">LinkedIn</a></li>
					<li id="quora" class="small sm-mbs"><a class="footer_link"
						href="http://www.quora.com/SecondMarket?t=fl" target="_blank">Quora</a>
					</li>
					<li id="madeinnyc" class="small sm-mbs"><a class="footer_link"
						href="http://nytm.org/made/?t=fl" target="_blank">Made In NYC</a>
					</li>
				</ul>
			</div>
		</div>
	</div>


	<div id="profileConnectOverlay" class="sm-tal" style="display: none;"></div>
	<div id="requestAddCompanyOverlay" class="sm-tal"
		style="display: none;">
		<a class="close close-overlay sm-link"></a>
		<div class="select-content"></div>
	</div>
	<div id="selectCompanyOverlay" class="sm-tal" style="display: none;">
		<a class="close close-overlay sm-link"></a>
		<div class="select-content"></div>
	</div>
	<div id="errorBox" class="sm-tal" style="display: none;">
		<a class="close close-overlay sm-link"></a>
		<div class="error-content"></div>
	</div>
	<div id="addUsersOverlay" class="sm-tal" style="display: none;">
		<a class="close close-overlay sm-link"></a>
		<div class="select-content"></div>
	</div>
	<script type="text/javascript"
		src="//asset0.zendesk.com/external/zenbox/zenbox-2.0.js"></script>
	<style type="text/css" media="screen, projection">
@import url(//asset0.zendesk.com/external/zenbox/zenbox-2.0.css);
</style>
	<script type="text/javascript"
		src="//asset0.zendesk.com/external/zenbox/v2.4/zenbox.js"></script>
	<style type="text/css" media="screen, projection">
@import url(//asset0.zendesk.com/external/zenbox/v2.4/zenbox.css);
</style>
	<script type="text/javascript">
    if (typeof(Zenbox) !== "undefined") {
    Zenbox.init({ dropboxID: "20034148", url: "https://secondmarket.zendesk.com", tabID: "support", tabColor: "#11ADD9", tabPosition: "Right" });
    }
</script>
	<script type="text/javascript">
var _sf_async_config={uid:16864,domain:"secondmarket.com"};

(function(){
  function loadChartbeat() {
    window._sf_endpt=(new Date()).getTime();
    var e = document.createElement('script');
    e.setAttribute('language', 'javascript');
    e.setAttribute('type', 'text/javascript');
    e.setAttribute('src',
       (("https:" == document.location.protocol) ? "https://a248.e.akamai.net/chartbeat.download.akamai.com/102508/" : "http://static.chartbeat.com/") +
       "js/chartbeat.js");
    document.body.appendChild(e);
  }
  var oldonload = window.onload;
  window.onload = (typeof window.onload != 'function') ?
     loadChartbeat : function() { oldonload(); loadChartbeat(); };
})();
</script>


</body>
</html>
