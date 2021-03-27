package models;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import databaseServices.Connect;
import databaseServices.exceptions.BookingUnavailableException;
import databaseServices.exceptions.RecordNotFoundException;
import databaseServices.exceptions.UnacceptableInputException;

public class Booking extends Connect {

  static String tableName = "bookings";  

  Integer id;
  Integer facilityId;
  String day;
  Time startTime;
  Time endTime;

  Booking(Integer id, Integer facilityId, String day, Time startTime, Time endTime) {
    this.id = id;
    this.facilityId = facilityId;
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static Integer create(String facilityName, Integer dayInteger, Time startTime, Time endTime) {
    Integer bookingId = null;

    try {
      Integer facilityId = Facility.getIdFromName(facilityName);
      String dayString = Availability.getDayMapping(dayInteger);
      validateStartTimeEndTime(startTime, endTime);
      validateBookingAvailability(facilityId, dayInteger, startTime, endTime);

      String query = String.format(
        "INSERT INTO %s (facility_id, day, start_time, end_time) VALUES (%d, '%s', '%s', '%s');",
        tableName, facilityId, dayString, startTime, endTime
      );
      bookingId = executeUpdate(query);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return bookingId;
  }

  private static void validateBookingAvailability(Integer facilityId, Integer dayInteger, Time startTime, Time endTime) throws BookingUnavailableException {
    if (Availability.isBookingPossible(facilityId, dayInteger, startTime, endTime) == false) {
      throw new BookingUnavailableException("Not within facility's availability");
    }
    if (Booking.hasBookingClashes(facilityId, dayInteger, startTime, endTime)) {
      throw new BookingUnavailableException("No booking slot available");
    }
  }

  private static void validateStartTimeEndTime(Time startTime, Time endTime) {
    try {
      if (startTime.before(endTime) == false) {
        throw new UnacceptableInputException("startTime is not before endTime");
      }
    } 
    catch (UnacceptableInputException e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }

  static boolean hasBookingClashes(Integer facilityId, Integer dayInteger, Time startTime, Time endTime) {
    boolean isPossible = false;

    String query = String.format(
        "SELECT * FROM %s WHERE facility_id = %d",
        tableName, facilityId
      );

    List<Booking> rs =  executeQuery(query);
    for (Booking booking : rs) {
      if (booking.isInDaysSelected(new Integer[]{dayInteger})) {
        if (startTime.before(booking.startTime)) { break; }
        if (endTime.after(booking.endTime)) { break; }
        isPossible = true;
      }
    }

    return isPossible;
  }

  private boolean isInDaysSelected(Integer[] daysSelected) {
    return Arrays.stream(Availability.getDays(daysSelected)).anyMatch(this.day::equals);
  }

  private static Integer executeUpdate(String query) {
    Integer id = null;

    try {
      setupConnection();
      Statement stmt = getConn().createStatement();
      id = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
      closeConnection(null, stmt);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return id;
  }

  private static List<Booking> executeQuery(String query) {
    List<Booking> results = new ArrayList<Booking>();
    
    try {
      setupConnection();
      Statement stmt = getConn().createStatement();
      ResultSet rs = stmt.executeQuery(query);

      Integer rsCount = 0;
      while (rs.next()) {
        results.add(
          new Booking(
            rs.getInt("id"), 
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
