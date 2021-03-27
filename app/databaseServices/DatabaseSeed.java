package databaseServices;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSeed extends Connect {
  public static void main(String args[]) {
    try {
      clearTables();
      populateTables();      
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }

  private static void clearTables() {
    
    List<String> tableNames = new ArrayList<String>();
    tableNames.add("monitors");
    tableNames.add("bookings");
    tableNames.add("availabilities");
    tableNames.add("facilities");

    for (String tableName : tableNames) {
      try {
        executeUpdate("DELETE FROM " + tableName);
      } 
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

  private static void populateTables() {
    
    List<String> sqls = new ArrayList<String>();
    
    sqls.add(
      "INSERT INTO facilities (id, subtype, name) VALUES " +
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
        executeUpdate(sql);
      } 
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

  private static void executeUpdate(String query) {
    try {
      setupConnection();
      Statement stmt = getConn().createStatement();

      stmt.executeUpdate(query);

      closeConnection(null, stmt);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }
}
