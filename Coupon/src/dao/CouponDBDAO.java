package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import core.Company;
import core.ConnectionPool;
import core.Coupon;
import core.CouponType;
import exceptions.DuplicateEntryException;
import exceptions.NullConnectionException;
import utilities.CouponSqlQuerys;
import utilities.DateTranslate;

/**
 * 
 * the CouponDBDAO hold all of the methods that are responsible for the coupons's actions.
 * the methods can be used by all of the clients, according to their authorization.
 * these methods connect to the database and retrieves data for the user or write data received from the
 * user to the database
 *
 */
public class CouponDBDAO implements CouponDAO
{
	
	private long userCompanyId;
	private long userCustomerId;
	ArrayList<Coupon> allCoupons = new ArrayList<>();
	public  CouponDBDAO()
	{
		
	}
	
	/**
	 * Receives a coupon instance and register it in the database
	 */
	@Override
	public void createCoupon(Coupon coupon) throws SQLException, ClassNotFoundException, InterruptedException, DuplicateEntryException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement checkStmt = (Statement) con.createStatement();
		ResultSet checkRs;
		// the mysql select statement that checks if there is a coupon with the same title
		checkRs = checkStmt.executeQuery(String.format(CouponSqlQuerys.COUPONS_BY_TITLE, coupon.getTitle()));

		if(checkRs.next())
		{
			throw new DuplicateEntryException("the user with the compId " + this.getUserCompanyId() + " tried to create a coupon"
					+ " with a title that already exist in the database");
		}
		else
		{
			// the mysql insert statement
			String query = CouponSqlQuerys.INSERT_COUPON;
			// create the mysql insert preparedstatement for the coupon table
			PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.setString (1, coupon.getTitle());
			preparedStmt.setString (2, DateTranslate.dateToString(coupon.getStartDate()));
			preparedStmt.setString (3, DateTranslate.dateToString(coupon.getEndDate()));
			preparedStmt.setInt    (4, coupon.getAmount());
			preparedStmt.setString (5, coupon.getCoupontype().toString());
			preparedStmt.setString (6, coupon.getMessage());
			preparedStmt.setDouble (7, coupon.getPrice());
			preparedStmt.setString (8, coupon.getImage());
			// execute the preparedstatement
			preparedStmt.execute();
			ResultSet rs = preparedStmt.getGeneratedKeys();
			long id = 0;
			if (rs.next())
			{
				id=rs.getInt(1);
			}
			// the mysql insert statement
			String recordQuery = CouponSqlQuerys.INSERT_COUPON_COMP;
			// create the mysql insert preparedstatement for the company_coupon table
			PreparedStatement recordPreparedStmt = (PreparedStatement) con.prepareStatement(recordQuery);
			recordPreparedStmt.setLong   (1, this.getUserCompanyId());
			recordPreparedStmt.setLong   (2, id);
			recordPreparedStmt.execute();
			System.out.println("coupon has been created");
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
	}
		
	
    
	/**
	 * receives a coupon instance and removes it's entries from the database
	 */
	@Override
	public void removeCoupon(Coupon coupon) throws SQLException, ClassNotFoundException, InterruptedException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		// create the mysql delete statement for the coupon table
		String query = CouponSqlQuerys.DELETE_COUPON_BY_ID;
		PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(query);
		preparedStmt.setLong(1, coupon.getId());
		// execute the preparedstatement
		preparedStmt.execute();	      
		// create the mysql delete statement for the company_coupon table
		String companyRecordQuery = CouponSqlQuerys.DELETE_COUPON_COMPANY_BY_ID;
		PreparedStatement companyRecordPreparedStmt = (PreparedStatement) con.prepareStatement(companyRecordQuery);
		companyRecordPreparedStmt.setLong(1, coupon.getId());
		// execute the preparedstatement
		companyRecordPreparedStmt.execute();      
		// create the mysql delete statement for the company_coupon table
		String customerRecordQuery = CouponSqlQuerys.DELETE_COUPON_CUSTOMER_BY_ID;
		PreparedStatement customerRecordPreparedStmt = (PreparedStatement) con.prepareStatement(customerRecordQuery);
		customerRecordPreparedStmt.setLong(1, coupon.getId());
		// execute the preparedstatement
		customerRecordPreparedStmt.execute();		      
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		System.out.println("coupon has been removed");

	}

    
	/**
	 * Receives a coupon instance and update it's entries in the database
	 */
	@Override
	public void updateCoupon(Coupon coupon) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		// create the java mysql update preparedstatement
		String query = CouponSqlQuerys.UPDATE_COUPON;
		PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(query);
		preparedStmt.setString (1, DateTranslate.dateToString(coupon.getEndDate()));
		preparedStmt.setDouble (2, coupon.getPrice());
		preparedStmt.setLong   (3, coupon.getId());    
		// execute the java preparedstatement
		preparedStmt.executeUpdate();
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		System.out.println("coupon has been updated!");
	}
	
    
	/**
	 * Receives an id of a coupon and returns an instance of that coupon from the database
	 */
	@Override
	public Coupon getCoupon(long id) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the return variables
		Coupon coupon = new Coupon();
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		// the mysql select statement for the correct coupon
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID, id));
		//adding the data from the mysql table to the correct members in the coupon instance
		while ( rs.next() )
		{
			coupon.setId(rs.getLong("id"));
			coupon.setTitle(rs.getString("title"));
			coupon.setStartDate(DateTranslate.stringToDate(rs.getString("start_date")));
			coupon.setEndDate(DateTranslate.stringToDate(rs.getString("end_date")));
			coupon.setAmount(rs.getInt("amount"));
			coupon.setCoupontype(CouponType.valueOf(rs.getString("type").trim()));
			coupon.setMessage(rs.getString("message"));
			coupon.setPrice(rs.getDouble("price"));
			coupon.setImage(rs.getString("image"));
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return coupon;
	}
	
    
	/**
	 * returning an ArrayList of all the coupons in the database
	 */
	@Override
	public Collection<Coupon> getAllCoupon() throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{	
		//initializing the return variables
		allCoupons.removeAll(allCoupons);
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;      
		// the mysql select statement for selecting all of the coupons
		rs = stmt.executeQuery(CouponSqlQuerys.ALL_COUPONS);     
		//adding the data from the sql table to the correct members in the coupon instance
		while ( rs.next() )
		{
			Coupon coupon = new Coupon();
			coupon.setId(rs.getLong("id"));
			coupon.setTitle(rs.getString("title"));
			coupon.setStartDate(DateTranslate.stringToDate(rs.getString("start_date")));
			coupon.setEndDate(DateTranslate.stringToDate(rs.getString("end_date")));
			coupon.setAmount(rs.getInt("amount"));
			coupon.setCoupontype(CouponType.valueOf(rs.getString("type").trim()));
			coupon.setMessage(rs.getString("message"));
			coupon.setPrice(rs.getDouble("price"));
			coupon.setImage(rs.getString("image"));            
			//adding all the coupons to the ArrayList
			allCoupons.add(coupon);
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		return allCoupons;
	}
	
    
	/**
	 * Receives a coupon type and returns an ArrayList of all the coupons in the database of that type
	 */
	@Override
	public Collection<Coupon> getCouponByType(CouponType couponType) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the return variables	 
		allCoupons.removeAll(allCoupons);
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;      
		// the mysql select statement for the correct coupons
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_TYPE, couponType.toString()));      
		//adding the data from the sql table to the correct members in the company instance
		while ( rs.next() )
		{
			Coupon coupon = new Coupon();
			coupon.setId(rs.getLong("id"));
			coupon.setTitle(rs.getString("title"));
			coupon.setStartDate(DateTranslate.stringToDate(rs.getString("start_date")));
			coupon.setEndDate(DateTranslate.stringToDate(rs.getString("end_date")));
			coupon.setAmount(rs.getInt("amount"));
			coupon.setCoupontype(CouponType.valueOf((rs.getString("type").trim())));
			coupon.setMessage(rs.getString("message"));
			coupon.setPrice(rs.getDouble("price"));
			coupon.setImage(rs.getString("image"));            
			//addind all the coupons to the ArrayList
			allCoupons.add(coupon);
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCoupons;


	}
		
    
	/**
	 * return the current company's user id
	 * @return the current company's user id
	 */
	public long getUserCompanyId()
	{
		return userCompanyId;
	}

	/**
	 * sets the current company's user id
	 * @param userCompanyId set's the current company's user id
	 */
	public void setUserCompanyId(long userCompanyId)
	{
		this.userCompanyId = userCompanyId;
	}
	
	
}
