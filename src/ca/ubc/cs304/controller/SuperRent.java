package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.ReservationModel;
import ca.ubc.cs304.model.VehicleModel;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.TerminalTransactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * This is the main controller class that will orchestrate everything.
 */
public class SuperRent implements LoginWindowDelegate, TerminalTransactionsDelegate {
    private DatabaseConnectionHandler dbHandler = null;
    private LoginWindow loginWindow = null;

    public SuperRent() {
        dbHandler = new DatabaseConnectionHandler();
    }

    private void start() {
        loginWindow = new LoginWindow();
        loginWindow.showFrame(this);
    }

    /**
     * LoginWindowDelegate Implementation
     *
     * connects to Oracle database with supplied username and password
     */
    public void login(String username, String password) {
        boolean didConnect = dbHandler.login(username, password);

        if (didConnect) {
            // Once connected, remove login window and start text transaction flow
            loginWindow.dispose();

            TerminalTransactions transaction = new TerminalTransactions();
            transaction.showMainMenu(this);
        } else {
            loginWindow.handleLoginFailed();

			if (loginWindow.hasReachedMaxLoginAttempts()) {
				loginWindow.dispose();
				System.out.println("You have exceeded your number of allowed attempts");
				System.exit(-1);
			}
		}
	}

	public void returnVehicle(String vlicense, int odometer, boolean fulltank, String currentDateStr, Date currentDate) {
		print(dbHandler.returnVehicle(vlicense, odometer, fulltank, currentDateStr, currentDate), null);
	}

	public void generateRentalsReport(String date){
		print(dbHandler.generateRentalsReport(date), "Daily Rentals Report:");
	}

	public void generateRentalsBranchReport(String date, String location, String city){
		print(dbHandler.generateRentalsBranchReport(date, location, city), "Daily Rentals for Branch Report:");
	}

	public void generateReturnsReport(String date){
		print(dbHandler.generateReturnsReport(date), "Daily Returns Report:");
	}

	public void generateReturnsBranchReport(String date, String location, String city){
		print(dbHandler.generateReturnsBranchReport(date, location, city), "Daily Returns for Branch Report:");
	}

	// helper method for printing
	private void print(ArrayList<ArrayList<String>> result, String reportName) {
        System.out.println();
        if (reportName != null) {
			System.out.print(reportName);
		}
		for(int i = 0; i < result.size(); i++) {
			System.out.println();
			for(int j = 0; j < result.get(i).size(); j++) {
				System.out.printf("%-25s", result.get(i).get(j));
			}
		}
        if (result == null) {
            System.out.print("Error: The entered branch does not exist.");
        } else {
            System.out.print(reportName);
            for(int i = 0; i < result.size(); i++) {
                System.out.println();
                for(int j = 0; j < result.get(i).size(); j++) {
                    System.out.printf("%-25s", result.get(i).get(j));
                }
            }
        }
        System.out.println();
    }


    public int countAvailableVehicles(String vtname, String location, String city, String startTime){
        int numAvailable = dbHandler.countAvailableVehicles(vtname, location, city, startTime);
        if (numAvailable == -1){
            System.out.println("\nSystem error occurs when searching.\n");
        } else if (numAvailable == 0) {
            System.out.println("\nThere are no available vehicles that match this search.\n");
        } else {
            System.out.println("\nThere are " + numAvailable + " available vehicles that match this search.\n");
        }
        return numAvailable;
    }

    public void displayAvailableVehicles(String vtname, String location, String city, String startTime) {
        VehicleModel[] vehicles = dbHandler.displayAvailableVehicles(vtname, location, city, startTime);
        ArrayList<String> vehicleAtrributes = new ArrayList<>(Arrays.asList("VLICENSE", "MAKE", "MODEL", "PRODUCTIONYEAR", "COLOR", "ODOMETER", "STATUS", "VTNAME", "LOCATION", "CITY"));

        System.out.println();
        for (int i = 0; i < vehicleAtrributes.size(); i++) {
            if (vehicleAtrributes.get(i).equals("LOCATION")){
                System.out.printf("%-40s", vehicleAtrributes.get(i));
            } else {
                System.out.printf("%-20s", vehicleAtrributes.get(i));
            }
        }

        System.out.print("\n");

        for (int i = 0; i < vehicles.length; i++) {
            VehicleModel model = vehicles[i];

            System.out.printf("%-20s", model.getVlicense());
            System.out.printf("%-20s", model.getMake());
            System.out.printf("%-20s", model.getModel());
            if (model.getYear() == 0){
                System.out.printf("%-20s", null);
            } else {
                System.out.printf("%-20s", model.getYear());
            }
            System.out.printf("%-20s", model.getColor());
            System.out.printf("%-20s", model.getOdometer());
            System.out.printf("%-20s", model.getStatus());
            System.out.printf("%-20s", model.getVtname());
            System.out.printf("%-40s", model.getLocation());
            System.out.printf("%-20s", model.getCity());
            System.out.print("\n");

        }
        System.out.println();
    }

    public int findCustomer(int dlicense){
        return dbHandler.findCustomer(dlicense);
    }

    public void addNewCustomer(CustomerModel model){
        dbHandler.addNewCustomer(model);
        System.out.println();
        System.out.println("You have successfully registered. The following is your information for this registration.");
        ArrayList<String> customerAtrributes = new ArrayList<>(Arrays.asList("DLICENSE", "CELLPHONE", "NAME", "ADDRESS"));
        System.out.println();
        for (int i = 0; i < customerAtrributes.size(); i++) {
            System.out.printf("%-20s", customerAtrributes.get(i));
        }
        System.out.print("\n");

        System.out.printf("%-20s", model.getDlicense());
        System.out.printf("%-20s", model.getCellphone());
        System.out.printf("%-20s", model.getName());
        System.out.printf("%-20s", model.getAddress());

        System.out.println();
    }

    public void reserveVehicle(String vtname, int dlicense, String fromDate, String toDate, String location, String city) {
        ReservationModel model = dbHandler.reserveVehicle(vtname, dlicense, fromDate, toDate);
        System.out.println();
        System.out.println("Your reservation is successful. The following is the information for this reservation at " + location + ", " + city);
        ArrayList<String> reservationAtrributes = new ArrayList<>(Arrays.asList("CONFNO", "VTNAME", "DLICENSE", "FROMDATE", "TODATE"));
        System.out.println();
        for (int i = 0; i < reservationAtrributes.size(); i++) {
            System.out.printf("%-25s", reservationAtrributes.get(i));
        }

        System.out.print("\n");
        System.out.printf("%-25s", model.getConfNo());
        System.out.printf("%-25s", model.getVtname());
        System.out.printf("%-25s", model.getDlicense());
        System.out.printf("%-25s", model.getFromDate());
        System.out.printf("%-25s", model.getToDate());

        System.out.println();
    }

    /**
     * TermainalTransactionsDelegate Implementation
     *
     * Insert a branch with the given info
     */
    public void insertBranch(BranchModel model) {
        dbHandler.insertBranch(model);
    }

    /**
     * TermainalTransactionsDelegate Implementation
     *
     * Delete branch with given branch ID.
     */
    public void deleteBranch(int branchId) {
        dbHandler.deleteBranch(branchId);
    }

    /**
     * TermainalTransactionsDelegate Implementation
     *
     * Update the branch name for a specific ID
     */

    public void updateBranch(int branchId, String name) {
        dbHandler.updateBranch(branchId, name);
    }

    /**
     * TermainalTransactionsDelegate Implementation
     *
     * Displays information about varies bank branches.
     */
    public void showBranch() {
        BranchModel[] models = dbHandler.getBranchInfo();

        for (int i = 0; i < models.length; i++) {
            BranchModel model = models[i];

            // simplified output formatting; truncation may occur
            System.out.printf("%-10.10s", model.getId());
            System.out.printf("%-20.20s", model.getName());
            if (model.getAddress() == null) {
                System.out.printf("%-20.20s", " ");
            } else {
                System.out.printf("%-20.20s", model.getAddress());
            }
            System.out.printf("%-15.15s", model.getCity());
            if (model.getPhoneNumber() == 0) {
                System.out.printf("%-15.15s", " ");
            } else {
                System.out.printf("%-15.15s", model.getPhoneNumber());
            }

            System.out.println();
        }
    }



    /**
     * TerminalTransactionsDelegate Implementation
     *
     * The TerminalTransaction instance tells us that it is done with what it's
     * doing so we are cleaning up the connection since it's no longer needed.
     */
    public void terminalTransactionsFinished() {
        dbHandler.close();
        dbHandler = null;

        System.exit(0);
    }

    /**
     * Main method called at launch time
     */
    public static void main(String args[]) {
        SuperRent superRent = new SuperRent();
        superRent.start();
    }
}
