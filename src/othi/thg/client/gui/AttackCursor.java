package othi.thg.client.gui;

import org.newdawn.slick.Image;

public class AttackCursor extends AbstractCursor {
    
    public AttackCursor(Image img) {
            this.img = img;
            width = img.getWidth();
            height = img.getHeight();
    }
}
