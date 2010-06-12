package othi.thg.client.gui;

import java.util.ArrayList;

import othi.thg.client.entities.Quest.QuestState;

import mdes.slick.sui.suiList.*;

/**
 * Quest list window 
 * @author Dong Won Kim
 */
public class QuestListWindow extends ListWindow {
    private SuiList guideNameList = null;
    private SuiList questNameList = null;
    private SuiList questStateList = null;
    ArrayList<String> guideNames = new ArrayList<String>();
    ArrayList<String> questNames = new ArrayList<String>();
    ArrayList<QuestState> questStates = new ArrayList<QuestState>();
    
    public QuestListWindow () {
        super("LIST OF QUESTS");

        guideNameList = new SuiList();
        guideNameList.setLocationRelativeTo(this);
        guideNameList.setLocation(0,0);
        guideNameList.setSize(100, ListWindow.getLISTWINDOW_HEIGHT());
        guideNameList.setSelectionMode(SuiList.SINGLE_SELECTION);
        guideNameList.setGlassPane(true);
        this.add(guideNameList);
        
        questNameList = new SuiList();
        questNameList.setLocationRelativeTo(this);
        questNameList.setLocation(guideNameList.getX() + guideNameList.getWidth() + 5, 0);
        questNameList.setSize(200, ListWindow.getLISTWINDOW_HEIGHT());
        questNameList.setSelectionMode(SuiList.SINGLE_SELECTION);
        this.add(questNameList);
        
        questStateList = new SuiList();
        questStateList.setLocationRelativeTo(this);
        questStateList.setLocation(questNameList.getX() + questNameList.getWidth()+ 5, 0);
        questStateList.setSize(100, ListWindow.getLISTWINDOW_HEIGHT());
        questStateList.setGlassPane(true);
        this.add(questStateList);
    }

    public ArrayList<String> getGuideNames() {
        return guideNames;
    }

    public SuiList getGuideNameList() {
        return guideNameList;
    }

    public ArrayList<QuestState> getQuestStates() {
        return questStates;
    }

    public SuiList getQuestStateList() {
        return questStateList;
    }

    public ArrayList<String> getQuestNames() {
        return questNames;
    }

    public SuiList getQuestNameList() {
        return questNameList;
    }

}
