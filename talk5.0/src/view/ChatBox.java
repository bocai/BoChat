package view;

import com.*;
import com.interfaces.MsgType;
import com.manage.MainManage;
import com.manage.TCPClientManage;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

// client chatting window
public class ChatBox extends JFrame implements MouseListener {

	private static final long serialVersionUID = 1L;
	protected InetAddress clientAddr;
	public UdpClient udpClient = null;

	JButton sendBtn = null;
	JButton sendFileBtn = null;
	JPanel jpSouth = null;

	TextArea tArea = new TextArea();
	TextField tField = new TextField(50);
	public TcpClient tcpClient = null;

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
		setTitle("Chating with " + udpClient.nickName);
	//	sendBtn = this.getImageBtn();
		if(null == sendBtn) {
			sendBtn = new JButton("send");
		}
		//sendBtn.setBackground(Color.blue);
		// sendBtn = getImageBtn();

		// sendBtn.setMargin(new Insets(0,0,0,0));
		// sendBtn.setContentAreaFilled(false); //透明
		// sendBtn.setBorder(BorderFactory.createRaisedBevelBorder());
		// //设置凸起来的按钮
		sendBtn.addMouseListener(this);
		sendFileBtn = new JButton("File");
		sendFileBtn.addMouseListener(this);

		jpSouth = new JPanel();
		jpSouth.add(tField, JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendBtn);// , JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendFileBtn);// , JPanel.LEFT_ALIGNMENT);
		tField.setLocation(0, 0);
		// tArea.setBounds(2, 2, 200, 250);
		// tArea.setLocation(2, 2);
		this.add(tArea);
		// this.add(tArea,"Center");
		this.add(jpSouth, "South");

		//
		tField.addActionListener(new txtListener());// Enter key press
		tField.setFocusable(true);
		// tField.requestFocus();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				setVisible(false);
				// System.exit(0);
			}
		});

		setSize(600, 400);// setBounds(400, 300, 600, 400);

	}

	private JButton getImageBtn() {
		JButton btn = null;
		ImageIcon btn_sendIcn = new ImageIcon("image/btn_send.png");
		if (btn_sendIcn != null) {

			btn = new JButton(btn_sendIcn);
			btn.setPreferredSize(new Dimension(btn_sendIcn.getIconWidth(),
					btn_sendIcn.getIconHeight()));
		}

		return btn;
	}

	public InetAddress getClientAddr() {
		return clientAddr;
	}

	public void setClientAddr(InetAddress clientIp) {
		this.clientAddr = clientIp;
	}

	protected void postMsg() {
		String str = tField.getText();
		if (str == null || str.length() <= 0)
			return;
		appendTarea("(" + MainManage.getNickname() + ") : " 
			+ System.getProperty("line.separator") + str);

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

	private void sendStr(String ts) {

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
			DatagramPacket data = new DatagramPacket(by, by.length, clientAddr,
					udpClient.destPort);
			ds.send(data);
			data = null;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == sendBtn) {
			postMsg();
		} 
		else if (e.getSource() == sendFileBtn) { // send file
			// send files
			MainManage.print("send file btn clicked");
			if (tcpClient == null) {
				MainManage.print("send file btn clicked first time");
				tcpClient = new TcpClient();
				tcpClient.setChatBox(this);
				TCPClientManage.addTClient(clientAddr.toString(), tcpClient);
			}
			tcpClient.setVisible(true);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
//		String fileName = "haha";
//		FileDialog fileDia = new FileDialog(this, "SAVE FILE", FileDialog.SAVE);
//		fileDia.setDirectory("~/");
//		fileDia.setFile(fileName);
//		fileDia.setVisible(true);
//		
//		File file = null;
//		if(fileDia.getFile() != null)
//			fileName = fileDia.getFile();
//		String dir = fileDia.getDirectory();
//		if(null == dir)
//			dir = "~/";
//		System.out.println("Received File Name = " + fileName);
//		file = new File(dir, fileName);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
