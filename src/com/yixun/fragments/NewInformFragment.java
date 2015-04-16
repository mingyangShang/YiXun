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

	private View view = null;//�������ݵĲ����ļ�
	private GridView receivers = null;//������
	private ImageView addRece = null;//����������
	private NewInformAdapter adapterReceivers = null;
	private List<Map<String,Object>> listData = null;
	private ImageView send = null;//����
	private OnNewNoticeListener onNewListener = null;//һ��������Ӧ������֪ͨ�Ľӿ�
	private List<String> persons = null;//ʢ�����н�����Ϣ���˵��˺�
	private List<String> unSelectedPersons = null;//�ڽ�������ʾ��û�б�ѡ�е��˺�
	private final String KEY_NAME = "NAME";//bundle�д�������ļ�
	private final String KEY_SELECE = "ISSELECTED";//bundle�д���Ƿ�ѡ�еļ�
	private final String KEY_CONTENT = "CONTENT";//֪ͨ������
	
	
	@Override//�����Fragment��activity�󶨵�ͬʱ��activityת��OnnewNoticeListener����
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
		//����������쳣�˳��Ļ�		
		if(savedInstanceState == null){
			System.out.println("�����쳣�˳�");
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
			//����������쳣�˳��Ļ�
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
			System.out.println("����onCreateVew");
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
			m = listData .get(i);//�õ�һ��map
			names.add((String)m.get("name"));//��������ӵ�names��bundle��
			boolean selected = persons.contains(m.get("name"))?true:false;//�ж��Ƿ�ѡ��
			select[i] = selected;//��ѡ��������ӵ�select�е�bundle��
			m.put("selected",selected);//�ı�map��ѡ�����
			listData .set(i,m);//�ı����ݶ�Ӧ��map
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
				map.put("name","������");
				map.put("selected",true);
				Data.add(map);
				
				map = new HashMap<String,Object>();
				map.put("name", "zhao");
				map.put("selected",true);
				Data.add(map);
				onClickAdd(Data);
				break;
			case R.id.image_face:
				Log.i("debug","����˱���");
				break;
			case R.id.img_send:				
				onClickSend();
				break;
		}
	}

/*	private List<Map<String,Object> > getData(){
		List<Map<String,Object> > listData = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name","������");
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



	//��������ϵ�˺�ִ��
	public void onClickAdd(List<Map<String,Object>> newData){			
		boolean changed = false;
		listData = adapterReceivers.getListData();
		List<String> unSelectedPer = adapterReceivers.getUnSelectedNumber();
		System.out.println("unSeleced");
		for(String s:unSelectedPer){
			System.out.println(s);
		}
		persons = adapterReceivers.getSelectedNumber();
		//��ǰûѡ��Ҳû���������ӵ�����ʱ�������ǵ�δѡ��״̬
		for(int i=0;i<listData.size();++i){
			Map<String,Object> map = listData.get(i);
			boolean inNew = false;
			final String name = map.get("name").toString();
			//�����ǰûѡ��
			if(unSelectedPer.contains(name)) {
				for(Map<String,Object> m:newData){
					if(m.get("name").equals(name)){
						inNew = true;
						break;
					}
				}
				//�����������
				if(inNew==false){
					map.put("selected", false);
					listData.set(i, map);
				}
			}			
		}
		for(Map<String,Object> map:newData){
			String name = (String) map.get("name");
			//��������ӵ��˲��ڵ�ǰ���б��У����
			if(unSelectedPer.contains(name)==false && persons.contains(name)==false){
				listData.add(map);
				System.out.println("���name:"+name);		
				changed = true;
			}
			//��������ӵ����а���ǰ���б��У�����û�б�ѡ�У���ͨ���ı�map���ı���Ӧ��ѡ��״̬
			else if(unSelectedPer.contains(name)){
				System.out.println(name+"�ڵ�ûѡ��");
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
		//��ǰûѡ��Ҳû���������ӵ�����ʱ�������ǵ�δѡ��״̬
			if(changed == true){
				adapterReceivers.notifyDataSetChanged();
			}
	}
	//������ͺ�ִ��
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
