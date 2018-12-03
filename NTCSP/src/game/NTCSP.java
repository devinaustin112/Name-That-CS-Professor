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
import java.util.LinkedList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

import app.*;
import io.ResourceFinder;
import resources.Marker;
import visual.*;
import visual.dynamic.described.Stage;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;
import visual.statik.sampled.ImageFactory;

/**
 * Encapsulates the game "Name That CS Professor".
 *
 * @author Christy Kobert, Chris Williams, Devin Dyer, Nkeng Atabong
 * @version 1.0 - December 3, 2018
 *
 * This work complies with the JMU Honor Code.
 */
public class NTCSP extends JApplication implements ActionListener, MouseListener
{
  // Category types
  private static final String EW = "EDUCATION / WORK";
  private static final String FT = "FAVORITE THINGS";
  private static final String IP = "INTERESTING STORIES";
  private static final String FP = "CHILDREN / PETS";
  private static final String CP = "CATCH PHRASES / QUOTES";
  private static final String LW = "LIFE OUTSIDE WORK";

  // Non-category constants
  private static final String FONT_NAME = "Impact";
  private static final String NEXT_Q = "Next Question";
  private static final String PLAY = "PLAY!";
  private static final String PLAY_AGAIN_SAME = "Play Again - Same Category";
  private static final String PLAY_AGAIN_DIFF = "Play Again - Different Category";
  private static final String START = "Start";
  private static final String SUBMIT = "Submit Choice";
  private static final String SUBMIT_AV = "Submit Avatar Choice";

  int questionsAsked;
  ArrayList<JButton> catButtons;
  ArrayList<Professor> professors;
  Clip clip;
  ContentFactory cf;
  HashMap<String, ArrayList<Question>> categoryToQuestions;
  ImageFactory ifa;
  InputStream is;
  JTextField usernameField;
  JTextArea question;
  LinkedList<Visualization> profList;
  LinkedList<VisualizationView> avatars;
  Professor correctProfessor;
  Random rand;
  ResourceFinder rf;
  Stage stage;
  String username, selectedCategory;
  TriviaScore score;
  Visualization vis;
  VisualizationView chosen, correct, avatar;

  /**
   * Explicit Value Constructor.
   *
   * @param width The width of the window
   * @param height The height of the window
   */
  public NTCSP(int width, int height)
  {
    super(width, height);
    categoryToQuestions = new HashMap<>();
    catButtons = new ArrayList<>();
    questionsAsked = 0;
    rand = new Random(System.currentTimeMillis());
    score = new TriviaScore();
  }

  /**
   * Main entry point to the application.
   *
   * @param args Command-line arguments
   */
  public static void main(String[] args)
  {
    invokeInEventDispatchThread(new NTCSP(1000, 800));
  }

  /**
   * Displays the opening start screen where users can enter their username and start the game.
   */
  private void displayStartScreen()
  {
    Content c, home;
    JPanel content;
    MovingImage mi1, mi2;
    VisualizationView stageView;

    // Get the content pane
    content = (JPanel) getContentPane();

    // Initialize ResourceFinder to get resources
    rf = ResourceFinder.createInstance(Marker.class);
    cf = new ContentFactory(rf);
    ifa = new ImageFactory(rf);

    // Create sliding banner of professor images
    c = cf.createContent("professors.png");
    mi1 = new MovingImage(c, 0, 0);
    mi2 = new MovingImage(c, 1960, 0);
    stage = new Stage(50);
    stageView = stage.getView();
    stageView.setBounds(0, 550, 1000, 200);

    // Add start screen main image
    vis = new Visualization();
    home = cf.createContent("NTCSP-1.png");
    vis.add(home);
    vis.getView().setBounds(0, 0, 1000, 550);

    stage.addView(vis.getView());
    stage.add(mi1);
    stage.add(mi2);
    content.add(stage.getView());
    content.add(vis.getView());

    // Display field to enter username
    displayUsernameOptions();

    // Create start button
    JButton start = new JButton(START);
    start.setFont(new Font(FONT_NAME, Font.PLAIN, 30));
    start.setBounds(width / 2, 750, 500, 50);
    start.addActionListener(this);
    content.add(start);
  }

  public void init()
  {
    // Display the game's start screen
    displayStartScreen();

    // Load questions from file
    loadQuestions();

    stage.start();
  }

  /**
   * Helper method that loads a list of categorized questions from a text file and sorts them by
   * category then puts them in the HashMap of questions.
   */
  private void loadQuestions()
  {
    ArrayList<Question> educationQs, favoriteQs, storiesQs, phrasesQs, familyQs, freetimeQs, bonusQs;
    BufferedReader in;
    Professor prof;
    Question q;
    String l, line, qu;

    // Initialize lists for questions
    professors = new ArrayList<>();
    educationQs = new ArrayList<>();
    favoriteQs = new ArrayList<>();
    storiesQs = new ArrayList<>();
    phrasesQs = new ArrayList<>();
    familyQs = new ArrayList<>();
    freetimeQs = new ArrayList<>();
    bonusQs = new ArrayList<>();
    prof = null;

    // Initialize the input stream to read from the file containing the questions
    is = rf.findInputStream("Quest.txt");
    in = new BufferedReader(new InputStreamReader(is));

    try
    {
      while ((line = in.readLine()) != null)
      {
        // Lines starting with a digit contain the professor's name
        if (Character.isDigit(line.charAt(0)))
        {
          prof = new Professor(line.substring(2, line.length()));
          professors.add(prof);
          continue;
        }

        // Get the first two characters of the line containing the code identifying the category
        // of question and add that question to its specified category
        l = line.substring(0, 2);
        qu = line.substring(line.indexOf('-') + 2, line.length());
        q = new Question(l, qu, prof);

        switch (l)
        {
          // Education/Work
          case "EW":
            educationQs.add(q);
            break;
          //Favorite things
          case "FT":
            favoriteQs.add(q);
            break;
          // Interesting stories
          case "IP":
            storiesQs.add(q);
            break;
          // Catch phrases
          case "CP":
            phrasesQs.add(q);
            break;
          // Family life
          case "FP":
            familyQs.add(q);
            break;
          // Activities in free time
          case "LW":
            freetimeQs.add(q);
            break;
          // Bonus questions - may be added in later iteration
          case "BO":
            bonusQs.add(q);
            break;
          default:
            System.err.println("Not an accurate level: " + line);
        }
      }

      // Put the categorized questions into the larger HashMap containing all of the questions
      categoryToQuestions.put(EW, educationQs);
      categoryToQuestions.put(FT, favoriteQs);
      categoryToQuestions.put(IP, storiesQs);
      categoryToQuestions.put(FP, familyQs);
      categoryToQuestions.put(CP, phrasesQs);
      categoryToQuestions.put(LW, freetimeQs);

    } catch (IOException e)
    {
      System.err.println("Questions could not be loaded into game.");
    }
  }

  /**
   * Helper method that displays the text box to enter a username and button to start playing.
   */
  private void displayUsernameOptions()
  {
    JPanel content = (JPanel) getContentPane();

    // Create user name label
    JLabel usernameLabel = new JLabel("Enter Name:", JLabel.CENTER);
    usernameLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
    usernameLabel.setBounds(0, 750, 150, 50);
    content.add(usernameLabel);

    // Create user name entry
    usernameField = new JTextField();
    usernameField.setHorizontalAlignment(JTextField.CENTER);
    usernameField.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
    usernameField.setBounds(150, 750, 350, 50);
    content.add(usernameField);
  }

  /**
   * Displays the Choose Category screen where each category will have a button and the user must
   * choose which category they want to play.
   */
  public void displayChooseCategoriesScreen()
  {
    BufferedImage icon, edImage;
    Content home;
    ImageFactory imageFactory;
    JButton educationButton, favoritesButton, storiesButton, familyButton, phrasesButton,
            freetimeButton, playButton;
    JLabel levelPic;
    JPanel content;
    Visualization v;

    // Get the content pane
    content = (JPanel) getContentPane();
    content.setBackground(Color.WHITE);
    content.removeAll();

    // Create the buttons for each category
    educationButton = new JButton(EW);
    favoritesButton = new JButton(FT);
    storiesButton = new JButton(IP);
    familyButton = new JButton(FP);
    phrasesButton = new JButton(CP);
    freetimeButton = new JButton(LW);

    // Create the play button that will start the game displaying questions
    playButton = new JButton(PLAY);
    imageFactory = new ImageFactory(rf);
    icon = imageFactory.createBufferedImage("ic.png", 4);
    playButton.setIcon(new ImageIcon(icon.getScaledInstance(512 / 4, 512 / 4, 1)));

    // Add the category buttons to the content pane
    catButtons.clear();
    catButtons.add(educationButton);
    catButtons.add(favoritesButton);
    catButtons.add(storiesButton);
    catButtons.add(familyButton);
    catButtons.add(phrasesButton);
    catButtons.add(freetimeButton);

    // Set design for category buttons moving them down 70 pixels between each button
    int y = 0;
    for (JButton button : catButtons)
    {
      button.setFont(new Font(FONT_NAME, Font.BOLD, 15));
      button.setBackground(new Color(105, 0, 250));
      button.setForeground(Color.white);
      button.setUI(new StyledButtonUI());
      button.setBounds(20, 300 + y, 400, 60);
      button.addActionListener(this);
      y += 70;
      content.add(button);
    }

    // Set the default image beside the categories - this may change based on category in later
    // iterations (very low priority)
    educationButton.setBackground(new Color(69, 0, 132));
    selectedCategory = educationButton.getLabel();
    edImage = imageFactory.createBufferedImage("education.jpg", 4);
    levelPic = new JLabel(new ImageIcon(edImage));
    levelPic.setBounds(530, 300, 400, 400);
    content.add(levelPic);

    // Set design for play button
    playButton.setFont(new Font(FONT_NAME, Font.BOLD, 50));
    playButton.setBackground(new Color(69, 0, 132));
    playButton.setForeground(new Color(223, 210, 170));
    playButton.setUI(new StyledButtonUI());
    playButton.setBounds(590, 730, 290, 60);
    playButton.addActionListener(this);
    content.add(playButton);

    // Display the page
    v = new Visualization();
    home = cf.createContent("chooseCat.png");
    v.add(home);
    v.getView().setBounds(0, 0, 1000, 550);
    content.add(v.getView());
    content.revalidate();
    content.repaint();
  }

  /**
   * Displays the score screen. This method delegates to the Score object's displayScore() method
   * to display the main score and then creates options to continue playing the game.
   */
  public void displayScore()
  {
    JPanel content = (JPanel) getContentPane();

    // Display the score at the top of the screen
    score.displayScore(content, avatar, username, cf);

    // Display options at the bottom of the score screen
    JPanel panel = new JPanel();
    panel.setBackground(Color.white);
    panel.setBounds(0, 750, 1000, 50);

    // Button to play again in the same category
    JButton playAgainButton = new JButton(PLAY_AGAIN_SAME);
    playAgainButton.setFont(new Font(FONT_NAME, Font.BOLD, 15));
    playAgainButton.setBounds(0, 750, 500, 100);
    playAgainButton.setBackground(new Color(223, 210, 170));
    playAgainButton.addActionListener(this);
    playAgainButton.setUI(new StyledButtonUI());
    panel.add(playAgainButton);

    // Button to play again in a different category
    JButton levelsButton = new JButton(PLAY_AGAIN_DIFF);
    levelsButton.setFont(new Font(FONT_NAME, Font.BOLD, 15));
    levelsButton.setBounds(500, 750, 520, 100);
    levelsButton.setBackground(new Color(223, 210, 170));
    levelsButton.setUI(new StyledButtonUI());
    levelsButton.addActionListener(this);
    panel.add(levelsButton);

    content.add(panel);
  }

  /**
   * Helper method to initialize a clip with the given audio file as a String.
   * This code was provided by Dr. Bernstein in his sample code.
   *
   * @param file The String containing the file name to play
   * @return The created Clip object from the file
   */
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

      // Make the clip loop "forever" until we stop it
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    catch (Exception e)
    {
      System.err.println("Unable to load and play audio clip.");
      clip = null;
    }

    return clip;
  }

  /**
   * Displays the Choose Avatar page where users choose which avatar they want to play as.
   */
  public void displayChooseAvatar()
  {
    Content avatarScreen, names;
    JPanel content;
    Visualization vis, vis1;

    content = (JPanel) getContentPane();

    // Create list of avatars to choose from
    avatars = new LinkedList<>();
    String[] pics = new String[4];
    pics[0] = "Pres.png";
    pics[1] = "David.png";
    pics[2] = "Bryan.png";

    // Create avatar screen
    content.removeAll();
    content.setBackground(Color.white);
    avatarScreen = cf.createContent("Avatar.png");
    avatarScreen.setLocation(0, 0);
    names = cf.createContent("Names.png");
    names.setLocation(0, 0);

    vis = new Visualization();
    vis.getView().setBounds(0, 0, 1000, 300);
    vis.add(avatarScreen);

    vis1 = new Visualization();
    vis1.getView().setBounds(0, 700, 1000, 50);
    vis1.add(names);

    content.add(vis.getView());
    content.add(vis1.getView());

    // Display each avatar and set properties
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

    // Create start button to submit avatar choice
    JButton start = new JButton(SUBMIT_AV);
    start.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
    start.setBounds(0, 750, 1000, 50);
    start.addActionListener(this);
    content.add(start);

    content.revalidate();
    content.repaint();
  }

  /**
   * Helper methods to add professors to each question as answer choices. The correct professor
   * must be one of the options, the other 3 are randomly chosen.
   *
   * @param q The question being displayed
   */
  public void addProfessors(Question q)
  {
    ArrayList<Integer> previous;
    ArrayList<Professor> otherChoices;
    JPanel content;

    content = (JPanel) getContentPane();

    if (profList != null)
    {
      for (int i = 0; i < profList.size(); i++)
      {
        content.remove(profList.get(i).getView());
      }
    }

    profList = new LinkedList<>();
    otherChoices = new ArrayList<>(professors);
    otherChoices.remove(q.getAnswer());

    previous = new ArrayList<>();

    // Randomly choose professors to add
    Visualization answer;
    int x = 0;
    boolean notAdded = true;
    for (int i = 0; i < 4; i++)
    {
      // Get random between 0 and 8 non repeating
      int index = (int) (rand.nextDouble() * 8);
      while (previous.contains(index))
      {
        index = (index + 1) % 8;
      }
      previous.add(index);

      // If random index is greater than or equal to 5 and not added
      // add the correct answer to vis. Otherwise add other choices
      Content prof;
      if (index >= 5 && notAdded || i == 3 && notAdded)
      {
        prof = cf.createContent(q.getAnswer().getImage());
        answer = new Visualization();
        answer.setBackground(Color.white);
        answer.addMouseListener(this);
        prof.setLocation(25, 0);
        answer.add(prof);
        answer.getView().setBounds(x, 420, 250, 200);
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
        answer.getView().setBounds(x, 420, 250, 200);
        content.add(answer.getView());
        profList.add(answer);
        x = x + 250;
      }
    }
    previous.clear();
  }

  /**
   * Starts the playing of the game by displaying a question and 4 professor images as answer
   * choices.
   */
  public void startGame()
  {
    JPanel content = (JPanel) getContentPane();

    // Store username
    username = usernameField.getText();

    //Reset game
    score.reset();
    content.removeAll();

    // Create button to submit answer
    JButton submit = new JButton(SUBMIT);
    submit.setFont(new Font(FONT_NAME, Font.PLAIN, 30));
    submit.setBounds(0, 750, 1000, 50);
    submit.addActionListener(this);
    content.add(submit);

    Content qContent = cf.createContent("question.png");

    Visualization vis = new Visualization();
    vis.add(qContent);
    vis.getView().setBounds(0, 0, 1000, 300);

    question = new JTextArea();
    question.setBackground(new Color(105, 0, 250));
    question.setForeground(new Color(0, 0, 0));
    question.setEditable(false);

    // Check if there are questions left, if not, reload questions
    if(categoryToQuestions.get(selectedCategory).size() <= 0) {
      loadQuestions();
    }

    // Randomly select next question
    int randQ = (int) (rand.nextDouble() * categoryToQuestions.get(selectedCategory).size());
    Question q = categoryToQuestions.get(selectedCategory).get(randQ);
    categoryToQuestions.get(selectedCategory).remove(randQ);

    // Get the correct answer to the question
    correctProfessor = q.getAnswer();

    // Set design for question display
    question.setFont(new Font(FONT_NAME, Font.PLAIN, 40));
    question.setText(q.getText());
    question.setLineWrap(true);
    question.setWrapStyleWord(true);
    question.setBounds(350, 60, 600, 200);
    question.setOpaque(false);

    // Add the professors (answer choices) to the display
    addProfessors(q);

    content.setBackground(Color.white);
    content.add(question);
    content.add(vis.getView());
    content.revalidate();
    content.repaint();
  }

  /**
   * Display the result screen after each question to show the correct professor and play their
   * audio clip.
   */
  public void resultScreen()
  {
    JPanel content = (JPanel) getContentPane();

    questionsAsked++;
    content.removeAll();

    // Create button to go to next question (leaving the result screen)
    JButton next = new JButton(NEXT_Q);
    next.setFont(new Font(FONT_NAME, Font.PLAIN, 30));
    next.setBounds(0, 750, 1000, 50);
    next.addActionListener(this);
    Content c;
    Content qContent = cf.createContent("question.png");

    Visualization vis = new Visualization();
    vis.add(qContent);
    vis.getView().setBounds(0, 0, 1000, 300);

    Visualization vis1 = new Visualization();
    vis1.getView().setBounds(150, 380, 615, 180);

    // Whenever an answer is submitted, the correct professor will be displayed. The audio that
    // is played will depend on if the answer was correct or incorrect.
    if (chosen == correct)
    {
      c = cf.createContent("Correct.png");
      c.setLocation(0, 0);
      vis1.add(c);

      // Get the correct audio for the professor
      clip = initClip(correctProfessor.getAudioNameCorrect());

      // Add one to the score
      score.updateScore(1);
    }
    else
    {
      c = cf.createContent("Incorrect.png");
      c.setLocation(0, 0);
      vis1.add(c);

      // Get the incorrect audio for the professor
      clip = initClip(correctProfessor.getAudioNameIncorrect());
    }

    // Make the professor "talk" by having their mouth move up and down (old-cartoon style talking)
    TalkingProfessor tp = new TalkingProfessor(cf, correctProfessor);

    // Set the display
    stage = new Stage(65);
    stage.getView().setBounds(0, 550, 200, 200);
    stage.add(cf.createContent(correctProfessor.getHeadImageName()));
    stage.add(tp);

    // Add the user's avatar to the result page
    avatar.setLocation(700, 450);
    content.setBackground(Color.white);
    content.add(vis1.getView());
    content.add(stage.getView());
    content.add(vis.getView());
    content.add(next);
    content.setBackground(Color.white);
    content.add(avatar);
    content.revalidate();
    content.repaint();

    // Start the stage and the audio clip
    stage.start();
    clip.start();
  }

  /**
   * Handle an action being performed
   *
   * @param arg0 The ActionEvent object containing the action that occurred
   */
  @Override
  public void actionPerformed(ActionEvent arg0)
  {
    JPanel content = (JPanel) getContentPane();
    String ac = arg0.getActionCommand();

    // Transition from start screen to choose avatar screen
    if (ac.equals(START)) {
      displayChooseAvatar();
    }

    // Transition from choose avatar screen OR score screen to choose category screen
    if (ac.equals(SUBMIT_AV) || ac.equals(PLAY_AGAIN_DIFF))
    {
      displayChooseCategoriesScreen();
    }
    // Category was chosen so highlight the category
    else if (categoryToQuestions.keySet().contains(ac)) //choosing a category
    {
      selectedCategory = ac;
      for (int i = 0; i < catButtons.size(); i++)
      {
        if (catButtons.get(i).getLabel().equals(ac))
        {

          for (JButton other : catButtons)
          {
            other.setBackground(new Color(105, 0, 250));
          }
          catButtons.get(i).setBackground(new Color(69, 0, 132));
          break;
        }
      }
    }
    // Transition from choose category screen OR play score screen to playing the game
    else if (ac.equals(PLAY) || ac.equals(PLAY_AGAIN_SAME))
    {
      startGame();
    }
    // Transition from question screen to result (of the question) screen
    else if (ac.equals(SUBMIT))
    {
      resultScreen();
    }
    // Transition from result (of the question) screen to the next question if they have answered
    // less than 5 questions and to the score screen if they have answered 5 questions
    else if (ac.equals(NEXT_Q))
    {
      // Stop playing the audio clip from the result screen
      clip.stop();

      // Check if they should display another question or go to the score screen
      if (questionsAsked % 5 == 0)
      {
        content.removeAll();
        content.revalidate();
        content.repaint();
        displayScore();
      }
      else
      {
        // Randomly choose the next question and display that question with the professors
        content.removeAll();
        JButton submit = new JButton(SUBMIT);
        submit.setFont(new Font(FONT_NAME, Font.PLAIN, 30));
        submit.setBounds(0, 750, 1000, 50);
        submit.addActionListener(this);
        content.add(submit);

        Content qContent = cf.createContent("question.png");

        vis = new Visualization();
        vis.add(qContent);
        vis.getView().setBounds(0, 0, 1000, 300);

        question = new JTextArea();
        question.setLineWrap(true);
        question.setWrapStyleWord(true);
        question.setBackground(new Color(105, 0, 250));
        question.setForeground(new Color(0, 0, 0));
        question.setEditable(false);

        if(categoryToQuestions.get(selectedCategory).size() <= 0){
          loadQuestions();
        }

        int randQ = (int) (rand.nextDouble() * categoryToQuestions.get(selectedCategory).size());
        Question q = categoryToQuestions.get(selectedCategory).get(randQ);
        categoryToQuestions.get(selectedCategory).remove(randQ);

        question.setFont(new Font(FONT_NAME, Font.PLAIN, 40));
        question.setText(q.getText());
        correctProfessor = q.getAnswer();
        question.setLineWrap(true);
        question.setBounds(350, 60, 600, 200);
        question.setOpaque(false);

        addProfessors(q);

        content.setBackground(Color.white);
        content.add(question);
        content.add(vis.getView());
        content.revalidate();
        content.repaint();
      }
    }
  }

  /**
   * Listens for MouseEvents to occur and when the mouse is clicked, highlights the professor
   * that was selected or the avatar that was selected.
   *
   * @param arg0 The MouseEvent that occurred
   */
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

  /**
   * Default - Required to be a MouseListener
   *
   * @param arg0 The MouseEvent that occurred
   */
  @Override
  public void mouseEntered(MouseEvent arg0)
  {
    // Default
  }

  /**
   * Default - Required to be a MouseListener
   *
   * @param arg0 The MouseEvent that occurred
   */
  @Override
  public void mouseExited(MouseEvent arg0)
  {
    // Default
  }

  /**
   * Default - Required to be a MouseListener
   *
   * @param arg0 The MouseEvent that occurred
   */
  @Override
  public void mousePressed(MouseEvent arg0)
  {
    // Default
  }

  /**
   * Default - Required to be a MouseListener
   *
   * @param arg0 The MouseEvent that occurred
   */
  @Override
  public void mouseReleased(MouseEvent arg0)
  {
    // Default
  }
}
