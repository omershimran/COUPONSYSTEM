package dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import core.Coupon;
import core.CouponType;
import exceptions.DuplicateEntryException;
import exceptions.NullConnectionException;

/**
 * 
 * the CompanyDAO interface should be implemented in any class that is used for connecting to the
 * database and write or read data from the database,regarding the action for the coupons in the database.
 *
 */
public interface CouponDAO
{
	/**
	 * Receives a coupon instance and register it in the database
	 * @param coupon a coupon instance
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws DuplicateEntryException thrown when trying to register a coupon that already exist
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public void createCoupon(Coupon coupon) throws SQLException, ClassNotFoundException, InterruptedException, DuplicateEntryException, NullConnectionException;
	/**
	 * Receives a coupon instance and removes it's entries from the database
	 * @param coupon a coupon instance
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public void removeCoupon(Coupon coupon) throws SQLException, ClassNotFoundException, InterruptedException, NullConnectionException;
	/**
	 * Receives a coupon instance and update it's entries in the database
	 * @param coupon a coupon instance
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public void updateCoupon(Coupon coupon) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException;
	/**
	 * Retrieves a coupon instance from the database by it's id
	 * @param id the desired coupon's id
	 * @return a coupon instance
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Coupon getCoupon(long id) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException;
	/**
	 * returns an ArrayList of all the coupons in the database
	 * @return an ArrayList of all the coupons in the database
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Collection<Coupon> getAllCoupon() throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException;
	/**
	 * returns an ArrayList of all the coupons in the database of a given type
	 * @param couponType a type of a coupon
	 * @return an ArrayList of all the coupons in the database of a given type
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Collection<Coupon> getCouponByType(CouponType couponType) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException;
	

}
