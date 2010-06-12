package othi.thg.client.gui;

import org.newdawn.slick.Image;
/**
 * cursor for talkable object (player sprite, NPC sprite) 
 * @author Dong Won Kim
 */
public class TalkableCursor extends AbstractCursor {
    
    public TalkableCursor(Image img) {
            this.img = img;
            width = img.getWidth();
            height = img.getHeight();
    }
}
