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
			<td style="text-align: center">${company.companyName}</td>
		</tr>

		<tr>
			<th style="text-align: center">Funding</th>
			<td style="text-align: center">${company.funding}</td>
		</tr>

		<tr>
			<th style="text-align: center">Industry</th>
			<td style="text-align: center">${company.industry}</td>
		</tr>

		<tr>
			<th style="text-align: center">Location</th>
			<td style="text-align: center">${company.location}</td>
		</tr>

		<tr>
			<th style="text-align: center">Country</th>
			<td style="text-align: center">${company.country}</td>
		</tr>

		<tr>
			<th style="text-align: center">Overview</th>
			<td>${company.overview}</td>
		</tr>
	</table>

	<div align="center">
		<table>
			<tr>
				<th><a href="javascript: history.go(-1)">Go back</a>&nbsp;&nbsp;</th>
				<th><a href="/SecondMarket/importall.htm">Home</a></th>
			</tr>
		</table>
	</div>
</body>
</html>


