package model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import java.awt.Graphics;

/**
 * Creates a styled button.
 *
 * @author Christy Kobert, Chris Williams, Devin Dyer, Nkeng Atabong
 * @version 1.0 - December 3, 2018
 *
 * This work complies with the JMU Honor Code.
 */
public class StyledButtonUI extends BasicButtonUI {

  @Override
  public void installUI (JComponent c) {
      super.installUI(c);
      AbstractButton button = (AbstractButton) c;
      button.setOpaque(false);
      button.setBorder(new EmptyBorder(5, 15, 5, 15));
  }

  @Override
  public void paint (Graphics g, JComponent c) {
      AbstractButton b = (AbstractButton) c;
      paintBackground(g, b, b.getModel().isPressed() ? 2 : 0);
      super.paint(g, c);
  }

  private void paintBackground (Graphics g, JComponent c, int yOffset) {
      Dimension size = c.getSize();
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setColor(c.getBackground().darker());
      g.fillRoundRect(0, yOffset, size.width, size.height - yOffset, 10, 10);
      g.setColor(c.getBackground());
      g.fillRoundRect(0, yOffset, size.width, size.height + yOffset - 5, 10, 10);
  }
}
