package com.manage;

import java.util.HashMap;
import com.*;

public class TCPClientManage {
	public static HashMap<String, TcpClient> hm = new HashMap<String, TcpClient>();

	public static TcpClient deleteOne(String usrID, TcpClient tc) {
		return (TcpClient) hm.get(usrID);
	}

	public static void addTClient(String UsrID, TcpClient tc) {
		hm.put(UsrID, tc);
	}

	public static TcpClient getTCPClient(String usrID) {
		return (TcpClient) hm.get(usrID);
	}

}
