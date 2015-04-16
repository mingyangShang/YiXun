package com.yixun.main;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.HttpTool;
import com.yixun.manager.JsonManager;
import com.yixun.manager.ReceivedNoticeManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.manager.TimeManager;
import com.yixun.myview.SocketService;


public class ReplyActivity extends Activity {
	public ReceivedNoticeManager mm = null;
	Map<String,String> received_notice=null;
	private static final String KEY_HEAD = "head";
	private static final String KEY_CONTENT = "content";
	private String id;
	private String currentUserNumber;
	private EditText edit=null;
	private Button reply=null;
	public void newfile()
	{			
		try{
			Intent intent = getIntent();
			currentUserNumber = SettingUtils.get(this,ShardPre.currentNumber,"");
			 mm = new ReceivedNoticeManager(FileManager.toReceiveNotice(currentUserNumber));
			 id = intent.getStringExtra(ShardPre.currentReceiveId);
		}catch(IOException e){
			e.printStackTrace();
		}
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.replynotice);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		
		newfile();
		//传入消息的Id、
		received_notice = mm.getNoticeById(id);
		String head=received_notice.get("head");
		String notice=received_notice.get("content");
		TextView content=(TextView) findViewById(R.id.replycontent);
		TextView name=(TextView) findViewById(R.id.name);
		TextView time=(TextView) findViewById(R.id.time);
		ImageView picture=(ImageView) findViewById(R.id.user_head);
		TextView topic=(TextView)this.findViewById(R.id.title_topic);
		topic.setText("收到的通知");//要改名字
		//获的消息内容
		content.setText(notice);
		
		name.setText(mm.getNumber(head));
		time.setText(TimeManager.toDisplayFormat(mm.getTime(head)));
		picture.setBackgroundResource(R.drawable.head2);
		reply=(Button)findViewById(R.id.button_reply);
		reply.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//开启新的线程发送数据
				new ReplyTask().execute();
			}
		});		/*
				final Button yesButton=(Button) findViewById(R.id.button_yes);
				 yesButton.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v)
					{
						//点击yes按钮
						Toast.makeText(getApplicationContext(), "回复去",
							     Toast.LENGTH_SHORT).show();
						reply.setText(yesButton.getText());
					}
				});
				
				 final Button noButton=(Button) findViewById(R.id.button_no);
				noButton.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v)
					{
						//点击no按钮
						Toast.makeText(getApplicationContext(), "回复不去",
							     Toast.LENGTH_SHORT).show();
						reply.setText(noButton.getText());
					}
				});*/
	}
	private class ReplyTask extends AsyncTask<Void,Void,Boolean>{
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//通过socket发送消息
			 int posi=id.indexOf("/");
			 String phone=id.substring(0, posi);
			 String time=TimeManager.toSaveFormat(System.currentTimeMillis());
			String reply=JsonManager.replyNotice(currentUserNumber, phone, edit.getText().toString(), id.substring(posi+1), time).toString();
			boolean flag= HttpTool.SendMsg(SocketService.socket, reply);
			if(flag==false){
				return false;
			}
			/**
			 * 写成功了，界面更新，在数据库中存储
			 */
			//发送成功的话，在数据中写入回复
			String head=received_notice.get("head");
			DatabaseManager.insertReply(getApplicationContext(),phone+Constants.SEOER_ID+mm.getTime(head), time, edit.getText().toString());
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==true){
				// 子Layout要以view的形式加入到主Layout中 
				 View mBarView; 
					// 主Layout的容器加载子Layout的View 
					 RelativeLayout mRelativeLayout; 
					//给出关键内容 
					 
					// 加载子Layout 
					mBarView = View.inflate(getApplicationContext(), R.layout.my_reply, null); 
					// 找到容器 
					mRelativeLayout = (RelativeLayout)findViewById(R.id.relative_reply); 
					// 加上View 结束 
					edit=(EditText)findViewById(R.id.replyEdit);
					final TextView myReply = (TextView)mBarView.findViewById(R.id.myReply_TextView);
					if(edit.getText().toString()!=""){
						// 子Layout要以view的形式加入到主Layout中 
						mRelativeLayout.addView(mBarView); 
						myReply.setText(edit.getText().toString());
					}
			}
		}
		
	}
}

