package exceptions;

/**
 * 
 * An exception that provides information on a wrong use in the purchaseCoupon method in the CustomerDBDAO
 *
 */
public class UnAvailableCouponException extends Exception
{

	/**
	 * Constructs a new exception with null as its detail message
	 */
	public UnAvailableCouponException()
	{

	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message message the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
	 */
	public UnAvailableCouponException(String message)
	{
		super(message);
	}

}
