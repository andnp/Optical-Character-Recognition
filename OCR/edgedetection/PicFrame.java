package edgedetection;

import javax.swing.*;

public class PicFrame{
	private static JFrame f = new JFrame();
	private Picture pic;
	PicFrame(Picture pic){
		this.pic = pic;
	}
	public void buildFrame(){
		f.setTitle("Image");
		f.setSize(pic.getWidth(), pic.getHeight());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel l = new JLabel(new ImageIcon(pic.getPic()));
		JPanel p = new JPanel();
		p.add(l);
		f.add(p);
		f.setVisible(true);
	}
}
