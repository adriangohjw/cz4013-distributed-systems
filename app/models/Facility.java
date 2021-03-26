package models;
import java.sql.*;
import java.util.List;

import databaseServices.Connect;
import databaseServices.exceptions.RecordNotFoundException;

public class Facility {

  static Connection conn;
  static String tableName = "facilities";

  Facility() {
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

      closeConnection(rs, stmt, conn);

      return result;
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return null;
    }

  }


  public static List<Availability> getAvailabilities(String name, Integer[] daysSelected) {
    return Availability.getAvailabilitiesForFacility(getIdFromName(name), daysSelected);
  }


  public static Integer getIdFromName(String name) {
    Integer result = null;

    try {
      setupConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT id FROM " + tableName + " WHERE name = '" + name + "';");
      Integer rsCount = 0;

      while (rs.next()) {
        result = rs.getInt("id");
        rsCount++;
      }

      if (rsCount == 0) {
        throw new RecordNotFoundException();
      }

      closeConnection(rs, stmt, conn);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return result;
  }


  private static void setupConnection() {
    try {
      conn = DriverManager.getConnection(Connect.DATABASE_URI, Connect.USERNAME, Connect.PASSWORD);
    } catch (SQLException e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    } 
  }

  private static void closeConnection(ResultSet rs, Statement stmt, Connection conn) {
    try {
      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }

}
