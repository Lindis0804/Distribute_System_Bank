package interfaces;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import bank.Statement;

/**
 * Created by AMCBR on 06/02/2017.
 */
public interface OperationsInterface extends Remote {

    int login(String usr, String pass) throws RemoteException, SQLException;

    double deposit(int accNum,double amt) throws RemoteException, SQLException; //return new balance

    double withdraw(int accNum,double amt) throws RemoteException, SQLException; // return new balance
    boolean checkAcc(int accNum) throws RemoteException,SQLException;
    double transfer(int accNum1,int accNum2,double amt) throws RemoteException,SQLException;
    double getBalance(int accNum) throws RemoteException, SQLException;

    boolean checkSessionId(UUID id) throws RemoteException;

    void exit() throws RemoteException, NotBoundException;

    Statement getStatement(int accNum) throws RemoteException;

    Statement getStatement(int accNum,Date d) throws RemoteException;

}
