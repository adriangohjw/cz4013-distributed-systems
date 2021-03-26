package models;
import java.sql.*;
import java.time.LocalDateTime;

import databaseServices.Connect;

public class Monitor {

  static Connection conn;
  static String tableName = "monitors";

  Monitor() {
  }

  public static boolean create(String facilityName, String address, Integer port, Integer lengthMin) {
    boolean isCreated = false;
    
    Integer facilityId = Facility.getIdFromName(facilityName);
    LocalDateTime start_time = LocalDateTime.now();
    LocalDateTime end_time = start_time.plusMinutes(lengthMin);

    try {
      setupConnection();

      Statement stmt = conn.createStatement();
      String query = String.format(
        "INSERT INTO %s (facility_id, address, host, start_time, end_time) VALUES (%d, '%s', %d, '%s', '%s');",
        tableName, facilityId, address, port, start_time, end_time
      );
      stmt.executeUpdate(query);

      isCreated = true;

      closeConnection(null, stmt, conn);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return isCreated;
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
      if (rs != null) {
        rs.close();
      }
      stmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }

}
