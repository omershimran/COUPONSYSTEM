package facade;

import java.sql.SQLException;

import core.ClientType;

/**
 * 
 * The CouponClientFacade should be implemented by any class that is used for granting access to certain 
 * methods for a client type, according to his needs and uses.
 * It holds an abstract method "login" that defines the type of user that is using the relevant facade.
 *
 */
public interface CouponClientFacade
{

	/**
	 * checks the database for an entry of a name and a password that matches the given client type
	 * @param name the client's name
	 * @param password the client's password
	 * @param clientType the client type
	 * @return true if the given input is a match' false if not a match
	 * @throws ClassNotFoundException thrown when the company/customer/admin facade class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 */
	public boolean login(String name,String password,ClientType clientType) throws ClassNotFoundException, InterruptedException, SQLException;

}
