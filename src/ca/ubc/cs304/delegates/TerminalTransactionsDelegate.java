package ca.ubc.cs304.delegates;

import ca.ubc.cs304.model.BranchModel;

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
	void updateBranch(int branchId, String name);
	void generateRentalsReport();
	void generateRentalsBranchReport(String location, String city);
	void generateReturnsReport();
	void generateReturnsBranchReport(String location, String city);
	
	public void terminalTransactionsFinished();
}
