package com.yixun.manager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendNoticeManager {
	
	private static File file;
	
	private static final String KEY_HEAD = "head";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_NUMBER = "number";
	private static final String  KEY_TIME = "time";
	private static final String SEPER_TIME_AND_PERSON = "/*";
	private static final String SEPER_PERSON_AND_PERSON = ",";
	private static final String SEPER_PERSON_AND_LENGTH = "//";
	private static final String SEPER_LENGTH_AND_CONTENT = "*/";
	private static final String SEPER=",";
	private static final String SEPER_NUMBER_AND_TIME = "/*";
	private static final String SEPER_TIME_AND_LENGTH = "//";
	
	private static final long MAX_NOTICE_LENGTH = 8; 
	private static final int MAX_NUMBER_OF_ID = 11;
	private static final int BYTES_OF_TIME = 19;
	private static final int MAX_REPLY_HEAD_LENGTH = 40;
	
	private static final String CHARSET = "UTF-8";
	
	public static void main(String[] args){
		
		String file_name = "c:\\Users\\Administrator\\Desktop\\3.txt";
		File file = null;
		SendNoticeManager snm = null;
		try{
			file = new File(file_name);
		}catch(Exception e){
			e.printStackTrace();
		}
		snm = new SendNoticeManager(file);
//		System.out.println(",".getBytes().length);
		snm.writeSendNotice(3, "2012-12-12 12:10:12", "11111111111,33333333333,22222222222", "我司上营养");
		snm.writeReply("66666666666", "2012-2-2 12:10:12", "啊，只熬了");
		snm.writeReply("99999999999", "1202-12-21 12:10:12", "好的");
		snm.writeReply("66666666666", "2012-2-2 12:10:12", "啊，只熬了");
		System.out.println(snm.getContentFromSendMessage(3));
		
		ArrayList<Map<String,String>> replys = getAllReply(3);
		for(Map<String,String> map:replys){
			System.out.println(map.get(KEY_NUMBER));
			System.out.println(map.get(KEY_TIME));
			System.out.println(map.get(KEY_CONTENT));
//			System.out.println(getNumberFromReply(map.get(KEY_HEAD)));


		}

		
	}
	//各种构造函数
	public SendNoticeManager(File f){
		file = f;
		if(f.exists()==false){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public SendNoticeManager(String pathname) throws IOException{
		file = new File(pathname);
		if(file.exists()==false){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public SendNoticeManager(String parent,String child){
		file = new File(parent,child);
		if(file.exists()==false){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public SendNoticeManager(File parent,String child){
		file = new File(parent,child);
		if(file.exists()==false){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//得到自己发送的消息中的时间（字符串的形式）
	public static String getTimeFromSendMessage(int number){
		String Message_Send = getNotice(number);
		int position = Message_Send.indexOf(SEPER_TIME_AND_PERSON);
		return (position == -1)?"":Message_Send.substring(0, position);
	}
	//得到自己发送的消息中的应该接收的人的账号
	public static String getPersonFromSendMessage(int number){
		String Message_Send = getNotice(number);
		int position1 = Message_Send.indexOf(SEPER_TIME_AND_PERSON);
		int position2 = Message_Send.indexOf(SEPER_PERSON_AND_LENGTH);
		return (position1 == -1 || position2 == -1)?"":Message_Send.substring(position1+SEPER_TIME_AND_PERSON.length(), position2);
	}
	//得到自己发送的消息中的所有信息的总长度
	private static long getLengthFromSendMessage(String Message_Send){
		int position1 = Message_Send.indexOf(SEPER_PERSON_AND_LENGTH);
		int position2 = Message_Send.indexOf(SEPER_LENGTH_AND_CONTENT);
		String length = (position1 == -1 || position2 == -1 )?"":Message_Send.substring(position1+SEPER_PERSON_AND_LENGTH.length(),position2);
		long len = 0;
		try{
			len = Long.parseLong(length.trim());
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return len;
	}
	//得到自己发送的消息的内容
	public static String getContentFromSendMessage(int number){
		String Message_Send  = getNotice(number);
		int position = Message_Send.indexOf(SEPER_LENGTH_AND_CONTENT);
		return (position == -1)?"":Message_Send.substring(position+SEPER_LENGTH_AND_CONTENT.length());
	}
	
	
	//得到别人回复消息的id
	public static String getNumberFromReply(String reply){
//		String reply = getNotice(number);
		int position = reply.indexOf(SEPER_NUMBER_AND_TIME);
		return (position == -1)?"":reply.substring(0, position);
	}
	//得到别人回复消息的时间
	public static String getTimeFromReply(String reply){
		int position1 = reply.indexOf(SEPER_NUMBER_AND_TIME);
		int position2 = reply.indexOf(SEPER_TIME_AND_LENGTH);
		return (position1 == -1 || position2 == -1)?"":reply.substring(position1+SEPER_NUMBER_AND_TIME.length(), position2);
	}
	//得到别人回复消息的总长度（详细消息的总长度）
	private static int getLengthFromReply(String reply){
		int position1 = reply.indexOf(SEPER_TIME_AND_LENGTH);
		int position2 = reply.indexOf(SEPER_LENGTH_AND_CONTENT);
		String length = (position1 == -1 || position2 == -1 )?"":reply.substring(position1+SEPER_TIME_AND_LENGTH.length(),position2);

		int len = 0;
		try{
			len = Integer.parseInt(length.trim());
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return len;
	}
	//得到别人回复消息的内容
	public static String getContentFromReply(String reply){
		int position = reply.indexOf(SEPER_LENGTH_AND_CONTENT);
		return (position == -1)?"":reply.substring(position+SEPER_LENGTH_AND_CONTENT.length());
	}
	//将一段人的账号解析出来
	public static ArrayList<String> getNumberFromPerson(String person){
		ArrayList<String> numbers = new ArrayList<String>();
		int position1 = 0;
		int position2 = person.indexOf(SEPER_PERSON_AND_PERSON);
		while(position2 != -1 && position1 != -1){
			numbers.add(person.substring(position1, position2));
			position1 = position2+1;
			position2 = person.indexOf(SEPER_PERSON_AND_PERSON,position1);
		}
		numbers.add(person.substring(position1));
		return numbers;
	}
	//向文件中写关于自己所发的通知的信息
	public static boolean writeSendNotice(int people_number,String time,String person,String content){
		
	    FileOutputStream f = null;
	    try{
	    	f = new FileOutputStream(file.toString());
	    }catch(FileNotFoundException e){
	    	e.printStackTrace();
	    	return false;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	return false;
	    }
		String head = "";
		head += time;
		head += SEPER_TIME_AND_PERSON;
		head += person;
		head += SEPER_PERSON_AND_LENGTH;
		int len_head = 0;
		try {
			len_head = (int)head.getBytes(CHARSET).length + (int)MAX_NOTICE_LENGTH;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int len = len_head + SEPER_LENGTH_AND_CONTENT.length() + content.getBytes().length;
		head += String.valueOf(len);
		byte[] bytes_head = head.getBytes();
		byte[] bytes = new byte[len_head];//将包括总长度在内的数据
		int i;
		for(i=0;i<bytes_head.length;++i){
			bytes[i] = bytes_head[i];
		}	
		for(;i<len_head;++i){
			try {
				bytes[i]=" ".getBytes(CHARSET)[0];
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String notice="";
		try {
			notice = new String(bytes,CHARSET)+SEPER_LENGTH_AND_CONTENT+content;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			f.write(notice.getBytes(CHARSET));
			f.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//向文件中写别人回复的信息
	public static boolean writeReply(String number,String time,String content){
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(file.toString(),true);
		}catch(FileNotFoundException e ){
			e.printStackTrace();
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		String head = "";
		head += number;
		head += SEPER_NUMBER_AND_TIME;
		head +=  time;
		head += SEPER_TIME_AND_LENGTH;
		
		long length = 0;
		try {
			length = content.getBytes(CHARSET).length + MAX_REPLY_HEAD_LENGTH;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		head += String.valueOf(length);
//		head += SEPER_LENGTH_AND_CONTENT;
		byte[] bytes_head = new byte[0];
		try {
			bytes_head = head.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] bytes = new byte[MAX_REPLY_HEAD_LENGTH-SEPER_LENGTH_AND_CONTENT.length()];
		int i;
		for(i=0;i<bytes_head.length;++i){
			bytes[i] = bytes_head[i];
		}
		for(;i<MAX_REPLY_HEAD_LENGTH-SEPER_LENGTH_AND_CONTENT.length();++i){
			try {
				bytes[i]=" ".getBytes(CHARSET)[0];
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String reply = new String(bytes)+SEPER_LENGTH_AND_CONTENT+content;
		try{
			fos.write(reply.getBytes(CHARSET));
			fos.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;

	}
	//从文件中读自己所发送的通知的信息
	private static String getNotice(int number_person){
		RandomAccessFile rf = null;
		int len = BYTES_OF_TIME + number_person*MAX_NUMBER_OF_ID + (number_person-1) + (int)MAX_NOTICE_LENGTH + SEPER_TIME_AND_PERSON.length() + 
						SEPER_PERSON_AND_LENGTH.length() + SEPER_LENGTH_AND_CONTENT.length();
//		System.out.println(len);
		try{
			 rf = new RandomAccessFile(file.toString(),"r");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		byte[] bytes_head = new byte[len];
		try{
			rf.seek(0);
			rf.readFully(bytes_head);
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		String notice_head = "";
		try {
			notice_head = new String(bytes_head,CHARSET);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

//		System.out.println(notice_head);
		long bytes_sum = getLengthFromSendMessage(notice_head);
	
		byte[] bytes_content = new byte[(int)bytes_sum - len];
		try{
			rf.seek(len);
			rf.readFully(bytes_content);
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		String notice = "";
		try {
			notice = notice_head + new String(bytes_content,CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notice;	
	}
	//得到所有回复的信息，以后再逐条解析
	public static ArrayList<Map<String,String>> getAllReply(int number){
		ArrayList<Map<String,String>> replys = new ArrayList<Map<String,String>>();
		String notice = getNotice(number);
		int len = (int)getLengthFromSendMessage(notice);
		long len_file = 0;
		RandomAccessFile rf = null;
		try{
			rf = new RandomAccessFile(file.toString(), "r");
			len_file = file.length();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		byte[] bytes_reply_head = new byte[MAX_REPLY_HEAD_LENGTH];
		int start = len;
		while(start < len_file){
			try{
				rf.seek(start);
				rf.readFully(bytes_reply_head);
			}catch(Exception e){
				e.printStackTrace();
			}
			Map<String,String> map = new HashMap<String,String>();
			String reply_head = "";
			try {
				reply_head = new String(bytes_reply_head,CHARSET);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			System.out.println(reply_head);
			map.put(KEY_NUMBER, getNumberFromReply(reply_head));
			map.put(KEY_TIME, getTimeFromReply(reply_head));
			
			int len_total = getLengthFromReply(reply_head);
			byte[] bytes_content = new byte[len_total-MAX_REPLY_HEAD_LENGTH];
			try{
				rf.seek(start+MAX_REPLY_HEAD_LENGTH);
				rf.readFully(bytes_content);
			}catch(Exception e){
				e.printStackTrace();
			}
			try {
				map.put(KEY_CONTENT, new String(bytes_content,CHARSET));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			start += len_total;
			replys.add(map);
		}
		return replys;
	}
	//获得回复人的账号，不重复
	public List<String> getNumberOfReplyPerson(int ToNumber){
		ArrayList<Map<String,String>> replys = getAllReply(ToNumber);
		if(replys == null){
			return null;
		}
		List<String> numbers = new ArrayList<String>();
		for(Map<String,String> map:replys){
			String number = (String)map.get(KEY_NUMBER);
			if(numbers.contains(number)==false){
				numbers.add(number);
			}
		}
		return numbers;
	}
	//获得回复的人数
	public int getReplysPersonCount(int toNumber){
		List<String> numbers = getNumberOfReplyPerson(toNumber);
		return (numbers==null)?0:numbers.size();
	}
	//获得一个人回复的所有内容和时间的信息
	public List<Map<String,String>> getReplysOf(int toNumber,String number){
		List<Map<String,String>> replysOfAll = getAllReply(toNumber);
		List<Map<String,String>> replysOfPerson = new ArrayList<Map<String,String>>();
		for(Map<String,String> map:replysOfAll){
			if(number.equals(map.get(KEY_NUMBER))){
				Map<String,String> m = new HashMap<String,String>();
				m.put(KEY_CONTENT,map.get(KEY_CONTENT));
				m.put(KEY_TIME, map.get(KEY_TIME));
				replysOfPerson.add(m);
			}
		}
		return replysOfPerson;
	}
	//从一个账号的数组转化为一个带分隔符的字符串
	public static String getDiscussion(List<String> discussions){
		 if(discussions == null){
			 return null;
		 }
		 if(discussions.size() == 0){
			 return null;
		 }
		 StringBuffer sb = new StringBuffer();
		 for(String dis:discussions){
			 System.out.println(dis);
			 sb.append(dis).append(SEPER);
		 }
		 if(sb.length() == 0){
			 return null;
		 }
		 String s = sb.toString();
		 return s.substring(0, s.length()-SEPER.length());
	 }
	

}
