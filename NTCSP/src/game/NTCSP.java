package game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
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
import visual.dynamic.described.Stage;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;
import visual.statik.sampled.ImageFactory;

public class NTCSP extends JApplication
        implements MetronomeListener, ActionListener, MouseListener
{
    Clip clip;
    JTextField usernameField;
    JTextArea question;
    int count;
    int score;
    Metronome met;
    Visualization vis;
    Stage stage;
    LinkedList<Visualization> profList;
    ResourceFinder rf;
    ContentFactory cf;
    ImageFactory ifa;
    InputStream is;
    Random rand;
    String username;
    Professor correctProfessor;
    HashMap<Integer, String> questions;
    HashMap<Integer, String[]> answers;
    HashMap<Visualization, Professor> answerToProfessor;
    VisualizationView chosen, correct;

    ArrayList<Question> level1Qs, level2Qs, level3Qs;
    ArrayList<Professor> professors; // don't know if we actually need this but
    // its loaded

    public NTCSP(int width, int height)
    {
        super(width, height);
        count = 0;
        score = 0;
        rand = new Random(System.currentTimeMillis());
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
        met = new Metronome(150);
        met.addListener(this);

        JPanel content = (JPanel) getContentPane();
        rf = ResourceFinder.createInstance(Marker.class);
        cf = new ContentFactory(rf);
        ifa = new ImageFactory(rf);

        Content c = cf.createContent("professors.png");
        MovingImage mi1 = new MovingImage(c, 0, 0);
        MovingImage mi2 = new MovingImage(c, 1960, 0);
        stage = new Stage(50);
        VisualizationView stageView = stage.getView();
        stageView.setBounds(0, 550, 1000, 200);

        vis = new Visualization();
        Content home = cf.createContent("NTCSP-1.png");
        vis.add(home);
        vis.getView().setBounds(0, 0, 1000, 550);

        stage.addView(vis.getView());
        stage.add(mi1);
        stage.add(mi2);
        content.add(stage.getView());
        content.add(vis.getView());

        // Create user name label
        JLabel usernameLabel = new JLabel("Enter User Name:", JLabel.CENTER);
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

        // Load questions from file
        load();
        stage.start();
    }

    @Override
    public void handleTick(int arg0)
    {
        JPanel content = (JPanel) getContentPane();
    }

    public void displayScore()
    {
        JPanel content = (JPanel) getContentPane();

        Content c = cf.createContent("score.png");
        vis = new Visualization();
        vis.add(c);
        vis.getView().setBounds(0, 0, 1000, 750);

        JTextField nameArea = new JTextField();
        nameArea.setFont(new Font("Times New Roman", Font.BOLD, 40));
        nameArea.setForeground(Color.white);
        nameArea.setOpaque(false);
        nameArea.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        nameArea.setHorizontalAlignment(JTextField.CENTER);
        nameArea.setEditable(false);
        nameArea.setText(username + "'s Score");
        nameArea.setBounds(0, 0, 1000, 100);

        JTextField scoreArea = new JTextField();
        scoreArea.setFont(new Font("Times New Roman", Font.BOLD, 200));
        scoreArea.setForeground(Color.black);
        scoreArea.setOpaque(false);
        scoreArea.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scoreArea.setHorizontalAlignment(JTextField.CENTER);
        scoreArea.setEditable(false);
        scoreArea.setText(score + "/5");
        scoreArea.setBounds(0, 200, 1000, 400);

        content.add(scoreArea);
        content.add(nameArea);
        content.add(vis.getView());

        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(0, 750, 1000, 50);
        playAgainButton.addActionListener(this);
        content.add(playAgainButton);
    }

    public Clip initClip(String file)
    {
        Clip clip;
        try
        {
            AudioInputStream stream;
            BufferedInputStream bis;
            InputStream is;
            ResourceFinder finder;

            // Get the resource
            finder = ResourceFinder.createInstance(resources.Marker.class);
            is     = finder.findInputStream(file);

            // Decorate the InputStream as a BufferedInputStream
            // so mark and reset are supported
            bis = new BufferedInputStream(is);

            // Create an AudioInputStream from the InputStream
            stream = AudioSystem.getAudioInputStream(bis);

            // Create a Clip (i.e., a Line that can be pre-loaded)
            clip = AudioSystem.getClip();

            // Tell the Clip to acquire any required system
            // resources and become operational
            clip.open(stream);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            clip = null;
        }

        return clip;
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

            //Reset score
            score = 0;

            content.removeAll();
            JButton submit = new JButton("Submit Choice");
            submit.setBounds(0, 750, 1000, 50);
            submit.addActionListener(this);
            content.add(submit);

            Content qContent = cf.createContent("question.png");

            vis = new Visualization();
            vis.add(qContent);
            vis.getView().setBounds(0, 0, 1000, 300);


            question = new JTextArea();
            question.setBackground(new Color(105, 0, 250));
            question.setForeground(new Color(0, 0, 0));
            question.setEditable(false);

            int randQ = (int)(rand.nextDouble() * level1Qs.size());
            Question q = level1Qs.get(randQ);
            level1Qs.remove(randQ);

            correctProfessor = q.getAnswer();
            question.setFont(new Font("Times New Roman", Font.BOLD, 40));
            question.setText(q.getText());
            question.setLineWrap(true);
            question.setBounds(350, 100, 600, 200);
            question.setOpaque(false);

            addProfessors(q);

            content.add(question);
            content.add(vis.getView());
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
                if(correctProfessor.toString().equals("Dr. Bernstein")) {
                    //clip = initClip(correctProfessor.getAudioNameCorrect());
                } else {
                    //clip = initClip(correctProfessor.getGenericAudio());
                }
                score++;
            }
            else
            {
                if(correctProfessor.toString().equals("Dr. Bernstein")) {
                    //clip = initClip(correctProfessor.getAudioNameIncorrect());
                } else {
                    //clip = initClip(correctProfessor.getGenericAudio());
                }
            }

            TalkingProfessor tp = new TalkingProfessor(cf, correctProfessor);

            stage = new Stage(65);
            stage.getView().setBounds(0, 550, 200, 200);
            stage.add(cf.createContent(correctProfessor.getHeadImageName()));
            stage.add(tp);

            content.setBackground(Color.white);
            content.add(stage.getView());
            content.add(vis.getView());
            content.add(next);
            content.revalidate();
            content.repaint();
            stage.start();

            //clip = initClip(correctProfessor.getAudioName());
            //clip.start();

            //hello
        }

        if (ac.equals("Next Question"))
        {
            //clip.stop();
            // Display question
            if (count % 5 == 0)
            {
                content.removeAll();
                content.revalidate();
                content.repaint();
                displayScore();
                load();
            }
            else
            {
                content.removeAll();
                JButton submit = new JButton("Submit Choice");
                submit.setBounds(0, 750, 1000, 50);
                submit.addActionListener(this);
                content.add(submit);

                Content qContent = cf.createContent("question.png");

                vis = new Visualization();
                vis.add(qContent);
                vis.getView().setBounds(0, 0, 1000, 300);

                question = new JTextArea();
                question.setBackground(new Color(105, 0, 250));
                question.setForeground(new Color(0, 0, 0));
                question.setEditable(false);

                int randQ = (int)(rand.nextDouble() * level1Qs.size());
                Question q = level1Qs.get(randQ);
                level1Qs.remove(randQ);

                question.setFont(new Font("Times New Roman", Font.BOLD, 40));
                question.setText(q.getText());
                correctProfessor = q.getAnswer();
                question.setLineWrap(true);
                question.setBounds(350, 100, 600, 200);
                question.setOpaque(false);

                addProfessors(q);

                content.add(question);
                content.add(vis.getView());
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
        chosen.setBackground(new Color(104, 23, 250));
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
            int index = (int)(rand.nextDouble() * 10);
            while (previous.contains(index))
            {
                index = (index + 1) % 10;
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
                answer.getView().setBounds(x, 450, 250, 200);
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
                answer.getView().setBounds(x, 450, 250, 200);
                content.add(answer.getView());
                profList.add(answer);
                x = x + 250;
            }
        }
        previous.clear();
    }
}
