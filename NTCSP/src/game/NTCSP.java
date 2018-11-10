package game;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
		implements MetronomeListener, ActionListener, MouseListener
{
	JButton start, next;
	JTextField usernameField;
	JTextArea question;
	int count;
	int[] score;
	Metronome met;
	Visualization vis;
	LinkedList<Visualization> profList;
	ResourceFinder rf;
	ContentFactory cf;
	ImageFactory ifa;
	InputStream is;
	String username;
	Type t, t1;
	Question currQ;
	HashMap<Integer, String> questions;
	HashMap<Integer, String[]> answers;

	ArrayList<Question> level1Qs, level2Qs, level3Qs;
	ArrayList<Professor> professors; // don't know if we actually need this but
										// its loaded

	public NTCSP(int width, int height)
	{
		super(width, height);
		count = 0;
		score = new int[4];
		score[0] = 0;
		score[1] = 0;
		score[2] = 0;
		score[3] = 0;
		currQ = null;
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
		if (arg0 < 4450)
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

			// Create user name label
			JLabel usernameLabel = new JLabel("Enter User Name:",
					JLabel.CENTER);
			usernameLabel.setBounds(0, 750, 150, 50);
			content.add(usernameLabel);

			// Create user name entry
			usernameField = new JTextField();
			usernameField.setBounds(150, 750, 350, 50);
			content.add(usernameField);

			// Create start button
			start = new JButton("Start");
			start.setBounds(width / 2, 750, 500, 50);
			start.addActionListener(this);
			content.add(start);
		}
	}

	public void displayScore()
	{
		JPanel content = (JPanel) getContentPane();

		JTextArea scoreArea = new JTextArea();
		scoreArea.setEditable(false);
		scoreArea.setText(username + "'s Score: " + score[0]);
		scoreArea.setBounds(width / 2, height / 2, 200, 50);
		content.add(scoreArea);

		JButton playAgainButton = new JButton("Play Again");
		playAgainButton.setBounds(width / 2, height / 2 + 200, 200, 50);
		playAgainButton.addActionListener(this);
		content.add(playAgainButton);

        //Reset score and count to 0
        count = 0;
        score[0] = 0;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JPanel content = (JPanel) getContentPane();
		String ac = arg0.getActionCommand();
		//set currQ to hold on to the correct answer
		currQ = level1Qs.get(count);
		if (ac.equals("Start") || ac.equals("Play Again"))
		{
			// Store username
			username = usernameField.getText();

			content.removeAll();
			next = new JButton("Next Question");
			next.setBounds(0, 750, 1000, 50);
			next.addActionListener(this);
			content.add(next);

			 addProfessors();

			question = new JTextArea();
			question.setEditable(false);
			question.setText(currQ.getText());
			question.setLineWrap(true);
			question.setBounds(100, 300, 400, 50);
			score[0]++;
			count++;
			content.add(question);
			content.revalidate();
			content.repaint();
			met.stop();
		}
		if (ac.equals("Next Question"))
		{
			addProfessors();

			// Display question
			if (count == 5)
			{
				content.removeAll();
				content.revalidate();
				content.repaint();
				displayScore();
			} else
			{
				//set currQ to the current question to save answer
				question.setText(currQ.getText());
				score[0]++;
				content.revalidate();
				content.repaint();
                count++;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		for (int i = 0; i < profList.size(); i++)
		{
			profList.get(i).setBackground(new JPanel().getBackground());
		}

		VisualizationView click = (VisualizationView) arg0.getSource();
		click.setBackground(new Color(105, 0, 250));
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	public void addProfessors()
	{
		JPanel content = (JPanel) getContentPane();

		if (profList != null)
		{
			for (int i = 0; i < profList.size(); i++)
			{
				content.remove(profList.get(i).getView());
			}
		}

		profList = new LinkedList<>();

		Visualization answer1 = new Visualization();
		answer1.addMouseListener(this);
		//place correct answer
		Content prof1 = cf.createContent(currQ.getAnswer().getImage());
		prof1.setLocation(25, 0);
		answer1.add(prof1);
		answer1.getView().setBounds(0, 500, 250, 200);
		content.add(answer1.getView());
		profList.add(answer1);

		Visualization answer2 = new Visualization();
		answer2.addMouseListener(this);
		Content prof2 = cf.createContent(professors.get(2).getImage());
		prof2.setLocation(25, 0);
		answer2.add(prof2);
		answer2.getView().setBounds(250, 500, 250, 200);
		content.add(answer2.getView());
		profList.add(answer2);

		Visualization answer3 = new Visualization();
		answer3.addMouseListener(this);
		Content prof3 = cf.createContent(professors.get(3).getImage());
		prof3.setLocation(25, 0);
		answer3.add(prof3);
		answer3.getView().setBounds(500, 500, 250, 200);
		content.add(answer3.getView());
		profList.add(answer3);

		Visualization answer4 = new Visualization();
		answer4.addMouseListener(this);
		Content prof4 = cf.createContent(professors.get(4).getImage());
		prof4.setLocation(25, 0);
		answer4.add(prof4);
		answer4.getView().setBounds(750, 500, 250, 200);
		content.add(answer4.getView());
		profList.add(answer4);

	}
}
