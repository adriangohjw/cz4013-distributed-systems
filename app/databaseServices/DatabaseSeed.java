package databaseServices;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSeed {
  public static void main(String args[]) {

    Connection c = null;
    Statement stmt = null;

    try {
      Class.forName(Connect.DRIVER_NAME);
      c = DriverManager.getConnection(Connect.DATABASE_URI, Connect.USERNAME, Connect.PASSWORD);
      stmt = c.createStatement();

      clearTables(c, stmt);
      populateTables(c, stmt);

      stmt.close();         
      c.close();
      
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }

  private static void clearTables(Connection c, Statement stmt) {
    
    List<String> tableNames = new ArrayList<String>();
    tableNames.add("monitors");
    tableNames.add("bookings");
    tableNames.add("availabilities");
    tableNames.add("facilities");

    for (String tableName : tableNames) {
      try {
        stmt.executeUpdate("DELETE FROM " + tableName);
      } 
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

  private static void populateTables(Connection c, Statement stmt) {
    
    List<String> sqls = new ArrayList<String>();
    
    sqls.add(
      "INSERT INTO facilities (id, type, name) VALUES " +
      "(1, 'meeting_room', 'Meeting Room 1'), " + 
      "(2, 'lecture_theatre', 'LT 1'); "
    );

    sqls.add(
      "INSERT INTO availabilities (facility_id, day, start_time, end_time) VALUES " +
      "(1, 'monday', '08:00:00', '18:00:00'), " + 
      "(1, 'tuesday', '08:00:00', '18:00:00'), " + 
      "(1, 'wednesday', '08:00:00', '18:00:00'), " + 
      "(1, 'thursday', '08:00:00', '18:00:00'), " + 
      "(1, 'friday', '08:00:00', '18:00:00'), " + 
      "(1, 'saturday', '08:00:00', '18:00:00'), " + 
      "(1, 'sunday', '08:00:00', '18:00:00'), " +  
      "(2, 'monday', '08:00:00', '18:00:00'), " + 
      "(2, 'tuesday', '08:00:00', '18:00:00'), " + 
      "(2, 'wednesday', '08:00:00', '18:00:00'), " + 
      "(2, 'thursday', '08:00:00', '18:00:00'), " + 
      "(2, 'friday', '08:00:00', '18:00:00'), " + 
      "(2, 'saturday', '08:00:00', '18:00:00'), " + 
      "(2, 'sunday', '08:00:00', '18:00:00');" 
    );

    for (String sql : sqls) {
      try {
        stmt.executeUpdate(sql);
        System.out.println("Tables populated successfully");
      } 
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }
}
