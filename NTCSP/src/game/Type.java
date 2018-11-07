package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class Type extends JComponent
{
	private int iteration;
	private String s;
	private float x;
	private float y;
	
	public Type(String s, float x, float y) {
		iteration = 1;
		this.s = s;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.WHITE);
		g2.drawString(s.substring(0, iteration), x, y);
	}
}
