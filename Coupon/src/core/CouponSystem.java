package core;

import java.sql.SQLException;

import exceptions.CustomerExceptionHandler;
import exceptions.GeneralExceptionHandler;
import facade.AdminFacade;
import facade.CompanyFacade;
import facade.CouponClientFacade;
import facade.CustomerFacade;
import threads.DailyCouponExpirationTask;


/**
 * 
 * The CouponSystem class is a singleton class that is used for logging in to the couponsystem.
 * It is in charge of returning the right facade according to the user type, and it holds the
 * method that shuts down the couponsystem.
 *
 */
public class CouponSystem {
	
	private static CouponSystem instance = null;
	DailyCouponExpirationTask myDailyCouponExpirationTask = new DailyCouponExpirationTask();
	Thread cleanSystem = new Thread(myDailyCouponExpirationTask,"clean");
	
	
	
		/**
		 *the constructor initialize the connectionPool and starts the DailyCouponExpirationTask
		 *and sets the daily thread as daemon so the system can exit while it is running. 
		 */
		private CouponSystem()
		{
			ConnectionPool.getInstance();
			cleanSystem.setDaemon(true);
			cleanSystem.start();	
		}
		
		//
		/**
		 * getInstace makes sure that only one instance of CouponSystem can be generated
		 * @return instance of the coupon system
		 */
		public static synchronized CouponSystem getInstance()
		{
			if (instance==null)
			{
				instance = new CouponSystem();
			}
			
			return instance;
		}
		
		/**
		 * a factory pattern that returns the correct facade according to the login input
		 * 
		 * 
		 * @param name the name of the client
		 * @param password the password of the client
		 * @param clientType the type of client
		 * @return a facade according to the login input if true, if false returns null
		 */
		public CouponClientFacade login(String name,String password,ClientType clientType)
		{
			boolean login = false;
			switch(clientType)
			{
			case CUSTOMER :
				CustomerFacade customerfacade = new CustomerFacade();
			    login = customerfacade.login(name, password, clientType);
			    if (login==true)
			    {
				System.out.println("welcome customer " + name);
			    return customerfacade;
			    }
			break;
				
			case COMPANY : 
				CompanyFacade companyfacade = new CompanyFacade();
			    login = companyfacade.login(name, password, clientType);
			    if (login==true)
			    {
				System.out.println("welcome " + name + " company");
				companyfacade.setUserId();
				return companyfacade;
			    }
			break;
			
			case ADMIN : 
			
				AdminFacade adminfacade = new AdminFacade();
				login = adminfacade.login(name, password, clientType);
			    if (login==true)
			    {
			    System.out.println("welcome admin");
				return adminfacade;
			    }
		    }
						
			return null;
         }
		
		/**
		 * this method is called when the system is shutting down.
		 * <br/>
		 * it stops the daily task from running and closing all of the connections
		 * 
		 */
		public void shutDown()
		{
			myDailyCouponExpirationTask.stopTask();
			ConnectionPool.getInstance().shuttingDown();
			try
			{
				ConnectionPool.getInstance().closeAllConnections();
			}
			catch (SQLException e)
			{
				GeneralExceptionHandler.couponExceptionHandle(e);
			}
			ConnectionPool.getInstance().shuttingDown();
		}
		
}
		




