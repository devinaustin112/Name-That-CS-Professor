package model;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

/**
 * Encapsulates a MovingImage that makes an image appear to slide across the bottom of the screen
 *
 * @author Christy Kobert, Chris Williams, Devin Dyer, Nkeng Atabong
 * @version 1.0 - December 3, 2018
 *
 * This work complies with the JMU Honor Code.
 */
public class MovingImage extends AbstractSprite{
    private double maxX, maxY, x, y, origin;
    private visual.statik.sampled.Content content;

    public MovingImage(visual.statik.sampled.Content content, double x, double y) {
        super();
        this.content = content;
        this.x = x;
        this.y = y;
        this.origin = x;
        setVisible(true);
    }

    @Override
    protected TransformableContent getContent() {
        return content;
    }

    @Override
    public void handleTick(int i) {
        if(x == -1960) {
            x = 1960;
        }

        setLocation(x = x - 2, y);
    }
}
