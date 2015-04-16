package com.yixun.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.DateFormat;

public class TimeManager {
	
		private static final String[] day_of_week = new String[]{
		"","����","��һ","�ܶ�","����","����","����","����"
		};
		private static final long TIME_MINUS = 30*60*1000;
		//ת�������ļ��д洢�ĸ�ʽ
		public static String toSaveFormat(long mill){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date(mill));
			return time;
		}
		//�����ļ��д洢�ĸ�ʽ�ַ���ת��������ʾ����ʽ
		public static String toDisplayFormat(String timeInFile){
			return toDisplayFormat(toDate(timeInFile));
		}
		//�����ļ��д洢�ĸ�ʽת��Date����
		public static Date toDate(String timeInFile){
			Date time = null;
			try{
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println("�ļ�ʱ��Ϊ:"+timeInFile);
				time = df.parse(timeInFile);    //timeInFile������"2013-02-02 12:11:10"���ַ���
			}catch (java.text.ParseException e) {
				e.printStackTrace();
				return null;
			}
			return time;	
		}
		//������date����ת�������������ʾ�ĸ�ʽ
		public static String toDisplayFormat(Date dTime){
			Calendar now = Calendar.getInstance();//���ڵ�ʱ��
			Calendar other = Calendar.getInstance();
			other.setTime(dTime);
			if(isSameYearday(other,now) == true){//�����ͬһ��Ļ�
				return "����"+toConcrete(other);
			}
			if(isLastDay(other,now) == true){//���������Ļ�
				return "����"+toConcrete(other);
			}
			if(isLLastDay(other,now) == true){//�����ǰ��Ļ�
				return "ǰ��"+toConcrete(other);
			}
			if(isSameYearweek(other,now) == true){//����Ǳ��ܵĻ�
				return toWeekday(other.get(Calendar.DAY_OF_WEEK))+toConcrete(other);
			}
			if(isSameYear(other,now) == true){//�����ͬһ��Ļ�
				return ((other.get(Calendar.MONTH))+1)+"��"+other.get(Calendar.DAY_OF_MONTH)+"��";
			}
			//�������ͬһ��Ļ�
			return other.get(Calendar.YEAR)+"��"+(other.get(Calendar.MONTH)+1)+"��"+other.get(Calendar.DAY_OF_MONTH);
		}
		
		//����������Ϊ����������ת�������������ʾ�ĸ�ʽ
		public static void toDisplayFormat(Calendar c){
			toDisplayFormat(c.getTime());
		}
		
		//�Ժ���Ϊ��λ������ת�������������ʾ�ĸ�ʽ
		public static String  toDisplayFormat(long lTime){
			Date date = new Date(lTime);
			 return toDisplayFormat(date);
		}
		
		//�ж�ǰһ��ʱ���ǲ��ǱȺ�һ��ʱ������Сʱ֮��
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
		
		//�ж����������ǲ���ͬһ��
		private static boolean isSameYear(Calendar one,Calendar other){
			//ת�������ļ��д洢�ĸ�ʽ
			return (one.get(Calendar.YEAR) == other.get(Calendar.YEAR))?true:false;
		}
		
		//�ж����������ǲ���ͬһ����
		private static boolean isSameMonth(Calendar one,Calendar other){
			return (one.get(Calendar.MONTH) == other.get(Calendar.MONTH))?true:false;
		}
		
		//�ж����������ǲ���ͬһ���ͬһ��
		private static boolean isSameYearday(Calendar one,Calendar other){
			return (isSameYear(one,other)==true && one.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR))?true:false;
		}
		
		//�ж����������ǲ�����ͬһ���е�ͬһ������
		private static boolean isSameYearweek(Calendar one,Calendar other){
			return (isSameYear(one,other)==true && one.get(Calendar.WEEK_OF_YEAR) == other.get(Calendar.WEEK_OF_YEAR))?true:false;
		}
		
		//�ж�һ�������ǲ�������һ��ʱ��ġ����족����ֻ�ǿ��ǵ�ͬһ���������������
		private static boolean isLastDay(Calendar first,Calendar second){
			return (isSameYear(first, second) == true && second.get(Calendar.DAY_OF_YEAR)-first.get(Calendar.DAY_OF_YEAR) == 1)?true:false;
		}
		
		//�ж�һ�������ǲ�����һ��ʱ�ڵġ�ǰ�조����ֻ�ǿ��ǵ�ͬһ���������������
		private static boolean isLLastDay(Calendar first,Calendar second){
			return (isSameYear(first,second) == true && second.get(Calendar.DAY_OF_YEAR)-first.get(Calendar.DAY_OF_YEAR )== 2)?true:false;
		}
		
		//�ж�һ�����ڵ�ʱ���ǲ���������
		private static boolean isMorning(Calendar one){
			return (one.get(Calendar.HOUR_OF_DAY)<12)?true:false;	
		}
		
		//ת����һ���ڵ��ܼ��ĸ�ʽ
		private static String toWeekday(int number){
			if(number>0 && number<8){
				return day_of_week[number];
			}
			return null;
		}
		
		//ת����һ������ʱ�䣬������������
		private static String toConcrete(Calendar other){
			return (isMorning(other) == true?"����":"����")+toMedium(other.get(Calendar.HOUR_OF_DAY))+":"+toMedium(other.get(Calendar.MINUTE))+":"+toMedium(other.get(Calendar.SECOND));
		}
		
		//ת�����м�����жϵĸ�ʽ
		private static String toMedium(String sTime){
			if(sTime.length() == 1){
				return "0"+sTime;
			}
			return sTime;
		}
		//ת�����м��жϵĸ�ʽ����intΪ������
		private static String toMedium(int iTime){
			String sTime = String.valueOf(iTime);
			return toMedium(sTime);
		}
	
}
