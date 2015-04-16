package com.yixun.main;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.manager.Contact;
import com.yixun.manager.DataManager;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.HttpTool;
import com.yixun.manager.ImageTools;
import com.yixun.manager.JsonManager;
import com.yixun.manager.MessageManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.manager.TimeManager;
import com.yixun.myview.SocketService;
import com.yixun.myview.UpdateHandler;
import com.yixun.myview.Updateable;

public class ChatActivity extends Activity implements OnClickListener,Updateable{

	private ArrayList<HashMap<String, Object>> users;
	private HashMap<String, Object> user;
	private ClipboardManager cm ;
	private String myNumber;
	private String number;
	private TextView  tw;
	private boolean isPerson;
	private final int MEG_SUCCESS = 1;
	private EditText edit=null;
	ListView mListView=null;
	Bitmap bitmap_my,bitmap_other;
	ImageButton send=null;
	ImageButton toBack=null;
	private boolean flag_ineye=false;
	private Handler mHandler = new Handler() {  
	        public void handleMessage (Message msg) {//此方法在ui线程运行  
	            switch(msg.what) {  
	            case MEG_SUCCESS:  
	            	Contact c = (Contact)msg.obj;
	                tw.setText(c.name); 
	               	break;  
	        }  
	    }
	};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println(SocketService.tag);
		setContentView(R.layout.activity_chat);
		UpdateHandler.registerUpdater(this);
		loadPictures();
		edit=(EditText)findViewById(R.id.editText1);
		OnClickListener listen = null;
		toBack = (ImageButton)findViewById(R.id.room_back);
		toBack.setOnClickListener(this);
		send=(ImageButton)findViewById(R.id.room_send);
		send.setOnClickListener(this);
		listen = new OnClickListener() {//这个是点击头像跳转
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isPerson==true){
				Intent intent = new Intent(ChatActivity.this, Contacts.class);
				intent.putExtra(ShardPre.currentChatNumber, number);
				startActivity(intent);
				}else{//如果是讨论组的话
					Intent intent = new Intent(ChatActivity.this,Group.class);
					intent.putExtra(ShardPre.currentChatNumber,number);
					startActivity(intent);
				}
			}
		};
		ImageButton b_menu;
		b_menu = (ImageButton) findViewById(R.id.room_menu);
		b_menu.setOnClickListener(listen);
		
		tw = (TextView) findViewById(R.id.name);
		// 两个string变量是由前文的activity传过来的数据
		try{
		myNumber = SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
		Intent intent = getIntent();
		isPerson = intent.getBooleanExtra(ShardPre.isPersonal, true);
		number = intent.getStringExtra(ShardPre.currentChatNumber);
		if(isPerson==true)getName();
		else{tw.setText(number);}
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		users = new ArrayList<HashMap<String, Object>>();
		mListView = (ListView) findViewById(R.id.users);
		//得到两个人的位图
		bitmap_my=FileManager.readImgFromContact(myNumber, myNumber+".png");
		bitmap_other=FileManager.readImgFromContact(myNumber, number+".png");
		try {
			addUsers(myNumber, number);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mListView.setAdapter(new MyBaseAdapter());
		this.registerForContextMenu(mListView);
		this.registerForContextMenu(edit);
	}

	
//    private void write() throws IOException { // // TODO Auto-generatedmethod stub // // 
//      String pathString = Environment.getExternalStorageDirectory().getPath(); // // .getPath(); // //
//	  MessageManager mm = new MessageManager(pathString+"/3.txt");
//	  mm.write("15267526156", "2014-03-01 12:21:12", "0","在？");
//	  mm.write("12345678911", "2014-03-01 12:21:30", "0","恩");
//	  mm.write("15267526156", "2014-03-01 12:23:12", "0","忙吗？");
//	  mm.write("12345678911", "2014-03-01 12:53:12", "0","还好");
//	  mm.write("15267526156", "2014-03-01 12:54:12", "0","那行，没事了，88");
//	  mm.write("15267526156", "2014-03-11 12:26:12", "0","我又来了。。。。");
//	  mm.write("12345678911", "2014-03-11 14:27:12", "0","有事？"); 
//	  mm.write("12345678911", "2014-03-11 15:28:12", "0","没事别来烦我，ok？");
//	  mm.write("15267526156", "2014-03-11 16:21:12", "0","好吧。。。"); 
//	  mm.write("12345678911", "2014-03-12 12:22:12", "0","昨天我失礼了。。"); 
//	  mm.write("15267526156", "2014-03-12 12:23:12", "0","没事"); 
//	  mm.write("12345678911", "2014-03-13 12:24:12", "0","前天我失礼了。。");
//	  mm.write("15267526156", "2014-04-13 12:25:12", "0","说了没事啦"); 
//
//    }
	 
//这只是联系人的，还没有讨论组的对话
	private void addUsers(String myNumber, String number) throws Exception {
		// TODO Auto-generated method stub

		String pathString = FileManager.toContactRecord(myNumber, number);
		MessageManager file_read = new MessageManager(pathString);

		String time = null;
		ArrayList<Map<String, String>> list = file_read.getDataFromBack(20);
		int size = list.size() - 1;
		String temp="";
		for (int i = size; i >= 0; i--) {
			Map<String, String> map = list.get(i);
			user = new HashMap<String, Object>();
			temp = map.get(file_read.KEY_HEAD);

			// 0代表是别人发过来的，1是自己发送的，2代表这是一个时间戳
			if (i == size) {
				user.put("type", 2);
				time = file_read.getTime(temp);
				user.put("text", TimeManager.toDisplayFormat(TimeManager.toDate(time)));
				users.add(user);
			} else {
				java.util.Date dtime = TimeManager.toDate(time);
				time = file_read.getTime(temp);
				if (!TimeManager.isLaterInHalfHour(dtime,TimeManager.toDate(time))) {
					user.put("type", 2);
					user.put("text",
							TimeManager.toDisplayFormat(TimeManager.toDate(time)));
					users.add(user);
				}
			}
			user = new HashMap<String, Object>();
			if (file_read.getNumber(temp).equalsIgnoreCase(myNumber)) {
				user.put("type", 1);
				user.put("photo", bitmap_my);
			} else {
				user.put("type", 0);
				user.put("photo", bitmap_other);
			}
			user.put("text", map.get(file_read.KEY_CONTENT));
			users.add(user);
		}
		return;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
		flag_ineye=true;
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		flag_ineye=false;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bitmap_my!=null && bitmap_my.isRecycled()==false){
			bitmap_my.recycle();
		}
		if(bitmap_other!=null && bitmap_other.isRecycled()==false){
			bitmap_other.recycle();
		}
		UpdateHandler.unRegisterUpdater(this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.room_send:
			//发送消息
			if(edit.getText().toString().length()==0){
				Toast.makeText(getApplicationContext(), "发送内容不能为空", 1000).show();
				return ;
			}else if(edit.getText().toString().length()>100){
				Toast.makeText(getApplicationContext(), "内容字数不能超过100", 1000).show();
				return ;
			}
			new SendMsgTask().execute();
			break;
		case R.id.room_back:
			finish();
			break;
		}
	}

	private class MyBaseAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public MyBaseAdapter() {
			// TODO Auto-generated constructor stub
			mLayoutInflater = (LayoutInflater) 
					getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			cm =(ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View firstItemView = null;
			View othersItemView = null;
			View timeView = null;
			ViewHolder holder = null;
			// 获取到当前位置所对应的Type
			if (((Integer) users.get(position).get("type")) == 1) {
				firstItemView = convertView;
				// if (firstItemView == null) {
				firstItemView = mLayoutInflater.inflate(R.layout.other_people, null);
				holder = new ViewHolder();
				holder.chatting_content = (TextView) firstItemView
						.findViewById(R.id.text);
				holder.chatting_photo = (ImageView) firstItemView
						.findViewById(R.id.photo);
				firstItemView.setTag(holder);

				convertView = firstItemView;
			} else if (((Integer)users.get(position).get("type")) == 0) {
				othersItemView = convertView;

				othersItemView = mLayoutInflater.inflate(R.layout.people,
						null);
				holder = new ViewHolder();
				holder.chatting_content = (TextView) othersItemView
						.findViewById(R.id.text);
				holder.chatting_photo = (ImageView) othersItemView
						.findViewById(R.id.photo);
				othersItemView.setTag(holder);

				convertView = othersItemView;
				final ImageView iv = (ImageView) convertView.findViewById(R.id.photo);
				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//						v.setBackgroundResource(R.drawable.photo);
//						v.setAlpha(0.3f);
						startActivity(new Intent(ChatActivity.this, Contacts.class));
					}
				});				
			}
			

			if (((Integer) users.get(position).get("type")) == 2) {
				timeView = convertView;
				timeView = mLayoutInflater.inflate(R.layout.people_time, null);
				( (TextView) timeView.findViewById(R.id.text) ).setText((String) users.get(
						position).get("text"));
				convertView = timeView;

			} else {
				holder.chatting_content.setText((String) users.get(
						position).get("text"));
				//开使设置头像
				holder.chatting_photo.setImageBitmap((Bitmap) users//设置头像
						.get(position).get("photo"));
			}
			return convertView;
		}

		private class ViewHolder {
			TextView chatting_content;
			ImageView chatting_photo;
		}

		@Override
		public int getCount() {
			if (users == null) {
				return 0;
			} else {
				return (users.size());
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

	}

	// 等比例缩放
	public  Bitmap adaptive(Bitmap bitmap) {
		WindowManager wm = this.getWindowManager();
		// 背景缩放
		float scalX = wm.getDefaultDisplay().getWidth();// 屏宽
		float scalY = wm.getDefaultDisplay().getHeight();// 屏高
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();// 获取资源位图的宽
		int height = bitmap.getHeight();// 获取资源位图的高
		float w = scalX / 720;
		float h = scalY / 1280;
		matrix.postScale(w, h);// 获取缩放比例
		// 根据缩放比例获取新的位图
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	@SuppressWarnings("deprecation")
	public void loadPictures() {

		InputStream is = getResources().openRawResource(R.drawable.room_bg_top);
		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		ImageView iv = (ImageView) findViewById(R.id.room_bg_top);
		iv.setImageBitmap(adaptive(mBitmap));

		ImageButton ib = null;
		mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_back));
		ib = (ImageButton) findViewById(R.id.room_back);
		BitmapDrawable bd = new BitmapDrawable(adaptive(mBitmap));
		ib.setBackgroundDrawable(bd);

		mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_menu));
		ib = (ImageButton) findViewById(R.id.room_menu);
		bd = new BitmapDrawable(adaptive(mBitmap));
		ib.setBackgroundDrawable(bd);

		mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_bg_chat));
		iv = (ImageView) findViewById(R.id.room_bg_chat);
		iv.setImageBitmap(adaptive(mBitmap));

		/*mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_plus));
		ib = (ImageButton) findViewById(R.id.room_plus);
		bd = new BitmapDrawable(adaptive(mBitmap));
		ib.setBackgroundDrawable(bd);*/

	/*	mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_send));
		ib = (ImageButton) findViewById(R.id.room_send);
		bd = new BitmapDrawable(adaptive(mBitmap));
		ib.setBackgroundDrawable(bd);
*/
		/*mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_upload));
		ib = (ImageButton) findViewById(R.id.room_upload);
		bd = new BitmapDrawable(adaptive(mBitmap));
		ib.setBackgroundDrawable(bd);*/

		/*mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_line));
		iv = (ImageView) findViewById(R.id.line);
		iv.setImageBitmap(adaptive(mBitmap));*/

		EditText et;
		mBitmap = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.room_chat));
		et = (EditText) findViewById(R.id.editText1);
		bd = new BitmapDrawable(adaptive(mBitmap));
		et.setBackgroundDrawable(bd);
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);	
		if( v == this.findViewById(R.id.editText1) ){
			menu.add(0,0,0,"粘贴");
		}
		else	menu.add(1, 0, 0, "复制");
	} 
    
    @Override  
    public boolean onContextItemSelected(MenuItem item) { 

		if( item.getGroupId() == 0) {
			EditText et  = (EditText) this.findViewById(R.id.editText1);
			et.setText(cm.getText());
		}
		else {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) 
					item.getMenuInfo();
			View text =  info.targetView;
			cm.setText( ( (TextView)(text.findViewById(R.id.text))).getText());
		}
        return super.onContextItemSelected(item);  
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	private void getName(){
		new Thread(new Runnable(){
			public void run(){
				Contact contact = DatabaseManager.queryByNumber(myNumber, getApplicationContext(), number);
				if(contact==null){
					contact=new Contact("","","","","",null);
				}
				mHandler.obtainMessage(MEG_SUCCESS,contact).sendToTarget();
			}
		}).start();
	}
	private class SendMsgTask extends AsyncTask<Void,Void,Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			boolean flag= HttpTool.SendMsg(SocketService.socket, JsonManager.sendMsg(myNumber, number, edit.getText().toString(), TimeManager.toSaveFormat(System.currentTimeMillis())));
			if(flag==false){
				return false;
			}
			if(flag==true){//如果成功了的话，想文件中写数据
				try {
					publishProgress(null);
					MessageManager mm=new MessageManager(FileManager.toContactRecord(myNumber, number));
					mm.write(myNumber, TimeManager.toSaveFormat(System.currentTimeMillis()), "1", edit.getText().toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			System.out.println("更新界面");
			// 0代表是别人发过来的，1是自己发送的，2代表这是一个时间戳
			user = new HashMap<String, Object>();
			user.put("type", 2);
			user.put("text", TimeManager.toDisplayFormat(System.currentTimeMillis()));
			users.add(user);
			user = new HashMap<String, Object>();
			user.put("type", 1);
			user.put("photo", bitmap_my);
			user.put("text", edit.getText().toString());
			users.add(user);
			((BaseAdapter)(mListView.getAdapter())).notifyDataSetChanged();
			edit.setText("");
		}
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==false){//发送成功的话
				//先在界面上显示
				Toast.makeText(getApplicationContext(), "该消息发送失败，请重试", 1000).show();
			}
		}
	}
	  //如果自己已经加入组的话梳理消息
	  @Override
		public void update(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:
				try{
					JSONObject json=(JSONObject)msg.obj;
					if(json.get("type").equals("chat") && json.get("from_phone").equals(number)){//disu这个还要改
						System.out.println("找到对应的聊天界面"+json.toString());
						//新增数据
						
						//更新界面
						if(flag_ineye){
							((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		}
	
}
