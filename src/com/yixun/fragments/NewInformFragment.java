package com.yixun.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;

public class NewInformFragment extends Fragment implements OnClickListener{

	private View view = null;//整个内容的布局文件
	private GridView receivers = null;//收信人
	private ImageView addRece = null;//增加收信人
	private NewInformAdapter adapterReceivers = null;
	private List<Map<String,Object>> listData = null;
	private ImageView send = null;//发送
	private OnNewNoticeListener onNewListener = null;//一个用来响应发送新通知的接口
	private List<String> persons = null;//盛放所有接受消息的人的账号
	private List<String> unSelectedPersons = null;//在界面上显示但没有被选中的账号
	private final String KEY_NAME = "NAME";//bundle中存放姓名的键
	private final String KEY_SELECE = "ISSELECTED";//bundle中存放是否被选中的键
	private final String KEY_CONTENT = "CONTENT";//通知的内容
	
	
	@Override//在这个Fragment和activity绑定的同时将activity转成OnnewNoticeListener对象
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
			onNewListener = (OnNewNoticeListener)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString()
                    + " must implement OnNewNoticeListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addRece = (ImageView)getActivity().findViewById(R.id.image_addReceiver);
		/*adapterReceivers = new SimpleAdapter(getActivity(), getData(),R.layout.receiver_in_new,new String[]{"name","selected"},
				new int[]{R.id.text_receiver,R.id.check_receiver});*/
		//如果程序不是异常退出的话		
		if(savedInstanceState == null){
			System.out.println("不是异常退出");
			listData = new ArrayList<Map<String,Object>>();
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("name","LI");
			m.put("selected",true);
			listData.add(m);
			
			m = new HashMap<String,Object>();
			m.put("name", "zhao");
			m.put("selected",true);
			listData.add(m);
			
			m = new HashMap<String,Object>();
			m.put("name", "1");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "2");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "3");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "4");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "5");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "6");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "7");
			m.put("selected",true);
			listData.add(m);
			
			m = new HashMap<String,Object>();
			m.put("name", "8");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "9");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "10");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "11");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "12");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "13");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "14");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "15");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "16");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "17");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "18");
			m.put("selected",true);
			listData.add(m);
			m = new HashMap<String,Object>();
			m.put("name", "19");
			m.put("selected",true);
			listData.add(m);m = new HashMap<String,Object>();
			m.put("name", "20");
			m.put("selected",true);
			listData.add(m);
			
		}
		else if(savedInstanceState.containsKey(KEY_NAME) && savedInstanceState.containsKey(KEY_SELECE)){
			ArrayList<String> names = savedInstanceState.getStringArrayList(KEY_NAME);
			boolean[] select = savedInstanceState.getBooleanArray(KEY_SELECE);
			//如果程序是异常退出的话
			listData = new ArrayList<Map<String,Object>>();
 			for(int i=0;i<select.length;++i){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("name", names.get(i));
				map.put("selected", select[i]);
			    listData.add(map);
			}
		}
		adapterReceivers = new NewInformAdapter(getActivity(),listData);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view == null){
			System.out.println("调用onCreateVew");
			view =  inflater.inflate(R.layout.new_inform_fragment, container, false);
			receivers = (GridView)view.findViewById(R.id.gird_receiver);
			receivers.setAdapter(adapterReceivers);
//			receivers.setOnItemClickListener(this);
			receivers.setNumColumns(4);
			addRece = (ImageView)view.findViewById(R.id.image_addReceiver);
			addRece.setOnClickListener(this);
			send = (ImageView)view.findViewById(R.id.img_send);
			send.setOnClickListener(this);			
		}
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unSelectedPersons = adapterReceivers.getUnSelectedNumber();
		persons = adapterReceivers.getSelectedNumber();
		listData = adapterReceivers.getListData();
		Map<String,Object> m = null;
		for(int i=0;i<listData.size();++i){
			m = listData.get(i);
			m.put("selected",persons.contains(m.get("name"))?true:false);
			listData.set(i,m);
		}		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapterReceivers.notifyDataSetChanged();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		unSelectedPersons = adapterReceivers.getUnSelectedNumber();
		persons = adapterReceivers.getSelectedNumber();
		listData = adapterReceivers.getListData();
		Map<String,Object> m = null;
		ArrayList<String> names = new ArrayList<String>();
		boolean[] select = new boolean[listData .size()];
		for(int i=0;i<listData .size();++i){
			m = listData .get(i);//得到一个map
			names.add((String)m.get("name"));//将姓名添加到names的bundle里
			boolean selected = persons.contains(m.get("name"))?true:false;//判断是否被选择
			select[i] = selected;//将选择的情况添加到select中的bundle中
			m.put("selected",selected);//改变map的选中情况
			listData .set(i,m);//改变数据对应的map
		}
		outState.putStringArrayList(KEY_NAME,names);
		outState.putBooleanArray(KEY_SELECE, select);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.image_addReceiver:
				List<Map<String,Object>> Data = new ArrayList<Map<String,Object>>();
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("name","商明阳");
				map.put("selected",true);
				Data.add(map);
				
				map = new HashMap<String,Object>();
				map.put("name", "zhao");
				map.put("selected",true);
				Data.add(map);
				onClickAdd(Data);
				break;
			case R.id.image_face:
				Log.i("debug","点击了表情");
				break;
			case R.id.img_send:				
				onClickSend();
				break;
		}
	}

/*	private List<Map<String,Object> > getData(){
		List<Map<String,Object> > listData = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name","商明阳");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "zhao");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "qian");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "sun");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "li");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "zhou");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "wu");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "zheng");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "wang");
		map.put("selected",true);
		listData.add(map);
		
		map = new HashMap<String,Object>();
		map.put("name", "feng");
		map.put("selected",true);
		listData.add(map);
		
		this.listData  = listData;
		return listData;
	}*/



	//点击添加联系人后执行
	public void onClickAdd(List<Map<String,Object>> newData){			
		boolean changed = false;
		listData = adapterReceivers.getListData();
		List<String> unSelectedPer = adapterReceivers.getUnSelectedNumber();
		System.out.println("unSeleced");
		for(String s:unSelectedPer){
			System.out.println(s);
		}
		persons = adapterReceivers.getSelectedNumber();
		//以前没选中也没有在新增加的人中时保持他们的未选中状态
		for(int i=0;i<listData.size();++i){
			Map<String,Object> map = listData.get(i);
			boolean inNew = false;
			final String name = map.get("name").toString();
			//如果以前没选中
			if(unSelectedPer.contains(name)) {
				for(Map<String,Object> m:newData){
					if(m.get("name").equals(name)){
						inNew = true;
						break;
					}
				}
				//如果不在组里
				if(inNew==false){
					map.put("selected", false);
					listData.set(i, map);
				}
			}			
		}
		for(Map<String,Object> map:newData){
			String name = (String) map.get("name");
			//如果新增加的人不在当前的列表中，添加
			if(unSelectedPer.contains(name)==false && persons.contains(name)==false){
				listData.add(map);
				System.out.println("添加name:"+name);		
				changed = true;
			}
			//如果新增加的人中爱当前的列表中，但是没有被选中，则通过改变map来改变相应的选中状态
			else if(unSelectedPer.contains(name)){
				System.out.println(name+"在但没选中");
				for(int i=0;i<listData.size();++i){					
					Map<String,Object> m = listData.get(i);
					if(m.get("name").equals(name)){
						m.put("selected", true);
						listData.set(i,m);
						break;
					}
				}
				changed = true;
			}
		}
		//以前没选中也没有在新增加的人中时保持他们的未选中状态
			if(changed == true){
				adapterReceivers.notifyDataSetChanged();
			}
	}
	//点击发送后执行
	public void onClickSend(){
		onNewListener.onNewNoticeSend();
		try{
		persons = adapterReceivers.getSelectedNumber();
		Toast.makeText(getActivity(), "size="+persons.size(), 1000).show();
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}
	public interface OnNewNoticeListener{
		public void onNewNoticeSend();
	}
}
