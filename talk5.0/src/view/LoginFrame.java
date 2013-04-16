package view;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.manage.MainManage;

public class LoginFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	JPanel jpNor = new JPanel();
	JPanel jpCenter = new JPanel();
	JPanel jpSouth = new JPanel();
	
	JButton yes_btn = new JButton("Ok");
	JButton no_btn   = new JButton("Cancel"); //取消
	JButton quit_btn = new JButton("Quit");
	TextField tFieldName = new TextField(30);
	
	JRadioButton rad_boy = new JRadioButton("Boy");
	JRadioButton rad_gril = new JRadioButton("Gril");
	ButtonGroup btngup = new ButtonGroup();
	JLabel jlName = new JLabel("NAME:");
	JLabel jlPort = new JLabel("port:");
	TextField tFieldPort = new TextField(5);
	String nickName = null;
	String sex = "girl";
	int port = 8888;
	
	public LoginFrame() {
		
		jpNor.add(jlPort);//, JPanel.LEFT_ALIGNMENT);
		tFieldPort.setText("8888");
		jpNor.add(tFieldPort);//, JPanel.LEFT_ALIGNMENT);
		
		jpCenter.add(jlName);
		jpCenter.add(tFieldName);
		
		jpSouth.add(yes_btn);
		jpSouth.add(no_btn);
		jpSouth.add(quit_btn);
		
		btngup.add(rad_boy);
		btngup.add(rad_gril);
		
		jpNor.add(rad_boy);
		jpNor.add(rad_gril);
		rad_gril.setSelected(true);
		
		this.add(jpNor, "North");
		this.add(jpCenter, "Center");
		this.add(jpSouth, "South");
		this.setSize(400, 160);
		
		
		tFieldName.addActionListener(new txtListener());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				System.exit(0);
			}
		});
		yes_btn.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent click) {
				loadInput();
				
				Strar();
			}
		});
		
		no_btn.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent click) {
				
				Strar();
			}
		});
		quit_btn.addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent click) {
				
				System.exit(0);
			}
		});
		//pack();
		this.setVisible(true);
		tFieldName.requestFocus();
	}
	private void loadInput() {
		nickName = tFieldName.getText();
		port = Integer.parseInt(tFieldPort.getText());
		if(rad_boy.isSelected()) {
			sex = "boy";
		}
		else {
			sex = "gril";
		}
		
	}
	
	// listen Entre
	private class txtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			loadInput();
			
			Strar();
			
		}
	}
	private void Strar() {
		setVisible(false);
		new MainManage(nickName, sex, port);
		
	}
	public static void main(String[] args) {
		
		 LoginFrame lg = new LoginFrame();
		 
		
	}

}
