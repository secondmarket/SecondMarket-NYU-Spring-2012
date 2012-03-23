<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" charset="utf-8" src="/js/mainpaging.js"></script>
<script type="text/javascript" charset="utf-8"
	src="/js/mainsortpaging.js"></script>
<link rel="stylesheet" type="text/css" href="/css/styles.css">
<style type="text/css">
#companyHeaderInMain {
	width: 70%;
	margin-left: 15%;
	margin-right: 15%;
	cellpadding: 5;
	cellspacing: 5;
	background-color: #E6E6E6;
	border: 1px dashed black;
}

#companyHeaderInMain th {
	width: 20%;
	style: text-align:center;
	background: yellow;
}

#companyHeaderInMain th a {
	display: block;
	position: relative
}

#companyHeaderInMain th a:hover {
	background: red;
	color: #fff
}

#content {
	margin-right: 20%;
}
</style>
</head>
<body>
	<div id="top">
		<h2 align="center">SecondMarket Data Aggregation Project</h2>
	</div>

	<div id="searchform" align="center">
		<form:form method="POST" commandName="company"
			action="/SecondMarket/search.htm" target="_blank">
			<table>
				<tr>
					<td><form:label path="companyName">Company Name</form:label></td>
					<td><form:input path="companyName" /></td>
					<td colspan="2"><input type="submit" value="Search"
						class="button" /></td>
				</tr>
			</table>
		</form:form>
	</div>

	<table id="companyHeaderInMain">
		<tr>
			<th><a href="#" onclick=loadSortedRecords("companyName")>Company</a></th>
			<th><a href="#" onclick=loadSortedRecords("location")>Location</a></th>
			<th><a href="#" onclick=loadSortedRecords("country")>Country</a></th>
			<th><a href="#" onclick=loadSortedRecords("fundingAmount")>Funding</a></th>
			<th><a href="#" onclick=loadSortedRecords("industry")>Industry</a></th>
		</tr>
	</table>

	<table id="companyTable" border="1" cellpadding="5" cellspacing="5"
		width="70%" align="center"
		style="background-color: #E6E6E6; border: 1px dashed black;">

	</table><br>


	<div id="content" align="center"></div>
	<input type="hidden" name="page_count" id="page_count" />

	<div align="center">
		<a href="/SecondMarket/mainpage.htm">Home</a>
	</div>
</html>