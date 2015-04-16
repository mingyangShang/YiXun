package com.yixun.manager;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.myview.PinyinComparator;

public class DataManager {
	public static List<Map<String,Object> > data_chat = null;//chatFragment������
	public static List<Map<String,Object> > data_receive,data_send = null;//��ʾ��listview�е����ݼ���
	public static List<Map<String,Object>>  data_contact,data_discussion,data_group = null;//��ʾ��listview�е����ݼ���
	public static Map<String, SoftReference<Bitmap>> imageCache=null;// = new HashMap<String, SoftReference<Bitmap>>();
	public static String myNumber=null;

	//�洢��chatfragment��ʹ�õ�����
	public static void getChatData(final String currentUserNumber,final Context context){
		/**
		 * �����Ĳ�ѯ���ݣ������ϴε�����
		 * ��Ҫ�����Ǹ��˵ĶԻ�����������ĶԻ�
		 * ���ڲ��õ���ͼƬ�������ã������Ǹ���ͼƬ��������
		 */
		if(myNumber==null){
			myNumber=SettingUtils.get(context, ShardPre.currentNumber, "");
		}
		if(imageCache==null){//�����û�н���ͼƬ�������õĻ�������
			getContactBitmap(context);
		}
		data_chat=new ArrayList<Map<String,Object>>();
		String suffix=".png";
		String flag="";
		Map<String,String> lastRecord = null;//һ����¼�ļ������һ����Ϣ
		Contact contact=null;
		MessageManager mm = null;//��Ϣ�Ĺ�����Ķ���
//		String myNumber=SettingUtils.get(context, ShardPre.currentNumber, "");
		ArrayList<String> numbers=SettingUtils.getData(context,Constants.SHARED_KEY_CHAT);
		if(numbers==null){
			System.out.println("��ʱ������");
			return ;
		}
		Bitmap bitmap=ImageTools.minBitmap(context, R.drawable.defaultcontact);
		for(String number:numbers){
			Map<String,Object> map = new HashMap<String,Object>();
			flag=number.substring(0, 1);
			if(flag.equals("0")){//˵���Ǹ���֮��ĶԻ�
				contact=DatabaseManager.queryByNumber(myNumber, context, number);
				map.put("number", number);
				map.put("person", true);
				if(imageCache.get(number)==null){
					  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, number+".png"));	  
					  imageCache.put(number, bitmapcache);		  
				}
				map.put("head",imageCache.get(number));
				map.put("name", contact.name);
				try{
					mm = new MessageManager(FileManager.toContactRecord(currentUserNumber, contact.number));
					}catch(Exception e){
						System.out.println("messageManager���������");
					}
					lastRecord = mm.getLastRecord();
					map.put("message", lastRecord.get("content"));
					map.put("time", TimeManager.toDisplayFormat(TimeManager.toDate(mm.getTime(lastRecord.get("head")))));
			}else{//������֮��ĶԻ�
				map.put("number", "");
				map.put("person", false);
				//��ȡͷ�����ǹ̶��ģ����Բ���ÿ�ζ�����
				map.put("head", bitmap);
//				map.put("head",FileManager.readImgFromContact(currentUserNumber, number+suffix));
				map.put("name",number);
				try{
					mm = new MessageManager(FileManager.toDiscussionRecord(currentUserNumber, number));
					}catch(Exception e){
						System.out.println("messageManager���������");
					}
				map.put("message", lastRecord.get("content"));
				map.put("time", TimeManager.toDisplayFormat(TimeManager.toDate(mm.getTime(lastRecord.get("head")))));
			}
			data_chat.add(map);
		}
		
		
		//
		/*data_chat = new ArrayList<Map<String,Object> >();
		String suffix = ".png";
		List<Contact> contacts = DatabaseManager.queryAll(currentUserNumber,context );
		if(contacts==null){return ;}
		MessageManager mm = null;//��Ϣ�Ĺ�����Ķ���
		Map<String,String> lastRecord = null;//һ����¼�ļ������һ����Ϣ
		for(Contact contact:contacts){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("head",FileManager.readImgFromContact(currentUserNumber, contact.number+suffix));
			map.put("name", contact.name);
			map.put("number", contact.number);
			map.put("person", true);
			try{
			mm = new MessageManager(FileManager.toContactRecord(currentUserNumber, contact.number));
			}catch(Exception e){
				System.out.println("messageManager���������");
			}
			lastRecord = mm.getLastRecord();
			map.put("message", lastRecord.get("content"));
			map.put("time", TimeManager.toDisplayFormat(TimeManager.toDate(mm.getTime(lastRecord.get("head")))));
			data_chat.add(map);*/
//		}
//		}
//	}).start();
		
	}
	
	 //�õ����յ�֪ͨ������
	  public static  void getReceiveData(Context context){
		 data_receive = new ArrayList<Map<String,Object>>(); 
		 ArrayList<String> ids=SettingUtils.getData(context,Constants.SHARED_KEY_RECEIVE_NOTICE);
		 if(ids==null){
			 return ;
		 }
		 ReceivedNoticeManager nm = null;
		 try {
			nm = new ReceivedNoticeManager(FileManager.toReceiveNotice(SettingUtils.get(context, ShardPre.currentNumber, "")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* 
		 * �����Ĳ�ѯ����
		 **��id��ʼ�ҵ�ÿ����Ϣ
		 */
		 ArrayList<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		 String head="",content="",number="";
		 for(String id:ids){
			 Map<String,Object>  map = new HashMap<String,Object>();
			 Map<String,String> notice_find=nm.getNoticeById(id);
			 head=notice_find.get("head");
//			Ҫ�������ͼ�� map.put("head", );
			 number=nm.getNumber(head);
			 map.put("number", number);
			 if(imageCache.get(number)==null){
			  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, number+".png"));	  
			  imageCache.put(number, bitmapcache);
			 }
			 map.put("head", imageCache.get(number));
			 map.put("name", DatabaseManager.queryByNumber(SettingUtils.get(context, ShardPre.currentNumber, ""), context, nm.getNumber(head)).number);
			 map.put("message", notice_find.get("content"));
			 map.put("id",id);
			 map.put("send", false);
			 map.put("time", 
	TimeManager.toDisplayFormat(TimeManager.toDate(nm.getTime(head))));
			 data_receive.add(map);
//			 list.add(map);
		 }
		/* String id = "1214";
		 for(int i=0;i<20;++i){
			 Map<String,Object>  map = new HashMap<String,Object>();
			 map.put("head", R.drawable.icon);
			 map.put("number", "22222222222");
			 map.put("name","������");
			 map.put("message",nm.getDataFromBack(1).get(0).get("content"));
			 map.put("id", id);
			 map.put("send", false);
			 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(nm.getTime(nm.getNoticeById("1214").get("head")))));
			 data_receive.add(map);
		 }*/
	  }
	  //�õ�����֪ͨ������
	  public static  void getSendData(Context context){
		  //�����Ĵ洢����
	    ArrayList<String> ids=SettingUtils.getData(context,Constants.SHARED_KEY_RECEIVE_NOTICE);
	    if(ids==null){
	    	return ;
	    }
		data_send = new ArrayList<Map<String,Object>>(); 
		SendNoticeManager snm = null;
		int number=-1;
	    for(String id:ids){
			 Map<String,Object>  map = new HashMap<String,Object>();
			 try {
					snm = new SendNoticeManager(FileManager.toSendNotice(SettingUtils.get(context, ShardPre.currentNumber, ""),id));
				} catch (IOException e) {
					e.printStackTrace();
				}
			 map.put("number", myNumber);
			 map.put("id", id);//���id��֪ͨ���ļ���
			 map.put("send",true);
			 if(map.get(myNumber)==null){
				  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, myNumber+".png"));	  
				  imageCache.put(myNumber, bitmapcache);
			 }
			 //������Լ���ͷ��map.put("head", R.drawable.defaultcontact);
			 map.put("ead", imageCache.get(myNumber));
			 map.put("name","��");
			 number=Integer.parseInt(id.substring(id.indexOf("_")+1));
			 map.put("message",snm.getContentFromSendMessage(number));
			 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(snm.getTimeFromSendMessage(number))));
			 data_send.add(map);
	    }

		/*data_send = new ArrayList<Map<String,Object>>(); 
		SendNoticeManager snm = null;
		String id="343";
		String fileName = "343_5.txt";
		for(int i=0;i<20;++i){
			try {
				snm = new SendNoticeManager(FileManager.toSendNotice(SettingUtils.get(context, ShardPre.currentNumber, ""),id, 5));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//�д��ı�
			 Map<String,Object>  map = new HashMap<String,Object>();
			 map.put("number", "11111111111");
			 map.put("id", fileName);//���id��֪ͨ���ļ���
			 map.put("send",true);
			 map.put("head", R.drawable.defaultcontact);
			 map.put("name","��");
			 map.put("message",snm.getAllReply(5).get(0).get("content"));
			 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(snm.getTimeFromSendMessage(5))));
			 data_send.add(map);
		 } */
	  }
	  public static void getContactData(Context context){
			data_contact = new ArrayList<Map<String,Object>>();
			//�����Ĵ����ݿ��ѯ������
			String suffix=".png";
			List<Contact> contacts = DatabaseManager.queryAll(myNumber, context);
			if(contacts==null || contacts.size()==0){
				System.out.println("��");
				return ;
			}
			try{
			for(Contact contact:contacts){
				 Map<String,Object>  map = new HashMap<String,Object>();
				 if(imageCache.get(contact.number)==null){
					  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, contact.number+".png"));	  
					  imageCache.put(contact.number, bitmapcache);
				 }
				 map.put("head", imageCache.get(contact.number));
				 map.put("type", "contact");
				 map.put("number", contact.number);
				 map.put("name",contact.name);
				 map.put("sign",contact.words);
				 data_contact.add(map);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			Collections.sort(data_contact,new PinyinComparator());
	  }
	  public static  void getDisData(Context context){
			data_discussion = new ArrayList<Map<String,Object>>();
			/*String[] discussions = new String[]{
					"������һ","�������","��������"
			};*/
			List<String> discussions=FileManager.getAllDiscussions(SettingUtils.get(context, ShardPre.currentNumber, ""));
			BitmapFactory.Options opts = new BitmapFactory.Options();
	    	opts.inSampleSize = 2;
			Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon,opts);
			for(int i=0;i<discussions.size();++i){
				 Map<String,Object>  map = new HashMap<String,Object>();
				 map.put("number", "");
				 map.put("head", bitmap2);
				 map.put("type", "discussion");
				 map.put("name",discussions.get(i));
				 map.put("sign","");
				 data_discussion.add(map);
			 }
			Collections.sort(data_discussion,new PinyinComparator());
			
	  }
	  public static  void getGroupData(Context context){
			data_group = new ArrayList<Map<String,Object>>();
			List<String> groups = FileManager.getAllGroup(SettingUtils.get(context, ShardPre.currentNumber, ""));
			Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.in);
			if(groups==null || groups.size()==0){
				return ;
			}
			for(String group:groups){
				 Map<String,Object>  map = new HashMap<String,Object>();
				 map.put("number", "");
				 map.put("head", bitmap3);//�д��ı�
				 map.put("type", "group");
				 map.put("name",group);
				 map.put("sign","");
				 data_group.add(map);
			}
			Collections.sort(data_group,new PinyinComparator());			
	  }
	  //��������õ�ͼƬ����
	  public static void getContactBitmap(Context con){
		  imageCache = new HashMap<String, SoftReference<Bitmap>>();
		  System.out.print("����������");
		  File file=new File(FileManager.toContactImageFile(myNumber));
		  List<String> numbers=DatabaseManager.queryAllNumbers(myNumber, con);
		  if(numbers==null){
			  System.out.println("����������ʱΪ��");
			  return ;
		  }
		  for(String num:numbers){
			  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, num+".png"));	  
			  imageCache.put(num, bitmapcache);
		  }
		  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, myNumber+".png"));	  
		  imageCache.put(myNumber, bitmapcache);
	  }
	  

}
