import bank.Account;
import bank.BankServer;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RMISecurityException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by AMCBR on 14/02/2017.
 */
public class BankServerTest {

    BankServer bank;
    Account a1;
    Account a2;
    ArrayList<Account> users;

    @Before
    public void before() throws RemoteException{
        bank = new BankServer();

        a1 = new Account("user1", "pass1", 100);//User exists
        a2 = new Account("asbh", "asdd", 250);//User doesnt exist

        users = new ArrayList<>();
        users.add(a1);
        users.add(a2);

    }

    @Test
    public void loginSuccessTest(){

        assertTrue(bank.login(a1.getName(), a1.getPassword()));
    }

    @Test
    public void loginFailTest(){

        assertFalse(bank.login(a2.getName(), a2.getPassword()));
    }

    @Test
    public void depositTest(){

        double amt = 100;
        double actual = a1.getBalance() + amt;

        bank.login(a1.getName(), a1.getPassword());

        assertTrue(Double.compare(bank.deposit(amt), actual) == 0);

    }

    @Test
    public void withdrawTest(){

        double amt = 100;
        double actual = a1.getBalance() - amt;

        bank.login(a1.getName(), a1.getPassword());

        assertTrue(Double.compare(bank.withdraw(amt), actual) == 0);
    }

    @Test
    public void checkSessionId() throws RemoteException{

        String testString="JUST_A_TEST_STRING";
        UUID testUUID = UUID.nameUUIDFromBytes(testString.getBytes());
        bank.checkSessionId(testUUID);
        assertTrue(bank.checkSessionId(testUUID));
    }

}
