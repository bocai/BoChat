package com;

import com.interfaces.*;
import com.manage.ChatBoxManage;
import com.manage.MainManage;
import com.manage.UDPClientManage;
import view.ChatBox;
import view.FriendList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import javax.swing.JLabel;

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
		uc.clientIp = dp.getAddress();

		JLabel jl = FriendList.getFriendListInstance().addrFriend(uc);
		ChatBoxManage.addHmLabel(addrStr, jl); // to del JLabel
		UDPClientManage.addHmJlb(jl, uc); // through jl to visit ChatBox
		UDPClientManage.addUdpClient(addrStr, uc); // to del UdpClient
		print("deal end; udpClient size " + UDPClientManage.hmJlb.size());
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

				if (null == cb) { // first recv normal msg
					cb = ChatBox.getChatBox(uc);
					cb.setClientIp(uc.clientIp);
					uc.chatBox = cb;
					ChatBoxManage.addBoxByIp(addrStr, cb);

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

			case MsgType.MSG_CONECT:
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
			}
			print("sender " + m.getSender());
			// print(Arrays.toString(by));
			// print("parse end! chatbox size " + ChatBoxManage.hmsc.size());
			// print("-----------------------------------");
			dp = null;
		}
	}

}
