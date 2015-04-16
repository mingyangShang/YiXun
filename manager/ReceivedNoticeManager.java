package com.yixun.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReceivedNoticeManager {
	
	private static File file = null;
	private static final String SEPER_NUMBER_AND_FLAG= "/*";
	private static final String SEPER_FLAG_AND_TIME = "//";
	private static final String SEPER_TIME_AND_ID= "**";
	private static final String SEPER_ID_AND_CONTENT = "*/";
	
	private static final long MAX_HEAD = 50;
	
	private static final String KEY_HEAD = "head";
	private static final String KEY_CONTENT = "content";
	
	private static final int BITES_OF_NUMBER = 11;//�˺ŵ��ֽڳ���
	private static final int BITES_OF_TIME = 19;//�涨ʱ����ֽڳ���
	private static final int BITES_FRONT_FLA = BITES_OF_NUMBER + SEPER_NUMBER_AND_FLAG.length();//���֮ǰ���ֽڳ��ȣ��������ǹ̶�ֵ13
	private static final int BITES_FRONT_ID = BITES_FRONT_FLA + SEPER_FLAG_AND_TIME.length() + BITES_OF_NUMBER +SEPER_TIME_AND_ID.length();//id֮ǰ���ֽڳ��ȣ�������
	private static final int FLAG_NO_SAW = 0;//δ���ı��
	private static final int FLAG_SAW = 1;//�����ı��
	
	private static final String CHARSET = "UTF-8";//�����ʽ
	
	public static void main(String[] args){
		String path="c:\\Users\\Administrator\\Desktop\\2.txt";
		ReceivedNoticeManager mm = null;
		try{
			 mm = new ReceivedNoticeManager(path);
		}catch(IOException e){
			e.printStackTrace();
		}
		String content="fsdfsjsjns";
		String head = "12/*0//2012-12-32 12:02:21**12*/";
		mm.write(head, content);
		String conten = "ɢ��";
		String hea = "12/*0//2012-18-17 12:21:11**21*/";
		mm.write(hea, conten);
		mm.write("12121211111", "2012-21-21 12:21:12", "0", "13",conten);
		ArrayList<Map<String,String> > list1 = mm.getDataFromBack(10);
	/*	for(Map<String,String > map:list1){
			System.out.println(map.get(KEY_HEAD));
			System.out.println(mm.getNumber(map.get(KEY_HEAD)));
			System.out.println(mm.getFlag(map.get(KEY_HEAD)));
			System.out.println(mm.getTime(map.get(KEY_HEAD)));

			System.out.println(mm.getId(map.get(KEY_HEAD)));

			System.out.println(map.get(KEY_CONTENT));
		}*/
		System.out.println("δ��:" + mm.getNoSaw());
		if(mm.getNoticeById("12")==null){
			System.out.println("��");
			System.exit(0);
		}
		System.out.println(mm.getNoticeById("12").get(KEY_CONTENT));
	}
	
	//���ֹ��캯��
	public ReceivedNoticeManager(File f){
		file = f;
	}
	public ReceivedNoticeManager(String pathname) throws IOException{
		file = new File(pathname);
	}
	public ReceivedNoticeManager(String parent,String child){
		file = new File(parent,child);
	}
	public ReceivedNoticeManager(File parent,String child){
		file = new File(parent,child);
	}
	
			//��ÿһ�ж����˺�
			public static String getNumber(String inf){
				String number = "";
				int position = inf.indexOf(ReceivedNoticeManager.SEPER_NUMBER_AND_FLAG);
				number = (position == -1)?null:inf.substring(0, position);
				return number;
			}
			//��ÿ�������ж����Ƿ��Ѷ��ı��
			public static int getFlag(String inf){
				String flag = "";
				int position1 = inf.indexOf(ReceivedNoticeManager.SEPER_NUMBER_AND_FLAG)+ReceivedNoticeManager.SEPER_NUMBER_AND_FLAG.length();
				int position2 = inf.indexOf(ReceivedNoticeManager.SEPER_FLAG_AND_TIME);
				flag = (position1 ==ReceivedNoticeManager.SEPER_NUMBER_AND_FLAG.length()-1 || position2 == -1)?null:inf.substring(position1, position2);
		
				int i = 0;
				try{
					 i = Integer.parseInt(flag);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				return i;
				
				
				
			}
			//��ÿ�������ж�������
			public static String getTime(String inf){
				String time = "";
				int position1=inf.indexOf(ReceivedNoticeManager.SEPER_FLAG_AND_TIME)+ReceivedNoticeManager.SEPER_FLAG_AND_TIME.length();
				int position2 = inf.indexOf(ReceivedNoticeManager.SEPER_TIME_AND_ID);
				

				time = (position1 ==ReceivedNoticeManager.SEPER_FLAG_AND_TIME.length()-1 || position2 ==-1)?null:inf.substring(position1, position2);
				return time;
			}
			//��ÿ�������ж���֪ͨ��id��
			private static String getId(String inf){
				String id= "";
				int position1 = inf.indexOf(ReceivedNoticeManager.SEPER_TIME_AND_ID)+ReceivedNoticeManager.SEPER_TIME_AND_ID.length();
				int position2 = inf.indexOf(ReceivedNoticeManager.SEPER_ID_AND_CONTENT);
				id = (position1==ReceivedNoticeManager.SEPER_TIME_AND_ID.length()-1 || position2 == -1 )?null:inf.substring(position1,position2);
				return id;
			}
			//��ÿ�������ж����ֽ���
			private static int getBytes(String inf){
				String bytes= "";
				int position = inf.indexOf(ReceivedNoticeManager.SEPER_ID_AND_CONTENT)+ReceivedNoticeManager.SEPER_ID_AND_CONTENT.length();
				bytes = (position==ReceivedNoticeManager.SEPER_ID_AND_CONTENT.length()-1)?null:inf.substring(position);
				int length = 0;
				try{
					length = Integer.parseInt(bytes.trim());
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				return length;
				
			}
			//����ж�����δ������Ϣ
			public static int getNoSaw(){
				RandomAccessFile rf = null;
				int no_saw = 0;
				byte[] bytes_head = new byte[(int)MAX_HEAD];
				long  back = 0;//
				long file_length = file.length();//�ļ��ĳ���
				try{
					rf = new RandomAccessFile(file.toString(),"r");
					while(file_length-back-MAX_HEAD>0){
						rf.seek(file_length-back-MAX_HEAD);
						rf.readFully(bytes_head);
						String head = new String(bytes_head);
						if(getFlag(head) == FLAG_NO_SAW ){
							++no_saw;
						}
					back += getBytes(head);
					}
				}catch(FileNotFoundException e){
					System.out.println("δ�ҵ��ļ�");
					e.printStackTrace();
				}catch(IOException e){
					System.out.println("IO����");
					e.printStackTrace();
				}finally{
					try{
						rf.close();
					}catch(FileNotFoundException e){
						e.printStackTrace();
					}catch(IOException  e){
						e.printStackTrace();
					}
				}
				return no_saw;
			}
			
			//���ļ�ĩβ��ʼ��count������
			public static ArrayList<Map<String,String> > getDataFromBack(int count){
				ArrayList<Map<String,String> > list = new ArrayList<Map<String,String>>();
				RandomAccessFile rf = null;
				String line_head,line_content;
				long bytes_sum = 0;
				byte[] bytes_head = new byte[(int)MAX_HEAD];
				
				try{
					rf = new RandomAccessFile(file.toString(),"r");
					long length = rf.length();
					if(length == 0){return null;}//����ļ��ǿյĻ�����null
					long start = rf.getFilePointer();
					long nextend=start+length-1;
					long total = 0;
					rf.seek(nextend);
				
					while(length-total-MAX_HEAD>0 && count>0){
						Map<String,String> map = new HashMap<String,String>();
						
						rf.seek(length-total-MAX_HEAD);
						rf.readFully(bytes_head);
						line_head = new String(bytes_head,CHARSET);
						bytes_sum = getBytes(line_head);//�������ݵ����ֽ���;
						map.put(KEY_HEAD,line_head);//����head����
						total += bytes_sum;

						byte[] bytes_content = new byte[(int)(bytes_sum-MAX_HEAD)];
						rf.seek(length-total);
						rf.readFully(bytes_content);
						line_content = new String(bytes_content,CHARSET);
						map.put(KEY_CONTENT, line_content);
						
						count--;
						list.add(map);
					}
					
					
				}catch(FileNotFoundException e){
					e.printStackTrace();
				}catch(IOException e){
					e.printStackTrace();
				}finally{
					try{
						rf.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				return list;
			}
			
			//���ļ���д����
			public static boolean write(String head,String content){
				FileOutputStream out = null;
				try {
					head += (content.getBytes(CHARSET).length+MAX_HEAD);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try{
					out = new FileOutputStream(file.toString(),true);
					byte[] bytes_head = head.getBytes(CHARSET);
					byte[] bytes = new byte[(int)MAX_HEAD];
					int i;
					for(i=0;i<bytes_head.length;++i){
						bytes[i] = bytes_head[i];
					}
					for(;i<MAX_HEAD;++i){
						bytes[i]=" ".getBytes(CHARSET)[0];
					}
					out.write(content.getBytes(CHARSET));
					out.write(bytes);
			}catch(FileNotFoundException e){
				e.printStackTrace();
				return false;
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}finally{
				try{
					out.close();
				}catch(FileNotFoundException e){
					e.printStackTrace();
					return false;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
				return true;
			}
			}
			//���ļ���д���ݣ�������������
			public static boolean write(String number,String time,String flag,String id,String content){
				String head = "";
				head += number + SEPER_NUMBER_AND_FLAG +flag + SEPER_FLAG_AND_TIME +time +  SEPER_TIME_AND_ID + id +SEPER_ID_AND_CONTENT;
				return (write(head,content) == true)?true:false;
			}
			
			//����id��һ��֪ͨ
			public static  Map<String,String> getNoticeById(String id){
				Map<String,String> map = new HashMap<String,String>();
				RandomAccessFile rf = null;;
				String content = "";
				byte[] bytes_head = new byte[(int)MAX_HEAD];
				long  back = 0;//
				long file_length = file.length();//�ļ��ĳ���
				try{
					rf = new RandomAccessFile(file.toString(),"r");
					while(file_length-back-MAX_HEAD>0){
						rf.seek(file_length-back-MAX_HEAD);
						rf.readFully(bytes_head);
						String head = new String(bytes_head,CHARSET);
					
						if(getId(head).equals(id) ){
							int total = getBytes(head);
							back += total;
							rf.seek(file_length-back);
							byte[] bytes_content = new byte[total-(int)MAX_HEAD];
							rf.readFully(bytes_content);
							content = new String(bytes_content,CHARSET);
							map.put(KEY_HEAD, head);
							map.put(KEY_CONTENT, content);
							return map;
						}
					back += getBytes(head);
					}
				}catch(FileNotFoundException e){
					System.out.println("δ�ҵ��ļ�");
					e.printStackTrace();
				}catch(IOException e){
					System.out.println("IO����");
					e.printStackTrace();
				}finally{
					try{
						rf.close();
					}catch(FileNotFoundException e){
						e.printStackTrace();
					}catch(IOException  e){
						e.printStackTrace();
					}
				}
				return null;
			}
}

