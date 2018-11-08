package game;

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
import javax.swing.JTextArea;

import app.*;
import event.Metronome;
import event.MetronomeListener;
import io.ResourceFinder;
import resources.Marker;
import visual.*;
import visual.statik.SimpleContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

public class NTCSP extends JApplication implements MetronomeListener, ActionListener
{
  JButton start, next;
  int count = 0;
  Metronome met;
  Visualization vis;
  ResourceFinder rf;
  InputStream is;

  ContentFactory cf;
  HashMap<Integer, String> questions;
  HashMap<Integer, String[]> answers;

  ArrayList<Question> level1Qs, level2Qs, level3Qs;
  ArrayList<Professor> professors;

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

    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    InputStream is = finder.findInputStream("Questions.txt");
    in = new BufferedReader(new InputStreamReader(is));
    
    try
    {

      //      rf = ResourceFinder.createInstance(Marker.class);
      //
      //      is = rf.findInputStream("Questions.txt");
      //      in = new BufferedReader(new InputStreamReader(is));

      //   in = new BufferedReader(new FileReader("Questions.txt"));

      line = in.readLine();
      while (line != null)
      {

        if (Character.isLetter(line.charAt(0)))
        {
          prof = new Professor(line);
          professors.add(prof);
          line = in.readLine();
          continue;
        }

        l = line.charAt(0);
        qu = line.substring(line.indexOf('-'), line.length());
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

        line = in.readLine();
      }
    } catch (IOException e)
    {
      e.printStackTrace();
    }

  }

  public void init()
  {

    //initialize global attributes
    met = new Metronome(5000);
    rf = ResourceFinder.createInstance(Marker.class);
    cf = new ContentFactory(rf);

    load();

    JPanel content = (JPanel) getContentPane();
    met.addListener(this);
    met.start();

    //TODO fix this
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
    JPanel content = (JPanel) getContentPane();
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
