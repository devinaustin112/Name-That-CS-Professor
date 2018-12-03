package model;

import javax.swing.JPanel;

/**
 * Encapsulates a score for a game.
 *
 * @author Christy Kobert, Chris Williams, Devin Dyer, Nkeng Atabong
 * @version 1.0 - December 3, 2018
 *
 * This work complies with the JMU Honor Code.
 */
public class Score
{
    protected int score;

    /**
     * Default Value Constructor
     */
    public Score()
    {
        this.score = 0;
    }

    /**
     * Explicit Value Constructor
     *
     * @param score The starting score
     */
    public Score(int score)
    {
        this.score = score;
    }

    /**
     * Gets the current score
     *
     * @return The current score
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Updates the score based on the change provided (can add to or subtract from based on it the
     * parameter is negative or non-negative).
     *
     * @param change The change to make to the score.
     */
    public void updateScore(int change)
    {
        score += change;
    }

    /**
     * Resets the score to 0.
     */
    public void reset()
    {
        score = 0;
    }

    /**
     * Displays the score using the provided JPanel. This method does nothing and should be
     * overwritten in any children.
     *
     * @param contentPane The content pane to display to.
     */
    public void displayScore(JPanel contentPane)
    {
        // Default display does nothing
    }
}
