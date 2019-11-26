package ca.ubc.cs304.ui;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.BranchModel;
import ca.ubc.cs304.model.CustomerModel;
import ca.ubc.cs304.model.VehicleModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class is only responsible for handling terminal text inputs.
 */
public class TerminalTransactions {
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	private static final int INVALID_INPUT = Integer.MIN_VALUE;
	private static final int EMPTY_INPUT = 0;

	private BufferedReader bufferedReader = null;
	private TerminalTransactionsDelegate delegate = null;

	public TerminalTransactions() {
	}

	/**
	 * Displays simple text interface
	 */
	public void showMainMenu(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;

		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;

		while (choice != 9) {
			System.out.println();
			System.out.println("Customer:");
			System.out.println("1. View available vehicles");
			System.out.println("2. Make reservation");
			System.out.println("Clerk:");
			System.out.println("3. Renting a vehicle");
			System.out.println("4. Returning a vehicle");
			System.out.println("5. Generate daily rentals report");
			System.out.println("6. Generate daily rentals for branch report");
			System.out.println("7. Generate daily returns report");
			System.out.println("8. Generate daily returns for branch report");
			System.out.println("9. Quit");
			System.out.print("Please choose one of the above 9 options: ");

			choice = readInteger(false);

			System.out.println(" ");

			if (choice != INVALID_INPUT) {
				switch (choice) {
					case 1:
						handleViewVehiclesOption();
						break;
					case 2:
						handleReservationOption();
						break;
					case 3:
						handleRentOption();
						break;
					case 4:
						handleReturnOption();
						break;
					case 5:
						handleRentalsReportOption();
						break;
					case 6:
						handleRentalsBranchReportOption();
						break;
					case 7:
						handleReturnsReportOption();
						break;
					case 8:
						handleReturnsBranchReportOption();
						break;
					case 9:
						handleQuitOption();
						break;
					default:
						System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
						break;
				}
			}
		}
	}

	private void handleViewVehiclesOption() {
		System.out.println("\nPlease provide the car type, location&city, and time interval you wish to search on, you can choose to leave any of them blank.\n");
		System.out.print("Please enter the car type you wish to view for available vehicles: ");
		String vtname = readLine().trim();
		if (vtname.length() == 0) {
			vtname = null;
		}

		System.out.print("Please enter the location you wish to view for available vehicles: ");
		String location = readLine().trim();
		if (location.length() == 0) {
			location = null;
		}

		System.out.print("Please enter the city you wish to view for available vehicles: ");
		String city = readLine().trim();
		if (city.length() == 0) {
			city = null;
		}

		System.out.print("Please enter the start time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to view for available vehicles: ");
		String startTime = readLine().trim();
		if (startTime.length() == 0) {
			startTime = null;
		} else {
			String pattern = "dd-MMM-YYYY HH:mm:ss";
			while (!isTimeInValidFormat(startTime, pattern)) {
				System.out.print("Please enter the start time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to view for available vehicles: ");
				startTime = readLine().trim();
			}
		}

		System.out.print("Please enter the end time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to view for available vehicles: ");
		String endTime = readLine().trim();
		if (endTime.length() == 0) {
			endTime = null;
		} else {
			String pattern = "dd-MMM-YYYY HH:mm:ss";
			while (!isTimeInValidFormat(endTime, pattern)) {
				System.out.print("Please enter the end time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to view for available vehicles: ");
				endTime = readLine().trim();
			}
		}

		delegate.countAvailableVehicles(vtname, location, city, startTime);
		System.out.print("If you wish to view the details of available vehicles enter Y, otherwise enter N: ");
		String YorN = readLine().trim();
		if (YorN.equals("Y")) {
			delegate.displayAvailableVehicles(vtname, location, city, startTime);
		}
	}


	// Helper method to validate the format of dateTime inputs
	private Boolean isTimeInValidFormat(String time, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		format.setLenient(false);
		try {
			ParsePosition p = new ParsePosition(0);
			format.parse(time, p);
			if (p.getIndex() < time.length()) {
				throw new ParseException(time, p.getIndex());
			}
			return true;
		} catch (ParseException e) {
			System.out.println(time + " does not follow the format " + pattern + " Please try again.");
			return false;
		}
	}

	private void handleReservationOption() {
		System.out.println("\nTo make a reservation, please provide the following information: \n");

		String city = null;
		while (city == null || city.length() <= 0) {
			System.out.print("Please enter the city you wish to pick up the vehicle: ");
			city = readLine().trim();
		}

		String location = null;
		while (location == null || location.length() <= 0) {
			System.out.print("Please enter the location you wish to pick up the vehicle: ");
			location = readLine().trim();
		}


		String vtname = null;
		while (vtname == null || vtname.length() <= 0) {
			System.out.print("Please enter the car type you wish to reserve: ");
			vtname = readLine().trim();
		}

		int dlicense = INVALID_INPUT;
		while (dlicense == INVALID_INPUT) {
			System.out.print("Please enter your driver license number: ");
			dlicense = readInteger(false);
		}

		String pattern = "dd-MMM-YYYY HH:mm:ss";
		String fromDate = null;
		while (fromDate == null || fromDate.length() <= 0 || !isTimeInValidFormat(fromDate, pattern)) {
			System.out.print("Please enter the time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to pick up the vehicle: ");
			fromDate = readLine().trim();
		}

		String toDate = null;
		while (toDate == null || toDate.length() <= 0 || !isTimeInValidFormat(toDate, pattern)) {
			System.out.print("Please enter the time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to return the vehicle: ");
			toDate = readLine().trim();
		}

		if (delegate.findCustomer(dlicense) == 0) {
			System.out.println();
			System.out.println("Sorry, you are not registered with SuperRent. In order to make a reservation, you need to be a registered member.");
			System.out.print("Please enter Y if you want to be registered into our System. Otherwise enter N and you will be taken back to the main menu: ");
			String YorN = readLine().trim();
			if (!YorN.equals("Y")) return;
			System.out.println();

			String name = null;
			while (name == null || name.length() <= 0) {
				System.out.print("Great. Please enter your name: ");
				name = readLine().trim();
			}

			System.out.print("Please enter your phone number (optional) (e.g (604)-000-000): ");
			String cellphone = readLine().trim();
			if (cellphone.length() == 0) {
				cellphone = null;
			}

			System.out.print("Please enter your address (optional): ");
			String address = readLine().trim();
			if (address.length() == 0) {
				address = null;
			}

			System.out.println("Thank you " + name + ", we will be adding you to our system.");
			CustomerModel model = new CustomerModel(dlicense, cellphone, name, address);
			delegate.addNewCustomer(model);

		}

		if (delegate.countAvailableVehicles(vtname, location, city, fromDate) != 0) {
			delegate.reserveVehicle(vtname, dlicense, fromDate, toDate, location, city);
		}
	}

	private void handleRentOption() {
		int confNo = INVALID_INPUT;
		while (confNo == INVALID_INPUT) {
			System.out.print("Please enter the confirmation number: ");
			confNo = readInteger(false);
		}

		if (delegate.findConfNo(confNo) == 0) {
			System.out.println();
			System.out.println("ERROR: Cannot find the confirmation number " + confNo + " in system.");
			System.out.print("Please enter Y if you want to be make a reservation, otherwise enter N to go back to the main menu: ");
			String YorN = readLine().trim();
			if (!YorN.equals("Y")) return;
			System.out.println();
			this.handleReservationOption();

			System.out.println();

			System.out.print("If you want to proceed to rent a vehicle, please enter Y, otherwise enter N to get back to the main menu: ");
			String YesOrNo = readLine().trim();
			if (!YesOrNo.equals("Y")) return;
			System.out.println();

			confNo = INVALID_INPUT;
			while (confNo == INVALID_INPUT) {
				System.out.print("Please enter the confirmation number: ");
				confNo = readInteger(false);
			}
		}


		int dlicense = delegate.findDlicense(confNo);
		String vtname = delegate.findVtName(confNo);

		String city = null;
		while (city == null || city.length() <= 0) {
			System.out.print("Please enter the city you wish to pick up the vehicle: ");
			city = readLine().trim();
		}

		String location = null;
		while (location == null || location.length() <= 0) {
			System.out.print("Please enter the location you wish to pick up the vehicle: ");
			location = readLine().trim();
		}

		String pattern = "dd-MMM-YYYY HH:mm:ss";
		String fromDate = null;
		while (fromDate == null || fromDate.length() <= 0 || !isTimeInValidFormat(fromDate, pattern)) {
			System.out.print("Please enter the time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to pick up the vehicle: ");
			fromDate = readLine().trim();
		}

		String toDate = null;
		while (toDate == null || toDate.length() <= 0 || !isTimeInValidFormat(toDate, pattern)) {
			System.out.print("Please enter the time (DD-MON-YYYY HH24:MI:SS e.g. 01-JAN-2019 23:00:00) you wish to return the vehicle: ");
			toDate = readLine().trim();
		}

		if ((delegate.countAvailableVehicles(vtname, location, city, fromDate)) == 0) {
			return;
		}

		VehicleModel model = delegate.getFirstAvailableVehicle(vtname, location, city, fromDate);


		String cardName = null;
		while (cardName == null || cardName.length() <= 0 || !(cardName.equals("Visa") || cardName.equals("MasterCard"))) {
			System.out.print("Please enter card type (only accept Visa or MasterCard): ");
			cardName = readLine().trim();
		}

		String expDate = null;
		while (expDate == null || expDate.length() <= 0 || !isTimeInValidFormat(expDate, "dd-MMM-YYYY")) {
			System.out.print("Please enter card expiry date(DD-MON-YYYY): ");
			expDate = readLine().trim();
		}

		int odometer = INVALID_INPUT;
		while (odometer == INVALID_INPUT) {
			System.out.print("Please enter the odometer: ");
			odometer = readInteger(false);
		}

		int cardNo = INVALID_INPUT;
		while (cardNo == INVALID_INPUT) {
			System.out.print("Please enter the card number: ");
			cardNo = readInteger(false);
		}

		delegate.handleRent(model.getVlicense(), dlicense, odometer, cardName, cardNo, expDate, fromDate, toDate, confNo, vtname, location, city);
		delegate.updateVehicleStatus(model.getVlicense());

	}

	private void handleReturnOption() {
		String vlicense = null;
		while (vlicense == null || vlicense.length() <= 0) {
			System.out.print("Please enter the license plate number for the vehicle you wish to return: ");
			vlicense = readLine().trim();
		}

		int odometer = INVALID_INPUT;
		while (odometer == INVALID_INPUT) {
			System.out.print("Please enter the odometer reading: ");
			odometer = readInteger(false);
		}

		String fulltankStr = "";
		int isBoolean = 0;
		while (isBoolean == 0) {
			System.out.print("Please enter \"true\" or \"false\" for whether the vehicle has full tank : ");
			String ans = readLine().trim();
			if (ans.equals("true") || ans.equals("false")) {
				fulltankStr = ans;
				isBoolean = 1;
			} else {
				System.out.print("Entered value does not match \"true\" or \"false\". ");
			}
		}
		boolean fulltank = Boolean.parseBoolean(fulltankStr);

		Date currentDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-YYYY HH:mm:ss");
		String currentDateStr = formatter.format(currentDate);

		delegate.returnVehicle(vlicense, odometer, fulltank, currentDateStr, currentDate);
	}

	private void handleRentalsReportOption() {
		String date = getDateForReport("rentals");
		delegate.generateRentalsReport(date);
	}

	private void handleRentalsBranchReportOption() {
		String date = getDateForReport("rentals for branch");
		String[] result = getCityAndLocation("rentals");
		delegate.generateRentalsBranchReport(date, result[0], result[1]);
	}

	private void handleReturnsReportOption() {
		String date = getDateForReport("returns");
		delegate.generateReturnsReport(date);
	}

	private void handleReturnsBranchReportOption() {
		String date = getDateForReport("returns for branch");
		String[] result = getCityAndLocation("returns");
		delegate.generateReturnsBranchReport(date, result[0], result[1]);
	}

	// helper method for report options to get the date of report
	private String getDateForReport(String reportName) {
		String date = null;
		String pattern = "dd-MMM-YYYY";
		while (date == null || date.length() <= 0 || !isTimeInValidFormat(date, pattern)) {
			System.out.print("Please enter the date you wish to generate " + reportName + " report for (eg. 01-JAN-2019): ");
			date = readLine().trim();
		}
		return date;
	}

	// helper method for handleRentalsBranchReportOption and handleReturnsBranchReportOption to get the branch
	// return a string array with result[0] = location, result[1] = city
	private String[] getCityAndLocation(String reportName) {
		String city = null;
		while (city == null || city.length() <= 0) {
			System.out.print("Please enter the city of the branch you wish to generate " + reportName + " report for: ");
			city = readLine().trim();
		}
		String location = null;
		while (location == null || location.length() <= 0) {
			System.out.print("Please enter the location of the branch you wish to generate " + reportName + " report for: ");
			location = readLine().trim();
		}
		String[] result = {location, city};
		return result;
	}

	private void handleDeleteOption() {
		int branchId = INVALID_INPUT;
		while (branchId == INVALID_INPUT) {
			System.out.print("Please enter the branch ID you wish to delete: ");
			branchId = readInteger(false);
			if (branchId != INVALID_INPUT) {
				delegate.deleteBranch(branchId);
			}
		}
	}

	private void handleInsertOption() {
		int id = INVALID_INPUT;
		while (id == INVALID_INPUT) {
			System.out.print("Please enter the branch ID you wish to insert: ");
			id = readInteger(false);
		}

		String name = null;
		while (name == null || name.length() <= 0) {
			System.out.print("Please enter the branch name you wish to insert: ");
			name = readLine().trim();
		}

		// branch address is allowed to be null so we don't need to repeatedly ask for the address
		System.out.print("Please enter the branch address you wish to insert: ");
		String address = readLine().trim();
		if (address.length() == 0) {
			address = null;
		}

		String city = null;
		while (city == null || city.length() <= 0) {
			System.out.print("Please enter the branch city you wish to insert: ");
			city = readLine().trim();
		}

		int phoneNumber = INVALID_INPUT;
		while (phoneNumber == INVALID_INPUT) {
			System.out.print("Please enter the branch phone number you wish to insert: ");
			phoneNumber = readInteger(true);
		}

		BranchModel model = new BranchModel(address,
				city,
				id,
				name,
				phoneNumber);
		delegate.insertBranch(model);
	}

	private void handleQuitOption() {
		System.out.println("Good Bye!");

		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				System.out.println("IOException!");
			}
		}

		delegate.terminalTransactionsFinished();
	}

	private void handleUpdateOption() {
		int id = INVALID_INPUT;
		while (id == INVALID_INPUT) {
			System.out.print("Please enter the branch ID you wish to update: ");
			id = readInteger(false);
		}

		String name = null;
		while (name == null || name.length() <= 0) {
			System.out.print("Please enter the branch name you wish to update: ");
			name = readLine().trim();
		}

		delegate.updateBranch(id, name);
	}

	private int readInteger(boolean allowEmpty) {
		String line = null;
		int input = INVALID_INPUT;
		try {
			line = bufferedReader.readLine();
			input = Integer.parseInt(line);
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		} catch (NumberFormatException e) {
			if (allowEmpty && line.length() == 0) {
				input = EMPTY_INPUT;
			} else {
				System.out.println(WARNING_TAG + " Your input was not an integer");
			}
		}
		return input;
	}

	private String readLine() {
		String result = null;
		try {
			result = bufferedReader.readLine();
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return result;
	}
}
