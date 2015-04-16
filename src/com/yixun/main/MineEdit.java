package com.yixun.main;
import java.io.File;
import java.io.InputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.manager.Contact;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.HttpTool;
import com.yixun.manager.ImageTools;
import com.yixun.manager.JsonManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;

public class MineEdit extends Activity{
	//String myNumber = "12345678911";
	private EditText sign=null; 
	private ImageView head=null;
	private static int RESULT_LOAD_IMAGE = 1;
	private Button back=null;
	TextView tvName = null;//(TextView) findViewById(R.id.mineEdit_name);
	TextView tvNumber = null;//(TextView) findViewById(R.id.mineEdit_number);
	RadioGroup group=null;//(RadioGroup)findViewById(R.id.mineEdit_sexChose);
	private Button bSave=null;
	private static String myNumber=null;
	String picturePath=null;//ѡȡ��ͷ����ļ�λ��
	private Bitmap bitmap_head,mBitmap2,mBitmap,bitmap_new;
//	private RadioButton radio_man;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mine_edit); 
		initViews();
		myNumber=SettingUtils.get(getApplicationContext(),ShardPre.currentNumber,"");
		back.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		bitmap_head=FileManager.readImgFromContact(myNumber, myNumber+".png");
		head.setImageBitmap(bitmap_head);
		head.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
			
		});
		System.out.println("number:"+myNumber);
		InputStream is2 = getResources().openRawResource(R.drawable.room_bg_top);
		mBitmap2 = BitmapFactory.decodeStream(is2);
		ImageView iv2 = (ImageView) findViewById(R.id.mineEdit_front);
		iv2.setImageBitmap(adaptive(mBitmap2));
		
		
		InputStream is = getResources().openRawResource(R.drawable.contacts_picture);
		mBitmap = BitmapFactory.decodeStream(is);
		ImageView iv = (ImageView) findViewById(R.id.mineEdit_picture);
		iv.setImageBitmap(adaptive(mBitmap));	
		
		 
		
		
		
		//�㱣�� ��
		bSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RadioButton  checkRadioButton = (RadioButton) group.findViewById(group  
		                .getCheckedRadioButtonId());
				new UpdateInforTask().execute(Constants.IP_UPDATEINFOR);
				
			}});
		
	}
	private class UpdateInforTask extends AsyncTask<String,Void,Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			try{//SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "")
				System.out.println(sign.getText().toString());
				String sex=group.getCheckedRadioButtonId()==R.id.mineEdit_male?"M":"W";
				String sig="";
				if(sign.getText().toString()==null || sign.getText().toString().length()==0){
					sig="";
				}
			String result=HttpTool.sendJson(params[0], JsonManager.updateInfor(myNumber,sig,sex).toString(), Constants.UTF_8);
			if(result==null || result.equals(Constants.HTTP_SEND_ERROR)){
				return false;
			}
			System.out.println("result:"+result);
			JSONObject json=new JSONObject(new String(result.getBytes(Constants.UTF_8)));
//			JSONObject json=new JSONObject(result);s
			System.out.println("json:"+json);
			String code=JsonManager.getCode(json);
			if(code.equals("0")){
				return false;
			}
			if(picturePath!=null){//�ļ�·����Ϊ�յĻ���֤�����������ѡ�����Լ���ͷ��
				//�ϴ�ͷ�񣬳ɹ��Ļ���ѡ���ͼƬ�����Լ���ǰ��ͷ�������õ���ͷ��ĵط�ҲҪ����
				System.out.println("��ʼ�ϴ�ͷ��");
				File file=new File(picturePath);
				String flag=HttpTool.uploadFile(file, Constants.IP_UPLOADHEAD);
				if(flag.equals("0")){//ʧ�ܵĻ�
					return false;
				}
				Bitmap bm=ImageTools.minBitmap(getApplicationContext(), picturePath, 1);
				FileManager.saveBitmap(FileManager.toContactImage(myNumber, myNumber+"png"), bm);
			}
			DatabaseManager.updateWords(myNumber, getApplication(),myNumber,sign.getText().toString());
			DatabaseManager.updateSex(myNumber, getApplicationContext(), myNumber, sex);
			return true;
			}catch(Exception e){
				System.out.println("�쳣");
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==false){
				Toast.makeText(getApplicationContext(),"���¸�����Ϣʧ�ܣ�������",1000).show();
			}else{
				//�ڴ�Ҫ��ѡ���ͼƬ������
				bitmap_new=ImageTools.minBitmap(getApplicationContext(),picturePath,1);
	            head.setImageBitmap(bitmap_new);  
				Toast.makeText(getApplicationContext(),"���¸�����Ϣ�ɹ�",1000).show();
			}
		}
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Contact c=DatabaseManager.queryByNumber(myNumber,getApplicationContext(),myNumber);
		tvName.setText(c.name);
		sign.setText(c.words);
		tvNumber.setText(c.number);
		if(c.sex=="M")
			group.check(R.id.mineEdit_male);  
		else 
			group.check(R.id.mineEdit_female);
		group.setClickable(false);
	}
	public  Bitmap adaptive(Bitmap bitmap) {

		WindowManager wm = MineEdit.this.getWindowManager();
		// ��������
		float scalX = wm.getDefaultDisplay().getWidth();// ����
		float scalY = wm.getDefaultDisplay().getHeight();// ����
		Matrix matrix = new Matrix();
		int width = bitmap.getWidth();// ��ȡ��Դλͼ�Ŀ�
		int height = bitmap.getHeight();// ��ȡ��Դλͼ�ĸ�
		float w = scalX / 720;
		float h = scalY / 1280;
		matrix.postScale(w, h);// ��ȡ���ű���
		// �������ű�����ȡ�µ�λͼ
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        super.onActivityResult(requestCode, resultCode, data);  
	          
	        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {  
	            Uri selectedImage = data.getData();  
	            String[] filePathColumn = { MediaStore.Images.Media.DATA };  
	      
	            Cursor cursor = getContentResolver().query(selectedImage,  
	                    filePathColumn, null, null, null);  
	            cursor.moveToFirst();  
	      
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);  
	            picturePath = cursor.getString(columnIndex);  
	            cursor.close();  
	        } 
	    }
	 //��ʼ���ؼ�
	    private void initViews(){
			sign=(EditText)findViewById(R.id.mineEdit_sign);
			back=(Button)findViewById(R.id.mineEdit_back);
			tvName = (TextView) findViewById(R.id.mineEdit_name);
			sign = (EditText) findViewById(R.id.mineEdit_sign);
			tvNumber = (TextView) findViewById(R.id.mineEdit_number);
			head=(ImageView)findViewById(R.id.mineEdit_photo);
			bSave=(Button)findViewById(R.id.mineEdit_save);
			group=(RadioGroup)findViewById(R.id.mineEdit_sexChose);
	    }
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub

			//����bitmap
			if(bitmap_head!=null && bitmap_head.isRecycled()==false){
				bitmap_head.recycle();
			}
			if(mBitmap !=null && mBitmap.isRecycled()==false){
				mBitmap.recycle();
			}
			if(mBitmap2!=null && mBitmap2.isRecycled()==false){
				mBitmap2.recycle();
			}
			super.onDestroy();
		}
	    
}

