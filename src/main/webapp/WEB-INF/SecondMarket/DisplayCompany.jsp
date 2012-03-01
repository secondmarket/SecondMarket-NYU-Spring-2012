<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
		<tr>
			<td width="20%" style="text-align: center">${company.companyName}</td>
			<td width="20%" style="text-align: center">${company.location}</td>
			<td width="20%" style="text-align: center">${company.country}</td>
			<td width="20%" style="text-align: center">${company.funding}</td>
			<td width="20%" style="text-align: center">${company.industry}</td>

		</tr>
	</table>

	<div align="center">
		<a href="/SecondMarket/import.htm">Home</a>
	</div>
</body>
</html>


