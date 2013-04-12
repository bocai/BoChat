package view;
import com.*;
import com.interfaces.MsgType;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;


public class ChatBox extends JFrame {
	String MyNickname;
	String clientNickName;
	String usrIpStr;
	TextArea tArea = new TextArea();
	TextField tField = new TextField(50);
	
	JButton sendBtn = new JButton("send");
	JButton sendFileBtn = new JButton("SendFile");
	JPanel jpSouth = new JPanel();
	//private static boolean isVisible = false;
	public static void main(String[] args) {
	//		new ChatBox("bobo");
	}
	public ChatBox(String MyNickname) {
		this.MyNickname = MyNickname;
		setTitle(MyNickname);
		
		jpSouth.add(tField, JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendBtn, JPanel.LEFT_ALIGNMENT);
		jpSouth.add(sendFileBtn, JPanel.LEFT_ALIGNMENT);
				
		this.add(tArea,"Center");
		this.add(jpSouth,"South");
		
		tField.addActionListener(new txtListener());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				setVisible(false);
				//System.exit(0);
			}
		});

		sendBtn.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent click) {
				//DatagramSocket dsk = MainManage.getUdpServerSock();
				sendPacker(tField.getText());
				tField.setText("");

			}
		});
		sendFileBtn.addMouseListener(new MouseAdapter() {
				
			public void mouseClicked(MouseEvent click) {

			}
		});
		setBounds(400, 300, 600, 400);
		//setVisible(true);		
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
			sendPacker(tField.getText());
			tField.setText("");
			
		}
	}
	public void appendTarea(String str) {
		tArea.append(str + System.getProperty("line.separator"));
	}
	public void sendPacker(String ts) {
		
		if(ts.length() == 0) return;
		MsgObj m = new MsgObj();
		m.setMsgtype(MsgType.MSG_NORMAL);
		m.setSender(this.MyNickname);
		m.setGetter(this.clientNickName);
		m.setContent(ts);
		try {
			DatagramSocket da=new DatagramSocket();
			byte[] by=new byte[1024*1024];
			ByteArrayOutputStream bs=new ByteArrayOutputStream();
			ObjectOutputStream bo=new ObjectOutputStream(bs);
			bo.writeObject(m);
			by = bs.toByteArray();	
			//System.out.println(new String(by));
			DatagramPacket data=new DatagramPacket(by,by.length,new InetSocketAddress("localhost", 8888) );
			da.send(data);
		} catch (SocketException e) {
			   e.printStackTrace();
		} catch (IOException e) {
			
			   e.printStackTrace();
		}
	}
	
}
