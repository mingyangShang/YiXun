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
	
	private Context context;//�����ģ���activity����ϵ
	private List<Map<String,Object> > data;//ʢ������
	private ViewHolder viewholder = null;//���Ż�ʹ��
	
	public MessageAdapter(Context mContext,List<Map<String,Object> > mData){
		this.context = mContext;
		this.data = mData;
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
		
		//Ϊͷ����Ӽ�����
		setData(arg0);	//Ϊÿһ����������
		return arg1;	//����ÿһ���Ĳ����ļ�
		
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
