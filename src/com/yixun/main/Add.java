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
	 /**��ϵ������**/  
    private ArrayList<String> mContactsName = new ArrayList<String>();  
    /**��ȡ��Phon���ֶ�**/  
    private static final String[] PHONES_PROJECTION = new String[] {  
        Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  
    /**��ϵ����ʾ����**/  
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
    /**��ϵ���˺�**/  
    private ArrayList<String> mContactsNumber = new ArrayList<String>();  
    /**�Ƿ��Ѿ��ں����еı��*/
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
		//�õ����������ݿ��е�������ϵ��
		List<String> contacts=DatabaseManager.queryAllNumbers(myNumber, getApplicationContext());
		if(contacts==null){
			for(int i=0;i<size;++i){
				mIsIn.add(out);
			}
		}else{
			for(int i=0;i<size;++i){
				//���ͨѶ�е���ϵ���ں�����ʱ��isin���ó�in
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
		noAddFriends.setAdapter(adapter_add);//�������ݵ�������
	}
	 /**�õ��ֻ�ͨѶ¼��ϵ����Ϣ**/
    private void getPhoneContacts() {
	ContentResolver resolver =getApplicationContext().getContentResolver();
	// ��ȡ�ֻ���ϵ��
	Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
			PHONES_PROJECTION, null, null, null);
	if (phoneCursor != null) {
		String phoneNumber=null,contactName=null;
	    while (phoneCursor.moveToNext()) {
		//�õ��ֻ�����
		phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
		//���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
		if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length()<11)
		    continue;
		//�õ���ϵ������
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
			//Ϊͷ����Ӽ�����
			setData(arg0);	//Ϊÿһ����������
			return arg1;	//����ÿһ���Ĳ����ļ�
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
				//������
				if(mIsIn.get(position).equals(in)){
					Toast.makeText(getApplicationContext(), mContactsName+"�Ѿ������ĺ�����", 1000).show();
				}else{
					System.out.println("�������"+mContactsNumber.get(position));
					//�ӷ�������ô��˵���Ϣ
					new AddFriendTask(position).execute(mContactsNumber.get(position));
				}
			}
		}
		private class ViewHolder{
			public TextView tv_number,tv_name;
			public Button bt_add;
		}
	}
	//���ͺ������������
	private class AddFriendTask extends AsyncTask<String, Void, Integer>{
		/**�������Ĳ�����ѡ����ӵ��˵��˺ţ���������Ѿ�ע���˵Ļ�����ô��ӳɹ���
		**��ӳɹ��Ļ�������������ϵ�˵Ľ������
		*Ȼ��Ҫ�����ݿ��в�������˵���Ϣ
		*��Ҫ���ļ��н�������˵���Ϣ
		*һ�µ�������0����������Ϣ��óɹ���1������Ӻ�������δ���ͳɹ���2����Ҫ���ҵ���ϵ�˻�Ϊע��
		*3�����ȡ���˵ĸ�����Ϣʧ�ܣ�4�����ȡ���˵�ͷ��ʧ��
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
			//�ɹ��������������ݿ����������
			result=HttpTool.sendJson(Constants.IP_USERINFOR, JsonManager.findUserInfor(params[0]).toString(), Constants.UTF_8);
			json=JsonManager.toJson(result);
			if(JsonManager.getCode(json).equals(Constants.CODE_FAIL)){
				return 3;
			}
			//�����Ϣ�ɹ�
			/**
			 * �ڱ��ص����ݿ��в��������
			 * ���´��˵�ͷ��,������Ϣ��
			 * ��ϵ���������ӣ�������ϵ�˽���Ҫ������
			 */
			
			Bitmap bitmap;
			try {
				bitmap = HttpTool.getImageStream(Constants.IP_GETIMAGE+params[0]+".png");
				if(bitmap==null){//֤���������Ǳ�û������˵�ͼƬ
					bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.defaultcontact);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return 4;
			}
			FileManager.newConImg(myNumber, bitmap, params[0]+".png");
			//��ӳɹ������˶�Ӧ�ĵ缫������Ϊ���ɵ��
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
				Toast.makeText(getApplicationContext(), "��ȡ������Ϣ�ɹ����Ͽ�ȥ�����",1000).show();
				adapter_add.notifyDataSetChanged();		
			}else if(result==1 || result==3 || result==4 ){
				Toast.makeText(getApplicationContext(), "��ȡ��Ϣʧ�ܣ����粻̫������"+result,1000).show();
			}else if(result==2){
				ad.show();
			}
		}		
	}
	private ProgressDialog showDialog(){
		 ProgressDialog xh_pDialog = new ProgressDialog(Add.this,AlertDialog.THEME_HOLO_LIGHT);  
         // ���ý�������񣬷��ΪԲ�Σ���ת��  
         xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
         // ����ProgressDialog ����  
         xh_pDialog.setTitle("��ʾ");  
         // ����ProgressDialog��ʾ��Ϣ  
         xh_pDialog.setMessage("������ȥ������Ϣ");  
         // ����ProgressDialog����ͼ��  
         xh_pDialog.setIcon(R.drawable.add);  
         // ����ProgressDialog �Ľ������Ƿ���ȷ false ���ǲ�����Ϊ����ȷ  
         xh_pDialog.setIndeterminate(false);  
         // ����ProgressDialog �Ƿ���԰��˻ؼ�ȡ��  
         xh_pDialog.setCancelable(false);  
         return xh_pDialog;
	}
	  public AlertDialog onShowDialogClick(){  
		   
	        AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);  
	        builder.setTitle("ò������������û��ע����");  
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
