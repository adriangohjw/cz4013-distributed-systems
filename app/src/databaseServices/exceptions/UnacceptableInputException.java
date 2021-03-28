package databaseServices.exceptions;

public class UnacceptableInputException extends Exception {

  private static final long serialVersionUID = 1L;

  public UnacceptableInputException(String msg) {
    super(msg);
  }
  
}