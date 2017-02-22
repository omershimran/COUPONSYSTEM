package threads;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import core.ConnectionPool;
import core.Coupon;
import dao.CouponDBDAO;
import exceptions.CouponExceptionHandler;
import exceptions.GeneralExceptionHandler;
import exceptions.NullConnectionException;


/**
 * 
 * a daily task that removes all out of date coupons
 *
 */
public class DailyCouponExpirationTask implements Runnable
{
	public boolean quit=false;
	public ArrayList<Coupon> allCoupons = new ArrayList<>();
	CouponDBDAO coupondbdao = new CouponDBDAO();

	/**
	 * Retrieves from the database an ArrayList of all the coupons in the database and removes all coupons
	 * that their end date has past
	 */
	@Override
	public void run()
	{
		Date today = Calendar.getInstance().getTime();

		while (!quit)
		{
			try {
				allCoupons = (ArrayList<Coupon>) coupondbdao.getAllCoupon();
			}
			catch (ClassNotFoundException | InterruptedException | SQLException | ParseException
					| NullConnectionException e1)
			{
				GeneralExceptionHandler.couponExceptionHandle(e1);
			}

			for (Coupon c : allCoupons)
			{
				if (c.getEndDate().before(today))
				{
					try {
						coupondbdao.removeCoupon(c);
						System.out.println("all out of date coupons have been removed");
					}
					catch (ClassNotFoundException | SQLException | InterruptedException | NullConnectionException e)
					{
						GeneralExceptionHandler.couponExceptionHandle(e);
					}
				}
			}

			try
			{
				Thread.sleep(24*60*60*1000);
			}
			catch (InterruptedException e)
			{
				GeneralExceptionHandler.couponExceptionHandle(e);
			}
		}
	}

	public void stopTask()
	{
		quit=true;
	}
}
