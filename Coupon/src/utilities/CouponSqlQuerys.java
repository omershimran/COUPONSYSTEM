package utilities;

/**
 * 
 * a list of constants that holds all of the coupon's sql querys
 *
 */
public class CouponSqlQuerys
{
	public static String COUPON_ID_BY_COMP_ID = "SELECT coupon_id FROM couponsystem.company_coupon WHERE comp_id = '%1s'";
	public static String COUPON_ID_BY_CUST_ID = "SELECT coupon_id FROM couponsystem.customer_coupon WHERE cust_id = '%1s'";
	public static String DELETE_COUPON_CUST_ID = "DELETE FROM couponsystem.customer_coupon WHERE coupon_id = '%1s'";
	public static String DELETE_COUPON_COMP_ID = "DELETE FROM couponsystem.company_coupon WHERE comp_id = '%1s'";
	public static String ALL_COUPONS_BY_ID = "SELECT * FROM couponsystem.coupon WHERE id = '%1s'";
	public static String ALL_COUPONS_BY_TYPE = "SELECT * FROM couponsystem.coupon WHERE type = '%1s'";
	public static String ALL_COUPONS = "SELECT * FROM couponsystem.coupon";
	public static String COUPONS_BY_TITLE = "SELECT * FROM couponsystem.coupon WHERE title = '%1s'";
	public static String INSERT_COUPON = " insert into couponsystem.coupon (title, start_date, end_date, amount, type, message, price, image)"
			+ " values (?, ?, ?, ? ,? ,? ,? ,?)";
	public static String INSERT_COUPON_COMP = " insert into couponsystem.company_coupon (comp_id, coupon_id)"
			+ " values (?, ?)";
	public static String DELETE_COUPON_BY_ID = "delete from couponsystem.coupon where id = ?";
	public static String DELETE_COUPON_COMPANY_BY_ID = "delete from couponsystem.company_coupon where coupon_id = ?";
	public static String DELETE_COUPON_CUSTOMER_BY_ID = "delete from couponsystem.customer_coupon where coupon_id = ?";
	public static String DELETE_COUPON_BY_COUPON_ID = "delete from couponsystem.coupon where id = '%1s'";
	public static String UPDATE_COUPON = "update couponsystem.coupon set end_date = ?, price = ? where id = ?";
	public static String TYPE_BY_ID = "SELECT type FROM couponsystem.coupon WHERE id = '%1s'";
	public static String UPDATE_COUPON_AMOUNT = "update couponsystem.coupon set amount = ? where id = ?";
	public static String ALL_COUPONS_BY_ID_AND_TYPE = "SELECT * FROM couponsystem.coupon WHERE id = '%1s' and type LIKE '%2s'";
	public static String ALL_COUPONS_BY_ID_AND_PRICE = "SELECT * FROM couponsystem.coupon WHERE id = '%1s' and price <= '%2s'";


}
