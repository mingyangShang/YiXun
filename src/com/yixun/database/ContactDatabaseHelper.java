package com.yixun.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDatabaseHelper extends SQLiteOpenHelper{
	private static int VERSION = 1;//���ݿ�İ汾��
	public static String DATABASE_NAME = "yixun_database";//���ݿ������
	//���캯����ĳ���û����˺ž�������û���ϵ�����ݿ������
	public ContactDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);	
		// TODO Auto-generated constructor stub
	}
	public ContactDatabaseHelper(Context context){
		this(context,DATABASE_NAME,null,VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		System.out.println("�������ݿ�");
	}

	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		System.out.println("�����ݿ�");
	}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println("�������ݿ�");
		VERSION = arg2;//�������ݿ�İ汾��
	}

	

}