package com.manage;

import java.util.HashMap;
import com.TcpClient;
import com.UdpClient;
import com.UdpMsgParseThread;

public class UDPClientManage {
	
	public static HashMap<String,UdpClient> hm = new HashMap<String,UdpClient>();
	
	public static void addUDPClient(String ipStr, UdpClient uc) {
		if(hm.put(ipStr, uc) == uc) {
			//hm.remove(ipStr);
			//hm.put(ipStr, uc);
		}
		
	}
	
	public static UdpClient getUClient(String ipStr) {
		return (UdpClient)hm.get(ipStr);
	}
}
