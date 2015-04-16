package com.yixun.fragments;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;
import com.yixun.main.Contacts;
import com.yixun.main.Group;
import com.yixun.manager.ShardPre;

public class MessageAdapter extends BaseAdapter{
	
	private Context context;//上下文，与activity相联系
	private List<Map<String,Object> > data;//盛放数据
	private ViewHolder viewholder = null;//做优化使用
	
	public MessageAdapter(Context mContext,List<Map<String,Object> > mData){
		this.context = mContext;
		this.data = mData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		//返回listview的条目数，即data的大小
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);//返回一个map对象,通过这个来获得点击的人的账号
	}

	@Override//暂时还不知道什么意思
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		//加载每一条消息的布局文件
		if(arg1 == null){
			arg1 = LayoutInflater.from(this.context).inflate(R.layout.baseadapter_message,null);
			
			viewholder = new ViewHolder();
			viewholder.head = (ImageView)arg1.findViewById(R.id.head_contact);
			viewholder.name = (TextView)arg1.findViewById(R.id.name_contact);
			viewholder.message = (TextView)arg1.findViewById(R.id.message_contact);
			viewholder.time = (TextView)arg1.findViewById(R.id.time_contact);
			viewholder.head.setOnClickListener(new MyOnClickListener(arg0));
			arg1.setTag(viewholder);
			} else{
				viewholder = (ViewHolder)arg1.getTag();
			}
		
		//为头像添加监听器
		setData(arg0);	//为每一条设置数据
		return arg1;	//返回每一条的布局文件
		
	}
	
	
	private class MyOnClickListener implements OnClickListener{
		private int posi;
		public MyOnClickListener(int position){
			this.posi = position;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Map<String,Object> map = data.get(posi);
			
			if((Boolean)map.get("person")==true){
			Intent intent = new Intent(context,Contacts.class);
			intent.putExtra(ShardPre.currentChatNumber,(String) map.get("number"));
			context.startActivity(intent);
			}
			else {
				Intent intent = new Intent(context,Group.class);
				intent.putExtra(ShardPre.currentChatNumber, (String)map.get("name"));
				context.startActivity(intent);
			}
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
