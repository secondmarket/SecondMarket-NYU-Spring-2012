<%@ include file="/WEB-INF/SecondMarket/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<style>
.button {
	border: 1px solid #666;
	background: #CCC;
	height: 25px;
	width: 100px;
}

.button:hover {
	border: 1px solid #BFBFBF;
	background: #BFBFBF;
}

label {
	width: 80px;
	margin: 2px 4px 6px 4px;
	text-align: right;
}
</style>
</head>
<body>


	<table width="70%" align="center">
		<tr>
			<th><a href="https://www.secondmarket.com/"> <img
					src="../images/secondmarket-logo.png" alt="Second Market"
					title="Click to open SecondMarket website" /></a></th>
			<th><h3>--Data Aggregation Project</h3></th>
		</tr>
	</table>


	<form:form method="POST" commandName="company">
		<fieldset width="70%">
			<legend>Click to import data</legend>
			<label>&nbsp; </label> <input type="submit" value="Import"
				class="button" />
		</fieldset>
	</form:form>


</body>
</html>