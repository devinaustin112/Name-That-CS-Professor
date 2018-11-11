package model;

/**
 * Question class stores all infromation about a Question
 * @version Nov 7, 2018
 *
 * This work complies with the JMU Honor Code
 */
public class Question
{
  int level;
  String questionText;
  Professor answer;
  
  public Question(int level, String txt, Professor prof) 
  {
    this.level = level;
    this.questionText = txt;
    this.answer = prof;
  }

  public Professor getAnswer() { return answer; }
  
  public Professor getAnswer() {
	  return answer;
  }
  
  public String getText() {
    return questionText;
  }
  
  public String toString() {
    return questionText + ":" + level + ":" + answer;
  }
}
