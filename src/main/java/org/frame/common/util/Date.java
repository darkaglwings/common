/**
 * DateUtil contains tools for java.util.Date
 */
package org.frame.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Date extends java.util.Date {

	private static final long serialVersionUID = 5275892306521598172L;

	public Date() {
		super();
	}
	
	public Date(long date) {
		super(date);
	}
	
	@Deprecated
	public Date(String s) {
		super(s);
	}
	
	@Deprecated
	public Date(int year, int month, int date) {
		super(year, month, date);
	}
	
	@Deprecated
	public Date(int year, int month, int date, int hrs, int min) {
		super(year, month, date, hrs, min);
	}
	
	@Deprecated
	public Date(int year, int month, int date, int hrs, int min, int sec) {
		super(year, month, date, hrs, min, sec);
	}
	
	/**
	 * convert date string to date
	 * 
	 * @param dateString date string(yyyy-MM-dd)
	 * 
	 * @return date described by string
	 */
	public Date string2Date(String dateString) {
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = (Date) simpleDateFormat.parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	/**
	 * convert date-time string to date
	 * 
	 * @param dateString date-time string(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @return date-time described by string
	 */
	public Date string2DateTime(String dateString) {
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date = (Date) simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	/**
	 * convert time string to date
	 * 
	 * @param dateString time string(HH:mm:ss)
	 * 
	 * @return time(1970-01-01 HH:mm:ss) described by string
	 */
	public Date string2Time(String dateString) {
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			date = (Date) simpleDateFormat.parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	/**
	 * convert date-time string to date with given specific pattern
	 * 
	 * @param dateString date-time string(HH:mm:ss)
	 * @param pattern date-time pattern
	 * 
	 * @return time with specific pattern described by string
	 */
	public Date string2Date(String dateString, String pattern) {
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			date = (Date) simpleDateFormat.parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	/**
	 * convert present local date to string
	 * 
	 * @return date string with specific pattern(yyyy&#x5E74;MM&#x6708;dd&#x65E5;)
	 */
	public String date2LocalString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		return simpleDateFormat.format(this);
	}
	
	/**
	 * convert present date to string
	 * 
	 * @return date string with specific pattern(yyyy-MM-dd)
	 */
	public String date2String() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(this);
	}
	
	/**
	 * convert present local date to string
	 * 
	 * @return date-time string with specific pattern(yyyy&#x5E74;MM&#x6708;dd&#x65E5; HH&#x65F6;mm&#x5206;ss&#x79D2;)
	 */
	public String dateTime2LocalString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		return simpleDateFormat.format(this);
	}
	
	/**
	 * convert present date to string
	 * 
	 * @return date-time string with specific pattern(yyyy-MM-dd HH:mm:ss)
	 */
	public String dateTime2String() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(this);
	}
	
	/**
	 * convert specific date to string with given specific pattern
	 * 
	 * @param date specific date
	 * @param pattern specific date pattern
	 * 
	 * @return date-time string with given specific pattern
	 */
	public String date2String(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(this);
	}
	
	/**
	 * convert present local time to string
	 * 
	 * @return time string with given specific pattern(HH&#x65F6;mm&#x5206;ss&#x79D2;)
	 */
	public String time2LocalString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH时mm分ss秒");
		return simpleDateFormat.format(this);
	}
	
	/**
	 * convert present time to string
	 * 
	 * @return time string with given specific pattern(HH:mm:ss)
	 */
	public String time2String() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		return simpleDateFormat.format(this);
	}
	
	/*public static void main(String[] args) {
		Date date = new Date();
		System.out.println(date.date2String("yyyy-MM-dd HH:mm:ss.SSS"));
	}*/
	
}
