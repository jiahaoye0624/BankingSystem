import java.util.*;
import java.sql.*;
import java.lang.Object;
import java.io.FileInputStream;

public class p2 {

    private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	public static void main(String argv[]) {
		System.out.println(":: PROGRAM START");
		if (argv.length < 1) {
			System.out.println("Need database properties filename");
		} else {
			init(argv[0]);
			testConnection();
			main_menu();
		}

		System.out.println(":: PROGRAM END");
	}

    public static void init(String filename) {
		try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);		// Create a new FileInputStream object using our filename parameter
			props.load(input);											// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");					// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");				// Load the username
			password = props.getProperty("jdbc.password");				// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	}

	public static void main_menu(){
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		System.out.println("Welcome to the Self Services Banking System!");
		System.out.println("\n1.New Customer");
		System.out.println("2.Customer Login");
		System.out.println("3.Exit");
		System.out.println();

		System.out.print("Enter your Option:");
		Scanner scanner = new Scanner(System.in);
		String option = scanner.next();

		switch(option){
			case "1":
				newCustomer();
				break;
			case "2":
				customerLogin();
				break;
			case "3":
				break;
		}
	}

	public static void newCustomer(){
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter your Name:");
		String name = scanner.next();
		System.out.println("Enter your Gender (M or F):");
		String gender = scanner.next();
		System.out.println("Enter your Age:");
		String age = scanner.next();
		System.out.println("Enter your Pin:");
		String pin = scanner.next();

        String sql_query = "CALL p2.CUST_CRT(?, ?, ?, ?, ?, ?, ?)";
		String cusid = "";
		String err_msg = "";
        try{
            con = DriverManager.getConnection(url, username, password);
            CallableStatement cs = con.prepareCall("{" + sql_query + "}");
            cs.setString(1, name);
            cs.setString(2, gender);
            cs.setInt(3, Integer.parseInt(age));
            cs.setInt(4, Integer.parseInt(pin));
			cs.registerOutParameter(5, Types.NUMERIC);
			cs.registerOutParameter(6, Types.NUMERIC);
			cs.registerOutParameter(7, Types.VARCHAR);
            cs.execute();
			cusid = String.valueOf(cs.getInt(5));
			err_msg = cs.getString(7);
        } catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("\nInput anything to Continue....");
			scanner.next();
			main_menu();
		}
		else{
			System.out.println("::New Customer -- Success");
			System.out.println("\nHere is your Customer ID: " + cusid);
			System.out.println("\nInput anything to Continue....");
			scanner.next();
			customerMenu();
		}
	}

	public static void customerLogin(){
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please Enter your Customer ID: ");
		String cusID = scanner.next();
		System.out.println("Enter your Pin Number: ");
		String pin = scanner.next();

		String sql_query = "CALL p2.CUST_LOGIN(?, ?, ?, ?, ?)";
		String err_msg = "";
		try{
			con = DriverManager.getConnection(url, username, password);
            CallableStatement cs = con.prepareCall("{" + sql_query + "}");
			cs.setInt(1, Integer.parseInt(cusID));
            cs.setInt(2, Integer.parseInt(pin));
			cs.registerOutParameter(3, Types.NUMERIC);
			cs.registerOutParameter(4, Types.NUMERIC);
			cs.registerOutParameter(5, Types.VARCHAR);
			cs.execute();
			err_msg = cs.getString(5);
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("\nEnter anything to Continue....");
			scanner.next();
			main_menu();
		}
		else{
			System.out.println("::LOGIN -- SUCCESS");
			System.out.println("\nEnter anything to Continue....");
			scanner.next();
			customerMenu();
		}
	}

	public static void customerMenu(){
		boolean stay = true;
		while(stay) {
			System.out.print("\033[H\033[2J");  
			System.out.flush();
			System.out.println("Customer Main Menu");
			System.out.println("\n1.Open Account");
			System.out.println("2.Close Account");
			System.out.println("3.Deposit");
			System.out.println("4.Withdraw");
			System.out.println("5.Transfer");
			System.out.println("6.Add Interest");
			System.out.println("7.Exit\n");

			Scanner scanner = new Scanner(System.in);
			System.out.print("Your Option:");
			String option = scanner.next();

			switch(option){
				case "1":
					newAccount();
					break;
				case "2":
					deactivateAccount();
					break;
				case "3":
					depositMoney();
					break;
				case "4":
					withdrawMoney();
					break;
				case "5":
					transferMoney();
					break;
				case "6":
					addInterest();
					break;
				case "7":
					stay = false;
					main_menu();
					break;
				default:
					customerMenu();
					break;
			}
		}		
	}

	public static void newAccount(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		
		System.out.println("Please enter your Customer ID: ");
		String cusId = scanner.next();

		System.out.println("Enter your Account Type (C for checking, S for Saving): ");
		String accountType = scanner.next();

		System.out.println("Enter your Balance (Initial deposit): ");
		String accountBalance = scanner.next();

		String sql_query = "CALL p2.ACCT_OPN(?, ?, ?, ?, ?, ?)";
		String err_msg = "";
		String accountNum = "";
		try{
			con = DriverManager.getConnection(url, username, password);
            CallableStatement cs = con.prepareCall("{" + sql_query + "}");
			cs.setInt(1, Integer.parseInt(cusId));
            cs.setInt(2, Integer.parseInt(accountBalance));
			cs.setString(3, accountType);
			cs.registerOutParameter(4, Types.NUMERIC);
			cs.registerOutParameter(5, Types.NUMERIC);
			cs.registerOutParameter(6, Types.VARCHAR);
			cs.execute();
			err_msg = cs.getString(6);
			accountNum = String.valueOf(cs.getInt(4));
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("::Open New Account -- Failed");
		}
		else{
			System.out.println("Here is your New Account Number:" + accountNum);
			System.out.println("::OPEN NEW ACCOUNT -- SUCCESS");
		}

		System.out.println("\nEnter anything to Continue....");
		scanner.next();
	}

	public static void deactivateAccount(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		
		System.out.print("Please enter your Customer ID: ");
		String cusid = scanner.next();
		System.out.print("Please enter your Account Number: ");
		String accountNumber = scanner.next();

		int flag = 0;
		String sql_query_identity = "CALL p2.ACCT_IDT(?, ?, ?, ?, ?)";
		try {
			con = DriverManager.getConnection(url, username, password);
            CallableStatement cs = con.prepareCall("{" + sql_query_identity + "}");
			cs.setInt(1, Integer.parseInt(cusid));
			cs.setInt(2, Integer.parseInt(accountNumber));
			cs.registerOutParameter(3, Types.NUMERIC);
			cs.registerOutParameter(4, Types.NUMERIC);
			cs.registerOutParameter(5, Types.VARCHAR);
			cs.execute();
			flag = cs.getInt(3);
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		String sql_query = "CALL p2.ACCT_CLS(?, ?, ?)";
		String err_msg = "";
		if(flag == 0){
			System.out.println("::This is not your Account");
		}
		else{	
			try{
				con = DriverManager.getConnection(url, username, password);
				CallableStatement cs = con.prepareCall("{" + sql_query + "}");
				cs.setInt(1, Integer.parseInt(accountNumber));
				cs.registerOutParameter(2, Types.NUMERIC);
				cs.registerOutParameter(3, Types.VARCHAR);
				cs.execute();
				err_msg = cs.getString(3);
			} catch (Exception e) {
				System.out.println(":: IDENTITY TEST - FAILED");
				// e.printStackTrace();
			}
		}

		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("::Close Account -- FAILED");
		}
		else {
			System.out.println("::CLOSE ACCOUNT -- SUCCESS");
		}
		
		System.out.println("Enter anything to Continue....");
		scanner.next();
	}

	public static void depositMoney(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("\033[H\033[2J");  
		System.out.flush();

		System.out.println("Please enter your Account Number: ");
		String accountNumber = scanner.next();
		System.out.println("Please enter the deposit amount: ");
		String depositAmount = scanner.next();


		String sql_query = "CALL p2.ACCT_DEP(?, ?, ?, ?)";
		String err_msg = "";
		try{
			con = DriverManager.getConnection(url, username, password);
            CallableStatement cs = con.prepareCall("{" + sql_query + "}");
			cs.setInt(1, Integer.parseInt(accountNumber));
			cs.setInt(2, Integer.parseInt(depositAmount));
			cs.registerOutParameter(3, Types.NUMERIC);
			cs.registerOutParameter(4, Types.VARCHAR);
			cs.execute();
			err_msg = cs.getString(4);
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("::DEPOSIT Account -- FAILED");
		}
		else {
			System.out.println("::DEPOSIT MONEY -- SUCCESS");
		}
		
		System.out.println("Enter anything to Continue....");
		scanner.next();
	}

	public static void withdrawMoney(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("\033[H\033[2J");  
		System.out.flush();

		System.out.print("Please enter your Customer ID: ");
		String cusid = scanner.next();
		System.out.print("Please enter your Account Number: ");
		String accountNumber = scanner.next();
		
		String sql_query_identity = "CALL p2.ACCT_IDT(?, ?, ?, ?, ?)";
		int flag = 0;
		try {
			con = DriverManager.getConnection(url, username, password);
            CallableStatement cs = con.prepareCall("{" + sql_query_identity + "}");
			cs.setInt(1, Integer.parseInt(cusid));
			cs.setInt(2, Integer.parseInt(accountNumber));
			cs.registerOutParameter(3, Types.NUMERIC);
			cs.registerOutParameter(4, Types.NUMERIC);
			cs.registerOutParameter(5, Types.VARCHAR);
			cs.execute();
			flag = cs.getInt(3);
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		String sql_query = "CALL P2.ACCT_WTH(?, ?, ?, ?)";
		String err_msg = "";
		if (flag == 0){
			System.out.println("This is not your account");
		} else {
			System.out.println("Please enter the withdrawn amount: ");
			String withdrawAmount = scanner.next();
			try{
				con = DriverManager.getConnection(url, username, password);
            	CallableStatement cs = con.prepareCall("{" + sql_query + "}");
				cs.setInt(1, Integer.parseInt(accountNumber));
				cs.setInt(2, Integer.parseInt(withdrawAmount));
				cs.registerOutParameter(3, Types.NUMERIC);
				cs.registerOutParameter(4, Types.VARCHAR);
				cs.execute();
				err_msg = cs.getString(4);
			} catch (Exception e){
				System.out.println(":: TEST - FAILED");
			}
		}
		
		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("::FAILED TO WITHDRAWN");
		}
		else {
			System.out.println("::WITHDRAW -- SUCCESS");
		}

		
		System.out.println("Enter anything to Continue....");
		scanner.next();
	}

	public static void transferMoney(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("\033[H\033[2J");  
		System.out.flush();

		System.out.print("Please enter your Customer ID: ");
		String cusid = scanner.next();
		System.out.print("Please enter your Account Number: ");
		String accountNumber = scanner.next();
		
		String sql_query_identity = "CALL p2.ACCT_IDT(?, ?, ?, ?, ?)";
		int flag = 0;
		try {
			con = DriverManager.getConnection(url, username, password);
            CallableStatement cs = con.prepareCall("{" + sql_query_identity + "}");
			cs.setInt(1, Integer.parseInt(cusid));
			cs.setInt(2, Integer.parseInt(accountNumber));
			cs.registerOutParameter(3, Types.NUMERIC);
			cs.registerOutParameter(4, Types.NUMERIC);
			cs.registerOutParameter(5, Types.VARCHAR);
			cs.execute();
			flag = cs.getInt(3);
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		String sql_query = "CALL p2.ACCT_TRX(?, ?, ?, ?, ?)";
		String err_msg = "";
		if(flag == 0){
			System.out.println("::This is not your account!");
		}
		else{
			System.out.println("Please enter the destination Account Number: ");
			String destinationAccountNumber = scanner.next();
			System.out.println("Please enter the transfer amount: ");
			String transferAmount = scanner.next();
			try{
				con = DriverManager.getConnection(url, username, password);
				CallableStatement cs = con.prepareCall("{" + sql_query + "}");
				cs.setInt(1, Integer.parseInt(accountNumber));
				cs.setInt(2, Integer.parseInt(destinationAccountNumber));
				cs.setInt(3, Integer.parseInt(transferAmount));
				cs.registerOutParameter(4, Types.NUMERIC);
				cs.registerOutParameter(5, Types.VARCHAR);
				cs.execute();
				err_msg = cs.getString(5);
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED");
				// e.printStackTrace();
			}
		}

		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("::Failed to process transfer");
		}
		else{
			System.out.println("::TRANSFER -- SUCCESS");
		}
		
		System.out.println("Enter anything to Continue....");
		scanner.next();
	}

	public static void addInterest(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("\033[H\033[2J");  
		System.out.flush();

		System.out.println("Please Enter the Saving Rate: ");
		String savingRate = scanner.next();
		System.out.println("Please Enter the Checking Rate: ");
		String checkingRate = scanner.next();
		String sql_query = "CALL p2.ADD_INTEREST(?, ?, ?, ?)";
		String err_msg = "";
		try{
			con = DriverManager.getConnection(url, username, password);
			CallableStatement cs = con.prepareCall("{" + sql_query + "}");
			cs.setFloat(1, Float.parseFloat(savingRate));
			cs.setFloat(2, Float.parseFloat(checkingRate));
			cs.registerOutParameter(3, Types.NUMERIC);
			cs.registerOutParameter(4, Types.VARCHAR);
			cs.execute();
			err_msg = cs.getString(4);
		} catch (Exception e) {
			System.out.println(":: TEST - FAILED");
			// e.printStackTrace();
		}

		if(err_msg != null){
			System.out.println(err_msg);
			System.out.println("Failed to Add interest");
		}
		else{
			System.out.println("::ADD INTEREST -- SUCCESS");
		}

		System.out.println("Enter anything to Continue....");
		scanner.next();
	}
}