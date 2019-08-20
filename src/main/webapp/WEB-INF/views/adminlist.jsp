<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" isELIgnored="false"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<%@include file="links.jsp"%>


<link
	href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css"
	rel="stylesheet">
<!-- <link
	href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.1.3/css/bootstrap.css"
	rel="stylesheet">
<link
	href="https://cdn.datatables.net/1.10.19/css/dataTables.bootstrap4.min.css"
	rel="stylesheet"> -->


</head>
<body>

	<div class="about-page">
		<%@include file="header.jsp"%>

		<%@include file="searchmenu.jsp"%>
		<hr>
		<div class="container" style="margin-top: 20px;">
			<c:if test="${id == false}">
				<span class='error_msg'>Employee is not existed</span>
			</c:if>
			<table id="adminsListTable"
				class="table table-striped table-bordered">
				<thead>
					<tr>
						<th>Username</th>
						<th>Email</th>
						<th>Mobile</th>
						<th>Category</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="${admins}" var="admin">
					<tbody>
						<tr>
							<td>${admin.username}</td>
							<td>${admin.email}</td>
							<td>${admin.mobile}</td>
							<td>${admin.adminDetails.category.categoryName}</td>
							<c:if test="${admin.isActive==true}">
								<td><a
									href="${contextPath}/owner/editadmin/${admin.userId}"
									class="btn btn-warning">Edit</a></td>
								<td><a
									href="${contextPath}/owner/disableadmin/${admin.userId}"
									class="btn btn-danger">Disable</a>
							</c:if>
							<c:if test="${admin.isActive==false}">
								<a
									href="${contextPath}/owner/activateadmin/${admin.userId}"
									class="btn btn-info">Active</a>
							</c:if>
							</td>
						</tr>
					</tbody>
				</c:forEach>
			</table>


		</div>



		<%@include file="footer.jsp"%>
	</div>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#adminsListTable').DataTable();
		});
	</script>


	<script type="text/javascript"
		src="https://code.jquery.com/jquery-3.3.1.js"></script>
	<script type="text/javascript"
		src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript"
		src="https://cdn.datatables.net/1.10.19/js/dataTables.bootstrap4.min.js"></script>

</body>
</html>