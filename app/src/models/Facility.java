package models;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import databaseServices.Connect;
import databaseServices.caches.FacilityCache;
import databaseServices.exceptions.RecordNotFoundException;

public class Facility extends Connect {

  static String tableName = "facilities";

  public Integer id;
  public String subtype;
  public String name;

  Facility(Integer id, String subtype, String name) {
    this.id = id;
    this.subtype = subtype;
    this.name = name;
  }

  public static List<Availability> getAvailabilities(String name, Integer[] daysSelected) {
    return Availability.getAvailabilitiesForFacility(getIdFromName(name), daysSelected);
  }

  public static Integer getIdFromName(String name) {
    Facility cacheEntry = FacilityCache.get(name);
    if (cacheEntry != null) return cacheEntry.id;

    try {
      String query = String.format(
        "SELECT * FROM %s WHERE name = '%s';",
        tableName, name
      );

      List<Facility> rs = executeQuery(query);
      Facility facility = rs.get(0);
      
      return facility.id;
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
      return null;
    }
  }

  private static List<Facility> executeQuery(String query) throws RecordNotFoundException {
    List<Facility> results = new ArrayList<Facility>();
    
    try {
      setupConnection();
      Statement stmt = getConn().createStatement();
      ResultSet rs = stmt.executeQuery(query);

      Integer rsCount = 0;
      while (rs.next()) {
        Facility facility = new Facility(
          rs.getInt("id"), 
          rs.getString("subtype"), 
          rs.getString("name")
        );
        FacilityCache.put(facility);
        results.add(facility);
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
