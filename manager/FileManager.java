
package com.yixun.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileManager {
	
	private static final String APP_NAME = "yixun";//应用程序的名字，也是主文件夹
	private static final String CONSTANTS= "contacts";//联系人主文件夹的名字
	private static final String DISCUSSION_GROUP = "discussion";//讨论组主文件夹的名字
	private static final String MY_GROUP = "group";//自定义组主文件的名字
	private static final String NOTICE = "notices";//通知主文件夹的名字
	private static final String DISCUSSION_IMG = "img_dis";//存放群组头像的文件夹的名字
	private static final String CONTACT_IMG = "img_con";//存放联系人头像的文件夹
	private static final String SEND_NOTICE = "send";//发送通知的文件夹
	private static final String RECEIVE_NOTICE = "receive";//收到通知的文件夹
	private static final String REPLY_NOTICE = "reply";//收到通知的文件夹

	
	private static final String INFOR = "information";//个人信息或者讨论组的信息
	private static final int LEN_OF_NUMBER = 11;
	private static final String SEPER = "/";//路径之间的分隔符
	private static final String MAIN_PATH = Environment.getExternalStorageDirectory().toString()+SEPER;
	private static final String RECORD = "record";//聊天记录
	private static final String SUFFIX = ".txt";//文本文件的后缀
	
	private static final String SEPER_NUMBER = ",";//账号之间的分隔符
	private static final String SEPER_ID_AND_COUNT = "_";//通知的id和接受通知的人数之间的分隔符
	/*public static void main(String[] args){
		
		try{
			System.out.println("主路径："+MAIN_PATH);
			System.out.println("开始执行新建文件函数");
			String myNumber = "12121212121";
			FileManager.newContact(myNumber,"12341234123");
			FileManager.newDiscussion(myNumber,"taolunzu1");
			FileManager.newGroup(myNumber,"group1");
			FileManager.newSendNotice(myNumber,"1221",3);
			FileManager.newReceiveNotice(myNumber);
			FileManager.newImg(myNumber, "11111111111");
			FileManager.addMemberToGroup(myNumber, "group1", "11111111111",true);
			FileManager.addMemberToGroup(myNumber, "group2", "11111111111",true);
			FileManager.addMemberToGroup(myNumber, "group1", "11111121111",true);

			ArrayList<String> list = FileManager.getMembersFromGroup(myNumber, "group1");
			list.add("12121212121");
			FileManager.addMembersToGroup(myNumber, "group1", list,true);
			ArrayList<String> lists = FileManager.getMembersFromGroup(myNumber, "group1");

			for(String s:lists){
				System.out.println(s);
			}
			FileManager.deleteMembersFromGroup(myNumber, "group1", lists);
//			FileManager.deleteGroup(myNumber, "group1");
//			FileManager.deleteContact(myNumber, "12341234123");
//			FileManager.deleteDiscussion(myNumber, "taolunzu1");
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	//新建一个联系人的文件夹，参数是账号（现在是手机号，11位）
	public static boolean newContact(String myNumber,String number) {
		if(number.length() != LEN_OF_NUMBER){
			try{
			throw new Exception("账号必须是"+LEN_OF_NUMBER+"位");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + CONSTANTS + SEPER;
		File parent = null;
		try{
			createMain();//建主文件夹
			createNumber(myNumber);//建自己账号的文件夹
			createContacts(myNumber);//建联系人文件夹
		
			File file = new File(path+number);//建这个联系人的文件夹
			if(file.exists() == true){return false;}//如果这个联系人已经存在的话返回false
			if(file.exists() == false){
				if(file.mkdir() == false){
					throw new Exception("新增联系人的文件夹不能被创建");
				}
				
				path +=  number + SEPER;
				File record = new File(path+RECORD+SUFFIX);//建聊天记录的文件
				if(record.exists() == false){
					if(record.createNewFile() == false){
						throw new Exception("新增联系人的聊天记录文件不能创建");
					}
				}	
			}
			
		}catch(FileNotFoundException f){
			System.out.println("文件没有找到");
			f.printStackTrace();
		}catch(IOException e){
			System.out.println("出现IO错误");
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("创建成功");
		return true;
	}
	//新建一个讨论组(根据讨论组的账号)
	public static boolean  newDiscussion(String myNumber,String number){
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + DISCUSSION_GROUP + SEPER;
		createMain();//建主文件夹
		createNumber(myNumber);//建自己账号的文件夹
		createDiscussion(myNumber);
		File file = null;
		try{
			file = new File(path+number);
			if(file.exists() == false)	file.mkdir();//创建新组的文件夹
			else{return false;}//如果以该账号或名字命名的讨论组已经存在，返回false
			path += number + SEPER;//下一步的路径
			file = new File(path+RECORD+SUFFIX);
			if(file.exists() == false)	file.createNewFile();//创建新组的聊天记录文件
			/*file = new File(path+INFOR+SUFFIX);
			if(file.exists() == false)	file.createNewFile();//创建新组的信息文件
*/		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;

	}
	//新建一个自定义群组（根据自定义组的名字），创建成功的话返回true，如果之前有这个自建的群组的话返回false
	public static boolean  newGroup(String myNumber,String name){
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER;
		File file = null;
		try{
			createMain();//建主文件夹
			createNumber(myNumber);//建自己账号的文件夹
			createGroup(myNumber);//创建自定义组的主文件夹
			file = new File(path+name);
			if(file.exists() == false)	file.mkdir();
			else{return false;}
			path += name + SEPER;
			file = new File(path+name+SUFFIX);
			if(file.exists() == false)	file.createNewFile();
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	//新建一个通知文件，之前没有创建成功的话返回true，如果之前已经有这个文件的话，返回false
	public static boolean newSendNotice(String myNumber,String id,int count){
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + NOTICE + SEPER + SEND_NOTICE +	 SEPER;
		File file =null;
		try{
			createMain();//建主文件夹
			createNumber(myNumber);//建自己账号的文件夹
			createNotice(myNumber);//创建通知文件夹
			createSendNotice(myNumber);//创建发送通知的文件夹
			file = new File(path+id+SEPER_ID_AND_COUNT+String.valueOf(count)+SUFFIX);
			if(file.exists() == false)	file.createNewFile();//建立以这条通知的id号为文件名的文件
			else{return false;}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	//创建收到的通知的文件
	public static void newReplyNotice(String myNumber){
		try{
			createMain();//建主文件夹
			createNumber(myNumber);//建自己账号的文件夹
			createNotice(myNumber);//创建通知文件夹
			createReplyNotice(myNumber);//创建回复通知的文件夹
			String path =  MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + NOTICE + SEPER  + 	REPLY_NOTICE	+ SEPER;
			File file = new File(path+REPLY_NOTICE + SUFFIX);
			if(file.exists() == false){
				file.createNewFile();
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//创建自己回复的通知的文件
	public static void newReceiveNotice(String myNumber){
		try{
			createMain();//建主文件夹
			createNumber(myNumber);//建自己账号的文件夹
			createNotice(myNumber);//创建通知文件夹
			createReceiveNotice(myNumber);//创建发送通知的文件夹
			String path =  MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + NOTICE + SEPER  + 	RECEIVE_NOTICE	+ SEPER;
			File file = new File(path+RECEIVE_NOTICE + SUFFIX);
			if(file.exists() == false){
				file.createNewFile();
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//在存放群组头像的文件夹下添加某一个群组的照片
	public static void newDisImg(String myNumber,Bitmap bm,String name){
		final String path =  MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +DISCUSSION_IMG + SEPER + name;

		try{
			createMain();//建主文件夹
			createNumber(myNumber);//建自己账号的文件夹
			createDisImage(myNumber);//建存放群组头像的文件夹
			saveBitmap(path,bm);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//在存放联系人头像的文件夹下添加一个联系人的头像图片
	public static void newConImg(String myNumber,Bitmap bm,String name){
		final String path =  MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG	+ SEPER + name;
System.out.println(path);
		try{
			createMain();//建主文件夹
			createNumber(myNumber);//建自己账号的文件夹
			createConImage(myNumber);//建存放联系人头像的文件夹
			//开始存数位图
			saveBitmap(path,bm);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//读取联系人的头像
	public static Bitmap readImgFromContact(String myNumber,String name){
		final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG + SEPER + name;
		Bitmap bitmap = null;
		try{
			BitmapFactory.Options opts = new BitmapFactory.Options();
		   	opts.inSampleSize = 2; //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰
		   	//返回原图解码之后的bitmap对象
		   bitmap=BitmapFactory.decodeFile(path, opts);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	public static void saveBitmap(String path,Bitmap bm) throws Exception{
		  File f = new File(path);
		  try {
		   if (f.exists()) {
			f.delete();
		   }
		   f.createNewFile();
		   System.out.println("文件创建成功");
		   FileOutputStream out = new FileOutputStream(f);
		   bm.compress(Bitmap.CompressFormat.PNG, 90, out);
		   out.flush();
		   out.close(); 
		   System.out.println("完毕");
		  } catch (FileNotFoundException e) {
				  throw new Exception("文件没有找到");
		  } catch (IOException e) {
		   throw new Exception("io错误");
		  }
	 }
	//读取群组的头像
	public static Bitmap readImgFromDis(String myNumber,String name){
		final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +DISCUSSION_IMG + SEPER + name;
		Bitmap bitmap = null;
		try{
			BitmapFactory.Options opts = new BitmapFactory.Options();
		   	opts.inSampleSize = 4; //这个的值压缩的倍数（2的整数倍），数值越小，压缩率越小，图片越清晰
		   	//返回原图解码之后的bitmap对象
		   bitmap=BitmapFactory.decodeFile(path, opts);		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	//获得所有讨论组的名字
	public static List<String> getAllDiscussions(String myNumber){
	final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + DISCUSSION_GROUP;
	return getChildFileName(path);
	}
	//获得所有自定义组的名字
	public static List<String> getAllGroup(String myNumber){
	final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP;
	return getChildFileName(path);
	}
	//获得一个文件夹下所有第一层文件的名字
	private static List<String> getChildFileName(String path){
	File file = null;
	try{
	file = new File(path);
	}catch(Exception e){
	e.printStackTrace();
	return null;
	}
	List<String> st=new ArrayList<String>();
	String[] ss=file.list();
	for (int i = 0; i < ss.length; i++) {
	st.add(ss[i]);
	}
	return st;
	}
	//建主文件夹
	public static void createMain() {
		try{
			File main = new File(MAIN_PATH + APP_NAME);//建 主文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("主文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//建登录账号的文件夹
	private static void createNumber(String number){
		try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number);
			if(main.exists() == false){
				main.mkdir();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//建联系人的文件夹
	private static void createContacts(String number){
		try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + CONSTANTS);//建联系人的文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("联系人文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void createDiscussion(String number){
		try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + DISCUSSION_GROUP);//建讨论组的文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("讨论组文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void createGroup(String number){
		try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + MY_GROUP);//建联系人的文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("自建组文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void createNotice(String number){
		try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER +NOTICE);//建联系人的文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("通知文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
    private static void createSendNotice(String number){
    	try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + NOTICE + SEPER + SEND_NOTICE);//建发送通知的文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("发送通知文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    private static void createReceiveNotice(String number){
    	try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + NOTICE + SEPER + RECEIVE_NOTICE);//建联系人的文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("收到通知的文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    private static void createReplyNotice(String number){
    	try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + NOTICE + SEPER + REPLY_NOTICE);//建联系人的文件夹
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("收到通知的文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    //创建存放讨论组头像的文件夹
    private static void createDisImage(String number){
    	try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + DISCUSSION_IMG);
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("存放群组图片的文件夹不能被创建");
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    //创建存放联系人头像的文件夹
    private static void createConImage(String number){
    	try{
    		File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + CONTACT_IMG);
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("存放联系人头像的文件夹不能被创建");
				}
			}
    	}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    //删除一个用户下所有的
    public static boolean deleteDir(String myNumber){
    	try{
    		File file = new File(MAIN_PATH + APP_NAME + SEPER + myNumber);
    		if(file.exists()==false){	
    		}else{
    			deleteDir(file);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
  //递归删除目录中的子目录下
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    //删除某个联系人的头像文件
    public static void deleteContactImg(String myNumber,String number){
		try{
			File file = new File(MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG + SEPER + number);
			if(file.exists()){
				file.delete();
			}
		}catch(Exception e){
			
		}
    }
    //删除某个自定义组的头像文件
    public static void deleteGroupImg(String myNumber,String name){
    	try{
    		File file = new File(MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +DISCUSSION_IMG + SEPER + name);
    		if(file.exists()){
    			file.delete();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    //删除自定义的组
    public static void deleteGroup(String myNumber,String groupName){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + groupName;
    	try{
    		File file = new File(path);
    		if(file.exists() == false)	return ;
    		File[] files = file.listFiles();
    		for(File f:files){
    			f.delete();//删除文件夹下的各个文件
    		}
    		file.delete();//删除这个文件夹
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    //删除某个联系人
    public static void deleteContact(String myNumber,String contactNumber){
    	String path = MAIN_PATH +APP_NAME + SEPER + myNumber + SEPER + CONSTANTS +SEPER + contactNumber;
    	try{
    		File file = new File(path);
    		if(file.exists() == false)	return ;
    		File[] files = file.listFiles();
    		for(File f:files){
    			f.delete();
    		}
    		file.delete();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    //删除某个讨论组
    public static void deleteDiscussion(String myNumber,String discussionNumber){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + DISCUSSION_GROUP +SEPER + discussionNumber;
    	try{
    		File file = new File(path);
    		if(file.exists() == false)	return;
    		File[] files = file.listFiles();
    		for(File f:files){
    			f.delete();
    		}
    		file.delete();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    //删除一条发出的通知
    public static void deleteSendNotice(String myNumber,String id_notice){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + NOTICE + SEPER + SEND_NOTICE ;
    	try{
    		File file = new File(path);
    		if(file.exists() == false)	return ;
    		File[] files = file.listFiles();
    		int position = -1;
    		for(File f:files){
    			position = f.getName().indexOf(SEPER_ID_AND_COUNT);
    			if(position != -1 && f.getName().substring(0,position).equals(id_notice)){
    				f.delete();
    				return ;
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    //在某个自定义的组里添加一个成员
    public static void addMemberToGroup(String myNumber,String name_group,String number_member){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + name_group +	 SEPER + name_group + SUFFIX;
    	System.out.println("path:"+path);
    	createMain();
    	createGroup(myNumber);
    	newGroup(myNumber, name_group);
    	FileOutputStream out = null;
    	System.out.println("开始try快");
    	try{
    		File file = new File(path);
    		ArrayList<String> members = getMembersFromGroup(myNumber,name_group);//得到这个组的所有成员
    		out = new FileOutputStream(file,true);//默认为true
    		if(members==null){
    			System.out.println("空");
    		}
    		if(members.contains(number_member) == true) {
    			return ;
    		}
    		long len = file.length();
    		
    		String content = (len == 0)?number_member:(SEPER_NUMBER+ number_member);
    		out.write(content.getBytes());
    		out.flush();
    	}catch(IOException e){
    		
    	}finally{
    		try{
    			out.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    //在某个自定义的组里添加一个以上的成员
    public static void addMembersToGroup(String myNumber,String name_group,ArrayList<String> number_members){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + name_group +	 SEPER + name_group + SUFFIX;
    	createMain();
    	createGroup(myNumber);
    	newGroup(myNumber, name_group);
    	FileOutputStream out = null;
    	try{
    		File file = new File(path);
    		if(file.exists() == false){
    			file.createNewFile();
    		}
//    		long len = file.length();
    		boolean writed = false;
    		String content = "";
    		ArrayList<String> members = getMembersFromGroup(myNumber,name_group);//得到这个组的所有成员
//    		out = new FileOutputStream(file,add_to_back);
    		out = new FileOutputStream(file,false);
    		for(String number_member:number_members){
    			/*if(members.contains(number_member) == true) {
        			continue;
        		}*/
    			if( writed == false){
    				content = number_member;
    				writed = true;
    			}
    			else{	
    				content = SEPER_NUMBER+ number_member;
    			}
        		out.write(content.getBytes());
        		out.flush();
        		}	
    	}catch(IOException e){
    		e.printStackTrace();
    	}finally{
    		try{
    			out.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }

    //在某个自定义的组中删除一个成员
    public static void deleteMemberFromGroup(String myNumber,String name_group,String number_member){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + name_group +	 SEPER + name_group + SUFFIX;
    	ArrayList<String> members = getMembersFromGroup(myNumber, name_group);
    	if(members.contains(number_member) == false){
    		return ;
    	}
    	members.remove(number_member);
    	System.out.println("已从文件中删除"+number_member);
    	System.out.println("开始冲喺呢");
    	
    	
    	addMembersToGroup(myNumber, name_group, members);

    }
    //在某个自定义的组中删除一个以上的成员
    public static void deleteMembersFromGroup(String myNumber,String name_group,ArrayList<String> number_members){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + name_group +	 SEPER + name_group + SUFFIX;
    	ArrayList<String> members = getMembersFromGroup(myNumber, name_group);
    	for(String number_member:number_members){
    		if(members.contains(number_member) == false){
    			continue;
    		}
    		members.remove(number_member);
    	}
    	addMembersToGroup(myNumber, name_group, members);
    }
    //读出在某个自定义组的所有成员的账号
    public static ArrayList<String> getMembersFromGroup(String myNumber,String name_group){
    	final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + name_group +	 SEPER + name_group + SUFFIX;
    	ArrayList<String> members = new ArrayList<String>();
    	FileInputStream in = null;
    	String numbers = "";
    	try{
    		File file = new File(path);
    		if(file.length()==0) return members;
    		byte[] bytes = new byte[512];
    		in = new FileInputStream(file);
    		int len = 0;
    		while((len = in.read(bytes)) != -1){
    			numbers += new String(bytes,0,len);
    		}
    		SendNoticeManager snm = new SendNoticeManager(file);
    		members = snm.getNumberFromPerson(numbers);
    	
    	}catch(FileNotFoundException e){
    		e.printStackTrace();
    		return null;
    	}catch(IOException e){   		
    		e.printStackTrace();
    		return null;
    	}catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    	try{
    	in.close();
    	}catch(Exception e){
    		System.out.println("关闭失败");
    		e.printStackTrace();
    	}
    	return members;
    }
   /* //返回账号为number的联系人的信息的文件名
    public static String toContactInformation(String myNumber,String number){
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+CONSTANTS+SEPER+number+SEPER+INFOR+SUFFIX;
    }*/
    //返回账号为number的联系人的聊天记录的文件名
    public static String toContactRecord(String myNumber,String number){
    	newContact(myNumber, number);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+CONSTANTS+SEPER+number+SEPER+RECORD+SUFFIX;
    }
   /* //返回某个讨论组的信息的文件名
    public static String toDiscussionInformation(String myNumber,String name){
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+DISCUSSION_GROUP+SEPER+name+SEPER+INFOR+SUFFIX;
    }*/
    //返回某个讨论组的记录的文件名
    public static String toDiscussionRecord(String myNumber,String name){
    	newDiscussion(myNumber, name);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+DISCUSSION_GROUP+SEPER+name+SEPER+RECORD+SUFFIX;
    }
    //返回某个自建组的文件名
    public static String toGroup(String myNumber,String name){
    	newGroup(myNumber, name);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+MY_GROUP+SEPER+name+SEPER+name+SUFFIX;
    }
    //返回发送的某条通知的文件名
    public static String toSendNotice(String myNumber,String id,int count){
    	//newSendNotice(myNumber, id, count);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+NOTICE+SEPER+SEND_NOTICE+SEPER+id+SEPER_ID_AND_COUNT+count+SUFFIX;
    }
    public static String toSendNotice(String myNumber,String filename){
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+NOTICE+SEPER+SEND_NOTICE+SEPER+filename+SUFFIX;
    }
    //返回收到的通知的文件名
    public static String toReceiveNotice(String myNumber){
    	newReceiveNotice(myNumber);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+NOTICE+SEPER+RECEIVE_NOTICE+SEPER+RECEIVE_NOTICE+SUFFIX;
    }
    //返回一个人的头像的文件位置
    public static String toContactImage(String myNumber,String number){
		final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG + SEPER + number;
		return path;
    }
    //生成通知的id
    public static String toId(){
    	return TimeManager.toSaveFormat(System.currentTimeMillis());
    }
  //从一个账号的数组转化为一个带分隔符的字符串
  	public static String getNumberString(List<String> discussions){
  		 if(discussions == null){
  			 return null;
  		 }
  		 if(discussions.size() == 0){
  			 return null;
  		 }
  		 StringBuffer sb = new StringBuffer();
  		 for(String dis:discussions){
  			 System.out.println(dis);
  			 sb.append(dis).append(SEPER_NUMBER);
  		 }
  		 if(sb.length() == 0){
  			 return null;
  		 }
  		 String s = sb.toString();
  		 return s.substring(0, s.length()-SEPER.length());
  	 }
}
