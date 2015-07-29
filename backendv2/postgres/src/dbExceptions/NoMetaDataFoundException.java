package dbExceptions;

public class NoMetaDataFoundException extends Exception{
	private static final long serialVersionUID = 1L;

	//parameterless constructor
	public NoMetaDataFoundException(){}
	
	public NoMetaDataFoundException(String message){
		super(message);
	}
}
