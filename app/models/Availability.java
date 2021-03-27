package models;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import databaseServices.Connect;
import databaseServices.exceptions.RecordNotFoundException;

public class Availability extends Connect {

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
    try {      
      String query = String.format(
        "SELECT * FROM %s WHERE facility_id = %d",
        tableName, facilityId
      );

      List<Availability> availabilities = new ArrayList<Availability>();

      List<Availability> rs = executeQuery(query);
      for (Availability availability : rs) {
        if (availability.isInDaysSelected(daysSelected)) {
          availabilities.add(availability);
        }
      }

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


  private boolean isInDaysSelected(Integer[] daysSelected) {
    return Arrays.stream(getDays(daysSelected)).anyMatch(this.day::equals);
  }

  private static List<Availability> executeQuery(String query) {
    List<Availability> results = new ArrayList<Availability>();
    
    try {
      setupConnection();
      Statement stmt = getConn().createStatement();
      ResultSet rs = stmt.executeQuery(query);

      Integer rsCount = 0;
      while (rs.next()) {
        results.add(
          new Availability(
            rs.getInt("facility_id"), 
            rs.getString("day"), 
            rs.getTime("start_time"),
            rs.getTime("end_time")
          )
        );
        rsCount++;
      }

      if (rsCount == 0) {
        throw new RecordNotFoundException();
      }

      closeConnection(rs, stmt);
    } 
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return results;
  }
}
