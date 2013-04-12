package com.manage;

import java.util.HashMap;

import javax.swing.JLabel;

import view.ChatBox;

import com.TcpClient;

public class ChatBoxManage {
	public static HashMap<String, ChatBox> hmsc = new HashMap<String, ChatBox>();
	public static HashMap<JLabel, ChatBox> hmjc = new HashMap<JLabel, ChatBox>();
	
	public static ChatBox getBoxByJLabel(JLabel jl) {
		return hmjc.get(jl);
	}

	public static void addBoxByJLabel(JLabel jl, ChatBox cb) {
		hmjc.put(jl, cb);
	}

	public static void addBoxByIp(String usrID, ChatBox cb) {
		hmsc.put(usrID, cb);
	}
	
	public static ChatBox getBoxByIp(String usrID) {
		
		return hmsc.get(usrID);
	}
}
