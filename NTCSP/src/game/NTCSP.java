package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import app.*;
import event.Metronome;
import event.MetronomeListener;
import io.ResourceFinder;
import resources.Marker;
import visual.*;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;
import visual.statik.sampled.ImageFactory;

public class NTCSP extends JApplication
		implements MetronomeListener, ActionListener
{
	JButton start, next;
	int count = 0;
	Metronome met;
	Type t, t1;
	Visualization vis;
	ResourceFinder rf;
	ContentFactory cf;
	ImageFactory ifa;
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
		met = new Metronome(50);
		met.addListener(this);
		met.start();
		JPanel content = (JPanel) getContentPane();
		rf = ResourceFinder.createInstance(Marker.class);
		cf = new ContentFactory(rf);
		ifa = new ImageFactory(rf);

		t = new Type("Make Name That CS Professor", 230);
		t.setImage(ifa.createBufferedImage("Term44.png"));
		t1 = new Type("./Name That CS Professor", 340);
		t1.setImage(ifa.createBufferedImage("Term20.png"));

		vis = new Visualization();
		VisualizationView visView = vis.getView();
		visView.setBounds(0, 0, 1000, 800);
		vis.add(t);

		content.add(visView);
	}

	@Override
	public void handleTick(int arg0)
	{
		JPanel content = (JPanel) getContentPane();
		if (arg0 < 4450)
		{
			vis.repaint();
			System.out.println(arg0);
		} else if (arg0 == 5000)
		{
			vis.remove(t);
			vis.add(t1);
			vis.repaint();
		} else if (arg0 > 5000 &&arg0 < 8750) {
			vis.repaint();
		} else if (arg0 == 10000)
		{
			Content c = cf.createContent("NameThatCSProfessor.png");
			vis.remove(t1);
			vis.add(c);
			vis.getView().setBounds(0, 0, 1000, 750);
			start = new JButton("Start");
			start.setBounds(0, 750, 1000, 50);
			start.addActionListener(this);
			content.add(start);
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JPanel content = (JPanel) getContentPane();
		String ac = arg0.getActionCommand();
		if (ac.equals("Start"))
		{
			content.removeAll();
			next = new JButton("Next Question");
			next.setBounds(0, 750, 1000, 50);
			next.addActionListener(this);
			content.add(next);
			content.revalidate();
			content.repaint();
		}
		if (ac.equals("Next Question"))
		{
			count++;
			if (count == 5)
			{
				content.removeAll();
				content.revalidate();
				content.repaint();
			}
		}
	}
}
