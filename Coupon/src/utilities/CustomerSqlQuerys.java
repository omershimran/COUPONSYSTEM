package utilities;

/**
 * 
 * a list of constants that holds all of the customer's sql querys
 *
 */
public class CustomerSqlQuerys
{

	public static String ALL_CUSTOMERS = "SELECT * FROM couponsystem.customer";
	public static String ALL_CUSTOMER_BY_NAME = "SELECT * FROM couponsystem.customer WHERE cust_name = '%1s' AND password like '%2s'";
	public static String ALL_CUSTOMER_BY_ID = "SELECT * FROM couponsystem.customer WHERE id = '%1s'";
	public static String INSERT_CUSTOMER = " insert into couponsystem.customer (cust_name, password)"
			+ " values (?, ?)";
	public static String SELECT_CUSTOMER_BY_NAME = "SELECT * FROM customer WHERE cust_name ='";
	public static String DELET_BY_CUST_NAME = "DELETE FROM couponsystem.customer WHERE cust_name = '%1s'";
	public static String DELET_BY_CUST_ID = "DELETE FROM couponsystem.customer_coupon WHERE cust_id = '%1s'";
	public static String UPDATE_CUSTOMER = "update couponsystem.customer set password = ? where id = ?";
	public static String CUSTOMER_BY_PASSWORD = "SELECT * FROM customer WHERE password = '%1s' and cust_name = '%2s'";
	public static String AMOUNT_AND_END_DATE_BY_ID = "SELECT amount, end_date FROM couponsystem.coupon WHERE id = '%1s'";
	public static String INSERT_CUSTOMER_COUPON = " insert into couponsystem.customer_coupon (cust_id,coupon_id)"
			+ " values (?, ?)";


}
