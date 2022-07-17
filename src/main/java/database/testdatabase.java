package database;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class testdatabase {
    public static Connection getConnection() throws SQLException {
         Connection c = null;
         try {
             Driver driver = new Driver();
             DriverManager.registerDriver(new Driver());
             String url = "jdbc:mySQL://localhost:3306/bank";
             String username = "root";
             String password = "";
             //tạo kết nối
             c = DriverManager.getConnection(url,username,password);

         }
         catch(SQLException e){
             System.out.println("Error in connection!!");
         }
        return c;
    }
    public static void closeConnection(Connection c) throws SQLException {
         if (c!=null){
             c.close();
         }
         else{
             System.out.println("Connection doesn't exist!!");
         }
    }
    public static void printfInfo(Connection c) throws SQLException {
        if (c!=null){
            System.out.println(c.getMetaData().toString());
        }
    }
}
