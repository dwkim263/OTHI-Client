package othi.thg.client;

/**
 * defining game default gui colors
 * @author Dong Won Kim
 */

import org.newdawn.slick.Color;
import org.newdawn.slick.AngelCodeFont;
import mdes.slick.sui.SuiTheme;
import org.newdawn.slick.SlickException;

public class THGTheme implements SuiTheme {

    //alpha of 85
  /** The button top color (up). */
    private final Color p1 = new Color(56, 102, 37);

  /** The button bottom color. */
    private final Color p2 = new Color(25, 46, 16);

  /** The button top color (rollover). */
    private final Color p3 = new Color(86, 164, 54);

  /** The titlebar and button text color. */
    private final Color p4 = new Color (240, 255, 255);
    
   // alpha of 85
   /** The titlebar top color.   */
    private final Color s1 = new Color(56, 102, 37);

   /** The titlebar bottom color */
    private final Color s2 = new Color(51, 65, 45);

   /** The border & button top color (down). */
   //  private final Color s3 = new Color(34, 64, 20);

   /** The border & button top color (down). */
    private final Color s3 = new Color(123, 130, 149);
    
    /** The text and window resizer color. */
    private final Color txt = new Color(0, 0, 7);

    /** TextField background color. */
    private final Color txtBack = new Color(255, 255, 255);

    /** The background color (down). */
    //private final Color s4 = Color.lightGray;

    /** The background color. */
    private final Color s4 = new Color(239, 243, 255);

    /** The text and window resizing triangle color. */

    private final Color shadow = new Color(182, 183, 186);

    private final Color disabled = Color.gray;

  //private final String SYSTEM_FONT_DATA = "Segoeui16.fnt";
  //private final String SYSTEM_FONT_IMG = "Segoeui16_00.png";

    private final String SYSTEM_FONT_DATA = "Tahoma16.fnt";
    private final String SYSTEM_FONT_IMG = "Tahoma16_00.png";

    private final String LOGIN_FONT_DATA = "Arial32.fnt";
    private final String LOGIN_FONT_IMG = "Arial32_00.png";
    private final String EDIT_FONT_DATA = "Copperplate_gothic_bold16.fnt";
    private final String EDIT_FONT_IMG = "Copperplate_gothic_bold16_00.png";
    private final String SYSTEM_MSG_FONT_DATA = "Copperplate_gothic_bold32.fnt";
    private final String SYSTEM_MSG_FONT_IMG = "Copperplate_gothic_bold32_00.png";
    private final String USER_NAME_42_DATA = "Copperplate_gothic_bold42.fnt";
    private final String USER_NAME_42_IMG = "Copperplate_gothic_bold42_00.png";

    private AngelCodeFont systemFont;
    private AngelCodeFont loginFont;
    private AngelCodeFont editFont;
    private AngelCodeFont systemMsgFont;
    private AngelCodeFont userName42;


    public THGTheme() {
      //sets up alpha values
      p1.a = 0.85f;
      p2.a = 0.85f;
      p3.a = 0.85f;
      
      s1.a = 0.85f;
      s2.a = 0.85f;
      s3.a = 0.85f;

      loadFonts();      
    }

    private void loadFonts() {
        try{
            systemFont =
                new AngelCodeFont("font/" + SYSTEM_FONT_DATA, "font/" + SYSTEM_FONT_IMG);
            loginFont =
                new AngelCodeFont("font/" + LOGIN_FONT_DATA, "font/" + LOGIN_FONT_IMG);
            editFont =
                new AngelCodeFont("font/" + EDIT_FONT_DATA, "font/" + EDIT_FONT_IMG);
            systemMsgFont =
                new AngelCodeFont("font/" + SYSTEM_MSG_FONT_DATA, "font/" + SYSTEM_MSG_FONT_IMG);
            userName42 =
                new AngelCodeFont("font/" + USER_NAME_42_DATA, "font/" + USER_NAME_42_IMG);

        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** The name of the theme. */
    @Override
    public String getName() { return "DefaultTHOTheme"; }
    
    /** The button top color (up). */
    @Override
    public Color getPrimary1() { return p1; }
    
    /** The button bottom color. */
    @Override
    public Color getPrimary2() { return p2; }
    
    /** The button top color (rollover). */
    @Override
    public Color getPrimary3() { return p3; }

    /** The button text color. */
    @Override
    public Color getPrimary4() { return p4; }

    /** The titlebar top color. */
    @Override
    public Color getSecondary1() { return s1; }
    
    /** The titlebar bottom color. */
    @Override
    public Color getSecondary2() { return s2; }
    
    /** The border & button top color (down). */
    @Override
    public Color getSecondary3() { return s3; }   

  /** Shadow color
   *  Use mdes.slick.sui.SuiWindow .
   */
        @Override
    public Color getShadow() { return shadow;}

        @Override
    public Color getBackground() { return s4; }

  /**
   * The text color.
   * <p>
   * Will soon be removed.
   * Use mdes.slick.sui.SuiLabel#setForeground(Color)
   */
        
  /** The text and window resizer color. */
        @Override
    public Color getForeground() { return txt; }

        @Override
    public Color getTextBackground() { return txtBack; }

        @Override
    public Color getDisabled() { return disabled; }

    @Override
    public AngelCodeFont getEditFont() {
        return editFont;
    }

    @Override
    public AngelCodeFont getLoginFont() {
        return loginFont;
    }

    @Override
    public AngelCodeFont getSystemFont() {
        return systemFont;
    }

    @Override
    public AngelCodeFont getSystemMsgFont() {
        return systemMsgFont;
    }

    @Override
    public AngelCodeFont getUserName42() {
        return userName42;
    }        
}    
