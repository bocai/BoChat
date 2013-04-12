package view;
import com.*;
import com.manage.ChatBoxManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FriendList extends JFrame implements ActionListener,MouseListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static String owerNickName;

	static JPanel jpFriend;
	static JPanel jpFriList;
	protected static ArrayList <JLabel> jlFriends;
	static int friendCount;
	static JButton friendsBtn;
	static JScrollPane jspFri;
	static CardLayout cl;
	
	private static FriendList FLinstance = new FriendList();
//	public static void main(String args[]) {
//		//new FriendList("bocai", 30);
//	}
	public String getOwerNickName() {
		return owerNickName;
	}

	public static void setOwerNickName(String owerNickName) {
		FriendList.owerNickName = owerNickName;
		print("im " + owerNickName);
	}
	private FriendList() {
		//friendCount = friendsCount;
		
		//print("wo shi " + owerNickName);
		jpFriend = new JPanel(new BorderLayout());
		friendsBtn = new JButton("My Friends");
		friendsBtn.addMouseListener(this);
		
		jpFriList = new JPanel(new GridLayout(100, 1, 4, 4));
		jlFriends = new ArrayList <JLabel> ();	
		
		jspFri = new JScrollPane(jpFriList);
		
		jpFriend.add(friendsBtn, "North");
		jpFriend.add(jspFri, "Center");
			
		cl = new CardLayout();
		this.setLayout(cl);
		this.add(jpFriend,"1");
		//this.setTitle(owerNickName);
		this.setBounds(100, 0, 280, 600);
		//this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				//setVisible(false);
				System.exit(0);
			}
		});
	}
	public static FriendList getFriendListInstance() {
		return FLinstance;
	}
	public static JLabel addrFriend(UdpClient uc, FriendList fl) {
		ImageIcon icon;
		if(uc == null || fl == null) {
			print("null Exp");
		}
			
		if(uc.sex.equalsIgnoreCase("boy"))
			icon = new ImageIcon("image/mm.jpg");
		else 
			icon = new ImageIcon("image/qq.gif");
		JLabel jl = new JLabel(uc.nickName,icon,JLabel.LEFT);
		jl.setEnabled(true);
		jl.addMouseListener(fl);
		jlFriends.add(jl);
		jpFriList.add(jl);
		friendCount++;
		print("现在有" + friendCount + "个好友");
		
		return jl;
	}
	public void updateList() {
		// clean old
		for(int i = 0; i < friendCount; i++) {
		}
		jlFriends = null;
		// update
		

	}
	public static void print(Object o) {
		System.out.println(o);
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2) {
			Object oj = (Object)e.getSource();
			if(oj.equals(friendsBtn))
					return;
			ChatBox cb = ChatBoxManage.getBoxByJLabel((JLabel)oj);
			if(cb == null ) {
				print("cb is err");
				return;
			}
			cb.setVisible(true);
			
		}
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == friendsBtn) {
				//updateList();
				System.out.println("clicked");
		}	
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		if(e.getSource()!= friendsBtn) {
			JLabel jl=(JLabel)e.getSource();
			jl.setForeground(Color.blue);
		}
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		if(e.getSource()!= friendsBtn) {
			JLabel jl=(JLabel)e.getSource();
			jl.setForeground(Color.black);
			
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {

	}
}
