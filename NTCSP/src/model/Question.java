
package model;

/**
 * Question class stores all infromation about a Question
 * @version Nov 7, 2018
 *
 * This work complies with the JMU Honor Code
 */
public class Question
{
  String category;
  String questionText;
  Professor answer;
  
  public Question(String level, String txt, Professor prof) 
  {
    this.category = level;
    this.questionText = txt;
    this.answer = prof;
  }

  public Professor getAnswer() { return answer; }

  public String getText() {
    return questionText;
  }
  
  public void setCategory(String c) {
    this.category = c;
  }
  
  public String toString() {
    return questionText + ":" + category + ":" + answer;
  }
}
