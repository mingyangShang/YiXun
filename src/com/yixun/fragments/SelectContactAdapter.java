package com.yixun.fragments;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewDebug.FlagToString;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SectionIndexer;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.myview.PingYinUtil;

public class SelectContactAdapter extends BaseAdapter implements OnClickListener,SectionIndexer{
	
	private Context context;//上下文，与activity相联系
	private List<Map<String,Object> > data;//盛放数据
	private ViewHolder viewholder = null;//做优化使用
	private List<String> names = null;//存放选中的人的名字
	private int flag;
	public SelectContactAdapter(Context mContext,List<Map<String,Object> > mData,int flag){
		this.context = mContext;
		this.data = mData;
		this.flag = flag;
		names = new ArrayList<String>();		
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
			arg1 = LayoutInflater.from(this.context).inflate(R.layout.select_contact_item,null);
			viewholder = new ViewHolder();
			viewholder.tvCatalog = (TextView)arg1.findViewById(R.id.contactitem_catalog_select);
			viewholder.head = (ImageView)arg1.findViewById(R.id.head_contact_select);
			viewholder.name = (TextView)arg1.findViewById(R.id.name_contact_select);
			viewholder.sign = (TextView)arg1.findViewById(R.id.message_contact_select);
			viewholder.check = (CheckBox)arg1.findViewById(R.id.check_select);
			viewholder.check.setOnClickListener(new MyOnClickListener(arg0));
			viewholder.head.setOnClickListener(this);
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
			viewholder.tvCatalog.setText(PingYinUtil.converterToFirstSpell((String)map.get("name")).substring(0, 1).toUpperCase());
			viewholder.head.setImageBitmap(((SoftReference<Bitmap>) map.get("head")).get());
			viewholder.name.setText((CharSequence) map.get("name"));
			viewholder.sign.setText((CharSequence) map.get("sign"));
			if(names.contains(map.get("name"))==true){
				viewholder.check.setChecked(true);
			}else{
				viewholder.check.setChecked(false);
			}String catalog = null;
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


	private  class ViewHolder{//优化
		public TextView tvCatalog;
		public ImageView head;
		public TextView name;
		public TextView sign;
		public CheckBox check;
	}
	 public void refresh(List<Map<String,Object>> list) {  
	        data = list;  
	        notifyDataSetChanged();  
	    }  
	private class MyOnClickListener implements OnClickListener {
		private int position;
		public MyOnClickListener(int posi){
			this.position = posi;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			CheckBox box = (CheckBox)arg0;
			boolean isChecked = box.isChecked();
			if(flag==Constants.FLAG_CONSTACT){
				String name = (String) data.get(position).get("number");
				if(isChecked==true && names!=null && names.contains(name)==false){
					names.add(name);
				}
				else if(isChecked==false && names!=null && names.contains(name)==true){
					names.remove(name);
				}		
			}else{
			String name = (String) data.get(position).get("name");
			if(isChecked==true && names!=null && names.contains(name)==false){
				names.add(name);
			}
			else if(isChecked==false && names!=null && names.contains(name)==true){
				names.remove(name);
			}		
			}
		}		
	}
	//获得选中的人的数据
	public List<String> getNames(){
		if(names== null){
			names = new ArrayList<String>();
		}
		return names;
	}
	

}

