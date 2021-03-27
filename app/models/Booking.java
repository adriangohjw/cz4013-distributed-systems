package models;
import java.sql.*;

import databaseServices.Connect;
import databaseServices.exceptions.UnacceptableInputException;

public class Booking extends Connect {

  static String tableName = "bookings";  

  Integer id;
  Integer facilityId;
  String day;
  Timestamp startTime;
  Timestamp endTime;

  Booking(Integer id, Integer facilityId, String day, Timestamp startTime, Timestamp endTime) {
    this.id = id;
    this.facilityId = facilityId;
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static Integer create(String facilityName, Integer dayInteger, Timestamp startTime, Timestamp endTime) {
    Integer bookingId = null;

    try {
      Integer facilityId = Facility.getIdFromName(facilityName);
      String dayString = Availability.getDayMapping(dayInteger);
      validateStartTimeEndTime(startTime, endTime);

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

  private static void validateStartTimeEndTime(Timestamp startTime, Timestamp endTime) {
    if (startTime.before(endTime) == false) {
      try {
        throw new UnacceptableInputException("startTime is not before endTime");
      } catch (UnacceptableInputException e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

}
