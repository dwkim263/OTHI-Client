/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package othi.thg.client.gui;

import mdes.slick.sui.*;

import org.newdawn.slick.Image;
import org.newdawn.slick.Color;

import othi.thg.client.THGClientDefault;

/**
 * player's status window
 * @author Steve
 */
public class StatusPanel extends SuiLabel {
    private Color c1;
    private SuiLabel levelLabel = null;  
    private SuiLabel moneyLabel = null;           
    private RpgBar hpBar = null;
    private RpgBar expBar = null;      
    private SuiLabel hpLabel = null;
    private SuiLabel expLabel = null;
    
    public StatusPanel(Image img) {
        super(img);
        
        this.pack();
        this.setLocation(THGClientDefault.SCREEN_WIDTH - this.getWidth() -5 , 10);
        this.setGlassPane(true);

        //label foreground
        c1 = new Color (getForegroundColor());
        c1.a = .65f;
        
        buildLevelLabel();
        buildMoneyLabel();  
        buildHpLabel();        
        buildHpBar();        
        buildMpBar();        
        buildExpLabel();
        buildExpBar();
        this.setVisible(true);           
    }
    
    private void buildLevelLabel() {
        //sets up the label of level
        levelLabel = new SuiLabel("Level: 0");
        levelLabel.pack();
        levelLabel.setHorizontalAlignment(SuiLabel.LEFT_ALIGNMENT);            
        levelLabel.setForegroundColor(c1);
        levelLabel.setLocationRelativeTo(this);
        levelLabel.setLocation(10, 5);            
        levelLabel.setGlassPane(true);
        levelLabel.setWidth(this.getWidth()-(int)levelLabel.getX());
        this.add(levelLabel);        
    }
    
    public void setLevelText (int level) {
        levelLabel.setText("Level: "+level);
    }
    
    private void buildMoneyLabel(){
        //sets up the label of gold      
        moneyLabel = new SuiLabel("Gold: 0");
        moneyLabel.pack();
        moneyLabel.setHorizontalAlignment(SuiLabel.LEFT_ALIGNMENT);            
        moneyLabel.setForegroundColor(c1);
        moneyLabel.setLocationRelativeTo(this);
        moneyLabel.setLocation(10, 20);            
        moneyLabel.setGlassPane(true);
        moneyLabel.setWidth(this.getWidth()-(int)moneyLabel.getX());
        this.add(moneyLabel);        
    }
    
    public void setMoneyText (int money) {
        moneyLabel.setText("Gold: "+money);
    }
    
    private void buildHpLabel(){
        //sets up the label of health point            
        hpLabel = new SuiLabel("HP:");
        hpLabel.pack();
        hpLabel.setHorizontalAlignment(SuiLabel.LEFT_ALIGNMENT);             
        hpLabel.setForegroundColor(c1);
        hpLabel.setLocationRelativeTo(this);
        hpLabel.setLocation(levelLabel.getX(), 35);
        hpLabel.setGlassPane(true);  
        hpLabel.setWidth(30);
        this.add(hpLabel);           
    }
    
    private void buildHpBar(){
        //Creates the hp bar, note that the constructor sets the
        //height for us already
        hpBar = new RpgBar(0, 0);
        hpBar.setLocationRelativeTo(this);
        hpBar.setLocation(hpLabel.getX() + hpLabel.getWidth() + 5, hpLabel.getY());
        hpBar.setGlassPane(true);
        this.add(hpBar);        
    }
    
    public void setHpBarPoints (int hp, int maxHp) {
        hpBar.setPoints(hp, maxHp);
    }
    
    private void buildMpBar(){
        /* RpgBar mpbar = new RpgBar("MP ", sprite.getMP(), sprite.getMaxMp());
        mpbar.setWidth(122);
        mpbar.setLocationRelativeTo(this);
        mpbar.translate(0, 20); */
    }
    
    private void buildExpLabel(){
        expLabel = new SuiLabel("EXP:");
        expLabel.pack();
        expLabel.setHorizontalAlignment(SuiLabel.LEFT_ALIGNMENT);               
        expLabel.setForegroundColor(c1);
        expLabel.setLocationRelativeTo(this);
        expLabel.setLocation(levelLabel.getX(), 50);          
        expLabel.setGlassPane(true); 
        expLabel.setWidth(hpLabel.getWidth());      
        this.add(expLabel);
    }
    
    private void buildExpBar(){
        expBar = new RpgBar(0, 0);
        expBar.setTopColor(new Color(96, 96, 255));            
        expBar.setBotColor(new Color(45, 38, 255));                        
        expBar.setLocationRelativeTo(this);
        expBar.setLocation(expLabel.getX() + expLabel.getWidth() + 5, expLabel.getY());
        expBar.setGlassPane(true);            
        this.add(expBar);
    }
    
    public void setExpBarPoints (int exp, int maxExp) {
        expBar.setPoints(exp, maxExp);
    }
    
    public RpgBar getExpBar() {
        return expBar;
    }

    public RpgBar getHpBar() {
        return hpBar;
    }

    public SuiLabel getLevelLabel() {
        return levelLabel;
    }

    public SuiLabel getMoneyLabel() {
        return moneyLabel;
    }

    public SuiLabel getExpLabel() {
        return expLabel;
    }

    public SuiLabel getHpLabel() {
        return hpLabel;
    }        
}
