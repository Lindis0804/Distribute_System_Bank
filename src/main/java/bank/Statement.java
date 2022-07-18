package bank;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by AMCBR on 06/02/2017.
 */
public class Statement implements Serializable{

    private int accNum;
    private String accName;
    private ArrayList transactions;
    private Date startDate;
    private Date endDate;
    public Statement(int accNum){
        this.accNum = accNum;
        this.transactions = new ArrayList();
    };
    public Statement(int accNum,Date startDate){
        this.accNum = accNum;
        this.startDate = startDate;
        this.transactions = new ArrayList();
    }
    public Statement(Account a){
        this.accName = a.getName();
        this.accNum = a.getAccountNum();
        this.transactions = a.getTransactions();
        this.startDate = getStartDate();
        this.endDate = getEndDate();
    }

    /**
     * Sets custom end date for statement
     * @param a
     * @param endDate
     */
    public Statement(Account a, Date startDate){
        this.accName = a.getName();
        this.accNum = a.getAccountNum();
        this.transactions = a.getTransactions();
        this.startDate = getStartDate();
        this.endDate = endDate;
    }


    /**
     * Set end date of statement to 3 months in past
     * @return
     */
    public Date getEndDate(){ //Gets start data of transaction for the period of 3 months before the current date

        Date pastDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(pastDate);
        c.add(Calendar.MONTH, -3);

        return c.getTime();
    } // returns start Date of bank.Statement, 3 months ago

    public int getAccountNum(){return this.accNum;}  // returns account number associated with this statement

    public Date getStartDate(){return new Date();} // returns end Date of bank.Statement i.e. the current date

    public String getAccoutName(){return this.accName;} // returns name of account holder

    public List getTransations(){return this.transactions;}
    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
    }

    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String info = "Account Number: " + accNum
                + "\nThis statement is for the period " + sdf.format(startDate) + " until " + sdf.format(new Date())
                + "\nTransactions for this period:\n" + transactions.toString() ;

        return info;
    }
    public String toString1(){
        String infor = "Account Number: "+accNum+"\nTransactions history :\n"+transactions.toString();
        return infor;
    }

}
