package textCapture;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

class TextCaptureGUI extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1547412459315380610L;

	private Container con;
	
	private JPanel menu_pn = new JPanel();
	private JPanel textArea_pn = new JPanel();
	
	private JButton start_btn = new JButton("캡쳐모드 활성화");
	private JButton save_btn = new JButton("저장하기(x)");
	private JButton translate_btn = new JButton("번역하기(x)");
	
	static JTextArea textArea = new JTextArea();
	
	private TextCapturePro pro = new TextCaptureProImpl();
	
	public void init(){
		con = this.getContentPane();
		con.setLayout(new BorderLayout());
		
		con.add("South", menu_pn);
		con.add("Center", textArea_pn);
		
		menu_pn.setLayout(new GridLayout(1, 4));
		
		menu_pn.add(start_btn);
		menu_pn.add(new JPanel());
		menu_pn.add(save_btn);
		menu_pn.add(translate_btn);

		start_btn.addActionListener(this);
		save_btn.addActionListener(this);
		translate_btn.addActionListener(this);
		
		textArea_pn.setBorder(BorderFactory.createEmptyBorder(10,10,20,10));
		textArea_pn.setLayout(new BorderLayout());
		textArea_pn.add("Center", textArea);
	}
	
	public void start(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public TextCaptureGUI(String title) {
		super(title);
		
		this.init();
		this.start();
		
		super.setSize(600,600);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int xpos = (int)(screen.getWidth()/2) - this.getWidth()/2;
		int ypos = (int)(screen.getHeight()/2) - this.getHeight()/2;
		super.setLocation(xpos, ypos);
		
		super.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(start_btn == e.getSource()) {
			if(start_btn.getText().equals("캡쳐모드 활성화")) {
				start_btn.setText("캡쳐모드 비활성화");
//				this.setState(JFrame.ICONIFIED);
				pro.captureModeStart();
			}else{
				start_btn.setText("캡쳐모드 활성화");
				pro.captureModeStop();
			}
		}
		if(save_btn == e.getSource()) {
			String fileName = JOptionPane.showInputDialog(null, "저장할 파일 명 : ", "입력", 
								JOptionPane.QUESTION_MESSAGE);
			if(pro.save(fileName)) {
				JOptionPane.showMessageDialog(null, "저장완료", "알림", JOptionPane.INFORMATION_MESSAGE);
			}else {
				JOptionPane.showMessageDialog(null, "저장실패", "알림", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
