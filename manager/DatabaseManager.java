package com.yixun.manager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yixun.database.ContactDatabaseHelper;

/**
 * 
 * @author Administrator
 * 这个是管理每个用户和每个用户的联系人的类，每有一个用户使用就会在数据库中多增加一张表，表的名字为contact_用户的账号
 * 增加表的函数为newTable（）,参数在函数前有写，自己测试的时候一定要先用这个函数新建一张表，然后操作
 * 大概的函数参数，作用什么的，在每个函数的前面都写着呢，使用过程中有什么不对的马上告诉我，我也是刚看数据库，也不是很熟
 *
 */
public class DatabaseManager {
	private static final int VERSION = 1;
	 private static final String CONTACTS = "contacts";
	 
	 private static final String NUMBER = "number";
	 private static final String NAME = "name";
	 private static final String SEX = "sex";
	 private static final String WORDS = "words";
	
	 private static final String ISCONTACT = "isContact";//是否是自己的联系人
	 private static final String DISCUSSIONS = "discussions";//所在的分组
	 private static final String SEPER = "~";//在数据库中存储时讨论组的分隔符
	 private static final String REPLY_TABLE_NAME="reply_table";//回复的通知的表
	 private static final String NOTICE_ID="notice_id",REPLY_TIME="reply_time",REPLY_CONTENT="reply_content";
	 
	 //向某个用户的联系人数据库表中添加一个联系人（其实不一定是联系人，也可能是讨论组的一个人而不在联系人列表中）
	 public static void insertContact(String myNumber,Context context,String number,String name,String sex,String words,String isContact,String discussion){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		SQLiteDatabase db = sdh.getWritableDatabase();
		ContentValues cv = new ContentValues();
		Contact per = null;
		if((per = queryByNumber(myNumber, context, number)) != null){//该账号的联系人已经存在
			if(per.isContact .equals("N")){
				updateIsContact(myNumber, context, number,isContact);
			}
			updateName(myNumber,context,number,name);
			updateSex(myNumber,context,number,sex);
			updateWords(myNumber,context,number,words);
			updateDiscussionAdd(myNumber, context, number, discussion);
			db.close();
			return ;
		}
		
		cv.put(NUMBER , number);
		cv.put(NAME, name);
		cv.put(SEX,sex);
		cv.put(WORDS,words);
		cv.put(ISCONTACT, isContact);//是否是联系人
		cv.put(DISCUSSIONS, discussion);//在哪个讨论组里
		
		myNumber = CONTACTS + "_" + myNumber;
		db.insert(myNumber,null,cv);
		db.close();
	}
	
	 //添加在仅在联系人列表中显示的联系人
	 public static void insertContact(String myNumber,Context context,String number,String name,String sex,String words){
		 insertContact(myNumber,context,number,name,sex,words,"Y",null);
	 }
	 public static void insertContact(String myNumber,Context context,String number,String name,String sex,String words,String discussion){
		 insertContact(myNumber,context,number,name,sex,words,"Y",discussion);
	 }
	 
	/* public static void insertContact(String myNumber,Context context,Contact person){
		 insertContact(myNumber, context, person.number,person.name,person.sex,person.words,person.isContact,person.discussions);
	 }*/
	 
	 //当新加入到一个群中时插入某个群成员的信息
	 public static void insertContactFromDiscussion(String myNumber,Context context,String number,String name,String sex,String words,String discussion){
		 insertContact(myNumber,context,number,name,sex,words,"N",discussion);
	 }
	 
	//在某个用户的联系人列表中删除一个联系人
	 public static void deleteContact(String myNumber,Context context,String number){
		 //如果这个人不在任何讨论组里，那么删除
		 //如果这个人在某个讨论组里，将isContact置为“N”
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 Contact con = queryByNumber(myNumber, context, number);
		 if(con == null){
			 System.out.println("无要删除的联系人");
			 db.close();
			 return ;
		 }
		 if(con.discussions == null){
			 System.out.println("要删除的联系人所在的组为空");
			 myNumber = CONTACTS + "_" + myNumber;
			 db.delete(myNumber,NUMBER+"=?", new String[]{number});	
		 }
		 else{
			 System.out.println("联系人所在的组不为空，开始更新是否是联系人的信息");
			 updateIsContact(myNumber, context, number, "N");
		 }
		 db.close();
	 }
	 public static void deleteContact(String myNumber,Context context,Contact person){
		 deleteContact(myNumber, context, person.number);
	 } 
	 //删除在某个讨论组的人
	 public static void deleteInDiscussion(String myNumber,Context context,String dis_name){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 Cursor cur = db.query(myNumber, null, null, null, null, null, null);
		 if(cur == null){db.close();return ;}
		 String dis = "";
		 List<String> list = null;
		 String number=null;
		 while(cur.moveToNext()){
			 dis = cur.getString(cur.getColumnIndex(DISCUSSIONS));
			 if(dis == null){
				 System.out.println("现在游标所指的联系人所在的组为空");
				 continue;
			 }
			 list = getDiscussions(dis);
			 number=cur.getString(cur.getColumnIndex(NUMBER));
			 if(list.contains(dis_name)){//如果该人所在的讨论组里有dis_name这个组的话
				 updateDiscussionDelete(myNumber, context, cur.getString(cur.getColumnIndex(NUMBER)), dis_name);
				 //如果这个人既不是自己的联系人又只在这一个讨论组里，那么删除这个人
				 if(list.size() == 1 && cur.getString(cur.getColumnIndex(ISCONTACT)).equals("N")){
					 myNumber = CONTACTS + "_" + myNumber;
					 db.delete(myNumber, "number=?", new String[]{number});
				 }
			 }	 
		 }
		 db.close();
	 }
	 //删除所有在该表中的人的信息
	 public static void deleteAll(String myNumber,Context context){
		 if(tabbleIsExist(context, myNumber)==false){
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 db.delete(myNumber, null, null);
		 db.close();
	 }
	 
	 //修改某个用户的账号信息
	 public static void updateNumber(String myNumber,Context context,String oldNumber,String newNumber){
		 if(tabbleIsExist(context, myNumber)==false){
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 //如果就账号不存在
		 if(queryByNumber(myNumber, context, oldNumber) == null){
			 db.close();
			 return ;
		 }
		 //如果新账号已经存在
		 if(queryByNumber(myNumber, context, newNumber) != null){
			 db.close();
			 return ;
		 }
		 ContentValues cv = new ContentValues();
		 cv.put(NUMBER, newNumber);
		 myNumber = CONTACTS + "_" + myNumber;
		 db.update(myNumber, cv, NUMBER+"=?", new String[]{oldNumber});
		 db.close();
	 }
	 
	 //修改某个用户的某个联系人的名字信息
	 public static void updateName(String myNumber,Context context,String number,String newName){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 ContentValues cv = new ContentValues();
		 cv.put(NAME, newName);
		 myNumber = CONTACTS + "_" + myNumber;
		 db.update(myNumber, cv, NUMBER+"=?", new String[]{number});
		 db.close();
	 }
	 
	 public static void updataName(String myNumber,Context context,Contact person){
		 updateName(myNumber,context,person.number,person.name);
	 }
	 
	 //修改某个用户的某个联系人的性别信息
	 public static void updateSex(String myNumber,Context context,String number,String newSex){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 ContentValues cv = new ContentValues();
		 cv.put(SEX, newSex);
		 myNumber = CONTACTS + "_" + myNumber;
		 db.update(myNumber, cv, NUMBER+"=?", new String[]{number});
		 db.close();
	 }
	 
	 public static void updateSex(String myNumber,Context context,Contact person){
		 updateSex(myNumber,context,person.number,person.sex);
	 }
	 
	 //修改某个用户的某个联系人的签名信息
	 public static void updateWords(String myNumber,Context context,String number,String newWords){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 ContentValues cv = new ContentValues();
		 cv.put(WORDS, newWords);
		 myNumber = CONTACTS + "_" + myNumber;
		 db.update(myNumber, cv, NUMBER+"=?", new String[]{number});
		 db.close();
	 }
	 
	 public static void updateWords(String myNumber,Context context,Contact person){
		 updateWords(myNumber,context,person.number,person.words);
	 }
	 
	 //修改是否是联系人的信息
	 public static void updateIsContact(String myNumber,Context context,String number,String newIs){
		 if(tabbleIsExist(context, myNumber)==false){
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 ContentValues cv = new ContentValues();
		 cv.put(ISCONTACT,newIs.toUpperCase());
		 
		 myNumber = CONTACTS + "_" + myNumber;
		 db.update(myNumber, cv, NUMBER+"=?", new String[]{number});
		 db.close();
	 }
	 
	 public static void updateIsContact(String myNumber,Context context,Contact person){
		 updateIsContact(myNumber,context,person.number,person.isContact.toUpperCase());
	 }
	 
	 //修改所在的讨论组的信息(删除)
	 public static void updateDiscussionDelete(String myNumber,Context context,String number,String discussion_name){
		 if(discussion_name == null){
			 return ;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		 Contact con = queryByNumber(myNumber, context, number);
		 if(con == null){
			 return ;
		 }
		 String dis = con.discussions;
		 if(dis == null){
			 return ;
		 }
		 List<String> list = getDiscussions(dis);
		 if(list == null || list.contains(discussion_name) == false){//如果该联系人未在任何讨论组里，或者未在discussion_name这个讨论组里，返回
			 return ;
		 }
		 list.remove(discussion_name);
		 ContentValues cv = new ContentValues();
		 cv.put(DISCUSSIONS, getDiscussion(list));
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 db.update(myNumber,cv,NUMBER+"=?",new String[]{number});
		 db.close();	 
	 }
	 
	 //修改所在的讨论组的信息(增加)
	 public static void updateDiscussionAdd(String myNumber,Context context,String number,String discussion_name){
		 if(discussion_name == null){
			 System.out.println("所添加的组为空");
			 return ;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return ;
		 }
		 Contact con = queryByNumber(myNumber, context, number);
		 if(con == null){	
			 System.out.println("找不到要添加组的联系人"+number);
			 return ;
		}
		 System.out.println("找到该联系人");
		 String dis = con.discussions;
		 if(dis != null){System.out.println("现在所在组为"+dis);}
		 List<String> list = null;
		 if(dis == null){//如果该联系人未在任何讨论组里，新建
			 list = new ArrayList<String>();
			 System.out.println("新建");
		 }
		 else{
			 list = getDiscussions(dis);
		 }
		
		 if(list != null && list.contains(discussion_name) == true){//如果该联系人已在discussion_name这个讨论组里，返回
			 return ;
		 } 
		 list.add(discussion_name);
		 ContentValues cv = new ContentValues();
		 cv.put(DISCUSSIONS, getDiscussion(list));
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 db.update(myNumber,cv,NUMBER+"=?",new String[]{number});
		 db.close();	
	 }
	 
	 //根据联系人的账号查询某个用户的某个联系人的信息
	 public static Contact queryByNumber(String myNumber,Context context,String number){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return null;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();

		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, NUMBER+"=?", new String[]{number}, null, null, null);
		 
		 if(cur == null){
			System.out.println("要找的联系人为空");
			 db.close();
			 return null;
		 }
		 Contact person = new Contact();
		if(cur.moveToNext()){
			 person.number = number;
			 person.name = cur.getString(cur.getColumnIndex(NAME));
			 person.sex = cur.getString(cur.getColumnIndex(SEX));
			 person.words = cur.getString(cur.getColumnIndex(WORDS));
			 person.isContact = cur.getString(cur.getColumnIndex(ISCONTACT));
			 person.discussions = cur.getString(cur.getColumnIndex(DISCUSSIONS));
			 cur.close();
			 db.close();
			 return person;
		 }
		cur.close();
		 db.close();
		 return null;
	 }
	 
	 //根据联系人的备注查找联系人
	 public static Contact queryByName(String myNumber,Context context,String name){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return null;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, NAME+"=?", new String[]{name}, null, null, null);
		 
		 if(cur == null){
			 System.out.println("要找的联系人为空");
			 db.close();
			 return null;
		 }
		 Contact person = new Contact();
		if(cur.moveToNext()){
			 person.number = cur.getString(cur.getColumnIndex(NUMBER));
			 person.name = name;
			 person.sex = cur.getString(cur.getColumnIndex(SEX));
			 person.words = cur.getString(cur.getColumnIndex(WORDS));
			 person.isContact = cur.getString(cur.getColumnIndex(ISCONTACT));
			 person.discussions = cur.getString(cur.getColumnIndex(DISCUSSIONS));
			 cur.close();
			 db.close();
			 return person;
		 }
		cur.close();
		 db.close();
		 return null;
	 }
	 
	 //查询所有在联系人列表中的信息
	 public static List<Contact> queryAll(String myNumber,Context context){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return null;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, ISCONTACT+"=?", new String[]{"Y"}, null, null, NAME);
		 if(cur == null){db.close();return null;}
		 List<Contact> persons = new ArrayList<Contact>();
		 while(cur.moveToNext()){
			 Contact person = new Contact();
			 person.number = cur.getString(cur.getColumnIndex(NUMBER));
			 person.name = cur.getString(cur.getColumnIndex(NAME));
			 person.sex = cur.getString(cur.getColumnIndex(SEX));
			 person.words = cur.getString(cur.getColumnIndex(WORDS));
			 person.isContact = cur.getString(cur.getColumnIndex(ISCONTACT));
			 person.discussions = cur.getString(cur.getColumnIndex(DISCUSSIONS));
			 persons.add(person);
		 }
		 cur.close();
		 db.close();
		 return persons;
	 }
	 //查询所有在联系人列表中的信息
	 public static List<String> queryAllNumbers(String myNumber,Context context){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return null;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, ISCONTACT+"=?", new String[]{"Y"}, null, null, NAME);
		 if(cur == null){db.close();return null;}
		 List<String> persons = new ArrayList<String>();
		 while(cur.moveToNext()){
			 persons.add(cur.getString(cur.getColumnIndex(NUMBER)));
		 }
		 cur.close();
		 db.close();
		 return persons;
	 }
	 //查询所有在某个讨论组的人的信息
	 public static List<Contact> queryInDiscussion(String myNumber,Context context,String dis_name){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return null;
		 }
		 if(dis_name == null){
			 return null;
		 }
		 List<Contact> persons = new ArrayList<Contact>();
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, null, null, null, null, null);
		 if(cur == null){db.close();return null;}
		 String dis = "";
		 while(cur.moveToNext()){
			 dis = cur.getString(cur.getColumnIndex(DISCUSSIONS));
			 if(dis == null){
				 continue;
			}
			 if(getDiscussions(dis).contains(dis_name)){//如果该人所在的讨论组里有dis_name这个组的话
				 Contact person = new Contact();
				 person.number = cur.getString(cur.getColumnIndex(NUMBER));
				 person.name = cur.getString(cur.getColumnIndex(NAME));
				 person.sex = cur.getString(cur.getColumnIndex(SEX));
				 person.words = cur.getString(cur.getColumnIndex(WORDS));
				 person.isContact = cur.getString(cur.getColumnIndex(ISCONTACT));
				 person.discussions = dis;
				 persons.add(person);
			 }	 
		 }
		 cur.close();
		 db.close();
		 return persons;
	 }
	 //查询所有在某个讨论组的人的姓名
	 public static List<String> queryNamesInDiscussion(String myNumber,Context context,String dis_name){
		 if(dis_name == null){
			 return null;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return null;
		 }
		 List<String> persons = new ArrayList<String>();
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, null, null, null, null, null);
		 if(cur == null){db.close();return null;}
		 String dis = "";
		 while(cur.moveToNext()){
			 dis = cur.getString(cur.getColumnIndex(DISCUSSIONS));
			 if(dis == null){
				 System.out.println("现在游标所指的联系人所在的组为空");
				 continue;
			}
			 if(getDiscussions(dis).contains(dis_name)){//如果该人所在的讨论组里有dis_name这个组的话
				 persons.add(cur.getString(cur.getColumnIndex(NAME)));
			 }	 
		 }
		 cur.close();
		 db.close();
		 return persons;
	 }
	 //查询所有在某个讨论组的人的姓名
	 public static List<String> queryNumbersInDiscussion(String myNumber,Context context,String dis_name){
		 if(dis_name == null){
			 return null;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return null;
		 }
		 List<String> persons = new ArrayList<String>();
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, null, null, null, null, null);
		 if(cur == null){db.close();return null;}
		 String dis = "";
		 while(cur.moveToNext()){
			 dis = cur.getString(cur.getColumnIndex(DISCUSSIONS));
			 if(dis == null){
				 continue;
			}
			 if(getDiscussions(dis).contains(dis_name)){//如果该人所在的讨论组里有dis_name这个组的话
				 persons.add(cur.getString(cur.getColumnIndex(NUMBER)));
			 }	 
		 }
		 cur.close();
		 db.close();
		 return persons;
	 }

	 //判断一个账号是否在联系人列表中
	 public static boolean isInContacts(String myNumber,Context context,String number){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("无"+myNumber+"表");
			 return false;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, ISCONTACT+"=? and "+NUMBER+"=?", new String[]{"Y",number}, null, null, NAME);
		 if(cur.getCount() == 0){
			 cur.close();
			 db.close();
			 return false;
		 }
		 cur.close();
		 db.close();
		 return true;
	 }
	 
	 //从一个所在讨论组的项里找出所有的讨论组的名字
	 public static List<String> getDiscussions(String discussions){
		 if(discussions == null){
			 return null;
		 }
		 List<String> list = new ArrayList<String>();

		int position1 = 0;
		int position2 = discussions.indexOf(SEPER);
		while(position2 != -1 && position1 != -1){
				list.add(discussions.substring(position1, position2));
				position1 = position2+1;
				position2 = discussions.indexOf(SEPER,position1);
				}
		list.add(discussions.substring(position1));
		return list;
	 }
	 
	 //将一串组群的名字合成为在数据库中显示的名字
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
	 //删除数据库
	 public static void deleteDatabase(String name,Context context){
		 final File file = context.getDatabasePath(name);
         if(file.exists()){
        	 file.delete();
         }
	 }
	 //删除数据库的一张名为name_table的表
	 public static void deleteTable(Context context,String myNumber){
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 try{
		 db.execSQL("drop table if exists "+myNumber);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 db.close();
			 }
	 }
	 
	 //新建一张用户表，名字为该用户的账号
	 public static void newTable(Context context,String myNumber){
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 String sql = "create table if not exists "+myNumber+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,number varchar(11) not null,name varchar(20) not null,sex CHAR,words TEXT,isContact CHAR,discussions text)";
		 db.execSQL(sql);
		 db.close();
	 }
	 //修改一张表的名字
	 public static void renameTable(Context context,String oldNumber,String newNumber){
		 if(tabbleIsExist(context, oldNumber)==false){
			 System.out.println("无"+oldNumber+"表");
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 oldNumber = CONTACTS + "_" + oldNumber;
		 newNumber = CONTACTS + "_" + newNumber;
		 try{
		 String sql = "ALTER TABLE "+oldNumber+" RENAME TO "+newNumber;
		 db.execSQL(sql);
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 db.close();
		 }
	 }
	 //判断一张表是否存在
	  private static boolean tabbleIsExist(Context context,String tableName){
		  boolean result = false;
          if(tableName == null){
                  return false;
          }
          SQLiteDatabase db = null;
          Cursor cursor = null;
          try {
     		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
     		 	tableName = CONTACTS+"_"+tableName;
                  db = sdh.getReadableDatabase();
                  String sql = "select count(*) as c from sqlite_master "+"where type ='table' and name ='"+tableName.trim()+"' ";
                  cursor = db.rawQuery(sql, null);
                  if(cursor.moveToNext()){
                          int count = cursor.getInt(0);
                          if(count>0){
                                  result = true;
                          }
                  }
                  
          } catch (Exception e) {
                  // TODO: handle exception
          }     finally{
        	  cursor.close();
        	  db.close();
          }
          return result;
  }
	  /**
	   * 以下是建立自己回复的通知的一张表
	   * 表的列有主键_id,通知的id号notice_id，通知的回复时间reply_time，回复内容reply_content
	   */
	  public static void newReplyTable(Context context){
		  ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
			 SQLiteDatabase db = sdh.getWritableDatabase();
			 String sql = "create table if not exists "+REPLY_TABLE_NAME+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,notice_id varchar(20) not null, reply_time varchar(20) not null,reply_content TEXT)";
			 db.execSQL(sql);
			 db.close();
	  }
	  //向回复的通知的表中插入一个回复
	  public static void insertReply(Context context,String id,String time,String content){
		  ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
			SQLiteDatabase db = sdh.getWritableDatabase();
		  ContentValues cv=new ContentValues();
		  cv.put(NOTICE_ID , id);
			cv.put(REPLY_TIME, time);
			cv.put(REPLY_CONTENT,content);
			db.insert(REPLY_TABLE_NAME, null, cv);
			db.close();
	  }
	  //根据通知的id号查询一个回复，返回一个回复的对象
	  public static ReplyedNotice queryReplyById(Context context,String id){
		  ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
			SQLiteDatabase db = sdh.getReadableDatabase();
			 Cursor cur = db.query(REPLY_TABLE_NAME, null, NOTICE_ID+"=?", new String[]{id}, null, null, REPLY_TIME+" asc");
			if(cur == null){
				System.out.println("要找的联系人为空");
				 db.close();
				 return null;
			 }
			 ReplyedNotice reply = new ReplyedNotice();
			if(cur.moveToNext()){
				 reply.notice_id = id;
				 reply.reply_time = cur.getString(cur.getColumnIndex(REPLY_TIME));
				 reply.reply_content = cur.getString(cur.getColumnIndex(REPLY_CONTENT));
				 db.close();
				 cur.close();
				 return reply;
			 }
			cur.close();
			 db.close();
			 return null;		
	  }
} 
