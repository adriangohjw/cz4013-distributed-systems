package databaseServices;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSetup extends Connect {
	
  
  /** 
   * @param args[]
   */
  public static void main(String args[]) {
    try {
      dropAllTables();
      createAllTables();
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
    System.out.println("Tables created successfully");
  }

  private static void dropAllTables() {
    
    List<String> tableNames = new ArrayList<String>();
    tableNames.add("monitors");
    tableNames.add("bookings");
    tableNames.add("availabilities");
    tableNames.add("facilities");

    for (String tableName : tableNames) {
      try {
        String query = String.format(
          "DROP TABLE %s;", 
          tableName
        );
        executeUpdate(query);
      } 
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

  private static void createAllTables() {

    List<String> sqls = new ArrayList<String>();
      sqls.add(
        "CREATE TABLE facilities (" +
          "id SERIAL PRIMARY KEY, " +
          "subtype    CHAR(50)    NOT NULL, " +
          "name       CHAR(256)   NOT NULL " +
        ");"
      );
      sqls.add(
        "CREATE TABLE availabilities ( " +
          "facility_id  INT NOT NULL, " +
          "day          VARCHAR(10) NOT NULL, " +
          "start_time   TIME     NOT NULL, " +
          "end_time     TIME     NOT NULL, " +
          "CONSTRAINT   fk_facility " + 
            "FOREIGN KEY(facility_id)" + 
              "REFERENCES facilities(id)" +
        ");"
      );
      sqls.add(
        "CREATE TABLE bookings ( " +
          "id SERIAL PRIMARY KEY, " +
          "facility_id  INT NOT NULL, " +
          "day          VARCHAR(10) NOT NULL, " +
          "start_time   TIME NOT NULL, " +
          "end_time     TIME NOT NULL, " +
          "CONSTRAINT   fk_facility " + 
            "FOREIGN KEY(facility_id)" + 
              "REFERENCES facilities(id)" +
        ");"
      );
      sqls.add(
        "CREATE TABLE monitors ( " +
          "facility_id  INT NOT NULL, " +
          "address      VARCHAR(256) NOT NULL, " +
          "host         INT NOT NULL, " +
          "start_time   TIMESTAMP NOT NULL, " +
          "end_time     TIMESTAMP NOT NULL, " +
          "CONSTRAINT   fk_facility " + 
            "FOREIGN KEY(facility_id)" + 
              "REFERENCES facilities(id)" +
        ");"
      );

      try {
        for (String sql : sqls) {
          executeUpdate(sql);
        }
      }
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
  }

}