package mdes.slick.sui;

import org.newdawn.slick.*;

/** 
 * Buttons, text fields and windows are rendered
 * using the colors defined in the current SuiTheme. 
 * <p>
 * Themes in Sui will be reworked shortly.
 *
 * @author davedes
 * @since b.0.1
 * @see mdes.slick.sui.Sui#setTheme(SuiTheme)
 */
public interface SuiTheme {
  
    /** The name of the theme. */
    public String getName();

    /** The button top color (up). */
    public Color getPrimary1();

    /** The button bottom color. */
    public Color getPrimary2();

    /** The button top color (rollover). */
    public Color getPrimary3();

    /** The button text color. */
    public Color getPrimary4();

    /** The titlebar top color. */
    public Color getSecondary1();

    /** The titlebar bottom color. */
    public Color getSecondary2();

    /** The border & button top color (down). */
    public Color getSecondary3();

    /** Shadow color
    *  Use mdes.slick.sui.SuiWindow .
    */
    public Color getShadow();

    public Color getBackground();

    /**
    * The text color.
    * <p>
    * Will soon be removed.
    * Use mdes.slick.sui.SuiLabel#setForeground(Color)
    */
    public Color getForeground();

    public Color getTextBackground();

    public Color getDisabled();

    public AngelCodeFont getEditFont();

    public AngelCodeFont getLoginFont();

    public AngelCodeFont getSystemFont();

    public AngelCodeFont getSystemMsgFont();

    public AngelCodeFont getUserName42();
}

