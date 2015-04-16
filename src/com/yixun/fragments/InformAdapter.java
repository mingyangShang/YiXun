package com.yixun.fragments;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixun.R;

public class InformAdapter extends BaseAdapter implements OnClickListener{

	private Context context = null;
	private List<Map<String,Object> > data = null;
	private ViewHolder viewholder = null;
//	private int flag;//是发送通知还是接收通知的标记(0为发送的标记，1为接收的标记)
	//构造函数，绑定数据和activity
	public InformAdapter(Context mContext , List<Map<String,Object> > mData){
		this.context = mContext;
		this.data = mData;
	}
	
	//返回一共有多少条
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);//返回map,通过这个来获得点击的人的账号
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		System.out.println("执行informadapter的getView函数");
		//加载每一条消息的布局文件
		if(arg1 == null){
			arg1 = LayoutInflater.from(this.context).inflate(R.layout.baseadapter_inform,null);
			viewholder = new ViewHolder();
			viewholder.head = (ImageView)arg1.findViewById(R.id.head_contact_notice);
			viewholder.name = (TextView)arg1.findViewById(R.id.name_contact_notice);
			viewholder.message = (TextView)arg1.findViewById(R.id.message_contact_notice);
			viewholder.time = (TextView)arg1.findViewById(R.id.time_contact_notice);
			arg1.setTag(viewholder);
			} else{
				viewholder = (ViewHolder)arg1.getTag();
			}
		viewholder.head.setOnClickListener(this);//为头像添加监听器
		setData(arg0);	//为每一条设置数据
		return arg1;	//返回每一条的布局文件
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		System.out.println("头像被点击");
		//输出对应的名字，只是测试，以后还要添加功能
		switch(arg0.getId()){
		case R.id.head_contact:
			break;
		}
		
	}
	
	
	//根据下标给每一项添加数据
	private void setData(int position){
		Map<String,Object> map = data.get(position);
		try{
			
			viewholder.head.setImageBitmap(((SoftReference<Bitmap>) map.get("head")).get());
			viewholder.name.setText((CharSequence) map.get("name"));
			viewholder.message.setText((CharSequence) map.get("message"));
			viewholder.time.setText((CharSequence) map.get("time"));
		} catch(ClassCastException e){
			System.out.println("强制转换失败");
		} catch(NullPointerException e){
			System.out.println("viewholder为空指针");
		} finally{
			System.out.println("设置inform数据函数结束");
		}
	}

		//返回此adapter中的数据,要求接受者判断是否为null
		public List<Map<String,Object> > getListData(){
			return this.data;
		}
		
		//设置adapter中的数据
		public boolean setListData(List<Map<String,Object> > listData){
			this.data = listData;//将本adapter对应的数据设置为参数listData
			if(listData == null){
				return false;
			} else{
				return true;
			}
		}


	private  class ViewHolder{				//优化	
		public ImageView head;
		public TextView name;
		public TextView message;
		public TextView time;
	}
	
	

	
}
