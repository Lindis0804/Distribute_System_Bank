/**
 * Created by AMCBR on 14/02/2017.
 */
import bank.Account;
import bank.Transaction;
import org.junit.*;

import static org.junit.Assert.assertTrue;

public class AccountTest {

    static Account a;

    @Before
    public void before(){
        a = new Account("Kat", "Cats", 1000);
    }

    @Test
    public void depositTransactionTest(){
        double before = a.getBalance();
        double deposit = 200.0;
        double newBalance = before + deposit;
        Transaction t = new Transaction(deposit, a.getAccountNum(), "Deposit");
        a.depositTransaction(t);
        assertTrue(Double.compare(a.getBalance(), newBalance) == 0);
    }

    @Test
    public void withdrawTransactionTest(){
        double before = a.getBalance();
        double withdraw = 200.0;
        double newBalance = before - withdraw;
        Transaction t = new Transaction(withdraw, a.getAccountNum(), "Withdraw");
        a.withdrawTransaction(t);
        assertTrue(Double.compare(a.getBalance(), newBalance) == 0);
    }

}
