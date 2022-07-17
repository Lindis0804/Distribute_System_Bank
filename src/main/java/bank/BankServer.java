package bank;

import database.testdatabase;
import interfaces.OperationsInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by AMCBR on 06/02/2017.
 */
public class BankServer extends UnicastRemoteObject implements OperationsInterface {
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final long FIVE_MINUTES = 5*60*1000; //5 minutes in milliseconds

    static String rmiName = "BankServer";
    static private int rmiPort;
    static Registry registry;
    private ArrayList<Account> users = new ArrayList<>();
    private HashMap<UUID, SessionId> activeIds = new HashMap<>();
    static Account activeAcc;


    public BankServer() throws RemoteException {
        super();

        Account a1 = new Account("user1", "pass1", 100);
        Account a2 = new Account("user2", "pass2", 250);
        Account a3 = new Account("user3", "pass3", 500);

        users.add(a1);
        users.add(a2);
        users.add(a3);
    }

    /**
     * checks if a users details exist, if yes then log them in
     * @param usr - username
     * @param pass - password
     * @return true if logged in, else false
     */
    public boolean login(String usr, String pass) throws SQLException {
//
//        for (Account a : users)
//            if (a.getName().equals(usr) && a.getPassword().equals(pass)) {
//                activeAcc = a;
//                return true;
//            }
//
//        return false;
        boolean check = false;
        int count = 0;
        try {
            /*Connection connection = testdatabase.getConnection();
            java.sql.Statement st = connection.createStatement();*/
            Connection connection = testdatabase.getConnection();
            java.sql.Statement statement = connection.createStatement();
            String sql = "SELECT * FROM user_account where name = '" + usr + "' and password = '" + pass+"'";
            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                //  System.out.println(rs.getInt("id");
                int accountNum = rs.getInt("accountNum");
                System.out.print(accountNum);
                String name = rs.getString("name");
                System.out.println(name);
                String password = rs.getString("password");
                System.out.println(password);
                double balance = rs.getDouble("balance");
                System.out.println(balance);
                activeAcc = new Account(name,password,accountNum,balance);
                    System.out.println(accountNum+" "+name);
                    count++;
                }
            if (count>0) check = true;
            testdatabase.closeConnection(connection);
        }
        catch(SQLException e){
            e.printStackTrace();
            }
        return check;
    }

    /***
     * deposits an amount in the users balance
     * @param amt
     * @return new user balance
     */
    public double deposit(double amt) throws SQLException {
        System.out.println("Deposite");
        Connection connection = testdatabase.getConnection();
        java.sql.Statement statement = connection.createStatement();
        Transaction t = new Transaction(amt, activeAcc.getAccountNum(),0, "Deposit",new Date());
        activeAcc.depositTransaction(t);
        String sql1 = "INSERT INTO transaction (Id, accountNum1, accountNum2, amount, transactionType, transDate) VALUES (NULL, "+activeAcc.getAccountNum()+", NULL, "+amt+", '"+t.getTransactionType()+"', '"+yearFormat.format(t.transDate)+"')";
        String sql = "Update user_account set balance = "+activeAcc.getBalance()+" where accountNum = '"+activeAcc.getAccountNum()+"'";
        int res = statement.executeUpdate(sql1);
        int kq = statement.executeUpdate(sql);
        testdatabase.closeConnection(connection);
        return activeAcc.getBalance();
    }

    /**
     * withdraws an amount from the user balance
     * @param amt
     * @return new user balance
     */
    public double withdraw(double amt) throws SQLException {
        System.out.println("Withdraw");
        Connection connection = testdatabase.getConnection();
        java.sql.Statement statement = connection.createStatement();
        Transaction t = new Transaction(amt, activeAcc.getAccountNum(),0, "Withdraw",new Date());
        activeAcc.withdrawTransaction(t);
        String sql1 = "INSERT INTO transaction (Id, accountNum1, accountNum2, amount, transactionType, transDate) VALUES (NULL, "+activeAcc.getAccountNum()+", NULL, "+amt+", '"+t.getTransactionType()+"', '"+yearFormat.format(t.transDate)+"')";
        String sql = "Update user_account set balance = "+activeAcc.getBalance()+" where accountNum = '"+activeAcc.getAccountNum()+"'";
        int kq1 = statement.executeUpdate(sql1);
        int kq = statement.executeUpdate(sql);
        testdatabase.closeConnection(connection);
        return activeAcc.getBalance();
    }
    /*
    * transfer money to another accNum
    * @param accNum
    * @param amt
    * */
    public boolean checkAcc(int accNum) throws SQLException {

        Connection connection = testdatabase.getConnection();
        java.sql.Statement statement = connection.createStatement();
        String sql = "Select * from user_account where accountNum = "+ accNum;
        System.out.println(sql);
        ResultSet rs = statement.executeQuery(sql);
        boolean check = false;
        while (rs.next()) check = true;
        testdatabase.closeConnection(connection);
        return check;

    }
    public double transfer(int accNum, double amt) throws SQLException{
        System.out.println("Transfer");
        //tạo transaction
        Transaction t = new Transaction(amt,activeAcc.getAccountNum(),accNum,"Transfer",new Date());
        if (activeAcc.transferTransaction(t) == 0.0) return -1.0;
        Connection connection = testdatabase.getConnection();
        java.sql.Statement statement = connection.createStatement();
        //cập nhật lại tài khoản được chuyển
        String sql = "Select balance from user_account where accountNum ="+accNum;
        ResultSet rs = statement.executeQuery(sql);
        int kq = 0;
        rs.next();
        double balance = rs.getDouble("balance");
        balance += amt;
        sql = "Update user_account set balance = " + balance + " where accountNum = " + accNum;
        kq = statement.executeUpdate(sql);

        //cập nhật lại tài khoản đã chuyển tiền
        activeAcc.transferTransaction(t);
        sql = "INSERT INTO transaction (Id, accountNum1, accountNum2, amount, transactionType, transDate) VALUES (NULL, "+activeAcc.getAccountNum()+", "+accNum+", "+amt+", '"+t.getTransactionType()+"', '"+yearFormat.format(t.transDate)+"')";
        kq = statement.executeUpdate(sql);
        sql = "Update user_account set balance = "+activeAcc.getBalance()+" where accountNum = "+activeAcc.getAccountNum();
        kq = statement.executeUpdate(sql);
        testdatabase.closeConnection(connection);
        if (kq >0) return amt;
        else return 0.0;
    }
    /**
     * Get current active user balance
     * @return
     */
    public double getBalance() {
        System.out.println("Balance: " + activeAcc.getBalance()+"Name :"+activeAcc.getName()+"Password :"+activeAcc.getPassword()+"AccNum :"+activeAcc.getAccountNum());
        return activeAcc.getBalance();
    }

    /**
     * Get statement of user transactions
     * @return
     */
    public Statement getStatement() {

        return activeAcc.getStatement();
    }

    /**
     * Get copy of user interaction back to desired date
     * @param d
     * @return
     */
    public Statement getStatement(Date d) {

        return activeAcc.getStatement(d);
    }


    /***
     * Check if the current UUID is still valid
     * @param id
     * @return true if session ID still valid, else false
     * @throws RemoteException
     */
    public boolean checkSessionId(UUID id) throws RemoteException {

        System.out.println("Active IDs" + activeIds.toString());
        if(activeIds.containsKey(id)) {

            System.out.println("");
            SessionId s = activeIds.get(id);
            long fiveAgo = System.currentTimeMillis() - FIVE_MINUTES;

            System.out.println("Time Elapsed: " + (s.getTimeStamp() - fiveAgo));
            if (s.getTimeStamp() < fiveAgo) {
                activeIds.remove(s.getId()); //remove expired IDs
                return false; //No longer valid
            }

            return true;
        }else{
            activeIds.put(id, new SessionId(id)); //Add newly logged in users to the map
            return true;
        }
    }

    /***
     * Unbind our rmi Host from the registry, stop exporting and shut down the process
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void exit() throws RemoteException, NotBoundException {

        // Unregister ourself
        registry.unbind(rmiName);

        // Unexport; this will also remove us from the RMI runtime
        UnicastRemoteObject.unexportObject(this, true);
        System.exit(0);
    }


    /**
     * Starts up rmi Server on specified port. If none available it defaults to 1099
     * @param args
     */
    public static void main(String[] args) { //CLI Args: Name, Password, Option

        //parse port
        try{
            rmiPort = Integer.parseInt(args[0]);
            System.out.println("RMI Port set to "+rmiPort);
        }catch(ArrayIndexOutOfBoundsException e){
            rmiPort = 1099; //Default to port 1099
            System.out.println("RMI Port defaulting to "+rmiPort);
        }

        try {

            //Set up RMI server

            OperationsInterface bank = new BankServer();
            registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind(rmiName, bank);
            System.out.println("BankServer bound");

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
