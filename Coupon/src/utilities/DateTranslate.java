package utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * A utility class that translate dates from string to java, in order to insert the right type of data
 * to the database from the eclipse and to retrieve the right type of data from the database to
 * the eclipse.
 *
 */
public class DateTranslate 
{

	/**
	 * changes the data that has been given in String to the java date
	 * @param date a date in a string format
	 * @return a date in the java date format
	 * @throws ParseException thrown if the date is not in the correct format
	 */
	public static Date  stringToDate(String date) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		java.util.Date newdate = sdf.parse(date);

		return newdate;

	}

	/**
	 * changes the java date that has been given to string
	 * @param date a date in a java date format
	 * @return a date in a string format
	 */
	public static String dateToString(Date date)
	{
		DateFormat sdf = new SimpleDateFormat("yy-MM-dd");
		String newDate = sdf.format(date);

		return newDate;
	}

}
