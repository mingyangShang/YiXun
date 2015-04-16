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
	public  static BufferedReader br=null;//������װsocket��������
	public static DataOutputStream dos=null;//������װsocket�������
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
                Log.d(tag, "����״̬�Ѿ��ı�");
                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();  
                if(info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    Toast.makeText(getApplicationContext(), "��ǰ����Ϊ"+name, 1000).show();
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
                    Log.d(tag, "û�п�������");
                    isConnetcted=false;
                    Toast.makeText(getApplicationContext(), "����Ͽ���������������", 1000).show();
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
		System.out.println("service����");
		myNumber=SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");
		//�õ��������˵�ʵ��������
		handler=UpdateHandler.getInstance();
		//�������״̬�ļ���
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);
		//���������ע��
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
					System.out.println("������");
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
				 new SendBeatThread().start();//���������������߳�
				
				 while(run){
		            //��ȡ�����������ڽ��շ������˷�����������
					/* if(socket.isClosed()){
							socket=new Socket(Constants.IP,Constants.PORT);*/	
//		            line=new StringBuffer();//ÿ�ζ���ʱ��Ҫ����ǰ���������
		          /*  while((txt=br.readLine())!=null){
		            	line.append(txt);//��ʱ�Ȳ��ӻس�����
		            }*/
					 try{
						 result=br.readLine();
					 }catch(IOException e){
						 e.printStackTrace();
					 }catch(Exception e){
						 e.printStackTrace();
					 }
		            System.out.println("����Ϣ:"+result);
		            result="";
				 }
			}
		}).start();
		            /*msg=new Message();
					msg.obj=JsonManager.toJson(result);//һ��json�Ķ���
					msg.what=1;
					handler.sendMessage(msg);*/
//					br.close();
				 
		            //�ͻ�����������˷�������
//		            dos.write(JsonManager.sendMsg("44444444444","11111111111","����444","2014-05-14 12:20:23").getBytes(Constants.UTF_8));
//		            dos.writeUTF(JsonManager.sendMsg("44444444444","11111111111","����444","2014-05-14 12:20:23"));
//		            System.out.println("ˢ��");
//		            dos.flush();
		            //��ӡ���ӷ������˽��յ�������
//		            System.out.println("���յ����ݣ�"+dis.readUTF());
		            
		            //����Ҫ����ʹ�ô�����ʱ���ǵùر�Ŷ
		            
		           
//		            socket.close();//���Ҫ�����ر�����
		          
		            
//					System.out.println("�ر�����");
				
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
						//System.out.println("��������");
						Message msg=new Message();
						Bundle bundle=new Bundle();
						bundle.putCharSequence("shang", "mingyang");
						msg.setData(bundle);
						msg.what=1;
						handler.sendMessage(msg);
						System.out.println("��������Ϣ");
						Thread.sleep(2000);
						while ((line = br.readLine()) != null) {  
				            txt += line + "\n";  
				        }  
						if(txt.length()!=0){
						System.out.println(txt);
						}
						//�����txt����Ϊ����
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
		//�ͷ���Դ
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
			@Override//��ʱ������������Լ����ߵı��
			public void run() {
				synchronized (dos) {
					try{
						while(run){
			            dos.write(JsonManager.SendBeat().getBytes(Constants.UTF_8));
			            System.out.println("����������");
						Thread.sleep(Constants.FIXED_TIME);
						}
					}catch(InterruptedException e){
						e.printStackTrace();	
					}catch(IOException e){
						System.out.println("δ�ɹ���������");
						e.printStackTrace();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
	 }
	}
}
	
	

