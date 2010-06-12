package mdes.slick.sui;

import org.newdawn.slick.*;
import mdes.slick.sui.event.*;

/**
 * The <tt>Sui</tt> class holds utilities for the
 * Simple User Interface system. It holds data
 * such as the GameContainer, Input, default Font, 
 * etc.
 * <p>
 * Color themes can also be set through Sui.
 * <p>
 * The <tt>Sui</tt> class is also responsible for
 * global input such as key, mouse wheel, and
 * controller. It dispatches input events to the
 * currently focused container.
 *
 * @author davedes
 * @since b.0.1
 */
public class Sui {
  
  //TODO: clipping for resizing
  //TODO: move mouse out of container -> stop drag calls (release press)
  
  /** 
   * The Slick Input system currently being used. 
   *
   * @see GameContainer.getInput()
   */
  private static Input input = null;
  
  /** The current SuiTheme -- initially a smooth blue theme. */
  private static SuiTheme theme = new DefaultTheme();
  
  /** 
   * The default Font currently being used.
   *  
   * @see GameContainer.getDefaultFont()
   */
  private static Font defaultFont = null;
  
  /** The GameContainer currently being used. */
  private static GameContainer container = null;
  
  /** The currently focused SuiContainer. */
  private static SuiContainer focusOwner = null;
  
  /** The global key, controller and mouse wheel input. */
  private static final KeyInputListener INPUT_LISTENER = new KeyInputListener();
  
  /** 
   * Initializes (or re-initializes) SUI based on the specified GameContainer. 
   *
   * @param c the GameContainer to use
   */
  public static void init(GameContainer c) {
  	//if an older input exists, remove the listener
  	if (input!=null)
  		input.removeListener(INPUT_LISTENER);
  	
  	//update fields
    container = c;
    input = c.getInput();
    defaultFont = c.getDefaultFont();

   	//add listener
    input.addPrimaryListener(INPUT_LISTENER);
  }

    public static void setContainer(GameContainer container) {
        Sui.container = container;
    }


  /** 
   * Returns the GameContainer being used by Sui.
   *
   * @returns the current GameContainer
   */
  public static GameContainer getContainer() {
    return container;
  }
  
  /**
   * Changes the default Font. When components are
   * created they start off using the default font.
   *
   * @param f the font to use
   * @see mdes.slick.sui.Sui#getDefaultFont()
   */
  public static void setDefaultFont(Font f) {
    defaultFont = f;
  }
  
  /**
   * Changes the Input being used. The old input
   * will have the global listeners removed and the
   * new input will have one added if it hasn't already
   * been.
   *
   * @param i the input to use
   * @see mdes.slick.sui.Sui#getInput()
   */
  public static void setInput(Input i) {
  	if (input!=null)
  		input.removeListener(INPUT_LISTENER);
    input = i;
    input.removeListener(INPUT_LISTENER);
    input.addPrimaryListener(INPUT_LISTENER);
  }
  
  /**
   * Gets the default font being used. 
   *
   * @returns the default font
   * @see mdes.slick.sui.Sui#setDefaultFont(Font)
   */
  public static Font getDefaultFont() {
    return defaultFont;
  }
  
  /**
   * Gets the Slick input system being used.
   *
   * @returns the current Input being used
   * @see mdes.slick.sui.Sui#getInput()
   */
  public static Input getInput() {
    return input;
  }
  
  /**
   * Changes the current color theme.
   *
   * @param t
   * @see mdes.slick.sui.Sui#getTheme()
   */
  public static void setTheme(SuiTheme t) {
    theme = t;
  }
  
  
  /**
   * Changes the current Sui color theme.
   *
   * @param t
   * @see mdes.slick.sui.Sui#getTheme()
   */
  public static SuiTheme getTheme() {
    return theme;
  }
  
  /**
   * Used internally to change the focus.
   *
   * @param c the new owner of the focus, or null
   * 			if no component has the focus
   */
  static void setFocusOwner(SuiContainer c) {
  	//if the container isn't focusable
  	if (c!=null && !c.isFocusable())
  		return;
  	focusOwner = c;
  }
  
  /**
   * Gets the component which currently has the focus.
   * If the current focus owner is not focusable and/or not
   * visible, the focus owner is released and <tt>null</tt> is returned.
   *
   * @returns the current focus owner, or <tt>null</tt> if none exists
   * @since b.0.2
   */
  public static SuiContainer getFocusOwner() {
  	//focus owner becomes null if the last wasn't focusable/visible
  	return (focusOwner!=null&& 
  				(!focusOwner.isFocusable()||!focusOwner.isVisible())
  					) ? (focusOwner=null) : focusOwner;
  }
  
  /**
   * The default blue theme, used internally. The RGB values
   * are as follows:
   * <code><pre> 
   *    //alphas of .85f
   *    primary1 = 37, 78, 102
   *    primary2 = 16, 36, 46
   *    primary3 = 54, 123, 163
   *    
   *    //alphas of .85f
   *    secondary1 = 37, 78, 102
   *    secondary2 = 62, 70, 75
   *    secondary3 = 20, 48, 64
   *    
   *    //alpha of 1.0f
   *    foreground = white
   * </pre></code>
   *
   * @see mdes.slick.sui.SuiTheme
   */
  public static class DefaultTheme implements SuiTheme {
    
        /** The button top color (up). */
        private final Color p1 = new Color(37, 78, 102);

        /** The button bottom color. */
        private final Color p2 = new Color(16, 36, 46);

        /** The button top color (rollover). */
        private final Color p3 = new Color(54, 123, 163);

        /** The titlebar top color. */
        private final Color s1 = new Color(37, 78, 102);

        /** The titlebar bottom color. */
        private final Color s2 = new Color(62, 70, 75);

        /** The border & button top color (down). */
        private final Color s3 = new Color(20, 48, 64);

        /** The text and window resizing triangle color. */
        private final Color txt = Color.white;

        /** Constructs the blue theme. */
        public DefaultTheme() {
          //sets up alpha values for a nicer look
          p1.a = 0.85f;
          p2.a = 0.85f;
          p3.a = 0.85f;

          s1.a = 0.85f;
          s2.a = 0.85f;
          s3.a = 0.85f;
        }

            @Override
        public Color getPrimary1() { return p1; }
            @Override
        public Color getPrimary2() { return p2; }
            @Override
        public Color getPrimary3() { return p3; }

            @Override
        public Color getSecondary1() { return s1; }
            @Override
        public Color getSecondary2() { return s2; }
            @Override
        public Color getSecondary3() { return s3; }


            @Override
        public String getName() { return "DefaultSuiTheme"; }

        @Override
        public Color getShadow() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Color getBackground() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Color getForeground() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Color getTextBackground() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Color getDisabled() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AngelCodeFont getEditFont() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AngelCodeFont getLoginFont() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AngelCodeFont getSystemFont() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AngelCodeFont getSystemMsgFont() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AngelCodeFont getUserName42() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Color getPrimary4() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
  }
  
  /** The global listener class for key, controller, and mouse wheel actions. */
  private static class KeyInputListener extends AbstractInputAdapter {
  	
        @Override
      public void mouseClicked(int button, int x, int y, int clickCount){
          
      }
      
        @Override
  	public boolean isAcceptingInput() {
  		return true;
  	}
  	
        @Override
  	public void inputEnded() {
  	}
  	
        @Override
  	public void setInput(Input i) {
  		Sui.setInput(i);
  	}
  	
        @Override
  	public void keyPressed(int key, char c) {
  		SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireKeyEvent(SuiKeyEvent.KEY_PRESSED, key, c);
        }
  	}
  	
        @Override
  	public void mouseWheelMoved(int change) {
  		SuiContainer focused = Sui.getFocusOwner();
  		if (focused!=null) {
  			focused.fireMouseWheelEvent(change);
  		}
  	}
  	
        @Override
  	public void keyReleased(int key, char c) {
  		SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireKeyEvent(SuiKeyEvent.KEY_RELEASED, key, c);
        }
  	}
  	
        @Override
  	public void controllerButtonPressed(int controller, int button) {
      	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller, button);
        }
  	}
  	
        @Override
  	public void controllerButtonReleased(int controller, int button) {
      	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller, button);
        }
  	}
  	
        @Override
  	public void controllerDownPressed(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller, 
      	  					SuiControllerEvent.DOWN_BUTTON);
        }
    }
  	
        @Override
  	public void controllerDownReleased(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller, 
      	  					SuiControllerEvent.DOWN_BUTTON);
        }
    }
    
        @Override
  	public void controllerUpPressed(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller, 
      	  					SuiControllerEvent.UP_BUTTON);
        }
    }
    
        @Override
  	public void controllerUpReleased(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller, 
      	  					SuiControllerEvent.UP_BUTTON);
        }
    }
  	
        @Override
  	public void controllerLeftPressed(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller, 
      	  					SuiControllerEvent.LEFT_BUTTON);
        }
    }
    
        @Override
    public void controllerLeftReleased(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller, 
      	  					SuiControllerEvent.LEFT_BUTTON);
        }
    }
    
        @Override
    public void controllerRightPressed(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller, 
      	  					SuiControllerEvent.RIGHT_BUTTON);
        }
    }
    
        @Override
  	public void controllerRightReleased(int controller) {
     	SuiContainer focused = Sui.getFocusOwner();
        if (focused!=null) {
      	  focused.fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller, 
      	  					SuiControllerEvent.RIGHT_BUTTON);
        }
    }
    
  }
}

    
    
    
    /*
    //alpha of 85 (or 55)
    private final Color p1 = new Color(56, 102, 37); //button top
    private final Color p2 = new Color(25, 46, 16); //button bottom
    private final Color p3 = new Color(86, 164, 54); //button top hover
    
    //alpha of 85
    private final Color s1 = new Color(56, 102, 37); //title top
    private final Color s2 = new Color(51, 65, 45); //title bottom & frame container
    private final Color s3 = new Color(34, 64, 20); //border & button top down
    
    private final Color txt = new Color(202, 208, 201);
    
    //TODO: remove font, bg, foreground, opacity from Container
    
    public DefaultSuiTheme() {
      //sets up alpha values
      p1.a = 0.85f;
      p2.a = 0.85f;
      p3.a = 0.85f;
      
      s1.a = 0.85f;
      s2.a = 0.85f;
      s3.a = 0.85f;
    }*/