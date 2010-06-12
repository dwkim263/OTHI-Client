package othi.thg.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import org.newdawn.slick.AngelCodeFont;
import mdes.slick.sui.SuiLabel;

/**
 * The message log window and the answer area of a quiz window.
 * @author Dong Won Kim
 */
public class MessageLogWindow extends ScrollWindow {


    public MessageLogWindow(String title, Image titleImg, AngelCodeFont font, float x, float y, int width, int height) {
        super(font, x, y, width, height);

        setTitle(titleImg, title);
    }

    public MessageLogWindow(AngelCodeFont font, int width, int height) {
        super(font, width, height);

        //delete titlebar
        SuiLabel titleBar = getTitleBar();  //
        titleBar.remove(getCloseButton());  // titleBar is disabled
        setTitleBar(titleBar);              // by setting its size as 0.
    }

    public void setDefalutMessageLogTheme() {
        Color bg = new Color(getBackgroundColor());
        bg.a = .70f;
        setBackgroundColor(bg);
    }

    @Override
    public boolean hasFocus(){
        if (hasTitleBar()) {
            boolean vsDownHasFocuse = getVsDown() == null ? false : getVsDown().hasFocus();
            boolean vsUpHasFocuse = getVsUp() == null ? false : getVsUp().hasFocus();
            return titlePane.hasFocus() || vsDownHasFocuse || vsUpHasFocuse;
        } else {
            return super.hasFocus();
        }
    }

    public void formDefaultComponents() {
        int textAreaHeight = getHeight()- getTextArea().getPadding() - 5;
        addTextArea(textAreaHeight);
    }

}