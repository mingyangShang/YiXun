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
	private static int VERSION = 1;//数据库的版本号
	public static String DATABASE_NAME = "yixun_database";//数据库的名字
	//构造函数，某个用户的账号就是这个用户联系人数据库的名字
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
		
		System.out.println("创建数据库");
	}

	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		System.out.println("打开数据库");
	}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		System.out.println("更新数据库");
		VERSION = arg2;//更新数据库的版本号
	}

	

}