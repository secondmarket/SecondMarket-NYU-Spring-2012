<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" import="com.secondmarket.model.Company"%>
<%@ page language="java" import="java.util.*"%>
<html>
<head>
<script type="text/javascript" src="/js/CeeBoxjs/ceeboxjquery.js"></script>
<script type="text/javascript" src="/js/CeeBoxjs/jquery.ceebox-min.js"></script>
<script type="text/javascript" src="/js/CeeBoxjs/jquery.swfobject.js"></script>
<script type="text/javascript">
 $(document).ready(function(){
	$(".videoclass").ceebox({
		borderColor:'#666',
		boxColor: "#000",
		titles: false
	});
		});
</script>

<link rel="stylesheet" type="text/css" href="/css/ceebox-min.css" />
</head>
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
			<th style="text-align: center">Logo</th>
			<td style="text-align: center">
				<%
					Company company = (Company) request.getAttribute("company");
					out.print("<img src=\"/SecondMarket/getLogo.htm?companyName="
							+ company.getCompanyName() + "\">");
				%>
			</td>
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
			<th style="text-align: center">Video</th>
			<td style="text-align: center">
			<div class="videoclass">
				<%
					List<String> videoSrcUrl = company.getVideos();
					int number = 1;
					for (String url : videoSrcUrl) {
						if (url != null && url.length() == 0) {
							out.println("No available video");
						} else {
							out.println("<a href=\""
									+ url
									+ "\" rel=\"width:420 height:380\" style=\"text-decoration: none\">Video["
									+ number + "]</a>");
							number++;
						}
					}
				%>
			</div>
			</td>
		</tr>

		<tr>
			<th style="text-align: center">Overview</th>
			<td>${company.overview}</td>
		</tr>
	</table>

	<div align="center">
		<table>
			<tr>
				<th><a href="javascript: self.close();">Close</a></th>
			</tr>
		</table>
	</div>
</body>
</html>


