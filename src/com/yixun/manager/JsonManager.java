package com.yixun.manager;

import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yixun.constants.Constants;

public class JsonManager {
	private static final String FLAG="flag",PHONE="phone",PASSWORD="password",
			HTTP_SUFFIX="[HTTP_SSDUTXYYX]",NAME="username",OLDPHONE="oldphone",SIGN="intro",SEX="sex",
			FRIEND_PHONE="friendphone",FRIEND_NAME="friendname",FRIENDLIST="friendlist",
			GROUP_NAME="groupname",GROUPLIST="grouplist",
			CODE="code",MSG="msg",MAN="M",WOMAN="W",
			BUILDER="builder",WEIXIN="weixin",FROM_NAME="from_name",TYPE="type",FROM_PHONE="from_phone",
			FROM_WEIXIN="from_weixin",TO_NAME="to_name",TO_PHONE="to_phone",TO_WEIXIN="to_weixin",
			TIME="time",SEND_TIME="send_time",REPLY_TIME="reply_time",INTRO="intro";
	//��¼����
	public static JSONObject login(String phone,String pass){
		JSONObject person = new JSONObject();  
		 try {
			person.put(FLAG,HTTP_SUFFIX);
			person.put(PHONE, phone);
			person.put(PASSWORD, pass);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}		 
		 return person;
	}
	//ע�����,�Ӳ����Ա���˵
	public static JSONObject register(String phone,String name,String pass,String sex,String sign){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(PHONE,phone);
			person.put(NAME,name);
			person.put(PASSWORD,pass);
			person.put(SEX, sex.equals("M")?MAN:WOMAN);
			person.put(INTRO, sign);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//������˵���Ϣ
	public static JSONObject findUserInfor(String phone){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(PHONE,phone);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//��ñ��˵���Ϣ����װ��һ��contact����
	public static Contact getUserInfor(JSONObject json){
		Contact contact=new Contact();
		try{
			contact.name=json.getString(NAME);
			contact.number=json.getString(PHONE);
			contact.isContact="Y";//����
			contact.sex=json.getString(SEX);
			contact.words=json.getString(INTRO);
//			contact.discussions="";
		}catch(JSONException e){
			System.out.println("δ�ҵ�ĳ����");
			e.printStackTrace();
		}
		return contact;
	}
	//����ǩ������,��û���Ա���˵
	public static JSONObject updateInfor(String oldphone,String sign,String sex){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(OLDPHONE, new String(oldphone.getBytes(Constants.UTF_8),Constants.UTF_8));
			person.put(SIGN,sign);
			person.put(SEX, sex);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//����ͷ�����
	public static void updateHead(){
		
	}
	//��Ӻ��Ѳ���
	public static JSONObject addFriendByPhone(String myphone,String phone){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(PHONE, myphone);
			person.put(FRIEND_PHONE,phone);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//ɾ�����Ѳ���
	public static JSONObject deleteFriendByPhone(String myphone,String phone){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(PHONE, myphone);
			person.put(FRIEND_PHONE,phone);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//����������
	public static JSONObject createDisu(String myphone,String disu_name){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(PHONE, myphone);
			person.put(GROUP_NAME,disu_name);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//�˳�������
	public static JSONObject exitDisu(String myphone,String disu_name){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(PHONE, myphone);
			person.put(GROUP_NAME,disu_name);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//��÷��سɹ�������Ϣ
	public static String getCode(JSONObject obj){
		try {
			return obj.getString(CODE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	//��÷��صľ���ĳɹ�ʧ�ܵ���Ϣ
	public static String getMsg(JSONObject obj){
		try {
			return obj.getString(MSG);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	//��÷��صľ���ĳɹ�ʧ�ܵ���Ϣ
		public static String getFlag(JSONObject obj){
			try {
				return obj.getString(FLAG);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}			
		}
	//����õ�����ת��json����
	public static JSONObject toJson(String ret_infor){
		JSONObject ret_json;
		try {
			ret_json = new JSONObject(ret_infor);
		} catch (JSONException e) {
			return null;
		}
		return ret_json;
	}
	//����Ⱥ����Ϣ
	public static JSONObject findGroupInfor(String name){
		JSONObject person = new JSONObject();
		try{
			person.put(FLAG, HTTP_SUFFIX);
			person.put(GROUP_NAME, name);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		 return person;
	}
	//��÷��ص���Ⱥ�б�
			public static String getGroupList(JSONObject obj){
				try {
					return obj.getString(GROUPLIST);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}

		//��÷��صĺ����б�
			public static String getFriendList(JSONObject obj){
				try {
					return obj.getString(FRIENDLIST);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}
			//��÷��صĺ�����Ϣ
			public static Friend getFriend(JSONObject obj){
				Friend f=new Friend();
				try {
					f.username=obj.getString(NAME);
//					f.weixin=obj.getString(WEIXIN);
					f.phone=obj.getString(PHONE);
					f.intro=obj.getString(SIGN);
					f.flag=obj.getString(FLAG);
					return f;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			//��÷��ص�socket
			public static SocketMessage getSocket(JSONObject obj){
				SocketMessage s=new SocketMessage();
				try {
					s.type=obj.getString(TYPE);
					//s.from_name=obj.getString(FROM_NAME);
					//s.from_weixin=obj.getString(FROM_WEIXIN);
					s.from_phone=obj.getString(FROM_PHONE);
					//s.to_name=obj.getString(TO_NAME);
					//s.to_weixin=obj.getString(TO_WEIXIN);
					s.to_phone=obj.getString(TO_PHONE);
					s.msg=obj.getString(MSG);
					s.time=obj.getString(TIME);
					return s;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			//�����ַ�������һ�����͵Ķ����Ȼ���ٵõ���������ݣ������������Ϣ��֪ͨ�ͻظ�
			public static SocketMessage getSocket(String json){
				try {
					return getSocket(new JSONObject(json));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}

			//��÷��ص�type
			public static String getType(JSONObject obj){
				try {
					return obj.getString(TYPE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}
			//��÷��ص�from_phone
			public static String getFrom_phone(JSONObject obj){
				try {
					return obj.getString(FROM_PHONE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}
			//��÷��ص�to_phone
			public static String getTo_phone(JSONObject obj){
				try {
					return obj.getString(TO_PHONE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}
			//��÷��ص�send_time
			public static String getSend_time(JSONObject obj){
				try {
					return obj.getString(SEND_TIME);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";
			}

			//��÷��ص�reply_time
			public static String getReply_time(JSONObject obj){
				try {
					return obj.getString(REPLY_TIME);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				return "";
			}
			}
			//������Ϣ
			private static JSONObject SendMessage(String myphone,String phone,String msg,String time){
				JSONObject json=new JSONObject();
				try {
					json.put("type", "chat");
					json.put("from_phone", myphone);
					json.put("to_phone", phone);
					json.put("msg", msg);
					json.put("time", time);
					return json;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}			
			}
			public static String sendMsg(String myphone,String phone,String msg,String time){
				JSONObject json=SendMessage(myphone, phone, msg, time);
				String ran8=UUID.randomUUID().toString().substring(0, 8)+"]";
				if(json==null){
					return "";
				}
				System.out.println("sendmsg:"+Constants.MSG_BEGIN+ran8+json.toString()+Constants.MSG_END+ran8);
				return Constants.MSG_BEGIN+ran8+json.toString()+Constants.MSG_END+ran8;
			}
			//�������˵���Լ����Ѿ���¼
			public static String SendLogin(String myphone){
				JSONObject json=new JSONObject();
				try{
					json.put("type", "login");
					json.put("phone", myphone);
				}catch(JSONException e){
					e.printStackTrace();
					return "";
				}
				String ran8=UUID.randomUUID().toString().substring(0, 8)+"]";
				return Constants.MSG_BEGIN+ran8+json.toString()+Constants.MSG_END+ran8;
			}
			//������������Լ����ߵ�֤��
			public static String SendBeat(){
				JSONObject json=new JSONObject();
				try{
					json.put("type", "hb");
				}catch(JSONException e){
					e.printStackTrace();
					return "";
				}
				String ran8=UUID.randomUUID().toString().substring(0, 8)+"]";
				return Constants.MSG_BEGIN+ran8+json.toString()+Constants.MSG_END+ran8;
			}
			//�����������֪ͨ
			public static String sendNotice(String myphone,List<String> phones,String msg,String time){
				JSONObject json=new JSONObject();
				JSONArray jsona=new JSONArray(phones);
				String ran8=UUID.randomUUID().toString().substring(0, 8)+"]";
				try{
					json.put("type", "message");
					json.put("msg", msg);
					json.put("from_phone", myphone);
					json.put("to_phone", jsona);
					json.put("send_time", time);
//					json.put("count", count);
				}catch(JSONException e){
					e.printStackTrace();
					return "";
				}
				return Constants.MSG_BEGIN+ran8+json.toString()+Constants.MSG_END+ran8;
			}
			//��������ظ�֪ͨ
			public static String replyNotice(String myphone,String phone,String msg,String time_send,String time_reply){
				JSONObject json=new JSONObject();
				String ran8=UUID.randomUUID().toString().substring(0, 8)+"]";
				try{
					json.put("type", "reply");
					json.put("msg", msg);
					json.put("from_phone", myphone);
					json.put("to_phone", phone);
					json.put("reply_time", time_reply);
					json.put("send_time",time_send);
//					json.put("count", count);
				}catch(JSONException e){
					e.printStackTrace();
					return "";
				}
				return Constants.MSG_BEGIN+ran8+json.toString()+Constants.MSG_END+ran8;
			}		
}
