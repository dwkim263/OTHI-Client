package mdes.slick.sui;

import org.newdawn.slick.*;
import mdes.slick.sui.event.*;

/**
 * A basic, clickable button component. <tt>SuiButtons</tt> are
 * rendered based on their state: UP, DOWN, or ROLLOVER. 
 * Images can be set for each state.
 * <p>
 * A button will be "hit" when the first mouse button is 
 * clicked and released over it (SuiMouseEvent.BUTTON1).
 * <p>
 * All SuiButtons are created with a padding of 5, and 
 * with text and images initially centered.
 * 
 * @author davedes
 * @since b.0.1
 */
public class SuiButton extends SuiLabel {
  
  /** A constant for the UP state. */
  public static final int UP = -10;
  
  /** A constant for the DOWN state. */
  public static final int DOWN = -9;
  
  /** A constant for the ROLLOVER state. */
  public static final int ROLLOVER = -8;
  
  /** The current state. */
  private int state = UP;
  
  /** The image to draw when the mouse rolls over the button. */
  private Image rolloverImage = null;
  
  /** The image to draw when the mouse presses down on the button. */
  private Image downImage = null;
  
  /** The image to draw when the button is disabled. */
  private Image disabledImage = null;
  
  /** The action command, initially an empty String. */
  private String actionCommand = "";
  
  /** 
   * Creates a button with the specified text, which
   * also acts as the action command String.
   * 
   * @param text the text to display on the button
   */
  public SuiButton(String text) {
    super(text);
    actionCommand = text;
    init();
  }
  
  /**
   * Creates a button with the specified UP image. 
   * 
   * @param img the image to display on the button
   */
  public SuiButton(Image img) {
    super(img);
    init();
  }
  
  /** Creates an empty button. */
  public SuiButton() {
    super();
    init();
  }
  
  /** Initializes the button. */
  private void init() {
    setForegroundColor(Sui.getTheme().getPrimary4());
  	setPadding(5);
  	setFocusable(true);
    addMouseListener(new ButtonListener());
  }
  
  /**
   * Sets the action command to be passed to
   * <tt>SuiActionEvent</tt>s when this button
   * is clicked.
   * 
   * @param t the command
   */
  public void setActionCommand(String t) {
  	this.actionCommand = t;
  }
  
  /**
   * Gets the action command.
   *
   * @returns the action command
   */
  public String getActionCommand() {
  	return actionCommand;
  }
  
  
  /**
   * Overridden to render the button graphics.
   *
   * @param c the GameContainer
   * @param g the Graphics context to draw on
   */
    @Override
  protected void renderComponent(GameContainer c, Graphics g) {
    Font oldFont = g.getFont();
    
    //first we draw the possibly opaque background
    if (isOpaque()) {
        Color old = g.getColor();
        g.setColor(getBackgroundColor());
        g.fillRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
        g.setColor(old);
    }
    
    //then we draw the button based on its state
    if (state==DOWN) {
      renderDown(c, g);
    }
    else if (state==ROLLOVER) {
      renderRollover(c, g);
    }
    else {
      renderUp(c, g);
    }
    
    //lastly we draw the text if it exists
    if (getText()!=null) {
      drawString(g, getText());
    }
    
    g.setFont(oldFont);
  }
  
  /**
   * Called to render the button when in the UP 
   * state. If the button is disabled and the
   * disabled image exists it will be drawn, otherwise
   * the regular (up) image will be drawn if it
   * exists.
   *
   * @param c the GameContainer
   * @param g the Graphics context to draw on
   */
  protected void renderUp(GameContainer c, Graphics g) {
    Color start = Sui.getTheme().getPrimary1();
    Color end = Sui.getTheme().getPrimary2();
    fillGradientRect(start, end, 
                getAbsoluteX(), getAbsoluteY(),
                getWidth(), getHeight());
    
    Image drawImg = (isEnabled()) ? getImage() : getDisabledImage();
    if (drawImg!=null) 
        drawImage(g, drawImg);
  }
  
  /**
   * Called to render the button when in the DOWN 
   * state. If the down image exists it will be drawn,
   * otherwise the regular (up) image will be drawn if
   * it exists.
   *
   * @param c the GameContainer
   * @param g the Graphics context to draw on
   */
  protected void renderDown(GameContainer c, Graphics g) {
    Color start = Sui.getTheme().getSecondary3();
    Color end = Sui.getTheme().getPrimary2();
    fillGradientRect(start, end, 
                getAbsoluteX(), getAbsoluteY(),
                getWidth(), getHeight());
    if (getDownImage()!=null) {
        drawImage(g, getDownImage());
    }
    else if (getImage()!=null) {
    	drawImage(g, getImage());
    }
  }
  
  /**
   * Called to render the button when in the ROLLOVER 
   * state. If the rollover image exists it will be drawn,
   * otherwise the regular (up) image will be drawn if
   * it exists.
   *
   * @param c the GameContainer
   * @param g the Graphics context to draw on
   */
  protected void renderRollover(GameContainer c, Graphics g) {
    Color start = Sui.getTheme().getPrimary3();
    Color end = Sui.getTheme().getPrimary2();    
    fillGradientRect(start, end, 
                getAbsoluteX(), getAbsoluteY(),
                getWidth(), getHeight());
    if (getRolloverImage()!=null) {
        drawImage(g, getRolloverImage());
    }
    else if (getImage()!=null) {
    	drawImage(g, getImage());
    }
  }
  
  /**
   * Called to render the button when in the ROLLOVER 
   * state. If the rollover image exists it will be drawn,
   * otherwise the regular (up) image will be drawn if
   * it exists.
   *
   * @param c the GameContainer
   * @param g the Graphics context to draw on
   */
    @Override
  protected void renderBorder(GameContainer c, Graphics g) {
    Color old = g.getColor();
    g.setColor(getBorderColor());
    g.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight() - 1);
    g.setColor(old);
  }
  
  /**
   * Gets the current state of the button.
   *
   * @returns the state id: either UP, DOWN, or ROLLOVER.
   */
  public int getState() {
  	return state;
  }
  
  /**
   * Overridden to set the state of the button to UP if 
   * it's being disabled.
   *
   * @param b <tt>true</tt> if the button should accept clicks
   * @see mdes.slick.sui.SuiContainer#setEnabled(boolean)
   */
    @Override
  public void setEnabled(boolean b) {
    //a disabled button is always in the UP state
    if (!b && isEnabled()) {
        state = UP;
    }
    super.setEnabled(b);
  }
  
  /**
   * Returns the disabled image.
   *
   * @returns the image or <tt>null</tt> if it hasn't been set
   */
  public Image getDisabledImage() {
    return disabledImage;
  }
  
  /**
   * Returns the down (pressed) image.
   *
   * @returns the image or <tt>null</tt> if it hasn't been set
   */
  public Image getDownImage() {
    return downImage;
  }
  
  /**
   * Returns the rollover image.
   *
   * @returns the image or <tt>null</tt> if it hasn't been set
   */
  public Image getRolloverImage() {
    return rolloverImage;
  }
  
  /**
   * Sets the disabled image.
   *
   * @param i the image or <tt>null</tt> if it shouldn't be drawn
   */
  public void setDisabledImage(Image i) {
    this.disabledImage = i;
  }
  
  /**
   * Sets the down (pressed) image.
   *
   * @param i the image or <tt>null</tt> if it shouldn't be drawn
   */
  public void setDownImage(Image i) {
    this.downImage = i;
  }
  
  /**
   * Sets the rollover image.
   *
   * @param i the image or <tt>null</tt> if it shouldn't be drawn
   */
  public void setRolloverImage(Image i) {
    this.rolloverImage = i;
  }
    
  /** 
   * Fires the specified action event to all action listeners
   * in this component.
   * 
   * @param command the action command for the event
   * @see mdes.slick.sui.event.SuiActionEvent
   */
  protected void fireActionPerformed(String command) {
    SuiActionEvent evt = null;
    
    final SuiActionListener[] listeners = listenerList.getListeners(SuiActionListener.class);
    for (int i=0; i<listeners.length; i++) {
      //lazily create it
      if (evt==null) {
        evt = new SuiActionEvent(this, command);
      }
      listeners[i].actionPerformed(evt);
    }
  }
  
  /**
   * A SuiMouseListener for handling the button clicks.
   */
  private class ButtonListener extends SuiMouseAdapter {
    
    private boolean inside = false;
    private boolean ok = true;
        
        @Override
    public void mousePressed(SuiMouseEvent e) {
      if (!isEnabled())
        return;
        
      if (e.getButton()==SuiMouseEvent.BUTTON1) {
        state = DOWN;
      }
    }
    
        @Override
    public void mouseEntered(SuiMouseEvent e) {
      ok = true;
      if (!isEnabled())
        return;
      
      state = (state==DOWN) ? DOWN : ROLLOVER;
      inside = true;
      
      if (getInput().isMouseButtonDown(0))
        ok = false;
    }
    
    //TODO: don't call these methods if a container is disabled
        @Override
    public void mouseReleased(SuiMouseEvent e) {
      if (!isEnabled()) {
        ok = true;
        return;
      }
        
      //only fire action if we are releasing button 1
      if (e.getButton()==SuiMouseEvent.BUTTON1) {
        state = (inside) ? ROLLOVER : UP;
        if (inside&&ok)
          fireActionPerformed(actionCommand);
      }
      ok = true;
    }
    
        @Override
    public void mouseExited(SuiMouseEvent e) {
      if (!isEnabled())
        return;
      
      inside = false;
      state = UP;
    }
  }
}
