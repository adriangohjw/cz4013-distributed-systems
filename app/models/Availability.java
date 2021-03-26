package models;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import databaseServices.Connect;

public class Availability {

  static Connection conn;
  static String tableName = "availabilities";

  public static Integer MONDAY    = 1;
  public static Integer TUESDAY   = 2;
  public static Integer WEDNESDAY = 3;
  public static Integer THURSDAY  = 4;
  public static Integer FRIDAY    = 5;
  public static Integer SATURDAY  = 6;
  public static Integer SUNDAY    = 7;

  public Integer facilityId;
  public String day;
  public Time startTime, endTime;

  Availability(Integer facilityId, Integer dayInteger,Time startTime, Time endTime) {
    this.facilityId = facilityId;
    this.day = getDayMapping(dayInteger);
    this.startTime = startTime;
    this.endTime = endTime;
  }

  Availability(Integer facilityId, String dayString,Time startTime, Time endTime) {
    this.facilityId = facilityId;
    this.day = dayString;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public String toString() {
    return String.format(
      "facility_id: %d, day: %s, start: %d:%d, end: %d:%d", 
      facilityId, day, getStartTimeHour(), getStartTimeMin(), getEndTimeHour(), getEndTimeMin()
      );
  }

  public Integer getStartTimeHour() {
    return startTime.getHours();
  }

  public Integer getStartTimeMin() {
    return startTime.getMinutes();
  }

  public Integer getEndTimeHour() {
    return endTime.getHours();
  }

  public Integer getEndTimeMin() {
    return endTime.getMinutes();
  }


  public static List<Availability> getAvailabilitiesForFacility(Integer facilityId, Integer[] daysSelected) {

    setupConnection();
    
    try {
      List<Availability> availabilities = new ArrayList<Availability>();
      
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE facility_id = " + String.valueOf(facilityId));

      while ( rs.next() ) {
        if (Arrays.asList(getDays(daysSelected)).contains(rs.getString("day"))) {
          availabilities.add(
            new Availability(
              rs.getInt("facility_id"), rs.getString("day"), rs.getTime("start_time"), rs.getTime("end_time")
            )
          );
        }
      }
        
      closeConnection(rs, stmt, conn);

      return availabilities;
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return null;
    }

  }


  private static String getDayMapping(Integer dayInteger) {
    HashMap<Integer, String> dayMappings = new HashMap<Integer, String>();
    dayMappings.put(MONDAY, "monday");
    dayMappings.put(TUESDAY, "tuesday");
    dayMappings.put(WEDNESDAY, "wednesday");
    dayMappings.put(THURSDAY, "thursday");
    dayMappings.put(FRIDAY, "friday");
    dayMappings.put(SATURDAY, "saturday");
    dayMappings.put(SUNDAY, "sunday");

    return dayMappings.get(dayInteger);
  }


  private static String[] getDays(Integer[] dayIntegers) {
    String[] results = new String[dayIntegers.length];
    
    Integer index = 0;
    for (Integer dayInteger : dayIntegers) {
      results[index] = getDayMapping(dayInteger);
      index++;
    }

    return results;
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
