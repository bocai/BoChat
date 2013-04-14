package com;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.manage.MainManage;
import com.manage.TCPClientManage;
import com.manage.UDPClientManage;

import view.ChatBox;

public class TcpClient extends JFrame implements Runnable, ActionListener, MouseListener {
	public ChatBox cb = null; // depends window
	private Socket sk = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;

	private JPanel jpFrame = null;
	private JPanel jpFileList = null;
	private JScrollPane jspList = null;
	private JButton jbtnSelect = null;
	private JButton jbtnRemove = null; // 移除
	private JButton jbtnSend = null;
	private JPanel jpSouth;
	
	public Thread recvThread = null;
	private String recvConfirm = null;
	private boolean isRunning = false;

	short checkboxSz = 0;
	short FILE_MAX_NUM = 10;
	Checkbox checkBoxs[] = new Checkbox[FILE_MAX_NUM];
	File files[] = new File[FILE_MAX_NUM];

	// public static void main(String[] args) {
	// new TcpClient();
	// }

	public TcpClient() {

		launch();
		isRunning = false;
	}

	public TcpClient(Socket s) {
		sk = s;
		initStream();
		launch();
		isRunning = false;
	}

	private void initStream() {
		MainManage.print("initStream");
		if (sk != null && null == dos) {
			try {
				dos = new DataOutputStream(sk.getOutputStream());
				//System.out.println("dos new");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		if (sk != null && null == dis) {
			try {
				dis = new DataInputStream(sk.getInputStream());
				//System.out.println("dis new");
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	private void launch() {
		setTitle("send files");
		// jpFrame = new JPanel(new CardLayout());
		jpSouth = new JPanel(new FlowLayout());
		jpFileList = new JPanel(new GridLayout(FILE_MAX_NUM, 1));// , 8, 8));
		jspList = new JScrollPane(jpFileList);
		jspList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jspList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// jspList.setSize(500, 350);

		jbtnSelect = new JButton("select");
		jbtnSelect.addMouseListener(this);
		jbtnRemove = new JButton("remove");
		jbtnRemove.addMouseListener(this);
		jbtnSend = new JButton("send");
		jbtnSend.addMouseListener(this);

		jpSouth.add(jbtnSelect);
		jpSouth.add(jbtnRemove);
		jpSouth.add(jbtnSend);

		add(jspList);// , "Center");
		add(jpSouth, "South");

		setSize(500, 400);
		this.setResizable(false); // 不可调整大小
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				ShowWindow(false);
				//MainManage.print("hide");
				// System.exit(0);
			}
		});

		// setVisible(true);

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		// String fileName = null;
		if (e.getSource() == jbtnSelect) {
			// select file
			MainManage.print("select file");

			JFileChooser fileChooser = new JFileChooser();// 创建文件选择器
			// fileChooser.setMultiSelectionEnabled(true);

			int flag = fileChooser.showOpenDialog(null);// 显示打开对话框
			File file = null;

			if (flag == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile(); // 获取File对象
			} else {
				return;
			}

			// MainManage.print(file.getAbsolutePath());

			if (checkboxSz >= checkBoxs.length)
				return;

			Checkbox ck = new Checkbox(file.getAbsolutePath());
			ck.setState(true);
			MainManage.print(ck.getLabel());
			jpFileList.add(ck, Checkbox.LEFT_ALIGNMENT);
			checkBoxs[checkboxSz] = ck;
			files[checkboxSz] = file;
			checkboxSz++;
			// jpFileList.validate();//jpFileList.updateUI();
			validate();
			jpFileList.repaint();
			// repaint();

		} else if (e.getSource() == jbtnRemove) {
			MainManage.print("remove...");

			for (int i = 0; i < checkBoxs.length; i++) {
				Checkbox ckb = checkBoxs[i];
				if (ckb != null && ckb.getState()) {
					jpFileList.remove(ckb);
					checkBoxs[i] = null;
					files[i] = null;
					// validate();
					jpFileList.revalidate();

					jpFileList.updateUI();//
					jpFileList.repaint();
					// repaint();
				}
			}
		} else if (e.getSource() == jbtnSend) {
			String ipStr = cb.getClientAddr().toString();
			ipStr = ipStr.substring(1);
			if(ipStr.equals(MainManage.getHostIp())== true ) {
					MainManage.print("can not send to self");
				return;
			}
		
		
			MainManage.print("send files reqMsg ...");
			int i = 0;
			for (; i < checkBoxs.length; i++) {
				Checkbox ckb = checkBoxs[i];
				if (ckb != null)
					break;
			}
			if (i >= checkBoxs.length) { // no file to send
				return;
			}
			Random rnd = new Random();
			recvConfirm = rnd.nextInt() + "";
			// jbtnSend.setEnabled(false);
			MainManage.reqSendFileMsg(cb.udpClient, recvConfirm);
		}
		// System.out.println("selectFile");

	}

	public String getRecvConfirm() {
		return recvConfirm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	private void ShowWindow(boolean show) {
		setVisible(show);
	}

	public Socket getSk() {
		return sk;
	}

	public void setSk(Socket s) {
		if (null == sk) {
			this.sk = s;
			// MainManage.print("sk = " + s);
		}

		initStream();
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public ChatBox getChatBox() {
		return cb;
	}

	public void setChatBox(ChatBox chatBox) {
		cb = chatBox;
	}

public Socket connect() {
		if (sk != null)
			return sk;
		
		try {
			String ipStr = cb.getClientAddr().toString();
			ipStr = ipStr.substring(1);
			if(ipStr.equals(MainManage.getUdpAddr().toString()) == true) {
				MainManage.print("can not connect self" + ipStr);
				return null;
			}
			sk = new Socket(ipStr, TcpServ.getPort());
		} catch (UnknownHostException e) {
	
			//e.printStackTrace();
			return null;
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
		initStream();
		recvThread = new Thread(this);
		recvThread.start();
		TCPClientManage.addTClient(sk.getInetAddress().toString(), this);
		return sk;
}

	public synchronized void sendFiles() {
		// connect();
		if (null == sk) {
			MainManage.print("sk == null,send file fail");
			return;
		}

		for (int i = 0; i < checkBoxs.length; i++) {
			Checkbox ckb = checkBoxs[i];
			if (ckb != null && ckb.getState()) {
				File file = files[i];
				try {
					sendFile(file);
					jpFileList.remove(ckb);
					checkBoxs[i] = null;
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}
	}

	public void sendFile(File selectedFile) throws Exception {
		if (dos == null) {
			System.out.println("sorry dos = null");
			return;
		}
		// Get the size of the file
		long length = selectedFile.length();
		MainManage.print("file len: " + length);
		if (length > Integer.MAX_VALUE) {
			throw new IOException("Could not completely read file "
					+ selectedFile.getName() + " as it is too long (" + length
					+ " bytes, max supported " + Integer.MAX_VALUE + ")");
		}

		// Create the byte array to hold the file data

		// now we start to send the file meta info.

		dos.writeUTF(selectedFile.getName());
		dos.flush();
		dos.writeLong(length);
		dos.flush();

		// end comment
		DataInputStream fis = new DataInputStream(new FileInputStream(
				selectedFile));

		byte[] bytes = new byte[2048];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		int fsize = (int) length;
		while (offset < fsize
				&& (numRead = fis.read(bytes, 0, bytes.length)) >= 0) {
			// pData.setData(bytes, numRead);
			dos.write(bytes, 0, numRead);
			dos.flush();
			offset += numRead;
			float precent = 100.0f * ((float) offset) / ((float) fsize);
			MainManage.print("send " + precent + "%");
			// setProgress((int)precent);
		}
		dos.flush();
		System.out.println("total send bytes = " + offset);
		// Ensure all the bytes have been read in
		if (offset < fsize) {
			throw new IOException("Could not completely transfer file "
					+ selectedFile.getName());
		}
		fis.close();
	}

	public void close() {
		try {
			if (sk != null) {
				sk.close();
				sk = null;
			}
			if (dis != null) {
				dis.close();
				dis = null;
			}
			if (dos != null) {
				dos.close();
				dos = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getThrIsRunning() {
		return isRunning;
	}

	@Override
	public synchronized void run() {
		if (dis == null)
			return;
		MainManage.print("TcpClient recv start:");
		isRunning = true;
		String fileName = null;
		long fileLength = 0;
		while (isRunning) {

			try {
				fileName = dis.readUTF();

				fileLength = (int) dis.readLong(); // number of total bytes
			} catch (IOException e) {
				continue;
				// e.printStackTrace();
			}
			System.out.println("Received File Name = " + fileName);
			System.out.println("Received File size = " + fileLength / 1024
					+ "KB");

			File file = new File(fileName);
			BufferedOutputStream output = null;
			try {
				output = new BufferedOutputStream(new FileOutputStream(file));
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

			// start to receive the content of the file and write them
			byte[] content = new byte[2048];
			long offset = 0;
			int numReadBytes = 0;
			while (offset < fileLength) {
				try {
					numReadBytes = dis.read(content);
				} catch (IOException e) {

					e.printStackTrace();
				}

				if (numReadBytes > 0) {
					try {
						output.write(content, 0, numReadBytes);
					} catch (IOException e) {

						e.printStackTrace();
					}
					offset += numReadBytes;
					float precent = 100.0f * ((float) offset)
							/ ((float) fileLength);
					MainManage.print("recv " + precent + "%");
					// setProgress((int) precent);
				}

			}
			// System.out.println("numReadBytes = " + numReadBytes);
			System.out.println("offset = " + offset);
			if (offset < fileLength) {
				try {
					numReadBytes = dis.read(content);
				} catch (IOException e) {

					e.printStackTrace();
				}
				System.out.println("numReadBytes = " + numReadBytes);
				System.out.println("File content error at server side");
			} else {
				System.out.println("File Receive Task has done correctly");
			}

			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				output = null;
			}

		}
		close();
		MainManage.print("recv thread end");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

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
