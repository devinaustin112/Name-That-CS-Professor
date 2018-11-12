package game;

import java.awt.Color;
import java.awt.Font;
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
import java.util.Random;

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
	JTextField usernameField;
	JTextArea question;
	int count;
	int score;
	Metronome met;
	Visualization vis;
	LinkedList<Visualization> profList;
	ResourceFinder rf;
	ContentFactory cf;
	ImageFactory ifa;
	InputStream is;
	Random rand;
	String username;
	Type t, t1;
	HashMap<Integer, String> questions;
	HashMap<Integer, String[]> answers;
	VisualizationView chosen, correct;

	ArrayList<Question> level1Qs, level2Qs, level3Qs;
	ArrayList<Professor> professors; // don't know if we actually need this but
										// its loaded

	public NTCSP(int width, int height)
	{
		super(width, height);
		count = 0;
		score = 0;
		rand = new Random(0);
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
					String[] temp = line.split("\\|");
					prof = new Professor(temp[0]);
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

		// testing
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
	  met.start();
	}

	@Override
	public void handleTick(int arg0)
	{
		JPanel content = (JPanel) getContentPane();
		if (arg0 < 4200 )
		{
			vis.repaint();
		} else if (arg0 == 4700)
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
			JButton start = new JButton("Start");
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
		scoreArea.setText(username + "'s Score: " + score);
		scoreArea.setBounds(width / 2, height / 2, 200, 50);
		content.add(scoreArea);

		JButton playAgainButton = new JButton("Play Again");
		playAgainButton.setBounds(width / 2, height / 2 + 200, 200, 50);
		playAgainButton.addActionListener(this);
		content.add(playAgainButton);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		JPanel content = (JPanel) getContentPane();
		String ac = arg0.getActionCommand();
		if (ac.equals("Start") || ac.equals("Play Again"))
		{
			// Store username
			username = usernameField.getText();

			content.removeAll();
			JButton submit = new JButton("Submit Choice");
			submit.setBounds(0, 750, 1000, 50);
			submit.addActionListener(this);
			content.add(submit);

			question = new JTextArea();
			question.setBackground(new Color(105, 0, 250));
			question.setForeground(new Color(255, 255, 255));
			question.setEditable(false);
			Question q = level1Qs.get(count);
			question.setFont(new Font("Times New Roman", Font.BOLD, 40));
			question.setText(q.getText());
			question.setLineWrap(true);
			question.setBounds(0, 0, 1000, 200);

			addProfessors(q);

			content.add(question);
			content.revalidate();
			content.repaint();
			met.stop();
		}
		if (ac.equals("Submit Choice"))
		{
			count++;
			content.removeAll();
			JButton next = new JButton("Next Question");
			next.setBounds(0, 750, 1000, 50);
			next.addActionListener(this);
			Content c;
			if (chosen == correct)
			{
				c = cf.createContent("correct.png");
				score++;
			} else
			{
				c = cf.createContent("incorrect.png");
			}
			vis.add(c);
			content.add(vis.getView());
			content.add(next);
			content.revalidate();
			content.repaint();
		}
		if (ac.equals("Next Question"))
		{
			// Display question
			if (count % 5 == 0)
			{
				content.removeAll();
				content.revalidate();
				content.repaint();
				displayScore();
			} else
			{
				// Store username
				username = usernameField.getText();

				content.removeAll();
				JButton submit = new JButton("Submit Choice");
				submit.setBounds(0, 750, 1000, 50);
				submit.addActionListener(this);
				content.add(submit);

				question = new JTextArea();
				question.setBackground(new Color(105, 0, 250));
				question.setForeground(new Color(255, 255, 255));
				question.setEditable(false);
				Question q = level1Qs.get(count);
				question.setFont(new Font("Times New Roman", Font.BOLD, 40));
				question.setText(q.getText());
				question.setLineWrap(true);
				question.setBounds(0, 0, 1000, 200);

				addProfessors(q);

				content.add(question);
				content.revalidate();
				content.repaint();
				met.stop();
			}

			count = count % (level1Qs.size() - 1); // if it gets to the end of the list it starts back at 0
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		for (int i = 0; i < profList.size(); i++)
		{
			profList.get(i).setBackground(new JPanel().getBackground());
		}

		chosen = (VisualizationView) arg0.getSource();
		chosen.setBackground(new Color(105, 0, 250));
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
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

	public void addProfessors(Question q)
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
		ArrayList<Professor> otherChoices = new ArrayList<>(professors);
		otherChoices.remove(q.getAnswer());
		ArrayList<Integer> previous = new ArrayList<>();

		Visualization answer;
		int x = 0;
		boolean notAdded = true;
		for (int i = 0; i < 4; i++)
		{
			// Get random between 0 and 10 non repeating
			int index = rand.nextInt(10);
			while (previous.contains(index))
			{
				index = (index + 1) % 11;
			}
			previous.add(index);

			// If random index is greater than or equal to 5 and not added
			// add the correct answer to vis. Otherwise add other choices

			// Code duplication
			Content prof;
			if (index >= 5 && notAdded || i == 3 && notAdded)
			{
				prof = cf.createContent(q.getAnswer().getImage());
				answer = new Visualization();
				answer.addMouseListener(this);
				prof.setLocation(25, 0);
				answer.add(prof);
				answer.getView().setBounds(x, 500, 250, 200);
				correct = answer.getView();
				content.add(answer.getView());
				profList.add(answer);
				x = x + 250;
				notAdded = false;
			} else
			{
				prof = cf.createContent(otherChoices.get(index).getImage());
				answer = new Visualization();
				answer.addMouseListener(this);
				prof.setLocation(25, 0);
				answer.add(prof);
				answer.getView().setBounds(x, 500, 250, 200);
				content.add(answer.getView());
				profList.add(answer);
				x = x + 250;
			}
		}
		previous.clear();
	}
}