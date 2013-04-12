package view;

import com.*;
import com.interfaces.MsgType;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatBox extends JFrame {
	public String MyNickname;
	public String clientNickName;
	protected InetAddress usrIp;

	public InetAddress getUsrIp() {
		return usrIp;
	}

	public void setUsrIp(InetAddress usrIp) {
		this.usrIp = usrIp;
	}

	public int destPort = 8888;
	TextArea tArea = new TextArea();
	TextField tField = new TextField(50);

	JButton sendBtn = new JButton("send");
	JButton sendFileBtn = new JButton("SendFile");
	JPanel jpSouth = new JPanel();

	// private static boolean isVisible = false;

	// public static void main(String[] args) {
	// // new ChatBox("bobo");
	// }
	public ChatBox(String clientNickName) {
		this.clientNickName = clientNickName;
		this.MyNickname = MainManage.getNickname();

		// tArea.setEditable(false);
		setTitle(MyNickname + " chating with " + clientNickName);
		jpSouth.add(tField, JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendBtn, JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendFileBtn, JPanel.LEFT_ALIGNMENT);
		tArea.setBounds(2, 2, 400, 300);
		// tArea.setLocation(2, 2);
		this.add(tArea, "Center");
		this.add(jpSouth, "South");

		tField.addActionListener(new txtListener());
		tField.setFocusable(true);
		tField.requestFocus();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				setVisible(false);
				// System.exit(0);
			}
		});

		sendBtn.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent click) {
				postMsg();

			}
		});
		sendFileBtn.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent click) {

			}
		});

		setSize(600, 400);
		// setBounds(400, 300, 600, 400);

		// pack();
	}

	protected void postMsg() {
		String str = tField.getText();
		if (str == null || str.length() <= 0)
			return;
		appendTarea("(" + MainManage.getNickname() + ") : " + str);

		sendPacker(str);
		tField.setText(""); // tField.setFocusable(true);
		tField.requestFocus();

	}

	public void showWindow() {
		setVisible(true);
	}

	public void hideWindow() {
		setVisible(false);
	}

	// listen Entre
	private class txtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			postMsg();

		}
	}

	public void appendTarea(String str) {
		tArea.append(str + System.getProperty("line.separator"));

	}

	public void sendPacker(String ts) {

		if (ts.length() == 0)
			return;
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_NORMAL);
		m.setSender(this.MyNickname);
		m.setGetter(this.clientNickName);
		m.setContent(ts);
		try {

			DatagramSocket ds = MainManage.getUdpServerSock();
			byte[] by = new byte[1024 * 1024];
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream bo = new ObjectOutputStream(bs);
			bo.writeObject(m);
			by = bs.toByteArray();
			// System.out.println(new String(by));
			DatagramPacket data = new DatagramPacket(by, by.length, usrIp,
					destPort);
			ds.send(data);
			data = null;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
