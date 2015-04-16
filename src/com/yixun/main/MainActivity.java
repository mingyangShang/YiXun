package com.yixun.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yixun.R;
import com.yixun.constants.Constants;
import com.yixun.fragments.ChatContentFragment;
import com.yixun.fragments.ContactsContentFragment;
import com.yixun.fragments.FragmentFactory;
import com.yixun.fragments.NewInformFragment.OnNewNoticeListener;
import com.yixun.fragments.NoticeContentFragment;
import com.yixun.fragments.SelectContactFragment;
import com.yixun.fragments.SettingContentFragment;
import com.yixun.manager.DataManager;
import com.yixun.manager.FileManager;
import com.yixun.manager.MessageManager;
import com.yixun.manager.SettingUtils;
import com.yixun.manager.ShardPre;
import com.yixun.myview.HideBarScrollListener.OnScroll;
import com.yixun.myview.SocketService;

public class MainActivity extends Activity implements OnClickListener,OnNewNoticeListener,OnScroll{
	
	//���浼���Ĳ���view
	private LinearLayout toolbarFrame = null;
	//����Ĳ���view
	private LinearLayout titleLayout = null;
	private FragmentTransaction ft=null;
	//ÿ������������ݶ�Ӧ��fragment�������Ӷ���ʱ��initFragments�����ӣ�ͬʱҲҪ��FragmentFactory�ж�Ӧ�ķ��������
	private ChatContentFragment chatContent = null;
	private NoticeContentFragment noticeContent = null;
	private ContactsContentFragment contactContent = null;
	private SettingContentFragment settingContent = null;
	private SelectContactFragment selectContent = null;
	public boolean ineye=false;
	//ÿ�������������������Ӧ��layout	
	private LinearLayout chatFrame,noticeFrame,contactsFrame,settingFrame,newFrame;
	
	//ÿ���������Ӧ�ĵײ�ͼ�꣬�����죬֪ͨ�ȵ�ͼ��
	private ImageView chatImage,noticeImage,contactsImage,settingImage,newImage;
	//���activity������������
//	private Intent intent = null;
	//����ѡ�е��˵�����
//	private List<String> names = null; 
//	private final String POSITION = "POSITION";
	private final String SELECTED_NAMES = "names";
	private final String BUNDLE_NAMES = "bundle_names";
//	private int height_toolbar1,height_toolbar2;
//	private int w,h;
//	private Bitmap draw_unchat,draw_chat,draw_notice,draw_unnotice,draw_contact,draw_uncontact,draw_set,draw_unset;
	private String currentUserNumber,currentUserKey;
	private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        names = new ArrayList<String>();
        currentUserNumber = SettingUtils.get(getApplicationContext(), ShardPre.currentNumber, "");    
        
//       newFile();
     /*new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
//					new SendNoticeManager(FileManager.toSendNotice("18840850852", "343", 5)).writeSendNotice(5, "2012-12-12 12:34:10", "11111111111,22222222222,33333333333,44444444444,55555555555","���҅�ҾͶ������EFI����EVEvn");
					new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1212", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
					new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1213", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
					new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1214", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
					new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1215", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
					new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1216", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
					newFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread());
			}
        	
        }).start();
        
       
        /*new Thread(new Runnable(){
        	public void run(){
        	 File file = getApplicationContext().getDatabasePath("18840850852");
       		 String name = "18840850852";
//        	 DatabaseManager.deleteTable(getApplicationContext(), name);
       		 DatabaseManager.newTable(getApplicationContext(),name);
       		 DatabaseManager.insertContact(name, getApplicationContext(), "11111111111", "С��", "M", "����С��", "Y", "һ����");
       		 DatabaseManager.insertContact(name, getApplicationContext(), "22222222222", "С��", "M", "����С��", "Y", "һ����");
       		 DatabaseManager.insertContact(name, getApplicationContext(), "33333333333", "СǮ", "M", "����Сǰ", "Y", "һ����");
       		 DatabaseManager.insertContact(name, getApplicationContext(), "44444444444", "С��", "M", "����С��", "Y", "һ����");
       		 DatabaseManager.insertContact(name, getApplicationContext(), "55555555555", "С��", "W", "����С��", "Y", null);
       		 DatabaseManager.insertContact(name, getApplicationContext(), "66666666666", "С��", "W", "����С��", "Y", null);
       		 DatabaseManager.insertContact(name, getApplicationContext(), "77777777777", "С֣", "W", "����С֣", "Y", null);
       		 DatabaseManager.insertContact(name, getApplicationContext(), "88888888888", "С��", "W", "����С��", "Y", null);

//        		DatabaseManager.renameTable(getApplicationContext(), name, "1999999");
//        		DatabaseManager.insertContact("18840850852", getApplicationContext(), "11111111111", "С��", "M", "����С��", "Y", "һ����");
        		
//        		DatabaseManager.deleteTable( getApplicationContext(),name);
//           		DatabaseManager.deleteDatabase("18840850852", getApplicationContext());
        		
//        		DatabaseManager.deleteInDiscussion("18840850852", getApplicationContext(), "һ����");
        		List<Contact>  list = DatabaseManager.queryInDiscussion(name, getApplicationContext(), "һ����");
        		for(Contact map:list){
        			Log.i("number",map.number);
            		Log.i("name",map.name);
            		Log.i("sex",map.sex);
            		Log.i("words",map.words);
            		Log.i("is",map.isContact);
            		if(map.discussions != null){
            			Log.i("dis",map.discussions);
            		}
            		else{
            			Log.i("dis","�����ڵ���");
            		}
        		}
        		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
       		    FileManager.newConImg("12121212121", bmp, "121.png");
//        		DatabaseManager.deleteContact(name, getApplicationContext(), "11111111111");
//        		  Resources res = getResources();
        	}
        }).start();*/
        
        /*File file = getApplicationContext().getDatabasePath("18840850852");
  		 String name = "18840850852";*/
//   	 DatabaseManager.deleteTable(getApplicationContext(), name);
  		/* DatabaseManager.newTable(getApplicationContext(),name);
  		 DatabaseManager.insertContact(name, getApplicationContext(), "11111111111", "С��", "M", "����С��", "Y", "һ����");
  		 DatabaseManager.insertContact(name, getApplicationContext(), "22222222222", "С��", "M", "����С��", "Y", "һ����");
  		 DatabaseManager.insertContact(name, getApplicationContext(), "33333333333", "СǮ", "M", "����Сǰ", "Y", "һ����");
  		 DatabaseManager.insertContact(name, getApplicationContext(), "44444444444", "С��", "M", "����С��", "Y", "һ����");
  		 DatabaseManager.insertContact(name, getApplicationContext(), "55555555555", "С��", "W", "����С��", "Y", null);
  		 DatabaseManager.insertContact(name, getApplicationContext(), "66666666666", "С��", "W", "����С��", "Y", null);
  		 DatabaseManager.insertContact(name, getApplicationContext(), "77777777777", "С֣", "W", "����С֣", "Y", null);
  		 DatabaseManager.insertContact(name, getApplicationContext(), "88888888888", "С��", "W", "����С��", "Y", null);*/
     /*  try{
		new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1212", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
		new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1213", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
		new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1214", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
		new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1215", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
		new ReceivedNoticeManager(FileManager.toReceiveNotice("18840850852")).write("11111111111", "2012-12-12 12:21:32", "1", "1216", "���ͷ���ɳ����˵�����ڽɷѷ���i��ֽ�Ϊif����ʱ�����ܼ���ƴ���V�����꼶������");
       }catch(Exception e){
    	   e.printStackTrace();
       }
		try {
			new SendNoticeManager(FileManager.toSendNotice("18840850852", "343", 5)).writeSendNotice(5, "2014-04-14 10:10:12", "11111111111,33333333333,22222222222,44444444444,55555555555", "123���Ƽ���˳���ݽ��ָ���ʥ���ں󸶿�");
			new SendNoticeManager(FileManager.toSendNotice("18840850852", "343", 5)).writeReply("33333333333", "2014-04-15 12:10:12", "����ֻ����");
			new SendNoticeManager(FileManager.toSendNotice("18840850852", "343", 5)).writeReply("44444444444", "2014-04-15 12:10:12", "456�ܺ͸�����AMD�ڷ�����ɶ���ط���ô���");
			new SendNoticeManager(FileManager.toSendNotice("18840850852", "343", 5)).writeReply("11111111111", "2014-04-15 12:10:12", "����ֻ����");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newFile();*/
        init();//��ʼ��fragment�Լ�view�����ó�ʼ��ʾ���
        /*w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        toolbarFrame.measure(w, h);
        height_toolbar1 = toolbarFrame.getMeasuredHeight();*/
//        height_toolbar2 = newFrame.getMeasuredHeight();
    }

    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ineye=true;
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//����������ݺ�֪ͨ�����ݱ��浽shared��ȥ
		new Thread(new Runnable(){
			public void run(){
				//��������ݣ���Ҫ�����Ǹ��˶Ի�����������ĶԻ�
				ArrayList<String> numbers=new ArrayList<String>();
				for(Map<String,Object> map:DataManager.data_chat){
					if((Boolean)map.get("person")==true){//����֮��ĶԻ�
						numbers.add("0"+map.get("number"));
					}else{//������ĶԻ�
						numbers.add("1"+map.get("name"));
					}
				}
				SettingUtils.saveData(getApplicationContext(), Constants.SHARED_KEY_CHAT, numbers);
				//����֪ͨ������
				numbers=new ArrayList<String>();
				//numbers.clear();
				if(DataManager.data_send!=null){
				for(Map<String,Object> map:DataManager.data_send){
					numbers.add((String)map.get("id"));//����õ������ļ���
				}
				SettingUtils.saveData(getApplicationContext(), Constants.SHARED_KEY_SEND_NOTICE, numbers);
				}
				
				if(DataManager.data_receive!=null){
				//����֪ͨ������
				numbers=new ArrayList<String>();
				for(Map<String,Object> map:DataManager.data_receive){
					numbers.add((String)map.get("id"));//����õ�����֪ͨ��id����Ϊ���յ�֪ͨʼ����һ���ļ���
				}
				SettingUtils.saveData(getApplicationContext(), Constants.SHARED_KEY_RECEIVE_NOTICE, numbers);
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("����");
		//���ļ��б�����һ�ν���ʱ������
		super.onDestroy();
		ineye=false;
		//ֹͣ�������
		stopService(new Intent(this,SocketService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main,menu);
		return true;
	}
	
	@Override//����view��onClick����
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
    	case R.id.layout_chat:
    		onClickChat();
    		break;
    	case R.id.notice_layout:
    		onClickNotice();
    		break;
    	case R.id.layout_contacts:
    		onClickContacts();
    		break;
    	case R.id.layout_setting:
    		onClickSetting();
    		break;
    	case R.id.layout_new:
    		onClickNew();
    		break;
    	   	}
	}

	@Override
	public void onIdle() {
		// TODO Auto-generated method stub
//		toolbarFrame.setVisibility(View.VISIBLE);
//		newFrame.setVisibility(View.VISIBLE);
		/*System.out.println("idle:"+"height1��"+height_toolbar1+"height2:"+height_toolbar2);

		
        
        Animation animation2 = new TranslateAnimation(0, 0, 0, -height_toolbar2);//��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣  
        animation2.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��  
        animation2.setDuration(300);
        
      
        newFrame.startAnimation(animation2);*/
	/*	Animation animation1 = new TranslateAnimation(0, 0, height_toolbar1, 0);//��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣  
        animation1.setFillAfter(true);
        animation1.setDuration(1000); 
        toolbarFrame.startAnimation(animation1);*/
	}

	@Override
	public void onFling() {
		// TODO Auto-generated method stub
		/*System.out.println("fling:"+"height1��"+height_toolbar1+"height2:"+height_toolbar2);
		Animation animation1 = new TranslateAnimation(0, 0, 0,height_toolbar1);//��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣  
        animation1.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��  
        animation1.setDuration(300); 
        
        Animation animation2 = new TranslateAnimation(0, 0, 0, height_toolbar2);//��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣  
        animation2.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��  
        animation2.setDuration(300);
        
        toolbarFrame.startAnimation(animation1);
        newFrame.startAnimation(animation2);*/
		/*Animation animation1 = new TranslateAnimation(0, 0, 0, height_toolbar1);//��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣  
        animation1.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��  
        animation1.setDuration(1000); 
        toolbarFrame.startAnimation(animation1);*/
	}

	@Override
	public void onScroll() {
		// TODO Auto-generated method stub
	/*	Animation animation1 = new TranslateAnimation(0, 0, 0, height_toolbar1);//��Ȼ����Ƚϼ�ֻ࣬��һ�д��롣  
        animation1.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��  
        animation1.setDuration(1000); 
        toolbarFrame.startAnimation(animation1);*/
        }

	/*@Override
	public void onOkSelect() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,NewInformActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(SELECTED_NAMES,(ArrayList<String>) selectContent.getNames());
		intent.putExtra(BUNDLE_NAMES, bundle);
		this.startActivity(intent);
	}

	@Override
	public void onCancelSelect() {
		// TODO Auto-generated method stub
		onClickContacts();
	}*/

	//��ʼ��Fragment
    private void initFragments(){
    	//��ʼ���������
//    	chatTitle = (ChatTitleFragment) FragmentFactory.newTitleFragment(Constants.FLAG_CHAT);
    	/*noticeTitle = (NoticeTitleFragment) FragmentFactory.newTitleFragment(Constants.FLAG_NOTICE);
    	contactsTitle = (ContactsTitleFragment) FragmentFactory.newTitleFragment(Constants.FLAG_CONTACTS);
    	settingTitle = (SettingTitleFragment) FragmentFactory.newTitleFragment(Constants.FLAG_SETTING);
    	newTitle = (NewTitleFragment) FragmentFactory.newTitleFragment(Constants.FLAG_NEW);*/
//    	newTitle = (NewTitleFragment) FragmentFactory.newTitleFragment(Constants.FLAG_NEW);
    	//��ʼ�����ݶ���
    	chatContent = (ChatContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_CHAT);
    	noticeContent = (NoticeContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_NOTICE);
    	contactContent = (ContactsContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_CONTACTS);
    	settingContent = (SettingContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_SETTING);
    	selectContent = (SelectContactFragment)FragmentFactory.newContentFragment(Constants.FLAG_CONTACTS_SELECT);
    	System.out.println("��ʼ��Fragment���");
    }
    
    @Override
	public void onNewNoticeSend() {
		// TODO Auto-generated method stub
		System.out.println("��ͯ�ӷ���");
	}



	//��ʼ��view�ؼ��Ͳ���
    private void initViews(){
    	//��ʼ�������ļ�
    	toolbarFrame = (LinearLayout)findViewById(R.id.frameLayout1);
//    	titleLayout = (LinearLayout)findViewById(R.id.main_title);
//    	newFrame = (RelativeLayout)findViewById(R.id.frame_new);
    	
    	chatFrame = (LinearLayout)findViewById(R.id.layout_chat);
    	noticeFrame = (LinearLayout)findViewById(R.id.notice_layout);
    	contactsFrame = (LinearLayout)findViewById(R.id.layout_contacts);
    	settingFrame = (LinearLayout)findViewById(R.id.layout_setting);
    	newFrame = (LinearLayout)findViewById(R.id.layout_new);
    	
    	//Ϊ�����������ü�����
    	chatFrame.setOnClickListener(this);
    	noticeFrame.setOnClickListener(this);
    	contactsFrame.setOnClickListener(this);
    	settingFrame.setOnClickListener(this);
    	newFrame.setOnClickListener(this);

    	//��ʼ��imageview
    	chatImage = (ImageView)findViewById(R.id.image_chat);
    	noticeImage = (ImageView)findViewById(R.id.image_notice);
    	contactsImage = (ImageView)findViewById(R.id.image_constacts);
    	settingImage = (ImageView)findViewById(R.id.image_setting);
    	newImage = (ImageView)findViewById(R.id.add_btn);//д��֪ͨ�İ�ť
    	
    	//�������
    	System.out.println("��ʼ��view����");
    	
    }
    
    //��ʼ����ʼ����ʾ����
    private void init(){
    	
    	initFragments();//��ʼ��Fragment
    	initViews();//��ʼ��view�ؼ��Ͳ���
    	getActionBar().hide();//����actionbar
    	
    	FragmentTransaction ft = getFragmentManager().beginTransaction();//�õ�һ���ܲ���fragment������
    	//��֪ͨ�������ó�һ�������ȿ�ʼ�Ľ���
    	if(chatContent==null){
    	chatContent = (ChatContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_CHAT);
    	}
    	ft.replace(R.id.frame_content,chatContent);
    	ft.commit();    
    	
    }
    
    //���������󴥷��������
    private void onClickChat(){    	
    	FragmentTransaction ft = getFragmentManager().beginTransaction();//�õ�һ���ܲ���fragment������
    	if(chatContent==null){
    	chatContent = (ChatContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_CHAT);
    	}
    	ft.replace(R.id.frame_content, chatContent);
    	ft.commit();
//    	chatImage.setImageResource(R.drawable.message_selected);
    	chatImage.setImageBitmap(minBitmap(R.drawable.message_selected));//(draw_chat);
    	noticeImage.setImageBitmap(minBitmap(R.drawable.notice_unselected));
    	contactsImage.setImageBitmap(minBitmap(R.drawable.contact_unselected));
    	settingImage.setImageBitmap(minBitmap(R.drawable.setting_unselected));//(draw_unset);
    }
    //�����֪ͨ�󴥷��������
    private void onClickNotice(){
    	FragmentTransaction ft = getFragmentManager().beginTransaction();//�õ�һ���ܲ���fragment������
    	if(noticeContent==null){
    	noticeContent = (NoticeContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_NOTICE);
    	}
    	ft.replace(R.id.frame_content, noticeContent);
    	ft.commit();
//    	chatImage.setImageResource(R.drawable.message_unselected);
    	chatImage.setImageBitmap(minBitmap(R.drawable.message_unselected));//(draw_chat);
    	noticeImage.setImageBitmap(minBitmap(R.drawable.notice_selected));
    	contactsImage.setImageBitmap(minBitmap(R.drawable.contact_unselected));
    	settingImage.setImageBitmap(minBitmap(R.drawable.setting_unselected));
    }
    //�������ϵ�˺󴥷��������
    private void onClickContacts(){   	
    	FragmentTransaction ft = getFragmentManager().beginTransaction();//�õ�һ���ܲ���fragment������
    	if(contactContent==null){
    	contactContent = (ContactsContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_CONTACTS);
    	}    
    	ft.replace(R.id.frame_content, contactContent);
    	ft.commit();
    	chatImage.setImageBitmap(minBitmap(R.drawable.message_unselected));//(draw_chat);
    	noticeImage.setImageBitmap(minBitmap(R.drawable.notice_unselected));
    	contactsImage.setImageBitmap(minBitmap(R.drawable.contact_selected));
    	settingImage.setImageBitmap(minBitmap(R.drawable.setting_unselected));
    }
    //��������ú󴥷��������
    private void onClickSetting(){
    	System.out.println("���������ú���");
    	
    	FragmentTransaction ft = getFragmentManager().beginTransaction();//�õ�һ���ܲ���fragment������
    	if(settingContent==null){
    	settingContent = (SettingContentFragment) FragmentFactory.newContentFragment(Constants.FLAG_SETTING);
    	}
    	ft.replace(R.id.frame_content, settingContent);
    	ft.commit();
    	chatImage.setImageBitmap(minBitmap(R.drawable.message_unselected));//(draw_chat);
    	noticeImage.setImageBitmap(minBitmap(R.drawable.notice_unselected));
    	contactsImage.setImageBitmap(minBitmap(R.drawable.contact_unselected));
    	settingImage.setImageBitmap(minBitmap(R.drawable.setting_selected));
    }
    
    //���������֪ͨ��ť
    private void onClickNew(){ 	
    	FragmentTransaction ft = getFragmentManager().beginTransaction();//�õ�һ���ܲ���fragment������
    	if(selectContent==null){
    	selectContent = (SelectContactFragment)FragmentFactory.newContentFragment(Constants.FLAG_CONTACTS_SELECT);
    	}
    	ft.replace(R.id.frame_content, selectContent);
    	ft.commit();
    }
  //���ص����е�Fragment
  /*	public void hideFragments(FragmentTransaction f){
  		//ֻҪ����null�Ļ�������fragment
  		if(chatTitle!=null)f.hide(chatTitle);
  		if(noticeTitle!=null)f.hide(noticeTitle);
  		if(newTitle!=null)f.hide(newTitle);
  		if(contactsTitle!=null)f.hide(contactsTitle);
  		if(settingTitle!=null)f.hide(settingTitle);
  		if(chatContent!=null)f.hide(chatContent);
  		if(noticeContent!=null)f.hide(noticeContent);
  		if(newContent!=null)f.hide(newContent);
  		if(contactContent!=null)f.hide(contactContent);
  		if(settingContent!=null)f.hide(settingContent);
  		f.commit();	
  	}*/
    //�����ļ�
    public void newFile(){
    	try{
			String myNumber = "18840850852";
			FileManager.newContact(myNumber,"11111111111");
			FileManager.newContact(myNumber, "22222222222");
			FileManager.newContact(myNumber, "33333333333");
			FileManager.newContact(myNumber, "44444444444");
			FileManager.newContact(myNumber, "55555555555");
			FileManager.newContact(myNumber,"66666666666");
			FileManager.newContact(myNumber, "77777777777");
			FileManager.newContact(myNumber, "88888888888");
//			FileManager.newDiscussion(myNumber,"taolunzu1");
//			FileManager.newGroup(myNumber,"group1");
		/*	FileManager.newSendNotice(myNumber,"1221",3);
			FileManager.newReceiveNotice(myNumber);
//			FileManager.newDisImg(myNumber, "2.ipg");
//			FileManager.newConImg(myNumber, "12.jpg");
			FileManager.addMemberToGroup(myNumber, "group1", "11111111111",true);
			FileManager.addMemberToGroup(myNumber, "group2", "11111111111",true);
			FileManager.addMemberToGroup(myNumber, "group1", "11111121111",true);
			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
   		    FileManager.newConImg(myNumber, bmp, "121.jpg");
			*/MessageManager mm = null;
			String[] numbers = new String[]{
					"11111111111","22222222222","33333333333","44444444444","55555555555","66666666666",
					"77777777777","88888888888"
			};
			for(int i=0;i<numbers.length;++i){
			try{
				 mm = new MessageManager(FileManager.toContactRecord(myNumber, numbers[i]));
			}catch(IOException e){
				e.printStackTrace();
			}
			mm.write(myNumber, "2014-04-10 12:21:12", "0", "���Ǳ��˷��ĵ�һ��");
			mm.write(myNumber, "2014-04-10 12:22:12", "0", "���Ǳ��˷��ĵڶ���");
			mm.write("18840850852", "2014-04-11 13:21:12","0","�����Լ����ĵ�����");
			mm.write(myNumber,"2014-04-11 14:10:40","0","���Ǳ��˷��ĵ�����");
			}
			/*ArrayList<Map<String,String> > list1 = mm.getDataFromBack(10);
			for(Map<String,String > map:list1){
				System.out.println(map.get("head"));
				System.out.println(map.get("content"));
			}
			System.out.println("δ��:" + mm.getNoSaw());*/
			/*ArrayList<String> list = FileManager.getMembersFromGroup(myNumber, "group1");
			list.add("12121212121");
			FileManager.addMembersToGroup(myNumber, "group1", list,true);
			ArrayList<String> lists = FileManager.getMembersFromGroup(myNumber, "group1");

			for(String s:lists){
				System.out.println(s);
			}
			FileManager.deleteMembersFromGroup(myNumber, "group1", lists);*/
			
//			FileManager.deleteGroup(myNumber, "group1");
//			FileManager.deleteContact(myNumber, "12341234123");
//			FileManager.deleteDiscussion(myNumber, "taolunzu1");
		}catch(Exception e){
			e.printStackTrace();
		}
    	
    }
    //ѹ��ͼƬ�Ĵ�С
    private Bitmap minBitmap(int id){
    	BitmapFactory.Options opts = new BitmapFactory.Options();
    	opts.inSampleSize = 3; //�����ֵѹ���ı�����2��������������ֵԽС��ѹ����ԽС��ͼƬԽ����
    	 Bitmap bitmap = null;
    	//����ԭͼ����֮���bitmap����
    	bitmap = BitmapFactory.decodeResource(getResources(),id, opts);
    	return bitmap;
    }
    
   
    
}
