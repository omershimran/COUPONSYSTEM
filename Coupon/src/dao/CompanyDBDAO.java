package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import core.Company;
import core.ConnectionPool;
import core.Coupon;
import core.CouponType;
import exceptions.DuplicateEntryException;
import exceptions.NullConnectionException;
import exceptions.WrongDataInputException;
import utilities.CompanySqlQuerys;
import utilities.CouponSqlQuerys;
import utilities.DateTranslate;

/**
 * 
 * the CompanyDBDAO hold all of the methods that are responsible for the company's actions.
 * these methods connect to the database and retrieves data for the user or write data received from the
 * user to the database
 *
 */
public class CompanyDBDAO implements CompanyDAO
{
	
	ArrayList<Company> allCompanys = new ArrayList<>();
	ArrayList<Coupon> allCoupons = new ArrayList<>();
	private long userCompanyId;
	
	public CompanyDBDAO()
	{
		
	}
	
	/**
	 * receives a company instance and writing it in the database
	 */
	@Override
	public void createCompany(Company company) throws ClassNotFoundException, InterruptedException, SQLException, DuplicateEntryException, NullConnectionException 
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement testStmt = (Statement) con.createStatement();
		ResultSet testRs;
		//the mysql statement to check if there is a company by that name in my database
		testRs = testStmt.executeQuery(String.format(CompanySqlQuerys.SELECT_ALL_COMP_NAME, company.getCompName()));
		boolean res = testRs.next();
		if (res)
		{
			throw new DuplicateEntryException("the admin tried to creat a company with a name that already exist in the datbase");
		}
		else
		{
			// the mysql insert statement
			String query = CompanySqlQuerys.INSERT_COMPANY;
			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(query);
			preparedStmt.setString (1, company.getCompName());
			preparedStmt.setString (2, company.getPassword());
			preparedStmt.setString (3, company.getEmail());	       
			// execute the preparedstatement
			preparedStmt.execute();
			System.out.println("company " + company.getCompName() + " has been added to the database");
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
	}
		
		
	

	/**
	 * receives a company instance and removes it from the database
	 */
	@Override
	public void removeComapny(Company company) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		//the mysql statement to check if there is a company by that name in my database
		rs = stmt.executeQuery(String.format(CompanySqlQuerys.SELECT_COMPANY_COMP_ID, company.getId()));
		if (rs.next())
		{

			long compId = company.getId();
			//the mysql statement that removes the company from the company table in the database
			Statement deleteCompany = (Statement) con.createStatement();
			deleteCompany.execute(String.format(CompanySqlQuerys.DELETE_COMPANY_ID, compId ));	 
			Statement getCouponsStmt = (Statement) con.createStatement();
			ResultSet newRs;
			newRs = getCouponsStmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_COMP_ID, company.getId()));
			while (newRs.next())
			{
				long id = newRs.getLong("coupon_id");
				//the mysql statement that removes the company's coupons from the coupon table in the database
				Statement delete = (Statement) con.createStatement();
				delete.execute(String.format(CouponSqlQuerys.DELETE_COUPON_BY_COUPON_ID, id ));
				delete.execute(String.format(CouponSqlQuerys.DELETE_COUPON_CUST_ID, id ));
				delete.execute(String.format(CouponSqlQuerys.DELETE_COUPON_COMP_ID, company.getId()));
			}
			//returning the connection
			ConnectionPool.getInstance().returnConnection(con);
			System.out.println("company " + company.getCompName() + " has been removerd");		    		
		}
		else
		{
			System.out.println("compnay " + company.getCompName() + " does not exist in the database");
		}
	}
	

	/**
	 * receives a company instance and update it's values in the database
	 */
	@Override
	public void updateCompany(Company company) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		// create the mysql update preparedstatement
		String query = CompanySqlQuerys.UPDATE_COMPANY_BY_ID;
		PreparedStatement preparedStmt = (PreparedStatement) con.prepareStatement(query);
		preparedStmt.setString   (1, company.getPassword());
		preparedStmt.setString   (2, company.getEmail());
		preparedStmt.setLong     (3, company.getId());
		// execute the java preparedstatement
		preparedStmt.executeUpdate();
		//returning the connections
		ConnectionPool.getInstance().returnConnection(con);
		System.out.println("company " + company.getCompName() + " has been updated");
	}
	

	/**
	 * receives an id of a company and returns an instance of the company with <br/>
	 * it's values from the database
	 * @throws ParseException thrown when the date is not in the correct format
	 */
	@Override
	public Company getCompany(long id) throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException, ParseException 
	{
		//getting a connection
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		// the mysql select statement for the correct company
		rs = stmt.executeQuery(String.format(CompanySqlQuerys.SELECT_COMPANY_COMP_ID, id ));
		Company comp = new Company();
		//adding the data from the sql table to the correct members in the company instance
		while ( rs.next() )
		{
			comp.setId(rs.getLong("id"));
			comp.setCompName(rs.getString("comp_name"));
			comp.setPassword(rs.getString("password"));
			comp.setEmail(rs.getString("email"));
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		this.setUserCompanyId((comp.getId()));
		comp.setMycoupons(this.getCoupons());

		return comp;

	}


	/**
	 * returns an ArrayList of all the companys in the database
	 * @throws ParseException thrown when the date is not in the correct format 
	 */
	@Override
	public Collection<Company> getAllCompanys() throws ClassNotFoundException, InterruptedException, SQLException, NullConnectionException, ParseException
	{
		//initializing the return variables		
		allCompanys.removeAll(allCompanys);
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;      
		// the mysql select statement for all companys
		rs = stmt.executeQuery(CompanySqlQuerys.SELECT_ALL_COMPANYS);      
		//adding the data from the sql table to the correct members in the company instance
		while ( rs.next() )
		{
			Company comp = new Company();
			comp.setId(rs.getLong("id"));
			comp.setCompName(rs.getString("comp_name"));
			comp.setPassword(rs.getString("password"));
			comp.setEmail(rs.getString("email"));                
			//addind all the companys to the ArrayList
			allCompanys.add(comp);
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);
		for (Company c : allCompanys)
		{
			this.setUserCompanyId(c.getId());
			c.setMycoupons(this.getCoupons());
		}

		return allCompanys;
	}
	

	/**
	 * returns an ArrayList of all the company's coupons in the database
	 */
	@Override
	public Collection<Coupon> getCoupons() throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the return variables
		allCoupons.removeAll(allCoupons);
		ArrayList<Long> couponId = new ArrayList<>(); 
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		// the mysql select statement for all of the company's coupons id 
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_COMP_ID, this.getUserCompanyId()));
		while(rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));
		}
		//a for loop that adds all of the coupons that the company has created
		for (int i=0;i<couponId.size();i++)
		{
			Statement addStmt = (Statement) con.createStatement();
			ResultSet addRs;
			addRs = addStmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID, couponId.get(i)));
			while ( addRs.next() )
			{
				Coupon coupon = new Coupon();
				coupon.setId(addRs.getLong("id"));
				coupon.setTitle(addRs.getString("title"));
				coupon.setStartDate(DateTranslate.stringToDate(addRs.getString("start_date")));
				coupon.setEndDate(DateTranslate.stringToDate(addRs.getString("end_date")));
				coupon.setAmount(addRs.getInt("amount"));
				coupon.setCoupontype(CouponType.valueOf((addRs.getString("type").trim())));
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
	 * an ArrayList of all the company's coupons of the given type in the database
	 * @param couponType a type of a coupon
	 * @return an ArrayList of all the company's coupons of the given type in the database
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format 
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Collection<Coupon> getCompanyCouponByType(CouponType couponType) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the return variables
		allCoupons.removeAll(allCoupons);
		ArrayList<Long> couponId = new ArrayList<>(); 
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;      
		// the mysql select statement for all of the company's coupons id 
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_COMP_ID, this.getUserCompanyId()));    
		while(rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));
		}
		//a for loop that adds all of the coupons that the company has created
		for (int i=0;i<couponId.size();i++)
		{
			Statement addstmt = (Statement) con.createStatement();
			ResultSet addRs;      
			// the mysql select statement for the correct coupons
			addRs = stmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID_AND_TYPE, couponId.get(i), couponType.toString()));     		        
			//adding the data from the sql table to the correct members in the coupon instance
			while ( addRs.next() )
			{
				Coupon coupon = new Coupon();
				coupon.setId(addRs.getLong("id"));
				coupon.setTitle(addRs.getString("title"));
				coupon.setStartDate(DateTranslate.stringToDate(addRs.getString("start_date")));
				coupon.setEndDate(DateTranslate.stringToDate(addRs.getString("end_date")));
				coupon.setAmount(addRs.getInt("amount"));
				coupon.setCoupontype(CouponType.valueOf((addRs.getString("type").trim())));
				coupon.setMessage(addRs.getString("message"));
				coupon.setPrice(addRs.getDouble("price"));
				coupon.setImage(addRs.getString("image"));            
				//adding all the coupons to the ArrayList
				allCoupons.add(coupon);
			}
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCoupons;

	}
	
	
	/**
	 * returns an Arraylist of all of the company's coupons with a price that is up to the given price
	 * @param price a maximum price of a coupon
	 * @return an Arraylist of all of the company's coupons with a <br/>
	 * price that is up to the given price
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException thrown when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format 
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Collection<Coupon> getCompanyCouponByPrice(double price) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the return variables
		allCoupons.removeAll(allCoupons);
		ArrayList<Long> couponId = new ArrayList<>();
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;      
		// the mysql select statement for all of the company's coupons id 
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_COMP_ID, this.getUserCompanyId()));       
		while(rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));

		}
		//a for loop that adds all of the coupons that the company has created
		for (int i=0;i<couponId.size();i++)
		{
			Statement addstmt = (Statement) con.createStatement();
			ResultSet addRs; 
			// the mysql select statement for the correct coupons
			addRs = stmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID_AND_PRICE, couponId.get(i), price ));        
			//adding the data from the sql table to the correct members in the coupon instance
			while ( addRs.next() )
			{
				Coupon coupon = new Coupon();
				coupon.setId(addRs.getLong("id"));
				coupon.setTitle(addRs.getString("title"));
				coupon.setStartDate(DateTranslate.stringToDate(addRs.getString("start_date")));
				coupon.setEndDate(DateTranslate.stringToDate(addRs.getString("end_date")));
				coupon.setAmount(addRs.getInt("amount"));
				coupon.setCoupontype(CouponType.valueOf((addRs.getString("type").trim())));
				coupon.setMessage(addRs.getString("message"));
				coupon.setPrice(addRs.getDouble("price"));
				coupon.setImage(addRs.getString("image"));            
				//addind all the coupons to the ArrayList
				allCoupons.add(coupon);
			}
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCoupons;
	}
	
	
	/**
	 * returns an ArrayList of all the company's coupons in the database with an endDate up to the given date
	 * @param date an endDate 
	 * @return an ArrayList of all the company's coupons in the database <br/>
	 * with an endDate up to the given date
	 * @throws ClassNotFoundException thrown when the coupon class is not available
	 * @throws InterruptedException thrown when the thread is interrupted - might be because the system is shutting down
	 * @throws SQLException when the sql query is wrong
	 * @throws ParseException thrown when the date is not in the correct format 
	 * @throws NullConnectionException thrown when the connection is null
	 */
	public Collection<Coupon> getCompanyCouponByDate(Date date) throws ClassNotFoundException, InterruptedException, SQLException, ParseException, NullConnectionException
	{
		//initializing the return variables
		allCoupons.removeAll(allCoupons);
		ArrayList<Long> couponId = new ArrayList<>();
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;      
		// the mysql select statement for all of the company's coupons id 
		rs = stmt.executeQuery(String.format(CouponSqlQuerys.COUPON_ID_BY_COMP_ID, this.getUserCompanyId()));      
		while(rs.next())
		{
			couponId.add(rs.getLong("coupon_id"));
		}
		//a for loop that adds all of the coupons that the company has created
		for (int i=0;i<couponId.size();i++)
		{
			Statement addstmt = (Statement) con.createStatement();
			ResultSet addRs;      
			// the mysql select statement for the correct coupon
			addRs = stmt.executeQuery(String.format(CouponSqlQuerys.ALL_COUPONS_BY_ID, couponId.get(i)));
			//adding the data from the sql table to the correct members in the coupon instance
			while ( addRs.next() )
			{
				if (DateTranslate.stringToDate(addRs.getString("end_date")).before(date))
				{
					Coupon coupon = new Coupon();
					coupon.setId(addRs.getLong("id"));
					coupon.setTitle(addRs.getString("title"));
					coupon.setStartDate(DateTranslate.stringToDate(addRs.getString("start_date")));
					coupon.setEndDate(DateTranslate.stringToDate(addRs.getString("end_date")));
					coupon.setAmount(addRs.getInt("amount"));
					coupon.setCoupontype(CouponType.valueOf((addRs.getString("type").trim())));
					coupon.setMessage(addRs.getString("message"));
					coupon.setPrice(addRs.getDouble("price"));
					coupon.setImage(addRs.getString("image"));        
					//addind all the coupons to the ArrayList
					allCoupons.add(coupon);
				}
			}
		}
		//returning the connection
		ConnectionPool.getInstance().returnConnection(con);

		return allCoupons;
	}
	

	/**
	 * receives a company's name and it's password and checks for compatibility in the database
	 */
	@Override
	public boolean login(String compName,String password) throws ClassNotFoundException, InterruptedException, SQLException, WrongDataInputException, NullConnectionException
	{
		//establishing the connection to the data base
		Connection con = (Connection) ConnectionPool.getInstance().getConnection();

		Statement stmt = (Statement) con.createStatement();
		ResultSet rs;
		// the mysql select statement to check if i have a row where the password and the compname match the
		//input data
		rs = stmt.executeQuery(String.format(CompanySqlQuerys.COMPANY_BY_PASSWORD, password, compName ));
		if(rs.next())
		{
			//sets the userCompanyId for later methods use	        	
			this.setUserCompanyId(rs.getLong("id"));
			//returning the connection
			ConnectionPool.getInstance().returnConnection(con);

			return true;
		}
		else
		{
			ConnectionPool.getInstance().returnConnection(con);
			throw new WrongDataInputException("a user tried to login and entered the compName " + compName + " and the password " + password);
		}

	}
	


	/**
	 * returns the current user company's id 
	 * @return the company's id from the login
	 */
	public long getUserCompanyId()
	{
		return userCompanyId;
	}

    /**
     *  set's the company's id of the logged in company
     * @param userCompanyId
     */
	public void setUserCompanyId(long userCompanyId)
	{
		this.userCompanyId = userCompanyId;
	}
	
	
	
}
	
	