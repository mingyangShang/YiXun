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
		//������Ϣ��Id��
		received_notice = mm.getNoticeById(id);
		String head=received_notice.get("head");
		String notice=received_notice.get("content");
		TextView content=(TextView) findViewById(R.id.replycontent);
		TextView name=(TextView) findViewById(R.id.name);
		TextView time=(TextView) findViewById(R.id.time);
		ImageView picture=(ImageView) findViewById(R.id.user_head);
		TextView topic=(TextView)this.findViewById(R.id.title_topic);
		topic.setText("�յ���֪ͨ");//Ҫ������
		//�����Ϣ����
		content.setText(notice);
		
		name.setText(mm.getNumber(head));
		time.setText(TimeManager.toDisplayFormat(mm.getTime(head)));
		picture.setBackgroundResource(R.drawable.head2);
		reply=(Button)findViewById(R.id.button_reply);
		reply.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//�����µ��̷߳�������
				new ReplyTask().execute();
			}
		});		/*
				final Button yesButton=(Button) findViewById(R.id.button_yes);
				 yesButton.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v)
					{
						//���yes��ť
						Toast.makeText(getApplicationContext(), "�ظ�ȥ",
							     Toast.LENGTH_SHORT).show();
						reply.setText(yesButton.getText());
					}
				});
				
				 final Button noButton=(Button) findViewById(R.id.button_no);
				noButton.setOnClickListener(new View.OnClickListener(){
					public void onClick(View v)
					{
						//���no��ť
						Toast.makeText(getApplicationContext(), "�ظ���ȥ",
							     Toast.LENGTH_SHORT).show();
						reply.setText(noButton.getText());
					}
				});*/
	}
	private class ReplyTask extends AsyncTask<Void,Void,Boolean>{
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//ͨ��socket������Ϣ
			 int posi=id.indexOf("/");
			 String phone=id.substring(0, posi);
			 String time=TimeManager.toSaveFormat(System.currentTimeMillis());
			String reply=JsonManager.replyNotice(currentUserNumber, phone, edit.getText().toString(), id.substring(posi+1), time).toString();
			boolean flag= HttpTool.SendMsg(SocketService.socket, reply);
			if(flag==false){
				return false;
			}
			/**
			 * д�ɹ��ˣ�������£������ݿ��д洢
			 */
			//���ͳɹ��Ļ�����������д��ظ�
			String head=received_notice.get("head");
			DatabaseManager.insertReply(getApplicationContext(),phone+Constants.SEOER_ID+mm.getTime(head), time, edit.getText().toString());
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==true){
				// ��LayoutҪ��view����ʽ���뵽��Layout�� 
				 View mBarView; 
					// ��Layout������������Layout��View 
					 RelativeLayout mRelativeLayout; 
					//�����ؼ����� 
					 
					// ������Layout 
					mBarView = View.inflate(getApplicationContext(), R.layout.my_reply, null); 
					// �ҵ����� 
					mRelativeLayout = (RelativeLayout)findViewById(R.id.relative_reply); 
					// ����View ���� 
					edit=(EditText)findViewById(R.id.replyEdit);
					final TextView myReply = (TextView)mBarView.findViewById(R.id.myReply_TextView);
					if(edit.getText().toString()!=""){
						// ��LayoutҪ��view����ʽ���뵽��Layout�� 
						mRelativeLayout.addView(mBarView); 
						myReply.setText(edit.getText().toString());
					}
			}
		}
		
	}
}

