package com.interfaces;

public interface MsgType {
	
	public final short MSG_CONNECT	 		= 0;
	public final short MSG_NORMAL  			= 1;
	public final short MSG_ONLINE  			= 2;
	public final short MSG_OFFLINE 	   		= 3;
	public final short MSG_SEND_FILE 		= 4;
	public final short MSG_RESPONSE    		= 5;
	
	public final short MSG_TCP_FILE 		= 6; //send file
	public final short MSG_TCP_CONFIRM   	= 7; //确认接收
	public final short MSG_TCP_RESPONSE  	= 8;
}
