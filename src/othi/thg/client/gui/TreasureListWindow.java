/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.client.gui;

import java.util.ArrayList;

import mdes.slick.sui.suiList.*;

/**
 * Treasure list window
 * @author Dong Won Kim
 */
public class TreasureListWindow extends ListWindow {
    private SuiList titleSuiListForTreasure = null;
    private SuiList introSuiListForTreasure = null;    
    ArrayList<String> titleListForTreasure = new ArrayList<String>();       
    ArrayList<String> introListForTreasure = new ArrayList<String>();   
    
    public TreasureListWindow () {
        super("LIST OF TREASURES");        
        
        titleSuiListForTreasure = new SuiList();
        titleSuiListForTreasure.setLocationRelativeTo(this);
        titleSuiListForTreasure.setLocation(0,0);          
        titleSuiListForTreasure.setSize(100, ListWindow.getLISTWINDOW_HEIGHT());      
        titleSuiListForTreasure.setSelectionMode(SuiList.SINGLE_SELECTION);
        titleSuiListForTreasure.setGlassPane(true);        
        this.add(titleSuiListForTreasure);
        
        introSuiListForTreasure = new SuiList();
        introSuiListForTreasure.setLocationRelativeTo(this);
        introSuiListForTreasure.setLocation(titleSuiListForTreasure.getX() + titleSuiListForTreasure.getWidth() + 5, 0);          
        introSuiListForTreasure.setSize(ListWindow.getLISTWINDOW_WIDTH() - titleSuiListForTreasure.getWidth(), ListWindow.getLISTWINDOW_HEIGHT());      
        introSuiListForTreasure.setSelectionMode(SuiList.SINGLE_SELECTION);
        
        this.add(introSuiListForTreasure);                       
    }

    public ArrayList<String> getIntroListForTreasure() {
        return introListForTreasure;
    }

    public SuiList getIntroSuiListForTreasure() {
        return introSuiListForTreasure;
    }

    public ArrayList<String> getTitleListForTreasure() {
        return titleListForTreasure;
    }

    public SuiList getTitleSuiListForTreasure() {
        return titleSuiListForTreasure;
    }
}
