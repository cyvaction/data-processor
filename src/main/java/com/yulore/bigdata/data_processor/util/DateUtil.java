package com.yulore.bigdata.data_processor.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	/**
	 * 日期格式化字符串->全数字表示
	 */
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	/**
	 * 日期格式化字符串 到秒
	 */
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyyMMdd
	 */
	public static final String YYYYMMDD = "yyyyMMdd";
	/**
	 * YYYY-MM-DD
	 */
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	
	/** HH **/
	public static final String HH = "HH";

	/** MM **/
	public static final String MM = "MM";

	/** SS **/
	public static final String SS = "SS";
	
	
	private DateUtil(){
		
	}
	
	/**
	 * 日期格式化
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formatTime(Date date,String dateFormat){
		DateFormat df = new SimpleDateFormat(dateFormat);
		return df.format(date);
	}
	
	public static String getCurrentTimeStr(String dateFormat){
		return formatTime(getCurrentTime(),dateFormat);
	}
	
	public static Date getCurrentTime(){
		return getCurrentTime(null);
	}
	
	/**
	 * 获取该时区当前时间
	 * @param timeZone
	 * @return
	 */
	public static Date getCurrentTime(TimeZone timeZone){
		Calendar calendar = Calendar.getInstance();
		if(null!=timeZone){
			calendar.setTimeZone(timeZone);
		}
		return calendar.getTime();
	}

	public static File getDirByCurrentDate(String baseDirPath){
		
		String currentDate = formatTime(getCurrentTime(),"yyyyMMdd");
		
		File baseDir = new File(baseDirPath,currentDate);
		
		int i=1;
		while(baseDir.exists()){
			baseDir = new File(baseDirPath, currentDate+"_"+i);
			i++;
		}
		return baseDir;
	}
	
	public static void main(String[] args) {
		System.out.println(getCurrentTimeStr(YYYYMMDDHHMMSS));
	}
}
