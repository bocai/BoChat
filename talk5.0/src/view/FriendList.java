package view;

import com.*;
import com.manage.ChatBoxManage;
import com.manage.MainManage;
import com.manage.UDPClientManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class FriendList extends JFrame implements Runnable, ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	static JPanel FramePanel;
	static JPanel gjpFriList;

	static int friendCount;
	static JButton friendsBtn;
	static JScrollPane jspFri;
	static CardLayout cl;

	private static FriendList FLinstance = null;

	private boolean isRunning = true;
	private boolean waiting = false;

	private FriendList() {

		FramePanel = new JPanel(new BorderLayout());
		friendsBtn = new JButton("My Friends");
		friendsBtn.addMouseListener(this);

		gjpFriList = new JPanel(new GridLayout(100, 1, 5, 5));

		jspFri = new JScrollPane(gjpFriList);

		FramePanel.add(friendsBtn, "North");
		FramePanel.add(jspFri, "Center");

		cl = new CardLayout();
		this.setLayout(cl);
		this.add(FramePanel, "1");
		this.setTitle(MainManage.getNickname());
		this.setSize(280, 600);
		// this.setBounds(100, 0, 280, 600);

		this.setResizable(false); // 不可调整大小
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				isRunning = false;

				HashMap<String, UdpClient> map = UDPClientManage.hmSudp;
				Iterator<Entry<String, UdpClient>> iter = map.entrySet()
						.iterator();

				while (iter.hasNext()) {
					Entry<String, UdpClient> entry = iter.next();
					// String addr = entry.getKey();
					UdpClient uc = (UdpClient) entry.getValue();
					// print("Send OffMsg to "+ uc.nickName);
					MainManage.sendOffLineMsg(uc);
				}
				// setVisible(false);
				MainManage.noExit = false;
				// MainManage.stopFriThrea();
				stop();
				MainManage.closeUdpSock();

				print("exit");

				System.exit(0);
			}
		});
		// this.setVisible(true);
	}

	public static FriendList getFriendListInstance() {
		if (FLinstance == null)
			FLinstance = new FriendList();

		return FLinstance;
	}

	public synchronized JLabel addrFriend(UdpClient uc) {
		ImageIcon icon;
		if (uc == null) {
			print("null Expt");
			return null;
		}
		// print("ucSex " + uc.sex);
		String sex = uc.sex;
		if (sex == null)
			sex = "gril";
		if (sex.equalsIgnoreCase("gril") == true)
			icon = new ImageIcon("image/mm.jpg");
		else
			icon = new ImageIcon("image/qq.gif");
		JLabel jl = new JLabel(uc.nickName, icon, JLabel.LEFT);
		if (null == jl)
			System.exit(1);

		jl.setEnabled(true);
		jl.addMouseListener(this);

		gjpFriList.add(jl);

		validate();
		repaint();
		friendCount++;
		print("friendCount" + friendCount + "个好友");

		return jl;
	}

	public void delLabel(JLabel jl) {
		if (null == jl)
			return;

		gjpFriList.remove(jl);
		jl = null;
		friendCount--;

		this.validate();
		this.repaint();
		// setVisible(true);
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {

			Object oj = (Object) e.getSource();
			if (oj.equals(friendsBtn))
				return;

			JLabel jlb = (JLabel) oj;
			UdpClient uct = UDPClientManage.getUdpClientbByJlabel(jlb);
			ChatBox cb = uct.chatBox;
			uct.setRead(true);
			if (cb == null) { // first request chatbox

				cb = ChatBox.getChatBox(uct);
				cb.setClientAddr(uct.clientAddr);
				uct.chatBox = cb;

				ChatBoxManage.addBoxByJLabel(jlb, cb);
				ChatBoxManage.addBoxByIp(uct.clientAddr.toString(), cb);
			}
			cb.setVisible(true);
			cb.tField.requestFocus();
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == friendsBtn) {
			/*
			 * ImageIcon icon = new ImageIcon("image/mm.jpg"); JLabel jl = new
			 * JLabel("hyy",icon,JLabel.LEFT); jl.addMouseListener(this);
			 * //jlFriends.add(jl); jpFriList.add(jl); jl.setEnabled(true);
			 * jpFriList.add(jl); jpFriList.validate(); //this.setVisible(true);
			 */
			/*
			 * HashMap<JLabel, ChatBox> map = ChatBoxManage.hmjc;
			 * java.util.Iterator<Entry<JLabel, ChatBox>> iter =
			 * map.entrySet().iterator(); print("There is ChatBox as follow");
			 * while (iter.hasNext()) { java.util.Map.Entry entry =
			 * (java.util.Map.Entry) iter.next(); //Object key = entry.getKey();
			 * ChatBox cb = (ChatBox) entry.getValue(); print("ChatBox --- "+
			 * cb.clientNickName);
			 * 
			 * }
			 */print("friend btn mouseReleased");

		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() != friendsBtn) {
			JLabel jl = (JLabel) e.getSource();
			jl.setForeground(Color.blue);
		}

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (e.getSource() != friendsBtn) {
			JLabel jl = (JLabel) e.getSource();
			jl.setForeground(Color.black);

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void run() {

		boolean enable = true;

		while (isRunning) {
			HashMap<JLabel, UdpClient> map = UDPClientManage.hmJlb;
			Iterator<Entry<JLabel, UdpClient>> iter = map.entrySet().iterator();
			boolean flag = false;

			while (iter.hasNext()) {
				Entry<JLabel, UdpClient> entry = iter.next();
				JLabel jl = entry.getKey();
				UdpClient uc = (UdpClient) entry.getValue();

				if (uc.isRead() == false) { // 有没查看的消息
					flag = true;

					enable = !enable;
					jl.setEnabled(enable);
				} else {
					jl.setEnabled(true);
				}
			}

			if (false == flag)
				waiting = true;

			synchronized (this) {
				if (!isRunning) {
					break;
				}
				if (waiting) {
					print("suspend friendList,waitting");
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				isRunning = false;
				print("Interrupted Exception");
				// e.printStackTrace();
			}
			// print("isRunning = " + isRunning);
		}

		print("JLabel enable quit");
	}

	/**
	 * 挂起线程
	 */
	public void suspendTr() {
		if (waiting) { // 是挂起状态则直接返回
			return;
		}
		synchronized (this) {
			this.waiting = true;
			print("挂起状态");
		}
	}

	/**
	 * 恢复线程
	 */
	public void resumeTr() {
		if (!waiting) { // 没有挂起则直接返回
			return;
		}
		synchronized (this) {
			this.waiting = false;
			this.notifyAll();
			print("恢复");
		}
	}

	/**
	 * 停止线程
	 */
	public void stop() {
		if (!isRunning) { // 没有运行则直接返回
			return;
		}
		synchronized (this) {
			isRunning = false;
		}
	}

}
