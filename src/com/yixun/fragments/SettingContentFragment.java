package com.yixun.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yixun.R;
import com.yixun.main.MineEdit;
import com.yixun.main.loading;
import com.yixun.manager.Contact;
import com.yixun.manager.DatabaseManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.myview.ToggleListener;

public class SettingContentFragment extends Fragment implements OnClickListener,OnItemClickListener{

	private View view;
	private ListView lv;
	private Button button;
	private ImageView head;
	private View infor_lay;
	private LinearLayout layout_voice,layout_vibrator,layout_receive,layout_byOther;
	private ToggleButton toggle_voice,toggle_vibrator,toggle_receive,toggle_byOther;  
	private ImageButton toggleButton_voice,toggleButton_vibrator,toggleButton_receive,toggleButton_byOther;  
	private LinearLayout change_infor;
	private static final String voice = "声音效果";
	private static final String vibrator = "震动效果";
	private static final String receive = "离线时接收消息";
	private static final String byOther = "对方无网时用短信发通知";
	private String currentUserNumber;
	Bitmap bitmap=null;
//	private OnScroll onScroll;
	
	/*@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
			onScroll = (OnScroll)activity;
		}catch(ClassCastException e){
			System.out.println("activity没有实现onscroll");
			e.printStackTrace();
		}
	}*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		currentUserNumber = SettingUtils.get(getActivity(),ShardPre.currentNumber,"");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.setting_content_fragment, container, false);
		initLayouts();//初始化几个layout布局对象
		initCompons();//初始化几个控件
		initViews();//设置控件状态
		setListeners();//设置监听器
		
		
	/*	//为listview设置adapter
		SimpleAdapter adapter = new SimpleAdapter(getActivity(),getData(),R.layout.setting_control,new String[]{
			"title","img"
		},new int[]{R.id.text_point,R.id.image_point
		});
		adapter.setViewBinder(new ViewBinder(){

			@Override
			public boolean setViewValue(View arg0, Object arg1, String arg2) {
				// TODO Auto-generated method stub
				if(arg0 instanceof ImageView && arg1 instanceof Drawable){
					ImageView arg = (ImageView)arg0;
					arg.setImageDrawable((Drawable)arg1);
					return true;
				}
				return false;
			}
			
		});*/
//		lv.setAdapter(adapter);
		
		return view;
		
	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();				
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if(bitmap!=null && bitmap.isRecycled()==false){
			bitmap.recycle();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		System.out.println(""+arg2);
	}





	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.toggleButton_voice:case R.id.layout_voice:
			toggle_voice.toggle();
			break;
		case R.id.toggleButton_vibrator:case R.id.layout_vibrator:
			toggle_vibrator.toggle();
			break;
		case R.id.toggleButton_receive:case R.id.layout_receive:
			toggle_receive.toggle();
			break;
		case R.id.toggleButton_byOther:case R.id.layout_byOther:
			toggle_byOther.toggle();
			break;	
		}
	}

	
	private void initLayouts(){
		 layout_voice = (LinearLayout)view.findViewById(R.id.layout_voice);
		 layout_vibrator = (LinearLayout)view.findViewById(R.id.layout_vibrator);
		 layout_receive = (LinearLayout)view.findViewById(R.id.layout_receive);
		 layout_byOther = (LinearLayout)view.findViewById(R.id.layout_byOther);
		 change_infor = (LinearLayout)view.findViewById(R.id.layout_change_infor);
		 change_infor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(getActivity(),MineEdit.class);
//				intent.putExtra(ShardPre.currentNumber, currentUserNumber);
				getActivity().startActivity(new Intent(getActivity(),MineEdit.class));
			}
		 });
//		 layout_all = (LinearLayout)view.findViewById(R.id.setting_content);
	}
	private void initCompons(){
		toggle_voice = (ToggleButton)view.findViewById(R.id.toggle_voice);
		toggle_vibrator = (ToggleButton)view.findViewById(R.id.toggle_vibrator);
		toggle_receive = (ToggleButton)view.findViewById(R.id.toggle_receive);
		toggle_byOther = (ToggleButton)view.findViewById(R.id.toggle_byOther);
		
		toggleButton_voice = (ImageButton)view.findViewById(R.id.toggleButton_voice);
		toggleButton_vibrator = (ImageButton)view.findViewById(R.id.toggleButton_vibrator);
		toggleButton_receive = (ImageButton)view.findViewById(R.id.toggleButton_receive);
		toggleButton_byOther = (ImageButton)view.findViewById(R.id.toggleButton_byOther);
	}
	private void initToggleButton(boolean yes,int id){
		switch(id){
		case R.id.toggleButton_voice:
	        toggle_voice.setChecked(yes);  
	        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) toggleButton_voice 
	                .getLayoutParams(); 
	        if (yes) { // 如果是自动播放  
	            // 调整位置  
	            params1.addRule(RelativeLayout.ALIGN_RIGHT, -1);  
	            params1.addRule(RelativeLayout.ALIGN_LEFT,  
	                    R.id.toggleButton_voice);  
	            toggleButton_voice.setLayoutParams(params1);  
	            toggleButton_voice  
	                    .setImageResource(R.drawable.progress_thumb_selector);  
	            toggle_voice.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);  
	        } else {  
	            // 调整位置  
	            params1.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_voice);  
	            params1.addRule(RelativeLayout.ALIGN_LEFT, -1);  
	            toggleButton_voice.setLayoutParams(params1);  
	            toggleButton_voice  
	                    .setImageResource(R.drawable.progress_thumb_off_selector);  
	            toggle_voice.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);  
	        }  
	        break;
		case R.id.toggleButton_vibrator:
			    toggle_vibrator.setChecked(yes);  
		        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) toggleButton_vibrator
		                .getLayoutParams(); 
		        if (yes) { // 如果是自动播放  
		            // 调整位置  
		            params2.addRule(RelativeLayout.ALIGN_RIGHT, -1);  
		            params2.addRule(RelativeLayout.ALIGN_LEFT,  
		                    R.id.toggleButton_voice);  
		            toggleButton_vibrator.setLayoutParams(params2);  
		            toggleButton_vibrator  
		                    .setImageResource(R.drawable.progress_thumb_selector);  
		            toggle_vibrator.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);  
		        } else {  
		            // 调整位置  
		            params2.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_vibrator);  
		            params2.addRule(RelativeLayout.ALIGN_LEFT, -1);  
		            toggleButton_vibrator.setLayoutParams(params2);  
		            toggleButton_vibrator  
		                    .setImageResource(R.drawable.progress_thumb_off_selector);  
		            toggle_vibrator.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);  
		        }  
		        break;
		case R.id.toggleButton_receive:
			  toggle_receive.setChecked(yes);  
		        RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) toggleButton_receive 
		                .getLayoutParams(); 
		        if (yes) { // 如果是自动播放  
		            // 调整位置  
		            params3.addRule(RelativeLayout.ALIGN_RIGHT, -1);  
		            params3.addRule(RelativeLayout.ALIGN_LEFT,  
		                    R.id.toggleButton_voice);  
		            toggleButton_receive.setLayoutParams(params3);  
		            toggleButton_receive  
		                    .setImageResource(R.drawable.progress_thumb_selector);  
		            toggle_receive.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);  
		        } else {  
		            // 调整位置  
		            params3.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_receive);  
		            params3.addRule(RelativeLayout.ALIGN_LEFT, -1);  
		            toggleButton_receive.setLayoutParams(params3);  
		            toggleButton_receive  
		                    .setImageResource(R.drawable.progress_thumb_off_selector);  
		            toggle_receive.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);  
		        }  
		        break;
		case R.id.toggleButton_byOther:
			    toggle_byOther.setChecked(yes);  
		        RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) toggleButton_byOther 
		                .getLayoutParams(); 
		        if (yes) { // 如果是自动播放  
		            // 调整位置  
		            params4.addRule(RelativeLayout.ALIGN_RIGHT, -1);  
		            params4.addRule(RelativeLayout.ALIGN_LEFT,  
		                    R.id.toggleButton_voice);  
		            toggleButton_byOther.setLayoutParams(params4);  
		            toggleButton_byOther  
		                    .setImageResource(R.drawable.progress_thumb_selector);  
		            toggle_byOther.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);  
		        } else {  
		            // 调整位置  
		            params4.addRule(RelativeLayout.ALIGN_RIGHT, R.id.toggle_byOther);  
		            params4.addRule(RelativeLayout.ALIGN_LEFT, -1);  
		            toggleButton_byOther.setLayoutParams(params4);  
		            toggleButton_byOther  
		                    .setImageResource(R.drawable.progress_thumb_off_selector);  
		            toggle_byOther.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);  
		        }  
		        break;
			
		}
	}
	//初始化 views
	 private void initViews() { 
		 	int[] ids = new int[]{
		 			R.id.toggleButton_voice,R.id.toggleButton_vibrator,R.id.toggleButton_receive,R.id.toggleButton_byOther
		 	};
		 	boolean[] yes = new boolean[]{
		 			SettingUtils.get(getActivity(), SettingUtils.VOICE,false),
			        SettingUtils.get(getActivity(), SettingUtils.VIBRATOR,false), 
			    	SettingUtils.get(getActivity(), SettingUtils.RECEIVE,false),
			        SettingUtils.get(getActivity(), SettingUtils.BYOTHER, false) 			    	    	    	                
		 	};
	       for(int i=0;i<yes.length;++i){
	    	   initToggleButton(yes[i],ids[i]);
	       }
	       ((TextView)view.findViewById(R.id.exit_login)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),loading.class));
				getActivity().finish();
			}
	       });
	       Contact contact=DatabaseManager.queryByNumber(currentUserNumber, getActivity(), currentUserNumber);
	       ((TextView)view.findViewById(R.id.my_number)).setText(contact.number);
	       ((TextView)view.findViewById(R.id.my_name)).setText(contact.name);
	       bitmap=FileManager.readImgFromContact(currentUserNumber, currentUserNumber+".png");
	       ((ImageView)view.findViewById(R.id.my_head)).setImageBitmap(bitmap);
    }  
	 private void setListeners() {  
	     toggle_voice.setOnCheckedChangeListener(new ToggleListener(getActivity(),voice,toggle_voice,toggleButton_voice));  
	     toggle_vibrator.setOnCheckedChangeListener(new ToggleListener(getActivity(),vibrator,toggle_vibrator,toggleButton_vibrator));
	     toggle_receive.setOnCheckedChangeListener(new ToggleListener(getActivity(),receive,toggle_receive,toggleButton_receive));
	     toggle_byOther.setOnCheckedChangeListener(new ToggleListener(getActivity(),byOther,toggle_byOther,toggleButton_byOther));

	        toggleButton_voice.setOnClickListener(this);  
	        layout_voice.setOnClickListener(this);  
	        toggleButton_vibrator.setOnClickListener(this);  
	        layout_vibrator.setOnClickListener(this);  
	        toggleButton_receive.setOnClickListener(this);  
	        layout_receive.setOnClickListener(this);  
	        toggleButton_byOther.setOnClickListener(this);  
	        layout_byOther.setOnClickListener(this); 	      
	 }
}
