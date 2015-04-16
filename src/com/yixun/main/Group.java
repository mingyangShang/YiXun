package com.yixun.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.fragments.ContactAdapter;
import com.yixun.fragments.SelectContactAdapter;
import com.yixun.manager.Contact;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;

public class Group extends Activity {
	private TextView title;
	private String currentUserNumber;
	private String name;
	private View add;
	private ListView listview;
	private List<Map<String,Object>> data;
	private ContactAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_infor);
		getNumber();
		getData();
		initListView();
		initCons();
		
		/*final ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
		final SimpleAdapter sa = new SimpleAdapter(this, users,
				R.layout.people_show, new String[] { "photo", "name", "text" },
				// 分别对应view 的id
				new int[] { R.id.photo, R.id.name, R.id.text });
		((ListView) findViewById(R.id.group_users)).setAdapter(sa);
*/
		/*//往users中添加从数据库中获得的某个讨论组的所有成员
		{
			HashMap<String, Object> people = new HashMap<String, Object>();
			people.put("photo", R.drawable.user);
			people.put("name", "小明");
			people.put("text", "11111111111");
			users.add(people);
		}

		{
			HashMap<String, Object> people = new HashMap<String, Object>();
			people.put("photo", R.drawable.user);
			people.put("name", "小王");
			people.put("text", "22222222222");
			users.add( people);		}

		{
			HashMap<String, Object> people = new HashMap<String, Object>();
			people.put("photo", R.drawable.user);

			people.put("name", "小红");
			people.put("text", "33333333333");
			users.add(people);
			sa.notifyDataSetChanged();
		}*/

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		title.setText(name);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent da) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, da);
		System.out.println("毁坏");
		getData();
		adapter.refresh(data);
		
	}

	private void getNumber(){
		try{
			currentUserNumber = SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
			Intent intent = getIntent();
			name = intent.getStringExtra(ShardPre.currentChatNumber);
		}catch(Exception e){
			e.printStackTrace();
			finish();
		}
	}
	private void getData(){
		data = new ArrayList<Map<String,Object>>();
		List<Contact> persons = DatabaseManager.queryInDiscussion(currentUserNumber, getApplicationContext(), name);
		final String suffix=".png";
		if(persons==null){
			return ;
		}
		for(Contact contact:persons){
		Map<String,Object>  map = new HashMap<String,Object>();
		 map.put("head", FileManager.readImgFromContact(currentUserNumber, contact.number+suffix));
		 map.put("type", "contact");
		 map.put("number", contact.number);
		 map.put("name",contact.name);
		 map.put("sign",contact.words);
		 data.add(map);
		}
	}
	private void initListView(){
		adapter = new ContactAdapter(getApplicationContext(), data);
		listview = (ListView)findViewById(R.id.group_users);
		listview.setAdapter(adapter);
	}
	private void initCons(){
		title = (TextView)findViewById(R.id.text_dis_name);
		add = (ImageView)findViewById(R.id.add_contact_to_dis);
		add.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 Intent intent_invite = new Intent(getApplicationContext(),Select.class);
				  intent_invite.putExtra(ShardPre.TYPE, Constants.TYPE_ADD_CONTACT_TO_DISCUSSION);
				  intent_invite.putExtra(ShardPre.NAME,name);
				  startActivityForResult(intent_invite, Constants.REQUEST_SELECT_CONTACT);
				  intent_invite=null;
			}			
		});
	}

}
