package utilities;

/**
 * 
 * a list of constants that holds all of the company's sql querys
 *
 */
public class CompanySqlQuerys
{

	public static String SELECT_ALL_COMP_NAME = "SELECT * FROM company WHERE comp_name = '%1s'";
	public static String INSERT_COMPANY = " insert into couponsystem.company (comp_name, password, email)" + " values (?, ?, ?)";
	public static String SELECT_COMPANY_COMP_ID = "SELECT * FROM couponsystem.company WHERE id ='%1s'";
	public static String DELETE_COMPANY_ID = "DELETE FROM couponsystem.company WHERE id = '%1s'";
	public static String UPDATE_COMPANY_BY_ID = "update couponsystem.company set password = ?, email = ? where id = ?";
	public static String SELECT_ALL_COMPANYS = "SELECT * FROM couponsystem.company";
	public static String COMPANY_BY_PASSWORD = "SELECT * FROM company WHERE password = '%1s' and comp_name LIKE '%2s'";

}
