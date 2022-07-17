package atm;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import interfaces.OperationsInterface;

/**
 * Created by AMCBR on 06/02/2017.
 */
public class ATM {

    static OperationsInterface bank;
    static Registry registry;
    static Scanner in = new Scanner(System.in);

    /**
     * Attenpt to parse port to connect to, else defaults to 1099 and tries connecting
     * Starts CLI interface to log in users and presents options for interacting with server
     * @param args
     * @throws RemoteException
     * @throws NotBoundException
     */
    public static void main(String[] args) throws RemoteException, NotBoundException, SQLException {
        String name = "BankServer";

        registry = LocateRegistry.getRegistry(1099);
        try{
            registry = LocateRegistry.getRegistry(args[0]);
            System.out.println("RMI Port set to "+args[0]);
        }catch(ArrayIndexOutOfBoundsException e) {
            registry = LocateRegistry.getRegistry(1099); //Default to port 800
            System.out.println("RMI Port defaulting to " + 1099);
        }

            bank = (OperationsInterface) registry.lookup(name);
        if (bank != null)
            System.out.println("bank found");

        System.out.println("Please enter your username and password: (In form username-password)");
        String input = in.next();
        String[] credentials = input.split("-");

        boolean loggedIn = false;
        int accNum = -1;
        int accNum1 = 0;
        try {
            accNum = bank.login(credentials[0],credentials[1] );
            if (accNum>0) {
                loggedIn = true;
                System.out.println("Successfully Logged In");
            }else{
                System.out.println("Details Incorrect");
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Details entered incorrectly");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        UUID sessionId = UUID.randomUUID();
        while (loggedIn) { //User stays logged in until bank.checkSessionId() changes it to false

            System.out.println("\nPlease enter an option: (deposit / withdraw / balance / statement / transfer / exit)");
            String operation = in.next();

            switch (operation.trim().toLowerCase()) {

                case "deposit":
                    System.out.println("Enter an amount to deposit");
                    input = in.next();
                    try {
                        System.out.println("Successfully deposited €" + input
                                + "\nNew Balance: €" + bank.deposit(accNum,Double.parseDouble(input))); //Test
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Input. Try again");
                        break;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "withdraw":
                    System.out.println("Enter an amount to withdraw");
                    input = in.next();
                    try {
                        System.out.println("Successfully withdrew €" + input
                                + "\nNew Balance: €" + bank.withdraw(accNum,Double.parseDouble(input)));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Input. Try again");
                        break;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "balance":
                    System.out.println("Your balance is €" + bank.getBalance(accNum));
                    break;

                case "statement":
                    System.out.println("Displaying statement:");
                    System.out.println("Would you like to set a custom date for the statement to run to? (Y/N)");
                    input = in.next();

                    if(input.toLowerCase().trim().equals("y")){
                        System.out.println("Enter custom date in form dd/mm/yyyy");
                        input = in.next();

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            Date choice = sdf.parse(input);
                            System.out.println(bank.getStatement(accNum,choice).toString());
                            break;
                        } catch (ParseException e) {
                            System.out.println("We could not understand your input, using default");
                            System.out.println(bank.getStatement(accNum).toString());
                            break;
                        }

                    }else {
                        if (input.toLowerCase().trim().equals("n")) {
                            System.out.println("Using default of past 6 months");
                            System.out.println(bank.getStatement(accNum).toString());
                            break;
                        } else {
                            System.out.println("Input not recognised, using default");
                            System.out.println(bank.getStatement(accNum).toString());
                            break;
                        }
                    }
                case "transfer":

                    while (true){
                        System.out.println("Input account number you want to transfer money :");
                        input = in.next();
                        if (bank.checkAcc(Integer.parseInt(input))){
                            accNum1 = Integer.parseInt(input);
                            break;
                        }
                        else{
                            System.out.println("Account doesn't exist");
                        }

                    }
                    double kq = 0.0;
                    while (true) {
                        System.out.println("Input amount of money :");
                        input = in.next();
                        kq = bank.transfer(accNum,accNum1, Double.parseDouble(input));
                        if (kq==-1.0) System.out.println("Your account has not enough money!! Please input another amount!!");
                        else break;
                    }
                    if (kq == 0.0) System.out.println("Failure in transfering!!");
                    else System.out.println("Transfer successfully!!");
                    break;
                case "exit":
                    System.out.println("Have a nice day");
                    System.exit(0);
                    break;

                case "exitbank": //Secret test option to shut down the bank process remotely
                    try { bank.exit(); } catch (RemoteException | NotBoundException e) {}

                default:
                    System.out.println("Input not recognised, please try again");
                    break;
            }

            loggedIn = bank.checkSessionId(sessionId);
        }

        System.out.println("Session has expired. Please restart to complete any further action");
        System.exit(0);
    }
}



