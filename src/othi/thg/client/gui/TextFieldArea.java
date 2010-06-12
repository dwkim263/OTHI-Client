package othi.thg.client.gui;

import mdes.slick.sui.*;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * Displaying texts, typed in the textfield, on textarea
 * @author Dong Won Kim
 */
public class TextFieldArea extends MessageLogWindow {
    private static final int TEXTFIELD_HEIGHT = 25; //30
    private SuiTextField textField = null;
    private SuiTextArea textDisplayArea = null;

    public TextFieldArea(AngelCodeFont font, int width, int height) {
        super(font, width, height);
    }

    @Override
    public void formDefaultComponents() {
        textDisplayArea = getTextArea();
        textDisplayArea.setHorizontalAlignment(SuiTextArea.LEFT_ALIGNMENT);
        textDisplayArea.setText("");
        int textAreaHeight = getHeight() - getTEXTFIELD_HEIGHT() - getTextArea().getPadding() - 5;
        addTextArea(textAreaHeight);
        addTextField(0, getHeight() - getTEXTFIELD_HEIGHT());
    }

    public void addTextField(float x, float y) {
        textField = new SuiTextField(getFont(), getWidth(), TEXTFIELD_HEIGHT);
        textField.setLocationRelativeTo(this);
        textField.setX(x);
        textField.setY(y);
        textField.setCursorVisible(true);
        add(textField);
    }

    @Override
    public boolean hasFocus(){
        boolean textFieldHasFocuse = textField == null ? false : textField.hasFocus();
        return super.hasFocus() || textFieldHasFocuse;
    }

    @Override
//  public void render(GameContainer c, Graphics g, boolean topLevel) {
    public void renderComponent(GameContainer c, Graphics g) {
        if (textField != null && textField.hasFocus()) {
            String textFromTextField = textField.getText();
            if (textFromTextField.length() > 0) {
                textDisplayArea.clearTexts();
                textDisplayArea.appendLog(textFromTextField);
            } else if (textDisplayArea.getText().length() > 0) {
                textDisplayArea.clearTexts();
            }
            if (isVisibleVS()){
                  getVsUp().setVisible(true);
                  getVsDown().setVisible(true);
            } else {
                  getVsUp().setVisible(false);
                  getVsDown().setVisible(false);
            }               
        }
        
        super.renderComponent(c, g);
//      super.render(c, g, topLevel);
    }

    public int getTEXTFIELD_HEIGHT() {
        return TEXTFIELD_HEIGHT;
    }

    public SuiTextArea getTextDisplayArea() {
        return textDisplayArea;
    }

    public SuiTextField getTextField() {
        return textField;
    }
}
