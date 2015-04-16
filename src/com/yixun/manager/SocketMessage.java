package com.yixun.manager;
public class SocketMessage{
				public String type;
				public String from_name;
				public String from_weixin;
				public String from_phone;
				public String to_name;
				public String to_weixin;
				public String to_phone;
				public String msg;
				public String time;
				public SocketMessage(){
					type=null;
					from_name=null;
					from_phone=null;
					from_weixin=null;
					to_name=null;
					to_phone=null;
					to_weixin=null;
					msg=null;
					time=null;
				}
			}