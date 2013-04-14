package com.manage;

import java.util.HashMap;
import com.*;

public class TCPClientManage {
	public static HashMap<String, TcpClient> hmST = new HashMap<String, TcpClient>();

	public  static void deleteOne(String address) {
		synchronized(hmST) {
			 hmST.remove(address);
		}
	}

	public  static void addTClient(String address, TcpClient tc) {
		synchronized(hmST) {
			hmST.put(address, tc);
		}
	}

	public static TcpClient getTCPClient(String address) {
		return (TcpClient) hmST.get(address);
	}

}
