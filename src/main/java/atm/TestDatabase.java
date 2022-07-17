package atm;

import DAO.UserDAO;
import Model.User;
import database.testdatabase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class TestDatabase {
     public static void main(String[] args) throws SQLException {
         try {
             Connection connection = testdatabase.getConnection();
             Statement st = connection.createStatement();
             String sql = "INSERT INTO account (accountNumber, username, password, balance) VALUES ('8','hieu','hieu',200)";
             int kq = st.executeUpdate(sql);
             System.out.println("Số hàng được thêm :" + kq);
             testdatabase.closeConnection(connection);
         }
         catch(SQLException e){
             e.printStackTrace();
         }
     }
}
