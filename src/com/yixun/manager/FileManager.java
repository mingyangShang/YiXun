
package com.yixun.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yixun.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileManager {
	
	private static final String APP_NAME = "yixun";//Ӧ�ó�������֣�Ҳ�����ļ���
	private static final String CONSTANTS= "contacts";//��ϵ�����ļ��е�����
	private static final String DISCUSSION_GROUP = "discussion";//���������ļ��е�����
	private static final String MY_GROUP = "group";//�Զ��������ļ�������
	private static final String NOTICE = "notices";//֪ͨ���ļ��е�����
	private static final String DISCUSSION_IMG = "img_dis";//���Ⱥ��ͷ����ļ��е�����
	private static final String CONTACT_IMG = "img_con";//�����ϵ��ͷ����ļ���
	private static final String SEND_NOTICE = "send";//����֪ͨ���ļ���
	private static final String RECEIVE_NOTICE = "receive";//�յ�֪ͨ���ļ���
	private static final String REPLY_NOTICE = "reply";//�յ�֪ͨ���ļ���

	
	private static final String INFOR = "information";//������Ϣ�������������Ϣ
	private static final int LEN_OF_NUMBER = 11;
	private static final String SEPER = "/";//·��֮��ķָ���
	private static final String MAIN_PATH = Environment.getExternalStorageDirectory().toString()+SEPER;
	private static final String RECORD = "record";//�����¼
	private static final String SUFFIX = ".txt";//�ı��ļ��ĺ�׺
	
	private static final String SEPER_NUMBER = ",";//�˺�֮��ķָ���
	private static final String SEPER_ID_AND_COUNT = "_";//֪ͨ��id�ͽ���֪ͨ������֮��ķָ���
	/*public static void main(String[] args){
		
		try{
			System.out.println("��·����"+MAIN_PATH);
			System.out.println("��ʼִ���½��ļ�����");
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
	//�½�һ���û����ļ���
	public static void newUser(Context con,String myNumber){
		//�ļ�
		createMain();
		createNumber(myNumber);//���Լ��˺ŵ��ļ���
		createContacts(myNumber);//����ϵ�˵��ļ���
		createDiscussion(myNumber);//������������ļ���
		createGroup(myNumber);//��������ļ���
		createConImage(myNumber);//������ϵ�˵�ͷ����ļ��ļ���
		createDisImage(myNumber);//�������ͷ����ļ���
		createNotice(myNumber);//����֪ͨ���ļ���
		createReceiveNotice(myNumber);//�����յ���֪ͨ���ļ���
		createSendNotice(myNumber);//��������֪ͨ���ļ���
//		newReplyNotice(myNumber);//������֪ͨ���ļ�
		//���ݿ�
		DatabaseManager.newTable(con.getApplicationContext(),myNumber);//��ϵ�˵ı�
		DatabaseManager.newReplyTable(con.getApplicationContext());//�ظ���֪ͨ�ı�

		//��Ĭ�ϵ�ͷ����Ϊ�û���ͷ������ļ�
		Bitmap bitmap=BitmapFactory.decodeResource(con.getResources(), R.drawable.defaultcontact);
		
	}
	//�½�һ����ϵ�˵��ļ��У��������˺ţ��������ֻ��ţ�11λ��
	public static boolean newContact(String myNumber,String number) {
		if(number.length() != LEN_OF_NUMBER){
			try{
			throw new Exception("�˺ű�����"+LEN_OF_NUMBER+"λ");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + CONSTANTS + SEPER;
		File parent = null;
		try{
			createMain();//�����ļ���
			createNumber(myNumber);//���Լ��˺ŵ��ļ���
			createContacts(myNumber);//����ϵ���ļ���
		
			File file = new File(path+number);//�������ϵ�˵��ļ���
			if(file.exists() == true){return false;}//��������ϵ���Ѿ����ڵĻ�����false
			if(file.exists() == false){
				if(file.mkdir() == false){
					throw new Exception("������ϵ�˵��ļ��в��ܱ�����");
				}
				
				path +=  number + SEPER;
				File record = new File(path+RECORD+SUFFIX);//�������¼���ļ�
				if(record.exists() == false){
					if(record.createNewFile() == false){
						throw new Exception("������ϵ�˵������¼�ļ����ܴ���");
					}
				}	
			}
			
		}catch(FileNotFoundException f){
			System.out.println("�ļ�û���ҵ�");
			f.printStackTrace();
		}catch(IOException e){
			System.out.println("����IO����");
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("�����ɹ�");
		return true;
	}
	//�½�һ��������(������������˺�)
	public static boolean  newDiscussion(String myNumber,String number){
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + DISCUSSION_GROUP + SEPER;
		createMain();//�����ļ���
		createNumber(myNumber);//���Լ��˺ŵ��ļ���
		createDiscussion(myNumber);
		File file = null;
		try{
			file = new File(path+number);
			if(file.exists() == false)	file.mkdir();//����������ļ���
			else{return false;}//����Ը��˺Ż������������������Ѿ����ڣ�����false
			path += number + SEPER;//��һ����·��
			file = new File(path+RECORD+SUFFIX);
			if(file.exists() == false)	file.createNewFile();//��������������¼�ļ�
			/*file = new File(path+INFOR+SUFFIX);
			if(file.exists() == false)	file.createNewFile();//�����������Ϣ�ļ�
*/		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;

	}
	//�½�һ���Զ���Ⱥ�飨�����Զ���������֣��������ɹ��Ļ�����true�����֮ǰ������Խ���Ⱥ��Ļ�����false
	public static boolean  newGroup(String myNumber,String name){
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER;
		File file = null;
		try{
			createMain();//�����ļ���
			createNumber(myNumber);//���Լ��˺ŵ��ļ���
			createGroup(myNumber);//�����Զ���������ļ���
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
	//�½�һ��֪ͨ�ļ���֮ǰû�д����ɹ��Ļ�����true�����֮ǰ�Ѿ�������ļ��Ļ�������false
	public static boolean newSendNotice(String myNumber,String id,int count){
		String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + NOTICE + SEPER + SEND_NOTICE +	 SEPER;
		File file =null;
		try{
			createMain();//�����ļ���
			createNumber(myNumber);//���Լ��˺ŵ��ļ���
			createNotice(myNumber);//����֪ͨ�ļ���
			createSendNotice(myNumber);//��������֪ͨ���ļ���
			file = new File(path+id+SEPER_ID_AND_COUNT+String.valueOf(count)+SUFFIX);
			if(file.exists() == false)	file.createNewFile();//����������֪ͨ��id��Ϊ�ļ������ļ�
			else{return false;}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	//�����յ���֪ͨ���ļ�
	public static void newReplyNotice(String myNumber){
		try{
			createMain();//�����ļ���
			createNumber(myNumber);//���Լ��˺ŵ��ļ���
			createNotice(myNumber);//����֪ͨ�ļ���
			createReplyNotice(myNumber);//�����ظ�֪ͨ���ļ���
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
	//�����Լ��ظ���֪ͨ���ļ�
	public static void newReceiveNotice(String myNumber){
		try{
			createMain();//�����ļ���
			createNumber(myNumber);//���Լ��˺ŵ��ļ���
			createNotice(myNumber);//����֪ͨ�ļ���
			createReceiveNotice(myNumber);//��������֪ͨ���ļ���
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
	//�ڴ��Ⱥ��ͷ����ļ��������ĳһ��Ⱥ�����Ƭ
	public static void newDisImg(String myNumber,Bitmap bm,String name){
		final String path =  MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +DISCUSSION_IMG + SEPER + name;

		try{
			createMain();//�����ļ���
			createNumber(myNumber);//���Լ��˺ŵ��ļ���
			createDisImage(myNumber);//�����Ⱥ��ͷ����ļ���
			saveBitmap(path,bm);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//�ڴ����ϵ��ͷ����ļ��������һ����ϵ�˵�ͷ��ͼƬ
	public static void newConImg(String myNumber,Bitmap bm,String name){
		final String path =  MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG	+ SEPER + name;
		System.out.println(path);
		try{
			createMain();//�����ļ���
			createNumber(myNumber);//���Լ��˺ŵ��ļ���
			createConImage(myNumber);//�������ϵ��ͷ����ļ���
			//��ʼ����λͼ
			saveBitmap(path,bm);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//��ȡ��ϵ�˵�ͷ��
	public static Bitmap readImgFromContact(String myNumber,String name){
		final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG + SEPER + name;
		Bitmap bitmap = null;
		try{
			BitmapFactory.Options opts = new BitmapFactory.Options();
		   	opts.inSampleSize = 2; //�����ֵѹ���ı�����2��������������ֵԽС��ѹ����ԽС��ͼƬԽ����
		   	//����ԭͼ����֮���bitmap����
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
		   System.out.println("�ļ������ɹ�");
		   FileOutputStream out = new FileOutputStream(f);
		   bm.compress(Bitmap.CompressFormat.PNG, 90, out);
		   out.flush();
		   out.close(); 
		   System.out.println("���");
		  } catch (FileNotFoundException e) {
				  throw new Exception("�ļ�û���ҵ�");
		  } catch (IOException e) {
		   throw new Exception("io����");
		  }
	 }
	//��ȡȺ���ͷ��
	public static Bitmap readImgFromDis(String myNumber,String name){
		final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +DISCUSSION_IMG + SEPER + name;
		Bitmap bitmap = null;
		try{
			BitmapFactory.Options opts = new BitmapFactory.Options();
		   	opts.inSampleSize = 4; //�����ֵѹ���ı�����2��������������ֵԽС��ѹ����ԽС��ͼƬԽ����
		   	//����ԭͼ����֮���bitmap����
		   bitmap=BitmapFactory.decodeFile(path, opts);		
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	//������������������
	public static List<String> getAllDiscussions(String myNumber){
	final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + DISCUSSION_GROUP;
	return getChildFileName(path);
	}
	//��������Զ����������
	public static List<String> getAllGroup(String myNumber){
	final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP;
	return getChildFileName(path);
	}
	//���һ���ļ��������е�һ���ļ�������
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
	//�����ļ���
	public static void createMain() {
		try{
			File main = new File(MAIN_PATH + APP_NAME);//�� ���ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("���ļ��в��ܱ�����");
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
	//����¼�˺ŵ��ļ���
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
	//����ϵ�˵��ļ���
	private static void createContacts(String number){
		try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + CONSTANTS);//����ϵ�˵��ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("��ϵ���ļ��в��ܱ�����");
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
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + DISCUSSION_GROUP);//����������ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("�������ļ��в��ܱ�����");
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
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + MY_GROUP);//����ϵ�˵��ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("�Խ����ļ��в��ܱ�����");
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
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER +NOTICE);//����ϵ�˵��ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("֪ͨ�ļ��в��ܱ�����");
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
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + NOTICE + SEPER + SEND_NOTICE);//������֪ͨ���ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("����֪ͨ�ļ��в��ܱ�����");
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
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + NOTICE + SEPER + RECEIVE_NOTICE);//����ϵ�˵��ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("�յ�֪ͨ���ļ��в��ܱ�����");
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
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + NOTICE + SEPER + REPLY_NOTICE);//����ϵ�˵��ļ���
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("�յ�֪ͨ���ļ��в��ܱ�����");
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
    //�������������ͷ����ļ���
    private static void createDisImage(String number){
    	try{
			File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + DISCUSSION_IMG);
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("���Ⱥ��ͼƬ���ļ��в��ܱ�����");
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
    //���������ϵ��ͷ����ļ���
    private static void createConImage(String number){
    	try{
    		File main = new File(MAIN_PATH + APP_NAME + SEPER + number + SEPER + CONTACT_IMG);
			if(main.exists() == false){
				if(main.mkdir() == false){
					throw new Exception("�����ϵ��ͷ����ļ��в��ܱ�����");
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
    //ɾ��һ���û������е�
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
  //�ݹ�ɾ��Ŀ¼�е���Ŀ¼��
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
        // Ŀ¼��ʱΪ�գ�����ɾ��
        return dir.delete();
    }
    //ɾ��ĳ����ϵ�˵�ͷ���ļ�
    public static void deleteContactImg(String myNumber,String number){
		try{
			File file = new File(MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG + SEPER + number);
			if(file.exists()){
				file.delete();
			}
		}catch(Exception e){
			
		}
    }
    //ɾ��ĳ���Զ������ͷ���ļ�
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
    //ɾ���Զ������
    public static void deleteGroup(String myNumber,String groupName){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + groupName;
    	try{
    		File file = new File(path);
    		if(file.exists() == false)	return ;
    		File[] files = file.listFiles();
    		for(File f:files){
    			f.delete();//ɾ���ļ����µĸ����ļ�
    		}
    		file.delete();//ɾ������ļ���
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    //ɾ��ĳ����ϵ��
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
    //ɾ��ĳ��������
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
    //ɾ��һ��������֪ͨ
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
    //��ĳ���Զ�����������һ����Ա
    public static void addMemberToGroup(String myNumber,String name_group,String number_member){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + name_group +	 SEPER + name_group + SUFFIX;
    	System.out.println("path:"+path);
    	createMain();
    	createGroup(myNumber);
    	newGroup(myNumber, name_group);
    	FileOutputStream out = null;
    	System.out.println("��ʼtry��");
    	try{
    		File file = new File(path);
    		ArrayList<String> members = getMembersFromGroup(myNumber,name_group);//�õ����������г�Ա
    		out = new FileOutputStream(file,true);//Ĭ��Ϊtrue
    		if(members==null){
    			System.out.println("��");
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
    //��ĳ���Զ�����������һ�����ϵĳ�Ա
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
    		ArrayList<String> members = getMembersFromGroup(myNumber,name_group);//�õ����������г�Ա
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

    //��ĳ���Զ��������ɾ��һ����Ա
    public static void deleteMemberFromGroup(String myNumber,String name_group,String number_member){
    	String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER + MY_GROUP + SEPER + name_group +	 SEPER + name_group + SUFFIX;
    	ArrayList<String> members = getMembersFromGroup(myNumber, name_group);
    	if(members.contains(number_member) == false){
    		return ;
    	}
    	members.remove(number_member);
    	System.out.println("�Ѵ��ļ���ɾ��"+number_member);
    	System.out.println("��ʼ�����");
    	
    	
    	addMembersToGroup(myNumber, name_group, members);

    }
    //��ĳ���Զ��������ɾ��һ�����ϵĳ�Ա
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
    //������ĳ���Զ���������г�Ա���˺�
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
    		System.out.println("�ر�ʧ��");
    		e.printStackTrace();
    	}
    	return members;
    }
   /* //�����˺�Ϊnumber����ϵ�˵���Ϣ���ļ���
    public static String toContactInformation(String myNumber,String number){
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+CONSTANTS+SEPER+number+SEPER+INFOR+SUFFIX;
    }*/
    //������ϵ��ͼƬ���ڵ��ļ���
    public static String toContactImageFile(String myNumber){
    	return MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG;//	+ SEPER + name;

    }
    //�����˺�Ϊnumber����ϵ�˵������¼���ļ���
    public static String toContactRecord(String myNumber,String number){
    	newContact(myNumber, number);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+CONSTANTS+SEPER+number+SEPER+RECORD+SUFFIX;
    }
   /* //����ĳ�����������Ϣ���ļ���
    public static String toDiscussionInformation(String myNumber,String name){
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+DISCUSSION_GROUP+SEPER+name+SEPER+INFOR+SUFFIX;
    }*/
    //����ĳ��������ļ�¼���ļ���
    public static String toDiscussionRecord(String myNumber,String name){
    	newDiscussion(myNumber, name);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+DISCUSSION_GROUP+SEPER+name+SEPER+RECORD+SUFFIX;
    }
    //����ĳ���Խ�����ļ���
    public static String toGroup(String myNumber,String name){
    	newGroup(myNumber, name);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+MY_GROUP+SEPER+name+SEPER+name+SUFFIX;
    }
    //���ط��͵�ĳ��֪ͨ���ļ���
    public static String toSendNotice(String myNumber,String id,int count){
    	//newSendNotice(myNumber, id, count);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+NOTICE+SEPER+SEND_NOTICE+SEPER+id+SEPER_ID_AND_COUNT+count+SUFFIX;
    }
    public static String toSendNotice(String myNumber,String filename){
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+NOTICE+SEPER+SEND_NOTICE+SEPER+filename+SUFFIX;
    }
    //�����յ���֪ͨ���ļ���
    public static String toReceiveNotice(String myNumber){
    	newReceiveNotice(myNumber);
    	return MAIN_PATH+APP_NAME+SEPER+myNumber+SEPER+NOTICE+SEPER+RECEIVE_NOTICE+SEPER+RECEIVE_NOTICE+SUFFIX;
    }
    //����һ���˵�ͷ����ļ�λ��
    public static String toContactImage(String myNumber,String number){
		final String path = MAIN_PATH + APP_NAME + SEPER + myNumber + SEPER +CONTACT_IMG + SEPER + number;
		return path;
    }
    //����֪ͨ��id
    public static String toId(){
    	return TimeManager.toSaveFormat(System.currentTimeMillis());
    }
  //��һ���˺ŵ�����ת��Ϊһ�����ָ������ַ���
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
