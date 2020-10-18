# SuperRent
> SuperRent is a simple database system for a car rental campany using Java/Oracle. It can be used to track transactions performed by both customers and employees of the company, and generate various rental and return reports.  

## Table of contents
* [Technologies](#technologies)
* [DatabaseTables](#databasetables)
* [Setup](#setup)
* [Features](#features)

## Technologies
* Java
* SQL
* Oracle

## DatabaseTables
* Reservations
* Rentals
* Vehicles
* VehicleTypes
* Customers
* Returns

## Setup
Setting up JDBC: https://www.students.cs.ubc.ca/~cs-304/resources/jdbc-oracle-resources/jdbc-java-setup.html

The script to create database tables is located at ```/sql scripts/project_create_tables.sql```. The script to populate the tables is located at ```/sql scripts/project_insert_rows.sql```. Please transfer these files to the directory where you will run SQLPlus. Run the following commands in SQLPlus to load data for the project: 
```
start project_create_tables.sql
start project_insert_row.sql
```

Execute ```/src/ca/ubc/cs304/controller/SuperRent.java``` to start the program, a text-based user interface will be available. 


## Code Examples
Once a vehicle is returned, update the status of a vehicle to available and note the odometer reading: 
```	java
private void updateVehicle(String vlicense, int odometer) {
	try {
		PreparedStatement ps = connection.prepareStatement("UPDATE Vehicle SET status = 'available', odometer = ? WHERE vlicense = ?");
		ps.setInt(1, odometer);
		ps.setString(2, vlicense);
		int rowCount = ps.executeUpdate();
		if (rowCount == 0) {
			System.out.println(WARNING_TAG + " Vehicle " + vlicense + " does not exist!");
		}
		connection.commit();
		ps.close();
	 } catch (SQLException e) {
		System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		rollbackConnection();
	}
}
```

## Features
The project supports the following actions:
* Transactions performed by a customer:    
• **View the number of available vehicles** for a specific car type, location, and time interval.   
• **Make a reservation**. If a customer is new, add the customer’s details to the database. Upon successful completion, a confirmation number for the reservation should be shown along with the details entered during the reservation. 

* Transactions performed by an employee of the car rental company:   
• **Renting a Vehicle**: The system will display a receipt with the necessary details (e.g., confirmation number, date of reservation, type of car, location, how long the rental period lasts for, etc.) for the customer.  
• **Returning a Vehicle**: Only a rented vehicle can be returned. Trying to return a vehicle that has not been rented should generate some type of error message for the clerk. When returning a vehicle, the system will display a receipt with the necessary details (e.g., reservation confirmation number, date of return, how the total was calculated etc.) for the customer.

* Generate a report for:  
• **Daily Rentals**: This report contains information on all the vehicles rented out during the day. The entries are grouped by branch, and within each branch, the entries are grouped by vehicle category. The report also displays the number of vehicles rented per category (e.g., 5 sedan rentals, 2 SUV rentals, etc.), the number of rentals at each branch, and the total number of new rentals across the whole company.   
• **Daily Rentals for Branch**: This is the same as the Daily Rental report but it is for one specified branch.  
• **Daily Returns**: The report contains information on all the vehicles returned during the day. The entries are grouped by branch, and within each branch, the entries are grouped by vehicle category. The report also shows the number of vehicles returned per category, the revenue per category, subtotals for the number of vehicles and revenue per branch, and the grand totals for the day.  
• **Daily Returns** for Branch: This is the same as the Daily Returns report, but it is for one specified branch.

