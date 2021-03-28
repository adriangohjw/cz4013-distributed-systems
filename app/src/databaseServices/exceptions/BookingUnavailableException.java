package databaseServices.exceptions;

public class BookingUnavailableException extends Exception {

  private static final long serialVersionUID = 1L;

  public BookingUnavailableException(String msg) {
    super(msg);
  }
  
}