package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class Type extends visual.statik.sampled.Content
{
	private int iteration;
	private String s;
	private int y;
	private boolean render = true;

	public Type(String s, int y)
	{
		super();
		this.s = s;
		this.y = y;
		iteration = 0;
	}

	@Override
	public void render(Graphics g)
	{
		super.render(g);
		Graphics2D g2 = (Graphics2D) g;
		Font f = new Font("Times New Roman", Font.PLAIN, 18);
		g2.setFont(f);
		g2.setColor(Color.WHITE);
		g2.drawString(s.substring(0, iteration / 3), 465, y);
		iteration++ ;
	}
}
