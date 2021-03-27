package models;
import java.sql.*;

import databaseServices.Connect;

public class Booking extends Connect {

  Integer id;
  Integer facilityId;
  Timestamp startTime;
  Timestamp endTime;

  Booking(Integer id, Integer facilityId, Timestamp startTime, Timestamp endTime) {
    this.id = id;
    this.facilityId = facilityId;
    this.startTime = startTime;
    this.endTime = endTime;
  }

}
