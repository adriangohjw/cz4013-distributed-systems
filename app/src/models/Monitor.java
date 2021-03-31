package src.models;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import src.databaseServices.Connect;
import src.databaseServices.caches.MonitorCache;
import src.databaseServices.exceptions.RecordNotFoundException;
import src.databaseServices.exceptions.UnacceptableInputException;

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

  
  /** 
   * Create a Monitor record
   * 
   * @param facilityName    Name of facility in which the booking will be made for
   * @param address         Address of the client monitoring
   * @param port            Port number that the client is monitoring on
   * @param lengthMin       Duration of the monitoring, in minutes
   * @return boolean        Return Monitor instance that is created, otherwise null
   */
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

  
  /** 
   * Get all active listeners (monitors) of a facility given by facility ID
   * 
   * @param facilityId      ID of Facility
   * @return List<Monitor>  Return list of active monitors if found, else null
   */
  public static List<Monitor> getActiveListeners(Integer facilityId) {
	List<Monitor> activeListeners = null;
    try {
      validateFacilityId(facilityId);

      String query = String.format(
        "SELECT * FROM %s WHERE facility_id = %d AND '%s' <= end_time;",
        tableName, facilityId, LocalDateTime.now()
      );
      activeListeners = 
        MonitorCache.cache.containsKey(facilityId) ?
          MonitorCache.cache.get(facilityId) :
          executeQuery(query) ;
      
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
    return activeListeners;
  }

  
  /** 
   * Validate if the facilityId is valid
   * - Check that facilityId is not null and more than 0
   * 
   * @param facilityId
   * @throws UnacceptableInputException when function input is invalid
   */
  private static void validateFacilityId(Integer facilityId) throws UnacceptableInputException {
    if (facilityId == null || facilityId <= 0) {
      throw new UnacceptableInputException("startTime is not before endTime");
    }
  }

  
  /** 
   * Abstracted reusable method to simplified the running of SQL query to read from the DB within this class
   * 
   * @param query                     The SQL query to be ran
   * @return List<Monitor>            List of monitors given the query being ran
   * @throws RecordNotFoundException  when no record found for the given query
   */
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

  
  /** 
   * Abstracted reusable method to simplified the running of SQL query to update the DB within this class
   * 
   * @param query     The SQL query to be ran
   * @return boolean  Return false if query executed successfully, else null
   */
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
