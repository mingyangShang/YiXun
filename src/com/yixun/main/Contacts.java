package com.yixun.main;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixun.R;
import com.yixun.manager.Contact;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;

public class Contacts extends Activity {

	private String currentUserNumber;
	private String chatNumber;
	
	private String sex,name,sign;
	private Contact contact;
	private TextView tvName,tvSex,tvTitle,tvNumber,tvSign;
	private ImageView ivPhoto;
	Bitmap  bitmap;//ͷ��
	private final String SUFFIX = ".png";
	private final int MSG_SUCCESS = 1;
	private final int IMAGE_SUCCESS = 2;
	private Handler mHandler = new Handler() {  
	        public void handleMessage (Message msg) {//�˷�����ui�߳�����  
	            switch(msg.what) {  
	            case MSG_SUCCESS:  
	                contact = (Contact) msg.obj;//imageview��ʾ�������ȡ����logo  
	                String s = contact.sex.equals("W")==true?"Ů":"��";
	                tvName.setText(contact.name+"("+s+")");
	        		tvSign.setText(contact.words);
	        		tvNumber.setText("  "+chatNumber);
//	        		tvTitle.setText(contact.name);
//	                Toast.makeText(getApplication(), getApplication().getString(R.string.get_pic_success), Toast.LENGTH_LONG).show();  
	                break;  
	  
	            case IMAGE_SUCCESS:  
	            	ivPhoto.setImageBitmap(adaptive((Bitmap)msg.obj));
	                break;  
	            }  
	        }  
	    };  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		try{
			currentUserNumber = SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
			Intent intent = getIntent();
			chatNumber = intent.getStringExtra(ShardPre.currentChatNumber);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		InputStream is2 = getResources().openRawResource(R.drawable.room_bg_top);
		Bitmap mBitmap2 = BitmapFactory.decodeStream(is2);
		ImageView iv2 = (ImageView) findViewById(R.id.contacts_front);
		iv2.setImageBitmap(adaptive(mBitmap2));
		
		
		InputStream is = getResources().openRawResource(R.drawable.contacts_picture);
		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		ImageView iv = (ImageView) findViewById(R.id.contacts_picture);
		iv.setImageBitmap(adaptive(mBitmap));
		
		
		Button bBack = (Button) findViewById(R.id.contacts_back);
		bBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
				//�˴�Ӧ���ص������Ľ���
//				startActivity(new Intent(Contacts.this,ChatActivity.class));
				finish();
				
			}
		});
		tvName = (TextView) findViewById(R.id.contacts_name);
		tvSign = (TextView) findViewById(R.id.contacts_sign);
		tvNumber = (TextView) findViewById(R.id.contacts_number);
		tvTitle = (TextView) findViewById(R.id.contacts_title);
		ivPhoto = (ImageView) findViewById(R.id.contacts_photo);
			
		//���µ��ĸ����ݶ�Ӧ��ͨ�����ļ��Ľ��������
		getContact();
		getImage();
		/*String sSex = "��";
		String sName = "�м���";
		String sSign = "�ǺǺǺǺ�";
		final String sNumber = "15279873782";
		
		tvName.setText(sName+"("+sSex+")");
		tvSign.setText(sSign);
		tvNumber.setText("     �˺�                  "+sNumber);
		tvTitle.setText(sName);*/
		
		//�����źʹ�绰
		TextView message = (TextView)findViewById(R.id.contacts_chat);
		ImageView bCall = (ImageView) findViewById(R.id.contacts_call);
		ImageView bMessage = (ImageView) findViewById(R.id.contacts_message);
		message.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
				intent.putExtra(ShardPre.currentChatNumber,chatNumber);
				intent.putExtra(ShardPre.isPersonal,true);
				startActivity(intent);
			}
		});
		bCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent call = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+contact.number));
				startActivity(call);
				
			}
		});
		bMessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent message = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+contact.number));
				startActivity(message);
				
			}
		});
		
	}

	public  Bitmap adaptive(Bitmap bitmap) {

		WindowManager wm = this.getWindowManager();
		// ��������
		float scalX = wm.getDefaultDisplay().getWidth();// ����
		float scalY = wm.getDefaultDisplay().getHeight();// ����
		// android.util.Log.i("scalX", String.valueOf(scalX));
		// android.util.Log.i("scalY", String.valueOf(scalY));
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();// ��ȡ��Դλͼ�Ŀ�
		int height = bitmap.getHeight();// ��ȡ��Դλͼ�ĸ�
		float w = scalX / 720;
		float h = scalY / 1280;
		matrix.postScale(w, h);// ��ȡ���ű���
		// �������ű�����ȡ�µ�λͼ
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		// android.util.Log.i("newbmp", String.valueOf(newbmp.getWidth()));
		// android.util.Log.i("newbmp", String.valueOf(newbmp.getHeight()));
		return newbmp;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}
	//�õ��Ա�
	private void getContact(){
		new Thread(new Runnable(){
			public void run(){
				contact = DatabaseManager.queryByNumber(currentUserNumber, getApplicationContext(), chatNumber);
				if(contact==null){
					contact=new Contact("","","","","",null);
				}
				mHandler.obtainMessage(MSG_SUCCESS,contact).sendToTarget();
			}
		}).start();
	}
	//�õ�ͷ��
		private void getImage(){
			new Thread(new Runnable(){
				public void run(){
					bitmap = FileManager.readImgFromContact(currentUserNumber, chatNumber+SUFFIX);
					if(bitmap==null){
						InputStream is = getResources().openRawResource(R.drawable.defaultcontact);
						bitmap = BitmapFactory.decodeStream(is); 
					}
					mHandler.obtainMessage(IMAGE_SUCCESS,bitmap).sendToTarget();
				}
			}).start();
		}

}
