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
	public static List<Map<String,Object> > data_chat = null;//chatFragment的数据
	public static List<Map<String,Object> > data_receive,data_send = null;//显示在listview中的数据集合
	public static List<Map<String,Object>>  data_contact,data_discussion,data_group = null;//显示在listview中的数据集合
	public static Map<String, SoftReference<Bitmap>> imageCache=null;// = new HashMap<String, SoftReference<Bitmap>>();
	public static String myNumber=null;

	//存储在chatfragment中使用的数据
	public static void getChatData(final String currentUserNumber,final Context context){
		/**
		 * 真正的查询数据，保存上次的数据
		 * 还要考虑是个人的对话还是讨论组的对话
		 * 现在采用的是图片的软引用，仅仅是个人图片的软引用
		 */
		if(myNumber==null){
			myNumber=SettingUtils.get(context, ShardPre.currentNumber, "");
		}
		if(imageCache==null){//如果该没有建立图片的软引用的话，建立
			getContactBitmap(context);
		}
		data_chat=new ArrayList<Map<String,Object>>();
		String suffix=".png";
		String flag="";
		Map<String,String> lastRecord = null;//一个记录文件的最后一条信息
		Contact contact=null;
		MessageManager mm = null;//消息的管理类的对象
//		String myNumber=SettingUtils.get(context, ShardPre.currentNumber, "");
		ArrayList<String> numbers=SettingUtils.getData(context,Constants.SHARED_KEY_CHAT);
		if(numbers==null){
			System.out.println("暂时无数据");
			return ;
		}
		Bitmap bitmap=ImageTools.minBitmap(context, R.drawable.defaultcontact);
		for(String number:numbers){
			Map<String,Object> map = new HashMap<String,Object>();
			flag=number.substring(0, 1);
			if(flag.equals("0")){//说明是个人之间的对话
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
						System.out.println("messageManager够构造错误");
					}
					lastRecord = mm.getLastRecord();
					map.put("message", lastRecord.get("content"));
					map.put("time", TimeManager.toDisplayFormat(TimeManager.toDate(mm.getTime(lastRecord.get("head")))));
			}else{//讨论组之间的对话
				map.put("number", "");
				map.put("person", false);
				//读取头像，这是固定的，所以不用每次都解析
				map.put("head", bitmap);
//				map.put("head",FileManager.readImgFromContact(currentUserNumber, number+suffix));
				map.put("name",number);
				try{
					mm = new MessageManager(FileManager.toDiscussionRecord(currentUserNumber, number));
					}catch(Exception e){
						System.out.println("messageManager够构造错误");
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
		MessageManager mm = null;//消息的管理类的对象
		Map<String,String> lastRecord = null;//一个记录文件的最后一条信息
		for(Contact contact:contacts){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("head",FileManager.readImgFromContact(currentUserNumber, contact.number+suffix));
			map.put("name", contact.name);
			map.put("number", contact.number);
			map.put("person", true);
			try{
			mm = new MessageManager(FileManager.toContactRecord(currentUserNumber, contact.number));
			}catch(Exception e){
				System.out.println("messageManager够构造错误");
			}
			lastRecord = mm.getLastRecord();
			map.put("message", lastRecord.get("content"));
			map.put("time", TimeManager.toDisplayFormat(TimeManager.toDate(mm.getTime(lastRecord.get("head")))));
			data_chat.add(map);*/
//		}
//		}
//	}).start();
		
	}
	
	 //得到接收的通知的数据
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
		 * 真正的查询数据
		 **由id开始找到每条消息
		 */
		 ArrayList<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		 String head="",content="",number="";
		 for(String id:ids){
			 Map<String,Object>  map = new HashMap<String,Object>();
			 Map<String,String> notice_find=nm.getNoticeById(id);
			 head=notice_find.get("head");
//			要在这加载图像 map.put("head", );
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
			 map.put("name","商明阳");
			 map.put("message",nm.getDataFromBack(1).get(0).get("content"));
			 map.put("id", id);
			 map.put("send", false);
			 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(nm.getTime(nm.getNoticeById("1214").get("head")))));
			 data_receive.add(map);
		 }*/
	  }
	  //得到发送通知的数据
	  public static  void getSendData(Context context){
		  //真正的存储数据
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
			 map.put("id", id);//这个id是通知的文件名
			 map.put("send",true);
			 if(map.get(myNumber)==null){
				  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, myNumber+".png"));	  
				  imageCache.put(myNumber, bitmapcache);
			 }
			 //这个是自己的头像map.put("head", R.drawable.defaultcontact);
			 map.put("ead", imageCache.get(myNumber));
			 map.put("name","我");
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
			//有待改变
			 Map<String,Object>  map = new HashMap<String,Object>();
			 map.put("number", "11111111111");
			 map.put("id", fileName);//这个id是通知的文件名
			 map.put("send",true);
			 map.put("head", R.drawable.defaultcontact);
			 map.put("name","商");
			 map.put("message",snm.getAllReply(5).get(0).get("content"));
			 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(snm.getTimeFromSendMessage(5))));
			 data_send.add(map);
		 } */
	  }
	  public static void getContactData(Context context){
			data_contact = new ArrayList<Map<String,Object>>();
			//真正的从数据库查询的数据
			String suffix=".png";
			List<Contact> contacts = DatabaseManager.queryAll(myNumber, context);
			if(contacts==null || contacts.size()==0){
				System.out.println("空");
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
					"讨论组一","讨论组二","讨论组三"
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
				 map.put("head", bitmap3);//有待改变
				 map.put("type", "group");
				 map.put("name",group);
				 map.put("sign","");
				 data_group.add(map);
			}
			Collections.sort(data_group,new PinyinComparator());			
	  }
	  //获得软引用的图片对象
	  public static void getContactBitmap(Context con){
		  imageCache = new HashMap<String, SoftReference<Bitmap>>();
		  System.out.print("创建软引用");
		  File file=new File(FileManager.toContactImageFile(myNumber));
		  List<String> numbers=DatabaseManager.queryAllNumbers(myNumber, con);
		  if(numbers==null){
			  System.out.println("创建软引用时为空");
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
