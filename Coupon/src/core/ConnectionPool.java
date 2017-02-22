package core;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import exceptions.CustomerExceptionHandler;
import exceptions.GeneralExceptionHandler;
import exceptions.NullConnectionException;

import java.sql.Connection;


/**
 * 
 * A singleton class that holds all of the connections to the database and distributes them to the asking methods.
 *
 */
public class ConnectionPool {

	private static ConnectionPool instance = null;
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost/couponsystem";
	private String username = "root";
	private String password = "053282102";
	/**
	 * @param key an object instance that is used for supporting multi-threads
	 */
	public  Object key = new Object();
	private int MAX_CON=5;
	private Set<Connection> connections = new HashSet<Connection>();
	private boolean isShuttingDown = false;



	/**
	 * the constructor of the connection pool, inside it class upon the poolInitilize method
	 */ 
	private ConnectionPool()
	{
		poolInitilize();
	}


	/**
	 * 
	 * @return returns an instance of the connection pool.
	 * it calls upon the constructor only if there isn't already an existing instance
	 */
	public static synchronized ConnectionPool getInstance()
	{
		if (instance==null)
		{
			instance = new ConnectionPool();
		}

		return instance;
	}



	/**
	 * poolInitilize method generates connections and adding them to a Set called connections.<br/>
	 * from this Set it will give connection to the asking method/thread and when they return<br/>
	 * the connection back it will be added once more to the Set
	 */
	private synchronized void poolInitilize()
	{
		while (connections.size()<MAX_CON)
		{
			try 
			{
				Class.forName(driver);
			}
			catch (ClassNotFoundException e)
			{
				GeneralExceptionHandler.couponExceptionHandle(e);
			}
			try
			{
				connections.add(DriverManager.getConnection(url, username, password));
			}
			catch (SQLException e)
			{
				GeneralExceptionHandler.couponExceptionHandle(e);
			}
		}

	}

	/**
	 *  getConnection is the method that gives connections to the asking method/thread<br/>
	 *  it checks if there are any available connections - if not' it places the asking<br/> 
	 *  method/thread in a wait status.   if there is a connection available' it gives the connection<br/>
	 *  and removes it from the Set.
	 * @return returns a connection
	 * @throws NullConnectionException 
	 */
	public synchronized Connection getConnection() throws NullConnectionException
	{
		Connection conn = null;
		synchronized (key)
		{
			while (connections.isEmpty())
			{
				try
				{
					key.wait();
				}
				catch (InterruptedException e)
				{
					CustomerExceptionHandler.customerExceptionHandle(e);
				}
			}
			if (!isShuttingDown)
			{	
				Iterator<Connection> it = connections.iterator();			
				conn = (Connection) it.next();
				connections.remove(conn);
			}
		}
		if (conn==null)
		{
			throw new NullConnectionException();			
		}

		return conn;
	}


	/**
	 * returnConnection method receives a connection from a method/thread that don't need it<br/>
	 * any more and adds it to the Set.<br/>
	 * once the connection has been added, the method give a notify notice to any method/thread<br/>
	 * that are in a wait status.
	 * @param conn a connection that is being returned to the connection pool
	 */
	public void returnConnection(Connection conn)
	{
		connections.add(conn);
		synchronized (key)
		{
			key.notifyAll();
		}

	}

	/**
	 *  cloasAllConnectios method runs on all the connections in the Set and closes them<br/>
	 *  one by one
	 * @throws SQLException thrown when the connection to the sql is not vaiable
	 */
	public void closeAllConnections() throws SQLException
	{
		Iterator<Connection> it = connections.iterator();

		while (connections.size()<MAX_CON)
		{
			synchronized (key)
			{
				try
				{
					key.wait(5000);
				}
				catch (InterruptedException e)
				{
					CustomerExceptionHandler.customerExceptionHandle(e);
				}
			}
		}
		while(it.hasNext())
		{
			Connection c = it.next();
			c.close();
		}
	}



	/**
	 * changes a boolean param to true and thus stops the method getConnection from giving<br/>
	 * connections.
	 */
	public void shuttingDown()
	{
		this.isShuttingDown = true;
	}



}
