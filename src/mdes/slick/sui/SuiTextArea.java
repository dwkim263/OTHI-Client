package mdes.slick.sui;

import org.newdawn.slick.*;
import java.util.*;
import java.text.*;

/**
Modified by DongWon Kim
 */
public class SuiTextArea extends SuiLabel {

    /** The character which denotes a line break. */
    public static final String NEWLINE = "\n";

    private final int MAX_WORD_LENGTH = 28; //measuring letter 'W'

    /** The text lines to render. */
    private List<TextLine> lines = new ArrayList<TextLine>();
    
    /** Whether to auto-adjust height on wrapping. */
    private boolean adjustingHeight;
    
    protected Color textAreaBorderColor;

    /** The amount of padding between lines. */
    private int linePad = 2;
    
   
    protected int rowHeight;
    protected int colWidth;
    private int currentHighestIndex = 0;
    private int maxLines = 0;
      
    private int indentationSize = 0;
  
    public SuiTextArea(String text) {
        super(text);        

        textAreaBorderColor = new Color (this.getBorderColor());
        textAreaBorderColor.a = .55f;

        setPadding(5);
                
        this.adjustingHeight = false;
        
        lines.add(new TextLine(text));        
                
        setHorizontalAlignment(SuiLabel.LEFT_ALIGNMENT);
        setVerticalAlignment(SuiLabel.TOP_ALIGNMENT);
        
        rowHeight = getFont().getHeight("M");
        colWidth = getFont().getWidth("m");
    }    
        
    public SuiTextArea() {
        this("");     
    }
    
    public void setTexts(String[] texts) {
        currentHighestIndex = 0;
        for (int i=0; i < texts.length; ++i) {
              append(texts[i]);
        }
    }

    public void setLogTexts(String[] texts) {
        currentHighestIndex = 0;
        for (int i=0; i < texts.length; ++i) {
              appendLog(texts[i]);
        }
    }

    public void clearTexts(){
        lines.clear();
        setText("");
    }

    public int getLineCount() {
        return lines.size();
    }
    
    public void setAdjustingHeight(boolean b) {
        adjustingHeight = b;
    }
    
    public boolean isAdjustingHeight() {
        return adjustingHeight;
    }
            
    public void append(String s) {
        setText(getText()+s);
        if (maxLines == 0) setMaxLines(); 
        wrap();
        if (lines.size() <= maxLines ) {
            currentHighestIndex = currentHighestIndex = lines.size()-1;
        } else {
            currentHighestIndex = maxLines - 1;
        }
    }

    public void appendLog(String s) {
        setText(getText()+s);
        if (maxLines == 0) setMaxLines();
        wrap();
    }
    
    public void append(char c) {
        append(String.valueOf(c));
    }

    protected void setMaxLines (){
        int h  = 0;
        maxLines = 0;
        do {
               ++ maxLines;
               h  = maxLines * rowHeight  + (maxLines-1) * getLinePadding();
//               h  = maxLines * (rowHeight - getYOffset("M")) + (maxLines-1) * getLinePadding();
        } while (h < (getHeight() - getPadding() * 2));
        --maxLines;
    }        
    
    public int getMaxLines() {
       if (maxLines == 0) setMaxLines();
       return maxLines;   
    }
    
    protected int getColumnWidth() {
        return colWidth;
    }
    
    protected int getRowHeight() {
        return rowHeight;
    }    

    @Override
    public void renderComponent(GameContainer c, Graphics g) {
        if (isOpaque()) {
            Color old = g.getColor();
            g.setColor(getBackgroundColor());
            g.fillRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
            g.setColor(old);
        }
        
        if (getImage()!=null) {
            drawImage(g, getImage());
        }
        
        if (text!=null&&!lines.isEmpty()) {
            
           if (lines.size()==1) {
                drawString(g, lines.get(0).text, getAbsoluteX()+getPadding(),
                              getAbsoluteY()+getPadding());
            } else {
                int heightDif = Math.max(getHeight()-getTextBlockHeight(), 0);
                
                float top = getAbsoluteY() + getPadding();
                float xpos = getAbsoluteX()+getPadding();
                float ypos = top+getHeight()- getPadding() - getLinePadding() - heightDif;
          
                int i = currentHighestIndex;
                int j = 0;
                while (i>=0 && j< maxLines)  {
                    if (ypos<top) {
                        break;
                    }
  
                    TextLine tl = lines.get(i);
                    drawString(g, tl.text, xpos, ypos);
                    ypos -= tl.height + getLinePadding();
                    --i;
                    ++j;
                }
            }
        }        
    }
    
    public void increaseCurrentHighestIndex(){
        ++currentHighestIndex;        
        if (currentHighestIndex > lines.size()-1)  currentHighestIndex = lines.size()-1;
    } 
    
    public void decreaseCurrentHighestIndex(){
        --currentHighestIndex;        
        if (currentHighestIndex < maxLines ) currentHighestIndex = maxLines - 1;
    }   
    
    public int getCurrentHighestIndex() {
         return currentHighestIndex;    
    }

    public void setCurrentHighestIndex(int currentHighestIndex) {
        this.currentHighestIndex = currentHighestIndex;
    }
    
    private int getTextBlockHeight() {
        int lnum = currentHighestIndex+1;
        if (lnum==0)
            return 0;
        else if (lnum==1)
            return rowHeight;
        else if (lnum >= maxLines)
            return maxLines * rowHeight + ((maxLines-1)*linePad);
        else 
            return lnum *rowHeight + ((lnum-1)*linePad);
    }
    
    public int getLinePadding() {
        return linePad;
    }
    
    public void setLinePadding(int i) {
        this.linePad = i;
    }
    
    @Override
    public void pack() {
        int old = getWidth();
        super.pack();
        setWidth(old);
        wrap();
    }    
    
    public void wrap() {            
        int width = getWidth();
        String textData = getText();
        
        if (textData==null || textData.length()==0 || width==0)
          return;
        
        lines.clear();

        //preventing a longer word than maximum number of word length
        String[] texts =  textData.split(" ");

        textData = "";

        for (String aText : texts) {
           int start = 0;
           int end = start + MAX_WORD_LENGTH;
           while (end < aText.length()) {
               textData = textData + aText.substring(start, end) + " ";
               start = end;
               end = start + MAX_WORD_LENGTH;
           }
           textData = textData + aText.substring(start,  aText.length()) + " ";
        }        
        textData = textData.substring(0, textData.length() - 1);

        // Boundary define
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(textData);
        
        StringBuffer temp = new StringBuffer();
        
        int start = boundary.first();
        
        //start off with padding height
        if (adjustingHeight)
            setHeight(getPadding());
       
        String t = "";
        boolean pdone = false;
        for (int end = boundary.next(); end != BreakIterator.DONE; 
                          start = end, end = boundary.next())
        {
              String word = textData.substring(start, end);

              temp.append(word);
          
              t = temp.toString().trim();
              int tempWidth = getFont().getWidth(t);
              String lookahead = "";
              if (end+1 < textData.length()-1) {
                    start = end;
                    end = boundary.next();
                    lookahead = textData.substring(start, end);
                    end = boundary.previous();
              }
              
              //TODO: fix padding on the right
              
              //jump down a line
              if (lookahead.equals(NEWLINE)||
                    tempWidth > width - getPadding()*2-getFont().getWidth(lookahead)) {               
                TextLine tl = new TextLine(t);                    
                lines.add(tl);           
                increaseCurrentHighestIndex();                
                temp = new StringBuffer();
                if (adjustingHeight)
                    setHeight(Math.max(getHeight(), 
                              getHeight()+tl.height+getLinePadding()));
              }
          
        }
        
        //add the last line
        TextLine last = new TextLine(t);
        lines.add(last);
        increaseCurrentHighestIndex();          
        if (adjustingHeight)
            setHeight(Math.max(getHeight(),getHeight()+last.height+getPadding()));
    }
    
    @Override
    protected void renderBorder(GameContainer c, Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(hasFocus() ? textAreaBorderColor : getBorderColor());
        g.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight()-1);
        g.setColor(oldColor);
    }
        
    private class TextLine {
        int height;
        String text;
    
        public TextLine(String text) {
          this.text = text;
          
          if (text.contains(NEWLINE))
            text="";
          
          int yoff = (text!=null&&text.length()!=0)
                        ? getYOffset(text)
                        : 0;
          
          int th = (text!=null&&text.length()!=0)
                            ? getFont().getHeight(text)
                            : getRowHeight();
          
          this.height = th-yoff;
        }
    }

    public void setIndentationSize(int indentationSize) {
        this.indentationSize = indentationSize;
    }

    public Color getTextAreaBorderColor() {
        return textAreaBorderColor;
    }

    public void setTextAreaBorderColor(Color textAreaBorderColor) {
        this.textAreaBorderColor = textAreaBorderColor;
    }

    public int getMAX_WORD_LENGTH() {
        return MAX_WORD_LENGTH;
    }

}
