/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.client.entities;

/**
 *
 * @author Steve
 */
public class Quest extends THGEntity {
    
    public static enum QuestState {
        Suggest, Accept, Reject, Question, Fail, Complete
    }

    public static enum InformationType {
        Help, Clue
    }

    int guideId;
    String guideName; //Nick Name
    String introduction;
    String question;
    QuestState state;

    public Quest(int questID, String questName){
        this.id = questID;
        this.name = questName;
    }

    public int getGuideId() {
        return guideId;
    }

    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public QuestState getState() {
        return state;
    }

    public void setState(QuestState status) {
        this.state = status;
    }

    public void setState(int status) {
        switch (status) {
            case 0:
                this.state = QuestState.Suggest;
                break;
            case 1:
                this.state = QuestState.Accept;
                break;
            case 2:
                this.state = QuestState.Reject;
                break;
            case 3:
                this.state = QuestState.Fail;
                break;
            case 4:
                this.state = QuestState.Complete;
                break;
            default:
                this.state = null;
        }
    }

    public static QuestState getState(int status) {
        QuestState qstate = null;
        switch (status) {
            case 0:
                qstate = QuestState.Suggest;
                break;
            case 1:
                qstate = QuestState.Accept;
                break;
            case 2:
                qstate = QuestState.Reject;
                break;
            case 3:
                qstate = QuestState.Fail;
                break;
            case 4:
                qstate = QuestState.Complete;
                break;
            default:
                qstate = null;
        }
        return qstate;
    }

    public static int getState(QuestState qstate) {
        int stateNo = 5;
        switch (qstate) {
            case Suggest:
                stateNo = 0;
                break;
            case Accept:
                stateNo = 1;
                break;
            case Reject:
                stateNo = 2;
                break;
            case Fail:
                stateNo = 3;
                break;
            case Complete:
                stateNo = 4;
                break;
            default:
                stateNo = 5;
        }
        return stateNo;
    }

    public static int getInfomationType(InformationType iType){
        int inforType = 0;

        switch (iType) {
            case Help:
                inforType = 0;
                break;
            case Clue:
                inforType = 1;
                break;
            default:
                inforType = 2;
        }
        return inforType;
    }

    public static InformationType getInfomationType(int iType){
        InformationType inforType = null;

        switch (iType) {
            case 0:
                inforType = InformationType.Help;
                break;
            case 1:
                inforType = InformationType.Clue;
                break;
            default:
        }
        return inforType;
    }
}
