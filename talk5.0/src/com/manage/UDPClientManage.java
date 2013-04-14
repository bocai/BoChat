package com.manage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JLabel;

import com.TcpClient;
import com.UdpClient;
import com.UdpMsgParseThread;

public class UDPClientManage {

	public static HashMap<String, UdpClient> hmSudp = new HashMap<String, UdpClient>();
	public static HashMap<JLabel, UdpClient> hmJlb = new HashMap<JLabel, UdpClient>();

	public static UdpClient getUdpClientbByJlabel(JLabel jl) {
		synchronized (hmJlb) {
			return hmJlb.get(jl);
		}
	}

	public static void addHmJlb(JLabel jl, UdpClient uc) {
		synchronized (hmJlb) {
			hmJlb.put(jl, uc);
		}
	}

	public static UdpClient delUdpClientbByJlabel(JLabel jl) {
		synchronized (hmJlb) {
			return hmJlb.remove(jl);
		}
	}

	public static void addUdpClient(String ipStr, UdpClient uc) {
		synchronized (hmSudp) {
			hmSudp.put(ipStr, uc);
		}
	}

	public static UdpClient getUdpClient(String ipStr) {
		synchronized (hmSudp) {
			return (UdpClient) hmSudp.get(ipStr);
		}
	}

	public static void delUdpClient(String ipStr) {
		synchronized (hmSudp) {
			hmSudp.remove(ipStr);
		}
	}
	public static void offLine() {
		//HashMap<String, UdpClient> map = hmSudp;
		synchronized (hmSudp) {
			Iterator<Entry<String, UdpClient>> iter = hmSudp.entrySet().iterator();

			while (iter.hasNext()) {
				Entry<String, UdpClient> entry = iter.next();
				String addr = entry.getKey();
				UdpClient uc = (UdpClient) entry.getValue();
				// print("Send OffMsg to "+ uc.nickName);
				if(uc != null) {
					MainManage.sendOffLineMsg(uc);
				}
			}
		}
	}
}
