package com;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import view.ChatBox;

import com.manage.ChatBoxManage;
import com.manage.MainManage;
import com.manage.TCPClientManage;
import com.manage.UDPClientManage;

public class TcpServ implements Runnable {
	ServerSocket ss = null;
	private static int port = 8888;

	public static void main(String[] args) {

	}
	public TcpServ() {
		
	}
	public TcpServ(int port) {
		this.port = port;
	}
	
	public static int getPort() {
		return port;
	}
	@Override
	public void run() {
		try {
			ss = new ServerSocket(port);
		} catch (BindException e) {
			JOptionPane jop = new JOptionPane("The port is in used!");
			jop.showMessageDialog(null, "The port is in used");
			return;
		} catch (IOException e1) {

			e1.printStackTrace();
			return;
		}

		while (true) {
			try {
				Socket s = ss.accept(); // wait...
				if (null == s)
					continue;

//				if(MainManage.getUdpAddr().equals(s.getInetAddress()) == true) 
//					continue;
				String inetAddrStr = s.getInetAddress().toString();
				ChatBox cbx = ChatBoxManage.getBoxByIp(inetAddrStr);
				if (null == cbx) { // first time
					UdpClient uc = UDPClientManage.getUdpClient(inetAddrStr);
					if (null == uc) {
						continue;
					}
					cbx = uc.chatBox;
					JLabel jlb = ChatBoxManage.getHmLabel(inetAddrStr);

					if (null == cbx) {
						cbx = ChatBox.getChatBox(uc);
						uc.chatBox = cbx;
					}
					ChatBoxManage.addBoxByJLabel(jlb, cbx);
					ChatBoxManage.addBoxByIp(inetAddrStr, cbx);
				}

				TcpClient tc = TCPClientManage.getTCPClient(inetAddrStr);
				if (null == tc) {
					MainManage.print("create tc & start");
					tc = new TcpClient();
					cbx.tcpClient = tc;
					tc.cb = cbx;  //关联
					
					TCPClientManage.addTClient(inetAddrStr, tc);
					//tc.recvThread = new Thread(tc);
					//tc.recvThread.start();
					
				} 
				//else {

					tc.setVisible(true);
				//}
				new Thread(tc.getRecvImpl(s)).start();
			} catch (IOException e) {
				MainManage.print("he is offline");
				e.printStackTrace();
			} // 阻塞等其他人连接
		}
	}

}
