import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSetup {
  public static void main(String args[]) {

    // To replace with DB credentials
    String username = "adria";
    String password = "password";

    Connection c = null;
    Statement stmt = null;

    try {
      Class.forName("org.postgresql.Driver");
      c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/cz4013", username, password);
      stmt = c.createStatement();

      dropAllTables(c, stmt);
      createAllTables(c, stmt);

      stmt.close();         
      c.close();
      
      System.out.println("Tables created successfully");
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }

  private static void dropAllTables(Connection c, Statement stmt) {
    
    List<String> tableNames = new ArrayList<String>();
    tableNames.add("monitors");
    tableNames.add("bookings");
    tableNames.add("availabilities");
    tableNames.add("facilities");

    for (String tableName : tableNames) {
      try {
        stmt.executeUpdate("DROP TABLE " + tableName);
      } 
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
    }
  }

  private static void createAllTables(Connection c, Statement stmt) {

    List<String> sqls = new ArrayList<String>();
      sqls.add(
        "CREATE TABLE facilities (" +
          "id SERIAL PRIMARY KEY, " +
          "type       CHAR(50)    NOT NULL, " +
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
          "start_time   TIMESTAMP NOT NULL, " +
          "end_time     TIMESTAMP NOT NULL, " +
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
          stmt.executeUpdate(sql);
        }
      }
      catch (Exception e) {
        System.err.println( e.getClass().getName() + ": " + e.getMessage());
      }
  }

}