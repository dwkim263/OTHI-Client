/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.client.gui;

import org.newdawn.slick.AngelCodeFont;

/**
 * Notice window
 * @author Dong Won Kim
 */
public class NoticeWindow extends ScrollCloseWindow {
    
    public NoticeWindow(AngelCodeFont font) {
        super(font, ScrollWindow.getSCROLLWINDOW_WIDTH(),
                 ScrollWindow.getSCROLLWINDOW_HEIGHT()/4);
        formDefaultComponents();
    }
}
