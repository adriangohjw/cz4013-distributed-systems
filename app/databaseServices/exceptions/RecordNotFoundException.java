package databaseServices.exceptions;

public class RecordNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public RecordNotFoundException() {
    super("Record not found");
  }
  
}