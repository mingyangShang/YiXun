  
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

public class MessageManager {
	private static File file = null;
	private static final String SEPER_NUMBER_AND_FLAG = "/*";
	private static final String SEPER_FLAG_AND_TIME = "//";
	private static final String SEPER_TIME_AND_CONTENT = "*/";
	
	private static final long MAX_HEAD = 50;
	
	public static final String KEY_HEAD = "head";
	public  static final String KEY_CONTENT = "content";
	
	private static final int BITES_OF_NUMBER = 11;//�˺ŵ��ֽڳ���
	private static final int BITES_FRONT_FLA = BITES_OF_NUMBER + SEPER_NUMBER_AND_FLAG.length();//���֮ǰ���ֽڳ��ȣ��������ǹ̶�ֵ13
	
	private static final int FLAG_NO_SAW = 0;//δ���ı��
	private static final int FLAG_SAW = 1;//�����ı��
//	private static final String FLAG_DEL = "2";
	
	private static final String CHARSET = "UTF-8";
	//���ֹ��캯��
		public MessageManager(File f){
			file = f;
		}
		public MessageManager(String pathname) throws IOException{
			file = new File(pathname);
		}
		public MessageManager(String parent,String child){
			file = new File(parent,child);
		}
		public MessageManager(File parent,String child){
			file = new File(parent,child);
		}
		
			//��ÿһ�ж����˺�
			public static String getNumber(String inf){
					String number = "";
					int position = inf.indexOf(MessageManager.SEPER_NUMBER_AND_FLAG);
					number = (position == -1)?null:inf.substring(0, position);
					return number;
				}
				//��ÿ�������ж����Ƿ��Ѷ��ı��
				public static int getFlag(String inf){
					String flag = "";
					int position1 = inf.indexOf(MessageManager.SEPER_NUMBER_AND_FLAG)+MessageManager.SEPER_NUMBER_AND_FLAG.length();
					int position2 = inf.indexOf(MessageManager.SEPER_FLAG_AND_TIME);
					flag = (position1 == MessageManager.SEPER_NUMBER_AND_FLAG.length()-1 || position2 == -1)?null:inf.substring(position1, position2);
			
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
					int position1=inf.indexOf(MessageManager.SEPER_FLAG_AND_TIME)+MessageManager.SEPER_FLAG_AND_TIME.length();
					int position2 = inf.indexOf(MessageManager.SEPER_TIME_AND_CONTENT);
					time = (position1 == MessageManager.SEPER_FLAG_AND_TIME.length()-1 || position2 ==-1)?null:inf.substring(position1, position2);
					return time;
				}
				//��ÿ�������ж����ֽ���
				private static int getBytes(String inf){
					String bytes= "";
					int position = inf.indexOf(MessageManager.SEPER_TIME_AND_CONTENT)+MessageManager.SEPER_TIME_AND_CONTENT.length();
					bytes = (position==MessageManager.SEPER_TIME_AND_CONTENT.length()-1)?null:inf.substring(position);
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
					if(file_length==0){
						return 0;
					}
					try{
						rf = new RandomAccessFile(file.toString(),"r");
						while(file_length-back-MAX_HEAD>0){
							rf.seek(file_length-back-MAX_HEAD);
							rf.readFully(bytes_head);
							String head = new String(bytes_head);
							if(getFlag(head) == FLAG_NO_SAW ){
								++no_saw;
							}else{
								break;
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
				//����ļ������һ������
				public static Map<String,String> getLastRecord(){
					RandomAccessFile rf = null;
					String line_head,line_content;
					long bytes_sum = 0;
					byte[] bytes_head = new byte[(int)MAX_HEAD];
					Map<String,String> map = new HashMap<String,String>();						
					try{
						rf = new RandomAccessFile(file.toString(),"r");
						long length = rf.length();
						if(length == 0){return null;}//����ļ��ǿյĻ�����null
						map = new HashMap<String,String>();						
						rf.seek(length-MAX_HEAD);
						rf.readFully(bytes_head);
						line_head = new String(bytes_head,CHARSET);
						bytes_sum = getBytes(line_head);//�������ݵ����ֽ���;
						map.put(KEY_HEAD,line_head);//����head����						
						byte[] bytes_content = new byte[(int)(bytes_sum-MAX_HEAD)];
						rf.seek(length-bytes_sum);
						rf.readFully(bytes_content);
						line_content = new String(bytes_content,CHARSET);
						map.put(KEY_CONTENT, line_content);					
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
					return map;
				}
				
				//���ļ���д����
				public  boolean write(String head,String content){
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
						int i=0;
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
				public boolean write(String number,String time,String flag,String content){
					String head = "";
					head += number + SEPER_NUMBER_AND_FLAG +flag + SEPER_FLAG_AND_TIME +time +  SEPER_TIME_AND_CONTENT;
					return (write(head,content) == true)?true:false;
				}
				
	}