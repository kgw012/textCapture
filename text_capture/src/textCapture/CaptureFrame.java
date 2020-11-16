package textCapture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CaptureFrame extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2286616196187742796L;

    private JLabel lb;

    private BufferedImage capturedImage = null;
    
	public void init(){
        setContentPane(new DrawPanel());
        
        lb = new JLabel("마우스를 드래그하여 캡쳐할 대상을 선택하세요");
        lb.setFont(new Font("", Font.ITALIC, 20));
        
        this.add("North", lb);
        
        addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyCode() == 0) {
					dispose();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
        });
        
	}
	
	public CaptureFrame() {
		
		this.init();
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		super.setSize((int)screen.getWidth(), (int)screen.getHeight());
		super.setResizable(false);
		super.setUndecorated(true);
		super.setBackground(new Color(0,0,0,10));

		super.setVisible(true);
	}
	
	public BufferedImage getCapturedImage() {
		return capturedImage;
	}

	class DrawPanel extends JPanel{

	    /**
		 * 
		 */
		private static final long serialVersionUID = -8069382543276280121L;
		private int x, y, x2, y2;

	    DrawPanel() {
	        x = y = x2 = y2 = 0; // 
	        MyMouseListener mouseListener = new MyMouseListener();
	        addMouseListener(mouseListener);
	        addMouseMotionListener(mouseListener);
	    }

	    public void setStartPoint(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }

	    public void setEndPoint(int x, int y) {
	        x2 = (x);
	        y2 = (y);
	    }

	    public void drawPerfectRect(Graphics g, int x, int y, int x2, int y2) {
	        int px = Math.min(x,x2);
	        int py = Math.min(y,y2);
	        int pw=Math.abs(x-x2);
	        int ph=Math.abs(y-y2);
	        g.drawRect(px, py, pw, ph);
	    }
	    
	    class MyMouseListener extends MouseAdapter {

	        public void mousePressed(MouseEvent e) {
	            setStartPoint(e.getX(), e.getY());
	        }

	        public void mouseDragged(MouseEvent e) {
	            setEndPoint(e.getX(), e.getY());
	            repaint();
	        }

	        public void mouseReleased(MouseEvent e) {
	            setEndPoint(e.getX(), e.getY());
	            repaint();

	            Rectangle selectedRect = new Rectangle(x, y, x2 - x, y2 - y);
	            try {
	            	capturedImage = new Robot().createScreenCapture(selectedRect);
	            	File file = new File(".\\temp.png");
	    			ImageIO.write(capturedImage, "png", file);
	    			File[] files = new File[] {file};
	    			String result = KakaoOCR.kakaoOCR(files);
	    			TextCaptureGUI.textArea.setText(result);

				} catch(Exception ex) {
					ex.printStackTrace();
					return;
				}
	            
	            dispose();
	        }
	    }

	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.setColor(Color.RED);
	        drawPerfectRect(g, x, y, x2, y2);
	    }

	}
}