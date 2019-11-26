package ca.ubc.cs304.database;

import ca.ubc.cs304.model.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

	public ArrayList<ArrayList<String>> returnVehicle(String vlicense, int afterOdometer, boolean fulltank, String currentDateStr, Date currentDate) {
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		try {
			String allReturnRid = "(SELECT rid FROM Return)";
			PreparedStatement ps1 = connection.prepareStatement("SELECT Rental.rid, fromDate, confno FROM Rental WHERE vlicense = ? AND fromDate < TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS') AND Rental.rid NOT IN " + allReturnRid,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps1.setString(1, vlicense);
			ps1.setString(2, currentDateStr);
			ResultSet rs1 = ps1.executeQuery();

			// check error
			int rowcount = 0;
			if (rs1.last()) {
				rowcount = rs1.getRow();
				rs1.beforeFirst();
			}
			if (rowcount == 0) {
				// no such vehicle
				ArrayList<String> temp = new ArrayList<>();
				temp.add("ERROR: This vehicle with license " + vlicense + " is either not rented or returned already.");
				result.add(temp);
				return result;
			} else if (rowcount > 1) {
				ArrayList<String> temp = new ArrayList<>();
				temp.add("ERROR: There should not be more than one rental with same vlicense that has not returned.");
				result.add(temp);
				return result;
			}

			int rid = 0;
			Timestamp fromDate = null;
			String fromDateStr = "";
			int confNo = 0;
			while (rs1.next()) {
				rid = rs1.getInt("rid");
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-YYYY HH:mm:ss");
				formatter.setLenient(false);
				fromDate = rs1.getTimestamp("fromDate");
				fromDateStr = formatter.format(fromDate);
				confNo = rs1.getInt("confNo");
			}
			String vtname = getVtname(confNo);

			ArrayList<String> spacing = new ArrayList<>();
			spacing.add(" ");
			ArrayList<String> title = new ArrayList<>();
			title.add("Receipt for returning: ");
			result.add(spacing);
			result.add(title);
			result.add(spacing);
			ArrayList<String> columnRow1 = new ArrayList<>();
			ArrayList<String> dataRow1 = new ArrayList<>();
			columnRow1.add("Confirmation number");
			dataRow1.add(Integer.toString(confNo));
			columnRow1.add("Rental id");
			dataRow1.add(Integer.toString(rid));
			columnRow1.add("Rental date");
			dataRow1.add(fromDateStr);
			columnRow1.add("Return date");
			dataRow1.add(currentDateStr);
			columnRow1.add("Vehicle Type");
			dataRow1.add(vtname);
			result.add(columnRow1);
			result.add(dataRow1);

			PreparedStatement ps2 = connection.prepareStatement("SELECT wrate, drate, hrate, wirate, dirate, hirate FROM VehicleType WHERE vtname = ?");
			ps2.setString(1, vtname);
			ResultSet rs2 = ps2.executeQuery();
			int wrate = 0;
			int drate = 0;
			int hrate = 0;
			int wirate = 0;
			int dirate = 0;
			int hirate = 0;
			while(rs2.next()) {
				wrate = rs2.getInt("wrate");
				drate = rs2.getInt("drate");
				hrate = rs2.getInt("hrate");
				wirate = rs2.getInt("wirate");
				dirate = rs2.getInt("dirate");
				hirate = rs2.getInt("hirate");
			}

			ArrayList<String> title2 = new ArrayList<>();
			title2.add("Rates: ");
			result.add(spacing);
			result.add(title2);
			result.add(spacing);
			ArrayList<String> columnRow2 = new ArrayList<>();
			ArrayList<String> dataRow2 = new ArrayList<>();
			columnRow2.add("Weekly rate");
			columnRow2.add("Daily rate");
			columnRow2.add("Hourly rate");
			columnRow2.add("Weekly insurance rate");
			columnRow2.add("Daily insurance rate");
			columnRow2.add("Hourly insurance rate");
			dataRow2.add(Integer.toString(wrate));
			dataRow2.add(Integer.toString(drate));
			dataRow2.add(Integer.toString(hrate));
			dataRow2.add(Integer.toString(wirate));
			dataRow2.add(Integer.toString(dirate));
			dataRow2.add(Integer.toString(hirate));
			result.add(columnRow2);
			result.add(dataRow2);

			long diffMs = (currentDate.getTime() - fromDate.getTime());
			long diffWeek = (diffMs / (1000 * 60 * 60 * 24 * 7));
			long diffDay = (diffMs / (1000 * 60 * 60 * 24) % 7);
			long diffHour = (diffMs / (1000 * 60 * 60) % 24);
			ArrayList<String> costTitleRow = new ArrayList<>();
			costTitleRow.add("Total cost for: " + diffWeek + " weeks " + diffDay + " days " + diffHour + " hours");
			result.add(spacing);
			result.add(costTitleRow);
			result.add(spacing);
			long wcost = (wrate + wirate) * diffWeek;
			long dcost = (drate + dirate) * diffDay;
			long hcost = (hrate + hirate) * diffHour;
			int totalCost = (int) (wcost + dcost + hcost);
			ArrayList<String> weekCostRow = new ArrayList<>();
			weekCostRow.add("Weekly rate applied cost: " + wcost);
			ArrayList<String> dayCostRow = new ArrayList<>();
			dayCostRow.add("Daily rate applied cost: " + dcost);
			ArrayList<String> hourCostRow = new ArrayList<>();
			hourCostRow.add("Hourly rate applied cost: " + hcost);
			ArrayList<String> totalCostRow = new ArrayList<>();
			totalCostRow.add("totalCost: " + totalCost);
			result.add(weekCostRow);
			result.add(dayCostRow);
			result.add(hourCostRow);
			result.add(totalCostRow);

			insertReturn(rid, currentDate, afterOdometer, fulltank, totalCost);
			updateVehicle(vlicense, afterOdometer);
			ps1.close();
			rs1.close();
			ps2.close();
			rs2.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void insertReturn(int rid, Date returnTime, int odometer, boolean fulltank, int payment) {
		try {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO Return VALUES (?,?,?,?,?)");
			ps.setInt(1, rid);
			ps.setDate(2, new java.sql.Date(returnTime.getTime()));
			ps.setInt(3, odometer);
			ps.setString(4, Boolean.toString(fulltank));
			ps.setInt(5, payment);

			ps.executeUpdate();
			connection.commit();

			ps.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
		}
	}

	private String getVtname(int confNo) {
		String vtname = "";
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT vtname FROM Reservation WHERE confNo = ?");
			ps.setInt(1, confNo);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				vtname = rs.getString("vtname");
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return vtname;
	}

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

	public ArrayList<ArrayList<String>> generateRentalsReport(String reportDate){
		String FROM_WHERE = "FROM Rental, Vehicle WHERE Rental.vlicense = Vehicle.vlicense AND TRUNC(fromDate) = TO_DATE(?, 'DD-MON-YYYY')";
		return generateReport(reportDate, null, null, FROM_WHERE, "rentals");
	}

	public ArrayList<ArrayList<String>> generateRentalsBranchReport(String reportDate, String location, String city){
		String FROM_WHERE = "FROM Rental, Vehicle WHERE Rental.vlicense = Vehicle.vlicense AND TRUNC(fromDate) = TO_DATE(?, 'DD-MON-YYYY') AND location = ? AND city = ?";
		if (!isThereBranch(location, city)) {
			ArrayList<ArrayList<String>> result = new ArrayList<>();
			ArrayList<String> temp = new ArrayList<>();
			temp.add("ERROR: The entered branch does not exist.");
			result.add(temp);
			return result;
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
			ArrayList<ArrayList<String>> result = new ArrayList<>();
			ArrayList<String> temp = new ArrayList<>();
			temp.add("ERROR: The entered branch does not exist.");
			result.add(temp);
			return result;
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
		if (reportName.equals("returns")) {
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
			ResultSet rsCat = psCat.executeQuery();
			ResultSet rsTotal = psTotal.executeQuery();

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

    //This is for viewing the number of available vehicles
    public int countAvailableVehicles(String vtname, String location, String city, String startTime) {
        String sqlQuery = getCountAvailableVehiclesQuery(vtname, location, city, startTime);
        int result = -1;
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            rs.next();
            result = rs.getInt(1);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    //After viewing the number of available vehicles, customer has the option to display details of available vehicles
    public VehicleModel[] displayAvailableVehicles(String vtname, String location, String city, String startTime) {
        ArrayList<VehicleModel> result = new ArrayList<VehicleModel>();
        String sqlQuery = getCountAvailableVehiclesQuery(vtname, location, city, startTime);
        //Instead of the number of available vehicles, we want the details of available vehicles
        sqlQuery = sqlQuery.replace("COUNT(*)", "*");

        try{
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);

            while(rs.next()) {
                VehicleModel model = new VehicleModel(rs.getString("vlicense"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("productionYear"),
                        rs.getString("color"),
                        rs.getInt("odometer"),
                        rs.getString("status"),
                        rs.getString("vtname"),
                        rs.getString("location"),
                        rs.getString("city"));
                result.add(model);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result.toArray(new VehicleModel[result.size()]);
    }


    // Helper function to form the SQL query for counting available vehicles
    private String getCountAvailableVehiclesQuery(String vtname, String location, String city, String startTime) {
        String sqlQuery;
        if (startTime == null) {
            sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE status = 'available'";
            if (vtname != null) {
                vtname = "\'" + vtname + '\'';
                sqlQuery = sqlQuery + " AND vtname=" + vtname;
            }
            if (location != null) {
                location = '\'' + location + '\'';
                sqlQuery = sqlQuery + " AND location=" + location;
            } if (city != null) {
                city = '\'' + city + '\'';
                sqlQuery = sqlQuery + " AND city=" + city;
            }
        } else {
            startTime = "TO_DATE(" + '\'' + startTime + '\'' + ',' + '\'' + "DD-MON-YYYY HH24:MI:SS" + '\'' +')';
            // if the customer provides a startTime, find vehicles that are returned before the startTime
            if (vtname != null && location == null && city == null) {
                vtname = '\'' + vtname + '\'';
                sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE (status = 'available' AND vtname=" + vtname + ")" + "OR (vtname=" + vtname + " AND status='rented' AND vlicense IN (SELECT vlicense FROM Rental WHERE toDate<" + startTime + "))";
            }
            else if (vtname != null && location != null && city == null) {
                vtname = '\'' + vtname + '\'';
                location = '\'' + location + '\'';
                sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE (status = 'available' AND vtname=" + vtname + " AND location=" + location + ")" + "OR (vtname=" + vtname + " AND location=" + location + " AND status='rented' AND vlicense IN (SELECT vlicense FROM Rental WHERE toDate<" + startTime + "))";
            }
            else if (vtname != null && location != null && city != null) {
                vtname = '\'' + vtname + '\'';
                location = '\'' + location + '\'';
                city = '\'' + city + '\'';
                sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE (status = 'available' AND vtname=" + vtname + " AND location=" + location + " AND city=" + city + ")" + "OR (vtname=" + vtname + " AND location=" + location + " AND city=" + city + " AND status='rented' AND vlicense IN (SELECT vlicense FROM Rental WHERE toDate<" + startTime + "))";
            }
            else if (vtname == null && location != null && city == null) {
                location = '\'' + location + '\'';
                sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE (status = 'available' AND location=" + location + ")" + "OR (location=" + location + " AND status='rented' AND vlicense IN (SELECT vlicense FROM Rental WHERE toDate<" + startTime + "))";
            }
            else if (vtname == null && location != null && city != null) {
                location = '\'' + location + '\'';
                city = '\'' + city + '\'';
                sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE (status = 'available' AND location=" + location + " AND city=" + city + ")" + "OR (location=" + location + " AND city=" + city + " AND status='rented' AND vlicense IN (SELECT vlicense FROM Rental WHERE toDate<" + startTime + "))";
            }
            else if (vtname == null && location == null && city != null) {
                city = '\'' + city + '\'';
                sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE (status = 'available' AND city=" + city + ")" + "OR (city=" + city + " AND status='rented' AND vlicense IN (SELECT vlicense FROM Rental WHERE toDate<" + startTime + "))";
            } else {
                vtname = '\'' + vtname + '\'';
                city = '\'' + city + '\'';
                sqlQuery = "SELECT COUNT(*) FROM Vehicle WHERE (status = 'available' AND vtname=" + vtname + " AND city=" + city + ")" + "OR (vtname=" + vtname + " AND city=" + city + " AND status='rented' AND vlicense IN (SELECT vlicense FROM Rental WHERE toDate<" + startTime + "))";
            }
        }
        return sqlQuery;
    }

    // Given the dlicense, check if customer is in the database, return 0 if the no customer with the given dlicense is found in database
    public int findCustomer(int dlicense){
	    int result = -1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM Customer WHERE dlicense = ?");
            ps.setInt(1, dlicense);
            ResultSet rs = ps.executeQuery();
            rs.next();
            result = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    // Insert new customer tuple into Customer table
    public void addNewCustomer(CustomerModel model){
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");
            ps.setInt(1, model.getDlicense());
            ps.setString(2, model.getCellphone());
            ps.setString(3, model.getName());
            ps.setString(4, model.getAddress());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public ReservationModel reserveVehicle(String vtname, int dlicense, String fromDate, String toDate){
	    int confNo = generateConfNoCounter();
	    if (confNo == -1) {
            System.out.println("Error in generating confirmation number.");
        }
	    ReservationModel model = new ReservationModel(confNo, vtname, dlicense, fromDate, toDate);
        try{
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Reservation VALUES (?,?,?,TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'))");
            ps.setInt(1, model.getConfNo());
            ps.setString(2,model.getVtname());
            ps.setInt(3, model.getDlicense());
            ps.setString(4, model.getFromDate());
            ps.setString(5,model.getToDate());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
        return model;
    }

    private int generateConfNoCounter(){
        int counter = -1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT MAX(confNo) AS counter FROM Reservation");
            ResultSet rs = ps.executeQuery();
            rs.next();
            counter = rs.getInt("counter");
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage() + "1");
        }
        return ++counter;
    }

    private int generateRidCounter(){
        int rid = -1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT MAX(rid) AS counter FROM Rental");
            ResultSet rs = ps.executeQuery();
            rs.next();
            rid = rs.getInt("counter");
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage() + "1");
        }
        return ++rid;
    }


    public int findConfNo(int confNo){
        int result = -1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM Reservation WHERE confNo = ?");
            ps.setInt(1, confNo);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    public int findDlicense(int confNo){
        int result = -1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT dlicense FROM Reservation WHERE confNo = ?");
            ps.setInt(1, confNo);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    public String findVtName(int confNo) {
        String result = "";
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT vtname FROM Reservation WHERE confNo = ?");
            ps.setInt(1, confNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return result;
    }

    public RentalModel handleRent(String vlicense, int dlicense, int odometer, String cardName, int cardNo, String expDate, String fromDate, String toDate, int confNo){
        int rid = generateRidCounter();
        if (rid == -1) {
            System.out.println("Error in generating Rid.");
        }

        RentalModel model = new RentalModel(rid, vlicense, dlicense, odometer, cardName, cardNo, expDate, fromDate, toDate, confNo);
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Rental VALUES (?,?,?,?,?,?,TO_DATE(?, 'DD-MON-YYYY'), TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'),TO_DATE(?, 'DD-MON-YYYY HH24:MI:SS'), ?)");

            ps.setInt(1, model.getRid());
            ps.setString(2, model.getVlicense());
            ps.setInt(3, model.getDlicense());
            ps.setInt(4, model.getOdometer());
            ps.setString(5, model.getCardName());
            ps.setInt(6, model.getCardNo());
            ps.setString(7, model.getExpDate());
            ps.setString(8, model.getFromDate());
            ps.setString(9,model.getToDate());
            ps.setInt(10, model.getConfNo());

            ps.executeUpdate();
            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
        return model;

    }

    public void updateVehicleStatus(String vlicense){
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE Vehicle SET status = 'rented' WHERE vlicense = ?");
            ps.setString(1, vlicense);

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
