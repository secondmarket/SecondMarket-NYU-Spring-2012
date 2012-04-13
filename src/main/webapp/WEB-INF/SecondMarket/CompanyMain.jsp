<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>

<!DOCTYPE html>
<html lang="en" xmlns:fb="http://www.facebook.com/2008/fbml"
	xmlns:og="http://opengraphprotocol.org/schema/">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="description"
	content="Private Company Search on SecondMarket" />

<title>Private Company Search - SecondMarket</title>
<link rel="image_src"
	href="http://www.secondmarket.com/static/images/sm-logo-big.png" />
<link rel="stylesheet" type="text/css" href="/css/paginator.css">
<link rel="stylesheet" type="text/css" media="all" href="/css/all.css" />
<link rel="stylesheet" type="text/css" media="all"
	href="/css/plugin.css" />
<link rel="stylesheet" type="text/css" media="print"
	href="/css/print.css" />
<!--[if IE]><link rel="stylesheet" href="/static/css/blueprint/ie.css" type="text/css" media="screen, projection" /><![endif]-->
<!--[if IE 6]><link rel="stylesheet" href="/static/css/ie-6.css" type="text/css" media="screen, projection" /><![endif]-->
<!--[if IE 7]><link rel="stylesheet" href="/static/css/ie-7.css" type="text/css" media="screen, projection" /><![endif]-->
<!--[if IE 8]><link rel="stylesheet" href="/static/css/ie-8.css" type="text/css" media="screen, projection" /><![endif]-->


<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" charset="utf-8" src="/js/paging.js"></script>

<script type="text/javascript">
    var contextPath = "/";
    var avatarUrl = "https://d36z0f16h51j2.cloudfront.net/";
    var companyLogoUrl = "https://dbr2dggbe4ycd.cloudfront.net/company/";
</script>
<script type="text/javascript">
document.write('<style type="text/css">.js-hide{display:none;}</style>');
</script>
<script type="text/javascript">
var http = ('https:' == document.location.protocol) ? 'https' : 'http';
</script>
<script type="text/javascript">
document.write(unescape('%3Cscript src="' + http + '://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js" type="text/javascript"%3E%3C/script%3E'));
</script>
<script type="text/javascript">
if (typeof jQuery.ui == 'undefined') {
document.write(unescape('%3Cscript src="/static/javascript/jquery-ui-1.8.7.min.js" type="text/javascript"%3E%3C/script%3E'));
}
</script>
<script type="text/javascript" src="/js/all.js"></script>
<script type="text/javascript" src="/js/organizations.js"></script>
<script type="text/javascript" src="/js/often.js"></script>
<script type="text/javascript" src="/js/pagination.js"></script>

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
							<!--  
							<div class="span-8 last">
								<form class="sm-search" action="/search" method="get">
									<fieldset>
										<input type="text" name="search" id="searchBar"/>search bar
										<input type="submit" value="Search" id="searchbtn"
											class="sm-allow-double-submit" />
									</fieldset>
								</form>
							</div>
							-->
						</div>
					</div>
					<hr class="space" />
				</nav>
			</div>
		</div>
		<div id="main">
			<div class="container">
				<!--
				<div class="span-19">
					<ul class="tabs">
						<li><a href="/private-company-market">Overview</a></li>
						<li><a href="/private-company-search" class="current">Search</a></li>
					</ul>
				</div>
				-->
				<hr />
				<div class="pane">
					<div id="sm-main-content" class="span-24">
						<div class="clear"></div>
						<div class="span-7 append-1">
							<form method="get" id="companyFilterForm" action="">
								<div class="sm-span-7">
									<fieldset id="filter" class="sm-search-box">
										<legend>Search Companies</legend>
										<input name="search" type="text" class="text sm-mb"
											id="companyFilterSearchString" />
										<button type="submit" id="companyFilterSearch"
											class="sm-allow-double-submit"
											onclick="Javascript: searchByCompanyName(companyFilterSearchString.value);">Search</button>
										<a href="#" onclick="Javascript: clearAll();"
											id="companyFilterClea" class="filterClear">Clear</a>
									</fieldset>
								</div>
								<hr class="space" />
								<div class="sm-span-7">
									<h2>Filter Companies</h2>
									<fieldset id="filter" class="sm-expand-menu">
										<div class="open">
											<a class="sm-link sm-expand-menu sm-b">Country</a>
										</div>
										<ul class="sm-clear-ul">
											<li><select name="country" id="country" class="sm-mt"
												onchange="Javascript: filterByCountry();">
													<option value="all">All Countries</option>
													<option value="USA">United States</option>
													<option value="ARG">Argentina</option>
													<option value="AUS">Australia</option>
													<option value="AUT">Austria</option>
													<option value="BAH">Bahamas</option>
													<option value="BAN">Bangladesh</option>
													<option value="BRB">Barbados</option>
													<option value="BEL">Belgium</option>
													<option value="BER">Bermuda</option>
													<option value="BRA">Brazil</option>
													<option value="BUL">Bulgaria</option>
													<option value="CMR">Cameroon</option>
													<option value="CAN">Canada</option>
													<option value="CAY">Cayman Islands</option>
													<option value="CHN">China</option>
													<option value="CHI">Chile</option>
													<option value="COL">Colombia</option>
													<option value="CRO">Croatia</option>
													<option value="CYP">Cyprus</option>
													<option value="CZE">Czech Republic</option>
													<option value="DEN">Denmark</option>
													<option value="DMA">Dominica</option>
													<option value="EGY">Egypt</option>
													<option value="EST">Estonia</option>
													<option value="FIN">Finland</option>
													<option value="FRA">France</option>
													<option value="GER">Germany</option>
													<option value="GHA">Ghana</option>
													<option value="GIB">Gibraltar</option>
													<option value="GRE">Greece</option>
													<option value="HKG">Hong Kong</option>
													<option value="HUN">Hungary</option>
													<option value="ISL">Iceland</option>
													<option value="IND">India</option>
													<option value="IE">Ireland</option>
													<option value="ISR">Israel</option>
													<option value="ITA">Italy</option>
													<option value="JPN">Japan</option>
													<option value="JOR">Jordan</option>
													<option value="KEN">Kenya</option>
													<option value="KOR">South Korea</option>
													<option value="LVA">Latvia</option>
													<option value="LIB">Lebanon</option>
													<option value="LTU">Lithuania</option>
													<option value="LUX">Luxembourg</option>
													<option value="MAX">Malaysia</option>
													<option value="MLT">Malta</option>
													<option value="MEX">Mexico</option>
													<option value="MC">Monaco</option>
													<option value="NED">Netherlands</option>
													<option value="NZL">New Zealand</option>
													<option value="NGA">Nigeria</option>
													<option value="NOR">Norway</option>
													<option value="PAK">Pakistan</option>
													<option value="PHI">Philippines</option>
													<option value="POL">Poland</option>
													<option value="POR">Portugal</option>
													<option value="REU">Reunion</option>
													<option value="RU">Russian Federation</option>
													<option value="SIN">Singapore</option>
													<option value="SVK">Slovakia</option>
													<option value="SVN">Slovenia</option>
													<option value="RSA">South Africa</option>
													<option value="ESP">Spain</option>
													<option value="SWE">Sweden</option>
													<option value="SUI">Switzerland</option>
													<option value="TWN">Taiwan</option>
													<option value="THA">Thailand</option>
													<option value="TUN">Tunisia</option>
													<option value="TUR">Turkey</option>
													<option value="UKR">Ukraine</option>
													<option value="UAE">United Arab Emirates</option>
													<option value="GBR">United Kingdom</option>
													<option value="UZB">Uzbekistan</option>
											</select></li>
										</ul>
									</fieldset>
								
									<fieldset id="industry" class="sm-expand-menu"
										onclick="Javascript: filterByIndustry();">
										<div class="open">
											<a class="sm-link sm-expand-menu sm-b">Industry</a>
										</div>
										<ul class="sm-clear-ul">
											<li><a class="sm-select-all sm-link" rel="nofollow">Select
													All</a> | <a class="sm-select-none sm-link" rel="nofollow">Clear</a>
											</li>
											<li><input type="checkbox" value="Advertising"
												class="sm-checkbox" name="industry" id="Advertising"
												checked="checked"> <label for="Advertising">Advertising
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Biotech"
												class="sm-checkbox" name="industry" id="Biotech"
												checked="checked"> <label for="Biotech">Bio
													Tech <a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Cleantech"
												class="sm-checkbox" name="industry" id="Cleantech"
												checked="checked"> <label for="Cleantech">Clean
													Tech <a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Hardware"
												class="sm-checkbox" name="industry" id="Hardware"
												checked="checked"> <label for="Hardware">Consumer
													Electronics/ Devices <a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Web"
												class="sm-checkbox" name="industry" id="Web"
												checked="checked"> <label for="Web">Consumer
													Web <a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Ecommerce"
												class="sm-checkbox" name="industry" id="Ecommerce"
												checked="checked"> <label for="Ecommerce">eCommerce
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Education"
												class="sm-checkbox" name="industry" id="Education"
												checked="checked"> <label for="Education">Education
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Enterprise"
												class="sm-checkbox" name="industry" id="Enterprise"
												checked="checked"> <label for="Enterprise">Enterprise
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Games_video"
												class="sm-checkbox" name="industry" id="Games_video"
												checked="checked"> <label for="Games_video">Game,Video
													and Entertainment <a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Legal"
												class="sm-checkbox" name="industry" id="Legal"
												checked="checked"> <label for="Legal">Legal
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Mobile"
												class="sm-checkbox" name="industry" id="Mobile"
												checked="checked"> <label for="Mobile">Mobile/Wireless
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Network-hosting"
												class="sm-checkbox" name="industry" id="Network-hosting"
												checked="checked"> <label for="Network-hosting">Network/Hosting
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Consulting"
												class="sm-checkbox" name="industry" id="Consulting"
												checked="checked"> <label for="Consulting">Consulting
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Public_relations"
												class="sm-checkbox" name="industry" id="Public_relations"
												checked="checked"> <label for="Public_relations">Communications
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Search"
												class="sm-checkbox" name="industry" id="Search"
												checked="checked"> <label for="Search">Search
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Security"
												class="sm-checkbox" name="industry" id="Security"
												checked="checked"> <label for="Security">Security
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Semiconductor"
												class="sm-checkbox" name="industry" id="Semiconductor"
												checked="checked"> <label for="Semiconductor">Semiconductor
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Software"
												class="sm-checkbox" name="industry" id="Software"
												checked="checked"> <label for="Software">Software
													<a class="sm-link sm-only">only</a>
											</label></li>
											<li><input type="checkbox" value="Other"
												class="sm-checkbox" name="industry" id="Other"
												checked="checked"> <label for="Other">Other
													<a class="sm-link sm-only">only</a>
											</label></li>
										</ul>
									</fieldset>

									<fieldset id="fundings" class="sm-expand-menu">
										<div class="open">
											<a class="sm-link sm-expand-menu sm-b">Total Funding</a>
										</div>

										<ul class="sm-clear-ul">
											<li id="funding-container"><select name="minFunding"
												id="minFunding" class="js-hide">
													<option value="0" selected="selected">&lt;$50,000</option>
													<option value="50000">$50,000</option>
													<option value="100000">$100,000</option>
													<option value="250000">$250,000</option>
													<option value="500000">$500,000</option>
													<option value="1000000">$1M</option>
													<option value="10000000">$10M</option>
													<option value="25000000">$25M</option>
													<option value="50000000">$50M</option>
													<option value="100000000">$100M</option>
													<option value="100000001">&gt;$100M</option>
											</select> <select name="maxFunding" id="maxFunding" class="js-hide">
													<option value="0">&lt;$50,000</option>
													<option value="50000">$50,000</option>
													<option value="100000">$100,000</option>
													<option value="250000">$250,000</option>
													<option value="500000">$500,000</option>
													<option value="1000000">$1M</option>
													<option value="10000000">$10M</option>
													<option value="25000000">$25M</option>
													<option value="50000000">$50M</option>
													<option value="100000000">$100M</option>
													<option value="100000001" selected="selected">&gt;$100M</option>
											</select></li>
											<li>
											<button type="submit" id="companyFilterSearch"
											class="sm-allow-double-submit"
											onclick="Javascript: filterByFundingRange()">Apply Range</button>
											<br>
											<br>
											</li>
										</ul>
									</fieldset>

									<fieldset id="minWatchers"
										class="sm-expand-menu sm-expand-menu-last" onclick="Javascript: filterByEmployee();">
										<div class="open">
											<a class="sm-link sm-expand-menu sm-b">Number of
												Employees</a>
										</div>
										<ul class="sm-clear-ul">
											<li class="sm-mt"><input type="radio" value="all"
												class="sm-checkbox" name="employee"
												id="filter_number_of_watchers_all" checked="checked">
												<label for="filter_number_of_watchers_all" class="sm-b">All</label>
											</li>
											<li><input type="radio" value="10" class="sm-checkbox"
												name="employee" id="filter_10"> <label
												for="filter_10">10+</label></li>
											<li><input type="radio" value="50" class="sm-checkbox"
												name="employee" id="filter_50"> <label
												for="filter_50">50+</label></li>
											<li><input type="radio" value="100" class="sm-checkbox"
												name="employee" id="filter_100"> <label
												for="filter_100">100+</label></li>
											<li class="sm-mb"><input type="radio" value="500"
												class="sm-checkbox" name="employee" id="filter_500">
												<label for="filter_500">500+</label></li>
										</ul>
									</fieldset>

								</div>
							</form>
							<hr class="space" />

							<!--  
							search a company
							<h3>Suggest a Company</h3>

							<div class="sm-shadow-box sm-watch-list-widget">
								<p>Can't find the company you're looking for?</p>
								<a class="sm-button sm-request-company"
									rel="requestAddCompanyOverlay" href="/company/request-new">Request
									a company to be added</a>
							</div>
							-->
							<hr class="space" />
						</div>
						<div id="companyDirectory" class="span-16 last">

							<div id="loadingdiv" class="sm-r bbq-loading"
								style="display: none">
								<div id="loadingscreen">
									<h2>Updating results</h2>
									<img src="/images/sm-loading.gif" />
								</div>
							</div>

							<div class="bbq-content">
								<div class="page1" id="CompanyMainTable">

									<!--  
									<div class="sm-card sm-span-16 sm-unhide last sm-mb">
										<div class="span-2 last">
											<a href="/company/facebook"><img class="sm-badge"
												alt="Facebook"
												src="https://dbr2dggbe4ycd.cloudfront.net/company/facebook_50.png"></a>
										</div>
										<table border="0" cellspacing="0" cellpadding="0">
											<tr>
												<td class="sm-b sm-card-name" width="410"><a
													href="/company/facebook" class="sm-card-link">Facebook</a></td>
												<td width="100" class="sm-tar"><div>Since
														December 2004</div></td>
											</tr>
											<tr>
												<td>Web Industry</td>
												<td class="sm-tar">500 Employees</td>
											</tr>
											<tr>
												<td width="300">Palo Alto, CA</td>
												<td rowspan="2" class="sm-r"><form
														action="/watchlist/add" alt="/watchlist/remove"
														method="post" class="sm-watch-unwatch-form sm-mts">
														<input type="hidden" class="sm-watch-slug" name="slug"
															value="twitter" />
														<button type="submit" rel="twitter"
															class="span-4 sm-watch-toggle sm-watch-button sm-button sm-mbs"
															hoveralt='$2.34B' clickalt='$2.34B' changealt='$2.34B'
															watchalt='$2.34B'>$2.34B</button>
													</form></td>
											</tr>
										</table>
									</div>
									-->


								</div>
							</div>

							<hr class="space" />

							<div id="paginator" class="paginatorclass"></div>
							<input id="page_count" type="hidden" name="page_count" />

							<!--  
							<div id="smPagination">
								<ul id="sm-pagination" class="sm-pagination hashlink sm-pg-full">
									<li class="inactive"><a
										href="/private-company-search?page=1" rel="companyDirectory">Previous</a>
									</li>
									<li class="current"><a
										href="/private-company-search?page=1" rel="companyDirectory">1</a>
									</li>
									<li class=""><a href="/private-company-search?page=2"
										rel="companyDirectory">2</a></li>
									<li class=""><a href="/private-company-search?page=3"
										rel="companyDirectory">3</a></li>
									<li class=""><a href="/private-company-search?page=4"
										rel="companyDirectory">4</a></li>
									<li class=""><a href="/private-company-search?page=5"
										rel="companyDirectory">5</a></li>
									<li class=""><a href="/private-company-search?page=6"
										rel="companyDirectory">6</a></li>
									<li class="">&nbsp;...&nbsp;</li>
									<li class=""><a href="/private-company-search?page=1116"
										rel="companyDirectory">1116</a></li>
									<li class=""><a href="/private-company-search?page=2"
										rel="companyDirectory">Next</a></li>
								</ul>
							</div>
							-->


						</div>
					</div>
				</div>

				<div class="pane"></div>
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
					Member&nbsp<a class="footer_link"
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

