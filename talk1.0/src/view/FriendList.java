package view;

import com.*;
import com.manage.ChatBoxManage;
import java.util.*;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument.Iterator;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class FriendList extends JFrame implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;

	static String owerNickName;

	static JPanel friendPanel;
	static JPanel jpFriList;
	// protected static ArrayList <JLabel> jlFriends;
	static int friendCount;
	static JButton friendsBtn;
	static JScrollPane jspFri;
	static CardLayout cl;

	private static FriendList FLinstance = null;

	// public static void main(String args[]) {
	// //new FriendList("bocai", 30);
	// }
	public String getOwerNickName() {
		return owerNickName;
	}

	public static void setOwerNickName(String owerNickName) {
		FriendList.owerNickName = owerNickName;
		// print("im " + owerNickName);
	}

	private FriendList() {
		// friendCount = friendsCount;

		// print("wo shi " + owerNickName);
		friendPanel = new JPanel(new BorderLayout());
		friendsBtn = new JButton("My Friends");
		friendsBtn.addMouseListener(this);

		jpFriList = new JPanel(new GridLayout(100, 1, 5, 5));
		// jlFriends = new ArrayList <JLabel> ();

		jspFri = new JScrollPane(jpFriList);

		friendPanel.add(friendsBtn, "North");
		friendPanel.add(jspFri, "Center");

		cl = new CardLayout();
		this.setLayout(cl);
		this.add(friendPanel, "1");
		this.setTitle(owerNickName);
		this.setSize(280, 600);
		// this.setBounds(100, 0, 280, 600);
		// this.setVisible(true);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				HashMap<JLabel, ChatBox> map = ChatBoxManage.hmjc;
				java.util.Iterator<Entry<JLabel, ChatBox>> iter = map
						.entrySet().iterator();
				while (iter.hasNext()) {
					java.util.Map.Entry entry = (java.util.Map.Entry) iter
							.next();
					// Object key = entry.getKey();
					ChatBox cb = (ChatBox) entry.getValue();
					print("Send OffMsg to " + cb.clientNickName);
					MainManage.sendOffLineMsg(cb);
				}
				// setVisible(false);
				System.exit(0);
			}
		});
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

		if (uc.sex.equalsIgnoreCase("gril") == true)
			icon = new ImageIcon("image/mm.jpg");
		else
			icon = new ImageIcon("image/qq.gif");
		JLabel jl = new JLabel(uc.nickName, icon, JLabel.LEFT);
		jl.setEnabled(true);
		jl.addMouseListener(this);

		jpFriList.add(jl);
		// jlFriends.add(jl);

		validate();
		repaint();
		friendCount++;
		print("现在有" + friendCount + "个好友");

		return jl;
	}

	public void delLabel(JLabel jl) {
		if (null == jl)
			return;
		// jl.setEnabled(false);
		jpFriList.remove(jl);
		jl = null;
		// jpFriList.validate();
		validate();
		this.repaint();
		setVisible(true);
	}

	public static void print(Object o) {
		// System.out.println(o);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			Object oj = (Object) e.getSource();
			if (oj.equals(friendsBtn))
				return;
			ChatBox cb = ChatBoxManage.getBoxByJLabel((JLabel) oj);
			if (cb == null) {
				print("cb is err");
				return;
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
			 * } print("clicked");
			 */
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
}
