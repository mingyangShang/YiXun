package com.yixun.myview;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.yixun.constants.Constants;
import com.yixun.manager.JsonManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.manager.SocketMessage;

public class SocketService extends Service{
	private static UpdateHandler handler;
	public static Socket socket=null;
	public  static BufferedReader br=null;//用来包装socket的输入流
	public static DataOutputStream dos=null;//用来包装socket的输出流
//	public static String action_toService="com.yixun.myview.socketservice";
	public boolean run=true;
	private ConnectivityManager connectivityManager;
    private NetworkInfo info;
	public static final String tag="tag";
	private String myNumber;
	private boolean isConnetcted=false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    	
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d(tag, "网络状态已经改变");
                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();  
                if(info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    Toast.makeText(getApplicationContext(), "当前网络为"+name, 1000).show();
                    run=true;
                    isConnetcted=true;
                    try{
                    if(socket!=null && socket.isClosed()){
                    	socket=new Socket(Constants.IP,Constants.PORT);	
       				 	dos = new DataOutputStream(socket.getOutputStream());
       				 	br = new BufferedReader(new InputStreamReader(  
       		                    socket.getInputStream()));
                    }
                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                } else {
                    Log.d(tag, "没有可用网络");
                    isConnetcted=false;
                    Toast.makeText(getApplicationContext(), "网络断开，请检查网络设置", 1000).show();
                    run=false;
                }
            }
        }
    };
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("service创建");
		myNumber=SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
		//得到撼动了人的实例化对象
		handler=UpdateHandler.getInstance();
		//这侧网络状态的监听
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);
		//进行网络的注册
		new Thread(new Runnable(){
		    String result="";
		    StringBuffer line=null;
		    String txt="";
		    Message msg=null;
		    JSONObject json=null;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					System.out.println("连接上");
				socket=new Socket(Constants.IP,Constants.PORT);	
				 dos = new DataOutputStream(socket.getOutputStream());
				  br = new BufferedReader(new InputStreamReader(  
		                    socket.getInputStream()));
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
				 dos.write(JsonManager.SendLogin(myNumber).getBytes());
				}catch(NullPointerException e){
					e.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}catch(Exception e ){
					e.printStackTrace();
				}
				 new SendBeatThread().start();//启动发送心跳的线程
				
				 while(run){
		            //获取输入流，用于接收服务器端发送来的数据
					/* if(socket.isClosed()){
							socket=new Socket(Constants.IP,Constants.PORT);*/	
//		            line=new StringBuffer();//每次读的时候要把先前的数据清空
		          /*  while((txt=br.readLine())!=null){
		            	line.append(txt);//暂时先不加回车试试
		            }*/
					 try{
						 result=br.readLine();
					 }catch(IOException e){
						 e.printStackTrace();
					 }catch(Exception e){
						 e.printStackTrace();
					 }
		            System.out.println("新消息:"+result);
		            result="";
				 }
			}
		}).start();
		            /*msg=new Message();
					msg.obj=JsonManager.toJson(result);//一个json的对象
					msg.what=1;
					handler.sendMessage(msg);*/
//					br.close();
				 
		            //客户端向服务器端发送数据
//		            dos.write(JsonManager.sendMsg("44444444444","11111111111","我是444","2014-05-14 12:20:23").getBytes(Constants.UTF_8));
//		            dos.writeUTF(JsonManager.sendMsg("44444444444","11111111111","我是444","2014-05-14 12:20:23"));
//		            System.out.println("刷新");
//		            dos.flush();
		            //打印出从服务器端接收到的数据
//		            System.out.println("接收的数据："+dis.readUTF());
		            
		            //不需要继续使用此连接时，记得关闭哦
		            
		           
//		            socket.close();//这个要在最后关闭连接
		          
		            
//					System.out.println("关闭连接");
				
		/*new Thread(new Runnable(){
			String line="",txt="";
			@Override
			public void run() {
				try {
					socket=new Socket(Constants.IP,Constants.PORT);
					br=new BufferedReader(new InputStreamReader(socket.getInputStream(),Constants.UTF_8));
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while(run){
					try{
						//System.out.println("我在运行");
						Message msg=new Message();
						Bundle bundle=new Bundle();
						bundle.putCharSequence("shang", "mingyang");
						msg.setData(bundle);
						msg.what=1;
						handler.sendMessage(msg);
						System.out.println("发送了消息");
						Thread.sleep(2000);
						while ((line = br.readLine()) != null) {  
				            txt += line + "\n";  
				        }  
						if(txt.length()!=0){
						System.out.println(txt);
						}
						//用完后将txt设置为“”
						txt="";
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}).start();	*/
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		System.out.println("startsevive");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("stopservice");
		//释放资源
		try{
		br.close();
		dos.close();
		socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		br=null;
		socket=null;
		handler=null;
		unregisterReceiver(mReceiver);
	}
	private class SendBeatThread extends Thread{
//			private DataOutputStream d=null;
			@Override//定时向服务器发送自己在线的标记
			public void run() {
				synchronized (dos) {
					try{
						while(run){
			            dos.write(JsonManager.SendBeat().getBytes(Constants.UTF_8));
			            System.out.println("发送了心跳");
						Thread.sleep(Constants.FIXED_TIME);
						}
					}catch(InterruptedException e){
						e.printStackTrace();	
					}catch(IOException e){
						System.out.println("未成功发送心跳");
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
	 }
	}
}
	
	

