package com.project;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.mysql.jdbc.PreparedStatement;



public class RestaurantDAO extends HttpServlet{
	
	
	public RestaurantDAO() {
		super();
	}
	
	/**
	 * This method is called once a button pertaining to a restaurant is clicked or if a user likes or dislikes a
	 * review found on the restaurant's page
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//if parameter contains Click, then this indicates a redirecting to the restaurant page
		if(request.getParameter("Click") !=null) {
			String id= ((ServletRequest)request).getParameter("restaurantID").toString();
			int restID = Integer.parseInt(id);
			String restName = ((ServletRequest)request).getParameter("restaurantName").toString();
			String restAddress = ((ServletRequest)request).getParameter("restaurantAddress").toString();
			String restType = ((ServletRequest)request).getParameter("restaurantType").toString();
			
			String username = ((ServletRequest)request).getParameter("username").toString();
			
			
			Restaurant aRestaurant = new Restaurant();
			aRestaurant.setRestaurantID(restID);
			aRestaurant.setRestaurantName(restName);
			aRestaurant.setRestaurantType(restType);
			BusinessInfo busInfo = this.retrieveRestaurantInfo(restID);
			aRestaurant.setRestaurantBusinessInfo(busInfo);
			
			request.setAttribute("chosenRestaurant", aRestaurant);
			request.setAttribute("loggedUser", username);
			RequestDispatcher reqDispatcher = request.getRequestDispatcher("restaurant.jsp");
			reqDispatcher.forward(request, response);
		}
		//else, if the review number is entered, then update the review's stats by issuing either a like or dislike for that user
		else if(request.getParameter("Like") !=null) {
			String user = ((ServletRequest)request).getParameter("username").toString();
			String revNum= ((ServletRequest)request).getParameter("reviewNumber").toString();
			String restID = ((ServletRequest)request).getParameter("restaurID").toString();
			
			String restName = ((ServletRequest)request).getParameter("restaurantName").toString();
			String restAddress = ((ServletRequest)request).getParameter("restaurantAddress").toString();
			String restType = ((ServletRequest)request).getParameter("restaurantType").toString();
			
			int revNumber = Integer.parseInt(revNum);
			int restaurID = Integer.parseInt(restID);
			
			//if the content of user is not equivalent to null, then don't restrict in liking a review
			if(!user.equals(null)) {
				Review r = new Review(restaurID, revNumber);
				r.updateLikeOrDislike(user,true, restaurID);
			}
		
			//must reinstantiate a Restaurant object once again, since forwarding to restaurant.jsp has to be done
			Restaurant aRestaurant = new Restaurant();
			aRestaurant.setRestaurantID(restaurID);
			aRestaurant.setRestaurantName(restName);
			aRestaurant.setRestaurantType(restType);
			BusinessInfo busInfo = this.retrieveRestaurantInfo(restaurID);
			aRestaurant.setRestaurantBusinessInfo(busInfo);
			
			request.setAttribute("chosenRestaurant", aRestaurant);
			request.setAttribute("loggedUser", user);
			RequestDispatcher reqDispatcher = request.getRequestDispatcher("restaurant.jsp");
			reqDispatcher.forward(request, response);
			
		}
		else if(request.getParameter("Dislike")!=null) {
			//loading the passed parameters from restaurant.jsp which include attributes for the restaurant alongside the username
			String user = ((ServletRequest)request).getParameter("username").toString();
			String revNum= ((ServletRequest)request).getParameter("reviewNumber").toString();
			String restID = ((ServletRequest)request).getParameter("restaurID").toString();
			
			String restName = ((ServletRequest)request).getParameter("restaurantName").toString();
			String restAddress = ((ServletRequest)request).getParameter("restaurantAddress").toString();
			String restType = ((ServletRequest)request).getParameter("restaurantType").toString();
			
			
			int revNumber = Integer.parseInt(revNum);
			int restaurID = Integer.parseInt(restID);
			
			//if the content of user is not equivalent to null, then don't restrict in disliking a review
			if(!user.equals(null)) {
				Review r = new Review(restaurID, revNumber);
				r.updateLikeOrDislike(user,false, restaurID);
			}
			
			//must reinstantiate a Restaurant object once again, since forwarding to restaurant.jsp has to be done
			Restaurant aRestaurant = new Restaurant();
			aRestaurant.setRestaurantID(restaurID);
			aRestaurant.setRestaurantName(restName);
			aRestaurant.setRestaurantType(restType);
			BusinessInfo busInfo = this.retrieveRestaurantInfo(restaurID);
			aRestaurant.setRestaurantBusinessInfo(busInfo);
			
			request.setAttribute("chosenRestaurant", aRestaurant);
			request.setAttribute("loggedUser", user);
			RequestDispatcher reqDispatcher = request.getRequestDispatcher("restaurant.jsp");
			reqDispatcher.forward(request, response);
		}
		//else if the Write Review paramter is passed, then add the review to the restaurant's review history
		else if (request.getParameter("Write Review")!=null) {
			
			//loading the passed parameters from restaurant.jsp which contain the current username, review comment, and the rating from the radio button
			String author = ((ServletRequest)request).getParameter("username");
			String comment = ((ServletRequest)request).getParameter("review");
			String rating = ((ServletRequest)request).getParameter("radio");
			
			//loading the passed parameters from restaurant.jsp which include attributes for the restaurant
			String restaurantID = ((ServletRequest)request).getParameter("restaurantID");
			String restName  = ((ServletRequest)request).getParameter("restaurantName");
			String restAddress = ((ServletRequest)request).getParameter("restaurantAddress");
			String restType = ((ServletRequest)request).getParameter("restaurantType");
			
			
			
			int restaurID = Integer.parseInt(restaurantID);
			
			//if the username is not equivalent to null, then the user's review is accepted and saved
			if(!author.equals(null)) {
				//if the rating is equivalent to null, then the review submission is a failure and user will be notified
				if(rating==null) {
					request.setAttribute("errorMessage", "Must select a rating selection from 1-5");
				}
				//else if is not null, then the review submission is fully completed
				else {
					int stars = Integer.parseInt(rating);
					Review newReview = new Review(restaurID, comment, stars);
					this.addReview(newReview, restaurID, author);
					
				}	
			}
			//must reinstantiate a Restaurant object once again, since forwarding to restaurant.jsp has to be done
			Restaurant aRestaurant = new Restaurant();
			aRestaurant.setRestaurantID(restaurID);
			aRestaurant.setRestaurantName(restName);
			aRestaurant.setRestaurantType(restType);
			BusinessInfo busInfo = this.retrieveRestaurantInfo(restaurID);
			aRestaurant.setRestaurantBusinessInfo(busInfo);
			
			request.setAttribute("chosenRestaurant", aRestaurant);
			request.setAttribute("loggedUser", author);
			RequestDispatcher reqDispatcher = request.getRequestDispatcher("restaurant.jsp");
			reqDispatcher.forward(request, response);
			
			
		}
		
	
	}
	

	
	/**
	 * This method processes the search results from the search bar to obtain restaurant results from theh database
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException{
		String restaurantName = ((ServletRequest) request).getParameter("keyword").toString();
		String nameOfUser = ((ServletRequest) request).getParameter("nameOfUser").toString();
		
		ArrayList listOfRestaurants = this.searchRestaurant(restaurantName);
		
		
		response.setContentType("text/html");
		System.out.println(listOfRestaurants);
		//setting attribute of username an the restaurants list to pass onto the searchresults.jsp page
		//NOTE: nameofuser will be null if there is no user logged in the current session
		request.setAttribute("username", nameOfUser);
		request.setAttribute("restaurantResults", listOfRestaurants);
		RequestDispatcher reqDispatcher = request.getRequestDispatcher("/searchresults.jsp");
		reqDispatcher.forward(request, response);
	}
	

	//This method searches the designated restaurant name in the database and gives back results of that particular restaurant name
	public ArrayList<Restaurant> searchRestaurant(String restaurantName) {
		
		ArrayList<Restaurant> searchResults = new ArrayList<Restaurant>();
		Connection c= getConnection();
		try {
			PreparedStatement statement = (PreparedStatement) c.prepareStatement("SELECT * from restaurant where restaurantName=?");
			statement.setString(1,restaurantName );
			ResultSet rs = statement.executeQuery();
			//if the result set is empty, this indicates the lack of search results
			if(rs==null) {
				return null;
			}
			//else result set contains search results 
			else {
				while(rs.next()) {
					Restaurant r = new Restaurant();
					int restID = rs.getInt("restaurantID");
					String restAddr = rs.getString("restaurantAddress");
					String restType = rs.getString("restaurantType");
					String restName = rs.getString("restaurantName");
					
					
					r.setRestaurantID(restID);
					r.setRestaurantName(restName);
					r.setRestaurantAddress(restAddr);
					r.setRestaurantType(restType);
					searchResults.add(r);
					
				}
			}
			System.out.println("SWEET");
			return searchResults;
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	
	
	}
	
	/**
	 * This method retrieves the restaurant's additional business information including hours of operation,etc
	 * @return
	 */
	public BusinessInfo retrieveRestaurantInfo(int restaurantID) {
		Connection conn = getConnection();
		try {
			PreparedStatement ptsmt = (PreparedStatement) conn.prepareStatement("SELECT * from restaurant_hours where restaurantID = ?");
			ptsmt.setInt(1, restaurantID);
			ResultSet rs = ptsmt.executeQuery();
			
			ArrayList<String> dailyHours = new ArrayList<String>();
			
			while(rs.next()) {
				String sundayHours = rs.getString("Sunday");
				String mondayHours = rs.getString("Monday");
				String tuesdayHours = rs.getString("Tuesday");
				String wednesdayHours = rs.getString("Wednesday");
				String thursdayHours = rs.getString("Thursday");
				String fridayHours = rs.getString("Friday");
				String saturdayHours = rs.getString("Saturday");
				
				dailyHours.add(sundayHours);
				dailyHours.add(mondayHours);
				dailyHours.add(tuesdayHours);
				dailyHours.add(wednesdayHours);
				dailyHours.add(thursdayHours);
				dailyHours.add(fridayHours);
				dailyHours.add(saturdayHours);
				
				
				BusinessInfo busInfo = new BusinessInfo();
				busInfo.addBusinessInfo(dailyHours);
				busInfo.showBusinessHours();
				
				return busInfo;
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method retrieves the reviews of a particular restaurant
	 * @return
	 */
	public ArrayList<Review> retrieveReviews(int restaurantID){
		try {
			ArrayList<Review> restaurantReviews = new ArrayList<Review>();
			Connection c = getConnection();
			PreparedStatement ptsmt = (PreparedStatement)c.prepareStatement("SELECT * from review where restaurantID =?");
			ptsmt.setInt(1,restaurantID);
			ResultSet rs = ptsmt.executeQuery();
			
			while(rs.next()) {
				int restID = rs.getInt("restaurantID");
				int reviewNum = rs.getInt("reviewNumber");
				int starsRating = rs.getInt("starRating");
				String comment = rs.getString("reviewContent");
				String author = rs.getString("userName");
				Review rev = new Review(restID, reviewNum);
				rev.setReviewNumber(reviewNum);
				rev.setStarsGiven(starsRating);
				rev.setReviewContent(comment);
				rev.setReviewAuthor(author);
				
				restaurantReviews.add(rev);
			}
			return restaurantReviews;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method adds a review to the restaurant's history of reviews...
	 */
	public void addReview(Review recentReview, int restaurantID, String author) {
		try {
			Connection c = getConnection();
			PreparedStatement statement = (PreparedStatement) c.prepareStatement("INSERT INTO review(userName, restaurantID, reviewNumber, reviewContent, starRating) values(?, ?, ?, ?, ?)");
			statement.setString(1, author);
			statement.setInt(2, restaurantID);
			statement.setInt(3, recentReview.getReviewNumber());
			statement.setString(4, recentReview.getComment());
			statement.setInt(5, recentReview.getStarsGiven());
			statement.executeUpdate();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public Connection getConnection() {
		String connectionUrl = "jdbc:mysql://localhost/muneerfirsttable";
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			connection = DriverManager.getConnection(connectionUrl, "root", "root");
		}
		catch(InstantiationException e ) {
			e.printStackTrace();
		}
		catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
	
}
