package com.yixun.fragments;

import java.util.ArrayList;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixun.R;
import com.yixun.main.Contacts;
import com.yixun.manager.ShardPre;

public class MyGroupAdapter extends BaseAdapter{
	
	private Context context;//上下文，与activity相联系
	private List<Map<String,Object> > data;//盛放数据
	private ViewHolder viewholder = null;//做优化使用
	private boolean flag;//checkbox是否显示的标记
	public List<String> numbers;//被选中的人的账号的集合

	public MyGroupAdapter(Context mContext,List<Map<String,Object>> mData,boolean fl){
		this.context = mContext;
		this.data = mData;
		this.flag=fl;
		numbers = new ArrayList<String>();
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
			arg1 = LayoutInflater.from(this.context).inflate(R.layout.contact_item,null);
			viewholder = new ViewHolder();
			viewholder.tvCatalog = (TextView)arg1.findViewById(R.id.contactitem_catalog_unselect);
			viewholder.head = (ImageView)arg1.findViewById(R.id.head_contact_unselect);
			viewholder.name = (TextView)arg1.findViewById(R.id.name_contact_unselect);
			viewholder.sign = (TextView)arg1.findViewById(R.id.message_contact_unselect);
			viewholder.check = (CheckBox)arg1.findViewById(R.id.check_group);
			viewholder.head.setOnClickListener(new MyOnClickListener(arg0));
			viewholder.check.setOnClickListener(new MyOnCheckClickListener(arg0));
			arg1.setTag(viewholder);
			} else{
				viewholder = (ViewHolder)arg1.getTag();
			}
		//为头像添加监听器
		setData(arg0);	//为每一条设置数据
		return arg1;	//返回每一条的布局文件
	}
	
	
	//根据下标给每一项添加数据
	private void setData(int position){
		Map<String,Object> map = data.get(position);
		System.out.println("message的第"+position+"条");
		try{
			viewholder.tvCatalog.setVisibility(View.GONE);
			viewholder.head.setImageBitmap((Bitmap) map.get("head"));
			viewholder.name.setText((CharSequence) map.get("name"));
			viewholder.sign.setText((CharSequence) map.get("sign"));
			if(flag){
				viewholder.check.setVisibility(View.VISIBLE);
			}else{
				viewholder.check.setVisibility(View.GONE);
			}
		} catch(ClassCastException e){
			System.out.println("强制转换失败");
		} catch(NullPointerException e){
			System.out.println("viewholder为空指针");
		} finally{
			System.out.println("设置message数据函数结束");
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


	private  class ViewHolder{//优化
		public ImageView head;
		public TextView name;
		public TextView sign;
		public CheckBox check;
		public TextView tvCatalog;
 
	}
	//头像的监听器
	private class MyOnClickListener implements OnClickListener{
		private int position;
		public MyOnClickListener(int posi){
			this.position=posi;
		}
		@Override
		public void onClick(View arg0) {
			Map<String,Object> map = data.get(position);
			Intent intent = new Intent(context,Contacts.class);
			intent.putExtra(ShardPre.currentChatNumber,(String) map.get("number"));
			context.startActivity(intent);
		}		
	}
	//checkbox的监听器
	private class MyOnCheckClickListener implements OnClickListener {
		private int position;
		public MyOnCheckClickListener(int posi){
			this.position = posi;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			CheckBox box = (CheckBox)arg0;
			boolean isChecked = box.isChecked();
			String number = (String) data.get(position).get("number");
			if(isChecked==true && numbers!=null && numbers.contains(numbers)==false){
				numbers.add(number);
			}
			else if(isChecked==false && numbers!=null && numbers.contains(number)==true){
				numbers.remove(number);
			}		
		}
	}
	//设置标记
	public void setFlag(boolean f){
		this.flag=f;
		this.notifyDataSetChanged();//自动更新
	}
	
}
