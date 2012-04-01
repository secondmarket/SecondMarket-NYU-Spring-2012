<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" charset="utf-8" src="/js/searchpaging.js"></script>
<link rel="stylesheet" type="text/css" href="/css/styles.css">
</head>
<body>

	<%
		out.println("<input type=\"hidden\" id=\"searchedname\" name=\"searchedname\" value=\""
				+ (String) request.getAttribute("searchedname") + "\" />");
	%>


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

	<table id="companyHeader" border="1" cellpadding="5" cellspacing="5"
		width="70%" align="center"
		style="background-color: #E6E6E6; border: 1px black;">

		<tr>
			<th width="20%" style="text-align: center">Company</th>
			<th width="20%" style="text-align: center">Location</th>
			<th width="20%" style="text-align: center">Country</th>
			<th width="20%" style="text-align: center">Funding</th>
			<th width="20%" style="text-align: center">Industry</th>
		</tr>
	</table>
	<table id="companyTable" border="1" cellpadding="5" cellspacing="5"
		width="70%" align="center"
		style="background-color: #E6E6E6; border: 1px dashed black;">

	</table>

	<div id="paginator" class="paginatorclass">
	
	</div>
	<input type="hidden" name="searchedpage_count" id="searchedpage_count" />

	<div align="center">
		<a href="javascript: self.close();">Close Search</a>
	</div>
</html>