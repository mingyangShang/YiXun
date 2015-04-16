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
 * ����ǹ���ÿ���û���ÿ���û�����ϵ�˵��࣬ÿ��һ���û�ʹ�þͻ������ݿ��ж�����һ�ű��������Ϊcontact_�û����˺�
 * ���ӱ�ĺ���ΪnewTable����,�����ں���ǰ��д���Լ����Ե�ʱ��һ��Ҫ������������½�һ�ű�Ȼ�����
 * ��ŵĺ�������������ʲô�ģ���ÿ��������ǰ�涼д���أ�ʹ�ù�������ʲô���Ե����ϸ����ң���Ҳ�Ǹտ����ݿ⣬Ҳ���Ǻ���
 *
 */
public class DatabaseManager {
	private static final int VERSION = 1;
	 private static final String CONTACTS = "contacts";
	 
	 private static final String NUMBER = "number";
	 private static final String NAME = "name";
	 private static final String SEX = "sex";
	 private static final String WORDS = "words";
	
	 private static final String ISCONTACT = "isContact";//�Ƿ����Լ�����ϵ��
	 private static final String DISCUSSIONS = "discussions";//���ڵķ���
	 private static final String SEPER = "~";//�����ݿ��д洢ʱ������ķָ���
	 private static final String REPLY_TABLE_NAME="reply_table";//�ظ���֪ͨ�ı�
	 private static final String NOTICE_ID="notice_id",REPLY_TIME="reply_time",REPLY_CONTENT="reply_content";
	 
	 //��ĳ���û�����ϵ�����ݿ�������һ����ϵ�ˣ���ʵ��һ������ϵ�ˣ�Ҳ�������������һ���˶�������ϵ���б��У�
	 public static void insertContact(String myNumber,Context context,String number,String name,String sex,String words,String isContact,String discussion){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
			 return ;
		 }
		ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		SQLiteDatabase db = sdh.getWritableDatabase();
		ContentValues cv = new ContentValues();
		Contact per = null;
		if((per = queryByNumber(myNumber, context, number)) != null){//���˺ŵ���ϵ���Ѿ�����
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
		cv.put(ISCONTACT, isContact);//�Ƿ�����ϵ��
		cv.put(DISCUSSIONS, discussion);//���ĸ���������
		
		myNumber = CONTACTS + "_" + myNumber;
		db.insert(myNumber,null,cv);
		db.close();
	}
	
	 //����ڽ�����ϵ���б�����ʾ����ϵ��
	 public static void insertContact(String myNumber,Context context,String number,String name,String sex,String words){
		 insertContact(myNumber,context,number,name,sex,words,"Y",null);
	 }
	 public static void insertContact(String myNumber,Context context,String number,String name,String sex,String words,String discussion){
		 insertContact(myNumber,context,number,name,sex,words,"Y",discussion);
	 }
	 
	/* public static void insertContact(String myNumber,Context context,Contact person){
		 insertContact(myNumber, context, person.number,person.name,person.sex,person.words,person.isContact,person.discussions);
	 }*/
	 
	 //���¼��뵽һ��Ⱥ��ʱ����ĳ��Ⱥ��Ա����Ϣ
	 public static void insertContactFromDiscussion(String myNumber,Context context,String number,String name,String sex,String words,String discussion){
		 insertContact(myNumber,context,number,name,sex,words,"N",discussion);
	 }
	 
	//��ĳ���û�����ϵ���б���ɾ��һ����ϵ��
	 public static void deleteContact(String myNumber,Context context,String number){
		 //�������˲����κ����������ôɾ��
		 //����������ĳ�����������isContact��Ϊ��N��
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 Contact con = queryByNumber(myNumber, context, number);
		 if(con == null){
			 System.out.println("��Ҫɾ������ϵ��");
			 db.close();
			 return ;
		 }
		 if(con.discussions == null){
			 System.out.println("Ҫɾ������ϵ�����ڵ���Ϊ��");
			 myNumber = CONTACTS + "_" + myNumber;
			 db.delete(myNumber,NUMBER+"=?", new String[]{number});	
		 }
		 else{
			 System.out.println("��ϵ�����ڵ��鲻Ϊ�գ���ʼ�����Ƿ�����ϵ�˵���Ϣ");
			 updateIsContact(myNumber, context, number, "N");
		 }
		 db.close();
	 }
	 public static void deleteContact(String myNumber,Context context,Contact person){
		 deleteContact(myNumber, context, person.number);
	 } 
	 //ɾ����ĳ�����������
	 public static void deleteInDiscussion(String myNumber,Context context,String dis_name){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
				 System.out.println("�����α���ָ����ϵ�����ڵ���Ϊ��");
				 continue;
			 }
			 list = getDiscussions(dis);
			 number=cur.getString(cur.getColumnIndex(NUMBER));
			 if(list.contains(dis_name)){//����������ڵ�����������dis_name�����Ļ�
				 updateDiscussionDelete(myNumber, context, cur.getString(cur.getColumnIndex(NUMBER)), dis_name);
				 //�������˼Ȳ����Լ�����ϵ����ֻ����һ�����������ôɾ�������
				 if(list.size() == 1 && cur.getString(cur.getColumnIndex(ISCONTACT)).equals("N")){
					 myNumber = CONTACTS + "_" + myNumber;
					 db.delete(myNumber, "number=?", new String[]{number});
				 }
			 }	 
		 }
		 db.close();
	 }
	 //ɾ�������ڸñ��е��˵���Ϣ
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
	 
	 //�޸�ĳ���û����˺���Ϣ
	 public static void updateNumber(String myNumber,Context context,String oldNumber,String newNumber){
		 if(tabbleIsExist(context, myNumber)==false){
			 return ;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 //������˺Ų�����
		 if(queryByNumber(myNumber, context, oldNumber) == null){
			 db.close();
			 return ;
		 }
		 //������˺��Ѿ�����
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
	 
	 //�޸�ĳ���û���ĳ����ϵ�˵�������Ϣ
	 public static void updateName(String myNumber,Context context,String number,String newName){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
	 
	 //�޸�ĳ���û���ĳ����ϵ�˵��Ա���Ϣ
	 public static void updateSex(String myNumber,Context context,String number,String newSex){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
	 
	 //�޸�ĳ���û���ĳ����ϵ�˵�ǩ����Ϣ
	 public static void updateWords(String myNumber,Context context,String number,String newWords){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
	 
	 //�޸��Ƿ�����ϵ�˵���Ϣ
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
	 
	 //�޸����ڵ����������Ϣ(ɾ��)
	 public static void updateDiscussionDelete(String myNumber,Context context,String number,String discussion_name){
		 if(discussion_name == null){
			 return ;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
		 if(list == null || list.contains(discussion_name) == false){//�������ϵ��δ���κ������������δ��discussion_name��������������
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
	 
	 //�޸����ڵ����������Ϣ(����)
	 public static void updateDiscussionAdd(String myNumber,Context context,String number,String discussion_name){
		 if(discussion_name == null){
			 System.out.println("����ӵ���Ϊ��");
			 return ;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
			 return ;
		 }
		 Contact con = queryByNumber(myNumber, context, number);
		 if(con == null){	
			 System.out.println("�Ҳ���Ҫ��������ϵ��"+number);
			 return ;
		}
		 System.out.println("�ҵ�����ϵ��");
		 String dis = con.discussions;
		 if(dis != null){System.out.println("����������Ϊ"+dis);}
		 List<String> list = null;
		 if(dis == null){//�������ϵ��δ���κ���������½�
			 list = new ArrayList<String>();
			 System.out.println("�½�");
		 }
		 else{
			 list = getDiscussions(dis);
		 }
		
		 if(list != null && list.contains(discussion_name) == true){//�������ϵ������discussion_name��������������
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
	 
	 //������ϵ�˵��˺Ų�ѯĳ���û���ĳ����ϵ�˵���Ϣ
	 public static Contact queryByNumber(String myNumber,Context context,String number){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
			 return null;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();

		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, NUMBER+"=?", new String[]{number}, null, null, null);
		 
		 if(cur == null){
			System.out.println("Ҫ�ҵ���ϵ��Ϊ��");
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
	 
	 //������ϵ�˵ı�ע������ϵ��
	 public static Contact queryByName(String myNumber,Context context,String name){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
			 return null;
		 }
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 Cursor cur = db.query(myNumber, null, NAME+"=?", new String[]{name}, null, null, null);
		 
		 if(cur == null){
			 System.out.println("Ҫ�ҵ���ϵ��Ϊ��");
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
	 
	 //��ѯ��������ϵ���б��е���Ϣ
	 public static List<Contact> queryAll(String myNumber,Context context){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
	 //��ѯ��������ϵ���б��е���Ϣ
	 public static List<String> queryAllNumbers(String myNumber,Context context){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
	 //��ѯ������ĳ����������˵���Ϣ
	 public static List<Contact> queryInDiscussion(String myNumber,Context context,String dis_name){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
			 if(getDiscussions(dis).contains(dis_name)){//����������ڵ�����������dis_name�����Ļ�
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
	 //��ѯ������ĳ����������˵�����
	 public static List<String> queryNamesInDiscussion(String myNumber,Context context,String dis_name){
		 if(dis_name == null){
			 return null;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
				 System.out.println("�����α���ָ����ϵ�����ڵ���Ϊ��");
				 continue;
			}
			 if(getDiscussions(dis).contains(dis_name)){//����������ڵ�����������dis_name�����Ļ�
				 persons.add(cur.getString(cur.getColumnIndex(NAME)));
			 }	 
		 }
		 cur.close();
		 db.close();
		 return persons;
	 }
	 //��ѯ������ĳ����������˵�����
	 public static List<String> queryNumbersInDiscussion(String myNumber,Context context,String dis_name){
		 if(dis_name == null){
			 return null;
		 }
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
			 if(getDiscussions(dis).contains(dis_name)){//����������ڵ�����������dis_name�����Ļ�
				 persons.add(cur.getString(cur.getColumnIndex(NUMBER)));
			 }	 
		 }
		 cur.close();
		 db.close();
		 return persons;
	 }

	 //�ж�һ���˺��Ƿ�����ϵ���б���
	 public static boolean isInContacts(String myNumber,Context context,String number){
		 if(tabbleIsExist(context, myNumber)==false){
			 System.out.println("��"+myNumber+"��");
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
	 
	 //��һ������������������ҳ����е������������
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
	 
	 //��һ����Ⱥ�����ֺϳ�Ϊ�����ݿ�����ʾ������
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
	 //ɾ�����ݿ�
	 public static void deleteDatabase(String name,Context context){
		 final File file = context.getDatabasePath(name);
         if(file.exists()){
        	 file.delete();
         }
	 }
	 //ɾ�����ݿ��һ����Ϊname_table�ı�
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
	 
	 //�½�һ���û�������Ϊ���û����˺�
	 public static void newTable(Context context,String myNumber){
		 ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
		 SQLiteDatabase db = sdh.getWritableDatabase();
		 myNumber = CONTACTS + "_" + myNumber;
		 String sql = "create table if not exists "+myNumber+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,number varchar(11) not null,name varchar(20) not null,sex CHAR,words TEXT,isContact CHAR,discussions text)";
		 db.execSQL(sql);
		 db.close();
	 }
	 //�޸�һ�ű������
	 public static void renameTable(Context context,String oldNumber,String newNumber){
		 if(tabbleIsExist(context, oldNumber)==false){
			 System.out.println("��"+oldNumber+"��");
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
	 //�ж�һ�ű��Ƿ����
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
	   * �����ǽ����Լ��ظ���֪ͨ��һ�ű�
	   * �����������_id,֪ͨ��id��notice_id��֪ͨ�Ļظ�ʱ��reply_time���ظ�����reply_content
	   */
	  public static void newReplyTable(Context context){
		  ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
			 SQLiteDatabase db = sdh.getWritableDatabase();
			 String sql = "create table if not exists "+REPLY_TABLE_NAME+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,notice_id varchar(20) not null, reply_time varchar(20) not null,reply_content TEXT)";
			 db.execSQL(sql);
			 db.close();
	  }
	  //��ظ���֪ͨ�ı��в���һ���ظ�
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
	  //����֪ͨ��id�Ų�ѯһ���ظ�������һ���ظ��Ķ���
	  public static ReplyedNotice queryReplyById(Context context,String id){
		  ContactDatabaseHelper sdh = new ContactDatabaseHelper(context);
			SQLiteDatabase db = sdh.getReadableDatabase();
			 Cursor cur = db.query(REPLY_TABLE_NAME, null, NOTICE_ID+"=?", new String[]{id}, null, null, REPLY_TIME+" asc");
			if(cur == null){
				System.out.println("Ҫ�ҵ���ϵ��Ϊ��");
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
