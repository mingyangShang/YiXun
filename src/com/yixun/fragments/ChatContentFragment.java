 package com.yixun.fragments;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.yixun.R;
import com.yixun.main.ChatActivity;
import com.yixun.manager.DataManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.myview.UpdateHandler;
import com.yixun.myview.Updateable;

public class ChatContentFragment extends ListFragment implements OnItemLongClickListener,Updateable{

	private View view = null;	//返回的主要布局
	private ListView list = null;  //主要布局中里的listview
	public final String[] items = new String[]{
			"head","name","message","time"
	};
	private List<Map<String,Object> > data = null;
//	private OnScroll onScroll = null;//用来监听listview滑动的监听器
	private String currentUserNumber;//用户自己的账号
	private String currentNumber;//个人聊天时对方的账号
	private String currentDiscuName;//群聊天的讨论组
	private MessageAdapter adapter;//listview的适配器
	private boolean flag_ineye;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
//			onScroll = (OnScroll)activity;
			currentUserNumber = SettingUtils.get(activity, ShardPre.currentNumber, ""); 
			
		}catch(ClassCastException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("和创建chatcontent");
//		buildData();
		if(DataManager.data_chat!=null){System.out.println("chat_data直接获取");
			data=DataManager.data_chat;
		}else{
			DataManager.getChatData(currentUserNumber, getActivity());
			data=DataManager.data_chat;
		}
		adapter = new MessageAdapter(getActivity(),data);
		UpdateHandler.registerUpdater(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//加载chat的内容布局
		view = inflater.inflate(R.layout.chat_content_fragment, container, false);
		this.setListAdapter(adapter);
		list = (ListView)view.findViewById(android.R.id.list);
		list.setOnItemLongClickListener(this);
//		list.setOnScrollListener(new HideBarScrollListener(onScroll));

		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Map<String,Object> map = (Map<String, Object>) this.getListAdapter().getItem(position);
		boolean isPerson = (Boolean) map.get("person");
		if(isPerson){//如果是个人聊天的话，拿到对方的账号
			
			currentNumber = (String)map.get("number");
			Intent intent = new Intent(getActivity(),ChatActivity.class);
			intent.putExtra(ShardPre.isPersonal, true);
			intent.putExtra(ShardPre.currentChatNumber, currentNumber);
			getActivity().startActivity(intent);
		}
		else{//如果是讨论组聊天的话
			currentDiscuName = (String)map.get("name");
			Intent intent = new Intent(getActivity(),ChatActivity.class);
			intent.putExtra(ShardPre.isPersonal, false);
			intent.putExtra(ShardPre.currentChatNumber, currentDiscuName);
			getActivity().startActivity(intent);
		}

	}

	@Override//长按删除
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		System.out.println(data.size());
		data.remove(arg2);
		adapter.notifyDataSetChanged();
		System.out.println(data.size());
		System.out.println(DataManager.data_chat.size());
		return true;//拦截点击事件
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("执行chatContentFragment的onviewCreated函数");
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		System.out.println("执行chatContentFragment的onPause函数");
		super.onPause();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		adapter.notifyDataSetChanged();
//		this.getListView().setOnItemLongClickListener(this);
    	System.out.println("执行chatContentFragment的onStart函数");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		flag_ineye=true;
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		flag_ineye=false;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UpdateHandler.unRegisterUpdater(this);
	}

	@Override
	public void update(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case 1:
			JSONObject json=(JSONObject)msg.obj;
			try {
				//如果是聊天消息的话
				if(json.get("type").equals("chat")){
					System.out.println("找到消息:"+json.toString());
					//新加数据,还要判断是不是讨论组
				/*	if(Map<String,Object> map:data){
						if(map.get("number"))
					}*/
					//更新界面
					if(flag_ineye==true){
						adapter.notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(msg.getData().get("shang"));
			break;
		}
	}
	
}
