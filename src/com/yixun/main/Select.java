package com.yixun.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.fragments.SelectContactAdapter;
import com.yixun.manager.Contact;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.myview.PinyinComparator;

public class Select extends Activity implements OnClickListener{
	private View head_title,cancel,ok;
	private ListView contacts;
	private List<Map<String,Object>> data_contact;
	private SelectContactAdapter adapter;
	private int flag;
	private String currentUserNumber,name;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select);
		try{
		currentUserNumber = SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
		if(currentUserNumber.equals("")){
			finish();
		}
		Intent intent = getIntent();
		flag = intent.getIntExtra(ShardPre.TYPE, 0);
		name = intent.getStringExtra(ShardPre.NAME);
		System.out.println("name:"+name);
		}catch(Exception e){
			finish();
			e.printStackTrace();
		}		
		initViews();
		
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
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.select_ok://点击确定后
			switch(flag){
				case Constants.TYPE_ADD_CONTACT_TO_GROUP:
					List<String> list = adapter.getNames();  
					for(String s:list){
						FileManager.addMemberToGroup(currentUserNumber, name, s );
					}
					//回传数据
					setResult(Constants.RESULT_SELECT_CONTACT);
					finish();
					break;
				case Constants.TYPE_ADD_CONTACT_TO_DISCUSSION:
					List<String> list_ = adapter.getNames();  
					for(String s:list_){
						DatabaseManager.updateDiscussionAdd(currentUserNumber, getApplicationContext(), s, name);//(currentUserNumber, name, s , true);
						System.out.println("添加"+s);
					}
					//回传数据					
					setResult(Constants.RESULT_SELECT_CONTACT);
					finish();					
					break;
		}
		case R.id.select_cancel://点击取消后
			finish();
			break;
	}
		}

	//以下是自己写的方法
	private void initViews(){
		head_title = (View)findViewById(R.id.select_);
		ok = (View)findViewById(R.id.select_ok);
		cancel = (View)findViewById(R.id.select_cancel);
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
		contacts = (ListView)findViewById(R.id.select_list);
		setDataForList();
	}
	//获得listview 的数据
	private void setDataForList(){
		setData();
		switch(flag){
		case Constants.TYPE_ADD_CONTACT_TO_GROUP://如果要选择联系人的话
			adapter = new SelectContactAdapter(getApplicationContext(), data_contact, Constants.FLAG_CONSTACT);
			contacts.setAdapter(adapter);
			break;
		case Constants.TYPE_ADD_CONTACT_TO_DISCUSSION:
			adapter = new SelectContactAdapter(getApplicationContext(), data_contact, Constants.FLAG_CONSTACT);
			contacts.setAdapter(adapter);
			break;
		}
	}
	//为listview设置数据
	private void setData(){
		switch(flag){
		case Constants.TYPE_ADD_CONTACT_TO_GROUP:case Constants.TYPE_ADD_CONTACT_TO_DISCUSSION://选择联系人列表的数据
			setContactData();
			break;
		}
	}
	private void setContactData(){
		 data_contact = new ArrayList<Map<String,Object>>();
			//真正的从数据库查询的数据
			String suffix=".png";
			List<Contact> contacts = DatabaseManager.queryAll(currentUserNumber, this);
			if(contacts==null || contacts.size()==0){
				return ;
			}
			try{
			for(Contact contact:contacts){
				 Map<String,Object>  map = new HashMap<String,Object>();
				 map.put("head", FileManager.readImgFromContact(currentUserNumber, contact.number+suffix));
				 map.put("type", "contact");
				 map.put("number", contact.number);
				 map.put("name",contact.name);
				 map.put("sign",contact.words);
				 data_contact.add(map);

			}}catch(Exception e){
				
			}
			Collections.sort(data_contact,new PinyinComparator());
	}
	

	
}
