import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.sql.SQLException;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test database connection.
	 */
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

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin) 
	{
		System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
				/* insert your code here */
		String sql = "INSERT INTO P1.customer (name, gender, age, pin) " +
					"VALUES ('" + name + "', '" + gender + "', '" + age + "', '" + pin + "')";
		
		int newCusId = 0;
		try{
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			stmt.execute(sql);
			System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
			stmt.close();
			con.close();	
		} catch (SQLException e1){
			System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID DATA");
			// e1.printStackTrace();
		}
	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount) 
	{
		System.out.println(":: OPEN ACCOUNT - RUNNING");
		String sql = "INSERT INTO P1.account (id, balance, type, status) " +
					"VALUES ('" + id + "', '" + amount + "', '" + type + "', 'A')";	
		try{
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			stmt.execute(sql);
			System.out.println(":: OPEN ACCOUNT - SUCCESS");
			stmt.close();
			con.close();
		} catch(SQLException e2){
			System.out.println("INVALID DATA");
			// e2.printStackTrace();
		}
	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum) 
	{
		System.out.println(":: CLOSE ACCOUNT - RUNNING");
		/* insert your code here */
		String sql = "UPDATE P1.account SET status = 'I', balance = 0 WHERE number = '" + accNum + "'";
		try{
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(":: CLOSE ACCOUNT - SUCCESS");
			stmt.close();
			con.close();
		} catch(SQLException e3){
			System.out.println("INVALID DATA");
		}
		
	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount) 
	{
		System.out.println(":: DEPOSIT - RUNNING");
				/* insert your code here */
		String sql = "UPDATE P1.account SET balance = balance + " + amount 
					+ " WHERE number = " + accNum + " AND status = 'A'";
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			int i = stmt.executeUpdate(sql);
			if(i == 0){
				System.out.println(":: DEPOSIT - FAILED");
			}
			else{
				System.out.println(":: DEPOSIT - SUCCESS");
			}
			stmt.close();
			con.close();
		} catch(SQLException e4){
			System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");
			// e4.printStackTrace();
		}
	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount) 
	{
		System.out.println(":: WITHDRAW - RUNNING");
				/* insert your code here */
		String sql = "UPDATE P1.account SET balance = balance - " + amount 
					+ " WHERE number = '" + accNum + "' AND balance >= " + amount;
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			int i = stmt.executeUpdate(sql);
			if(i == 0){
				System.out.println(":: WITHDRAW - ERROR - NOT ENOUGH FUNDS");
			}
			else{
				System.out.println(":: WITHDRAW - SUCCESS");
			}
			stmt.close();
			con.close();
		} catch(SQLException e4){
			System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT ");
		}
	}

	/**
	 * Transfer amount from source account to destination account. 
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount) 
	{
		System.out.println(":: TRANSFER - RUNNING");
				/* insert your code here */	
		String sql = "UPDATE P1.account SET balance = balance - " + amount 
					+ " WHERE number = " + srcAccNum + " AND balance >= " + amount;
		String sql_b = "UPDATE P1.account SET balance = balance + " + amount + " WHERE number = " + destAccNum;
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			int i = stmt.executeUpdate(sql);
			if (i == 0){
				System.out.println(":: TRANSFER - ERROR - NOT ENOUGH FUNDS");
			} else {
				stmt.executeUpdate(sql_b);
				System.out.println(":: TRANSFER - SUCCESS");
			}
			stmt.close();
			con.close();
		} catch(SQLException e4){
			System.out.println(":: TRANSFER - ERROR");
		}
	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID) 
	{
		System.out.println(":: ACCOUNT SUMMARY - RUNNING");
				/* insert your code here */	

		String sql = "select number, balance from P1.account where id = '" + cusID + "' AND status = 'A'";
		try{
			int total = 0;
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf("%-12s %-12s\n", "NUMBER", "BALANCE");
			System.out.printf("%12s %12s\n", "------------", "------------");
			while(rs.next()){
				System.out.printf("%12d %12d\n",
					rs.getInt("Number"),
					rs.getInt("balance"));
				total += rs.getInt("balance");
			}
			System.out.printf("%24s\n", "-------------------------");
			System.out.print("Total:");
			System.out.printf("%19d\n", total);
			System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
		} catch (SQLException e1) {
            // TODO Auto-generated catch block
			System.out.println(":: ACCOUNT SUMMARY - Failed");
            // e1.printStackTrace();
        }
	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA() 
	{
		System.out.println(":: REPORT A - RUNNING");
				/* insert your code here */	
		String sql = "SELECT a.id, c.name, c.gender, c.age, sum(a.balance) as Total "
					  + "FROM P1.account as a "
					  + "INNER JOIN P1.customer as C "
					  + "ON a.id = c.id "
					  + "WHERE a.status = 'A'"
					  + "GROUP BY a.id, c.name, c.gender, c.age "
					  + "ORDER BY Total DESC";
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf("%-12s %-12s %-6s %-12s %-12s\n", "ID", "NAME", "GENDER", "AGE", "TOTAL");
			System.out.printf("%12s %12s %6s %12s %12s\n", 
								"------------", 
								"------------", 
								"------", 
								"------------", 
								"------------");
			while(rs.next()){
				System.out.printf("%12d %-12s %-6s %12d %12d\n",
					rs.getInt("id"),
					rs.getString("name"),
					rs.getString("gender"),
					rs.getInt("age"),
					rs.getInt("total"));
			}
			System.out.println(":: REPORT A - SUCCESS");
		} catch (SQLException e5){
			System.out.println(":: REPORT A - FAILED");
			// e5.printStackTrace();
		}
		
	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		System.out.println(":: REPORT B - RUNNING");
				/* insert your code here */		
		String sql = "SELECT SUM(Total)/count(*) as AVERAGE "
					+ "FROM (SELECT a.id, c.age, sum(a.balance) as Total "
					  + "FROM P1.account as a "
					  + "INNER JOIN P1.customer as c "
					  + "ON a.id = c.id "
					  + "GROUP BY a.id, c.age "
					  + "ORDER BY Total DESC) as t "
					  + "WHERE age >= " + min + " and age <= " + max;
		try{
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			System.out.printf("%-12s\n", "AVERAGE");
			System.out.printf("%12s\n", "------------");
			while(rs.next()){
				System.out.printf("%12d\n", rs.getInt("AVERAGE"));
			}
			System.out.println(":: REPORT B - SUCCESS");
		} catch (SQLException e6) {
			System.out.println(":: REPORT B - FAILED");
			// e6.printStackTrace();
		}
	}

	/**
	 * Return the customer ID.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static int returnCustomerID(String name, String gender, String age, String pin){
		String sql2 = "SELECT id FROM P1.customer WHERE name = '" + name + "'"
					+ " AND gender = '" + gender + "' AND age = '" + age + "' AND pin = '" + pin + "'";
		int newCusId = 0;
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql2);
			while(rs.next()){
				newCusId = rs.getInt("id");
			}
			stmt.close();
			con.close();
		} catch(SQLException h1){
			System.out.println("FAILED TO RETURN CUSTOMER ID");
		}
		return newCusId;
	}

	/**
	 * Return the account number.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static int returnAccountNum (String id, String type, String amount){
		String sql2 = "SELECT number FROM P1.account WHERE id = '" + id + "'" + " AND type = '" + type + "' "
						+ "AND balance = '" + amount + "'";
		int newAccountNumber = 0;
		try {
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql2);
			while(rs.next()){
				newAccountNumber = rs.getInt("number");
			}
			stmt.close();
			con.close();
		} catch (SQLException h2) {
			System.out.println("FAILED TO RETUNR ACCOUNT NUMBER");
		}
		return newAccountNumber;
	}

	/**
	 * Return an integer to check customer login information.
	 * @param cusid customer id
	 * @param pin customer pin
	 */
	public static int checkCustomerLogin(String cusid, String pin){
		System.out.println(":: CHECKING CUSTOMER INFORMATION - RUNNING");
		String sql = "SELECT count(*) as flag FROM P1.customer WHERE id = '" + cusid + "' AND pin = '" + pin + "'";
		int flag = 0;
		try{
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				flag = rs.getInt("flag");
			}
			System.out.println(":: CHECKING CUSTOMER INFORMATION - SUCCESS");
			stmt.close();
			con.close();
		} catch(SQLException e7){
			System.out.println("Invalid customer information");
		}
		return flag;
	}

	/**
	 * Return an integer to owner of the account
	 * @param accnum Account Number
	 * @param cusid customer id
	 */
	public static int checkIdentity(String accnum, String cusid){
		String sql = "SELECT count(*) as flag FROM P1.account WHERE number = '" + accnum + "' AND id = '" + cusid + "'";
		int flag = 0;
		try{
			con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()){
				flag = rs.getInt("flag");
			}
			con.close();
			stmt.close();
		} catch(SQLException e8){
			System.out.println(":: CHECK Identity FAILED");
		}
		return flag;
	}
}
