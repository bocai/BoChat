package com.manage;

import java.util.HashMap;

import javax.swing.JLabel;

import view.ChatBox;

import com.TcpClient;
import com.UdpClient;

public class ChatBoxManage {

	public static HashMap<String, ChatBox> hmsc = new HashMap<String, ChatBox>();
	public static HashMap<JLabel, ChatBox> hmjc = new HashMap<JLabel, ChatBox>();
	public static HashMap<String, JLabel> hmLabel = new HashMap<String, JLabel>();

	public static JLabel getHmLabel(String str) {
		return hmLabel.get(str);
	}

	public static void addHmLabel(String str, JLabel jl) {
		hmLabel.put(str, jl);
	}

	public static void print(Object o) {
		// System.out.println(o);
	}

	public static ChatBox getBoxByJLabel(JLabel jl) {
		return hmjc.get(jl);
	}

	public static void addBoxByJLabel(JLabel jl, ChatBox cb) {
		hmjc.put(jl, cb);
	}

	public static void delBoxByLabel(JLabel jl) {
		print("remove box ByLb ");
		hmjc.remove(jl);
	}

	public static void addBoxByIp(String ipStr, ChatBox cb) {
		print("add box ByIp " + ipStr);
		hmsc.put(ipStr, cb);
	}

	public static ChatBox getBoxByIp(String ipStr) {
		print("getByIp " + ipStr);
		return hmsc.get(ipStr);
	}

	public static void delBoxByIp(String ipStr) {
		print("del box ByIp " + ipStr);
		hmsc.remove(ipStr);

	}

}
