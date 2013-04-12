package com.manage;

import java.util.HashMap;

import javax.swing.JLabel;

import com.TcpClient;
import com.UdpClient;
import com.UdpMsgParseThread;

public class UDPClientManage {

	public static HashMap<String, UdpClient> hmSudp = new HashMap<String, UdpClient>();
	public static HashMap<JLabel, UdpClient> hmJlb = new HashMap<JLabel, UdpClient>();

	public static UdpClient getUdpClientbByJlabel(JLabel jl) {
		return hmJlb.get(jl);
	}

	public static void addHmJlb(JLabel jl, UdpClient uc) {
		hmJlb.put(jl, uc);
	}

	public static void addUdpClient(String ipStr, UdpClient uc) {
		if (hmSudp.put(ipStr, uc) == uc) {
			// hm.remove(ipStr);
			// hm.put(ipStr, uc);
		}

	}

	public static UdpClient getUdpClient(String ipStr) {
		return (UdpClient) hmSudp.get(ipStr);
	}

	public static void delUdpClient(String ipStr) {
		hmSudp.remove(ipStr);
	}
}
