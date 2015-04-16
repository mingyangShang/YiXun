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
//	private int flag;//�Ƿ���֪ͨ���ǽ���֪ͨ�ı��(0Ϊ���͵ı�ǣ�1Ϊ���յı��)
	//���캯���������ݺ�activity
	public InformAdapter(Context mContext , List<Map<String,Object> > mData){
		this.context = mContext;
		this.data = mData;
	}
	
	//����һ���ж�����
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);//����map,ͨ���������õ�����˵��˺�
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		System.out.println("ִ��informadapter��getView����");
		//����ÿһ����Ϣ�Ĳ����ļ�
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
		viewholder.head.setOnClickListener(this);//Ϊͷ����Ӽ�����
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
			
			viewholder.head.setImageBitmap(((SoftReference<Bitmap>) map.get("head")).get());
			viewholder.name.setText((CharSequence) map.get("name"));
			viewholder.message.setText((CharSequence) map.get("message"));
			viewholder.time.setText((CharSequence) map.get("time"));
		} catch(ClassCastException e){
			System.out.println("ǿ��ת��ʧ��");
		} catch(NullPointerException e){
			System.out.println("viewholderΪ��ָ��");
		} finally{
			System.out.println("����inform���ݺ�������");
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


	private  class ViewHolder{				//�Ż�	
		public ImageView head;
		public TextView name;
		public TextView message;
		public TextView time;
	}
	
	

	
}
