<%@page import="twitter4j.auth.AccessToken"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Tuitter Feeder</title>
<meta name="description" content="Twitter Feeder">
<meta name="author" content="SitePoint">
<link rel="stylesheet" href="css/bootstrap.min.css?v=1.0">
</head>


<script src="http://code.jquery.com/jquery-latest.min.js"
	type="text/javascript"></script>
<script src="js/jquery.gracefulWebSocket.js" type="text/javascript"></script>
<script src="js/wsclient.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/favs.js" type="text/javascript"></script>
<script src="js/printTuit.js" type="text/javascript"></script>

<nav></nav>
<!-- End Navigation -->
<!-- Main content area -->
<section>

<br><br>

<%@ page import=" twitter4j.auth.AccessToken" %>

<%

	boolean logged = request.getSession().getAttribute("accessToken") != null;

	if(logged)
	{
		AccessToken at = (AccessToken) request.getSession().getAttribute("accessToken");
		%>
		<input id='username' type="hidden" value='<%=at.getScreenName()%>' />
		
		<div class="row" style="text-align:center">
			<input type="text" class="input-medium search-query" id="filter">
			<button class="btn" id="buttonSC" onclick="wsclient.connect()">Start</button>

			<div class="btn-group">
			  <a class="btn btn-primary" href="#"><i class="icon-user icon-white"></i><%=at.getScreenName()%></a>
			  <a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="caret"></span></a>
			  <ul class="dropdown-menu">
			    <li><a onclick="listFavoritos()"><i class="icon-thumbs-up"></i>Favoritos</a></li>
			    <li><a href='logout'><i class="icon-pencil"></i>Logout</a></li>
			  </ul>
			</div>
		</div>
		<%

	} else {
		%>
		<p style="text-align:center">
		  <a id='login' class="btn btn-large btn-primary" type="button" href='auth'> Login with Twitter</a>
		</p>
		<%
	}
%>



<br>

<div id=content class="container">

	
	
</div> <!-- end container -->
</section>
<!-- End of Main content area -->
<!-- Sidebar -->
<aside>
	<!-- Sidebar 
	<section>
		<ul>
			<li>tuit1</li>
			<li>tuit2</li>
		</ul>
	</section>
	-->
</aside>
<!-- End Sidebar -->
<!-- Footer -->
<footer></footer>
<!-- End of Footer -->
</body>
</html>