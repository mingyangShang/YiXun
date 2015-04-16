package com.yixun.main;



import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.manager.Contact;
import com.yixun.manager.DataManager;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.HttpTool;
import com.yixun.manager.JsonManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;

public class Add extends Activity {

	String myNumber = "";
	Contact ct = new Contact();
	 /**联系人名称**/  
    private ArrayList<String> mContactsName = new ArrayList<String>();  
    /**获取库Phon表字段**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  
    /**联系人显示名称**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
    /**联系人账号**/  
    private ArrayList<String> mContactsNumber = new ArrayList<String>();  
    /**是否已经在好友中的标记*/
    private ArrayList<String> mIsIn = new ArrayList<String>();  
    private static final int PHONES_NUMBER_INDEX = 1;  
//    ArrayList<Map<String, String>> users = new ArrayList<Map<String, String>>();
    final String in="Y",out="N";
    ListView noAddFriends=null;
    AddAdapter adapter_add=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		myNumber=SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
		getPhoneContacts();
		int size=mContactsName.size();
		//得到现在在数据库中的所有联系人
		List<String> contacts=DatabaseManager.queryAllNumbers(myNumber, getApplicationContext());
		if(contacts==null){
			for(int i=0;i<size;++i){
				mIsIn.add(out);
			}
		}else{
			for(int i=0;i<size;++i){
				//如果通讯中的联系人在好友中时，isin设置成in
				mIsIn.add(contacts.contains(mContactsNumber.get(i))==true?in:out);
			}
		}
		/*for(int i=0;i<size;++i){
			Map<String, String> user = new HashMap<String, String>();
			user.put("number",mContactsNumber.get(i));
			user.put("name", mContactsName.get(i));
			user.put("isIn", mIsIn.get(i));
			users.add(user);
		}*/
		noAddFriends = (ListView) findViewById(R.id.add_unAddFriends);
		adapter_add=new AddAdapter();
		noAddFriends.setAdapter(adapter_add);//设置数据的适配器
	}
	 /**得到手机通讯录联系人信息**/
    private void getPhoneContacts() {
	ContentResolver resolver =getApplicationContext().getContentResolver();
	// 获取手机联系人
	Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
			PHONES_PROJECTION, null, null, null);
	if (phoneCursor != null) {
		String phoneNumber=null,contactName=null;
	    while (phoneCursor.moveToNext()) {
		//得到手机号码
		phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
		//当手机号码为空的或者为空字段 跳过当前循环
		if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length()<11)
		    continue;
		//得到联系人名称
		contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
		mContactsName.add(contactName);
		phoneNumber=phoneNumber.replaceAll("\\D", "");
		System.out.println(phoneNumber.length()+"/"+phoneNumber);
		mContactsNumber.add(phoneNumber);
	    }
	    phoneCursor.close();
	}
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	private class AddAdapter extends BaseAdapter{
		private	ViewHolder vh=null;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mIsIn.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(arg1==null){
				vh=new ViewHolder();		
				arg1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_friend_item,null);
				vh.tv_number = (TextView)arg1.findViewById(R.id.number_);
				vh.tv_name = (TextView)arg1.findViewById(R.id.name_);
				vh.bt_add = (Button)arg1.findViewById(R.id.add_);
				arg1.setTag(vh);
			}else{
				vh = (ViewHolder)arg1.getTag();
			}
			vh.bt_add.setOnClickListener(new MyOnClickListener(arg0));
			//为头像添加监听器
			setData(arg0);	//为每一条设置数据
			return arg1;	//返回每一条的布局文件
		}
		private void setData(int posi){
			vh.tv_number.setText(mContactsNumber.get(posi));
			vh.tv_name.setText(mContactsName.get(posi));
			if(mIsIn.get(posi).equals(in)){
				vh.bt_add.setClickable(false);
			}else{
				vh.bt_add.setClickable(true);
			}
		}
		private class MyOnClickListener implements OnClickListener{
			private int position;
			public MyOnClickListener(int posi){
				this.position=posi;
			}
			@Override
			public void onClick(View arg0) {
				//点击添加
				if(mIsIn.get(position).equals(in)){
					Toast.makeText(getApplicationContext(), mContactsName+"已经是您的好友了", 1000).show();
				}else{
					System.out.println("点击的是"+mContactsNumber.get(position));
					//从服务器获得此人的信息
					new AddFriendTask(position).execute(mContactsNumber.get(position));
				}
			}
		}
		private class ViewHolder{
			public TextView tv_number,tv_name;
			public Button bt_add;
		}
	}
	//发送好友申请的请求
	private class AddFriendTask extends AsyncTask<String, Void, Integer>{
		/**传进来的参数是选择添加的人的账号，如果该人已经注册了的话，那么添加成功，
		**添加成功的话，首先提醒联系人的界面更新
		*然后要向数据库中插入这个人的信息
		*还要在文件中建立这个人的信息
		*一下的整数，0代表所有信息获得成功，1代表添加好友请求未发送成功，2代表要查找的联系人还为注册
		*3代表获取该人的个人信息失败，4代表获取该人的头像失败
		*/
		public AddFriendTask(int posi){
			this.position=posi;
		}
		private int position;
		private ProgressDialog pd=showDialog();
		private AlertDialog ad=onShowDialogClick();
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd.show();
		}
		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result=HttpTool.sendJson(Constants.IP_ADDFRIEND, JsonManager.addFriendByPhone(myNumber, params[0]).toString(), Constants.UTF_8);
			System.out.println("add_result:"+result);
			if(result.equals(Constants.HTTP_SEND_ERROR)){
				return 1;
			}
			JSONObject json=JsonManager.toJson(result);
			if(JsonManager.getCode(json).equals(Constants.CODE_FAIL)){
				return 2;
			}
			//成功，服务器的数据库中有这个人
			result=HttpTool.sendJson(Constants.IP_USERINFOR, JsonManager.findUserInfor(params[0]).toString(), Constants.UTF_8);
			json=JsonManager.toJson(result);
			if(JsonManager.getCode(json).equals(Constants.CODE_FAIL)){
				return 3;
			}
			//获得信息成功
			/**
			 * 在本地的数据库中插入这个人
			 * 存下此人的头像,聊天信息等
			 * 联系人数据增加，提醒联系人界面要更新了
			 */
			
			Bitmap bitmap;
			try {
				bitmap = HttpTool.getImageStream(Constants.IP_GETIMAGE+params[0]+".png");
				if(bitmap==null){//证明服务器那边没有这个人的图片
					bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.defaultcontact);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 4;
			}
			FileManager.newConImg(myNumber, bitmap, params[0]+".png");
			//添加成功将此人对应的电极间设置为不可点击
			mIsIn.set(position, in);
			FileManager.newContact(myNumber, params[0]);
			Contact contact=JsonManager.getUserInfor(json);
			DatabaseManager.insertContact(myNumber, getApplicationContext(), contact.number, contact.name, contact.sex, contact.words);
			Map<String,Object>  map = new HashMap<String,Object>();
			if(DataManager.imageCache.get(contact.number)==null){
				SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(myNumber, contact.number+".png"));	  
				DataManager.imageCache.put(contact.number, bitmapcache);
				 map.put("head",bitmapcache);
			}
			 map.put("type", "contact");
			 map.put("number", contact.number);
			 map.put("name",contact.name);
			 map.put("sign",contact.words);
			DataManager.data_contact.add(map);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			if(result==0){
				Toast.makeText(getApplicationContext(), "获取好友信息成功，赶快去聊天吧",1000).show();
				adapter_add.notifyDataSetChanged();		
			}else if(result==1 || result==3 || result==4 ){
				Toast.makeText(getApplicationContext(), "获取信息失败，网络不太给力啊"+result,1000).show();
			}else if(result==2){
				ad.show();
			}
		}		
	}
	private ProgressDialog showDialog(){
		 ProgressDialog xh_pDialog = new ProgressDialog(Add.this,AlertDialog.THEME_HOLO_LIGHT);  
         // 设置进度条风格，风格为圆形，旋转的  
         xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
         // 设置ProgressDialog 标题  
         xh_pDialog.setTitle("提示");  
         // 设置ProgressDialog提示信息  
         xh_pDialog.setMessage("正在拉去好友信息");  
         // 设置ProgressDialog标题图标  
         xh_pDialog.setIcon(R.drawable.add);  
         // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确  
         xh_pDialog.setIndeterminate(false);  
         // 设置ProgressDialog 是否可以按退回键取消  
         xh_pDialog.setCancelable(false);  
         return xh_pDialog;
	}
	  public AlertDialog onShowDialogClick(){  
		   
	        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);  
	        builder.setTitle("貌似他（她）还没有注册呢");  
	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
				}
	        });  
	        AlertDialog ad = builder.create();  
	        return ad;
	    }  
}
