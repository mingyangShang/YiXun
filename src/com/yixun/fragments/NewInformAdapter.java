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
	private List<String> isSelected = null;//��ѡ�е��˵�����
	private List<String> isDeleted = null;//δ��ѡ�е��˵�����
	private int size ;
	private String name = null;//��ѡ�е�ż���˵�����
	
	//���캯���������ݺ�activity
	public NewInformAdapter(Context mContext , List<Map<String,Object> > mData){
		this.context = mContext;
		this.data = mData;
		this.size = mData.size();
		isSelected = new ArrayList<String>();
		for(Map<String,Object> map:mData){
			isSelected.add((String)map.get("name"));
		}
		isDeleted = new ArrayList<String>();
		System.out.println("���캯��");
	}
		
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();//�������ݵĸ���
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);//����һ��arg0λ�õ�map
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
			System.out.println("ˢ��");
			System.out.println("Size="+size);
			System.out.println("newsize"+data.size());
			for(Map<String,Object> map:data){
				System.out.println(map);
				String s = map.get("name").toString();
				//��������ӵ�����˲��ڵ�ǰ���б��У���ӣ�������ֵΪѡ��״̬����isSelected�����
				if(isSelected.contains(s)==false && isDeleted.contains(s)==false){
					System.out.println("����µĽ�����"+s);
					isSelected.add(s);
				}
				//���������Ѿ��ڵ�ǰѡ�е��˵ļ����д��ڣ�ʲôҲ����
				//��������Ӯ�ڵ�ǰδѡ�е��˵ļ����д��ڣ�����ֵΪѡ�У���isSelected����ӣ���isDeleted��ɾ��
				else if(isDeleted.contains(s)==true){
					isSelected.add(s);
					isDeleted.remove(s);
				}
			}
			size = data.size();
		}
		//����ÿһ����Ϣ�Ĳ����ļ�
		if(arg1 == null){
			arg1 = LayoutInflater.from(this.context).inflate(R.layout.receiver_in_new,null);
			viewholder = new ViewHolder();
			viewholder.name = (TextView)arg1.findViewById(R.id.text_receiver);
			viewholder.isSelected = (CheckBox)arg1.findViewById(R.id.check_receiver);
			arg1.setTag(viewholder);
			} else{
				viewholder = (ViewHolder)arg1.getTag();
			}
		setData(arg0);	//Ϊÿһ����������
		//Ϊʲô�����������У���������if(arg1==null)�оͲ��У�
		viewholder.isSelected.setOnCheckedChangeListener(new MyOnCheckedChangedListener(arg0));//new OnCheckedChangeListener(){
		return arg1;//����ÿһ���Ĳ����ļ�
		}		

	   //�����±��ÿһ���������
		private void setData(int position){
			Map<String,Object> map = data.get(position);
			System.out.println("����Newinform�ĵ�"+position+"��");
			try{
				viewholder.name.setText((CharSequence) map.get("name"));
				viewholder.isSelected.setChecked((Boolean)map.get("selected"));
			} catch(ClassCastException e){
				System.out.println("ǿ��ת��ʧ��");
			} catch(NullPointerException e){
				System.out.println("viewholderΪ��ָ��");
			}
		}
		//���ر��x�е�check���Ό���� �����
		public List<String> getSelectedNumber(){
			return this.isSelected;
		}
		//����δ��ѡ�е��˵�����
		public List<String> getUnSelectedNumber(){
			return this.isDeleted;
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

	private class ViewHolder{				//�Ż�
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
				System.out.println("����"+s);
		}
		if(arg1==true && isDeleted.contains(s)==true){
			System.out.println("xuanzhong"+s);
			isDeleted.remove(s);
		}
		if(arg1==false && isSelected.contains(s)==true)
		{
				isSelected.remove(s);	
				System.out.println("ɾ��"+s);
		}
		if(arg1==false && isDeleted.contains(s)==false){
			System.out.println("weixuanzhong"+s);
			isDeleted.add(s);
		}				
	}
	}
}
