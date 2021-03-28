package models;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import databaseServices.Connect;
import databaseServices.caches.MonitorCache;
import databaseServices.exceptions.RecordNotFoundException;
import databaseServices.exceptions.UnacceptableInputException;

public class Monitor extends Connect {

  static String tableName = "monitors";

  public Integer facilityId;
  public String address;
  public Integer host;
  public Timestamp startTime;
  public Timestamp endTime;

  Monitor(Integer facilityId, String address, Integer host, Timestamp startTime, Timestamp endTime) {
    this.facilityId = facilityId;
    this.address = address;
    this.host = host;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static boolean create(String facilityName, String address, Integer port, Integer lengthMin) {
    boolean isCreated = false;
    
    Integer facilityId = Facility.getIdFromName(facilityName);
    LocalDateTime start_time = LocalDateTime.now();
    LocalDateTime end_time = start_time.plusMinutes(lengthMin);

    try {
      String query = String.format(
        "INSERT INTO %s (facility_id, address, host, start_time, end_time) VALUES (%d, '%s', %d, '%s', '%s');",
        tableName, facilityId, address, port, start_time, end_time
      );
      isCreated = execute(query);
      
      Monitor monitor = new Monitor(facilityId, address, port, Timestamp.valueOf(start_time), Timestamp.valueOf(end_time));
      if (isCreated) {
        MonitorCache.put(monitor);
      }
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return isCreated;
  }

  public static void alertActiveListeners(Integer facilityId) {
    try {
      validateFacilityId(facilityId);

      String query = String.format(
        "SELECT * FROM %s WHERE facility_id = %d AND '%s' <= end_time;",
        tableName, facilityId, LocalDateTime.now()
      );
      List<Monitor> activeListeners = 
        MonitorCache.cache.containsKey(facilityId) ?
          MonitorCache.cache.get(facilityId) :
          executeQuery(query) ;

      // TODO: alerting all active listeners;
      
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }

  private static void validateFacilityId(Integer facilityId) throws UnacceptableInputException {
    if (facilityId == null || facilityId <= 0) {
      throw new UnacceptableInputException("startTime is not before endTime");
    }
  }

  private static List<Monitor> executeQuery(String query) throws RecordNotFoundException {
    List<Monitor> monitors = new ArrayList<Monitor>();
    
    try {
      setupConnection();
      Statement stmt = getConn().createStatement();
      ResultSet rs = stmt.executeQuery(query);

      Integer rsCount = 0;
      while (rs.next()) {
        monitors.add(
          new Monitor(
            rs.getInt("facility_id"), 
            rs.getString("address"), 
            rs.getInt("host"),
            rs.getTimestamp("start_time"),
            rs.getTimestamp("end_time")
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

    MonitorCache.put(monitors);

    return monitors;
  }

  private static boolean execute(String query) {
    boolean isSuccessful = false;

    try {
      setupConnection();
      Statement stmt = getConn().createStatement();
      
      stmt.executeUpdate(query);
      isSuccessful = true;

      closeConnection(null, stmt);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }

    return isSuccessful;
  }
}
