package othi.thg.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import mdes.slick.sui.*;
/**
 * RPG bar for player's status window
 * @author Dong Won Kim
 */
public class RpgBar extends SuiContainer {

    /** Total amount of hitpoints. */
    private int totalPoints;

    /** Current amount of hitpoints. */
    private int currentPoint;

    /** Color for the top half gradient of the bar. */
    private Color top = new Color(203, 73, 70);

    /** Color for the bottom half gradient of the bar. */
    private Color bot = new Color(140, 21, 19);

    /** Color for the border of the bar. */
    private Color border = new Color(78, 48, 48);

    /** The label which displays the current health. */
    private SuiLabel pointLabel = null;

    /** Creates a new health bar with 100 hitpoints. */
    public RpgBar(int currentPoint, int totalPoint) {
        super();


        //health text label
        pointLabel = new SuiLabel();     
        pointLabel.pack();
        pointLabel.setHorizontalAlignment(SuiLabel.CENTER_ALIGNMENT);
        pointLabel.setLocationRelativeTo(this);
        pointLabel.setLocation(0, 0);         
        pointLabel.setVisible(false);

        //foreground color for label
        Color c1 = new Color(pointLabel.getForegroundColor());
        c1.a = .5f;

        pointLabel.setForegroundColor(c1);
        add(pointLabel);

        this.currentPoint = currentPoint;
        this.totalPoints = totalPoint;
        updateLabel();

        setWidth(122);            

        //label starts off height-packed to the text label            
        setHeight(pointLabel.getHeight());            
    }

    /** Updates the label when the hitpoints change. */
    private void updateLabel() {
        pointLabel.setText(currentPoint+" / "+totalPoints);
        pointLabel.pack();
        pointLabel.setWidth(getWidth());
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        pointLabel.setWidth(width);
    }

    /** Sets the current hitpoints value. */
    public void setCurrentPoint(int currentPoint) {
        setPoints(currentPoint, this.totalPoints);
    }

    /** Sets the total hitpoints value. */
    public void setTotal(int total) {
        setPoints(this.currentPoint, total);
    }

    /** Gets the current hitpoints value. */
    public int getCurrentPoint() {
        return currentPoint;
    }

    /** Gets the total hitpoints value. */
    public int getTotal() {
        return totalPoints;
    }

    /** Sets the current hitpoints and total hitpoints values. */
    public void setPoints(int point, int total) {
        if (currentPoint != point || totalPoints != total){
            currentPoint = point;
            totalPoints = total;
            updateLabel();
        }
    }

    public void setTopColor(Color c) {
        top = c;
    }

    public void setBotColor(Color c) {
        bot = c;
    }

    /** Called to render this component (the red bar). */
    @Override
    public void renderComponent(GameContainer c, Graphics g) {
        //percent to show
        float amt = (float)currentPoint/totalPoints;
        //we can take advantage of the gradient painter in SuiContainer
        fillGradientRect(top, bot, getAbsoluteX(), getAbsoluteY(), (int)(getWidth()*amt), getHeight());
    }

    /** Called to render the border. */
    @Override
    public void renderBorder(GameContainer c, Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(border);            
        g.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight()-1);
        g.setColor(oldColor); 
    }    
} 

