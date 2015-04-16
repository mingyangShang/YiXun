package com.yixun.fragments;

import android.app.Fragment;

import com.yixun.constants.Constants;

public class FragmentFactory {
	//��ó�����ĵ���
//	private static final Constants constants = Constants.getInstance();
	
	/*//���ݱ�ǻ��һ�������fragment����
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
			System.out.println("fragment�ı�־λ����");
		}
		
		return fragment;
	}*/
	
	//���ݱ�ǻ��һ������fragment����
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
