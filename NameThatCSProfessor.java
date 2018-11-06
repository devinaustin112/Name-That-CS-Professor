package Game;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import Resources.Marker;
import app.*;
import event.Metronome;
import event.MetronomeListener;
import io.ResourceFinder;
import visual.*;
import visual.dynamic.sampled.Screen;
import visual.statik.SimpleContent;
import visual.statik.described.Content;
import visual.statik.sampled.ContentFactory;
import visual.statik.sampled.ImageFactory;

public class NameThatCSProfessor extends JApplication
		implements MetronomeListener
{
	JButton start;
	ResourceFinder rf;
	ContentFactory cf;
	
	public NameThatCSProfessor(int width, int height)
	{
		super(width, height);
	}

	public static void main(String[] args)
	{
		invokeInEventDispatchThread(new NameThatCSProfessor(1000, 800));
	}

	public void init()
	{
		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setBackground(Color.WHITE);
		Metronome met = new Metronome(20);
		met.addListener(this);
		met.start();
		rf = ResourceFinder.createInstance(Marker.class);
		cf = new ContentFactory(rf);

		Screen screen = new Screen(8);
		screen.setRepeating(false);
		String[] names = rf.loadResourceNames("term.txt");
		SimpleContent[] frames = cf.createContents(names, 4);

		for (SimpleContent s : frames)
		{
			screen.add(s);
		}
		
		Visualization vis = new Visualization();
		VisualizationView visView = screen.getView();
		visView.setBounds(0, 0, 1000, 800);
		contentPane.add(visView);
		screen.start();
	}

	@Override
	public void handleTick(int arg0)
	{
		if(arg0 == 12000) {
			JPanel content = (JPanel)getContentPane();
		    content.removeAll(); 
		    
		    
		    JTextArea ta = new JTextArea("NAME THAT PROFESSOR");
		    ta.setBounds(500, 400, 1000, 800);
		    visual.statik.sampled.Content berny = cf.createContent("bernstein.jpg", 4);
		    berny.setLocation(50, 50);
			Visualization vis = new Visualization();
			VisualizationView visView = vis.getView();
			vis.add(berny);
			visView.setBounds(0, 0, 1000, 800);
			content.add(visView);
		    
		    content.add(ta);
		    content.add(visView);
		    content.revalidate();
		    content.repaint();
		}
	}
}
