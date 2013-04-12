package com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

//import javax.swing.*;

import javax.swing.JOptionPane;

import view.ChatBox;
import view.FriendList;

import com.interfaces.MsgType;

public class MainManage implements Runnable {
	protected String ownerIP = null; // use ip as id
	static String nickname = null;;

	public static DatagramSocket serverSK = null;
	protected static int UDPport = 8888; // 默认
	boolean noExit = true;
	private InetAddress broadcastAddress = null;
	private static InetAddress UdpAddr = null;
	public static InetAddress loAddr = null;

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
			UdpAddr = InetAddress.getLocalHost();
			loAddr = InetAddress.getByName("127.0.0.1");
			broadcastAddress = InetAddress.getByName("255.255.255.255");
			//print("lo: " + loAddr);
		} catch (SocketException e) {
			// e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Port " + UDPport
					+ " has been used！");
			System.exit(0);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		//
		// e.printStackTrace();
		// }

		FriendList.setOwerNickName(nickname);
		FriendList friendLists = FriendList.getFriendListInstance();
		// friendLists.setTitle(nickname);

		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_CONECT);
		m.setSender(nickname);
		m.setGetter(null);
		m.sex = "boy";
		new Thread(this).start();
		// print("sendBroadcast");
		udpSendMsgObj(m, broadcastAddress);

		friendLists.setVisible(true);
	}

	public static void udpSendMsgObj(MsgObj m, InetAddress addr) {

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
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void sendOffLineMsg(ChatBox cb) {
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_OFFLINE);
		m.setSender(nickname);
		m.setGetter(cb.clientNickName);
		m.setContent(null);

		udpSendMsgObj(m, cb.getUsrIp());

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
				serverSK.receive(dp); // wait for datagramPacket

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
				e.printStackTrace();
			}
		}
		if(serverSK != null) {
			serverSK.close();
		}
	}
}
