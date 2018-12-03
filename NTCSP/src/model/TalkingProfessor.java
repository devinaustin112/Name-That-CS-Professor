package model;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;
import visual.statik.sampled.Content;
import visual.statik.sampled.ContentFactory;

public class TalkingProfessor extends AbstractSprite {
    double x, y, origin;
    Content content;

    public TalkingProfessor(ContentFactory cf, Professor p) {

        super();
        content = cf.createContent(p.getMouthImageName());;
        switch (p.getHeadImageName()) {
            case "Bowers.png":

                this.x = 82;
                this.y = 136;
                origin = 136;
                break;
            case "Lam.png":
                this.x = 53;
                this.y = 139;
                origin = 139;
                break;
            case "Bernstein.png":
                this.x = 87;
                this.y = 134;
                origin = 134;
                break;
            case "Mayfield.png":
                this.x = 59;
                this.y = 146;
                origin = 146;
                break;
            case "Stewart.png":
                this.x = 88;
                this.y = 139;
                origin = 139;
                break;
            case "Norton.png":
                this.x = 96;
                this.y = 159;
                origin = 159;
                break;
            case "Fox.png":
                this.x = 92;
                this.y = 117;
                origin = 117;
                break;
            case "Weikle.png":
                this.x = 102;
                this.y = 121;
                origin = 121;
                break;
            case "Aboutabl.png":
                this.x = 69;
                this.y = 104;
                origin = 104;
                break;
            case "Kirkpatrick.png":
                this.x = 86;
                this.y = 101;
                origin = 101;
                break;
        }
        setLocation(x, y);
        setVisible(true);
    }

    @Override
    protected TransformableContent getContent() {
        return content;
    }

    @Override
    public void handleTick(int i) {
        if(y > origin + 4 ){
            y = origin;
        } else {
            y = y + 2;
        }
        setLocation(x, y);
    }
}
