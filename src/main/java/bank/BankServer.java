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
import java.util.*;

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
    static Map<Integer,Account> activeAcc = new HashMap<>();


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
     *
     * @param usr  - username
     * @param pass - password
     * @return true if logged in, else false
     */
    public int login(String usr, String pass) throws SQLException {
        boolean check = false;
        int res = 0;
        try {
            /*Connection connection = testdatabase.getConnection();
            java.sql.Statement st = connection.createStatement();*/
            Connection connection = testdatabase.getConnection();
            java.sql.Statement statement = connection.createStatement();
            String sql = "SELECT * FROM user_account where name = '" + usr + "' and password = '" + pass+"'";
            System.out.println(sql);
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
                //  System.out.println(rs.getInt("id");
                int accountNum = rs.getInt("accountNum");
                System.out.print(accountNum);
                String name = rs.getString("name");
                System.out.println(name);
                String password = rs.getString("password");
                System.out.println(password);
                double balance = rs.getDouble("balance");
                System.out.println(balance);
                activeAcc.put(accountNum,new Account(name,password,accountNum,balance));
                System.out.println(accountNum+" "+name+" password");
                res = accountNum;
            testdatabase.closeConnection(connection);
        }
        catch(SQLException e){
            res = -1;
            }
        return res;
    }

    /***
     * deposits an amount in the users balance
     * @param amt
     * @return new user balance
     */
    public double deposit(int accNum,double amt) throws SQLException {
        System.out.println("Deposite");
        Connection connection = testdatabase.getConnection();
        java.sql.Statement statement = connection.createStatement();
        Transaction t = new Transaction(amt, accNum,0, "Deposit",new Date());
        activeAcc.get(accNum).depositTransaction(t);
        String sql1 = "INSERT INTO transaction (Id, accountNum1, accountNum2, amount, transactionType, transDate) VALUES (NULL, "+activeAcc.get(accNum).getAccountNum()+", NULL, "+amt+", '"+t.getTransactionType()+"', '"+yearFormat.format(t.transDate)+"')";
        String sql = "Update user_account set balance = "+activeAcc.get(accNum).getBalance()+" where accountNum = '"+activeAcc.get(accNum).getAccountNum()+"'";
        int res = statement.executeUpdate(sql1);
        int kq = statement.executeUpdate(sql);
        testdatabase.closeConnection(connection);
        return activeAcc.get(accNum).getBalance();
    }

    /**
     * withdraws an amount from the user balance
     * @param amt
     * @return new user balance
     */
    public double withdraw(int accNum,double amt) throws SQLException {
        System.out.println("Withdraw");
        Connection connection = testdatabase.getConnection();
        java.sql.Statement statement = connection.createStatement();
        Transaction t = new Transaction(amt, accNum,0, "Withdraw",new Date());
        activeAcc.get(accNum).withdrawTransaction(t);
        String sql1 = "INSERT INTO transaction (Id, accountNum1, accountNum2, amount, transactionType, transDate) VALUES (NULL, "+activeAcc.get(accNum).getAccountNum()+", NULL, "+amt+", '"+t.getTransactionType()+"', '"+yearFormat.format(t.transDate)+"')";
        String sql = "Update user_account set balance = "+activeAcc.get(accNum).getBalance()+" where accountNum = '"+activeAcc.get(accNum).getAccountNum()+"'";
        int kq1 = statement.executeUpdate(sql1);
        int kq = statement.executeUpdate(sql);
        testdatabase.closeConnection(connection);
        return activeAcc.get(accNum).getBalance();
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
    public double transfer(int accNum1, int accNum2, double amt) throws SQLException{
        System.out.println("Transfer");
        //tạo transaction
        Transaction t = new Transaction(amt, accNum1, accNum2,"Transfer",new Date());
        double res = activeAcc.get(accNum1).transferTransaction(t);
        if (res == 0.0) return -1.0;
        Connection connection = testdatabase.getConnection();
        java.sql.Statement statement = connection.createStatement();
        //cập nhật lại tài khoản được chuyển
        activeAcc.get(accNum2).setBalance(activeAcc.get(accNum2).getBalance()+amt);
//        String sql = "Select balance from user_account where accountNum ="+ accNum2;
//        ResultSet rs = statement.executeQuery(sql);
//        int kq = 0;
//        rs.next();
//        double balance = rs.getDouble("balance");
//        balance += amt;
        String sql = "Update user_account set balance = " + activeAcc.get(accNum2).getBalance() + " where accountNum = " + accNum2;
        int kq = statement.executeUpdate(sql);

        //cập nhật lại tài khoản đã chuyển tiền
        sql = "INSERT INTO transaction (Id, accountNum1, accountNum2, amount, transactionType, transDate) VALUES (NULL, "+accNum1+", "+ accNum2 +", "+amt+", '"+t.getTransactionType()+"', '"+yearFormat.format(t.transDate)+"')";
        kq = statement.executeUpdate(sql);
        sql = "Update user_account set balance = "+activeAcc.get(accNum1).getBalance()+" where accountNum = "+accNum1;
        kq = statement.executeUpdate(sql);
        testdatabase.closeConnection(connection);
        if (kq >0) return amt;
        else return 0.0;
    }
    /**
     * Get current active user balance
     * @return
     */
    public double getBalance(int accNum) throws SQLException {
        return activeAcc.get(accNum).getBalance();
    }

    /**
     * Get statement of user transactions
     * @return
     */
    public Statement getStatement(int accNum) {

        return activeAcc.get(accNum).getStatement();
    }

    /**
     * Get copy of user interaction back to desired date
     * @param d
     * @return
     */
    public Statement getStatement(int accNum,Date d) {

        return activeAcc.get(accNum).getStatement(d);
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
