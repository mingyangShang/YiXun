package com.yixun.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.main.Add;
import com.yixun.main.ChatActivity;
import com.yixun.main.Contacts;
import com.yixun.main.Group;
import com.yixun.main.MyGroup;
import com.yixun.main.Select;
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
public class ContactsContentFragment extends Fragment implements OnItemClickListener,OnItemLongClickListener,OnScrollListener,Updateable{
	private View view = null;
	private OnScroll onScroll = null;
	private ViewPager viewPager = null;
	List<Map<String,Object>>  data_contact,data_discussion,data_group = null;//��ʾ��listview�е����ݼ���
	private List<View> views;// Tabҳ���б�  
	private View view_discu,view_group,view_contact;//����tab��Ӧ��view
	private ListView list_contact,list_group,list_discussion;//ÿ��view ��listView
	private ContactAdapter ada_contact,ada_group,ada_disu;
	private TextView txtOverlay;//������ĸ������ʾ����
    private int currIndex = 1;// ��ǰҳ�����  
	private int scrollState; // ������״̬
    int screenW;//��Ļ�Ŀ��
	private SideBar indexBar_contact,indexBar_dicussion,indexBar_group;//���ϵ���ĸ��
    private TextView textView1,textView2,textView3;	
    private RadioButton image_contact,image_discussion,image_group;
    private ImageView image_add;//����ѡ���ͼƬ
    private final int FLAG_CONTACT=0;
    private final int FLAG_DISCUSSION=1;
    private final int FLAG_GROUP=2;
    private String currentUserNumber;
    private RadioGroup group;
    private boolean flag_ineye=false;
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
			onScroll = (OnScroll)activity;
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
		if(DataManager.data_contact==null){
		DataManager.getContactData(getActivity());
		}data_contact=DataManager.data_contact;
		if(DataManager.data_discussion==null){
			DataManager.getDisData(getActivity());
		}data_discussion=DataManager.data_discussion;
		if(DataManager.data_group==null){
			DataManager.getGroupData(getActivity());
		}data_group=DataManager.data_group;
		ada_contact = new ContactAdapter(getActivity(), data_contact);
		ada_group = new ContactAdapter(getActivity(), data_group);
		ada_disu = new ContactAdapter(getActivity(), data_discussion);
		UpdateHandler.registerUpdater(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.contact_content_fragment, container, false);		
		initImageView();
		initViewPager();
		initSideBar();
		return view;
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(currIndex==0){//������������ɼ�
			flag_ineye=true;
			((BaseAdapter)list_discussion.getAdapter()).notifyDataSetChanged();
		}else{
			flag_ineye=false;
		}if(currIndex==1){//��������ϵ�˿ɼ�
			((BaseAdapter)list_contact.getAdapter()).notifyDataSetChanged();
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UpdateHandler.unRegisterUpdater(this);
	}
	//��ʼ��ImageView
	  private void initImageView(){
		  	image_discussion = (RadioButton)view.findViewById(R.id.title_discussion);
		  	image_discussion.setOnCheckedChangeListener(new MyOnClickListener());
		  	
		  	image_contact = (RadioButton)view.findViewById(R.id.title_contact);
		  	image_contact.setOnCheckedChangeListener(new MyOnClickListener());
		  	
		  	image_group = (RadioButton)view.findViewById(R.id.title_group);
		  	image_group.setOnCheckedChangeListener(new MyOnClickListener());
		  	
		  	image_add = (ImageView)view.findViewById(R.id.image_add);
		  	image_add.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					switch(currIndex){
					case 0://�½�������
						popNewDis();
						break;
					case 1://�½���ϵ��
						Intent intent = new Intent(getActivity(),Add.class);
						getActivity().startActivity(intent);
						break;
					case 2://�½��Խ�Ⱥ
						popNewDis();
						break;
					}					
				}
		  		
		  	});
		  	group = (RadioGroup)view.findViewById(R.id.group_contact);
		  }
	  //��ʼ��viewpager
	  private void initViewPager() {
			viewPager = (ViewPager) view.findViewById(R.id.pager);
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
		        list_discussion.setOnScrollListener(this);
		        //����adapter����������
		        list_contact.setAdapter(ada_contact);
		        list_discussion.setAdapter(ada_disu);
		        list_group.setAdapter(ada_group);
		        System.out.println("���ü�����");
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
	  
	  @Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		  switch(arg0.getId()){
		  case R.id.lv_contact:
			  Intent intent_con = new Intent(getActivity(),ChatActivity.class);
			  intent_con.putExtra(ShardPre.isPersonal,true);
			  intent_con.putExtra(ShardPre.currentChatNumber, (String)data_contact.get(arg2).get("number"));
			  getActivity().startActivity(intent_con);
			  intent_con=null;
			  break;
		  case R.id.lv_discussion:
			  Intent intent_dis = new Intent(getActivity(),ChatActivity.class);
			  intent_dis.putExtra(ShardPre.isPersonal, false);
				intent_dis.putExtra(ShardPre.currentChatNumber,(String)data_discussion.get(arg2).get("name"));
				getActivity().startActivity(intent_dis);
				intent_dis=null;
			  break;
		  case R.id.lv_group:
			  Intent intent_group = new Intent(getActivity(),MyGroup.class);
			  intent_group.putExtra(ShardPre.currentChatNumber, (String)data_group.get(arg2).get("name"));
			  getActivity().startActivity(intent_group);
			  intent_group = null;
			  break;
		  }
	}	 
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		 switch(arg0.getId()){
		  case R.id.lv_contact:
			  popContact(arg2).show();
			  break;
		  case R.id.lv_discussion:
			  popDiscussion(arg2).show();
			  break;
		  case R.id.lv_group:
			  popGroup(arg2).show();
			  break;
		  }
		return false;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//���Գ��ȣ��Ƿ������ݣ�
		Log.d("TestContactActivity", "TestContactActivity-----onScroll");
		/*if (mNicks != null && mNicks.length > 0){
			Log.d("TestContactActivity", "TestContactActivity-----onScroll0.0"+state);
			txtOverlay.setText(String.valueOf(PingYinUtil.converterToFirstSpell(mNicks[firstVisibleItem + (visibleItemCount >> 1)]).charAt(0)).toUpperCase());
		}*/
			
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		//����״̬�ı�ĵ���
		Log.d("TestContactActivity", "TestContactActivity-----onScrollStateChanged");
		/*this.scrollState = scrollState;
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE)
		{
			handler.removeCallbacks(disapearThread);
			// ��ʾ�ӳ�1s����ʧ
			handler.postDelayed(disapearThread, 1000);
		} else
		{
			txtOverlay.setVisibility(View.VISIBLE);
		}*/
	}

	//����һ����������textView��Listener
	  private class MyOnClickListener implements OnCheckedChangeListener{  
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				switch(buttonView.getId()){
				case R.id.title_contact:
					if(isChecked){
					viewPager.setCurrentItem(1);
					}
					break;
				case R.id.title_discussion:
					if(isChecked){
					viewPager.setCurrentItem(0);
					}
					break;
				case R.id.title_group:
					if(isChecked){
					viewPager.setCurrentItem(2);
					}
					break;
				}
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
	  private class DisapearThread implements Runnable {
			
			public void run() {
				Log.d("TestContactActivity", "TestContactActivity-----DisapearThread");
				// ������1.5s�ڣ��û��ٴ��϶�ʱ��ʾ����ִ���������
				/*if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE)
				{
					txtOverlay.setVisibility(View.INVISIBLE);
				}*/
			}
		}
	  //����һ����������viewpager��view�仯��listener
	  public class MyOnPageChangeListener implements OnPageChangeListener{  
	        public void onPageScrollStateChanged(int arg0) {  	              	              
	        }  
	        public void onPageScrolled(int arg0, float arg1, int arg2) {       
	        }  
	        public void onPageSelected(int arg0) {   
	        	switch(arg0){
	        	case 0:
	        		image_discussion.setChecked(true);
					image_group.setChecked(false);
					image_contact.setChecked(false);	
					currIndex=0;
					flag_ineye=true;
	        		break;
	        	case 1:
	        		image_discussion.setChecked(false);
					image_contact.setChecked(true);
					image_group.setChecked(false);
					currIndex=1;
					flag_ineye=false;
	        		break;
	        	case 2:
	        		image_discussion.setChecked(false);
					image_contact.setChecked(false);
					image_group.setChecked(true);
					currIndex=2;
					flag_ineye=false;
	        		break;
	        	}
	        }           
	    }  
	  private void getContactData(){
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
				 map.put("number", "");
				 map.put("head", bitmap2);
				 map.put("type", "discussion");
				 map.put("name",discussions[i]);
				 map.put("sign","");
				 data_discussion.add(map);
			 }
			Collections.sort(data_discussion,new PinyinComparator());
			
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
			
	  }
	  //������ϵ�˵���
	  private AlertDialog popContact(int position){
		  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("��Ҫ��<"+(String)data_contact.get(position).get("name")+">��ʲô")
		  		 .setIcon(R.drawable.delete)
		  		 .setItems(R.array.contact_pop_list,new MyDialogOnClickListener(FLAG_CONTACT,position))
		  		 .setCancelable(true);
		  AlertDialog dialog = builder.create();
		  return dialog;
	  }
	  //���������鵯��
	  private AlertDialog popDiscussion(int position){
		  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("��Ҫ��<"+(String)data_discussion.get(position).get("name")+">��ʲô")
		  		 .setIcon(R.drawable.delete)
		  		 .setItems(R.array.discussion_pop_list,new MyDialogOnClickListener(FLAG_DISCUSSION,position))
		  		 .setCancelable(true);
		  AlertDialog dialog = builder.create();
		  return dialog;
	  }
	  //�����Խ����鵯��
	  private AlertDialog popGroup(int position){
		  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
		  builder.setTitle("��Ҫ��<"+(String)data_group.get(position).get("name")+">��ʲô")
		  		 .setIcon(R.drawable.delete)
		  		 .setItems(R.array.group_pop_list,new MyDialogOnClickListener(FLAG_GROUP,position))
		  		 .setCancelable(true);
		  AlertDialog dialog = builder.create();
		  return dialog;
	  }
	  private class MyDialogOnClickListener implements DialogInterface.OnClickListener{
		  private int flag,position;
		  public MyDialogOnClickListener(int fla,int posi){
			  this.flag=fla;
			  this.position = posi;
		  }
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			switch(flag){
			case FLAG_CONTACT:
				System.out.println("�������ϵ��");
				dealContact(which,position);
				break;
			case FLAG_DISCUSSION:
				dealDiscussion(which,position);
				break;
			case FLAG_GROUP:
				dealGroup(which,position);
				break;
			}
			
		}
		  
	  }
	  private void dealContact(int which,int posi){
		  switch(which){
			  case 0://�ƶ�������
				  break;
			  case 1://�������������
				  break;
			  case 2://��ʼ����
				  Intent intent_con = new Intent(getActivity(),ChatActivity.class);
				  intent_con.putExtra(ShardPre.isPersonal,true);
				  intent_con.putExtra(ShardPre.currentChatNumber, (String)data_contact.get(posi).get("number"));
				  getActivity().startActivity(intent_con);
				  break;
			  case 3://�鿴��������
				  Intent intent = new Intent(getActivity(),Contacts.class);
					intent.putExtra(ShardPre.currentChatNumber,(String) data_contact.get(posi).get("number"));
					startActivity(intent);  
				  break;
			  case 4://�����˴Ӻ����б���ɾ��
				  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
				  builder.setTitle("��ʾ");
				  builder.setMessage("ȷ����"+(String) data_contact.get(posi).get("name")+"�Ӻ����б���ɾ����");
				  final int po = posi;
				  builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//��һ���µ��߳���ɾ�����й��ڴ��˵���Ϣ,������ϵ���б��и���
						new DeleteContactTask().execute(po);				
					}
				});
				  builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				  builder.create().show();
		  }
	  }
	  //��һ���µ��߳���ɾ��һ����ϵ��
	  private class DeleteContactTask extends AsyncTask{
		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			int position = (Integer)arg0[0];
			String name = (String)data_contact.get(position).get("name");
			String num = (String)data_contact.get(position).get("number");
			data_contact.remove(position);
			publishProgress(name);
			try{
			DatabaseManager.deleteContact(currentUserNumber, getActivity(), num);
			FileManager.deleteContact(currentUserNumber, num);
			FileManager.deleteContactImg(currentUserNumber, num+Constants.SUFFIX);			
			}catch(Exception e){
				return name;
			}
			return name;
		}
		@Override
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			((BaseAdapter)list_contact.getAdapter()).notifyDataSetChanged();
		}
		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getActivity(), "��ϵ��"+(String)result+"��ɾ��", 1000).show();
		}  
	  }
	  //������������ĳ����¼�
	  private void dealDiscussion(int which,int posi){
		  switch(which){
		  case 0://��������
			  Intent intent_dis = new Intent(getActivity(),ChatActivity.class);
			  intent_dis.putExtra(ShardPre.isPersonal, false);
				intent_dis.putExtra(ShardPre.currentChatNumber,(String)data_discussion.get(posi).get("name"));
				getActivity().startActivity(intent_dis);
				intent_dis=null;
			  break;
		  case 1://�鿴��������Ϣ
			  Intent intent = new Intent(getActivity(),Group.class);
				intent.putExtra(ShardPre.currentChatNumber,(String)data_discussion.get(posi).get("name"));
				startActivity(intent);
			  break;
		  case 2://������ѽ����������
			  Intent intent_invite = new Intent(getActivity(),Select.class);
			  intent_invite.putExtra(ShardPre.TYPE, Constants.TYPE_ADD_CONTACT_TO_DISCUSSION);
			  intent_invite.putExtra(ShardPre.NAME,(String) data_discussion.get(posi).get("name"));
			  getActivity().startActivityForResult(intent_invite, Constants.REQUEST_SELECT_CONTACT);
			  intent=null;
			  break;
		  case 3://�˳�������
			  break;
		  }
	  }
	  @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		getDisData();
		ada_disu.notifyDataSetChanged();
	}
	//�������Խ�Ⱥ�ĳ����¼�
	  private void dealGroup(int which,int posi){
		  switch(which){
		  case 0://�鿴��Ա
			  Intent intent_group = new Intent(getActivity(),MyGroup.class);
			  intent_group.putExtra(ShardPre.currentChatNumber, (String)data_group.get(posi).get("name"));
			  getActivity().startActivity(intent_group);
			  intent_group=null;
			  break;
		  case 1://�����ϵ�����÷���
			  Intent intent_dele = new Intent(getActivity(),Select.class);
			  intent_dele.putExtra(ShardPre.TYPE, Constants.TYPE_ADD_CONTACT_TO_GROUP);
			  intent_dele.putExtra(ShardPre.NAME,(String) data_group.get(posi).get("name"));
//			  intent_dele.putExtra(ShardPre.TYPE, Constants.TYPE_DELE_CONTACT_FROM_GROUP);
//			  intent_dele.putExtra(ShardPre.NAME,(String) data_group.get(posi).get("name"));
			  getActivity().startActivity(intent_dele);
//			  getActivity().startActivityForResult(intent, Constants.REQUEST_SELECT_CONTACT, null);
			  break;
		  case 2://�Ӹ÷�����ɾ����Ա			 
			  Intent intent = new Intent(getActivity(),MyGroup.class);
			  intent.putExtra(ShardPre.currentChatNumber, (String) data_group.get(posi).get("name"));			 
			  getActivity().startActivity(intent);
			  intent=null;
			  break;
		  case 3://ɾ���÷���
//			  new DeleteGroupTask().execute(posi);
			  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
			  builder.setTitle("��ʾ");
			  builder.setMessage("ȷ����"+(String) data_group.get(posi).get("name")+"���Զ���Ⱥ��ɾ����");
			  final int po = posi;
			  builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//��һ���µ��߳���ɾ�����й��ڴ������Ϣ,��������и���
					new DeleteGroupTask().execute(po);				
				}
			});
			  builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			  builder.create().show();
			  break;
		  }
	  }
	  //����һ���µ��߳�������ɾ���Զ�������¼�
	  private class DeleteGroupTask extends AsyncTask<Integer,Void,String>{
		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			String name = (String)data_group.get(params[0]).get("name");
			data_group.remove(params[0]);
			publishProgress(null);
			FileManager.deleteGroup(currentUserNumber, name);
			FileManager.deleteGroupImg(currentUserNumber, name+Constants.SUFFIX);
			return name;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(getActivity(), "�Զ���Ⱥ��"+result+"��ɾ��", 1000).show();
			ada_group.notifyDataSetChanged();
		}
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			((BaseAdapter)list_group.getAdapter()).notifyDataSetChanged();
		}  
	  }
	  private void popNewDis(){
		  View v = LayoutInflater.from(getActivity()).inflate(R.layout.new_, null);
		  final EditText ed = (EditText)v.findViewById(R.id.editname);
		  new AlertDialog.Builder(getActivity())  
		  .setTitle((currIndex==0)?"�½�������":"�½��Զ�����")  
		  .setIcon(android.R.drawable.ic_dialog_info)  
		  .setView(v)  
		  .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(currIndex==0){
				if(judgeDiscuExists(ed.getText().toString())==false){//�ɹ�
					new AddDiscuTask().execute(ed.getText().toString());
				}else{
					Toast.makeText(getActivity(), "���������Ѿ�����	", 1000).show();
				}
				}else{
					if(judgeGroupExists(ed.getText().toString())==false){//�ɹ�
						new AddDiscuTask().execute(ed.getText().toString());
					}else{
						Toast.makeText(getActivity(), "����������Ѵ���	", 1000).show();
					}
				}
			}
		})  
		  .setNegativeButton("ȡ��", null)  
		  .show();
	  }
	  //�ж�������������Ƿ��Ѵ���
	  private boolean judgeDiscuExists(String name){
		  List<String> discs = FileManager.getAllDiscussions(currentUserNumber);//(currentUserNumber);
			 if(discs.contains(name)==true){
				 return true;
			 }
			 return false;
	  }
	//�ж�������������Ƿ��Ѵ���
	  private boolean judgeGroupExists(String name){
		 List<String> groups = FileManager.getAllGroup(currentUserNumber);
		 if(groups.contains(name)==true){
			 return true;
		 }
		 return false;
	  }
	  private class AddDiscuTask extends AsyncTask<String,Void,Void>{

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			if(currIndex==0){
			FileManager.newDiscussion(currentUserNumber, arg0[0]);
			Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
			Map<String,Object>  map = new HashMap<String,Object>();
			 map.put("number", "");
			 map.put("head",bitmap2);
			 map.put("type", "discussion");
			 map.put("name",arg0[0]);
			 map.put("sign","");
			 data_discussion.add(map);
		Collections.sort(data_discussion,new PinyinComparator());
			}else{
				FileManager.newGroup(currentUserNumber, arg0[0]);
				Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.in);
					 Map<String,Object>  map = new HashMap<String,Object>();
					 map.put("number", "");
					 map.put("head", bitmap3);//�д��ı�
					 map.put("type", "group");
					 map.put("name",arg0[0]);
					 map.put("sign","");
					 data_group.add(map);
				Collections.sort(data_group,new PinyinComparator());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(currIndex==0){
			ada_disu.notifyDataSetChanged();
			}else{
				ada_group.notifyDataSetChanged();
			}
		}
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
							((BaseAdapter)list_discussion.getAdapter()).notifyDataSetChanged();
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
		}
}
