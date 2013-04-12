package com;

import com.interfaces.*;
import com.manage.ChatBoxManage;
import com.manage.MainManage;
import com.manage.TCPClientManage;
import com.manage.UDPClientManage;
import view.ChatBox;
import view.FriendList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

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

	void print(Object o) {
		System.out.println(o);
	}

	void responseMsg(MsgObj m) {
		m.setSender(MainManage.nickname);
		// m.setSender(m.getGetter());
		m.setMsgtype(MsgType.MSG_RESPONSE);// 回应

		MainManage.udpSendMsgObj(m, dp.getAddress());
	}

	void dealConnect(MsgObj m) {

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

		print("dealConnect end; udpClient size " + UDPClientManage.hmJlb.size());
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

		if (m.getGetter() == null
				|| true == MainManage.nickname.equalsIgnoreCase(m.getGetter())) {
			addrStr = dp.getAddress().toString();

			uc = UDPClientManage.getUdpClient(addrStr);

			switch (m.getMsgtype()) {
			case MsgType.MSG_NORMAL:
				print("Normal Msg");

				if (null == uc)
					return;
				ChatBox cb = uc.chatBox;
				// firstRecv()
				if (null == cb) { // first recv normal msg
					cb = createBox(cb);

					// print("can not find chatBox in hashMap");
				}
				if (cb.isVisible() == false) {
					uc.setRead(false);
					// MainManage.getFriendListThread().resumeTr();
					FriendList.getFriendListInstance().resumeTr();
				}
				cb.appendTarea(m.getSender() + " say: " + m.getContent());
				// print(m.getSender() + " say: " +
				// m.getContent()+" from ("+addrStr+")");
				break;

			case MsgType.MSG_ONLINE:

				print("Online msg");

				break;

			case MsgType.MSG_CONNECT:
				// connect
				print("recv connect msg");
				// 处理connect
				if (uc == null) {
					dealConnect(m);
				}
				responseMsg(m);// cb.showWindow();
				break;

			case MsgType.MSG_RESPONSE:
				print("recv response msg");
				if (uc == null)
					dealConnect(m);
				break;

			case MsgType.MSG_OFFLINE:
				print("recv OffLineMsg");
				JLabel jl = ChatBoxManage.getHmLabel(addrStr);
				FriendList.getFriendListInstance().delLabel(jl);

				ChatBoxManage.delBoxByIp(addrStr);
				UDPClientManage.delUdpClient(addrStr);
				UDPClientManage.delUdpClientbByJlabel(jl);

				break;
			case MsgType.MSG_TCP_FILE:
				print("recv MSG_TCP_FILE");
				
				if (null == uc)
					return;
				int ret = JOptionPane.showConfirmDialog(null, "确认接收文件吗?",
						"确认框", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {

					print("I accpet recv");
					MainManage.sendConfirmFileMsg(uc, m.getContent());

				} else if (ret == JOptionPane.NO_OPTION) {
					print("I refuse recv");
					MainManage.sendConfirmFileMsg(uc,
							(Integer.parseInt(m.getContent()) + 1) + "");
				}

				break;

			case MsgType.MSG_TCP_CONFIRM:
				print("recv MSG_TCP_CONFIRM");
				if (uc == null) {
					return;
				}
				TcpClient tcpClient = TCPClientManage.getTCPClient(addrStr);
				if (null == tcpClient) {
					return;
				}

				if (m.getContent().equalsIgnoreCase(tcpClient.getRecvConfirm()) == true) {
					
					tcpClient.connect();

					try {
						//print("sleep wait a min");
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return;
						//e.printStackTrace();
					}
				
					tcpClient.sendFiles();
					print("send files end!");
				} else {
					print("he refuse files req");
				}

				break;
			}

			// print("sender " + m.getSender());
			// print(Arrays.toString(by));
			// print("parse end! chatbox size " + ChatBoxManage.hmsc.size());
			// print("-----------------------------------");
			dp = null;
		}
	}

	private ChatBox createBox(ChatBox cb) {
		cb = ChatBox.getChatBox(uc);
		cb.setClientAddr(uc.clientAddr);
		uc.chatBox = cb;
		ChatBoxManage.addBoxByIp(addrStr, cb);

		// print("can not find chatBox in hashMap");
		return cb;
	}
}
