package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.*;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	Visualization vis;
	ResourceFinder rf;
	ContentFactory cf;
	ImageFactory ifa;
	InputStream is;
	Type t, t1;
	HashMap<Integer, String> questions;
	HashMap<Integer, String[]> answers;

	  ArrayList<Question> level1Qs, level2Qs, level3Qs;
	  ArrayList<Professor> professors; //don't know if we actually need this but its loaded

	public NTCSP(int width, int height)
	{
		super(width, height);
	}

	public static void main(String[] args)
	{
		invokeInEventDispatchThread(new NTCSP(1000, 800));
	}

	  public void load()
	  {

	    professors = new ArrayList<>();
	    level1Qs = new ArrayList<>();
	    level2Qs = new ArrayList<>();
	    level3Qs = new ArrayList<>();

	    BufferedReader in;
	    String line, qu;
	    Professor prof = null;
	    int l;
	    Question q;

	    is = rf.findInputStream("Questions.txt");
	    in = new BufferedReader(new InputStreamReader(is));

	    try
	    {

	      while ( (line = in.readLine()) != null)
	      {

	        if (Character.isLetter(line.charAt(0)))
	        {
	          prof = new Professor(line);
	          professors.add(prof);
	          continue;
	        }

	        l = line.charAt(0) - '0';
	        qu = line.substring(line.indexOf('-') + 2, line.length());
	        q = new Question(l, qu, prof);

	        switch (l)
	        {
	          case 1:
	            level1Qs.add(q);
	            break;
	          case 2:
	            level2Qs.add(q);
	            break;
	          case 3:
	            level3Qs.add(q);
	            break;
	        }
	      }
	    } catch (IOException e)
	    {
	      e.printStackTrace();
	    }

//	    testing
//	    System.out.println(professors);
//	    System.out.println(level1Qs);
//	    System.out.println(level2Qs);
//	    System.out.println(level3Qs);
//	     System.out.println(level3Qs.size() + level2Qs.size() + level1Qs.size());
	  }

	public void init()
	{

		// initialize global attributes
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

	
	
//	
//	
//	<<<<<<<
//	HEAD JButton start,next;
//	int count = 0;
//	Metronome met;
//	Type t, t1;
//	Visualization vis;
//	ResourceFinder rf;
//	ContentFactory cf;
//	ImageFactory ifa;
//	HashMap<Integer, String> questions;
//	HashMap<Integer, String[]> answers;
//
//	public NTCSP(int width, int height)
//	{
//		super(width, height);
//	}
//
//	public static void main(String[] args)
//	{
//		invokeInEventDispatchThread(new NTCSP(1000, 800));
//	}
//
//	public void init()
//	{
//		met = new Metronome(50);
//		met.addListener(this);
//		met.start();
//		JPanel content = (JPanel) getContentPane();
//		rf = ResourceFinder.createInstance(Marker.class);
//		cf = new ContentFactory(rf);
//		ifa = new ImageFactory(rf);
//
//		t = new Type("Make Name That CS Professor", 230);
//		t.setImage(ifa.createBufferedImage("Term44.png"));
//		t1 = new Type("./Name That CS Professor", 340);
//		t1.setImage(ifa.createBufferedImage("Term20.png"));
//
//		vis = new Visualization();
//		VisualizationView visView = vis.getView();
//		visView.setBounds(0, 0, 1000, 800);
//		vis.add(t);
//
//		content.add(visView);
//	}
//
//	@Override
//	public void handleTick(int arg0)
//	{
//		JPanel content = (JPanel) getContentPane();
//		if (arg0 < 4450)
//		{
//			vis.repaint();
//			System.out.println(arg0);
//		} else if (arg0 == 5000)
//		{
//			vis.remove(t);
//			vis.add(t1);
//			vis.repaint();
//		} else if (arg0 > 5000 && arg0 < 8750)
//		{
//			vis.repaint();
//		} else if (arg0 == 10000)
//		{
//			Content c = cf.createContent("NameThatCSProfessor.png");
//			vis.remove(t1);
//			vis.add(c);
//			vis.getView().setBounds(0, 0, 1000, 750);
//			start = new JButton("Start");
//			start.setBounds(0, 750, 1000, 50);
//			start.addActionListener(this);
//			content.add(start);
//		}
//	}
//
//	@Override
//	public void actionPerformed(ActionEvent arg0)
//	{
//		JPanel content = (JPanel) getContentPane();
//		String ac = arg0.getActionCommand();
//		if (ac.equals("Start"))
//		{
//			content.removeAll();
//			next = new JButton("Next Question");
//			next.setBounds(0, 750, 1000, 50);
//			next.addActionListener(this);
//			content.add(next);
//			content.revalidate();
//			content.repaint();
//		}
//		if (ac.equals("Next Question"))
//		{
//			count++;
//			if (count == 5)
//			{
//				content.removeAll();
//				content.revalidate();
//				content.repaint();
//			}
//		}
//	}=======
//	
//	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void handleTick(int arg0)
	{
		JPanel content = (JPanel) getContentPane();
		if (arg0 < 4500)
		{
			vis.repaint();
		} else if (arg0 == 5000)
		{
			vis.remove(t);
			vis.add(t1);
			vis.repaint();
		} else if (arg0 > 5000 && arg0 < 8750)
		{
			vis.repaint();
		} else if (arg0 == 9500)
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
//=======
//  JButton start, next;
//  int count = 0;
//  Metronome met;
//  Visualization vis;
//  ResourceFinder rf;
//  InputStream is;
//
//  ContentFactory cf;
//  HashMap<Integer, String> questions;
//  HashMap<Integer, String[]> answers;
//
//  ArrayList<Question> level1Qs, level2Qs, level3Qs;
//  ArrayList<Professor> professors; //don't know if we actually need this but its loaded
//
//  public NTCSP(int width, int height)
//  {
//    super(width, height);
//  }
//
//  public static void main(String[] args)
//  {
//    invokeInEventDispatchThread(new NTCSP(1000, 800));
//  }
//
//  public void load()
//  {
//
//    professors = new ArrayList<>();
//    level1Qs = new ArrayList<>();
//    level2Qs = new ArrayList<>();
//    level3Qs = new ArrayList<>();
//
//    BufferedReader in;
//    String line, qu;
//    Professor prof = null;
//    int l;
//    Question q;
//
//    is = rf.findInputStream("Questions.txt");
//    in = new BufferedReader(new InputStreamReader(is));
//
//    try
//    {
//
//      while ( (line = in.readLine()) != null)
//      {
//
//        if (Character.isLetter(line.charAt(0)))
//        {
//          prof = new Professor(line);
//          professors.add(prof);
//          continue;
//        }
//
//        l = line.charAt(0) - '0';
//        qu = line.substring(line.indexOf('-') + 2, line.length());
//        q = new Question(l, qu, prof);
//
//        switch (l)
//        {
//          case 1:
//            level1Qs.add(q);
//            break;
//          case 2:
//            level2Qs.add(q);
//            break;
//          case 3:
//            level3Qs.add(q);
//            break;
//        }
//      }
//    } catch (IOException e)
//    {
//      e.printStackTrace();
//    }
//
////    testing
////    System.out.println(professors);
////    System.out.println(level1Qs);
////    System.out.println(level2Qs);
////    System.out.println(level3Qs);
////     System.out.println(level3Qs.size() + level2Qs.size() + level1Qs.size());
//  }
//
//  public void init()
//  {
//
//    //initialize global attributes
//    met = new Metronome(5000);
//    rf = ResourceFinder.createInstance(Marker.class);
//    cf = new ContentFactory(rf);
//
//    load();
//
//    JPanel content = (JPanel) getContentPane();
//    met.addListener(this);
//    met.start();
//
//    //TODO fix this
//    Content c = cf.createContent("Term44.png");
//
//    vis = new Visualization();
//    VisualizationView visView = vis.getView();
//    vis.add(c);
//    visView.setRenderer(new PlainVisualizationRenderer());
//    visView.setBounds(0, 0, 1000, 800);
//
//    content.add(visView);
//  }
//
//  @Override
//  public void handleTick(int arg0)
//  {
//    JPanel content = (JPanel) getContentPane();
//    if (arg0 == 5000)
//    {
//      Content c = cf.createContent("Term21.png");
//      vis.add(c);
//      vis.repaint();
//    } else if (arg0 == 10000)
//    {
//      Content c = cf.createContent("NameThatCSProfessor.png");
//      vis.clear();
//      vis.add(c);
//      vis.getView().setBounds(0, 0, 1000, 750);
//      start = new JButton("Start");
//      start.setBounds(0, 750, 1000, 50);
//      start.addActionListener(this);
//      met.stop();
//      content.add(start);
//    }
//  }
//
//  @Override
//  public void actionPerformed(ActionEvent arg0)
//  {
//    JPanel content = (JPanel) getContentPane();
//    String ac = arg0.getActionCommand();
//    if (ac.equals("Start"))
//    {
//      content.removeAll();
//      next = new JButton("Next Question");
//      next.setBounds(0, 750, 1000, 50);
//      next.addActionListener(this);
//      content.add(next);
//      content.revalidate();
//      content.repaint();
//    }
//    if (ac.equals("Next Question"))
//    {
//      count++;
//      if (count == 5)
//      {
//        content.removeAll();
//        content.revalidate();
//        content.repaint();
//      }
//    }
//  }
//>>>>>>> 2925dbea75eb568b5617fe362b39c6bc86952000
//}
