<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" isELIgnored="false"%>



<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form"
	prefix="springform"%>



<spring:url value="/resources/img" var="img"></spring:url>
<spring:url value="/resources/css" var="css"></spring:url>
<spring:url value="/resources/fonts" var="fonts"></spring:url>
<spring:url value="/resources/js" var="js"></spring:url>



<link rel="icon" sizes="16x16" href="${img}/index/favicon.png">
<!--css-->
<link rel="stylesheet" type="text/css" href="${css}/style.css" />
<link rel="stylesheet" type="text/css" href="${css}/range-select.css">
<!--BOOTSTRAP-->
<link rel="stylesheet" type="text/css" href="${css}/bootstrap.css" />
<!--fonts-->
<link href="https://fonts.googleapis.com/css?family=Karla:400,700"
	rel="stylesheet">
<link rel="stylesheet" type="text/css"
	href="${fonts}/font-awesome-4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css" href="${fonts}/font/flaticon.css">
<!--slider-->
<link rel="stylesheet" href="${css}/owl.carousel.min.css">
<link rel="stylesheet" type="text/css" href="${css}/testimonial.css">



<spring:url value="${pageContext.request.contextPath}" var="contextPath"></spring:url>

