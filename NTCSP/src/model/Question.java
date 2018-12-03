
package model;

/**
 * Question class stores all infromation about a Question
 * @author Christy Kobert, Chris Williams, Devin Dyer, Nkeng Atabong
 * @version 1.0 - December 3, 2018
 *
 * This work complies with the JMU Honor Code.
 */
public class Question
{
  String category;
  String questionText;
  Professor answer;

  /**
   * Explicit Value Constructor
   *
   * @param category The category of the question.
   * @param txt The text of the question (the question itself).
   * @param prof The professor associated with this question (the answer).
   */
  public Question(String category, String txt, Professor prof)
  {
    this.category = category;
    this.questionText = txt;
    this.answer = prof;
  }

  /**
   * Gets the answer to this question.
   *
   * @return The answer to this question.
   */
  public Professor getAnswer() { return answer; }

  /**
   * Gets the text of the question.
   *
   * @return The text of the question.
   */
  public String getText()
  {
    return questionText;
  }

  /**
   * Sets the category for this question.
   *
   * @param c The category for this question.
   */
  public void setCategory(String c)
  {
    this.category = c;
  }

  /**
   * Formats this question into question:category:answer.
   *
   * @return The formatted String.
   */
  public String toString()
  {
    return questionText + ":" + category + ":" + answer;
  }
}
