<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.project.Restaurant"  %>
<%@ page import="com.project.RestaurantDAO"  %>
<%@ page import="java.util.*"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/style.css" type="text/css" />
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<title>Insert title here</title>
</head>
<style>
    
</style>


<body background = "search.png">
	<%
	ArrayList<Restaurant> restaurantList = (ArrayList<Restaurant>)request.getAttribute("restaurantResults");
	String name = (String)request.getAttribute("username");
	%>
	<header>
        <div id="header-wrapper">
        	<div class="w3-bar w3-teal" id="navigation-wrapper">
                <div>
                    <a href="register.jsp" class="w3-bar-item w3-button">Home</a>
                    <a href="#" class="w3-bar-item w3-button">Link 1</a>
                    <a href="#" class="w3-bar-item w3-button">Link 2</a>
                    <a href="#" class="w3-bar-item w3-button"><%=username %></a>
                </div>
            </div>
            <div id="search-wrapper">
            	<form action = "Search" method = "get">
                	<button type="submit">Search</button>	
             		<input name="keyword" type="search" placeholder="Search..">
             		<input type="hidden" name="nameOfUser" value=<%=username %>>
             	</form>
             </div>
         </div>
    </header>
	
	<%

	
	if(restaurantList ==null){
		out.println("No restaurants havve been identified with that name");
	}
	
	
	else{
	%>
	<div class ="topnavigator" align="left">
		<%
			for(Restaurant r:restaurantList){ %>

				<div class = "row" >
					<div class = "col" align ="center">
						<p class = "normal">
							<td> <%= r.getRestaurantName() %> </td>
						</p>
						
						<form action = "Search" method = "POST">
							<input type = "hidden" name = "restaurantID" value=<%=r.getRestaurantID() %> />
							<input type = "hidden" name = "restaurantName" value = <%= r.getRestaurantName() %> />
							<input type = "hidden" name = "restaurantAddress" value = <%= r.getRestaurantAddress() %> />
							<input type = "hidden" name = "restaurantType" value = <%= r.getRestaurantType() %> />
							<input type = "hidden" name = "username" value = <%=name %>>
	                		<input type="submit" name="Click" value="Click" >
            			</form>
					</div>
					
					<div class ="col">
						<p class = "normal">
							<td> <%=r.getRestaurantAddress() %></td>
						</p>
						
					</div>
					
				</div>
			<%}%>
			</div>
	<%}
	%>
	
</body>
</html>
