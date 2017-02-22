package exceptions;

/**
 * 
 * An exception that provides information about the state of a connection, and the possibility that the system is shutting down
 *
 */
public class NullConnectionException extends Exception
{
	/**
	 * Constructs a new exception with null as its detail message
	 */
	public NullConnectionException()
	{

	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message message the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
	 */
	public NullConnectionException(String message)
	{
		super(message);
	}

}
