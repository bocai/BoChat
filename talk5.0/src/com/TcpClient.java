package com;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FileDialog;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitorInputStream;

import com.manage.MainManage;
import com.manage.TCPClientManage;
import com.manage.UDPClientManage;

import view.ChatBox;
// for translate files
public class TcpClient extends JFrame implements ActionListener, MouseListener {
	public ChatBox cb = null; // depends window
//	private JPanel jpFrame = null;
	private JPanel jpFileList = null;
	private JScrollPane jspList = null;
	private JButton jbtnSelect = null;
	private JButton jbtnRemove = null; // 移除
	private JButton jbtnSend = null;
	private JPanel  jpSouth;
	
	private short 	checkboxNum = 0;
	private short 	FILE_MAX_NUM = 10;
	private Checkbox checkBoxs[] = null;
	private String[] sendFileNames = null;
	private File[] filesTosSend = null;
	public  File[]  filesToRecv = null;
	
	//private boolean waiting = true;
	// public static void main(String[] args) {
	// new TcpClient();
	// }

	public TcpClient() {

		launch();
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
		sendFileNames = new String[FILE_MAX_NUM];
		checkBoxs = new Checkbox[FILE_MAX_NUM];
		filesTosSend = new File[FILE_MAX_NUM];
		filesToRecv = new File[FILE_MAX_NUM];
		// setVisible(true);

	}
	
	private void JpFileListAddBar(JProgressBar jpbar){
		jpFileList.add(jpbar);
		this.validate();
		this.repaint();
	}
	private void JpFileListRemoveBar(JProgressBar jpbar){
		jpFileList.remove(jpbar);
		this.validate();
		this.repaint();
		//this.setVisible(true);
	}
	public String getFileName(int i) {
		if(i < 0 || i >= FILE_MAX_NUM)
			return null;
		return sendFileNames[i];
		
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
			} 
			else {
				return;
			}

			// MainManage.print(file.getAbsolutePath());

			if (checkboxNum >= checkBoxs.length)
				return;

			Checkbox ck = new Checkbox(file.getAbsolutePath());
			ck.setState(true);
			MainManage.print(ck.getLabel());
			jpFileList.add(ck, Checkbox.LEFT_ALIGNMENT);
			checkBoxs[checkboxNum] = ck;
			filesTosSend[checkboxNum] = file;
			sendFileNames[checkboxNum] = file.getName();
			checkboxNum++;
			// jpFileList.validate();//jpFileList.updateUI();
			validate();
			jpFileList.repaint();
			 repaint();

		} else if (e.getSource() == jbtnRemove) {
			MainManage.print("remove...");

			for (int i = 0; i < checkBoxs.length; i++) {
				Checkbox ckb = checkBoxs[i];
				if (ckb != null && ckb.getState()) {
					jpFileList.remove(ckb);
					checkBoxs[i] = null;
					filesTosSend[i] = null;
					sendFileNames[i] = null; 
					checkboxNum--;
					validate();
					//jpFileList.revalidate();

					//jpFileList.updateUI();//
					//jpFileList.repaint();
					 repaint();
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
				if(ckb != null && ckb.getState()) {
					MainManage.reqSendFileMsg(cb.udpClient, sendFileNames[i] + ":" + i);
				}
				//if (ckb != null)
				//	break;
			}
			
		}
		// System.out.println("selectFile");
		print("file count " + checkboxNum);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	private void ShowWindow(boolean show) {
		setVisible(show);
	}

	public ChatBox getChatBox() {
		return cb;
	}

	public void setChatBox(ChatBox chatBox) {
		cb = chatBox;
	}

	public Socket connectAndSendFile(UdpClient uc, int i) {
			
		if(uc == null ) 
			return null;
		if(i < 0 || i > FILE_MAX_NUM) 
			return null;
		Socket sk = null;
		
		try {
			String ipStr = uc.clientAddr.toString();
			
			if(ipStr == null) return null;

			if(ipStr.equals(MainManage.getUdpAddr().toString()) == true) {
				print("can not connect self" + ipStr);
				return null;
			}
			ipStr = ipStr.substring(1);
			
			sk = new Socket(ipStr, TcpServ.getPort());
			
//			try {
//			//print("sleep wait a min");
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			return;
//			//e.printStackTrace();
//		}
			sendFileImpl sender = new sendFileImpl(sk, i);
			print("send files start");
			new Thread(sender).start();
			
		} catch (UnknownHostException e) {
	
			//e.printStackTrace();
			return null;
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		}
		
	//	TCPClientManage.addTClient(sk.getInetAddress().toString(), this);
		return sk;
	}

	public File getFilesRecv(int i) {
		if(i < 0 || i >= FILE_MAX_NUM) 
			return null;
		return filesToRecv[i];
	}

	public boolean setFilesRecv(String fileName, int i) {
		if(i < 0 || i >= FILE_MAX_NUM) 
			return false;
		
		filesToRecv[i] = getSavePath(fileName);
		return true;
	}
	
	private File getSavePath(String fileName) {
	
		//print("Save oooo");
		FileDialog fileDia = new FileDialog(this, "SAVE FILE", FileDialog.SAVE);
		fileDia.setDirectory("~"+File.separator);
		fileDia.setFile(fileName);
		fileDia.setVisible(true);
		
		File file = null;
		if(fileDia.getFile() != null)
			fileName = fileDia.getFile();
		String dir = fileDia.getDirectory();
		if(null == dir)
			dir = "~" + File.separator;
		print("i will Received " + fileName);
		file = new File(dir, fileName);
		
		return file;
	}
	
	public recvImpl getRecvImpl(Socket sk) {
		return new recvImpl(sk);
	}

	class recvImpl implements Runnable {
	
		Socket sket = null;
		DataInputStream dis = null;
		
		File fileToRecv = null;
		recvImpl(Socket sk) {
			sket = sk;
		}
	
		private void close() {
			try {

				if (dis != null) {
					dis.close();
					dis = null;
				}
				if (sket != null) {
					sket.close();
					sket = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			// 接收文件线程
			try {
				dis = new DataInputStream(sket.getInputStream());
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			if (dis == null) {
				print("dis == null");
				return;
			}

			print("TcpClient recv start:");
			
			long fileLength = 0;
			int i = 0;
			
			print("pre is oky");
			try {
				fileLength = (int) dis.readLong(); // number of total bytes
				print("Received File size  = " + fileLength / 1024 + "KB");
				i = dis.readInt();
			} catch (IOException e) {
				return;
				// e.printStackTrace();
			}	
			print("index = " + i);
			
			if(i < 0 || i > FILE_MAX_NUM) {
				print("error index ");
				return;
			}
			fileToRecv = filesToRecv[i];
			if(fileToRecv == null) 
				return;
			BufferedOutputStream output = null;
			JProgressBar jpbar = new JProgressBar(JProgressBar.HORIZONTAL);
			//jpbar.setBackground(Color.GRAY);
			jpbar.setMaximum(100);
			//jpbar.setString(fileToRecv.getName());
			jpbar.setName("---------");
			jpbar.setBorderPainted(true);
			jpbar.setMinimum(0);
			//jpbar.setVisible(true);
			JpFileListAddBar(jpbar);
		
		
			try {
				output = new BufferedOutputStream(new FileOutputStream(fileToRecv));
				
				
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
					float precent = 100.0f * ((float) offset) / ((float) fileLength);
					print("recv " + precent + "%");
					jpbar.setValue((int)precent);
					// setProgress((int) precent);
				}
				else if(numReadBytes < 0) {
					print("end of file");
					break;
				}
			}
			// System.out.println("numReadBytes = " + numReadBytes);
			
			if (offset < fileLength) {
				try {
					print("left data");
					numReadBytes = dis.read(content);
					if (numReadBytes > 0) {
						try {
							output.write(content, 0, numReadBytes);
						} catch (IOException e) {
	
							e.printStackTrace();
						}
						offset += numReadBytes;
						float precent = 100.0f * ((float) offset) / ((float) fileLength);
						print("recv " + precent + "%");
						jpbar.setValue((int)precent);
					}
				} catch (IOException e) {
	
					e.printStackTrace();
				}
				
				//System.out.println("numReadBytes = " + numReadBytes);
				print("File content error at server side");
			} 
			else {
				print("File Receive Task has done correctly");
			}
			
			System.out.println("total revc bytes = " + offset);
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				output = null;
			}
			
			print("recv thread end");
			
			close();
			JpFileListRemoveBar(jpbar);
		}
	}
	public sendFileImpl sendFileImpl(Socket sk, int i) {
		return new sendFileImpl(sk, i); 
	}
	class sendFileImpl implements Runnable {
		
		Socket sket = null;
		
		DataOutputStream dos = null;
		private int file_index = 0;
		private File fileToSend;
		
		sendFileImpl(Socket sk, int i) {
			this.sket = sk;
			this.file_index = i;

		}
		
		public synchronized void sendFile(File file) throws Exception {
			dos = new DataOutputStream(sket.getOutputStream());
			
			if(null == file || dos == null) 
				return;
			
			synchronized (this) {
				// Get the size of the file
				long length = file.length();
				print("file len: " + length);
				if (length > Integer.MAX_VALUE) {
					throw new IOException("Could not completely read file "
							+ file.getName() + " as it is too long (" + length
							+ " bytes, max supported " + Integer.MAX_VALUE + ")");
				}
				// now we start to send the file meta info.
		
				dos.writeLong(length);
				dos.flush();
				dos.writeInt(file_index);
				dos.flush();
				// end comment
				DataInputStream fis = new DataInputStream(new FileInputStream(file));
		        ProgressMonitorInputStream pm = 
		                  new ProgressMonitorInputStream(TcpClient.this,"Reading a file" + file.getAbsolutePath(),fis);
		              // 读取文件，如果总耗时超过2秒，将会自动弹出一个进度监视窗口。
		              //   显示已读取的百分比。
				byte[] bytes = new byte[2048];
		
				// Read in the bytes
				int offset = 0;
				int numRead = 0;
				int fsize = (int) length;
				while (offset < fsize
						&& (numRead = pm.read(bytes, 0, bytes.length)) >= 0) {
					// pData.setData(bytes, numRead);
					dos.write(bytes, 0, numRead);
					dos.flush();
					offset += numRead;
					float precent = 100.0f * ((float) offset) / ((float) fsize);
					MainManage.print("send " + precent + "%");
					// setProgress((int)precent);
				}
				dos.flush();
				//System.out.println("total send bytes = " + offset);
				// Ensure all the bytes have been read in
				if (offset < fsize) {
					throw new IOException("Could not completely transfer file "
							+ file.getName());
				}
				pm.close();
				fis.close();
			}
			close();
			checkBoxs[file_index].setLabel( file.getName() + " send over!");
		}
		
		private void close() {
			try {
				if (dos != null) {
					dos.close();
					dos = null;
				}				
				if (sket != null) {
					sket.close();
					sket = null;
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			fileToSend = filesTosSend[file_index];
			if(fileToSend == null) 
				return;
			try {
				sendFile(fileToSend);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			close();
		}
	
	}
	
	public void mouseClicked(MouseEvent e) {
	

	}

	public void mousePressed(MouseEvent e) {
	
	}

	public void mouseEntered(MouseEvent e) {
	
		
	}

	public void mouseExited(MouseEvent e) {
	
	}

	void print(Object o) {
		//System.out.println(o);
	}
}


