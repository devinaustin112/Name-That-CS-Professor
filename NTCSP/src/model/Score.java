package model;

import javax.swing.JPanel;

public class Score
{
    protected int score;

    public Score()
    {
        this.score = 0;
    }

    public Score(int score)
    {
        this.score = score;
    }

    public int getScore()
    {
        return score;
    }

    public void updateScore(int change)
    {
        score += change;
    }

    public void reset()
    {
        score = 0;
    }

    public void displayScore(JPanel contentPane)
    {
        // Default display
    }
}
