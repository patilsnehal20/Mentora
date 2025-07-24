package com.academic.examapp.main;
import com.academic.examapp.model.SplashFrame;
import java.sql.*;
public class Buffer1 {
   public static void main(String[] args) {
       try {
           // Establish DB connection
           DBConnection.getConnection();
           // Launch login
           new SplashFrame();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}
class DBConnection {
   private static Connection con;
   public static Connection getConnection() throws ClassNotFoundException, SQLException {
       if (con == null || con.isClosed()) {
           Class.forName("com.mysql.cj.jdbc.Driver");
           con = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/examdb", "root", "Ssu@2005"
           );
           System.out.println("MySQL connection established.");
       }
       return con;
   }
}
