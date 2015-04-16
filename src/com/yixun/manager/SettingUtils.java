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
	public static final String VOICE = "VOICE";	//声音
	public static final String VIBRATOR = "VIBRATOR";	// 震动
	public static final String RECEIVE = "RECEIVE";	// 离线时接收消息
	public static final String BYOTHER = "BYOTHER";	// 发通知时对方无网络可用的话发短xXi
	/**
	 * 获取配置
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
	 * 保存用户配置
	 * @param context
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean set(Context context, String name, boolean value) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		return editor.commit();	//提交
	}

	public static boolean set(Context context, String name, String value) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(name, value);
		return editor.commit();	//提交
	}
	/**
	 * 保存每次进入时的信息，比如聊天的信息，哪条通知
	 * 不同类型的数据根据shared的名字也就是参数name才区分
	 */
	public static boolean saveData(Context context,String name,List<String> data){
		SharedPreferences sp=context.getSharedPreferences(name,Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		int size=data.size();
		editor.putInt("size",size);//先放一个变量，知道这次将放多少个数据
		for(int i=0;i<size;++i){
			editor.putString(String.valueOf(i), data.get(i));//一个一个地数据
		}
		editor.commit();
		return true;
	}
	//获得在shared中存数的数据，也是根据shared的名字也就是参数中的那么来判断
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
