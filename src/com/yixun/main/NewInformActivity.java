package com.yixun.main;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.manager.DataManager;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.HttpTool;
import com.yixun.manager.JsonManager;
import com.yixun.manager.SendNoticeManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.manager.TimeManager;
import com.yixun.myview.SocketService;

public class NewInformActivity extends Activity implements OnClickListener{
	private View view = null;//整个内容的布局文件
	private GridView receivers = null;//收信人
	//private ImageView addRece = null;//增加收信人
	private NewInformAdapter adapterReceivers = null;
	private List<String> listData = null;//账号
	private View send = null;//发送
	private List<String> persons = null;//盛放所有接受消息的人的账号
//	private static final String KEY_NAME = "NAME";//bundle中存放姓名的键
	//private static final String KEY_SELECE = "ISSELECTED";//bundle中存放是否被选中的键
	private static final String KEY_CONTENT = "CONTENT";//通知的内容
	private static final String KEY_TIME="TIME";
	private int length = 4;//每行显示的人数
	private static final String POSITION = "POSITION";
	public static final String SELECTED_NAMES = "names";
	public static final String BUNDLE_NAMES = "bundle_names";
	private ListView sendNotice=null;//发送的通知的列表
	private SendNoticeAdapter send_adapter=null;//发送通知的适配器
	private List<Map<String,String>> sendData=null;
	private View edit=null;//编辑框
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_inform);
		adapterReceivers = new NewInformAdapter();
		Intent intent = getIntent();
		if(intent!=null){
			if(intent.getBundleExtra(BUNDLE_NAMES).containsKey(SELECTED_NAMES)){
				persons = intent.getBundleExtra(BUNDLE_NAMES).getStringArrayList(SELECTED_NAMES);
			}
			listData = persons;
		}
		else{
			getData();
		}
		initViews();		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//初始化控件
		initActionbar();//初始化actionbar
		receivers.setAdapter(adapterReceivers);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.room_send:
			Toast.makeText(this, "size"+listData.size(), 1000).show();
			if(persons==null || persons.size()==0){
				Toast.makeText(getApplicationContext(),"请选择接收通知的人",1000).show();
				return ;
			}
			if(((EditText) edit).getText().toString()==null || ((EditText) edit).getText().toString().length()==0){
				Toast.makeText(getApplicationContext(),"要发送的通知不能为空",1000).show();
			}
			new SendNewNoticeTask().execute();
			//发送
			//发送成功的话在前台显示，还要在后台写入数据
			//现在假设发送成功了
		/*	new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						SendNoticeManager send_manager=new SendNoticeManager(FileManager.toSendNotice(SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, ""), FileManager.toId(), persons.size()));
						send_manager.writeSendNotice(persons.size(), TimeManager.toSaveFormat(System.currentTimeMillis()), FileManager.getNumberString(persons), ((EditText)edit).getText().toString());				
						send_manager=null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			}).start();
			Map<String,String> map = new HashMap<String,String>();
			map.put(KEY_TIME, TimeManager.toSaveFormat(System.currentTimeMillis()));
			map.put(KEY_CONTENT, ((EditText) edit).getText().toString());
			sendData.add(map);
			send_adapter.notifyDataSetChanged();		
			break;*/
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_add:
			/*Intent intent = new Intent(this,MainActivity.class);
			intent.putExtra(POSITION,"3");
			this.startActivity(intent);
			break;*/
		case R.id.menu_search:
			System.out.println("按下了搜索键");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.actionbar_new, menu);
//	        SearchView searchView = (SearchView) menu.findItem(R.id.menu_add).getActionView();
	        return true;
	    }

	//初始化控件
	private void initViews(){
		receivers = (GridView)findViewById(R.id.gird_receiver);
		receivers.setNumColumns(length);
		/*addRece = (ImageView)findViewById(R.id.image_addReceiver);
		addRece.setOnClickListener(this);*/
		send = (ImageButton)findViewById(R.id.room_send);
		send.setOnClickListener(this);	
		edit=(EditText)findViewById(R.id.editText1);
		sendNotice=(ListView)findViewById(R.id.list_newinform);
		//设置监听器
		if(send_adapter==null){
			send_adapter=new SendNoticeAdapter();
		}
		if(sendData==null){
			sendData=new ArrayList<Map<String,String>>();
		}
		sendNotice.setAdapter(send_adapter);
	}
	//初始化actionbar
	private void initActionbar(){
		ActionBar bar = this.getActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowCustomEnabled(false);
		bar.setDisplayShowHomeEnabled(false);
		bar.setTitle("新通知");
		
	}
	//获得在gridview中显示的数据
	private void getData(){
		length = 4;
		listData = new ArrayList<String>();
		String[] names = new String[]{
				"上","明阳","李志磊","胡世豪","李振鹏","的份上就发生付款可"
		};
		for(int i=0;i<names.length;++i){
			listData.add(names[i]);
		}
		Toast.makeText(getApplicationContext(),"size:"+listData.size(),1000).show();
	}
	
	//自定义的adapter
	private class NewInformAdapter extends BaseAdapter{

		private ViewHolder viewholder = null;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Toast.makeText(getApplicationContext(),"开始绘制",1000).show();
			// TODO Auto-generated method stub
			if(arg1 == null){
				arg1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.receiver_in_new,null);
				viewholder = new ViewHolder();
				viewholder.name = (TextView)arg1.findViewById(R.id.text_receiver);
				viewholder.isSelected = (ImageView)arg1.findViewById(R.id.check_receiver);
				arg1.setTag(viewholder);
				} else{
					viewholder = (ViewHolder)arg1.getTag();
				}
			setData(arg0);	//为每一条设置数据
			//为什么必须放在这才行，放在上面if(arg1==null)中就不行？
			viewholder.isSelected.setOnClickListener(new DeleteListener(arg0));
			return arg1;//返回每一条的布局文件
		}
		private class ViewHolder{				//优化
			public TextView name;
			public ImageView isSelected;		
		}
		private void setData(int position){
			try{
				viewholder.name.setText((CharSequence) listData.get(position));
			} catch(ClassCastException e){
				System.out.println("强制转换失败");
			} catch(NullPointerException e){
				System.out.println("viewholder为空指针");
			}
		}
	}
	//用于给listview提供数据的adapter
	private class SendNoticeAdapter extends BaseAdapter{
		private ViewHolder viewholder=null;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return sendData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return sendData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(arg1==null){
				viewholder=new ViewHolder();
				arg1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.newnotice_item,null);
				viewholder.time_text=(TextView)arg1.findViewById(R.id.newnotice_time);
				viewholder.content_text=(TextView)arg1.findViewById(R.id.new_notice);
				arg1.setTag(viewholder);
			} else{
				viewholder = (ViewHolder)arg1.getTag();
			}
			setData(arg0);//设置数据
			return arg1;
		}
		private class ViewHolder{
			public TextView time_text;
			public TextView content_text;
		}
		//为每一条设置数据
		private void setData(int posi){
			viewholder.time_text.setText(sendData.get(posi).get(KEY_TIME));
			viewholder.content_text.setText(sendData.get(posi).get(KEY_CONTENT));
		}
	}
	public class DeleteListener implements OnClickListener{
		private int position;
		public DeleteListener(int arg0){
			this.position = arg0;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//删除后刷新
			listData.remove(this.position);
			adapterReceivers = (NewInformAdapter)receivers.getAdapter();
			adapterReceivers.notifyDataSetChanged();
		}
	}
	/**
	 * 这是用来发送新通知的线程
	 * @author Administrator
	 *
	 */
	private class SendNewNoticeTask extends AsyncTask<String,Void,Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			//通过socket发送消息
			String id_=FileManager.toId();
			boolean flag = HttpTool.SendMsg(SocketService.socket, JsonManager.sendNotice(DataManager.myNumber, persons, ((EditText) edit).getText().toString(), id_));
			//发送失败，返回false
			if(flag==false){
				return false;
			}
			//发送成功的话,新建文件记录新的通知
			SendNoticeManager send_manager=null;
			try {
				send_manager = new SendNoticeManager(FileManager.toSendNotice(DataManager.myNumber, id_, persons.size()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			send_manager.writeSendNotice(persons.size(), id_, FileManager.getNumberString(persons), ((EditText)edit).getText().toString());				
			send_manager=null;
			//在data中的数据中添加新的项
			Map<String,Object> map=new HashMap<String,Object>();
			 map.put("number", DataManager.myNumber);
			 map.put("id", id_+"_"+persons.size()+".txt");//这个id是通知的文件名
			 map.put("send",true);
			 if(DataManager.imageCache.get(DataManager.myNumber)==null){
				  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(DataManager.myNumber, DataManager.myNumber+".png"));	  
				  DataManager.imageCache.put(DataManager.myNumber, bitmapcache);
			 }
			 map.put("head", DataManager.imageCache.get(DataManager.myNumber));
			 map.put("name","我");
			 map.put("message",((EditText) edit).getText().toString());
			 map.put("time",TimeManager.toDisplayFormat(id_));
			 DataManager.data_send.add(0,map);
			 if(DataManager.data_send.size()>20){//如果现在新通知的显示item的数量》20的话，删除最下面的一个
				 DataManager.data_send.remove(DataManager.data_send.size()-1);
			 }
			 //在shared中保存
			 List<String> list=SettingUtils.getData(getApplicationContext(), Constants.SHARED_KEY_SEND_NOTICE);//(getApplicationContext(), Constants.SHARED_KEY_SEND_NOTICE, data)
			 list.add(0,id_+"_"+persons.size()+".txt");
			 if(list.size()>20){
				  list.remove(list.size()-1);
			 }
			 SettingUtils.saveData(getApplicationContext(), Constants.SHARED_KEY_SEND_NOTICE, list);
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==true){//成功发送的话，在前台显示
				Map<String,String> map = new HashMap<String,String>();
				map.put(KEY_TIME, TimeManager.toSaveFormat(System.currentTimeMillis()));
				map.put(KEY_CONTENT, ((EditText) edit).getText().toString());
				sendData.add(map);
				send_adapter.notifyDataSetChanged();	
				((EditText) edit).setText("");//将编辑框置空
				Toast.makeText(getApplicationContext(), "新通知发送成功", 1000).show();
			}else{
				Toast.makeText(getApplicationContext(), "新通知发送失败，请重试", 1000).show();
			}
		}
	}
}
