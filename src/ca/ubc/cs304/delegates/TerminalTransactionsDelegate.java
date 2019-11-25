package ca.ubc.cs304.delegates;

import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.VehicleModel;

import java.util.Date;

/**
 * This interface uses the delegation design pattern where instead of having
 * the TerminalTransactions class try to do everything, it will only
 * focus on handling the UI. The actual logic/operation will be delegated to the 
 * controller class (in this case SuperRent).
 * 
 * TerminalTransactions calls the methods that we have listed below but 
 * SuperRent is the actual class that will implement the methods.
 */
public interface TerminalTransactionsDelegate {
	void deleteBranch(int branchId);
	void insertBranch(BranchModel model);
	void showBranch();
    int findConfNo(int confNo);
    int findDlicense(int confNo);
    String findVtName(int confNo);
    VehicleModel getFirstAvailableVehicle(String vtname, String location, String city, String fromDate);
    void handleRent(String vlicense, int dlicense, int odometer, String cardName, int cardNo, String expDate, String fromDate, String toDate, int confNo, String vtname, String location, String city);
    void updateVehicleStatus(String vlicense);
    void updateBranch(int branchId, String name);
	void returnVehicle(String vlicense, int odometer, boolean fulltank, String currentDateStr, Date currentDate);
	int countAvailableVehicles(String vtname, String location, String city, String startTime);
	void displayAvailableVehicles(String vtname, String location, String city, String startTime);
	int findCustomer(int dlicense);
	void addNewCustomer(CustomerModel model);
	void reserveVehicle(String vtname, int dlicense, String fromDate, String toDate, String location, String city);
	void generateRentalsReport(String date);
	void generateRentalsBranchReport(String date, String location, String city);
	void generateReturnsReport(String date);
	void generateReturnsBranchReport(String date, String location, String city);
	
	void terminalTransactionsFinished();
}
