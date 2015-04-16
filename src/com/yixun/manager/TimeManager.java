package com.yixun.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeManager {
	
		private static final String[] day_of_week = new String[]{
		"","周日","周一","周二","周三","周四","周五","周六"
		};
		private static final long TIME_MINUS = 30*60*1000;
		//转换成在文件中存储的格式
		public static String toSaveFormat(long mill){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date(mill));
			return time;
		}
		//将在文件中存储的格式字符串转换环成显示的形式
		public static String toDisplayFormat(String timeInFile){
			return toDisplayFormat(toDate(timeInFile));
		}
		//将在文件中存储的格式转成Date对象
		public static Date toDate(String timeInFile){
			Date time = null;
			try{
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println("文件时间为:"+timeInFile);
				time = df.parse(timeInFile);    //timeInFile是类似"2013-02-02 12:11:10"的字符串
			}catch (java.text.ParseException e) {
				e.printStackTrace();
				return null;
			}
			return time;	
		}
		//以日期date对象转换成在软件中显示的格式
		public static String toDisplayFormat(Date dTime){
			Calendar now = Calendar.getInstance();//现在的时间
			Calendar other = Calendar.getInstance();
			other.setTime(dTime);
			if(isSameYearday(other,now) == true){//如果是同一天的话
				return "今天"+toConcrete(other);
			}
			if(isLastDay(other,now) == true){//如果是昨天的话
				return "昨天"+toConcrete(other);
			}
			if(isLLastDay(other,now) == true){//如果是前天的话
				return "前天"+toConcrete(other);
			}
			if(isSameYearweek(other,now) == true){//如果是本周的话
				return toWeekday(other.get(Calendar.DAY_OF_WEEK))+toConcrete(other);
			}
			if(isSameYear(other,now) == true){//如果是同一年的话
				return ((other.get(Calendar.MONTH))+1)+"月"+other.get(Calendar.DAY_OF_MONTH)+"日";
			}
			//如果不是同一年的话
			return other.get(Calendar.YEAR)+"年"+(other.get(Calendar.MONTH)+1)+"月"+other.get(Calendar.DAY_OF_MONTH);
		}
		
		//以日历对象为参数将日期转换成在软件中显示的格式
		public static void toDisplayFormat(Calendar c){
			toDisplayFormat(c.getTime());
		}
		
		//以毫秒为单位将日期转换成在软件中显示的格式
		public static String  toDisplayFormat(long lTime){
			Date date = new Date(lTime);
			 return toDisplayFormat(date);
		}
		
		//判断前一个时间是不是比后一个时间早半个小时之内
		public static boolean isLaterInHalfHour(Calendar front,Calendar back){
			return (back.getTimeInMillis()-front.getTimeInMillis()<=TIME_MINUS)?true:false;
		}
		public static boolean isLaterInHalfHour(Date dfront,Date dback){
			Calendar front = Calendar.getInstance();
			Calendar back = Calendar.getInstance();
			front.setTime(dfront);
			back.setTime(dback);
			return (back.getTimeInMillis()-front.getTimeInMillis()<=TIME_MINUS)?true:false;
		}
		
		//判断两个日期是不是同一年
		private static boolean isSameYear(Calendar one,Calendar other){
			//转换成在文件中存储的格式
			return (one.get(Calendar.YEAR) == other.get(Calendar.YEAR))?true:false;
		}
		
		//判断两个日期是不是同一个月
		private static boolean isSameMonth(Calendar one,Calendar other){
			return (one.get(Calendar.MONTH) == other.get(Calendar.MONTH))?true:false;
		}
		
		//判断两个日期是不是同一年的同一天
		private static boolean isSameYearday(Calendar one,Calendar other){
			return (isSameYear(one,other)==true && one.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR))?true:false;
		}
		
		//判断两个日期是不是在同一年中的同一个星期
		private static boolean isSameYearweek(Calendar one,Calendar other){
			return (isSameYear(one,other)==true && one.get(Calendar.WEEK_OF_YEAR) == other.get(Calendar.WEEK_OF_YEAR))?true:false;
		}
		
		//判断一个日期是不是在另一个时间的“昨天”、（只是考虑的同一年的情况，简单起见）
		private static boolean isLastDay(Calendar first,Calendar second){
			return (isSameYear(first, second) == true && second.get(Calendar.DAY_OF_YEAR)-first.get(Calendar.DAY_OF_YEAR) == 1)?true:false;
		}
		
		//判断一个日期是不是另一个时期的“前天“，（只是考虑的同一年的情况，简单起见）
		private static boolean isLLastDay(Calendar first,Calendar second){
			return (isSameYear(first,second) == true && second.get(Calendar.DAY_OF_YEAR)-first.get(Calendar.DAY_OF_YEAR )== 2)?true:false;
		}
		
		//判断一个日期的时刻是不是在上午
		private static boolean isMorning(Calendar one){
			return (one.get(Calendar.HOUR_OF_DAY)<12)?true:false;	
		}
		
		//转换成一星期的周几的格式
		private static String toWeekday(int number){
			if(number>0 && number<8){
				return day_of_week[number];
			}
			return null;
		}
		
		//转化成一天具体的时间，从上下午算起
		private static String toConcrete(Calendar other){
			return (isMorning(other) == true?"上午":"下午")+toMedium(other.get(Calendar.HOUR_OF_DAY))+":"+toMedium(other.get(Calendar.MINUTE))+":"+toMedium(other.get(Calendar.SECOND));
		}
		
		//转换成中间进行判断的格式
		private static String toMedium(String sTime){
			if(sTime.length() == 1){
				return "0"+sTime;
			}
			return sTime;
		}
		//转化成中间判断的格式（以int为参数）
		private static String toMedium(int iTime){
			String sTime = String.valueOf(iTime);
			return toMedium(sTime);
		}
	
}
