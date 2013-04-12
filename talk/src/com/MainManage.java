package com;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

//import javax.swing.*;

import javax.swing.JOptionPane;

import view.FriendList;

import com.interfaces.MsgType;

public class MainManage implements Runnable {
	protected String ownerIP; //use ip as id
	static String nickname;
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
	public static DatagramSocket serverSK;
	protected static int UDPport = 8888; //默认
	boolean noExit = true; 
	private InetAddress broadcastAddress;
	public static void main(String args[]) {
		new MainManage();
	}
	public static DatagramSocket getUdpServerSock() {
		return serverSK;
	}
	public String getIPstr() {
		return ownerIP;
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
			
			broadcastAddress = InetAddress.getByName("255.255.255.255");
			
		} catch (SocketException e) {
			//e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Port " + UDPport + " has been used！");
            System.exit(0);
			
		}catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}

//        try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			
//			e.printStackTrace();
//		}
        
        new Thread(this).start();
        FriendList friendLists = FriendList.getFriendListInstance();
        FriendList.setOwerNickName(nickname);
        friendLists.setTitle(nickname);
         
        friendLists.setVisible(true);
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_CONECT);
		m.setSender(nickname);
		m.setGetter(null);
		m.sex = "boy";
		print("sendBroadcast");
		udpSendMsgObj(m, broadcastAddress);
	}
	public static void udpSendMsgObj(MsgObj m, InetAddress bstaddr) {
			
			//if(ts.length() == 0) return;
			
			try {
				byte[] by=new byte[1024*1024];
				ByteArrayOutputStream bs=new ByteArrayOutputStream();
				ObjectOutputStream bo=new ObjectOutputStream(bs);
				bo.writeObject(m);
				by = bs.toByteArray();	
				//System.out.println(new String(by));
				DatagramPacket pk=new DatagramPacket(by,by.length,bstaddr,UDPport);
				serverSK.send(pk);
			} catch (SocketException e) {
				   e.printStackTrace();
			} catch (IOException e) {
				
				   e.printStackTrace();
			}
	}
//	public void sendBroadcast(MsgObj m) {
//		
//	}
	public static void print(Object o) {
		System.out.println(o);
	}
	@Override
	public void run() {
		print("start recv");
		while(noExit == true) {
			byte[] by = new byte[1024*1024];	             
			//try {
				DatagramPacket dp = new DatagramPacket(by, by.length);
				try {
					serverSK.receive(dp); print("recv one pk");
					new Thread(new UdpMsgParseThread(dp)).start();
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
//				ByteArrayInputStream bas = new ByteArrayInputStream(dp.getData());
//				ObjectInputStream ois = new ObjectInputStream(bas);
//				MsgObj m = (MsgObj)ois.readObject();
//				
//				System.out.println(m.getSender() + " say: " + m.getContent());
//				//System.out.println(Arrays.toString(by));
//				print(dp.getAddress().toString() + " " + dp.getSocketAddress());
//			} catch (ClassNotFoundException e) {
//					
//					e.printStackTrace();
//					             
//			} catch (SocketException e) {
//				            e.printStackTrace();
//			} catch (IOException e) {
//				           e.printStackTrace();
//	        }
			
		}
		
	}
}
