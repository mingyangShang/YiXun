package com.yixun.main;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.HttpTool;
import com.yixun.manager.JsonManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.myview.SocketService;
public class loading extends Activity {
	private TextView tv;
	EditText id,keyword;
	Button loading,register;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		loading=(Button) findViewById(R.id.loading_loading);
		register=(Button)findViewById(R.id.new_loading);
		id=(EditText) findViewById(R.id.id_loading);
		keyword=(EditText) findViewById(R.id.keyword_load);
		String number=SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
		if(number.equals("")==false){
			id.setText(number);
		}
		number=SettingUtils.get(getApplicationContext(), ShardPre.currentKey, "");
		if(number.equals("")==false){
			keyword.setText(number);
		}
		number=null;
		tv=(TextView) findViewById(R.id.forget_loading);
		tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		loading.setOnClickListener(new View.OnClickListener() {
			//点击登录 传服务器验证
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
//				String mynumber = id.getText().toString();
//				String key = keyword.getText().toString();
				new SendRegiInforTask().execute(Constants.IP_LOGIN);
				/*if(judge(mynumber,key)==true){
					SettingUtils.set(getApplicationContext(), ShardPre.currentNumber, mynumber);
					SettingUtils.set(getApplicationContext(), ShardPre.currentKey, key);
					startActivity(new Intent(getApplicationContext(),MainActivity.class));
					finish();
				}*/
			}
		});
		register.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(loading.this,Login.class);
				startActivityForResult(intent, 0);
			}	
		});
			
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==0 && resultCode==2){
			id.setText(data.getStringExtra("number"));
			keyword.setText(data.getStringExtra("key"));
		}
	}

		//一个用来向服务器发送登录信息的线程
		private class SendRegiInforTask extends AsyncTask<String,Void,Boolean>{
			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(result==true){
				startService(new Intent(loading.this,SocketService.class));//启动service
				startActivity(new Intent(getApplicationContext(),MainActivity.class));
				finish();
				}else{
					Toast.makeText(getApplicationContext(), "登录失败", 1000).show();
				}
			}
			@Override
			protected Boolean doInBackground(String... arg0) {
				try {
					String result =	HttpTool.sendJson(arg0[0],JsonManager.login(id.getText().toString(),keyword.getText().toString()).toString(),Constants.UTF_8);

					if(result.equals(Constants.HTTP_SEND_ERROR)){
						return false;
					}else{
						JSONObject json=new JSONObject(result);
						System.out.println(json);
						String code=JsonManager.getCode(json);
						if(code.equals("0")){return false;}
						SettingUtils.set(getApplicationContext(), ShardPre.currentNumber,id.getText().toString());
						SettingUtils.set(getApplicationContext(), ShardPre.currentKey, keyword.getText().toString());
						/*if(SettingUtils.get(getApplicationContext(), ShardPre.FIRST_LOADING, "").equals("")){
							SettingUtils.set(getApplicationContext(),ShardPre.FIRST_LOADING,"true");
						}*/
						return true;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return true;
				}
			}
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
		}	
}
