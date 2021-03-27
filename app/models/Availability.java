package models;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import databaseServices.Connect;
import databaseServices.caches.AvailabilityCache;
import databaseServices.exceptions.RecordNotFoundException;
import databaseServices.exceptions.UnacceptableInputException;

public class Availability extends Connect {

  static String tableName = "availabilities";

  public static Integer MONDAY    = 1;
  public static Integer TUESDAY   = 2;
  public static Integer WEDNESDAY = 3;
  public static Integer THURSDAY  = 4;
  public static Integer FRIDAY    = 5;
  public static Integer SATURDAY  = 6;
  public static Integer SUNDAY    = 7;

  public static HashMap<Integer, String> dayMappings =  new HashMap<Integer, String>();
  static {
    dayMappings.put(MONDAY, "monday");
    dayMappings.put(TUESDAY, "tuesday");
    dayMappings.put(WEDNESDAY, "wednesday");
    dayMappings.put(THURSDAY, "thursday");
    dayMappings.put(FRIDAY, "friday");
    dayMappings.put(SATURDAY, "saturday");
    dayMappings.put(SUNDAY, "sunday");
  }

  public Integer facilityId;
  public String day;
  public LocalTime startTime, endTime;

  Availability(Integer facilityId, Integer dayInteger,LocalTime startTime, LocalTime endTime) {
    this.facilityId = facilityId;
    this.day = getDayMapping(dayInteger);
    this.startTime = startTime;
    this.endTime = endTime;
  }

  Availability(Integer facilityId, String dayString, LocalTime startTime, LocalTime endTime) {
    this.facilityId = facilityId;
    this.day = dayString;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public String toString() {
    return String.format(
      "facility_id: %d, day: %s, start: %s, end: %s", 
      facilityId, day, startTime.toString(), endTime.toString()
    );
  }

  public static List<Availability> getAvailabilitiesForFacility(Integer facilityId, Integer[] daysSelected) {   
    if (AvailabilityCache.cache.containsKey(facilityId))
      return AvailabilityCache.cache.get(facilityId);

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

  static boolean isBookingPossible(Integer facilityId, Integer dayInteger, LocalTime startTime, LocalTime endTime) {
    boolean isPossible = false;

    String query = String.format(
      "SELECT * FROM %s WHERE facility_id = %d",
      tableName, facilityId
    );

    try {
      List<Availability> availabilities = 
        AvailabilityCache.cache.containsKey(facilityId) ? 
          AvailabilityCache.cache.get(facilityId) : 
          executeQuery(query);

      for (Availability availability : availabilities) {
        if (availability.isInDaysSelected(new Integer[]{dayInteger})) {
          if (startTime.isBefore(availability.startTime)) { break; }
          if (endTime.isAfter(availability.endTime)) { break; }
          isPossible = true;
          break;
        }
      }
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return isPossible;
  }


  static String getDayMapping(Integer dayInteger) {
    if (dayMappings.get(dayInteger) == null) {
      try {
        throw new UnacceptableInputException("dayInteger value is unacceptable");
      } 
      catch (UnacceptableInputException e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }

    return dayMappings.get(dayInteger);
  }

  static Integer getDayMapping(String dayString) {
    if (reversedDayMappings().get(dayString) == null) {
      try {
        throw new UnacceptableInputException("dayString value is unacceptable");
      } 
      catch (UnacceptableInputException e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }

    return reversedDayMappings().get(dayString);
  }


  static String[] getDays(Integer[] dayIntegers) {
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

  private static HashMap<String, Integer> reversedDayMappings() {
    HashMap<String, Integer> reversedDayMappings = new HashMap<String, Integer>();
    for(HashMap.Entry<Integer, String> entry : dayMappings.entrySet()){
      reversedDayMappings.put(entry.getValue(), entry.getKey());
    }
    return reversedDayMappings;
  }

  private static List<Availability> executeQuery(String query) throws RecordNotFoundException {
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
            rs.getTime("start_time").toLocalTime(),
            rs.getTime("end_time").toLocalTime()
          )
        );
        rsCount++;
      }

      if (rsCount == 0) {
        throw new RecordNotFoundException();
      }

      closeConnection(rs, stmt);
    } 
    catch (RecordNotFoundException e) {
      throw e;
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    AvailabilityCache.put(results);

    return results;
  }
}
