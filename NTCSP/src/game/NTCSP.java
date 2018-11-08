package game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.*;

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
    JTextField usernameField;
    JTextArea question;
	int count = 0;
	Metronome met;
	Visualization vis;
	ResourceFinder rf;
	ContentFactory cf;
	ImageFactory ifa;
	InputStream is;
	String username;
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

			while ((line = in.readLine()) != null)
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

		// Load questions from file
        load();
	}

	@Override
	public void handleTick(int arg0)
	{
		JPanel content = (JPanel) getContentPane();
		if (arg0 < 4500)
		{
			vis.repaint();
		}
		else if (arg0 == 5000)
		{
			vis.remove(t);
			vis.add(t1);
			vis.repaint();
		}
		else if (arg0 > 5000 && arg0 < 8750)
		{
			vis.repaint();
		}
		else if (arg0 == 9500)
		{
			Content c = cf.createContent("NameThatCSProfessor.png");
			vis.remove(t1);
			vis.add(c);
			vis.getView().setBounds(0, 0, 1000, 750);

			// Create user name label
            JLabel usernameLabel = new JLabel("Enter User Name:", JLabel.CENTER);
            usernameLabel.setBounds(0,750, 150, 50);
            content.add(usernameLabel);

			// Create user name entry
			usernameField = new JTextField();
			usernameField.setBounds(150, 750, 350, 50);
			content.add(usernameField);

			// Create start button
			start = new JButton("Start");
			start.setBounds(width/2, 750, 500, 50);
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
		    // Store username
		    username = usernameField.getText();

			content.removeAll();
			next = new JButton("Next Question");
			next.setBounds(0, 750, 1000, 50);
			next.addActionListener(this);
			content.add(next);
			
		    question = new JTextArea();
		    question.setEditable(false);
		    question.setText(level1Qs.get(count).toString());
		    question.setLineWrap(true);
		    question.setBounds(100, 300, 400, 50);
		    content.add(question);
			content.revalidate();
			content.repaint();
			met.stop();
		}
		if (ac.equals("Next Question"))
		{
		    // Display question
			count++;
			if (count == 5)
			{
				content.removeAll();
				content.revalidate();
				content.repaint();
			} else {
		    question.setText(level1Qs.get(count).toString());
			content.revalidate();
			content.repaint();
			}
		}
	}
}
