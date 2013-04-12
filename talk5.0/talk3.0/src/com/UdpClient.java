package com;

import java.net.InetAddress;

import view.ChatBox;

public class UdpClient {
	public String nickName;
	public String sex;
	public boolean isOnLine;
	private boolean isRead;

	public InetAddress clientIp;
	public int destPort = 8888;
	public ChatBox chatBox = null;

	public boolean isRead() {
		// synchronized (this) {
		return isRead;
		// }
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
}
