package com.manage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import view.FriendList;

import com.MsgObj;
import com.TcpServ;
import com.UdpClient;
import com.UdpMsgParseThread;
import com.interfaces.MsgType;

public class MainManage implements Runnable {
	protected static String ownerIP; // use ip as id
	public static String nickname;

	public static DatagramSocket serverSK;
	
	public static boolean noExit = true;
	private InetAddress broadcastAddress;
	private String ownerSex;
	private static Thread friendListThread = null;
	Thread tcpSvrTr = null;
	protected static int UDPport = 8888; // 默认
	private static InetAddress UdpAddr;
	public static InetAddress loAddr;

	public static void main(String args[]) {
		new MainManage();
	}

	public static DatagramSocket getUdpServerSock() {
		return serverSK;
	}

	public static Thread getFriendListThread() {
		return friendListThread;
	}

	public static String getHostIp() {
		return ownerIP;
	}

	public static InetAddress getUdpAddr() {
		return UdpAddr;
	}

	public static String getNickname() {
		return nickname;
	}

	public static void setNickname(String nickname) {
		MainManage.nickname = nickname;
	}

	public MainManage() {
		launch();
	}

	public MainManage(int port) {
		UDPport = port;
		launch();
	}

	public void launch() {

		try {
			serverSK = new DatagramSocket(UDPport);
			
			UdpAddr = InetAddress.getLocalHost();
			ownerIP = UdpAddr.getHostAddress();
			loAddr = InetAddress.getByName("127.0.0.1");
			broadcastAddress = InetAddress.getByName("255.255.255.255");
			 print("lo: " + loAddr);

			nickname = JOptionPane.showInputDialog("Entre your nickname please");
			if (nickname == null || 0 == nickname.length())
				nickname = UdpAddr.toString();
			else {
				nickname = nickname + " - "
						+ UdpAddr.getHostAddress().toString();
			}

			this.ownerSex = "gril";
			// print("my name is " + nickname);
		} catch (SocketException e) {
			// e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Port " + UDPport
					+ " has been used！");
			System.exit(0);

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		FriendList friendLists = FriendList.getFriendListInstance();

		Thread slfTh = new Thread(this);
		slfTh.start();

		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_CONNECT);
		m.setSender(nickname);
		m.setGetter(null);
		m.setSex(ownerSex);
		// print("sendBroadcast");
		udpSendMsgObj(m, broadcastAddress);

		friendLists.setVisible(true);
		friendListThread = new Thread(friendLists);
		friendListThread.start();
		TcpServ tSvr = new TcpServ();
		tcpSvrTr = new Thread(tSvr);
		tcpSvrTr.start();
	}

	public static void stopFriThread() {
		if (friendListThread != null)
			friendListThread.interrupt();
	}

	public static void udpSendMsgObj(MsgObj m, InetAddress addr) {

		try {
			byte[] by = new byte[1024 * 1024];
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			ObjectOutputStream bos = new ObjectOutputStream(bas);
			bos.writeObject(m);
			by = bas.toByteArray();
			// System.out.println(new String(by));
			DatagramPacket pk = new DatagramPacket(by, by.length, addr, UDPport);
			serverSK.send(pk);
		} catch (SocketException e) {
			JOptionPane.showMessageDialog(null, "Network is down");
			// e.printStackTrace();
		} catch (IOException e) {

			JOptionPane.showMessageDialog(null, "Network is down");
			// e.printStackTrace();
			// System.exit(1);
		}
	}

	public static void sendOffLineMsg(UdpClient uc) {
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_OFFLINE);
		m.setSender(nickname);
		m.setGetter(uc.nickName);
		m.setContent(null);

		udpSendMsgObj(m, uc.clientAddr);
	}

	public static void reqSendFileMsg(UdpClient uc) {
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_TCP_FILE);
		m.setSender(nickname);
		m.setGetter(uc.nickName);

		udpSendMsgObj(m, uc.clientAddr);
	}

	public static void sendConfirmFileMsg(UdpClient uc, String str) {
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_TCP_CONFIRM);
		m.setSender(nickname);
		m.setGetter(uc.nickName);
		m.setContent(str);

		udpSendMsgObj(m, uc.clientAddr);
	}
	public static void sendOnLineMsg(UdpClient uc, String str) {
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_ONLINE);
		m.setSender(nickname);
		m.setGetter(uc.nickName);
		m.setContent(str);

		udpSendMsgObj(m, uc.clientAddr);
	}

	public static void reqSendFileMsg(UdpClient uc, String str) {
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_TCP_FILE);
		m.setSender(nickname);
		m.setGetter(uc.nickName);
		m.setContent(str);
		udpSendMsgObj(m, uc.clientAddr);
	}

	public static void closeUdpSock() {
		if (null != serverSK) {
			noExit = false;
			serverSK.close();
			serverSK = null;
		}
	}

	public static void print(Object o) {

		//System.out.println(o);
	}
	
	public static void cleanClient(String addrStr) {
		JLabel jl = ChatBoxManage.getHmLabel(addrStr);
		FriendList.getFriendListInstance().delLabel(jl);

		ChatBoxManage.delBoxByIp(addrStr);
		UDPClientManage.delUdpClient(addrStr);
		UDPClientManage.delUdpClientbByJlabel(jl);
		TCPClientManage.deleteOne(addrStr);
	}
	@Override
	public void run() {
		// print("udp serv start recv");
		while (noExit) {
			byte[] by = new byte[1024 * 1024];
			// try {
			DatagramPacket dp = new DatagramPacket(by, by.length);
			try {
				serverSK.receive(dp); // wait for datagramPacket
				
//				 if(dp.getAddress().toString().substring(1).equals(ownerIP)
//				 == true )||
//				 true == dp.getAddress().toString().equals(loAddr.toString())
//				 ||true == dp.getAddress().equals(MainManage.getUdpAddr())
//				 ||true == dp.getAddress().equals(loAddr)) 
//				 {
//					 print("seft msg " + dp.getAddress());
//					 
//					 continue;
//				 }
				print("recv one pk from ip " + dp.getAddress());
				new Thread(new UdpMsgParseThread(dp)).start();

			} catch (IOException e) {
				// e.printStackTrace();
				JOptionPane.showMessageDialog(null, "recv error");
				System.exit(1);
			}
		}
	}
}
