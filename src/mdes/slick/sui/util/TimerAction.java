package mdes.slick.sui.util;

import org.lwjgl.Sys;

/** 
 * A very simple timer class based off GTGE's Timer
 * utility.
 * <p>
 * The update() method returns <tt>true</tt> if
 * the timer is active and the time is up. If the timer
 * is not repeating (the default) it will be disabled
 * before returning <tt>true</tt>.
 *
 * @author davedes
 */
public class TimerAction {
    
    /** The delay between actions. */
    private long delay = 1000;
    
    /** Whether this timer is active. */
    private boolean active = false;
    
    /** Whether to repeat this timer. */
    private boolean repeats = false;
    
    private long lastTime = getTime();
    
    private boolean done = false;
    
    private boolean isAction = false;
    
    /** 
     * Creates a new TimerAction with the specified delay. 
     * 
     * @param the delay in milliseconds
     */
    public TimerAction(long delay) {
        this.delay = delay;
        active = true;
    }
    
    /** Creates a new TimerAction with a 1 second delay. */
    public TimerAction() {
        this(1000);
    }
    
    /** 
     * Updates the timer and returns <tt>true</tt> if the action is occurring.
     * 
     * @returns <tt>true</tt> if we should notify that the action has occurred
     */
    public boolean update(int delta) {
        isAction = false;
        if (!active || (!repeats && done) )
            return false;
        
        long now = (getTime()-delta)-lastTime;
        if (now >= delay) {
            lastTime = getTime();
            
            if (!repeats && !done) {
                done = true;
            }
            
            isAction = true;
            return true;
        }
        else 
            return false; 
    }
    
    /** 
     * Whether the action has occurred.
     *
     * @returns <tt>true</tt> if the action has occurred */
    public boolean isAction() {
        return isAction;
    }
    
    /** 
     * Whether to repeat this timer after every action. 
     * 
     * @param b whether to repeat after every action
     */
    public void setRepeats(boolean b) {
        if (!repeats&&done)
            restart();
        repeats = b;
        done = false;
    }
    
    /**
     * Whether this timer is repeating.
     *
     * @returns <tt>true</tt> if we should notify that the action has occurred
     */
    public boolean isRepeats() {
        return repeats;
    }
    
    /** Restarts this timer. */
    public void restart() {
        lastTime = getTime();
        done = false;
    }
    
    /** 
     * Sets whether this timer is active. 
     * 
     * @param b the state of this timer
     */
    public void setActive(boolean b) {
        if (!active && b)
            restart();
        active = b;
    }
    
    /** 
     * Whether the timer is active. 
     *
     * @returns whether the timer is active
     */
    public boolean isActive() {
        return active;
    }
    
    /** 
     * Sets the delay of this timer. 
     *
     * @param delay the time in milliseconds
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }
    
    /**
     * Gets the delay of this timer.
     *
     * @returns the delay of this timer
     */
    public long getDelay() {
        return delay;
    }
    
    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
}