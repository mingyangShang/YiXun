package com.yixun.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SettingUtils {
	public static final String VOICE = "VOICE";	//����
	public static final String VIBRATOR = "VIBRATOR";	// ��
	public static final String RECEIVE = "RECEIVE";	// ����ʱ������Ϣ
	public static final String BYOTHER = "BYOTHER";	// ��֪ͨʱ�Է���������õĻ�����xXi
	/**
	 * ��ȡ����
	 * @param context
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static boolean get(Context context, String name, boolean defaultValue) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean value = prefs.getBoolean(name, defaultValue);
		return value;
	}
	public static String get(Context context, String name, String defaultValue) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String value = prefs.getString(name, defaultValue);
		return value;
	} 
	
	/**
	 * �����û�����
	 * @param context
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean set(Context context, String name, boolean value) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		return editor.commit();	//�ύ
	}

	public static boolean set(Context context, String name, String value) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(name, value);
		return editor.commit();	//�ύ
	}
	/**
	 * ����ÿ�ν���ʱ����Ϣ�������������Ϣ������֪ͨ
	 * ��ͬ���͵����ݸ���shared������Ҳ���ǲ���name������
	 */
	public static boolean saveData(Context context,String name,List<String> data){
		SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		int size=data.size();
		editor.putInt("size",size);//�ȷ�һ��������֪����ν��Ŷ��ٸ�����
		for(int i=0;i<size;++i){
			editor.putString(String.valueOf(i), data.get(i));//һ��һ��������
		}
		editor.commit();
		return true;
	}
	//�����shared�д��������ݣ�Ҳ�Ǹ���shared������Ҳ���ǲ����е���ô���ж�
	public static ArrayList<String> getData(Context context,String name){
		ArrayList<String> list=new ArrayList<String>();
		SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
		int size=sp.getInt("size",0);
		for(int i=0;i<size;++i){
			list.add(sp.getString(String.valueOf(i),""));
		}
		return list;
	}
	/*public static boolean saveSendNoticeData(Context context,String name,List<String> data){
		SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		Bundle bundle=new Bundle();
		bundle.putStringArrayList("data_send_notice", (ArrayList<String>) data);
		int size=data.size();
		editor.putInt("size",size);
		for(int i=0;i<size;++i){
			editor.putString(String.valueOf(i), data.get(i));
		}
		editor.commit();
		return true;
	} 
	public static boolean saveReceiveNoticeData(Context context,String name,List<String> data){
		SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		Bundle bundle=new Bundle();
		int size=data.size();
		editor.putInt("size",size);
		for(int i=0;i<size;++i){
			editor.putString(String.valueOf(i), data.get(i));
		}
		editor.commit();
		return true;
	}*/
}
