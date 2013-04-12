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
	short port = 8888;

//	public static void main(String[] args) {
//
//	}

	public TcpServ() {

	}

	public TcpServ(short port) {
		this.port = port;
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

				String inetAddrStr = s.getInetAddress().toString();
				ChatBox cbx = ChatBoxManage.getBoxByIp(inetAddrStr);
				if (null == cbx) {
					UdpClient uc = UDPClientManage.getUdpClient(inetAddrStr);
					if (null == uc) {
						continue;
					}
					cbx = uc.chatBox;
					JLabel jlb = ChatBoxManage.getHmLabel(inetAddrStr);

					if (null == cbx) {
						cbx = ChatBox.getChatBox(uc);
					}
					ChatBoxManage.addBoxByJLabel(jlb, cbx);
					ChatBoxManage.addBoxByIp(inetAddrStr, cbx);
				}

				TcpClient tc = TCPClientManage.getTCPClient(inetAddrStr);
				if (null == tc) {
					MainManage.print("create tc");
					tc = new TcpClient(s);
					cbx.tcpClient = tc;
					tc.cb = cbx;
					cbx.tcpClient = tc;
					tc.recvThread = new Thread(tc);
					tc.recvThread.start();
					TCPClientManage.addTClient(inetAddrStr, tc);
				} else {
					MainManage.print("tc setSk");
					tc.setSk(s);

					if (false == tc.getThrIsRunning()) {
						tc.recvThread = new Thread(tc);
						tc.recvThread.start();
					}
					tc.setVisible(true);
				}

			} catch (IOException e) {
				MainManage.print("he is offline");
				e.printStackTrace();
			} // 阻塞等其他人连接
		}
	}

}
