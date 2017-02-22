package dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import core.Company;
import core.ConnectionPool;
import core.Coupon;
import core.CouponType;
import core.Customer;
import exceptions.DuplicateCouponTypeException;
import exceptions.DuplicateEntryException;
import exceptions.NullConnectionException;
import exceptions.UnAvailableCouponException;
import exceptions.WrongDataInputException;
import utilities.CouponSqlQuerys;
import utilities.CustomerSqlQuerys;
import utilities.DateTranslate;

/**
 * 
 * the CompanyDBDAO hold all of the methods that are responsible for the customer's actions.
 * these methods connect to the database and retrieves data for the user or write data received from the
 * user to the database
 *
 */
public class CustomerDBDAO implements CustomerDAO
{
	
	ArrayList<Customer> allCustomers = new ArrayList<>();
	ArrayList<Coupon> allCoupons = new ArrayList<>();
	private long userCustomerId;
	
	public CustomerDBDAO()
	{
		
	}

	/**
	 * Receives a customer instance and register it in the database
	 */
	@Override
	public void createCustomer(Customer customer) throws ClassNotFoundException, InterruptedException, SQLException, DuplicateEntryException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement testStmt = (Statement) con.createStatement();
		ResultSet testRs;
		//the mysql statement to check if there is a customer by that name with the same password in my database
		testRs = testStmt.executeQuery(String.format(CustomerSqlQuerys.ALL_CUSTOMER_BY_NAME, customer.getCustName(),customer.getPassword()));
		if (testRs.next())
		{
			throw new DuplicateEntryException("the admin tried to create a customer with a name and password that already exist in the database");
		}
		else
		{
			
			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(CustomerSqlQuerys.INSERT_CUSTOMER);
			preparedStmt.setString (1, customer.getCustName());
			preparedStmt.setString (2, customer.getPassword());  
			// execute the preparedstatement
			preparedStmt.execute();
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		System.out.println("customer " + customer.getCustName() + " has been added");

	}
	

	/**
	 * Receives a customer instance and removes it's entries from the database
	 */
	@Override
	public void removeCustomer(Customer customer) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		//a mysql statement that checks if the customer exist in the database
		rs = stmt.executeQuery(CustomerSqlQuerys.SELECT_CUSTOMER_BY_NAME + customer.getCustName() +"'");
		// mysql statements that removes the customer and all his purchased coupos from the database
		stmt.execute(String.format(CustomerSqlQuerys.DELET_BY_CUST_NAME, customer.getCustName()));
		stmt.execute(String.format(CustomerSqlQuerys.DELET_BY_CUST_ID, customer.getId()));    
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		System.out.println("customer " + customer.getCustName() + " has been removed");

	}
	

	/**
	 * Receives a customer instance and update its entries in the database
	 */
	@Override
	public void updateCustomer(Customer customer) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		// create the java mysql update preparedstatement
		String query = CustomerSqlQuerys.UPDATE_CUSTOMER;
		PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(query);
		preparedStmt.setString   (1, customer.getPassword());
		preparedStmt.setLong     (2, customer.getId());
		// execute the java preparedstatement
		preparedStmt.executeUpdate();
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		System.out.println("customer " + customer.getCustName() + " has been updated");
	}
	

	/**
	 * Receives an id of a customer and returns an instance of the desired customer from the database
	 */
	@Override
	public Customer getCustomer(long id) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException
	{
		//Initializing the return variables
		Customer customer = new Customer();
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		// the mysql select statement for the correct customer
		rs = stmt.executeQuery(String.format(CustomerSqlQuerys.ALL_CUSTOMER_BY_ID, id ));
		//adding the data from the sql table to the correct members in the customer instance
		while ( rs.next() )
		{
			customer.setId(rs.getLong("id"));
			customer.setCustName(rs.getString("cust_name"));
			customer.setPassword(rs.getString("password"));
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return customer;
	}
	

	/**
	 * returns an ArrayList of all the customers in the database
	 */
	@Override
	public Collection<Customer> getAllCustomer() throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException
	{		
		//initializing the return variables		
		allCustomers.removeAll(allCustomers);	
		//Establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;			        
		// the mysql select statement for all customers
		rs = stmt.executeQuery(CustomerSqlQuerys.ALL_CUSTOMERS);	       
		//adding the data from the sql table to the correct members in the customer instance
		while ( rs.next() )
		{
			Customer customer = new Customer();
			customer.setId      (rs.getLong("id"));
			customer.setCustName(rs.getString("cust_name"));
			customer.setPassword(rs.getString("password"));		            
			//addind all the customers to the ArrayList
			allCustomers.add(customer);
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCustomers;
	}
	

	/**
	 * returns an ArrayList of all the coupons purchased by the current customer from the database
	 */
	@Override
	public Collection<Coupon> getCoupons() throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the return variables
		allCoupons.removeAll(allCoupons);
		ArrayList<Long> couponId = new ArrayList<>();		
		//Establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		// the mysql select statement for all of the customer's coupons id by his id from the customer_coupon table
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_CUST_ID, this.getUserCustomerId()));
		while(rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));
		}
		//a for loop that adds all of the coupons that the customer has purchased to and ArrayList
		for (int i=0;i<couponId.size();i++)
		{
			Statement addStmt = (Statement) con.createStatement();
			ResultSet addRs;
			addRs = stmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID, couponId.get(i)));
			if (addRs.next())
			{
				Coupon coupon = new Coupon();
				coupon.setId(addRs.getLong("id"));
				coupon.setTitle(addRs.getString("title"));
				coupon.setStartDate(DateTranslate.stringToDate(addRs.getString("start_date")));
				coupon.setEndDate(DateTranslate.stringToDate(addRs.getString("end_date")));
				coupon.setAmount(addRs.getInt("amount"));
				coupon.setCoupontype(CouponType.valueOf(addRs.getString("type").trim()));
				coupon.setMessage(addRs.getString("message"));
				coupon.setPrice(addRs.getDouble("price"));
				coupon.setImage(addRs.getString("image"));
				allCoupons.add(coupon);
			}
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCoupons;
	}
		
    

	/**
	 * checks the database for a customer with the given name and the given password
	 */
	@Override
	public boolean login(String custName, String password) throws ClassNotFoundException, InterruptedException, SQLException, WrongDataInputException, NullConnectionException
	{
		//Establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		//a mysql statement that checks if there is a customer with the input name and password
		rs = stmt.executeQuery(String.format(CustomerSqlQuerys.CUSTOMER_BY_PASSWORD, password, custName ));
		if(rs.next())
		{
			this.setUserCustomerId((rs.getLong("id")));
			//returning the connection
			ConnectionPool.getInstance().returnConnection(con);

			return true;
		}
		else
		{
			ConnectionPool.getInstance().returnConnection(con);
			throw new WrongDataInputException("a user tried to login using the customer name " + custName + " and the password " + password);
		}
	}
		
	
	/**
	 * Receives a coupon instance and updates a purchase of that coupon in the database
	 * @param coupon a coupon instance
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws DuplicateCouponTypeException thrown when attempting to purchase a coupon of a type that is already purchased by that customer
	 * @throws UnAvailableCouponException thrown when a coupon is not available for purchasing
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public void purchaseCoupon(Coupon coupon) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, DuplicateCouponTypeException, UnAvailableCouponException, NullConnectionException
	{
		boolean canPurchase = false;
		canPurchase = this.validCoupon(coupon);
		canPurchase = this.validateCouponType(coupon);
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		if (canPurchase==true)
		{
			//sql statement that updates the available amount of the coupon
			String updateQuery = CouponSqlQuerys.UPDATE_COUPON_AMOUNT;		
			PreparedStatement updateStmt = (PreparedStatement) con.prepareStatement(updateQuery);
			updateStmt.setInt   (1, (coupon.getAmount()-1));
			updateStmt.setLong  (2, coupon.getId());
			updateStmt.executeUpdate();
			// sql statement that adds the purchased coupon's id and the customer's id to the customer-coupon table
			String query = CustomerSqlQuerys.INSERT_CUSTOMER_COUPON;	  
			PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(query);
			preparedStmt.setLong (1, this.getUserCustomerId());
			preparedStmt.setLong (2, coupon.getId());
			// execute the preparedstatement
			preparedStmt.execute();     
			//returning the connection
			ConnectionPool.getInstance().returnConnection(con);
			System.out.println("coupon has been purchased");
		}
		else
		{
			System.out.println("cannot purchase coupon " + coupon.getTitle());
		}
	}
	
	/**
	 * Receives a coupon and check if that coupon's amount is larger then 0 and if the end date of the coupon
	 * is not out of date
	 * @param coupon a coupon instance
	 * @return true if the coupon can be purchased
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws UnAvailableCouponException thrown when a coupon is not available for purchasing
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public boolean validCoupon(Coupon coupon) throws SQLException, ParseException, UnAvailableCouponException, NullConnectionException 
	{
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();
		Date today = Calendar.getInstance().getTime();
		Date validDate = null;
		int availableAmount = 0;

		Statement stmt = (Statement) con.createStatement();		
		//sql statement that checks if the coupon been purchased is actually available for purchasing
		ResultSet checkAvailability;
		checkAvailability = stmt.executeQuery(String.format(CustomerSqlQuerys.AMOUNT_AND_END_DATE_BY_ID, coupon.getId() ));
		if (checkAvailability.next())
		{
			availableAmount = checkAvailability.getInt("amount");
			validDate = (Date) DateTranslate.stringToDate(checkAvailability.getString("end_date"));
		}

		if( availableAmount>0 && today.before(validDate) )
		{
			ConnectionPool.getInstance().returnConnection(con);
			return true;
		}
		else
		{
			ConnectionPool.getInstance().returnConnection(con);
			throw new UnAvailableCouponException("customer " + this.getUserCustomerId() + " tried to purchse a coupon that is"
					+ " either out of date or it's available amount is 0");
		}
	}
	
	/**
	 * Receives a coupon and checks the database to see if the customer that is trying to purchase this
	 * coupon has already a coupon of the same type purchased
	 * @param coupon a coupon instance
	 * @return true if there is no coupon of the same coupon type purchased by the customer
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws DuplicateCouponTypeException thrown when the customer has already purchased a coupon of the sam coupon type
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public boolean validateCouponType(Coupon coupon) throws SQLException, DuplicateCouponTypeException, NullConnectionException
	{
		ArrayList<Long> couponId = new ArrayList<>();
		ArrayList<CouponType> coupontype = new ArrayList<>();
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		//sql statement that gather all of the current customer's coupons's id
		ResultSet rs;
		Statement stmtP = (Statement) con.createStatement();
		rs = stmtP.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_CUST_ID, this.getUserCustomerId() ));
		while (rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));					
		}
		for (int i=0;i<couponId.size();i++)
		{
			ResultSet newrs;
			Statement stmtN = (Statement) con.createStatement();
			newrs = stmtN.executeQuery(String.format(CouponSqlQuerys.TYPE_BY_ID, couponId.get(i) ));
			while (newrs.next())
			{
				coupontype.add(CouponType.valueOf((newrs.getString("type").trim())));
			}
		}
		//a for loop that checks if the current customer already has a coupon of the same type as the one he is trying to buy
		for (int i=0;i<coupontype.size();i++)
		{
			if(coupontype.get(i)==coupon.getCoupontype())
			{
				ConnectionPool.getInstance().returnConnection(con);
				throw new DuplicateCouponTypeException("customer " + this.getUserCustomerId() + " tried to purchse a coupon of a type that he already has");
			}
		}
		ConnectionPool.getInstance().returnConnection(con);
		
		return true;

	}
		
	
	/**
	 * returns an ArrayList of all the coupons purchased by the current customer from the database by a given type
	 * @param coupontype a type of a coupon
	 * @return an ArrayList of all the coupons purchased by the current customer from the database by a given type
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public ArrayList<Coupon> getAllPurchasedCouponsByType(CouponType coupontype) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the method variables
		ArrayList<Long> couponId = new ArrayList<>();
		allCoupons.removeAll(allCoupons);
		//Establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		//a mysql statement that adds all of the purchased coupons id
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_CUST_ID, this.getUserCustomerId() ));
		while (rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));
		}
		//a for loop with a mysql statement that adds all of the purchased coupons with the input couponType to an ArrayList
		for (int i=0;i<couponId.size();i++)
		{
			ResultSet newrs;	
			Statement stmtN = (Statement) con.createStatement();
			newrs = stmtN.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID_AND_TYPE, couponId.get(i), coupontype.toString() ));
			if (newrs.next())
			{
				Coupon coupon = new Coupon();
				coupon.setId(newrs.getLong("id"));
				coupon.setTitle(newrs.getString("title"));
				coupon.setStartDate(DateTranslate.stringToDate(newrs.getString("start_date")));
				coupon.setEndDate(DateTranslate.stringToDate(newrs.getString("end_date")));
				coupon.setAmount(newrs.getInt("amount"));
				coupon.setCoupontype(CouponType.valueOf(newrs.getString("type").trim()));
				coupon.setMessage(newrs.getString("message"));
				coupon.setPrice(newrs.getDouble("price"));
				coupon.setImage(newrs.getString("image"));
				allCoupons.add(coupon);

			}
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCoupons;
	}
		
		
	
	
	/**
	 * returns an ArrayList of all the coupons purchased by the current customer from the database with a price of up to a given price
	 * @param price the max price of a coupon
	 * @return an ArrayList of all the coupons purchased by the current customer from the database with a price of up to a given price
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public ArrayList<Coupon> getAllPurchasedCouponsByPrice(double price) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the method variables
		ArrayList<Long> couponId = new ArrayList<>();
		allCoupons.removeAll(allCoupons);	
		//Establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		//a mysql statement that adds all of the purchased coupons id
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_CUST_ID, this.getUserCustomerId() ));
		while (rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));					
		} 
		//a for loop with a mysql statement that adds all of the purchased coupons with a price that is lower or equal to the input price to an ArrayList
		for (int i=0;i<couponId.size();i++)
		{
			ResultSet newrs;					
			newrs = stmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID_AND_PRICE, couponId.get(i), price ));
			while (newrs.next())
			{
				Coupon coupon = new Coupon();
				coupon.setId(newrs.getLong("id"));
				coupon.setTitle(newrs.getString("title"));
				coupon.setStartDate(DateTranslate.stringToDate(newrs.getString("start_date")));
				coupon.setEndDate(DateTranslate.stringToDate(newrs.getString("end_date")));
				coupon.setAmount(newrs.getInt("amount"));
				coupon.setCoupontype(CouponType.valueOf(newrs.getString("type").trim()));
				coupon.setMessage(newrs.getString("message"));
				coupon.setPrice(newrs.getDouble("price"));
				coupon.setImage(newrs.getString("image"));       
				allCoupons.add(coupon);

			}
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCoupons;

	}
	

	/**
	 * return the current customer user's id
	 * @return the current customer user's id
	 */
	public long getUserCustomerId()
	{
		return userCustomerId;
	}

	/**
	 * sets the current customer user's id
	 * @param userCustomerId the current customer user id
	 */
	public void setUserCustomerId(long userCustomerId)
	{
		this.userCustomerId = userCustomerId;
	}
		
}




