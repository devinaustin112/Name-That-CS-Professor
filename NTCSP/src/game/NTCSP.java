package game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

public class NTCSP extends JApplication implements MetronomeListener, ActionListener, MouseListener
{
  Clip clip;
  JTextField usernameField;
  JTextArea question;
  int score;
  int questionsAsked;
  Metronome met;
  Visualization vis;
  Stage stage;
  LinkedList<Visualization> profList;
  LinkedList<VisualizationView> avatars;
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
  HashMap<String, ArrayList<Question>> categoryToQuestions;
  ArrayList<Question> questionSet;
  ArrayList<JButton> levelButtons = new ArrayList<>();
  String selectedCategory;
  VisualizationView chosen, correct, avatar;

  ArrayList<Question> educationQs, favoriteQs, storiesQs, phrasesQs, familyQs, freetimeQs, bonusQs;
  ArrayList<Professor> professors; // don't know if we actually need this but
  // its loaded

  public NTCSP(int width, int height)
  {
    super(width, height);
    categoryToQuestions = new HashMap<>();
    score = 0;
    questionsAsked = 0;
    rand = new Random(System.currentTimeMillis());
  }

  public static void main(String[] args)
  {
    invokeInEventDispatchThread(new NTCSP(1000, 800));
  }

  public void loadQuestions()
  {
    professors = new ArrayList<>();
    educationQs = new ArrayList<>();
    favoriteQs = new ArrayList<>();
    storiesQs = new ArrayList<>();
    phrasesQs = new ArrayList<>();
    familyQs = new ArrayList<>();
    freetimeQs = new ArrayList<>();
    bonusQs = new ArrayList<>();

    BufferedReader in;
    String line, qu;
    Professor prof = null;
    String l;
    Question q;

    is = rf.findInputStream("Quest.txt");
    in = new BufferedReader(new InputStreamReader(is));

    try
    {

      while ((line = in.readLine()) != null)
      {

        if (Character.isDigit(line.charAt(0)))
        {
          //    String[] temp = line.split("\\|");
          prof = new Professor(line.substring(2, line.length()));
          professors.add(prof);
          continue;
        }

        l = line.substring(0, 2);
        qu = line.substring(line.indexOf('-') + 2, line.length());
        q = new Question(l, qu, prof);

        switch (l)
        {
          case "EW":
            educationQs.add(q);
            break;
          case "FT":
            favoriteQs.add(q);
            break;
          case "IP":
            storiesQs.add(q);
            break;
          case "CP":
            phrasesQs.add(q);
            break;
          case "FP":
            familyQs.add(q);
            break;
          case "LW":
            freetimeQs.add(q);
            break;
          case "BO":
            bonusQs.add(q);
            break;
          default:
            System.out.println("Not an accurate level: " + line);
        }
      }

      categoryToQuestions.put("EDUCATION / WORK", educationQs);
      categoryToQuestions.put("FAVORITE THINGS", favoriteQs);
      categoryToQuestions.put("INTERESTING STORIES", storiesQs);
      categoryToQuestions.put("CHILDREN / PETS", familyQs);
      categoryToQuestions.put("CATCH PHRASES / QUOTES", phrasesQs);
      categoryToQuestions.put("LIFE OUTSIDE WORK", freetimeQs);

    } catch (IOException e)
    {
      e.printStackTrace();
    }

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

    setupUsername();

    // Create start button
    JButton start = new JButton("Start");
    start.setBounds(width / 2, 750, 500, 50);
    start.addActionListener(this);
    content.add(start);

    // Load questions from file
    loadQuestions();
    stage.start();
  }

  private void setupUsername()
  {
    JPanel content = (JPanel) getContentPane();

    // Create user name label
    JLabel usernameLabel = new JLabel("Enter User Name:", JLabel.CENTER);
    usernameLabel.setBounds(0, 750, 150, 50);
    content.add(usernameLabel);

    // Create user name entry
    usernameField = new JTextField();
    usernameField.setBounds(150, 750, 350, 50);
    content.add(usernameField);
  }

  private void startScreen()
  {

  }

  public void chooseCategoriesScreen()
  {
    JPanel content = (JPanel) getContentPane();
    content.setBackground(Color.WHITE);
    content.removeAll();

    JButton educationButton = new JButton("Education / Work".toUpperCase());
    JButton favoritesButton = new JButton("Favorite things".toUpperCase());
    JButton storiesButton = new JButton("Interesting stories".toUpperCase());
    JButton familyButton = new JButton("Children / Pets".toUpperCase());
    JButton phrasesButton = new JButton("Catch Phrases / Quotes".toUpperCase());
    JButton freetimeButton = new JButton("Life outside work".toUpperCase());

    //play button
    JButton playButton = new JButton("Play!".toUpperCase());
    ImageFactory imageFactory = new ImageFactory(rf);
    BufferedImage icon = imageFactory.createBufferedImage("ic.png", 4);
    playButton.setIcon(new ImageIcon(icon.getScaledInstance(512 / 4, 512 / 4, 1)));

    levelButtons.clear();

    levelButtons.add(educationButton);
    levelButtons.add(favoritesButton);
    levelButtons.add(storiesButton);
    levelButtons.add(familyButton);
    levelButtons.add(phrasesButton);
    levelButtons.add(freetimeButton);

    int y = 0;
    for (JButton button : levelButtons)
    {
      button.setFont(new Font("Oswald", Font.BOLD, 15));
      button.setBackground(new Color(105, 0, 250));
      button.setForeground(Color.white);
      button.setUI(new StyledButtonUI());
      button.setBounds(20, 300 + y, 400, 60);
      button.addActionListener(this);
      y += 70;
      content.add(button);
    }

    //default
    educationButton.setBackground(new Color(69, 0, 132));
    selectedCategory = educationButton.getLabel();

    BufferedImage edImage = imageFactory.createBufferedImage("education.jpg", 4);
    JLabel levelPic = new JLabel(new ImageIcon(edImage));
    levelPic.setBounds(530, 300, 400, 400);
    content.add(levelPic);

    playButton.setFont(new Font("Impact", Font.BOLD, 50));
    playButton.setBackground(new Color(69, 0, 132));//223,210,170));
    playButton.setForeground(new Color(223, 210, 170));//Color.WHITE);
    playButton.setUI(new StyledButtonUI());
    playButton.setBounds(590, 730, 290, 60);
    playButton.addActionListener(this);
    content.add(playButton);

    Visualization v = new Visualization();
    Content home = cf.createContent("chooseCat.png");
    v.add(home);
    v.getView().setBounds(0, 0, 1000, 550);
    content.add(v.getView());

    content.revalidate();
    content.repaint();
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
    nameArea.setText(username + "'s Score.");
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

    JPanel panel = new JPanel();
    panel.setBackground(new Color(105, 0, 250));
    panel.setBounds(0, 750, 1000, 50);

    JButton playAgainButton = new JButton("Play Again - Same Category");
    playAgainButton.setFont(new Font("Oswald", Font.BOLD, 15));
    playAgainButton.setBounds(0, 750, 500, 100);
    playAgainButton.setBackground(Color.YELLOW);
    playAgainButton.addActionListener(this);
    playAgainButton.setUI(new StyledButtonUI());
    panel.add(playAgainButton);

    JButton levelsButton = new JButton("Play Again - Different Category");
    levelsButton.setFont(new Font("Oswald", Font.BOLD, 15));
    levelsButton.setBounds(500, 750, 520, 100);
    levelsButton.setBackground(Color.YELLOW);
    levelsButton.setUI(new StyledButtonUI());
    levelsButton.addActionListener(this);
    panel.add(levelsButton);

    content.add(panel);
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
      is = finder.findInputStream(file);

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
    } catch (Exception e)
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

    if (ac.equals("Start")) {
        addAvatars();
    }

    if (ac.equals("Choose Category") || ac.equals("Play Again - Different Category"))
    {
        chooseCategoriesScreen();
    }

    else if (categoryToQuestions.keySet().contains(ac)) //choosing a category
    {
      selectedCategory = ac;
      for (int i = 0; i < levelButtons.size(); i++)
      {
        if (levelButtons.get(i).getLabel().equals(ac))
        {

          for (JButton other : levelButtons)
          {
            other.setBackground(new Color(105, 0, 250));
          }
          levelButtons.get(i).setBackground(new Color(69, 0, 132));
          break;
        }
      }
    }

    else if (ac.equals("PLAY!") || ac.equals("Play Again")
        || ac.equals("Play Again - Same Category"))
    {
        startGame();
    } else if (ac.equals("Submit Choice"))
    {
        resultScreen();
    }

    else if (ac.equals("Next Question"))
    {
//      clip.stop();
      // Display question
      if (questionsAsked % 5 == 0)
      {
        content.removeAll();
        content.revalidate();
        content.repaint();
        displayScore();
        loadQuestions();
      } else
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

        int randQ = (int) (rand.nextDouble() * questionSet.size());
        Question q = questionSet.get(randQ);
        questionSet.remove(randQ);

        question.setFont(new Font("Times New Roman", Font.BOLD, 35));
        question.setText(q.getText());
        correctProfessor = q.getAnswer();
        question.setLineWrap(true);
        question.setBounds(350, 100, 600, 200);
        question.setOpaque(false);

        addProfessors(q);

        content.setBackground(Color.white);
        content.add(question);
        content.add(vis.getView());
        content.revalidate();
        content.repaint();
        met.stop();
      }
    }
  }

  @Override
  public void mouseClicked(MouseEvent arg0)
  {
    if(profList != null) {
      for (int i = 0; i < profList.size(); i++) {
        profList.get(i).setBackground(Color.white);
      }
    }

    for (int i = 0; i < avatars.size(); i++) {
      avatars.get(i).setBackground(new Color(104, 23, 250));
    }


    if(avatars.contains(arg0.getSource())) {
      avatar = (VisualizationView) arg0.getSource();
      avatar.setBackground(new Color(223, 210, 170));
    } else {
      chosen = (VisualizationView) arg0.getSource();
      chosen.setBackground(new Color(104, 23, 250));
    }

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

  public void addAvatars()
  {
    JPanel content = (JPanel) getContentPane();

    avatars = new LinkedList<>();
    String[] pics = new String[4];
    pics[0] = "Pres.png";
    pics[1] = "David.png";
    pics[2] = "Bryan.png";

    content.removeAll();
    content.setBackground(Color.white);

    Content avatarScreen = cf.createContent("Avatar.png");
    avatarScreen.setLocation(0, 0);
    Content names = cf.createContent("Names.png");
    names.setLocation(0, 0);

    Visualization vis = new Visualization();
    vis.getView().setBounds(0, 0, 1000, 300);
    vis.add(avatarScreen);

    Visualization vis1 = new Visualization();
    vis1.getView().setBounds(0, 700, 1000, 50);
    vis1.add(names);

    content.add(vis.getView());
    content.add(vis1.getView());

    int x = 0;
    for(int i = 0; i < 3; i++) {
      Content avatr = cf.createContent(pics[i]);
      avatr.setLocation(0, 0);
      vis = new Visualization();
      vis.add(avatr);
      vis.getView().setBounds(x, 300, 334, 400);
      vis.addMouseListener(this);
      vis.setBackground(new Color(104, 23, 250));
      avatars.add(vis.getView());
      content.add(vis.getView());
      x = x + 333;
    }
    avatar = vis.getView();

    JButton start = new JButton("Choose Category");
    start.setBounds(0, 750, 1000, 50);
    start.addActionListener(this);
    content.add(start);

    content.revalidate();
    content.repaint();
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
      int index = (int) (rand.nextDouble() * 8); //8 not 10
      while (previous.contains(index))
      {
        index = (index + 1) % 8;
      }
      previous.add(index);

      // If random index is greater than or equal to 5 and not added
      // add the correct answer to vis. Otherwise add other choices

      // Code duplication - yea
      Content prof;
      if (index >= 5 && notAdded || i == 3 && notAdded)
      {
        prof = cf.createContent(q.getAnswer().getImage());
        answer = new Visualization();
        answer.setBackground(Color.white);
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
        answer.setBackground(Color.white);
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

  public void startGame()
  {
      JPanel content = (JPanel) getContentPane();

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

      questionSet = categoryToQuestions.get(selectedCategory);

      int randQ = (int) (rand.nextDouble() * questionSet.size());
      Question q = questionSet.get(randQ);
      questionSet.remove(randQ);

      correctProfessor = q.getAnswer();
      question.setFont(new Font("Times New Roman", Font.BOLD, 40));
      question.setText(q.getText());
      question.setLineWrap(true);
      question.setBounds(350, 100, 600, 200);
      question.setOpaque(false);

      addProfessors(q);

      content.setBackground(Color.white);
      content.add(question);
      content.add(vis.getView());
      content.revalidate();
      content.repaint();
      met.stop();
  }

  public void resultScreen()
  {
      JPanel content = (JPanel) getContentPane();

      questionsAsked++;
      content.removeAll();
      JButton next = new JButton("Next Question");
      next.setBounds(0, 750, 1000, 50);
      next.addActionListener(this);
      Content c;

      if (chosen == correct)
      {
//        clip = initClip(correctProfessor.getAudioNameCorrect());
        score++;
      }
      else
      {
//        clip = initClip(correctProfessor.getAudioNameIncorrect());
      }

      TalkingProfessor tp = new TalkingProfessor(cf, correctProfessor);

      stage = new Stage(65);
      stage.getView().setBounds(0, 550, 200, 200);
      stage.add(cf.createContent(correctProfessor.getHeadImageName()));
      stage.add(tp);

      avatar.setLocation(700, 450);
      content.setBackground(Color.white);
      content.add(stage.getView());
      content.add(vis.getView());
      content.add(next);
      content.setBackground(new Color(104, 23, 250));
      content.add(avatar);
      content.revalidate();
      content.repaint();
      stage.start();

//      clip.start();
  }
}
