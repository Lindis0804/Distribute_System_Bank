package DAO;

import Model.User;
import database.testdatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserDAO implements DAOInterface<User>{
    public static UserDAO getInstance(){
        return new UserDAO();
    }
    @Override
    public boolean add(User user) throws SQLException {
        try {
            Connection connection = testdatabase.getConnection();
            Statement st = connection.createStatement();
            String sql = "INSERT INTO account (accountNumber, username, password, balance) VALUES ('" + user.getNumberAccount() + "', '" + user.getUsername() + "', '" + user.getPassword() + "', '" + 0 + "')";
            int kq = st.executeUpdate(sql);
            System.out.println("Số hàng được thêm :"+kq);
            testdatabase.closeConnection(connection);
        }
        catch(SQLException e){
            System.out.println("Error in adding");
        }
        return true;
    }

    @Override
    public boolean update(User user) throws SQLException {
        Connection connection = testdatabase.getConnection();
        Statement st = connection.createStatement();
        String sql = "Update account set balance ="+user.getBalance()+" where accoutNumber = "+user.getNumberAccount();
        int kq = st.executeUpdate(sql);
        System.out.println("Số hàng thay đổi :"+kq);
        testdatabase.closeConnection(connection);
        return false;
    }

    @Override
    public int xoa(User user) {
        return 0;
    }

    @Override
    public ArrayList<User> selectAll() {
        return null;
    }

    @Override
    public User selectByName(User user) throws SQLException {
        Connection connection = testdatabase.getConnection();
        Statement st = connection.createStatement();
        String sql = "Select accountNumber from account";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            String accountNumber = rs.getString("accountNumber");
        }
        testdatabase.closeConnection(connection);
        return null;
    }

    @Override
    public ArrayList<User> selectByCondition(String condition) {
        return null;
    }
}
