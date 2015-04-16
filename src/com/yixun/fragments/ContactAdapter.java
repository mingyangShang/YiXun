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
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yixun.R;
import com.yixun.main.Contacts;
import com.yixun.main.Group;
import com.yixun.main.MyGroup;
import com.yixun.manager.Contact;
import com.yixun.manager.DataManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.ShardPre;
import com.yixun.myview.PingYinUtil;

public class ContactAdapter extends BaseAdapter implements OnClickListener,SectionIndexer{
	
	private Context context;//上下文，与activity相联系
	private List<Map<String,Object> > data;//盛放数据
	private ViewHolder viewholder = null;//做优化使用

	public ContactAdapter(Context mContext,List<Map<String,Object> > mData){
		this.context = mContext;
		this.data = mData;
	}
	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
		for (int i = 0; i < data.size(); i++)
		{
			String l = PingYinUtil.converterToFirstSpell((String)data.get(i).get("name")).substring(0, 1);
			char firstChar = l.toUpperCase().charAt(0);
			if (firstChar == section)
			{
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
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
			viewholder.head.setOnClickListener(new MyOnClickListener(arg0));
			arg1.setTag(viewholder);
			} else{
				viewholder = (ViewHolder)arg1.getTag();
			}
		
		//为头像添加监听器
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
			if(map.get("head")==null){
				if(DataManager.imageCache.get(map.get("number"))==null){
					  SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(FileManager.readImgFromContact(DataManager.myNumber, map.get("number")+".png"));	  
					  DataManager.imageCache.put((String)map.get("number"), bitmapcache);
				 }
			}
			viewholder.tvCatalog.setText(PingYinUtil.converterToFirstSpell((String)map.get("name")).substring(0, 1).toUpperCase());
		/*	if(DataManager.imageCache.get(map.get("head")).get()==null){
				DataManager.imageCache.put(arg0, arg1)
			}*/
//			viewholder.head.setImageBitmap(((SoftReference<Bitmap>) map.get("head")).get());
			viewholder.head.setImageBitmap(DataManager.imageCache.get(map.get("number")).get());
			viewholder.name.setText((CharSequence) map.get("name"));
			viewholder.sign.setText((CharSequence) map.get("sign"));
			String catalog = null;
			String lastCatalog = null;
			catalog = PingYinUtil.converterToFirstSpell((String)map.get("name")).substring(0, 1).toUpperCase();
			if (position == 0)
			{
				viewholder.tvCatalog.setVisibility(View.VISIBLE);
				viewholder.tvCatalog.setText(catalog);
			} else
			{
				lastCatalog = PingYinUtil.converterToFirstSpell((String)data.get(position-1).get("name")).substring(0, 1).toUpperCase();
				if (catalog.equals(lastCatalog))
				{
					viewholder.tvCatalog.setVisibility(View.GONE);
				} else
				{
					viewholder.tvCatalog.setVisibility(View.VISIBLE);
					viewholder.tvCatalog.setText(catalog);
				}
			}
		} catch(ClassCastException e){
			System.out.println("强制转换失败");
		} catch(NullPointerException e){
			System.out.println("viewholder为空指针");
		} 
	}

		//返回此adapter中的数据,要求接受者判断是否为null
		public List<Map<String,Object> > getListData(){
			return this.data;
		}
		 public void refresh(List<Map<String,Object>> list) {  
		        data = list;  
		        this.notifyDataSetChanged(); 
		 }


	private  class ViewHolder{//优化
		public TextView tvCatalog;
		public ImageView head;
		public TextView name;
		public TextView sign;
	}
	//头像的监听器
	private class MyOnClickListener implements OnClickListener{
		private int position;
		public MyOnClickListener(int posi){
			this.position=posi;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Map<String,Object> map = data.get(position);
			String type = (String)map.get("type");
			if(type.equals("contact")){
				Intent intent = new Intent(context,Contacts.class);
				intent.putExtra(ShardPre.currentChatNumber,(String) map.get("number"));
				context.startActivity(intent);
			}else if(type.equals("discussion")){//讨论组
				Intent intent = new Intent(context,Group.class);
				intent.putExtra(ShardPre.currentChatNumber,(String)map.get("name"));
				context.startActivity(intent);
				
			}else if(type.equals("group")){
				Intent intent = new Intent(context,MyGroup.class);
				intent.putExtra(ShardPre.currentChatNumber,(String)map.get("name"));
				context.startActivity(intent);
			}
		}
	}
}
