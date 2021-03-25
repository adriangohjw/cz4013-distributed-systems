package models;
import java.sql.*;

import databaseServices.Connect;

public class Facility {

  static Connection conn;
  static String tableName = "facilities";

  Facility() {
  }

  private static void setupConnection() {
    try {
      conn = DriverManager.getConnection(Connect.DATABASE_URI, Connect.USERNAME, Connect.PASSWORD);
    } catch (SQLException e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    } 
  }

  // sample Query
  public static String getName(Integer id) {

    setupConnection();
    
    try {
      String result = null;

      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE id = " + String.valueOf(id));
      while ( rs.next() ) {
        result = rs.getString("name");
      }

      rs.close();
      stmt.close();
      conn.close();

      return result;
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return null;
    }

  }

}
