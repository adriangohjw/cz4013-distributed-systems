package models;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import databaseServices.Connect;
import databaseServices.exceptions.BookingUnavailableException;
import databaseServices.exceptions.RecordNotFoundException;
import databaseServices.exceptions.UnacceptableInputException;

public class Booking extends Connect {

  static String tableName = "bookings";
  public final static int UPDATE_TIMING_ADVANCE = -1;  
  public final static int UPDATE_TIMING_POSTPONE = 1;  

  Integer id;
  Integer facilityId;
  String day;
  LocalTime startTime;
  LocalTime endTime;

  Booking(Integer id, Integer facilityId, String day, LocalTime startTime, LocalTime endTime) {
    this.id = id;
    this.facilityId = facilityId;
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static Integer create(String facilityName, Integer dayInteger, LocalTime startTime, LocalTime endTime) {
    Integer bookingId = null;
    Integer facilityId = null;

    try {
      facilityId = Facility.getIdFromName(facilityName);
      String dayString = Availability.getDayMapping(dayInteger);
      validateStartTimeEndTime(startTime, endTime);
      validateBookingAvailability(facilityId, dayInteger, startTime, endTime, null);

      String query = String.format(
        "INSERT INTO %s (facility_id, day, start_time, end_time) VALUES (%d, '%s', '%s', '%s');",
        tableName, facilityId, dayString, startTime, endTime
      );
      bookingId = executeUpdate(query);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    Monitor.alertActiveListeners(facilityId);

    return bookingId;
  }

  public static boolean updateTiming(Integer id, Integer advanceOrPostpone, Integer offsetMinutes) {
    try {
      validateAdvanceOrPostpone(advanceOrPostpone);
      validateOffsetMinutes(offsetMinutes);
    }
    catch (UnacceptableInputException e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return false;
    }

    Booking booking;
    try {
      booking = getById(id);
    }
    catch (RecordNotFoundException e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return false;
    }

    try {
      Integer dayInteger = Availability.getDayMapping(booking.day);
      LocalTime newStartTime = booking.startTime.plusMinutes(advanceOrPostpone * offsetMinutes);
      LocalTime newEndTime = booking.endTime.plusMinutes(advanceOrPostpone * offsetMinutes);

      validateBookingAvailability(booking.facilityId, dayInteger, newStartTime, newEndTime, booking.id);

      String query = String.format(
        "UPDATE %s SET start_time = '%s', end_time = '%s' WHERE id = %d;",
        tableName, newStartTime, newEndTime, booking.id
      );
      executeUpdate(query);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return false;
    }

    Monitor.alertActiveListeners(booking.facilityId);

    return true;
  }

  private static void validateBookingAvailability(Integer facilityId, Integer dayInteger, LocalTime startTime, LocalTime endTime, Integer idToExclude) throws Exception {
    if (Availability.isBookingPossible(facilityId, dayInteger, startTime, endTime) == false) {
      throw new BookingUnavailableException("Not within facility's availability");
    }
    if (Booking.hasBookingClashes(facilityId, dayInteger, startTime, endTime, idToExclude)) {
      throw new BookingUnavailableException("No booking slot available");
    }
  }

  private static void validateStartTimeEndTime(LocalTime startTime, LocalTime endTime) throws UnacceptableInputException {
    if (startTime.isBefore(endTime) == false) {
      throw new UnacceptableInputException("startTime is not before endTime");
    }
  }

  private static void validateAdvanceOrPostpone(int advanceOrPostpone) throws UnacceptableInputException {
    switch (advanceOrPostpone) {
      case UPDATE_TIMING_ADVANCE:
        break;
      case UPDATE_TIMING_POSTPONE:
        break;
      default:
        throw new UnacceptableInputException("advanceOrPostpone is unacceptable");
    }
  }

  private static void validateOffsetMinutes(int offsetMinutes) throws UnacceptableInputException {
    if (offsetMinutes < 0) {
      throw new UnacceptableInputException("offsetMinutes is unacceptable");
    }
  }

  static boolean hasBookingClashes(Integer facilityId, Integer dayInteger, LocalTime startTime, LocalTime endTime, Integer idToExclude) {
    String query;
    if (idToExclude == null) {
      query = String.format(
        "SELECT * FROM %s WHERE facility_id = %d;",
        tableName, facilityId
      );
    } 
    else {
      query = String.format(
        "SELECT * FROM %s WHERE facility_id = %d AND id <> %d;",
        tableName, facilityId, idToExclude
      );
    }

    try {
      List<Booking> rs = executeQuery(query);
      System.out.println(String.format("--- %s %s---", startTime, endTime));
      for (Booking booking : rs) {
        if (booking.isInDaysSelected(new Integer[]{dayInteger})) {
          if (booking.hasBookingClash(startTime, endTime)) {
            return true;
          }
        }
      }
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return false;
  }

  boolean hasBookingClash(LocalTime startTime, LocalTime endTime) {
    if (endTime.isBefore(this.startTime) || endTime.equals(this.startTime)) return false;
    if (startTime.isAfter(this.endTime) || startTime.equals(this.endTime)) return false;
    return true;
  }

  static Booking getById(Integer id) throws RecordNotFoundException {
    String query = String.format(
      "SELECT * FROM %s WHERE id = %d;",
      tableName, id
    );
    List<Booking> results = executeQuery(query);
    return results.get(0);
  }

  private boolean isInDaysSelected(Integer[] daysSelected) {
    return Arrays.stream(Availability.getDays(daysSelected)).anyMatch(this.day::equals);
  }

  private static Integer executeUpdate(String query) {
    Integer id = null;

    try {
      setupConnection();
      PreparedStatement stmt = getConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      stmt.execute();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
          id = rs.getInt(1);
      }

      closeConnection(null, stmt);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return id;
  }

  private static List<Booking> executeQuery(String query) throws RecordNotFoundException {
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

    return results;
  }

}
