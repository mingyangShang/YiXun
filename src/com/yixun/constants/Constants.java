package com.yixun.constants;

import com.yixun.manager.SettingUtils;

public class Constants {
	
	private static Constants constants = null;
	//每个界面对应的标志
	public static final int FLAG_CHAT = 1;
	public static final int FLAG_NOTICE = 2;
	public static final int FLAG_CONTACTS = 3;
	public static final int FLAG_SETTING = 4;
	public static final int FLAG_ADD = 5;
	public static final int FLAG_NEW = 6;
	public static final int NUMBER_MAIN_VIEW = 6;
	public static final int FLAG_CONTACTS_SELECT = 7;
	
	public static final String SUFFIX = ".png";
	public static final int FLAG_CONSTACT = 0;
	public static final int FLAG_DISCUSSION = 1;
	public static final int FLAG_GROUP = 2;
	
	public static final String SEOER_ID="/";
	public static final int FIXED_TIME=25*1000;
	public static final int TIME_OUT = 10*1000; //超时时间
	 public static final String SUCCESS="1";
	 public static final String FAILURE="0"; 
	 public static final int MAX_SIZE=15;
	//这是在shared中存储时所用的键
	public static final String SHARED_KEY_CHAT="SHARED_KEY_CHAT";
	public static final String SHARED_KEY_SEND_NOTICE="SHARED_KEY_SEND_NOTICE";
	public static final String SHARED_KEY_RECEIVE_NOTICE="SHARED_KEY_RECEIVE_NOTICE";
	
	public static final int REQUEST_SELECT_CONTACT = 0;
	public static final int RESULT_SELECT_CONTACT = 1;
	
	public final static int TYPE_ADD_CONTACT_TO_GROUP = 0 ;
	public final static int TYPE_DELE_CONTACT_FROM_GROUP = 1;
	public final static int TYPE_ADD_CONTACT_TO_DISCUSSION=2;
	
	public final static String 	KEY_SELECTED="KEY_SELECTED"; 
	public final static String UTF_8="utf-8";
//	public final static int TIMEOUT = 1000;
	public final static String HTTP_SEND_ERROR = "sendText error!";
	public final static int PORT=8991;
	public final static String IP="210.30.97.63";
	public final static String BEGIN="http://";
	public final static String MSG_BEGIN= "[BEGIN_SSDUTXYYX_";
	public final static String MSG_END="[END_SSDUTXYYX_";
	//以上这条这是大宋消息时用到的前缀，后面还要接随机生成的8问字符串
	public final static String IP_REGISTER = BEGIN+IP+"/reg";
	public final static String IP_LOGIN = BEGIN+IP+"/login";
	public final static String IP_UPDATEINFOR=BEGIN+IP+"/updateuserinfo";
	public final static String IP_ADDFRIEND=BEGIN+IP+"/addfriend";
	public final static String IP_DELFRIEND=BEGIN+IP+"/delfriend";
	public final static String IP_FRIENDLIST=BEGIN+IP+"/friendlist";
	public final static String IP_USERINFOR=BEGIN+IP+"/userinfo";
	public final static String IP_BUILDGROUP=BEGIN+IP+"/buildgroup";
	public final static String IP_JOSINGROUP=BEGIN+IP+"/joingroup";
	public final static String IP_QUITGROUP=BEGIN+IP+"/quitgroup";
	public final static String IP_GROUPLIST=BEGIN+IP+"/grouplist";
	public final static String IP_GETIMAGE=BEGIN+IP+"/icon/";//后面还要加上手机号+图片后缀
	public final static String IP_UPLOADHEAD=BEGIN+IP+"/uploadicon.php";//"http://210.30.97.63/uploadicon.php"
	public final static String JSON="json";
	public final static String CODE_FAIL="0",CODE_SUCC="1";
//	public final static String 

//	public final static String TYPE_ADD_CONTACT_TO_GROUP = "TYPE_ADD_CONTACT_TO_GROUP" ;
//	public final static String TYPE_ADD_CONTACT_TO_GROUP = "TYPE_ADD_CONTACT_TO_GROUP" ;


		
	//私有的构造函数
	private Constants(){
		
		}
	
	//获得单例
	public static Constants getInstance(){
		if(constants == null){
			constants = new Constants();
		}
		return constants;
	}

}
