package databaseServices;

import java.sql.*;

public class Connect {

  // To replace with DB credentials
  static public String USERNAME = "adria";
  static public String PASSWORD = "password";
  static public String DRIVER_NAME = "org.postgresql.Driver";
  static public String DATABASE_URI = "jdbc:postgresql://localhost:5432/cz4013";

  protected static Connection conn;

  public static Connection getConn() {
    return conn;
  }

  public static void setupConnection() throws Exception {
    conn = DriverManager.getConnection(DATABASE_URI, USERNAME, PASSWORD);
  }

  protected static void closeConnection(ResultSet rs, Statement stmt) throws Exception {
    if (rs != null) {
      rs.close();
    }
    if (stmt != null) {
      stmt.close();
    }
    conn.close();
  }

  protected static void executeUpdate(String query) {
    try {
      setupConnection();
      Statement stmt = getConn().createStatement();

      stmt.executeUpdate(query);

      closeConnection(null, stmt);
    }
    catch (Exception e) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage());
    }
  }
}
