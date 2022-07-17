package Model;

public class User {
    private String numberAccount;
    private String username;
    private String password;
    private int balance;
    public User(){
        super();
    }
    public User(String numberAccount, String username, String password, int balance) {
        this.numberAccount = numberAccount;
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public User(String numberAccount, String username, String password) {
        this.numberAccount = numberAccount;
        this.username = username;
        this.password = password;
    }

    public User(String numberAccount, int balance) {
        this.numberAccount = numberAccount;
        this.balance = balance;
    }

    public String getNumberAccount() {
        return numberAccount;
    }

    public void setNumberAccount(String numberAccount) {
        this.numberAccount = numberAccount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
