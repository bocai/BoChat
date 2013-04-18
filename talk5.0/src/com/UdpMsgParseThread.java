package com;

import com.interfaces.*;
import com.manage.ChatBoxManage;
import com.manage.MainManage;
import com.manage.TCPClientManage;
import com.manage.UDPClientManage;
import view.ChatBox;
import view.FriendList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.io.FileInputStream;

// Parse UDP packet
public class UdpMsgParseThread implements Runnable {
	// String ipStr;
	String Mynickname;
	DatagramPacket dp;
	String addrStr;
	private int port;
	UdpClient uc = null;

	public UdpMsgParseThread(DatagramPacket dp) {
		this.dp = dp;
	}

	public int getPort() {
		return port;
	}

	void responseMsg(MsgObj m) {
		m.setSender(MainManage.nickname);
		m.setSex(MainManage.getOwnerSex());
		m.setMsgtype(MsgType.MSG_RESPONSE);// 回应
	
		MainManage.udpSendMsgObj(m, dp.getAddress());
	}

	private void dealConnectMsg(MsgObj m) {

		uc = new UdpClient();
		if (uc == null)
			return;
		// uc.usrIpStr = dp.getAddress().toString();
		uc.isOnLine = true;
		uc.setRead(true);
		uc.nickName = new String(m.getSender());
		uc.sex = m.getSex();
		uc.clientAddr = dp.getAddress();

		JLabel jl = FriendList.getFriendListInstance().addrFriend(uc);
		ChatBoxManage.addHmLabel(addrStr, jl); // to del JLabel
		UDPClientManage.addHmJlb(jl, uc); // through jl to visit ChatBox
		UDPClientManage.addUdpClient(addrStr, uc); // to del UdpClient
		if(addrStr.substring(1).equals(MainManage.getHostIp()) == false) { //排除自己
			new Thread(uc).start();
		}
		print("dealConnect end; udpClient size " + UDPClientManage.hmJlb.size());
	}
	
	private void dealNormalMsg(MsgObj m) {
		
		ChatBox cb = uc.chatBox;
		// firstRecv()
		if (null == cb) { // first recv normal msg
			cb = createBox();

			// print("can not find chatBox in hashMap");
		}
		if (cb.isVisible() == false) {
			uc.setRead(false);
			// MainManage.getFriendListThread().resumeTr();
			FriendList.getFriendListInstance().resumeTr();
		}
		cb.appendTarea("From " + m.getSender() + System.getProperty("line.separator") + m.getContent());
		// print(m.getSender() + " say: " +
		// m.getContent()+" from ("+addrStr+")");

	}

	private void dealRecvFileMsg(MsgObj m) {
		
		if (null == uc)
			return;
		print("content:" + m.getContent());
		StringTokenizer str = new StringTokenizer(m.getContent(), ":");
		String filename = str.nextToken();
		if(null == filename) 
			return;
		int ret = JOptionPane.showConfirmDialog(null, "File: " + filename + " from "+ addrStr,//m.getContent(),
				"确认接收文件吗？", JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.YES_OPTION) {

			print("I accpet recv");
			TcpClient tcpClient = TCPClientManage.getTCPClient(addrStr);
			if (null == tcpClient) { // first time
				ChatBox cb = uc.chatBox;
				print("fist connetc tcp");
				if (null == cb) { // first recv 
					cb = createBox();
					print("createBox");
					uc.chatBox = cb;
					// print("can not find chatBox in hashMap");
				}
				
				tcpClient = new TcpClient();
				tcpClient.setChatBox(cb);
				cb.tcpClient = tcpClient;
				TCPClientManage.addTClient(addrStr, tcpClient);
				
			}

			int i = Integer.parseInt( str.nextToken() );
			
			if( true == tcpClient.setFilesRecv(filename, i) ) {

				MainManage.sendConfirmFileMsg(uc, m.getContent());
				print("ok i=" + i);
			}
			else {
				MainManage.sendConfirmFileMsg(uc, null);
			}
			

		} else if (ret == JOptionPane.NO_OPTION) {
			print("I refuse recv");
			MainManage.sendConfirmFileMsg(uc, null);
		}

	}
	// start send file by Confirm msg
	private void dealResponConfirmMsg (MsgObj m) { 
		if (uc == null) {
			return;
		}
		if(m.getContent() == null) {
			print("content is null");
			return;
		}
		print("content:" + m.getContent());
		TcpClient tcpClient = TCPClientManage.getTCPClient(addrStr);
		if (null == tcpClient) {
			return;
		}
		
		StringTokenizer content = new StringTokenizer(m.getContent(), ":");
		String filenameStr = content.nextToken();
		int i = Integer.parseInt( content.nextToken() );
		String file_of_index = tcpClient.getFileName(i);
		
		print(filenameStr + " & begin send i=" + i + " "+ file_of_index  );
		
		if (filenameStr.equalsIgnoreCase(file_of_index) == true) {
			
			if(null == tcpClient.connectAndSendFile(uc, i)) {
				//MainManage.cleanClient(addrStr);
			}
			
		} else {
			//tcpClient.sendFiles[i] = null;
			print("he refuse files req");
		}
	}
	private void playMsgSound() {
		/* 由于声音的播放需要  java.applet.*; 或者sun.audio.*;较麻烦，因此先不弄了*/
//		AudioClip aClip =  getAudioClip(getDocumentBase(),"sound/msg.wav");;
//		if(aClip != null)
//			aClip.play();
		
	}
	
	@Override
	public void run() {

		ByteArrayInputStream bas = new ByteArrayInputStream(dp.getData());
		ObjectInputStream ois = null;
		MsgObj m = null;
		try {
			ois = new ObjectInputStream(bas);
			m = (MsgObj) ois.readObject();

		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if (m.getGetter() == null  //broadcase
				|| true == MainManage.nickname.equalsIgnoreCase(m.getGetter())) {
			addrStr = dp.getAddress().toString();

			uc = UDPClientManage.getUdpClient(addrStr);
			if(uc != null) {
				uc.setUdpLastRecvTime(System.currentTimeMillis());
			}
			
			switch (m.getMsgtype()) {
			case MsgType.MSG_NORMAL:
				print("Normal Msg");

				if (null == uc)
					return;
				
				dealNormalMsg(m);
				break;

			case MsgType.MSG_ONLINE:
				if(uc != null) {
					uc.isOnLine = true;
					if(m.getContent() == null) {
						MainManage.sendOnLineMsg(uc, "ok");
					}
				}
				print("Online msg");

				break;

			case MsgType.MSG_CONNECT:
				// connect
				print("recv connect msg");
				// 处理connect
				if (uc == null) {
					dealConnectMsg(m);
				}
				responseMsg(m);
				break;

			case MsgType.MSG_RESPONSE:
				print("recv response msg");
				if (uc == null)
					dealConnectMsg(m);
				break;

			case MsgType.MSG_OFFLINE:
				if(null == uc) {
					return;
				}
				
				print("recv OffLineMsg");
				uc.isOnLine = false;
				MainManage.cleanClient(addrStr);
				
				break;
			case MsgType.MSG_TCP_FILE:
				print("recv MSG_TCP_FILE");
				dealRecvFileMsg(m);
				break;

			case MsgType.MSG_TCP_CONFIRM:
				print("recv MSG_TCP_CONFIRM");
				dealResponConfirmMsg(m);
				
				break;
			case MsgType.MSG_BE_READ_REV_DAT:
				
				break;
			}
			
			// print("parse end! chatbox size " + ChatBoxManage.hmsc.size());
			// print("-----------------------------------");
			dp = null;
		}
	}


	private ChatBox createBox() {
		ChatBox cb = ChatBox.getChatBox(uc);
		cb.setClientAddr(uc.clientAddr);
		uc.chatBox = cb;
		ChatBoxManage.addBoxByIp(addrStr, cb);

		// print("can not find chatBox in hashMap");
		return cb;
	}
	

	void print(Object o) {
	//	System.out.println(o);
	}

}
