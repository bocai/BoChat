package com.manage;

import java.util.HashMap;
import com.*;

public class TCPClientManage {
	public static HashMap<String, TcpClient> hmST = new HashMap<String, TcpClient>();

	public synchronized static TcpClient deleteOne(String address, TcpClient tc) {
		return (TcpClient) hmST.get(address);
	}

	public synchronized static void addTClient(String address, TcpClient tc) {
		hmST.put(address, tc);
	}

	public synchronized static TcpClient getTCPClient(String address) {
		return (TcpClient) hmST.get(address);
	}

}
