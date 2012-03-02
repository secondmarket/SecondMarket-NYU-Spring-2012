<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" import="java.util.*"%>
<%@ page language="java" import="com.secondmarket.model.Company"%>
<html>
<body>
	<h2 align="center">SecondMarket Data Aggregation Project</h2>

	<table border="1" cellpadding="5" cellspacing="5" width="70%"
		align="center"
		style="background-color: #E6E6E6; border: 1px dashed black;">
		<tr>
			<th style="text-align: center">Company</th>
			<th style="text-align: center">Location</th>
			<th style="text-align: center">Country</th>
			<th style="text-align: center">Funding</th>
			<th style="text-align: center">Industry</th>
		</tr>

		<%
			Iterator<Company> itr;
		%>
		<%
			List<Company> data = (List<Company>) request
					.getAttribute("companies");
			for (itr = data.iterator(); itr.hasNext();) {
				Company company = (Company) itr.next();
		%>

		<tr>
			<td width="20%"><%=company.getCompanyName()%></td>
			<td width="20%"><%=company.getLocation()%></td>
			<td width="20%"><%=company.getCountry()%></td>
			<td width="20%"><%=company.getFunding()%></td>
			<td width="20%"><%=company.getIndustry()%></td>
		</tr>

		<%
			}
		%>

		<%-- <c:forEach var = "company" items = "${companies}">
			<c:out value="${company.companyName}"/>
			<c:out value="${company.location}" />
			<c:out value="${company.country}" />
			<c:out value="${company.funding}" />
			<c:out value="${company.industry}" />
		</c:forEach> --%>

	</table>
	<div align="center">
		<a href="/SecondMarket/import.htm">Home</a>
	</div>
</body>
</html>


