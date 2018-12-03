package model;

import visual.Visualization;
import visual.VisualizationView;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

import javax.swing.*;
import java.awt.*;

public class TriviaScore extends Score
{
    public void displayScore(JPanel contentPane, VisualizationView avatar, String username, ContentFactory cf)
    {
        Content content;
        Visualization vis;

        content = cf.createContent("score.png");
        vis = new Visualization();
        vis.add(content);
        vis.getView().setBounds(0, 0, 1000, 750);

        JTextField nameArea = new JTextField();
        nameArea.setFont(new Font("Impact", Font.PLAIN, 40));
        nameArea.setForeground(Color.black);
        nameArea.setOpaque(false);
        nameArea.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        nameArea.setHorizontalAlignment(JTextField.CENTER);
        nameArea.setEditable(false);
        nameArea.setText(username + "'s Score:");
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

        avatar.setLocation(750, 570);
        contentPane.add(avatar);
        contentPane.add(scoreArea);
        contentPane.add(nameArea);
        contentPane.add(vis.getView());
    }
}
