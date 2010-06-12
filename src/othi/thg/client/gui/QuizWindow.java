package othi.thg.client.gui;

import org.newdawn.slick.AngelCodeFont;
import mdes.slick.sui.*;

/**
 * Quiz window
 * @author Dong Won Kim
 */
public class QuizWindow extends ScrollCloseWindow {

    private final String REQUIREMENT_FOR_ANSWER = "Click here and answer to the question!!!";
    private final float ANSWERAREA_RATE = 0.6f;
    
    /** Quest ID = Topic ID */
    private int questID;
    private String subject;
    private String question;
    private TextFieldArea answerArea = null;

    public QuizWindow(AngelCodeFont font) {
        super(font, ScrollWindow.getSCROLLWINDOW_WIDTH(), (int) (ScrollWindow.getSCROLLWINDOW_HEIGHT()/2));
        answerArea = new TextFieldArea(font, getWidth(), (int) (getHeight() * ANSWERAREA_RATE));
    }

    @Override
    public void formDefaultComponents() {
        SuiButton closeBtn = getCloseButton();
        int closeButtonX = getWidth()/2 - closeBtn.getWidth()/2;
        int closeButtonY = getHeight() - 5 - closeBtn.getHeight();
        int textFieldAreaY = closeButtonY - 5 - answerArea.getHeight();
        addTextFieldArea(answerArea, textFieldAreaY);
        SuiTextField textField = answerArea.getTextField();
        textField.setInitialText(REQUIREMENT_FOR_ANSWER);
        int textAreaHeight = textFieldAreaY - 5 - getTextArea().getPadding();
        formDefaultComponents(closeButtonX, textAreaHeight);
    }

    private void addTextFieldArea(TextFieldArea textFieldArea, int textFieldAreaY){
        textFieldArea.setLocationRelativeTo(this);
        textFieldArea.setX(0);
        textFieldArea.setY(textFieldAreaY);
        textFieldArea.formDefaultComponents();
        textFieldArea.setVisible(true);
        add(textFieldArea);
    }

    @Override
    public boolean hasFocus(){
        boolean answerAreaHasFocuse =  answerArea == null ?  false : answerArea.hasFocus();
        return super.hasFocus() || answerAreaHasFocuse;
    }
    
    public void setQuestionArea() {
        setTitle(subject);
        SuiTextArea textArea = getTextArea();
        textArea.setText("");
        textArea.append(question + SuiTextArea.NEWLINE);
    }

    public String getREQUIREMENT_FOR_ANSWER() {
        return REQUIREMENT_FOR_ANSWER;
    }
    
    public TextFieldArea getAnswerArea() {
        return answerArea;
    }
    
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getQuestID() {
        return questID;
    }

    public void setQuestID(int questID) {
        this.questID = questID;
    }
}

