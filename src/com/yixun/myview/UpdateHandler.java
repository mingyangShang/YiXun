package com.yixun.myview;

import java.util.ArrayList;
import java.util.List;

import com.yixun.fragments.ChatContentFragment;

import android.os.Handler;
import android.os.Message;

public class UpdateHandler extends Handler{
	private static List<Updateable> updates=new ArrayList<Updateable>();;
	private static UpdateHandler handler;
/*	private UpdateHandler(){
		updates= 
	}*/
	public static UpdateHandler getInstance(){
		handler=new UpdateHandler();
		return handler;
	}
	public static void registerUpdater(Updateable update){
		updates.add(update);
	}
	public static void unRegisterUpdater(Updateable update){
		updates.remove(update);
	}
	
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		System.out.println("开始处理消息,现在有"+updates.size()+"个注册者");
		//更新数据
		for(Updateable update:updates){
			update.update(msg);
		}
		/*switch(msg.what){
		case 1:
			for(Updateable update:updates){
				if(update instanceof ChatContentFragment){
					
				}
			}
			break;
		}*/
	}		
}


