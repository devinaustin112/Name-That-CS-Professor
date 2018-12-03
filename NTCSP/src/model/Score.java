package model;

import javax.swing.JPanel;

public abstract class Score
{
    protected int score;

    public Score()
    {
        this.score = 0;
    }

    public int getScore()
    {
        return score;
    }

    public void updateScore(int change)
    {
        score += change;
    }

    public abstract void displayScore(JPanel contentPane);
}
