package com.yixun.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.fragments.MyGroupAdapter;
import com.yixun.fragments.SelectContactFragment;
import com.yixun.manager.Contact;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;

public class MyGroup extends Activity implements OnItemClickListener,OnItemLongClickListener{

	
	private String myNumber;
	private String groupName;
	private ListView listview;
	private MyGroupAdapter adapter,adapter_show;
	private List<Map<String,Object>> data;
	private TextView title;
	private Button back;
	private final boolean FLAG_SHOW = true;
	private final boolean FLAG_HIDE = false;
//	private boolean flag_show=false,flag_unshow=true;//是否显示checkbox的标记
	private View addToGroup,deleteFromGroup = null;//向组中添加联系人的按钮
	private boolean showCheck = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mygroup);		
		getNumber();
		getData();
		initViews();
		initListView();
	}
	private void getNumber(){
		try{
		myNumber = SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
		Intent intent = getIntent();
		groupName = intent.getStringExtra(ShardPre.currentChatNumber);
		}catch(Exception e){
			finish();
		}
	}
	private void getData(){
		data = new ArrayList<Map<String,Object>>();
		ArrayList<String> listNumber = FileManager.getMembersFromGroup(myNumber, groupName);
		if(listNumber==null){
			return ;
		}
		for(String number:listNumber){
			Contact contact = DatabaseManager.queryByNumber(myNumber, getApplicationContext(), number);
			if(contact!=null){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("number", number);
			map.put("name", contact.name);
			map.put("sign", contact.words);
			map.put("show",false);
			map.put("head", FileManager.readImgFromContact(myNumber, number+".png"));
			data.add(map);
			}
		}
	}
	private void initViews(){
		title = (TextView)findViewById(R.id.mygrouptextview);
		title.setText(groupName);
		back = (Button)findViewById(R.id.mygroup_exit);
		back.setOnClickListener(new OnClickListener(){
			public void onClick(View a){				
				finish();	
			}
		});
		addToGroup = (View)findViewById(R.id.add_foot);
		addToGroup.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//跳转到添加联系人界面，寻则要加入到该分组的联系人
				if(showCheck==false){
				Intent intent = new Intent(getApplicationContext(),Select.class);
				  Bundle bundle = new Bundle();
				  intent.putExtra(ShardPre.TYPE, Constants.TYPE_ADD_CONTACT_TO_GROUP);
				  intent.putExtra(ShardPre.NAME,groupName);
				  startActivityForResult(intent,Constants.REQUEST_SELECT_CONTACT);
				}else{
					for(String n:adapter.numbers){
						System.out.println(n);
					}
					new  DeleteContactTask().execute(adapter.numbers);
				}
			}
		});
		deleteFromGroup = (View)findViewById(R.id.delete_foot);
		deleteFromGroup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showCheck==false){
				adapter.setFlag(true);
				adapter.notifyDataSetChanged();
				showCheck=true;			
				}else{//变为取消键
					adapter.setFlag(false);
					adapter.notifyDataSetChanged();
					showCheck=false;
				}
				
			}
		});
		
	}
	private void initListView(){
		listview = (ListView)findViewById(R.id.mygroup_listview);
//		listview.addFooterView(getLayoutInflater().inflate(R.layout.foot_mygroup, null) );//为listview添加页脚
		adapter = new MyGroupAdapter(this,data,false);
		System.out.println("size:"+data.size());
//		adapter_show = new MyGroupAdapter(getApplicationContext(), data, flag_show);//一个显示checkbox的适配器
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		popDialog(arg2).show();//是否删除的提示框
//		listview.setAdapter(adapter_show);
//		adapter_show.notifyDataSetChanged();
		return false;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		  Intent intent_con = new Intent(getApplicationContext(),ChatActivity.class);
		  intent_con.putExtra(ShardPre.isPersonal,true);
		  intent_con.putExtra(ShardPre.currentChatNumber, (String)data.get(arg2).get("number"));
		  startActivity(intent_con);
		  intent_con=null;
	}
	private Dialog popDialog(int position){
		final Map<String,Object>  map = data.get(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示");
		builder.setMessage("要将"+map.get("name")+"从此群中删除吗");
		builder.setIcon(R.drawable.delete);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//删除
				try{
				FileManager.deleteMemberFromGroup(myNumber, groupName,(String)map.get("number"));
				}catch(Exception e){
					e.printStackTrace();
				}
				Toast.makeText(getApplicationContext(), (String)map.get("name")+"已从"+groupName+"中删除", 1000).show();
			}			
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}			
		});
		builder.setCancelable(true);
		AlertDialog dia = builder.create();
		return dia;
	}
	 private class DeleteContactTask extends AsyncTask<List<String>,Void,Void>{

		@Override
		protected Void doInBackground(List<String>... params) {
			// TODO Auto-generated method stub
			for(String number:params[0]){
			FileManager.deleteMemberFromGroup(myNumber,groupName , number);
			for(int i=0;i<data.size();++i){
				if(data.get(i).get("number").equals(number)){
					data.remove(i);
				}
			}			
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			showCheck=false;
			adapter.setListData(data);
			adapter.setFlag(false);
		}
			
	}
	 
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		super.onResume();
		System.out.println("调用onResume");
		/*data = new ArrayList<Map<String,Object>>();
		getData();
		System.out.println("data.size()"+data.size());
		adapter.setFlag(false);*/
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent da) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, da);
		switch(resultCode){
		case Constants.RESULT_SELECT_CONTACT:
			System.out.println("开始回传");
			getData();
			System.out.println(data.size());
			for(int i=0;i<data.size();++i){
				System.out.println(data.get(i));
			}
			adapter.setListData(data);
			adapter.setFlag(false);//刷新数据
			break;
		}
	}
	 
}
