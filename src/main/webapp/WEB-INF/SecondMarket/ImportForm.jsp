<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<style>
.error {
	color: #ff0000;
	width:70%;
}

.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
</head>
<body>
	<h2>SecondMarket Data Aggregation Project</h2>

	<form:form method="POST" commandName="company">
		<form:errors path="*" cssClass="errorblock" element="div" />
		<table>
			<tr>
				<td>Company Name :</td>
				<td><form:input path="companyName" /></td>
				<td><form:errors path="companyName" cssClass="error" /></td>
			</tr>

			<tr>
				<td colspan="3"><input type="submit" value="Import"/></td>
			</tr>
		</table>
	</form:form>

 
</body>
</html>