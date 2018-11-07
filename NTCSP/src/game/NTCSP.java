package game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import app.*;
import event.Metronome;
import event.MetronomeListener;
import io.ResourceFinder;
import resources.Marker;
import visual.*;
import visual.dynamic.sampled.Screen;
import visual.statik.SimpleContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

public class NTCSP extends JApplication
		implements MetronomeListener, ActionListener
{
	JButton start, next;
	Metronome met;
	Visualization vis;
	ResourceFinder rf;
	ContentFactory cf;
	HashMap<Integer, String> questions;
	HashMap<Integer, String[]> answers;

	public NTCSP(int width, int height)
	{
		super(width, height);
	}

	public static void main(String[] args)
	{
		invokeInEventDispatchThread(new NTCSP(1000, 800));
	}

	public void init()
	{
		met = new Metronome(5000);
		met.addListener(this);
		met.start();
		JPanel content = (JPanel) getContentPane();
		rf = ResourceFinder.createInstance(Marker.class);
		cf = new ContentFactory(rf);

		Content c = cf.createContent("Term44.png");

		vis = new Visualization();
		VisualizationView visView = vis.getView();
		vis.add(c);
		visView.setRenderer(new PlainVisualizationRenderer());
		visView.setBounds(0, 0, 1000, 800);
		
		content.add(visView);
	}

	@Override
	public void handleTick(int arg0)
	{ 
		JPanel content = (JPanel)getContentPane();  
		if (arg0 == 5000)
		{
			Content c = cf.createContent("Term21.png");
			vis.add(c);
			vis.repaint();
		} else if (arg0 == 10000)
		{
			Content c = cf.createContent("NameThatCSProfessor.png");
			vis.clear();
			vis.add(c);
			vis.getView().setBounds(0, 0, 1000, 750);
			start = new JButton("Start");
			start.setBounds(0, 750, 1000, 50);
			start.addActionListener(this);
			met.stop();
			content.add(start);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JPanel content = (JPanel)getContentPane();  
		String ac = arg0.getActionCommand();
		if(ac.equals("Start")) 
		{
			content.removeAll();
			next = new JButton("Next Question");
			next.setBounds(0, 750, 1000, 50);
			next.addActionListener(this);
			content.add(next);
			content.revalidate();
			content.repaint();
		}

	}
}
