package seeDBExceptions;

public class NoDatabaseConnectionException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//parameterless constructor
	public NoDatabaseConnectionException(){}
	
	public NoDatabaseConnectionException(String message){
		super(message);
	}
}
