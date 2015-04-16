package com.yixun.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.yixun.R;

public class NewInformAdapter extends BaseAdapter{
	private Context context = null;
	private List<Map<String,Object> > data = null;
	private ViewHolder viewholder = null;
	private List<String> isSelected = null;//被选中的人的名字
	private List<String> isDeleted = null;//未被选中的人的名字
	private int size ;
	private String name = null;//被选中的偶个人的姓名
	
	//构造函数，绑定数据和activity
	public NewInformAdapter(Context mContext , List<Map<String,Object> > mData){
		this.context = mContext;
		this.data = mData;
		this.size = mData.size();
		isSelected = new ArrayList<String>();
		for(Map<String,Object> map:mData){
			isSelected.add((String)map.get("name"));
		}
		isDeleted = new ArrayList<String>();
		System.out.println("构造函数");
	}
		
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();//返回数据的个数
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);//返回一个arg0位置的map
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(size!=data.size()){
			System.out.println("刷新");
			System.out.println("Size="+size);
			System.out.println("newsize"+data.size());
			for(Map<String,Object> map:data){
				System.out.println(map);
				String s = map.get("name").toString();
				//如果新增加的这个人不在当前的列表中，添加，并将其值为选中状态，在isSelected中添加
				if(isSelected.contains(s)==false && isDeleted.contains(s)==false){
					System.out.println("添加新的接收人"+s);
					isSelected.add(s);
				}
				//如果这个人已经在当前选中的人的集合中存在，什么也不做
				//如果这个人赢在当前未选中的人的集合中存在，则将其值为选中，在isSelected中添加，在isDeleted中删除
				else if(isDeleted.contains(s)==true){
					isSelected.add(s);
					isDeleted.remove(s);
				}
			}
			size = data.size();
		}
		//加载每一条消息的布局文件
		if(arg1 == null){
			arg1 = LayoutInflater.from(this.context).inflate(R.layout.receiver_in_new,null);
			viewholder = new ViewHolder();
			viewholder.name = (TextView)arg1.findViewById(R.id.text_receiver);
			viewholder.isSelected = (CheckBox)arg1.findViewById(R.id.check_receiver);
			arg1.setTag(viewholder);
			} else{
				viewholder = (ViewHolder)arg1.getTag();
			}
		setData(arg0);	//为每一条设置数据
		//为什么必须放在这才行，放在上面if(arg1==null)中就不行？
		viewholder.isSelected.setOnCheckedChangeListener(new MyOnCheckedChangedListener(arg0));//new OnCheckedChangeListener(){
		return arg1;//返回每一条的布局文件
		}		

	   //根据下标给每一项添加数据
		private void setData(int position){
			Map<String,Object> map = data.get(position);
			System.out.println("绘制Newinform的第"+position+"条");
			try{
				viewholder.name.setText((CharSequence) map.get("name"));
				viewholder.isSelected.setChecked((Boolean)map.get("selected"));
			} catch(ClassCastException e){
				System.out.println("强制转换失败");
			} catch(NullPointerException e){
				System.out.println("viewholder为空指针");
			}
		}
		//返回被選中的check波形對應德爾名字
		public List<String> getSelectedNumber(){
			return this.isSelected;
		}
		//但会未被选中的人的名字
		public List<String> getUnSelectedNumber(){
			return this.isDeleted;
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

	private class ViewHolder{				//优化
		public TextView name;
		public CheckBox isSelected;
	}

	private class MyOnCheckedChangedListener implements OnCheckedChangeListener{
		private int position;
		public MyOnCheckedChangedListener(int posi){
			this.position = posi;
		}
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		final String s = data.get(position).get("name").toString();
		arg0.setChecked(arg1);
		if(arg1==true && isSelected.contains(s)==false){				
				isSelected.add(s);	
				System.out.println("增加"+s);
		}
		if(arg1==true && isDeleted.contains(s)==true){
			System.out.println("xuanzhong"+s);
			isDeleted.remove(s);
		}
		if(arg1==false && isSelected.contains(s)==true)
		{
				isSelected.remove(s);	
				System.out.println("删除"+s);
		}
		if(arg1==false && isDeleted.contains(s)==false){
			System.out.println("weixuanzhong"+s);
			isDeleted.add(s);
		}				
	}
	}
}
