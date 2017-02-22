package core;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import utilities.DateTranslate;

/**
 * 
 * The coupon class - holds all the members and constructors of the coupons.
 *
 */
public class Coupon {

	private long id;
	private String title;
    private java.util.Date startDate;
	private java.util.Date endDate;
	private int amount;
	private CouponType coupontype;
	private String message;
	private double price;
	private String image;
	
	
	public Coupon(){
		
	}
	
	/**
	 * 
	 * the constructor of the coupon
	 * @param id the id of the coupon in the database
	 * @param title title or name  of the coupon
	 * @param startDate the date that states the start of this coupon validation
	 * @param endDate the date that states the end of this coupon validation
	 * @param amount the number of available coupons of this coupon 
	 * @param coupontype the type of this coupon
	 * @param message a message from the creator of this coupon
	 * @param price the price of this coupon
	 * @param image an image to illustrate the coupos's use
	 * 
	 * 
	 * @throws ParseException thrown when the date is not in the correct format
	 */
//the normal constroctor for the coupon class
	public Coupon(long id, String title, String startDate, String endDate, int amount,CouponType coupontype, String message, double price,
			String image) throws ParseException
	{
		
		this.id = id;
		this.title = title;
		this.startDate = DateTranslate.stringToDate(startDate);
		this.endDate = DateTranslate.stringToDate(endDate);
		this.amount = amount;
		this.coupontype = coupontype;
		this.message = message;
		this.price = price;
		this.image = image;

	}
	

	/**
	 * return the coupons id
	 * @return the coupons id
	 */
	public long getId()
	{
		return id;
	}
	
    /**
     * sets the coupon's id
     * @param id the coupon's id
     */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * return the coupon's title
	 * @return the coupon's title
	 */
	public String getTitle() 
	{
		return title;
	}

	/**
	 * set's the title of the coupon
	 * @param title the coupon's title or name
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * return the coupon's start date of validation
	 * @return the coupon's start date of validation
	 */
	public java.util.Date getStartDate() 
	{
		return startDate;
	}

	/**
	 * set's the coupon's start date of validation
	 * @param date the coupon's start date
	 */
	public void setStartDate(java.util.Date date)
	{
		this.startDate = date;
	}

	/**
	 * return the coupon's end date of validation
	 * @return the coupon's end date of validation
	 */
	public java.util.Date getEndDate()
	{
		return endDate;
	}

	/**
	 * sets the coupon's end date of validation
	 * @param date the coupon's end date of validation
	 */
	public void setEndDate(java.util.Date date)
	{
		this.endDate = date;
	}

	/**
	 * return the available amount of coupons
	 * @return the available amount of coupons
	 */
	public int getAmount()
	{
		return amount;
	}

	/**
	 * sets the available amount of coupons
	 * @param amount the current amount of available coupons
	 */
	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	/**
	 * return the message from the coupon's creator
	 * @return the message from the coupon's creator
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * set's the message from the coupon's creator
	 * @param message a message from the coupon's creator
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * return the price of the coupon
	 * @return the price of the coupon
	 */
	public double getPrice()
	{
		return price;
	}

	/**
	 * sets the price of the coupon
	 * @param price the price of the coupon
	 */
	public void setPrice(double price)
	{
		this.price = price;
	}

	/**
	 * return the image of the coupon
	 * @return the image of the coupon
	 */
	public String getImage()
	{
		return image;
	}

	/**
	 * sets the image of the coupon
	 * @param image an image to illustrate the coupon's use 
	 */
	public void setImage(String image)
	{
		this.image = image;
	}
	
	/**
	 * return the type of the coupon
	 * @return the type of the coupon
	 */
	public CouponType getCoupontype()
	{
		return coupontype;
	}

	/**
	 * sets the type of the coupon
	 * @param coupontype the type of the coupon
	 */
	public void setCoupontype(CouponType coupontype)
	{
		this.coupontype = coupontype;
	}

	@Override
	public String toString()
	{
		return "Coupon [id=" + id + ", title=" + title + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", amount=" + amount + ", coupontype=" + coupontype + ", message=" + message + ", price=" + price + ", image=" + image
				+ "]";
	}
	
}
