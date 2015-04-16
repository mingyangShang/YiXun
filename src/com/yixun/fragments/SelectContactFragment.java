package com.yixun.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.main.NewInformActivity;
import com.yixun.manager.Contact;
import com.yixun.manager.DataManager;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.myview.HideBarScrollListener;
import com.yixun.myview.HideBarScrollListener.OnScroll;
import com.yixun.myview.PinyinComparator;
import com.yixun.myview.SideBar;
import com.yixun.myview.UpdateHandler;
import com.yixun.myview.Updateable;

public class SelectContactFragment extends Fragment implements Updateable,OnItemClickListener,OnClickListener,OnItemLongClickListener{
	private View view = null;
	private OnScroll onScroll = null;
	private ViewPager viewPager = null;
	List<Map<String,Object>>  data_contact,data_discussion,data_group = null;//��ʾ��listview�е����ݼ���
	private List<View> views;// Tabҳ���б�  
	private TextView txtOverlay;//������ĸ������ʾ����
	private List<String> names_contact,names_discussion,names_group,names = null;
//	private SelectContactAdapter adapter_contact,adapter_discussion,adapter_group;
	private View view_discu,view_group,view_contact;//����tab��Ӧ��view
	private SideBar indexBar_contact,indexBar_dicussion,indexBar_group;//���ϵ���ĸ��
    private int currIndex = 0;// ��ǰҳ�����  
    int screenW;//��Ļ�Ŀ��
    private ListView list_contact,list_discussion,list_group;
	private int state = 0;//״̬������
	private int checkbox=1;
	public  String[] mNicks;//������
	public  Map<Integer, String> p2s = new HashMap<Integer, String>();
	private ImageView ok;
//	private OnCancelSelect oncancel;
//	private OnOkSelect onok;
	public static final String SELECTED_NAMES = "names";
	public static final String BUNDLE_NAMES = "bundle_names";
	private boolean flag_ineye=false;
	private String currentUserNumber;
	private SelectContactAdapter ada_contact,ada_group,ada_disu;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
			onScroll = (OnScroll)activity;
//			oncancel = (OnCancelSelect)activity;
//			onok = (OnOkSelect)activity;
		}catch(ClassCastException e){
			e.printStackTrace();
		}
		views = new ArrayList<View>();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try{
			currentUserNumber = SettingUtils.get(getActivity(), ShardPre.currentNumber, "");
			}catch(Exception e){
				getActivity().finish();
			}
		UpdateHandler.registerUpdater(this);
		if(DataManager.data_contact==null){
			DataManager.getContactData(getActivity());
			}data_contact=DataManager.data_contact;
			if(DataManager.data_discussion==null){
				DataManager.getDisData(getActivity());
			}data_discussion=DataManager.data_discussion;
			if(DataManager.data_group==null){
				DataManager.getGroupData(getActivity());
			}data_group=DataManager.data_group;
			ada_contact = new SelectContactAdapter(getActivity(), data_contact,1);
			ada_group = new SelectContactAdapter(getActivity(), data_group,2);
			ada_disu = new SelectContactAdapter(getActivity(), data_discussion,0);
			
//			getDisData();
//			getGroupData();
//			ada_contact = new ContactAdapter(getActivity(), data_contact);
//			ada_group = new ContactAdapter(getActivity(), data_group);
//			ada_disu = new ContactAdapter(getActivity(), data_discussion);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.select_contact_content, container, false);		
		initImageView();
		initViewPager();
		initTextView();
		initSideBar();
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(currIndex==0){
			flag_ineye=true;
			((BaseAdapter)list_discussion.getAdapter()).notifyDataSetChanged();
		}else{
			flag_ineye=false;
		}
		if(currIndex==1){//������ϵ�˿ɼ�
			ada_contact.notifyDataSetChanged();
		}
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
	//��ʼ��ImageView
	  private void initImageView(){
	        ok = (ImageView)view.findViewById(R.id.ok_select);
	        ok.setOnClickListener(this);
	  }
	  //��ʼ��viewpager
	  private void initViewPager() {
			viewPager = (ViewPager) view.findViewById(R.id.pager_select);
			 LayoutInflater inflater=getActivity().getLayoutInflater();  
		        view_contact=inflater.inflate(R.layout.contact, null);
		        view_discu=inflater.inflate(R.layout.discussion, null); 
		        view_group=inflater.inflate(R.layout.group,null);
		        list_contact = (ListView)view_contact.findViewById(R.id.lv_contact);
		        list_group = (ListView)view_group.findViewById(R.id.lv_group);
		        list_discussion = (ListView)view_discu.findViewById(R.id.lv_discussion);
		        //���û���������
		        list_contact.setOnScrollListener(new HideBarScrollListener(onScroll));
		        list_group.setOnScrollListener(new HideBarScrollListener(onScroll));
		        list_discussion.setOnScrollListener(new HideBarScrollListener(onScroll));
		        //����adapter����������
		        list_contact.setAdapter(ada_contact);
		        list_discussion.setAdapter(ada_disu);
		        list_group.setAdapter(ada_disu);
		        //������Ŀ�ĵ���¼��ļ�����
		        list_contact.setOnItemClickListener(this);
		        list_discussion.setOnItemClickListener(this);
		        list_group.setOnItemClickListener(this);
		        //���ó����¼��ļ�����
		        list_contact.setOnItemLongClickListener(this);
		        list_discussion.setOnItemLongClickListener(this);
		        list_group.setOnItemLongClickListener(this);
		        //��views���view
		        views.add(view_discu);  
		        views.add(view_contact);  
		        views.add(view_group);
		        //��ʼ��viewpager
		        viewPager.setAdapter(new MyViewPagerAdapter(views)); 
		        viewPager.setCurrentItem(1);  
		        viewPager.setOnPageChangeListener(new MyOnPageChangeListener()); 
		}
	  private void initSideBar(){
			txtOverlay = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_position, null);
			txtOverlay.setVisibility(View.INVISIBLE);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
			getActivity().getWindowManager().addView(txtOverlay, lp);
			//���ر��ϵ���ĸ
			//TextView txtOverlay ��������WindowManager����ʾ��ʾ�ַ�
			indexBar_contact = (SideBar) view_contact.findViewById(R.id.sideBar_contact);
			indexBar_contact.setListView(list_contact);
			indexBar_contact.setTextView(txtOverlay);
			
			indexBar_dicussion = (SideBar) view_discu.findViewById(R.id.sideBar_discussion);
			indexBar_dicussion.setListView(list_discussion);
			indexBar_dicussion.setTextView(txtOverlay);
			
			indexBar_group = (SideBar) view_group.findViewById(R.id.sideBar_group);
			indexBar_group.setListView(list_group);
			indexBar_group.setTextView(txtOverlay);
	  }
	  private void initTextView(){
		/*    textView1 = (TextView) view.findViewById(R.id.tv_tab_person);  
	        textView2 = (TextView) view.findViewById(R.id.tv_tab_group);
	        textView3 = (TextView) view.findViewById(R.id.tv_tab_crowd);
	        textView1.setOnClickListener(new MyOnClickListener(0));  
	        textView2.setOnClickListener(new MyOnClickListener(1)); 
	        textView3.setOnClickListener(new MyOnClickListener(2)); */ 
	  }
	  
	  @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		/*  Map<String,Object> map = new HashMap<String,Object>();
			try{
				ContactAdapter adapter = (ContactAdapter)arg0.getAdapter();
				map = (Map<String, Object>) adapter.getItem(arg2);
			}catch(ClassCastException e){
				e.printStackTrace();
				return ;
			}*/
//			System.out.println(map.get("name"));
		  switch(arg0.getId()){
		  case R.id.lv_contact:
			  Map<String,Object> ca = (Map<String, Object>) ada_contact.getItem(arg2);
			  System.out.println("���������ϵ��"+ca.get("number"));
			  break;
		  case R.id.lv_discussion:
			  Map<String,Object> cb = (Map<String, Object>) ada_disu.getItem(arg2);
			  System.out.println("�������������"+cb.get("name"));
			  break;
		  case R.id.lv_group:
			  Map<String,Object> cc = (Map<String, Object>) ada_group.getItem(arg2);
			  System.out.println("��������Խ�����"+cc.get("name"));
			  break;
		  }
		  Toast.makeText(getActivity(), "�����", 1000).show();
	}
	  @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			/*case R.id.cancel_select:
				oncancel.onCancelSelect();
				break;*/
			case R.id.ok_select:
//				onok.onOkSelect();
				Intent intent=new Intent(getActivity(),NewInformActivity.class);
				Bundle bundle=new Bundle();
				bundle.putStringArrayList(SELECTED_NAMES, (ArrayList<String>) getNames());
				intent.putExtra(BUNDLE_NAMES, bundle);
				startActivity(intent);
				break;
			}
		}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		System.out.println("������");
		return false;
	}

	//����һ����������textView��Listener
	  private class MyOnClickListener implements OnClickListener{  
	        private int index=0;  
	        public MyOnClickListener(int i){  
	            index=i;  
	        }  
	        public void onClick(View v) {  
	            viewPager.setCurrentItem(index);              
	        } 
	    }  
	  //����һ����������viewpager�й���view�仯��adapter
	  public class MyViewPagerAdapter extends PagerAdapter{  
	        private List<View> mListViews;  
	        public MyViewPagerAdapter(List<View> mListViews) {  
	            this.mListViews = mListViews;  
	        }  
	        @Override  
	        public void destroyItem(ViewGroup container, int position, Object object)   {     
	            container.removeView(mListViews.get(position));  
	        }  
	        @Override  
	        public Object instantiateItem(ViewGroup container, int position) {            
	             container.addView(mListViews.get(position), 0);  
	             return mListViews.get(position);  
	        }  
	        @Override  
	        public int getCount() {           
	            return  mListViews.size();  
	        }  
	        @Override  
	        public boolean isViewFromObject(View arg0, Object arg1) {             
	            return arg0==arg1;  
	        }  
	    }  
	  //����һ����������viewpager��view�仯��listener
	  public class MyOnPageChangeListener implements OnPageChangeListener{  
	        public void onPageScrollStateChanged(int arg0) {  	              	              
	        }  
	        public void onPageScrolled(int arg0, float arg1, int arg2) {       
	        }  
	        public void onPageSelected(int arg0) {  
	        	 /* Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);//��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣  
	              currIndex = arg0;  
	              animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��  
	              animation.setDuration(300);  
	              imageView.startAnimation(animation);  */
	        }           
	    }  
	  
	/*  private void getContactData(){
		  data_contact = new ArrayList<Map<String,Object>>();
			//�����Ĵ����ݿ��ѯ������
			String suffix=".png";
			List<Contact> contacts = DatabaseManager.queryAll(currentUserNumber, getActivity());
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
	  private void getDisData(){
			data_discussion = new ArrayList<Map<String,Object>>();
			String[] discussions = new String[]{
					"������һ","�������","��������"
			};
			Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
			for(int i=0;i<discussions.length;++i){
				 Map<String,Object>  map = new HashMap<String,Object>();
				 map.put("head", bitmap2);
				 map.put("number", "");
				 map.put("name",discussions[i]);
				 map.put("sign","");
				 data_discussion.add(map);
			 }
			
	  }
	  private void getGroupData(){
		  data_group = new ArrayList<Map<String,Object>>();
			List<String> groups = FileManager.getAllGroup(currentUserNumber);
			Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.in);
			if(groups==null || groups.size()==0){
				return ;
			}
			for(String group:groups){
				 Map<String,Object>  map = new HashMap<String,Object>();
				 map.put("number", "");
				 map.put("head", bitmap3);//�д��ı�
				 map.put("type", "group");
				 map.put("name",group);
				 map.put("sign","");
				 data_group.add(map);
			}
			Collections.sort(data_group,new PinyinComparator());
	  }*/
	  public List<String> getNames(){
		  names = new ArrayList<String>();
//		  adapter_contact = (SelectContactAdapter)list_contact.getAdapter();
		  names_contact = ada_contact.getNames();
//		  adapter_discussion = (SelectContactAdapter)list_discussion.getAdapter();
		  names_discussion = ada_disu.getNames();
//		  adapter_group = (SelectContactAdapter)list_group.getAdapter();
		  names_group = ada_group.getNames();
		  names.addAll(names_contact);
		  List<String> name_in ;
		  int len;
		  for(String name:names_group){
			  name_in = FileManager.getMembersFromGroup(currentUserNumber, name);
			  //������Զ�����������û���˵Ļ�,������һ���ѡ��
			  if(name_in==null){
				  continue;
			  }
			  //������Զ�����������δ��ѡ�е���ϵ���������
			  /*len=name_in.size();
			  for(int i=0;i<len;++i){
				  name_in.set(i,DatabaseManager.queryByNumber(currentUserNumber, getActivity(), name_in.get(i)).name);
			  }*/
			  for(String n:name_in){
				  if(names.contains(n)==false){
					  names.add(n);
				  }
			  }
		  }
		  for(String name:names_discussion){
			 name_in = DatabaseManager.queryNumbersInDiscussion(currentUserNumber, getActivity(), name);
			  //�������������û���˵Ļ�
			 if(name_in==null){
				 continue;
			 }
			  //������Զ�����������δ��ѡ�е���ϵ����ʱ���
			 for(String n:name_in){
				 if(names.contains(n)==false){
					 names.add(n);
				 }
			 }
		  }
		  return names;
	  }
	  public interface OnCancelSelect{
		  void onCancelSelect();
	  }
	  public interface OnOkSelect{
		  void onOkSelect();
	  }
	  //����Լ��Ѿ�������Ļ�������Ϣ
	  @Override
		public void update(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 1:
				try{
					JSONObject json=(JSONObject)msg.obj;
					if(json.get("type").equals("disu")){//disu�����Ҫ��
						System.out.println(json.toString());
						//��������
						
						//���½���
						if(flag_ineye==true){
							ada_disu.notifyDataSetChanged();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		}
}

