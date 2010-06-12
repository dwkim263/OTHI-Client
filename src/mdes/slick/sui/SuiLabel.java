package mdes.slick.sui;

import org.newdawn.slick.*;

/**
 * SuiLabel is the base class for displaying
 * a String and/or Image on a component. Text is
 * always drawn over images, which are drawn over
 * the color background (if opaque).
 * <p>
 * Text and images can be aligned horizontally and/or
 * vertically through setHorizontalAlignment
 * and setVerticalAlignment.
 * <p>
 * All labels start with a padding of 0, initially
 * centered.
 *
 * @author davedes
 * @since b.0.1
 */
public class SuiLabel extends SuiContainer {
  
  /** A constant for the horizontal alignment. */
  public static final int LEFT_ALIGNMENT = 0;
  
  /** A constant for the horizontal alignment. */
  public static final int RIGHT_ALIGNMENT = 1;
  
  /** A constant for the vertical/horizontal alignment. */
  public static final int CENTER_ALIGNMENT = 2;
  
  /** A constant for the vertical alignment. */
  public static final int TOP_ALIGNMENT = 3;
  
  /** A constant for the vertical alignment. */
  public static final int BOTTOM_ALIGNMENT = 4;
  
  /** The text to be displayed. */
  protected String text = null;
  
  /** The image to be displayed. */
  protected Image image = null;
  
  /** The current horizontal alignment. */
  protected int horizAlignment = CENTER_ALIGNMENT;
  
  /** The current vertical alignment. */
  protected int vertAlignment = CENTER_ALIGNMENT;
  
  /** The current amount of padding. */
  protected int padding = 0;
  
  /** The current font. */
  private Font font = Sui.getDefaultFont();
  
  /** The current background color. */
  protected Color background = Sui.getTheme().getTextBackground();
  
  /** The current foreground color. */
  protected Color foreground = Sui.getTheme().getForeground();
  
  /** The current disabled color, initially gray. */
  protected Color disabledColor = Sui.getTheme().getDisabled();
  
  /** Whether this label is enabled. */
  private boolean enabled = true;
  
  /** Whether this label is opaque; drawing all pixels. */
  private boolean opaque = false;
  
  /** The yoffset for text. */
  protected int yoff = 0;
  
  /** 
   * Creates a new label with the specified text and image. 
   * 
   * @param image the image to be displayed (rendered below text)
   * @param text the text to be displayed
   */
  public SuiLabel(Image image, String text) {
    this.image=image;
    this.text=text;
    yoff = getYOffset(text);
  }
  
  /** Creates a new label with the specified text. */
  public SuiLabel(String text) {
    this.text = text;
    yoff = getYOffset(text);
  }

  public SuiLabel(Font font) {
    this.font = font;
  }
  /** 
   * Creates a new label with the specified image. 
   *
   * @param image the image to be displayed
   */
  public SuiLabel(Image image) {
    this.image = image;
  }
  
  /** Creates a new empty label. */
  public SuiLabel() {
    
  }
  
  /** 
   * Whether this label is enabled
   * 
   * @returns <tt>true</tt> if this label is enabled
   */
  public boolean isEnabled() {
    return enabled;
  }
  
  /**
   * Set whether to enable or disable this label.
   *
   * @param b <tt>true</tt> if this label should be enabled
   */
  public void setEnabled(boolean b) {
    this.enabled = b;
  }
  
  /** 
   * Packs this label based on current font & text,
   * leaving a space for padding.
   */
  public void pack() {
    //no text to display
    if (text==null||text.length()==0) {
      //if there is an image
      if (image!=null) {
        setWidth(padding*2+image.getWidth());
        setHeight(padding*2+image.getHeight());
      }
    }
    //text exists
    else {
      int tw = font.getWidth(text);
      int th = font.getHeight(text)-yoff;
      
      setWidth(padding*2+tw);
      setHeight(padding*2+th);
    }
  }
  
  /**
   * Sets the font of this label.
   *
   * @param f the new font to use
   */
  public void setFont(Font f) {
    this.font = f;
    yoff = getYOffset(text);
  }
  
  /**
   * Gets the font being used by this label.
   *
   * @returns the font being used
   */
  public Font getFont() {
    return font;
  }
  
  /**
   * Sets the disabled foreground color to be
   * used.
   *
   * @param c the new foreground color
   */
  public void setDisabledForegroundColor(Color c) {
    this.disabledColor = c;
  }
  
  /**
   * Gets the disabled foreground color.
   *
   * @returns the disabled foreground color
   */
  public Color getDisabledForegroundColor() {
    return disabledColor;
  }
  
  /**
   * Sets the foreground text color.
   *
   * @param c the new foreground color
   */
  public void setForegroundColor(Color c) {
    this.foreground = c;
  }
  
  /**
   * Gets the foreground text color.
   *
   * @returns the foreground color
   */
  public Color getForegroundColor() {
    return foreground;
  }
  
  /**
   * Gets the background color. This will
   * only be drawn if this label is opaque.
   *
   * @returns the background color
   */
  public Color getBackgroundColor() {
    return background;
  }
  
  /**
   * Sets the background color. This will
   * only be drawn if this label is opaque.
   *
   * @param c the new background color
   */
  public void setBackgroundColor(Color c) {
    this.background = c;
  }




  /**
   * Sets whether this label is opaque.
   * Opaque components will draw any
   * transparent pixels with the 
   * background color.
   *
   * @param b <tt>true</tt> if background color
   *			should be drawn
   */
  public void setOpaque(boolean b) {
    this.opaque = b;
  }
  
  /**
   * Sets whether this label is opaque.
   * Opaque labels will fill a rectangle
   * of the background color before drawing the
   * text or image.
   *
   * @param b <tt>true</tt> if background color
   *			should be drawn
   */
  public boolean isOpaque() {
    return opaque;
  }
  
  /**
   * Sets the Image to be displayed.
   *
   * @param i the Image to draw
   */
  public void setImage(Image i) {
    this.image = i;
  }
  
  /**
   * Gets the Image being displayed.
   *
   * @returns the label's image
   */
  public Image getImage() {
    return image;
  }
  
  /**
   * Sets the text to be displayed.
   *
   * @param text the text to draw
   */
  public void setText(String text) {
    this.text = text;
    yoff = getYOffset(text);
  }
  
  /**
   * Gets the padding of this label.
   *
   * @returns the padding, in pixels
   */
  public int getPadding() {
    return padding;
  }
  
  /**
   * Sets the padding of this label.
   *
   * @param i the padding, in pixels
   */
  public void setPadding(int i) {
    padding = i;
  }
	
  /**
   * Gets the text being displayed.
   *
   * @returns the text for this label
   */  
  public String getText() {
    return text;
  }
  
  /**
   * Overridden to render the label. 
   *
   * @param c the GameContainer to draw in
   * @param g the Graphics context to draw with
   */
    @Override
  protected void renderComponent(GameContainer c, Graphics g) {
    
    if (isOpaque()) {
        Color old = g.getColor();
        g.setColor(getBackgroundColor());
        g.fillRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
        g.setColor(old);
    }
    
    if (image!=null) {
        drawImage(g, image);
    }
    
    if (text!=null) {
        drawString(g, text);
    }    
  }
  
  /** 
   * Sets the horizontal alignment of the text/image.
   *
   * @param horizAlignment the alignment constant; either LEFT_ALIGNMENT,
   *							RIGHT_ALIGNMENT, or CENTER_ALIGNMENT
   */
  public void setHorizontalAlignment(int horizAlignment) {
    this.horizAlignment = horizAlignment;
  }
  
  /** 
   * Gets the horizontal alignment of the text/image.
   * 
   * @returns the horizontal alingment constant
   */
  public int getHorizontalAlignment() {
    return horizAlignment;
  }
  
  /** 
   * Sets the vertical alignment of the text/image.
   *
   * @param vertAlignment the alignment constant; either TOP_ALIGNMENT,
   *							BOTTOM_ALIGNMENT, or CENTER_ALIGNMENT
   */
  public void setVerticalAlignment(int vertAlignment) {
    this.vertAlignment = vertAlignment;
  }
  
  /** 
   * Gets the vertical alignment of the text/image.
   * 
   * @returns the vertical alingment constant
   */
  public int getVerticalAlignment() {
    return vertAlignment;
  }
  
  //TODO: support for alignment X and Y
  //TODO: tweak getTextX/Y by reusing width/height
  
  /**
   * Should be used by subclasses to draw an Image at the correct
   * location (based on alignment & padding).
   *
   * @param g the graphics to draw with
   * @param image the image to draw
   */
  protected void drawImage(Graphics g, Image image) {
    int iw = image.getWidth();
    int ih = image.getHeight();
    
    int x = (int)getObjectX(iw);
    int y = (int)getObjectY(ih);
    
    g.drawImage(image, x, y);
  }
  
  /**
   * Should be used by subclasses to draw a String at the correct
   * location (based on yoffset, alignment & padding).
   *
   * @param g the graphics to draw with
   * @param String the text to draw
   */
  protected void drawString(Graphics g, String str) {
    int yoff1 = (str.equals(text)) ? this.yoff : getYOffset(str);
    
    int tw = font.getWidth(str);
    int th = font.getHeight(str)-yoff1;
    
    float x = getObjectX(tw);
    float y = getObjectY(th);
    
    drawString(g, str, x, y);
  }
  
  /**
   * Should be used by subclasses to draw a String at the correct
   * location (based on yoffset).
   *
   * @param g the graphics to draw with
   * @param String the text to draw
   * @param x the absolute x value
   * @param y the absolute y value
   */
  protected void drawString(Graphics g, String str, float x, float y) {
    //use the cached yoff unless text has changed
    int yoff1 = (str.equals(text)) ? this.yoff : getYOffset(str);
    
    Color oldColor = g.getColor();
    Font oldFont = g.getFont();
        
    g.setColor(enabled ? foreground : disabledColor);
    g.setFont(font);
    
    g.drawString(str, x, y-yoff1);
    
    g.setColor(oldColor);
    g.setFont(oldFont);
  }
  
  /** 
   * Gets the y position of an object that has the specified height, 
   * based on alignment and padding.
   *
   * @param height the height of the object (eg: image, text)
   */
  protected float getObjectY(int height) {
    //y position
    switch (vertAlignment) {
        case CENTER_ALIGNMENT:
        default:
            return getAbsoluteY() + ( getHeight()/2.0f - height/2.0f );
        case TOP_ALIGNMENT:
            return getAbsoluteY() + padding;
        case BOTTOM_ALIGNMENT:
            return getAbsoluteX() + getHeight() - padding - height;
    }
  }
  
  /** 
   * Gets the x position of an object that has the specified width, 
   * based on alignment and padding.
   *
   * @param width the width of the object (eg: image, text)
   */
  protected float getObjectX(int width) {
    //x position
    switch (horizAlignment) {
        case CENTER_ALIGNMENT:
        default:
            return getAbsoluteX() + ( getWidth()/2.0f - width/2.0f );
        case LEFT_ALIGNMENT:
            return getAbsoluteX() + padding;
        case RIGHT_ALIGNMENT:
            return getAbsoluteX() + getWidth() - padding - width;
    }
  }
  
  /** 
   * Gets the yoffset if the current font is an instanceof AngelCodeFont,
   * otherwise returns 0. This method on its own does not change the protected
   * variable <tt>yoffset</tt>. Whenever the text changes, this method is
   * used to store the new offset in the <tt>yoffset</tt> variable.
   *
   * @returns the yoffset of the font if it is an instanceof AngelCodeFont,
   *				otherwise 0
   */
  protected int getYOffset(String s) {
    if (s==null||s.length()==0)
      return 0;
    else if (font instanceof AngelCodeFont)
      return ((AngelCodeFont)font).getYOffset(s);
    else
      return 0;
  }
}