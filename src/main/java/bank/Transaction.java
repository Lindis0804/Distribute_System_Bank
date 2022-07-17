package bank;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AMCBR on 08/02/2017.
 */
public class Transaction implements Serializable {

    double amount = 0;
    int accountNum1 = 0;
    int accountNum2 = 0;
    String transactionType = "";
    Date transDate = new Date();

    public Transaction(double amt, int accNum1,int accNum2, String type,Date date){

        this.amount = amt;
        this.accountNum1 = accNum1;
        this.accountNum2 = accNum2;
        this.transactionType = type;
        this.transDate = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Transaction(int accountNum1, int accountNum2) {
        this.accountNum1 = accountNum1;
        this.accountNum2 = accountNum2;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public void setType(String type) {
        this.transactionType = type;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String info = "\nTransaction Type: " + this.transactionType
                + "\nAmount: "+ this.amount
                +"\nSource: "+this.accountNum1
                +"\nDes: "+this.accountNum2
                + "\nDate: "+ sdf.format(this.transDate) + "\n";

        return info;
    }

}
