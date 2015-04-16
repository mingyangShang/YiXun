package com.yixun.main;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.manager.DataManager;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.HttpTool;
import com.yixun.manager.JsonManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;

public class Login extends Activity {

	String number=null;
	String name=null;
	String sex="M";
	String password1=null;
	String password2=null;
	String word=null;
	
	String error=null;
	
	EditText number_ed=null;
	EditText name_ed=null;
	RadioGroup sex_rd = null;
	EditText password1_ed=null;
	EditText password2_ed=null;
	EditText word_ed=null;
	Button exit_bu=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		number_ed=(EditText) findViewById(R.id.number_ed);
		name_ed=(EditText) findViewById(R.id.name_ed);
		sex_rd = (RadioGroup)this.findViewById(R.id.sex_rd);
		password1_ed=(EditText) findViewById(R.id.password1_ed);
		password2_ed=(EditText) findViewById(R.id.password2_ed);
		word_ed=(EditText) findViewById(R.id.word_ed);
		exit_bu=(Button) findViewById(R.id.exit_bu);
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
		
		
		sex_rd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //��ȡ������ѡ�����ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //����ID��ȡRadioButton��ʵ��
                RadioButton rb = (RadioButton)findViewById(radioButtonId);
                //�����ı����ݣ��Է���ѡ����
                if(rb.getText().equals("Ů")){
                	sex="W";
                }
                else{
                	sex="M";
                }
           }
        });
		
		
		
		exit_bu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				number=number_ed.getText().toString();
				name=name_ed.getText().toString();
				password1=password1_ed.getText().toString();
				password2=password2_ed.getText().toString();
				word=word_ed.getText().toString();
				error=null;
				AlertDialog.Builder builder = new Builder(Login.this);
				builder.setTitle("��ʾ");
				builder.setNegativeButton("ȷ��", null) ;
				//builder.show();
				if(number.length()!=11){
					error+="�ֻ��Ų���11λ\n";
				}
				if(name.equals("")){
					error+="����Ϊ��\n";
				}
				if(password1.equals("")){
					error+="����Ϊ��\n";
				}
				else if(password2.equals("")){
					error+="����Ϊ��\n";
				}
				else if(!password1.equals(password2)){
					error+="�����������벻ͬ\n";
				}
				if(error!=null){
					builder.setMessage(error);
					builder.show();
				}
				else{
					//��Ϣ��ȷ ��������
					 new SendRegiInforTask().execute(Constants.IP_REGISTER);
				}			 
			}
		});
		
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item)
	    {
	        // TODO Auto-generated method stub
	        if(item.getItemId() == android.R.id.home)
	        {
	            finish();
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//һ�����������������ע����Ϣ���߳�
	private class SendRegiInforTask extends AsyncTask<String,Void,Boolean>{
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==false){
				Toast.makeText(getApplication(),"ע��ʧ��",1000).show();
				return ;
			}
			Toast.makeText(getApplication(),"ע��ɹ�",1000).show();
			Intent intent=new Intent();
			intent.putExtra("number", number);
			intent.putExtra("key", password1);
			setResult(2, intent); 
			finish();
		}
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				if(word_ed.getText().toString()==null || word_ed.getText().toString().length()==0){
					word="";
				}
				String result=HttpTool.sendJson(Constants.IP_REGISTER, JsonManager.register(number, name, password1,sex,word).toString(),Constants.UTF_8);
				System.out.println("ע��"+result);
				if(result==null || result.equals(Constants.HTTP_SEND_ERROR)){
					return false;
				}
				JSONObject json=new JSONObject(result);
				String code=JsonManager.getCode(json);
				if(code.equals("0")){
					return false;
				}
				//�ڱ��ؽ����ļ�
				FileManager.newUser(getApplicationContext(), number);
				DatabaseManager.insertContact(number, getApplicationContext(), number, name, sex, word, "N",null);
				Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.defaultcontact);
				FileManager.newConImg(number, bitmap, number+".png");
				return true;			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
	}

}

