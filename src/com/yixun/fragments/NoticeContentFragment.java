package com.yixun.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.yixun.R;
import com.yixun.main.ReplyActivity;
import com.yixun.main.SendActivity;
import com.yixun.manager.DataManager;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.ReceivedNoticeManager;
import com.yixun.manager.SendNoticeManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.manager.TimeManager;
import com.yixun.myview.HideBarScrollListener;
import com.yixun.myview.HideBarScrollListener.OnScroll;
import com.yixun.myview.UpdateHandler;
import com.yixun.myview.Updateable;

public class NoticeContentFragment extends Fragment implements OnItemClickListener,OnItemLongClickListener,Updateable{

	private View view = null;
	private ViewPager viewPager = null;
	List<Map<String,Object> > data_receive,data_send = null;//显示在listview中的数据集合
	private List<View> views;// Tab页面列表  
	private View view_receive,view_send;//两个tab对应的view
    private int currIndex = 0;// 当前页卡编号  
    private OnScroll onScroll = null;
    private Button send_image,receive_image;//标题的两个图片
    private String currentUserNumber;
    private boolean flag_send_ineye=false,flag_rece_ineye=false;
    private ListView list_send,list_rece;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
			onScroll = (OnScroll)activity;
			currentUserNumber = SettingUtils.get(activity, ShardPre.currentNumber, "");
		}catch(ClassCastException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		if(DataManager.data_receive==null){
			System.out.println("直接获取receive_data");
			DataManager.getReceiveData(getActivity());
		}data_receive=DataManager.data_receive;
		if(DataManager.data_send==null){
			DataManager.getSendData(getActivity());		
		}data_send=DataManager.data_send;
		UpdateHandler.registerUpdater(this);
//		getReceiveData();//得到接收的通知的数据
//		getSendData();//得到发送 的通知的数据
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//创建视图
		view = inflater.inflate(R.layout.notice_content_fragment, container, false);
        initImageView();//初始化imageview
        InitViewPager();//初始化viewpager
        return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		System.out.println("执行noticeContent的onStart函数");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(currIndex==0){//证明这是是通知在显示
			flag_send_ineye=true;
			flag_rece_ineye=false;
			((BaseAdapter)list_send.getAdapter()).notifyDataSetChanged();
		}else if(currIndex==1){
			flag_send_ineye=false;
			flag_rece_ineye=true;
			((BaseAdapter)list_rece.getAdapter()).notifyDataSetChanged();

		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		flag_rece_ineye=flag_rece_ineye=false;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UpdateHandler.unRegisterUpdater(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		try{
			InformAdapter adapter = (InformAdapter)arg0.getAdapter();
			map = (Map<String, Object>) adapter.getItem(arg2);
			String id = (String)map.get("id");
			boolean isSend = (Boolean)map.get("send");
			if(isSend){
				Intent intent = new Intent(getActivity(),SendActivity.class);
				intent.putExtra(ShardPre.currentSendId, id);
				getActivity().startActivity(intent);				
			}
			else{
				Intent intent = new Intent(getActivity(),ReplyActivity.class);
				intent.putExtra(ShardPre.currentReceiveId,id );
				getActivity().startActivity(intent);
			}
		}catch(ClassCastException e){
			e.printStackTrace();
			return ;
		}
		System.out.println(map.get("name"));
	}
	@Override//处理长按删除事件
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		InformAdapter adapter = (InformAdapter)arg0.getAdapter();
		System.out.println("长按"+adapter.getItem(arg2));
		if(arg0.getId()==R.id.list_receive){
			data_receive.remove(arg1);
		}else{
			data_send.remove(arg1);
		}
		adapter.notifyDataSetChanged();
		return true;//拦截相应事件
	}
	//初始化viewPager
	  private void InitViewPager() {  
		    viewPager=(ViewPager) view.findViewById(R.id.vPager); 
	        views=new ArrayList<View>();  
	        LayoutInflater inflater=getActivity().getLayoutInflater();  
	        view_receive=inflater.inflate(R.layout.receive_notice, null);
	        view_send=inflater.inflate(R.layout.send_notice, null); 
	        list_rece = (ListView)view_receive.findViewById(R.id.list_receive);
	        list_rece.setOnScrollListener(new HideBarScrollListener(onScroll));
	        list_send = (ListView)view_send.findViewById(R.id.list_send);
	        list_send.setOnScrollListener(new HideBarScrollListener(onScroll));
	        list_rece.setAdapter(new InformAdapter(getActivity(),data_receive));
	        list_rece.setOnItemClickListener(this);
	        list_rece.setOnItemLongClickListener(this);
	        list_send.setAdapter(new InformAdapter(getActivity(),data_send));
	        list_send.setOnItemClickListener(this);
	        list_send.setOnItemLongClickListener(this);
	        views.add(view_send);  
	        views.add(view_receive);  
	        viewPager.setAdapter(new MyViewPagerAdapter(views)); 
	        viewPager.setCurrentItem(0);  
	        send_image.setBackgroundColor(getResources().getColor(R.color.title_img_selected));
        	send_image.setTextColor(getResources().getColor(R.color.title_text_selected));
        	receive_image.setBackgroundColor(getResources().getColor(R.color.title_img_unselected));
        	receive_image.setTextColor(getResources().getColor(R.color.title_text_unselected));
	        viewPager.setOnPageChangeListener(new MyOnPageChangeListener()); 
	    }  
	  //初始化ImageView
	  private void initImageView(){
	        send_image = (Button)view.findViewById(R.id.send_bg);
			send_image.setOnClickListener(new MyOnClickListener(0));
			receive_image = (Button)view.findViewById(R.id.receive_bg);
			receive_image.setOnClickListener(new MyOnClickListener(1));
	  }
	  //定义一个用来监听textView的Listener
	  private class MyOnClickListener implements OnClickListener{  
	        private int index=0;  
	        public MyOnClickListener(int i){  
	            index=i;  
	        }  
	        public void onClick(View v) {  
	            viewPager.setCurrentItem(index); 
	            switch(index){
	            case 0:
	            	send_image.setBackgroundColor(getResources().getColor(R.color.title_img_selected));
	            	send_image.setTextColor(getResources().getColor(R.color.title_text_selected));
	            	receive_image.setBackgroundColor(getResources().getColor(R.color.title_img_unselected));
	            	receive_image.setTextColor(getResources().getColor(R.color.title_text_unselected));
	            	viewPager.setCurrentItem(0);
	            	break;
	            case 1:
	            	send_image.setBackgroundColor(getResources().getColor(R.color.title_img_unselected));
	            	send_image.setTextColor(getResources().getColor(R.color.title_text_unselected));
	            	receive_image.setBackgroundColor(getResources().getColor(R.color.title_img_selected));
	            	receive_image.setTextColor(getResources().getColor(R.color.title_text_selected));
	            	viewPager.setCurrentItem(1);
	            	break;
	            }	            
	        } 
	    }  
	  //定义一个用来监听viewpager中国的view变化的adapter
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
	  //定义一个用来监听viewpager中view变化的listener
	  public class MyOnPageChangeListener implements OnPageChangeListener{  
		  	public void onPageScrollStateChanged(int arg0) {  	              	              
	        }  
	        public void onPageScrolled(int arg0, float arg1, int arg2) {       
	        }  
	        public void onPageSelected(int arg0) {  
	        	switch(arg0){
	        	case 0:
	        		send_image.setBackgroundColor(getResources().getColor(R.color.title_img_selected));
	            	send_image.setTextColor(getResources().getColor(R.color.title_text_selected));
	            	receive_image.setBackgroundColor(getResources().getColor(R.color.title_img_unselected));
	            	receive_image.setTextColor(getResources().getColor(R.color.title_text_unselected));
	            	currIndex=0;
	            	flag_send_ineye=true;
	            	flag_rece_ineye=false;
	        		break;
	        	case 1:
	        		send_image.setBackgroundColor(getResources().getColor(R.color.title_img_unselected));
	            	send_image.setTextColor(getResources().getColor(R.color.title_text_unselected));
	            	receive_image.setBackgroundColor(getResources().getColor(R.color.title_img_selected));
	            	receive_image.setTextColor(getResources().getColor(R.color.title_text_selected));
	            	currIndex=1;
	            	flag_send_ineye=false;
	            	flag_rece_ineye=true;
	        		break;
	        	}
	        }           
	    }  
	  //得到接收的通知的数据
	  private void getReceiveData(){
		 data_receive = new ArrayList<Map<String,Object>>(); 
		 ReceivedNoticeManager nm = null;
		 try {
			nm = new ReceivedNoticeManager(FileManager.toReceiveNotice(currentUserNumber));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 String id = "1214";
		 for(int i=0;i<20;++i){
			 Map<String,Object>  map = new HashMap<String,Object>();
			 map.put("head", R.drawable.icon);
			 map.put("number", "22222222222");
			 map.put("name","商明阳");
			 map.put("message",nm.getDataFromBack(1).get(0).get("content"));
			 map.put("id", id);
			 map.put("send", false);
			 System.out.println(nm.getDataFromBack(1).get(0).get("content"));
			 System.out.println(nm.getDataFromBack(1).get(0).get("head"));
			 System.out.println(nm.getTime(nm.getDataFromBack(1).get(0).get("head")));
			 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(nm.getTime(nm.getNoticeById("1214").get("head")))));
			 data_receive.add(map);
		 }
	  }
	  //得到发送通知的数据
	  private void getSendData(){
		data_send = new ArrayList<Map<String,Object>>(); 
		SendNoticeManager snm = null;
		String id="343";
		String fileName = "343_5.txt";
		for(int i=0;i<20;++i){
			try {
				snm = new SendNoticeManager(FileManager.toSendNotice(currentUserNumber,id, 5));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//有待改变
			 Map<String,Object>  map = new HashMap<String,Object>();
			 map.put("number", "11111111111");
			 map.put("id", fileName);
			 map.put("send",true);
			 map.put("head", R.drawable.defaultcontact);
			 map.put("name","商");
			 map.put("message",snm.getAllReply(5).get(0).get("content"));
			 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(snm.getTimeFromSendMessage(5))));
			 data_send.add(map);
		 }
	  }
	  //更新数据

	@Override
	public void update(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case 1:
			JSONObject json=(JSONObject) msg.obj;
//			还要加数据的更新和界面是否要更新的判断
			try {
				if(json.get("type").equals("reply")){//如果是别人回复的通知
					System.out.println("回复");
					//新增数据
//					String id_send=json.getString("send_time")+" "+json.get("");
					/*Map<String,Object> map=new HashMap<String,Object>();
					 map.put("number", json.getString("from_phone"));
					 map.put("id", json.getString());//这个id是通知的文件名
					 map.put("send",true);
					 map.put("head", R.drawable.defaultcontact);
					 map.put("name","商");
					 map.put("message",snm.getAllReply(5).get(0).get("content"));
					 map.put("time",TimeManager.toDisplayFormat(TimeManager.toDate(snm.getTimeFromSendMessage(5))));
					 data_send.add(map);*/
					//更新界面
					if(flag_send_ineye==true){
						((BaseAdapter)list_send.getAdapter()).notifyDataSetChanged();
					}
				}
				else if(json.get("type").equals("message")){
					System.out.println("收到的通知");
					//新增数据
					 Map<String,Object>  map = new HashMap<String,Object>();
					map.put("number", json.getString("from_phone"));
					 map.put("name", DatabaseManager.queryByNumber(SettingUtils.get(getActivity(), ShardPre.currentNumber, ""), getActivity(), json.getString("from_phone")));
					 map.put("message", json.get("msg"));
					 map.put("id",json.getString("send_time")+"/"+json.getString("from_phone"));
					 map.put("send", false);
					 map.put("time", 
			TimeManager.toDisplayFormat(json.getString("send_time")));
					 data_receive.add(map);
					 ReceivedNoticeManager.write( json.getString("from_phone"), json.getString("send_time"), "0",json.getString("send_time")+"/"+json.getString("from_phone") ,json.getString("msg"));
					//更新界面
					if(flag_rece_ineye==true){
						((BaseAdapter)list_rece.getAdapter()).notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	  
}
