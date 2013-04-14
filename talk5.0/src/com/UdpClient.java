package com;

import java.net.InetAddress;

import javax.swing.JLabel;

import com.manage.ChatBoxManage;
import com.manage.MainManage;
import com.manage.TCPClientManage;
import com.manage.UDPClientManage;

import view.ChatBox;
import view.FriendList;

public class UdpClient implements Runnable {
	public String nickName;
	public String sex;
	private long udpLastRecvTime = System.currentTimeMillis();//开始心跳的时间

	public boolean isOnLine;
	private boolean isRead;

	public InetAddress clientAddr;
	public short destPort = 8888;
	public ChatBox chatBox = null;

	public long getUdpLastRecvTime() {
		return udpLastRecvTime;
	}

	public void setUdpLastRecvTime(long udpLastRecvTime) {
		this.udpLastRecvTime = udpLastRecvTime;
	}

	public boolean isRead() {
		// synchronized (this) {
		return isRead;
		// }
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	@Override
	public void run() {
		/* 如果由于对端异常退出，所以没有收到offline消息，无法得知其是否在线，为此设心跳线程 */
		final long timeOutMinute = 3 * 60;
		while(isOnLine) {
			long crentTime = System.currentTimeMillis();
			long timeCount = crentTime - udpLastRecvTime;//时间差
			
			if(timeCount > timeOutMinute * 1000L) { 
				String addrStr = clientAddr.toString();
//				if(true == MainManage.getHostIp().equals(addrStr.substring(1))) {
//					continue;
//				}
				MainManage.cleanClient(addrStr);
				MainManage.print("timeout");
			}
			else if(timeCount > 1 * 60 * 1000L) { // 1 minte 
				MainManage.sendOnLineMsg(this,null);
			}
			
			try {
				Thread.sleep(60 * 1000L);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
		
	}

}
