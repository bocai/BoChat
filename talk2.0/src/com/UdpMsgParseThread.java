package com;

import com.interfaces.*;
import com.manage.ChatBoxManage;
import com.manage.UDPClientManage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import javax.swing.JLabel;

import view.ChatBox;
import view.FriendList;

// 解析UDP消息
public class UdpMsgParseThread implements Runnable {
	// String ipStr;
	String Mynickname;
	DatagramPacket dp;
	String addrStr;
	private int port;
	UdpClient uc = null;

	UdpMsgParseThread(DatagramPacket dp) {
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
		// print("responseMsg");
	}

	void dealConnect(MsgObj m) {

		uc = new UdpClient();

		// uc.usrIpStr = dp.getAddress().toString();
		uc.isOnLine = true;
		uc.isRead = true;
		uc.nickName = new String(m.getSender());
		uc.sex = m.sex;
		uc.clientIp = dp.getAddress();

		JLabel jl = FriendList.getFriendListInstance().addrFriend(uc);
		ChatBoxManage.addHmLabel(addrStr, jl); // to del JLabel
		UDPClientManage.addHmJlb(jl, uc); // to visit ChatBox
		UDPClientManage.addUdpClient(addrStr, uc); // to del UdpClient
		print("deal end; udpClient size " + UDPClientManage.hmJlb.size());
	}

	@Override
	public void run() {

		ByteArrayInputStream bas = new ByteArrayInputStream(dp.getData());
		ObjectInputStream ois;
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

				UdpClient uct = UDPClientManage.getUdpClient(addrStr);
				if (null == uct)
					return;
				ChatBox cb = uct.chatBox;

				if (null == cb) {

					cb = new ChatBox(uct);
					cb.setClientIp(uct.clientIp);
					uct.chatBox = cb;
					ChatBoxManage.addBoxByIp(addrStr, cb);
					// ChatBoxManage.addBoxByJLabel(jlb, cb);
					if (false == cb.isVisible())
						uc.isRead = false;
					print("can not find chatBox");
				}
				if (cb.isVisible() == false)
					uct.isRead = false;
				cb.appendTarea(m.getSender() + " say: " + m.getContent());
				print(m.getSender() + " say: " + m.getContent() + " from ("
						+ addrStr + ")");
				break;

			case MsgType.MSG_ONLINE:

				print("Online msg");

				break;
			case MsgType.MSG_CONECT:
				// connect
				print("recv connect msg");
				// 处理connect
				if (uc == null)
					dealConnect(m);
				// cb.showWindow();
				responseMsg(m);
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

				break;
			}
			// print("sender " + m.getSender());
			// System.out.println(Arrays.toString(by));
			print("parse end! chatbox size " + ChatBoxManage.hmsc.size());
			print("-----------------------------------");
			dp = null;
		}
	}

}
