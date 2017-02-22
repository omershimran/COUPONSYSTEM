package core;


public enum ClientType 
{
	/**
	 * a customer - able to buy coupons
	 */
	CUSTOMER,
	/**
	 * a company - able to create and remove coupons
	 */
	COMPANY,
	/**
	 * an administrator - has max abilities, can create and remove customers/companys 
	 */
	ADMIN

}
