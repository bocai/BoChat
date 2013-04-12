package com;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import javax.swing.JOptionPane;
import view.FriendList;
import com.interfaces.MsgType;

public class MainManage implements Runnable {
	protected String ownerIP; // use ip as id
	static String nickname;

	public static DatagramSocket serverSK;
	protected static int UDPport = 8888; // 默认
	public static boolean noExit = true;
	private InetAddress broadcastAddress;
	private static Thread friendListThrea = null;
	private static InetAddress UdpAddr;
	public static InetAddress loAddr;

	public static void main(String args[]) {
		new MainManage();
	}

	public static DatagramSocket getUdpServerSock() {
		return serverSK;
	}

	public String getIPstr() {
		return ownerIP;
	}

	public static InetAddress getUdpAddr() {
		return UdpAddr;
	}

	public String getOwnerIP() {
		return ownerIP;
	}

	public void setOwnerIP(String ownerIP) {
		this.ownerIP = ownerIP;
	}

	public static String getNickname() {
		return nickname;
	}

	public static void setNickname(String nickname) {
		MainManage.nickname = nickname;
	}

	public MainManage() {
		login();
	}

	public MainManage(int port) {
		UDPport = port;
		login();
	}

	public void login() {
		nickname = JOptionPane.showInputDialog("請輸入你的昵称");
		nickname = nickname == null ? "匿名" : nickname;
		// print("my name is " + nickname);
		try {
			serverSK = new DatagramSocket(UDPport);
			// new DatagramSocket()
			UdpAddr = InetAddress.getLocalHost();
			loAddr = InetAddress.getByName("127.0.0.1");
			broadcastAddress = InetAddress.getByName("255.255.255.255");// .getByName("230.0.0.1");
			// broadcastAddress
			// print("lo: " + loAddr);
		} catch (SocketException e) {
			// e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Port " + UDPport
					+ " has been used！");
			System.exit(0);

		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		FriendList friendLists = FriendList.getFriendListInstance();

		Thread th = new Thread(this);
		th.start();
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_CONECT);
		m.setSender(nickname);
		m.setGetter(null);
		m.sex = "boy";
		// print("sendBroadcast");
		udpSendMsgObj(m, broadcastAddress);
		friendLists.setVisible(true);
		friendListThrea = new Thread(friendLists);
		friendListThrea.start();

	}

	public static void stopFriThrea() {
		if (friendListThrea != null)
			friendListThrea.interrupt();
	}

	public static void udpSendMsgObj(MsgObj m, InetAddress addr) {

		// if(ts.length() == 0) return;

		try {
			byte[] by = new byte[1024 * 1024];
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream bo = new ObjectOutputStream(bs);
			bo.writeObject(m);
			by = bs.toByteArray();
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

		udpSendMsgObj(m, uc.clientIp);

	}

	public static void closeUdpSock() {
		if (null != serverSK) {
			serverSK.close();
			serverSK = null;
		}
	}

	public static void print(Object o) {

		System.out.println(o);
	}

	@Override
	public void run() {
		// print("start recv");
		while (noExit == true) {
			byte[] by = new byte[1024 * 1024];
			// try {
			DatagramPacket dp = new DatagramPacket(by, by.length);
			try {
				serverSK.receive(dp);
				// if(dp.getAddress().toString().equals(MainManage.getUdpAddr().toString())
				// == true ||
				// true == dp.getAddress().toString().equals(loAddr.toString())
				// ||true == dp.getAddress().equals(MainManage.getUdpAddr())
				// ||true == dp.getAddress().equals(loAddr)) {
				// print("seft msg " + dp.getAddress());
				// continue;
				// }
				print("recv one pk from ip " + dp.getAddress());
				new Thread(new UdpMsgParseThread(dp)).start();

			} catch (IOException e) {
				// e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Network is down");
				// System.exit(1);
			}

		}
		serverSK.close();
	}
}
