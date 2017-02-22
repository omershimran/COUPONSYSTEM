package dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import core.Coupon;
import core.Customer;
import exceptions.DuplicateEntryException;
import exceptions.NullConnectionException;
import exceptions.WrongDataInputException;

/**
 * 
 * the CompanyDAO interface should be implemented in any class that is used for connecting to the
 * database and write or read data from the database,regarding the action for the customers in the database.
 *
 */
public interface CustomerDAO
{
	
	/**
	 * Receives a customer instance and register it in the database
	 * @param customer a customer instance
	 * @throws ClassNotFoundException thrown when the customer class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws DuplicateEntryException thrown when trying to register a customer that already exist
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public void createCustomer(Customer customer) throws ClassNotFoundException, InterruptedException, SQLException, DuplicateEntryException, NullConnectionException;
	/**
	 * Receives a customer instance and removes it's entries from the database
	 * @param customer a customer instance
	 * @throws ClassNotFoundException thrown when the customer class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public void removeCustomer(Customer customer) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException;
	/**
	 * Receives a customer instance and update its entries in the database
	 * @param customer a customer instance
	 * @throws ClassNotFoundException thrown when the customer class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public void updateCustomer(Customer customer) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException;
	/**
	 * Receives an id of a customer and returns an instance of the desired customer from the database
	 * @param id the desired customer's id
	 * @return an instance of the desired customer from the database
	 * @throws ClassNotFoundException thrown when the customer class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Customer getCustomer(long id) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException;
	/**
	 * returns an ArrayList of all the customers in the database
	 * @return an ArrayList of all the customers in the database
	 * @throws ClassNotFoundException thrown when the customer class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Collection<Customer> getAllCustomer() throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException;
	/**
	 * returns an ArrayList of all the coupons purchased by the current customer from the database
	 * @return an ArrayList of all the coupons purchased by the current customer from the database
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Collection<Coupon> getCoupons() throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException;
	/**
	 * checks the database for a customer with the given name and the given password
	 * @param custName the customer's name
	 * @param password the customer's password
	 * @return true value if there is a match' false if there is no match
	 * @throws ClassNotFoundException thrown when the customer class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws WrongDataInputException thrown when the input does not match any customer in the database
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public boolean login(String custName, String password) throws ClassNotFoundException, InterruptedException, SQLException, WrongDataInputException, NullConnectionException;
	

}
