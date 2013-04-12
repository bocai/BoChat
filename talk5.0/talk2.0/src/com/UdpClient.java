package com;

import java.net.InetAddress;

import view.ChatBox;

public class UdpClient {
	public String nickName;
	public String sex;
	public boolean isOnLine;
	public boolean isRead;
	public InetAddress clientIp;
	public int destPort = 8888;
	public ChatBox chatBox = null;
}
