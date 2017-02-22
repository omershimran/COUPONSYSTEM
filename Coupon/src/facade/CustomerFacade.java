package facade;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import core.ClientType;
import core.Coupon;
import core.CouponType;
import dao.CouponDBDAO;
import dao.CustomerDBDAO;
import exceptions.CouponExceptionHandler;
import exceptions.CustomerExceptionHandler;
import exceptions.DuplicateCouponTypeException;
import exceptions.NullConnectionException;
import exceptions.UnAvailableCouponException;
import exceptions.WrongDataInputException;

/**
 * 
 * The CustomerFacade class is used by the customer users of the couponsystem.
 * It grants them access to all of the relevant methods for their uses.
 *
 */
public class CustomerFacade implements CouponClientFacade
{

	private CustomerDBDAO customerdbdao;
	private CouponDBDAO  coupondbdao;
	private ArrayList<Coupon> allCoupons = new ArrayList<>();

	/**
	 * the constructor of the CustomerFacade
	 * it initialize the customerdbdao and the coupondbdao
	 */
	public CustomerFacade()
	{
		this.customerdbdao = new CustomerDBDAO();
		this.coupondbdao = new CouponDBDAO();

	}

	/**
	 * Receives a coupon instance and updates it's purchase in the database
	 * @param coupon a coupon instance
	 */
	public void purchaseCoupon(Coupon coupon)
	{	
		try
		{
			customerdbdao.purchaseCoupon(coupon);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | ParseException | DuplicateCouponTypeException | UnAvailableCouponException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

	}

	/**
	 * returns an ArrayList of all the current customer's purchased coupons
	 * @return an ArrayList of all the current customer's purchased coupons
	 */
	public ArrayList<Coupon> getAllPurchasedCoupons()
	{
		try
		{
			allCoupons = (ArrayList<Coupon>) customerdbdao.getCoupons();
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | ParseException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

		return allCoupons;

	}

	/**
	 * returns an ArrayList of all the current customer's purchased coupons of a given type
	 * @param coupontype a coupon type
	 * @return returns an ArrayList of all the current customer's purchased coupons of a given type
	 */
	public ArrayList<Coupon> getAllPurchasedCouponsByType(CouponType coupontype)
	{
		try
		{
			allCoupons = customerdbdao.getAllPurchasedCouponsByType(coupontype);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | ParseException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

		return allCoupons;

	}

	/**
	 * returns an ArrayList of all the current customer's purchased coupons with a price up to a given price
	 * @param price the max price of a coupon
	 * @return an ArrayList of all the current customer's purchased coupons with a price up to a given price
	 */
	public ArrayList<Coupon> getAllPurchasedCouponsByPrice(double price)
	{
		try
		{
			allCoupons = customerdbdao.getAllPurchasedCouponsByPrice(price);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | ParseException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

		return allCoupons;

	}

	/**
	 * checks the database for a customer entry with the given name and the given password
	 * returns true if it found one, returns false if there is no such entry in the database
	 */
	@Override
	public boolean login(String name, String password, ClientType clientType)
	{	
		try
		{
			return customerdbdao.login(name, password);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | WrongDataInputException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

		return false;
	}


	/**
	 * returns a coupon instance of the given id from the database
	 * @param id a coupon's id
	 * @return a coupon instance of the given id from the database
	 */
	public Coupon getCoupon(long id)
	{
		Coupon coup = new Coupon();

		try
		{
			coup = coupondbdao.getCoupon(id);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | ParseException | NullConnectionException e)
		{
			CouponExceptionHandler.couponExceptionHandle(e);
		}

		return coup;

	}

	/**
	 * returns an ArrayList of all the coupons in the database
	 * @return an ArrayList of all the coupons in the database
	 */
	public ArrayList<Coupon> getAllCoupons()
	{
		try
		{
			allCoupons = (ArrayList<Coupon>) coupondbdao.getAllCoupon();
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | ParseException | NullConnectionException e)
		{
			CouponExceptionHandler.couponExceptionHandle(e);
		}

		return allCoupons;

	}
}



