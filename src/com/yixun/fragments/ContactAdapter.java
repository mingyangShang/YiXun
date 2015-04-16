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
	
	private Context context;//�����ģ���activity����ϵ
	private List<Map<String,Object> > data;//ʢ������
	private ViewHolder viewholder = null;//���Ż�ʹ��

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
			viewholder.head.setOnClickListener(new MyOnClickListener(arg0));
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
			System.out.println("ǿ��ת��ʧ��");
		} catch(NullPointerException e){
			System.out.println("viewholderΪ��ָ��");
		} 
	}

		//���ش�adapter�е�����,Ҫ��������ж��Ƿ�Ϊnull
		public List<Map<String,Object> > getListData(){
			return this.data;
		}
		 public void refresh(List<Map<String,Object>> list) {  
		        data = list;  
		        this.notifyDataSetChanged(); 
		 }


	private  class ViewHolder{//�Ż�
		public TextView tvCatalog;
		public ImageView head;
		public TextView name;
		public TextView sign;
	}
	//ͷ��ļ�����
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
			}else if(type.equals("discussion")){//������
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
