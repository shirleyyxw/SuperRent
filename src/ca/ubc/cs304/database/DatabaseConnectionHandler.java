package ca.ubc.cs304.database;

import ca.ubc.cs304.model.BranchModel;

import java.sql.*;
import java.util.ArrayList;

/**
 * This class handles all database related transactions
 */
public class DatabaseConnectionHandler {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	
	private Connection connection = null;
	
	public DatabaseConnectionHandler() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
	
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public ArrayList<ArrayList<String>> generateRentalsReport(String reportDate){
		String FROM_WHERE = "FROM Rental, Vehicle WHERE Rental.vlicense = Vehicle.vlicense AND TRUNC(fromDate) = TO_DATE(?, 'DD-MON-YYYY')";
		return generateReport(reportDate, null, null, FROM_WHERE, "rentals");
	}

	public ArrayList<ArrayList<String>> generateRentalsBranchReport(String reportDate, String location, String city){
		String FROM_WHERE = "FROM Rental, Vehicle WHERE Rental.vlicense = Vehicle.vlicense AND TRUNC(fromDate) = TO_DATE(?, 'DD-MON-YYYY') AND location = ? AND city = ?";
		if (!isThereBranch(location, city)) {
			return null;
		} else {
			return generateReport(reportDate, location, city, FROM_WHERE, "rentals");
		}
	}

	public ArrayList<ArrayList<String>> generateReturnsReport(String reportDate){
		String FROM_WHERE = "FROM Rental, Return, Vehicle WHERE Rental.vlicense = Vehicle.vlicense AND Rental.rid = Return.rid AND TRUNC(returnTime) = TO_DATE(?, 'DD-MON-YYYY')";
		return generateReport(reportDate, null, null, FROM_WHERE, "returns");
	}
	public ArrayList<ArrayList<String>> generateReturnsBranchReport(String reportDate, String location, String city){
		String FROM_WHERE = "FROM Rental, Return, Vehicle WHERE Rental.vlicense = Vehicle.vlicense AND Rental.rid = Return.rid AND TRUNC(returnTime) = TO_DATE(?, 'DD-MON-YYYY') AND location = ? AND city = ?";
		if (!isThereBranch(location, city)) {
			return null;
		} else {
			return generateReport(reportDate, location, city, FROM_WHERE, "returns");
		}
	}

	// helper method for generating all reports
	// assume location and city are either both null or refers to a valid branch
	//        reportName is either "returns" or "rentals"
	private ArrayList<ArrayList<String>> generateReport(String reportDate, String location, String city, String FROM_WHERE, String reportName) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		boolean isForBranch = false;
		if (location != null && city != null) {
			isForBranch = true;
		}
		String totalRevenue = " ";
		if (reportName == "returns") {
			totalRevenue = ", SUM(payment) AS revenue ";
		}
		String allVehicles = "SELECT Vehicle.vlicense, make, model, productionYear, color, Vehicle.odometer, status, vtname, location, city " + FROM_WHERE;
		String perCategory = "SELECT vtname, COUNT(*) AS numOfVehicles" + totalRevenue + FROM_WHERE + " GROUP BY vtname";
		String totalNum = "SELECT Count(*) AS numOfVehicles " + totalRevenue + FROM_WHERE;
		if (isForBranch) {
			allVehicles += " ORDER BY vtname";
		} else {
			allVehicles += " ORDER BY city, location, vtname";
		}
		try {
			PreparedStatement psVehicles = connection.prepareStatement(allVehicles);
			PreparedStatement psCat = connection.prepareStatement(perCategory);
			PreparedStatement psTotal = connection.prepareStatement(totalNum);

			psVehicles.setString(1, reportDate);
			psCat.setString(1, reportDate);
			psTotal.setString(1, reportDate);
			if(isForBranch) {
				psVehicles.setString(2, location);
				psVehicles.setString(3, city);
				psCat.setString(2, location);
				psCat.setString(3, city);
				psTotal.setString(2, location);
				psTotal.setString(3, city);
			}

			ResultSet rsVehicles = psVehicles.executeQuery();
			System.out.print("1");
			ResultSet rsCat = psCat.executeQuery();
			System.out.print("2");
			ResultSet rsTotal = psTotal.executeQuery();
			System.out.print("3");

			// get info on ResultSet
			ResultSetMetaData rsmdVehicles = rsVehicles.getMetaData();
			ResultSetMetaData rsmdCat = rsCat.getMetaData();
			ResultSetMetaData rsmdTotal = rsTotal.getMetaData();

			ArrayList<String> spacing = new ArrayList<>();
			spacing.add(" ");

			String verb;
			if (reportName.equals("rentals")) {
				verb = "rented";
			} else {
				verb = "returned";
			}

			// for rsVehicles
			ArrayList<String> line1 = new ArrayList<>();
			String message = "";
			message += "Vehicles " + verb + " on: " + reportDate;
			if (isForBranch) {
				message += " for " + city + " " + location;
			}
			line1.add(message);
			result.add(line1);
			result.add(getColumnNames(rsmdVehicles));
			while(rsVehicles.next()) {
				ArrayList<String> vehicleRow = new ArrayList<>();
				vehicleRow.add(rsVehicles.getString("vlicense"));
				if (rsVehicles.getString("make") == null) {
					vehicleRow.add("null");
				} else {
					vehicleRow.add(rsVehicles.getString("make"));
				}
				if (rsVehicles.getString("model") == null) {
					vehicleRow.add("null");
				} else {
					vehicleRow.add(rsVehicles.getString("model"));
				}
				if (rsVehicles.getInt("productionYear") == 0) {
					vehicleRow.add("null");
				} else {
					vehicleRow.add(Integer.toString(rsVehicles.getInt("productionYear")));
				}
				if (rsVehicles.getString("color") == null) {
					vehicleRow.add("null");
				} else {
					vehicleRow.add(rsVehicles.getString("color"));
				}
				if (rsVehicles.getInt("odometer") == 0) {
					vehicleRow.add("null");
				} else {
					vehicleRow.add(Integer.toString(rsVehicles.getInt("odometer")));
				}
				vehicleRow.add(rsVehicles.getString("status"));
				vehicleRow.add(rsVehicles.getString("vtname"));
				vehicleRow.add(rsVehicles.getString("location"));
				vehicleRow.add(rsVehicles.getString("city"));
				result.add(vehicleRow);
			}

			// for rsCat
			ArrayList<String> line2 = new ArrayList<>();
			message = "Number of Vehicles " + verb + " per category on: " + reportDate;
			if (isForBranch) {
				message += " for " + city + " " + location;
			}
			line2.add(message);
			result.add(spacing);
			result.add(line2);
			result.add(getColumnNames(rsmdCat));
			while(rsCat.next()) {
				ArrayList<String> row = new ArrayList<>();
				row.add(rsCat.getString("vtname"));
				row.add(Integer.toString(rsCat.getInt("numOfVehicles")));
				if (reportName.equals("returns")) {
					row.add(Float.toString(rsCat.getFloat("revenue")));
				}
				result.add(row);
			}

			// for rsBranch
			if (!isForBranch) {
				String perBranch = "SELECT city, location, COUNT(*) AS numOfVehicles " + totalRevenue + FROM_WHERE + " GROUP BY city, location";
				PreparedStatement psBranch = connection.prepareStatement(perBranch);
				psBranch.setString(1, reportDate);
				ResultSet rsBranch = psBranch.executeQuery();
				ResultSetMetaData rsmdBranch = rsBranch.getMetaData();
				ArrayList<String> line3 = new ArrayList<>();
				line3.add("Number of Vehicles " + verb + " for each branch on: " + reportDate);
				result.add(spacing);
				result.add(line3);
				result.add(getColumnNames(rsmdBranch));
				while(rsBranch.next()) {
					ArrayList<String> row = new ArrayList<>();
					row.add(rsBranch.getString("city"));
					row.add(rsBranch.getString("location"));
					row.add(Integer.toString(rsBranch.getInt("numOfVehicles")));
					if (reportName.equals("returns")) {
						row.add(Float.toString(rsBranch.getFloat("revenue")));
					}
					result.add(row);
				}
				rsBranch.close();
				psBranch.close();
			}

			// for rsTotal
			ArrayList<String> line4 = new ArrayList<>();
			message = "Total number of Vehicles " + verb + " on: " + reportDate;
			if (isForBranch) {
				message += " for " + city + " " + location;
			}
			line4.add(message);
			result.add(spacing);
			result.add(line4);
			result.add(getColumnNames(rsmdTotal));
			while(rsTotal.next()) {
				ArrayList<String> row = new ArrayList<>();
				row.add(Integer.toString(rsTotal.getInt("numOfVehicles")));
				if (reportName.equals("returns")) {
					row.add(Float.toString(rsTotal.getFloat("revenue")));
				}
				result.add(row);
			}

			rsVehicles.close();
			psVehicles.close();
			rsCat.close();
			psCat.close();
			rsTotal.close();
			psTotal.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return result;
	}

	// helper method for getting the column names from ResultSetMetaData of ResultSet
	// returns an arrayList of column names
	private ArrayList<String> getColumnNames(ResultSetMetaData rsmd) {
		ArrayList<String> columnRow = new ArrayList<>();
		try {
			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				columnRow.add(rsmd.getColumnName(i + 1));
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return columnRow;
	}

	// helper method that checks whether the given location and city are existing branch
	// return true if branch exists, otherwise return false
	private boolean isThereBranch(String location, String city) {
		boolean result = false;
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) AS num FROM Vehicle WHERE location = ? AND city = ?");
			ps.setString(1, location);
			ps.setString(2, city);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				if (rs.getInt("num") > 0) {
					result = true;
				}
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return result;
	}

	public void deleteBranch(int branchId) {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM branch WHERE branch_id = ?");
			ps.setInt(1, branchId);
			
			int rowCount = ps.executeUpdate();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Branch " + branchId + " does not exist!");
			}
			
			connection.commit();
	
			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public void insertBranch(BranchModel model) {
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?)");
			ps.setInt(1, model.getId());
			ps.setString(2, model.getName());
			ps.setString(3, model.getAddress());
			ps.setString(4, model.getCity());
			if (model.getPhoneNumber() == 0) {
				ps.setNull(5, java.sql.Types.INTEGER);
			} else {
				ps.setInt(5, model.getPhoneNumber());
			}

			ps.executeUpdate();
			connection.commit();

			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}
	
	public BranchModel[] getBranchInfo() {
		ArrayList<BranchModel> result = new ArrayList<BranchModel>();
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM branch");
		
//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}
			while(rs.next()) {
				BranchModel model = new BranchModel(rs.getString("branch_addr"),
													rs.getString("branch_city"),
													rs.getInt("branch_id"),
													rs.getString("branch_name"),
													rs.getInt("branch_phone"));
				result.add(model);
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}	
		
		return result.toArray(new BranchModel[result.size()]);
	}
	
	public void updateBranch(int id, String name) {
		try {
		  PreparedStatement ps = connection.prepareStatement("UPDATE branch SET branch_name = ? WHERE branch_id = ?");
		  ps.setString(1, name);
		  ps.setInt(2, id);
		
		  int rowCount = ps.executeUpdate();
		  if (rowCount == 0) {
		      System.out.println(WARNING_TAG + " Branch " + id + " does not exist!");
		  }
	
		  connection.commit();
		  
		  ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}	
	}
	
	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}
	
			connection = DriverManager.getConnection(ORACLE_URL, username, password);
			connection.setAutoCommit(false);
	
			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}

	private void rollbackConnection() {
		try  {
			connection.rollback();	
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}
}
