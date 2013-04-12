package view;

import com.*;
import com.interfaces.MsgType;
import com.manage.MainManage;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

// client chatting window
public class ChatBox extends JFrame {

	private static final long serialVersionUID = 1L;
	protected InetAddress clientIp;
	public UdpClient udpClient = null;

	JButton sendBtn = null;
	JButton sendFileBtn = new JButton("File");
	JPanel jpSouth = new JPanel();

	TextArea tArea = new TextArea();
	TextField tField = new TextField(50);

	// public static void main(String[] args) {
	// // new ChatBox("bobo");
	// }

	public static ChatBox getChatBox(UdpClient udpClient) {
		if (udpClient == null)
			return null;
		else
			return new ChatBox(udpClient);
	}

	private ChatBox(UdpClient udpClient) {

		this.udpClient = udpClient;
		// tArea.setEditable(false);
		setTitle("chating with " + udpClient.nickName);

		ImageIcon btn_sendIcn = new ImageIcon("image/btn_send.png");
		if (btn_sendIcn != null) {

			sendBtn = new JButton(btn_sendIcn);
			sendBtn.setPreferredSize(new Dimension(btn_sendIcn.getIconWidth(),
					btn_sendIcn.getIconHeight()));
		} else {
			MainManage.print("sendbtn == null");
			sendBtn = new JButton("sendMsg");
		}
		// sendBtn.setMargin(new Insets(0,0,0,0));
		// sendBtn.setContentAreaFilled(false); //透明
		// sendBtn.setBorder(BorderFactory.createRaisedBevelBorder());
		// //设置凸起来的按钮

		jpSouth.add(tField, JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendBtn);// , JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendFileBtn);// , JPanel.LEFT_ALIGNMENT);
		tField.setLocation(0, 0);
		// tArea.setBounds(2, 2, 400, 300);
		// tArea.setLocation(2, 2);
		this.add(tArea, "Center");
		this.add(jpSouth, "South");

		//
		tField.addActionListener(new txtListener());
		tField.setFocusable(true);
		// tField.requestFocus();

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
				// send files
			}
		});

		setSize(600, 400);// setBounds(400, 300, 600, 400);
		// pack();
	}

	public InetAddress getClientIp() {
		return clientIp;
	}

	public void setClientIp(InetAddress clientIp) {
		this.clientIp = clientIp;
	}

	protected void postMsg() {
		String str = tField.getText();
		if (str == null || str.length() <= 0)
			return;
		appendTarea("(" + MainManage.getNickname() + ") : " + str);

		sendStr(str);
		tField.setText(""); // tField.setFocusable(true);
		tField.requestFocus();

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

	public void sendStr(String ts) {

		if (null == ts || ts.length() == 0)
			return;
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_NORMAL);
		m.setSender(MainManage.getNickname());
		m.setGetter(udpClient.nickName);
		m.setContent(ts);

		try {

			DatagramSocket ds = MainManage.getUdpServerSock();
			byte[] by = new byte[1024 * 1024];
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream bo = new ObjectOutputStream(bs);
			bo.writeObject(m);
			by = bs.toByteArray();
			// System.out.println(new String(by));
			DatagramPacket data = new DatagramPacket(by, by.length, clientIp,
					udpClient.destPort);
			ds.send(data);
			data = null;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
