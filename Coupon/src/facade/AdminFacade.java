package facade;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import core.ClientType;
import core.Company;
import core.Customer;
import dao.CompanyDBDAO;
import dao.CouponDBDAO;
import dao.CustomerDBDAO;
import exceptions.CompanyExceptionHandler;
import exceptions.CustomerExceptionHandler;
import exceptions.DuplicateEntryException;
import exceptions.NullConnectionException;

/**
 * 
 * The AdminFacade class is used only by the Admin of the couponsystem. 
 * He has the access to create, remove and update the different users of the couponsystem.
 *
 */
public class AdminFacade implements CouponClientFacade
{

	private CompanyDBDAO companydbdao;
	private CouponDBDAO coupondbdao;
	private CustomerDBDAO customerdbdao;
	ArrayList<Company> allCompanys = new ArrayList<>();
	ArrayList<Customer> allCustomers = new ArrayList<>();


	/**
	 * the AdminFacade constructor - it initialize all the DBDAOs
	 */
	public AdminFacade()
	{
		this.companydbdao = new CompanyDBDAO();
		this.coupondbdao = new CouponDBDAO();
		this.customerdbdao = new CustomerDBDAO();

	}

	/**
	 * Receives a company instance and register it in the database
	 * @param company a company instance
	 */
	public void createCompany(Company company)
	{
		try
		{
			companydbdao.createCompany(company);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | DuplicateEntryException | NullConnectionException e)
		{
			CompanyExceptionHandler.companyExceptionHandler(e);
		}

	}

	/**
	 * Receives a company instance and removes its entries from the database
	 * @param company a company instance
	 */
	public void removeCompany(Company company)
	{	
		try
		{
			companydbdao.removeComapny(company);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException e)
		{
			CompanyExceptionHandler.companyExceptionHandler(e);
		}

	}

	/**
	 * Receives a company instance and update its entries in the database
	 * @param company a company instance
	 */
	public void updateCompany(Company company)
	{	
		try
		{
			companydbdao.updateCompany(company);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException e)
		{
			CompanyExceptionHandler.companyExceptionHandler(e);
		}

	}

	/**
	 * Returns an ArrayList of all the companys in the database
	 * @return an ArrayList of all the companys in the database
	 */
	public Collection<Company> getAllCompanies()
	{
		try
		{
			allCompanys = (ArrayList<Company>) companydbdao.getAllCompanys();
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException | ParseException e)
		{
			CompanyExceptionHandler.companyExceptionHandler(e);
		}

		return allCompanys;

	}

	/**
	 * Receives an id of a company and returns an instance of that company from the database
	 * @param id a company's id
	 * @return an instance of that company from the database
	 */
	public Company getCompany(long id)
	{
		Company comp = new Company();

		try
		{
			comp = companydbdao.getCompany(id);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException | ParseException e)
		{
			CompanyExceptionHandler.companyExceptionHandler(e);
		}

		return comp;

	}

	/**
	 * Receives a customer instance and register it in the database
	 * @param customer a customer instance
	 */
	public void createCustomer(Customer customer)
	{
		try
		{
			customerdbdao.createCustomer(customer);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | DuplicateEntryException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);

		}

	}

	/**
	 * Receives a customer instance and removes its entries from the database
	 * @param customer a customer instance
	 */
	public void removeCustomer(Customer customer)
	{
		try
		{
			customerdbdao.removeCustomer(customer);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

	}

	/**
	 * Receives a customer instance and updates its entries in the database
	 * @param customer a customer instance
	 */
	public void updateCustomer(Customer customer)
	{
		try
		{
			customerdbdao.updateCustomer(customer);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

	}

	/**
	 * returns an ArrayList of all the customers in the database
	 * @return an ArrayList of all the customers in the database
	 */
	public Collection<Customer> getAllCustomer()
	{
		allCustomers = null;

		try
		{
			allCustomers = (ArrayList<Customer>) customerdbdao.getAllCustomer();
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException e) 
		{
			CustomerExceptionHandler.customerExceptionHandle(e);	
		}

		return allCustomers;
	}

	/**
	 * Receives a customer is and returns an instance of that customer from the database
	 * @param id a customer id
	 * @return an instance of that customer from the database
	 */
	public Customer getCustomer(long id)
	{
		Customer cust = new Customer();

		try
		{
			cust = customerdbdao.getCustomer(id);
		}
		catch (ClassNotFoundException | InterruptedException | SQLException | NullConnectionException e)
		{
			CustomerExceptionHandler.customerExceptionHandle(e);
		}

		return cust;

	}

	/**
	 * checking the database for an entry of an admin client type with the matching name and password
	 * returns true if it found one' returns false if there is no matching entry
	 */
	@Override
	public boolean login(String name, String password, ClientType clientType)
	{
		if (name.equals("admin") && password.equals("1234"))
		{
			return true;
		}

		return false;
	}
}
