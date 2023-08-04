import java.util.*;
import java.lang.Object;

public class P1 {

	public static void main(String argv[]) {
		System.out.println(":: PROGRAM START");
		
		if (argv.length < 1) {
			System.out.println("Need database properties filename");
		} else {
			BankingSystem.init(argv[0]);
			BankingSystem.testConnection();
			System.out.println();

			System.out.println("this is before calling main_menu");
			main_menu();
		}

		System.out.println(":: PROGRAM END");
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
		BankingSystem.newCustomer(name, gender, age, pin);
		String cusid = "";
		int i = BankingSystem.returnCustomerID(name, gender, age, pin);
		cusid = String.valueOf(i);
		System.out.println("\nHere is your Customer ID: " + cusid);

		System.out.println("\nInput anything to Continue....");
		scanner.next();
		customerMenu();
	}

	public static void customerLogin(){
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please Enter your Customer ID: ");
		String cusID = scanner.next();
		System.out.println("Enter your Pin Number: ");
		String pin = scanner.next();

		int flag = 0;
		if (cusID.equals("0") && pin.equals("0")){
			administratorScreen();
		} else {
			flag = BankingSystem.checkCustomerLogin(cusID, pin);
			if (flag == 0){
				System.out.println("Invalid customer Information, please retype it again.");
				customerLogin();
			} else{
				System.out.println("\nEnter anything to Continue....");
				scanner.next();
				customerMenu();
			}
		}
	}

	public static void customerMenu(){
		boolean stay = true;
		while(stay) {
			System.out.print("\033[H\033[2J");  
			System.out.flush();
			System.out.println("Customer Main Menu");
			System.out.println("\n1.Open Account");
			System.out.println("2.Close Account");  // --------------------Need Cusid
			System.out.println("3.Deposit");
			System.out.println("4.Withdraw");		// --------------------Need Cusid
			System.out.println("5.Transfer");		// --------------------ScrAccount need to confirm cusid
			System.out.println("6.Account Summary");
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
					printAccountSummary();
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
		BankingSystem.openAccount(cusId, accountType, accountBalance);

		int i = BankingSystem.returnAccountNum(cusId, accountType, accountBalance);
		String accountNum = String.valueOf(i);
		System.out.println("Here is your New Account Number:" + accountNum);

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
		int i = BankingSystem.checkIdentity(accountNumber, cusid);
		if (i == 0){
			System.out.println("INVALID Customer ID");
		} else {
			BankingSystem.closeAccount(accountNumber);
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
		BankingSystem.deposit(accountNumber, depositAmount);
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
		

		int i = BankingSystem.checkIdentity(accountNumber, cusid);
		if (i == 0){
			System.out.println("INVALID Customer ID");
		} else {
			System.out.println("Please enter the withdrawn amount: ");
			String withdrawAmount = scanner.next();
			BankingSystem.withdraw(accountNumber, withdrawAmount);
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
		System.out.println("Please enter your Account Number: ");
		String accountNumber = scanner.next();
		
		int i = BankingSystem.checkIdentity(accountNumber, cusid);
		if (i == 0){
			System.out.println("INVALID Customer ID");
		} else {
			System.out.println("please enter the destination Account Number: ");
			String destinationAccountNumber = scanner.next();
			System.out.println("Please enter the transfer amount: ");
			String transferAmount = scanner.next();
			BankingSystem.transfer(accountNumber, destinationAccountNumber, transferAmount);
		}

		
		System.out.println("Enter anything to Continue....");
		scanner.next();
	}

	public static void printAccountSummary(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("\033[H\033[2J");  
		System.out.flush();
		System.out.print("Please enter your Customer ID: ");
		String cusid = scanner.next();
		System.out.println("Here is your Account Summary:");
		BankingSystem.accountSummary(cusid);
		System.out.println("Enter anything to Continue....");
		scanner.next();
	}

	public static void administratorScreen(){
		Scanner scanner = new Scanner(System.in);
		boolean stay = true;
		while(stay){
			System.out.print("\033[H\033[2J");  
			System.out.flush();

			System.out.println("1.Account Summary for a Customer");
			System.out.println("2.Report A :: Customer Information with Total Balance in Decreasing Order");
			System.out.println("3.Report B :: Find the Average Total Balance Between Age Groups");
			System.out.println("4.Exit");

			System.out.println();
			String option = scanner.next();
			switch(option) {
				case "1":
					printAccountSummary();
					break;
				case "2":
					BankingSystem.reportA();
					System.out.println("Enter anything to Continue....");
					scanner.next();
					break;
				case "3":
					System.out.println("Enter the min age:");
					String min = scanner.next();
					System.out.println("Enter the max age:");
					String max = scanner.next();
					BankingSystem.reportB(min, max);
					System.out.println("Enter anything to Continue....");
					scanner.next();
					break;
				case "4":
					stay = false;
					main_menu();
					break;
				default:
					administratorScreen();
			}
		}
		
	}
}