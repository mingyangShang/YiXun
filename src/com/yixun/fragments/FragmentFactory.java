package com.yixun.fragments;

import android.app.Fragment;

import com.yixun.constants.Constants;

public class FragmentFactory {
	//获得常量类的单例
//	private static final Constants constants = Constants.getInstance();
	
	/*//根据标记获得一个标题的fragment对象
	public static Fragment newTitleFragment(int flag){
		Fragment fragment = null;
		final int i = 1;
		switch(flag){
		case Constants.FLAG_CHAT:
			fragment =  new ChatTitleFragment();
			break;
			
		case Constants.FLAG_NOTICE:
			fragment = new NoticeTitleFragment();
			break;
			
		case Constants.FLAG_CONTACTS:
			fragment = new ContactsTitleFragment();
			break;
			
		case Constants.FLAG_SETTING:
			fragment = new SettingTitleFragment();
			break;
			
		case Constants.FLAG_NEW:
			fragment = new NewTitleFragment();
			break;
			
		case Constants.FLAG_CONTACTS_SELECT:
			fragment = new SelectContactTitleFragment();
			break;
		default:
			System.out.println("fragment的标志位错误");
		}
		
		return fragment;
	}*/
	
	//根据标记获得一个内容fragment对象
	public static Fragment newContentFragment(int flag){
		Fragment fragment = null;
		switch(flag){
		case Constants.FLAG_CHAT:
			fragment = new ChatContentFragment();
			break;
		case Constants.FLAG_NOTICE:
			fragment = new NoticeContentFragment();
			break;
		case Constants.FLAG_CONTACTS:
			fragment = new ContactsContentFragment();
			break;
		case Constants.FLAG_SETTING:
			fragment = new SettingContentFragment();
			break;
		case Constants.FLAG_NEW:
			fragment = new NewInformFragment();
			break;
			
		case Constants.FLAG_CONTACTS_SELECT:
			fragment = new SelectContactFragment();
			break;
			
		}
		
		return fragment;
	}

}
