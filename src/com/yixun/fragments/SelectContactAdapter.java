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
	
	private Context context;//�����ģ���activity����ϵ
	private List<Map<String,Object> > data;//ʢ������
	private ViewHolder viewholder = null;//���Ż�ʹ��
	private List<String> names = null;//���ѡ�е��˵�����
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
		//����listview����Ŀ������data�Ĵ�С
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);//����һ��map����,ͨ���������õ�����˵��˺�
	}

	@Override//��ʱ����֪��ʲô��˼
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		//����ÿһ����Ϣ�Ĳ����ļ�
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
		
		//Ϊͷ����Ӽ�����
		setData(arg0);	//Ϊÿһ����������
		return arg1;	//����ÿһ���Ĳ����ļ�
	}
	
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		System.out.println("ͷ�񱻵��");
		//�����Ӧ�����֣�ֻ�ǲ��ԣ��Ժ�Ҫ��ӹ���
		switch(arg0.getId()){
		case R.id.head_contact:
			break;
		}
	}
	
	//�����±��ÿһ���������
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
			System.out.println("ǿ��ת��ʧ��");
		} catch(NullPointerException e){
			System.out.println("viewholderΪ��ָ��");
		} finally{
		}
	}

		//���ش�adapter�е�����,Ҫ��������ж��Ƿ�Ϊnull
		public List<Map<String,Object> > getListData(){
			return this.data;
		}
		
		//����adapter�е�����
		public boolean setListData(List<Map<String,Object> > listData){
			this.data = listData;//����adapter��Ӧ����������Ϊ����listData
			if(listData == null){
				return false;
			} else{
				return true;
			}
		}


	private  class ViewHolder{//�Ż�
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
	//���ѡ�е��˵�����
	public List<String> getNames(){
		if(names== null){
			names = new ArrayList<String>();
		}
		return names;
	}
	

}

