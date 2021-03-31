package models;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import databaseServices.Connect;
import databaseServices.caches.BookingCache;
import databaseServices.exceptions.BookingUnavailableException;
import databaseServices.exceptions.RecordNotFoundException;
import databaseServices.exceptions.UnacceptableInputException;

public class Booking extends Connect {

  static String tableName = "bookings";
  public final static int UPDATE_TIMING_ADVANCE = -1;  
  public final static int UPDATE_TIMING_POSTPONE = 1;  

  public Integer id;
  public Integer facilityId;
  public List<Monitor> activeListeners;
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

  
  /** 
   * Create a Booking record
   * 
   * @param facilityName  Name of facility in which the booking will be made for
   * @param dayInteger    Specific day selected to check availability (1 for Monday, 2 for Tuesday, etc.)
   * @param startTime     Start time of the desired booking slot
   * @param endTime       End time of the desired booking slot
   * @return Booking      Return Booking instance that is created, otherwise null
   */
  public static Booking create(String facilityName, Integer dayInteger, LocalTime startTime, LocalTime endTime) {
    Integer bookingId = null;
    Integer facilityId = null;
    Booking booking = null;
    
    try {
      facilityId = Facility.getIdFromName(facilityName);
      String dayString = Availability.getDayMapping(dayInteger);
      validateStartTimeEndTime(startTime, endTime);
      validateBookingAvailability(facilityId, dayInteger, startTime, endTime, null);

      String query = String.format(
        "INSERT INTO %s (facility_id, day, start_time, end_time) VALUES (%d, '%s', '%s', '%s');",
        tableName, facilityId, dayString, startTime, endTime
      );
      bookingId = execute(query);

      booking = new Booking(bookingId, facilityId, dayString, startTime, endTime);
      if (bookingId != null) {
        BookingCache.put(booking);
        booking.activeListeners = Monitor.alertActiveListeners(facilityId);
      }
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return booking;
  }

  
  /** 
   * Update the timing of a booking of a certain ID, 
   * by advancing/postponing it by a certain amount of minutes
   * Note: total duration of booking stays the same
   * 
   * @param id                  ID of the Booking
   * @param advanceOrPostpone   UPDATE_TIMING_ADVANCE / UPDATE_TIMING_POSTPONE to be passed in
   * @param offsetMinutes       Minutes to shift the booking timing by
   * @return boolean            Return true if operation is successful, else false
   */
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
      execute(query);

      Booking updatedBooking = new Booking(id, booking.facilityId, booking.day, newStartTime, newEndTime);
      BookingCache.put(updatedBooking);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return false;
    }

    booking.activeListeners = Monitor.alertActiveListeners(booking.facilityId);

    return true;
  }

  
  /** 
   * For a facility with a given ID on a given day, start time and end time, check if booking is possible
   * by taking into considerations the facility's availability, as well overlaps with exiting bookings
   * 
   * @param facilityId    ID of the Facility
   * @param dayInteger    Specific day selected to check availability (1 for Monday, 2 for Tuesday, etc.)
   * @param startTime     Start time of the desired booking slot
   * @param endTime       End time of the desired booking slot
   * @param idToExclude   Exclude bookings to check for, based on ID
   * @throws Exception    when booking is not possible to be made
   */
  private static void validateBookingAvailability(Integer facilityId, Integer dayInteger, LocalTime startTime, LocalTime endTime, Integer idToExclude) throws Exception {
    if (Availability.isBookingPossible(facilityId, dayInteger, startTime, endTime) == false) {
      throw new BookingUnavailableException("Not within facility's availability");
    }
    if (Booking.hasBookingClashes(facilityId, dayInteger, startTime, endTime, idToExclude)) {
      throw new BookingUnavailableException("No booking slot available");
    }
  }

  
  /** 
   * Validate if the startTime and endTime are valid
   * - Check if startTime is before endTime
   * 
   * @param startTime                     startTime input to validate
   * @param endTime                       endTime input to validate
   * @throws UnacceptableInputException   when function input is invalid
   */
  private static void validateStartTimeEndTime(LocalTime startTime, LocalTime endTime) throws UnacceptableInputException {
    if (startTime.isBefore(endTime) == false) {
      throw new UnacceptableInputException("startTime is not before endTime");
    }
  }

  
  /** 
   * Validate if the advanceOrPostpone input is valid
   * 
   * @param advanceOrPostpone             advanceOrPostpone input to validate
   * @throws UnacceptableInputException   when function input is invalid
   */
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

  
  /** 
   * Validate if the offsetMinutes input is valid
   * - Check if offsetMinutes is more than zero
   * 
   * @param offsetMinutes                 offsetMinutes input to validate
   * @throws UnacceptableInputException   when function input is invalid
   */
  private static void validateOffsetMinutes(int offsetMinutes) throws UnacceptableInputException {
    if (offsetMinutes < 0) {
      throw new UnacceptableInputException("offsetMinutes is unacceptable");
    }
  }

  
  /** 
   * For a facility with a given ID on a given day, start time and end time, check if booking is possible
   * by taking into considerations if it clashes with other existing bookings
   * 
   * @param facilityId    ID of the Facility
   * @param dayInteger    Specific day selected to check availability (1 for Monday, 2 for Tuesday, etc.)
   * @param startTime     Start time of the desired booking slot
   * @param endTime       End time of the desired booking slot
   * @param idToExclude   Exclude bookings to check for, based on ID
   * @return boolean      Return true if there are clash(es), else false
   */
  static boolean hasBookingClashes(Integer facilityId, Integer dayInteger, LocalTime startTime, LocalTime endTime, Integer idToExclude) {
    try {
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

      List<Booking> rs = executeQuery(query);
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

  
  /** 
   * Check if Booking instance will clash will a given pair of desired booking by startTime, endTime
   * 
   * @param startTime   Start time of the desired booking slot
   * @param endTime     End time of the desired booking slot
   * @return boolean    Return true if booking wlll clash with a given pair of startTime, endTime
   */
  boolean hasBookingClash(LocalTime startTime, LocalTime endTime) {
    if (endTime.isBefore(this.startTime) || endTime.equals(this.startTime)) return false;
    if (startTime.isAfter(this.endTime) || startTime.equals(this.endTime)) return false;
    return true;
  }

  
  /** 
   * Retrieve a booking record by ID
   * 
   * @param id                        ID of Booking
   * @return Booking                  Return booking instance if found, else null
   * @throws RecordNotFoundException  when no record found for the given query
   */
  public static Booking getById(Integer id) throws RecordNotFoundException {
    // TODO: add cache

    String query = String.format(
      "SELECT * FROM %s WHERE id = %d;",
      tableName, id
    );
    List<Booking> results = executeQuery(query);
    return results.get(0);
  }

  
  /** 
   * Check if an instance is within a list of integers (based on integers)
   * 
   * @param daysSelected  Specific days selected to check availabilities (1 for Monday, 2 for Tuesday, etc.)
   * @return boolean      Return true if instance is within the list of given days
   */
  private boolean isInDaysSelected(Integer[] daysSelected) {
    return Arrays.stream(Availability.getDays(daysSelected)).anyMatch(this.day::equals);
  }

  
  /** 
   * Abstracted reusable method to simplified the running of SQL query to update the DB within this class
   * 
   * @param query       The SQL query to be ran
   * @return Integer    Return the id of the booking created / updated if successful, else null
   */
  private static Integer execute(String query) {
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

  
  /** 
   * Abstracted reusable method to simplified the running of SQL query to read from the DB within this class
   * 
   * @param query                     The SQL query to be ran
   * @return List<Booking>            List of bookings given the query being ran
   * @throws RecordNotFoundException  when no record found for the given query
   */
  private static List<Booking> executeQuery(String query) throws RecordNotFoundException {
    List<Booking> results = new ArrayList<Booking>();
    
    try {
      setupConnection();
      Statement stmt = getConn().createStatement();
      ResultSet rs = stmt.executeQuery(query);

      Integer rsCount = 0;
      while (rs.next()) {
        Booking booking = new Booking(
          rs.getInt("id"), 
          rs.getInt("facility_id"), 
          rs.getString("day"), 
          rs.getTime("start_time").toLocalTime(),
          rs.getTime("end_time").toLocalTime()
        );
        BookingCache.put(booking);
        results.add(booking);
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
