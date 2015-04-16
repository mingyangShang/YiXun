package com.yixun.main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.manager.FileManager;
import com.yixun.manager.SendNoticeManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.manager.TimeManager;
import com.yixun.myview.UpdateHandler;
import com.yixun.myview.Updateable;

public class SendActivity  extends Activity implements Updateable {
	private List<Map<String, Object>> mData; 
	public SendNoticeManager snm = null;
	public int totalnum=0;
	public int repliednum=0;
	private String id;
	private String fileName;
	private String currentUserNumber;
	private boolean flag_ineye;
	ListView list;
	@SuppressLint("SimpleDateFormat")
	public SimpleDateFormat sdf  =   new  SimpleDateFormat( " yyyy-MM-dd HH:mm:ss " ); 
	public void newfile()
	{
		Intent intent = getIntent();
		if(intent.hasExtra(ShardPre.currentSendId)==false){
			try{
			throw new Exception("intent 传值错误");
			}catch(Exception e){
				e.printStackTrace();
				return ;
			}
		}
		
		  try {
			  fileName = intent.getStringExtra(ShardPre.currentSendId);
			  System.out.println(fileName);
			  id = fileName.substring(0, fileName.indexOf("_"));
			  System.out.println(id);
			  currentUserNumber = SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
			  String temp = fileName.substring(fileName.indexOf('_')+1,fileName.indexOf('.'));
			  totalnum =Integer.parseInt(temp);
			snm = new SendNoticeManager(FileManager.toSendNotice(currentUserNumber, id, totalnum));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_send);
		UpdateHandler.registerUpdater(this);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);		
		list= (ListView) findViewById(R.id.listView_reply); 		
		newfile();
		mData = getData();
		Log.i("1","getdata");
		MyAdapter adapter = new MyAdapter(this);
		list.setAdapter(adapter);
		
		//人数內容時間更新
				
				TextView content=(TextView) findViewById(R.id.replycontent);
				TextView time=(TextView) findViewById(R.id.host_time);
				TextView topic=(TextView)this.findViewById(R.id.title_topic);
				topic.setText("通知");
				content.setText(snm.getContentFromSendMessage(totalnum));
				
				time.setText(TimeManager.toDisplayFormat(snm.getTimeFromSendMessage(totalnum)));
				
				TextView statement=(TextView) findViewById(R.id.statement);
				statement.setText(repliednum+"/"+totalnum);
				ImageView head=(ImageView) findViewById(R.id.host_head);
				head.setBackgroundResource(R.drawable.head2);
		
		// ListView 中某项被点击后的逻辑 
		list.setOnItemClickListener(new OnItemClickListener() {
			
	 		@Override
	 		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	 				long arg3) {
	 			// TODO Auto-generated method stub
	 			
	 			final String num = (String)mData.get(arg2).get("number");
	 			final String content = (String)mData.get(arg2).get("content");
	 			new AlertDialog.Builder(SendActivity.this,AlertDialog.THEME_HOLO_LIGHT)    
				
				  
                .setMessage(content)  
 .setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						 

					}
				})
                .setPositiveButton("复制",new DialogInterface.OnClickListener() {
					
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						 ClipboardManager copy = (ClipboardManager) SendActivity.this.getSystemService(Context.CLIPBOARD_SERVICE); 
						 copy.setText(content); 
						 Toast.makeText(getApplicationContext(), "复制成功", Toast.LENGTH_SHORT).show();

					}
				})  
				
  
                .show();  

}
					}
	 		    );
	 
		// ListView 中某项被长按后的逻辑  修改状态信息  
		/*list.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}});*/
		
		Button returnButton=(Button)this.findViewById(R.id.title_return);
		//点击返回按钮 
		returnButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{				
				finish();
			}
		});		
		
		
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		flag_ineye=true;
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
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
		UpdateHandler.unRegisterUpdater(this);
	}
	private List<Map<String, Object>> getData() {  
        		//从程序中读的回复
		ArrayList<Map<String,String>> replys =snm.getAllReply(totalnum);
		repliednum=replys.size();
		ArrayList<Map<String,Object>> list=new ArrayList<Map<String, Object>>();  
		if(replys == null){
			Log.i("debug","回覆為空");
		}
		ArrayList<String> all=snm.getNumberFromPerson(snm.getPersonFromSendMessage(totalnum));
		for(Map<String,String> map:replys){
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("number",map.get("number"));
			m.put("time",TimeManager.toDisplayFormat(map.get("time")));
			m.put("content", map.get("content"));
			m.put("head",FileManager.readImgFromContact(currentUserNumber, map.get("number")+".png"));
			
			all.remove(m.get("number"));
			list.add(m);
		}
		Log.i("debug","回覆3");
		for(String num:all){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("number",num);
			map.put("time", "");
			map.put("content", "");
			map.put("head",FileManager.readImgFromContact(currentUserNumber, map.get("number")+".png"));
			
			list.add(map);
		}
		Log.i("debug","回覆4");
		return list;
    } 
	
 	 public final class ViewHolder{  
         public ImageView head;  
        // public ImageView statement;  
         public TextView title;  
         public TextView info;  
         public TextView time;  
     }  
	 //Adapter
	 public class MyAdapter extends BaseAdapter{  
		 private LayoutInflater inflater; 
		 
		  public MyAdapter(Context context){  
              this.inflater = LayoutInflater.from(context);  
          }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
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
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			ViewHolder holder=null;
			if(convertView==null)
			{
				holder=new ViewHolder();
				convertView=inflater.inflate(R.layout.listview_reply, null);
				holder.head=(ImageView)convertView.findViewById(R.id.friend_head);
				holder.title=(TextView)convertView.findViewById(R.id.ItemTitle);
				holder.info=(TextView)convertView.findViewById(R.id.ItemText);
				holder.head.setOnClickListener(new MyOnClickListener(position));
				holder.time=(TextView)convertView.findViewById(R.id.textview_time);
				convertView.setTag(holder);
			}
			else
			{
				holder=(ViewHolder)convertView.getTag();
			}
			
		 	final String content=(String)mData.get(position).get("content");
		 	final String num = (String)mData.get(position).get("number");
		 	String time=(String)mData.get(position).get("time");
		 	Bitmap head=(Bitmap)mData.get(position).get("head");
		 	holder.time.setText(time);
		 	holder.info.setText(content);
		 	holder.title.setText(num);
		 	
			//头像颜色改变
		 	if(content!="") 
		 	{   //Drawable draw= getResources().getDrawable(Integer.parseInt(head));
		 		holder.head.setImageBitmap((Bitmap)mData.get(position).get("head"));
		 	}
		 	 else 
		 	{
		 		 Drawable draw = (Drawable)new BitmapDrawable((Bitmap)mData.get(position).get("head"));
		 		draw.mutate();          
		        ColorMatrix cm = new ColorMatrix();          
		        cm.setSaturation(0);          
		        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);          
		        draw.setColorFilter(cf);
		        holder.head.setImageDrawable(draw);   
		 	}
			return convertView;
		}  
		 
		 
		 
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class MyOnClickListener implements OnClickListener{
		private int posi;
		public MyOnClickListener(int position){
			this.posi = position;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Map<String,Object> map = mData.get(posi);
			Intent intent = new Intent(SendActivity.this,Contacts.class);
			intent.putExtra(ShardPre.currentChatNumber,(String) map.get("number"));
			SendActivity.this.startActivity(intent);
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
					if(json.getString("type").equals("reply")){
						String rece_id=json.getString("from_phone")+json.getString("send_time");
						if(rece_id.equals(id)){
							System.out.println("收到了回复的通知"+json.toString());
						//新增数据
						
						//更新界面
						if(flag_ineye==true){	
							((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
						}
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
					return ;
				}	
				
				
				
			}
		}	
}

