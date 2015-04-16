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
	
	private Context context;//�����ģ���activity����ϵ
	private List<Map<String,Object> > data;//ʢ������
	private ViewHolder viewholder = null;//���Ż�ʹ��
	private boolean flag;//checkbox�Ƿ���ʾ�ı��
	public List<String> numbers;//��ѡ�е��˵��˺ŵļ���

	public MyGroupAdapter(Context mContext,List<Map<String,Object>> mData,boolean fl){
		this.context = mContext;
		this.data = mData;
		this.flag=fl;
		numbers = new ArrayList<String>();
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
		//Ϊͷ����Ӽ�����
		setData(arg0);	//Ϊÿһ����������
		return arg1;	//����ÿһ���Ĳ����ļ�
	}
	
	
	//�����±��ÿһ���������
	private void setData(int position){
		Map<String,Object> map = data.get(position);
		System.out.println("message�ĵ�"+position+"��");
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
			System.out.println("ǿ��ת��ʧ��");
		} catch(NullPointerException e){
			System.out.println("viewholderΪ��ָ��");
		} finally{
			System.out.println("����message���ݺ�������");
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
		public ImageView head;
		public TextView name;
		public TextView sign;
		public CheckBox check;
		public TextView tvCatalog;
 
	}
	//ͷ��ļ�����
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
	//checkbox�ļ�����
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
	//���ñ��
	public void setFlag(boolean f){
		this.flag=f;
		this.notifyDataSetChanged();//�Զ�����
	}
	
}
