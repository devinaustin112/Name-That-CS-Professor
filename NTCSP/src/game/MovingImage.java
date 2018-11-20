package game;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class MovingImage extends AbstractSprite{
    private double maxX, maxY, x, y, origin;
    private visual.statik.sampled.Content content;

    MovingImage(visual.statik.sampled.Content content, double x, double y) {
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
