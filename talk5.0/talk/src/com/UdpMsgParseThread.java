package com;
import com.interfaces.*;
import com.manage.ChatBoxManage;
import com.manage.UDPClientManage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;

import javax.swing.JLabel;

import view.ChatBox;
import view.FriendList;
// 解析UDP消息
public class UdpMsgParseThread implements Runnable {
	String ipStr;
	String nickname;
	DatagramPacket dp;
	private int port;
	UdpMsgParseThread(DatagramPacket dp){
		this.dp = dp;
	}
    public int getPort() {
		return port;
	}
	void print(Object o) {
		System.out.println(o);
	}
	@Override
	public void run() {
		ByteArrayInputStream bas = new ByteArrayInputStream(dp.getData());
		ObjectInputStream ois;
		MsgObj m = null;
		try {
			ois = new ObjectInputStream(bas);
			m = (MsgObj)ois.readObject();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
		}
		short mt = m.getMsgtype();
		String addrStr = dp.getAddress().toString();
		UdpClient uc = UDPClientManage.getUClient(addrStr);
		if(mt == MsgType.MSG_NORMAL) {
			print("Normal Msg");
			ChatBox cb = ChatBoxManage.getBoxByIp(addrStr);
			//if() return;
			if(null != cb && false == cb.isVisible()) {
				uc.isRead = false;
			
				cb.appendTarea(m.getContent());
			}
			print(m.getSender() + " say: " + m.getContent());
		} 
		else if(mt == MsgType.MSG_ONLINE)
		{
			print("Online msg");
		}
		else if(mt == MsgType.MSG_CONECT) {
			//connect 
			print("connect msg");
			if(uc == null)
				uc = new UdpClient();
			
			//uc.usrIpStr = dp.getAddress().toString();
			uc.isOnLine = true;
			uc.isRead = true;
			uc.nickName = m.getSender();
			uc.sex = m.sex;
			ChatBox cb = new ChatBox(MainManage.getNickname());
			
			JLabel jl = FriendList.addrFriend(uc,FriendList.getFriendListInstance());
			jl.setEnabled(true);
			UDPClientManage.addUDPClient(addrStr, uc);
			
			ChatBoxManage.addBoxByJLabel(jl, cb);
			ChatBoxManage.addBoxByIp(addrStr, cb);
			print("chatbox size " + ChatBoxManage.hmsc.size());
			//cb.showWindow();
		}
		else if(mt == MsgType.MSG_OFFLINE) {
			
		}
		//print("sender " + m.getSender());
		//System.out.println(Arrays.toString(by));
		print("from -- " + dp.getSocketAddress());
		dp = null;
	}
	
}
