package othi.thg.client;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.newdawn.slick.Font;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.opengl.CursorLoader;
import org.lwjgl.input.Cursor;

import othi.thg.client.THGClientDefault.GameState;
import othi.thg.client.OutfitStore.Part;
import othi.thg.client.entities.sprites.PlayerSprite;
import othi.thg.common.CommandListener;
import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;

import mdes.slick.sui.Sui;

/**
 * managing authentification 
 * @author Dong Won Kim
 */
public class THGkeeper extends BasicGameState implements ComponentListener {

    public static final int ID = 1;
    
    private StateBasedGame game;
    
    private static  final   int LOGINTEXTX    = 412;
    private static  final   int LOGINTEXTY    = 282;
    private static  final   int LOGINSTATUSX  = 200;
    private static  final   int LOGINSTATUSY  = 370;        
    private static  final   int LOGINBUTTONX  =	300;
    private static  final   int LOGINBUTTONY  =	420;
    private static  final   int EXITBUTTONX   = 410;
    private static  final   int EXITBUTTONY   = 420;
    private static  final   int	STARTBUTTONX  = 300;
    private static  final   int	STARTBUTTONY  = 420;
    private static  final   int	LOGOUTBUTTONX = 410;
    private static  final   int	LOGOUTBUTTONY = 420;
      
    private static  final   int INTROTEXTX      =   150;
    private static  final   int INTROTEXTY      =   125;
    private static  final   int INTROSKIPBUTTONX    =   412;
    private static  final   int INTROSKIPBUTTONY    =   510;
    private static  final   int INTROPREVBUTTONX    =   500;
    private static  final   int INTROPREVBUTTONY    =   510;
    private static  final   int INTRONEXTBUTTONX    =   590;
    private static  final   int INTRONEXTBUTTONY    =   510;
    
    //Define constant variables for text display
    private static  final   int	TEXTWIDTH   =   500;
    private static  final   int	TEXTHEIGHT  =   385;
    private static  final   int	LINESPACING =   28;
    private static  final   int	MAXIMUMLINE =   12;

    private static  final   int	LOBBYPLAYERX    =   340;
    private static  final   int	LOBBYPLAYERY    =   200;
       
    private static final long NANOMILLIS = 1000 * 1000;
    private static final long GAMELOADINGTIME = 5 * 1000 * NANOMILLIS;

   
    private static final long STAGELOADINGTIME = 2 * 1000 * NANOMILLIS;
           
    private GameContainer container;
    private Image WaitingScreen = null;    
    private Image stageLobbyScreen = null;   
    
    //button
    private Image startOff;
    private Image startOn;
    private Image logoutOff;
    private Image logoutOn;            
    
    private PlayerSprite lobbyPlayer;
    protected String[] scoreBoardNames;
    protected int[] scoreBoardScore;   
    
    private Image gameLoadingScreen = null;
    private Image gameIntroducingScreen = null;
    private Image loginScreen = null;
    
    //button
    private Image smallPrevOff;
    private Image smallPrevOn;
    private Image smallNextOff;
    private Image smallNextOn;
    private Image smallSkipOff;
    private Image smallSkipOn;
    
    private Image loginOff;
    private Image loginOn;    
    private Image exitOff;
    private Image exitOn;
        
    private MouseOverArea introPrevButton;
    private MouseOverArea introNextButton;
    private MouseOverArea introSkipButton;
    private MouseOverArea startButton;    
    private MouseOverArea loginButton;
    private MouseOverArea logoutButton;    
    private MouseOverArea exitButton;
    
    private TextField nameInput;
    private TextField passwordInput;
    private TextField passwordDisplay;

    private transient String loginStatus;
    private List<String> introductionOfGame = new LinkedList<String>();
    private List<String> textToDisplay = new LinkedList<String>();
    private int startIntroLine = 0;
    private boolean enablePrevButton = false;
    private boolean disableNextButton = true;
    private Rectangle textBorder;
    
    private boolean doneStageLoading = false;
    private boolean doneGameLoading = false; 
    private boolean receivedUserInfo = false; 
    private THGTimer loadingTimer = null;
    
    
    public THGkeeper() {

    }
    
    @Override
    public int getID() {
            return ID;            
    }
    
   /**
     * 
     * @param container 
     * @param game 
     * @throws org.newdawn.slick.SlickException 
     */
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;     
        this.container = container;

        Sui.init(container);

        Sui.setTheme(new THGTheme());
        Sui.setDefaultFont(Sui.getTheme().getSystemFont());

        gameLoadingScreen = new Image("gui/GameLoading.png");         
        doneGameLoading = false;
        setCursor();
    }
  
    private void setCursor() {
        try {
              Cursor arrowCursor = CursorLoader.get().getCursor("gui/arrow_cursor.png", 5, 0);
              container.setMouseCursor(arrowCursor, 5, 0);       
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SlickException e){
            e.printStackTrace();
        } catch (org.lwjgl.LWJGLException e) {
            e.printStackTrace();
        }           
    }
    
    private void loadGameResources() {
        try {
            gameIntroducingScreen = new Image("gui/GameIntro.png");    
            loginScreen = new Image("gui/LoginScreen.png");         
            WaitingScreen = new Image("gui/WaitingScreen.png");           
            stageLobbyScreen = new Image("gui/StageLobbyScreen.png");
            
            startOff = new Image("gui/start_off.png");
            startOn = new Image("gui/start_on.png");
            logoutOff = new Image("gui/logout_off.png");
            logoutOn = new Image("gui/logout_on.png");                           
            
            smallNextOff = new Image("gui/small_next_off.png");
            smallNextOn = new Image("gui/small_next_on.png");
            smallPrevOff = new Image("gui/small_prev_off.png");
            smallPrevOn = new Image("gui/small_prev_on.png");     
            smallSkipOff = new Image("gui/small_skip_off.png");
            smallSkipOn = new Image("gui/small_skip_on.png");
                      
            loginOff = new Image("gui/login_off.png");
            loginOn = new Image("gui/login_on.png");                       
            exitOff = new Image("gui/exit_off.png");
            exitOn = new Image("gui/exit_on.png");

            // Introduction Next button
            introNextButton =
                new MouseOverArea(container, smallNextOff, INTRONEXTBUTTONX,
                    INTRONEXTBUTTONY, this);
                    
            introNextButton.setMouseOverImage(smallNextOn);
            introNextButton.setAcceptingInput(false);

            // Introduction Previous button
            introPrevButton =
                new MouseOverArea(container, smallPrevOff, INTROPREVBUTTONX,
                    INTROPREVBUTTONY, this);
                    
            introPrevButton.setMouseOverImage(smallPrevOn);
            introPrevButton.setAcceptingInput(false);
            
            // Introduction Skip button
            introSkipButton =
                new MouseOverArea(container, smallSkipOff, INTROSKIPBUTTONX,
                    INTROSKIPBUTTONY, this);
                    
            introSkipButton.setMouseOverImage(smallSkipOn);
            introSkipButton.setAcceptingInput(false);
            
            // Login button
            loginButton =
                new MouseOverArea(container, loginOff, LOGINBUTTONX, LOGINBUTTONY, this);
            loginButton.setMouseOverImage(loginOn);
            loginButton.setAcceptingInput(false);
            
            // Exit button
            exitButton =
                new MouseOverArea(container, exitOff, EXITBUTTONX, EXITBUTTONY,this);
            exitButton.setMouseOverImage(exitOn);
            exitButton.setAcceptingInput(false);
            
            // start button
            startButton =
                new MouseOverArea(container, startOff, STARTBUTTONX,       STARTBUTTONY, this);
            startButton.setMouseOverImage(startOn);
            startButton.setAcceptingInput(false);
            
             // Logout button
            logoutButton =
                new MouseOverArea(container, logoutOff, LOGOUTBUTTONX, LOGOUTBUTTONY, this);
            logoutButton.setMouseOverImage(logoutOn);    
            logoutButton.setFocus(false);
            logoutButton.setAcceptingInput(false);
                             
            // text input field
            nameInput =
                new TextField(container, Sui.getTheme().getLoginFont(), LOGINTEXTX,
                    LOGINTEXTY, 195, 29, this);
            nameInput.setBorderColor(Color.gray);
            nameInput.setBackgroundColor(Color.white);
            nameInput.setTextColor(Color.darkGray);
            nameInput.setAcceptingInput(false);

            passwordInput = new TextField(container, Sui.getTheme().getLoginFont(), LOGINTEXTX,
                                          LOGINTEXTY + 39, 195, 29,this);
            passwordInput.setAcceptingInput(false);

            passwordDisplay = new TextField(container, Sui.getTheme().getLoginFont(), LOGINTEXTX,
                              LOGINTEXTY + 39, 195, 29,this);
            passwordDisplay.setBorderColor(Color.gray);
            passwordDisplay.setBackgroundColor(Color.white);
            passwordDisplay.setTextColor(Color.darkGray);
            passwordDisplay.deactivate();
            passwordDisplay.setAcceptingInput(false);

            //Create Text Box for Introduction
            textBorder = new Rectangle( INTROTEXTX-10, INTROTEXTY-5, TEXTWIDTH+30, TEXTHEIGHT);
            introductionOfGame = readText("course/overview.txt");
            textToDisplay = getTextToDisplay(introductionOfGame, startIntroLine, MAXIMUMLINE);
            
            loginStatus = "Enter user id and password!";
           
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void componentActivated(AbstractComponent source) {
        if (game.getCurrentStateID() == ID) { 
            if (source == exitButton){    
                doGameExit();                
            } else if (source == loginButton){
                if (source.isAcceptingInput() && isValidLoginInput()) doLogin();
            } else if (source == startButton) {
                doGameStart();
            } else if (source == logoutButton) {
                doGameLogout();
            } else if (source == nameInput) {
                passwordInput.setFocus(true);
            } else if (source == passwordInput) {
                nameInput.setFocus(true);
                if (source.isAcceptingInput() && isValidLoginInput()) doLogin();         
            } else if (source == introPrevButton) {
                doPreviousPage();
            } else if (source == introNextButton) {
                doNextPage();      
            } else if (source == introSkipButton) {
                doSkipIntro();
            }    
        }    
    }

    protected void setStartIntroLine(int i) {
        startIntroLine = i;
    }

    protected int getStartIntroLine() {
        return startIntroLine;
    }

    protected void doNextPage() {
        enablePrevButton = true;       
        int totalLine = introductionOfGame.size();
        int startLine = getStartIntroLine() + MAXIMUMLINE + 1;
       
        if (startLine <= totalLine) {
              setStartIntroLine(startLine);
        } 
        if ((totalLine - startLine - 1) <= MAXIMUMLINE ) { 
            disableNextButton = true;
        }
            
        textToDisplay = getTextToDisplay(introductionOfGame, getStartIntroLine(), MAXIMUMLINE);       
        
    }
        
    protected void doPreviousPage() {
        disableNextButton = false;
        startIntroLine =  startIntroLine -  MAXIMUMLINE - 1;
        
        if (startIntroLine  <=  0 ) {
                     enablePrevButton = false;
                     startIntroLine = 0;
        }  
        textToDisplay = getTextToDisplay(introductionOfGame, startIntroLine, MAXIMUMLINE);
         
    }
    
    protected List<String>  getTextToDisplay(List<String> allText, int startLine, int maximumLine ) {
        int lineNumber = 0;
        String aLine = "";
        List<String>  list = new LinkedList<String>();
        
        for ( Iterator<String> iter =  allText.iterator(); iter.hasNext();) {
                aLine = iter.next();
                if (lineNumber >= startLine) {                                  
                       list.add(new String(aLine));
                }
               ++lineNumber;
               if (lineNumber > (getStartIntroLine() + maximumLine)) {
                   disableNextButton = false;
                   break;
               }
        }
        
        return list;
    }
        
    protected void doSkipIntro() {
        THGClient.get().setGameState(GameState.LOGIN);
    }
        
     protected void doGameLogout() {    	 
         THGClient.get().getSimpleClient().logout(false);
    }
        
    protected void doGameExit() {
         container.exit();        
    }

     /**
     * 
     * @return 
     */
    protected boolean isValidLoginInput() {
        String userName = nameInput.getText();
        String userPassword = passwordInput.getText();
        if (userName. isEmpty() && userPassword.isEmpty() ) {
                nameInput.setFocus(true);
  //              passwordInput.setFocus(false);
                loginStatus = "Enter user id and password!";
                return false; 
        } else if (userName. isEmpty())  {
               nameInput.setFocus(true);
  //             passwordInput.setFocus(false);
               loginStatus = "Enter user id!";
               return false;
        } else if (userPassword.isEmpty()) {
               passwordInput.setFocus(true);
   //            nameInput.setFocus(false);
               loginStatus = "Please enter password above.";
               return false;
        } else {
               return true;
        }
    }
       
    /**
     * 
     */
    protected void doLogin() {
        THGClient.get().setGameState(GameState.LOGINING);
        THGClient.get().setUserName(nameInput.getText());
        THGClient.get().setUserPassword(passwordInput.getText());
        THGClient.get().doLogin();
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int timeDelta) throws SlickException  {
        processOneMessage();        
        
        GameState state = THGClient.get().getGameState();
              
        if (state == GameState.GAMELOADING) {
            if (!doneGameLoading) {
                loadingTimer = new THGTimer(GAMELOADINGTIME);
                loadGameResources();
                doneGameLoading = true;
                return;
            } else {
                if (loadingTimer.checkTimeExpired()) {
                    
                /*  If you want to notice players something, 
                    uncomment the below statement and 
                    comment the next statement. */                     
//                  THGClient.get().setGameState(GameState.INTRODUCING);
                    THGClient.get().setGameState(GameState.LOGIN);
                    nameInput.setAcceptingInput(true);
                    nameInput.setFocus(true);
                    passwordInput.setAcceptingInput(true);
                    passwordDisplay.setAcceptingInput(true);
                    loginButton.setAcceptingInput(true);
                    exitButton.setAcceptingInput(true);
                    return;
                }
            }
        }        
        
        if (state == GameState.LOGINED) {
            if (receivedUserInfo) {
                nameInput.setText("");
                nameInput.setAcceptingInput(false);
                passwordInput.setText("");
                passwordInput.setAcceptingInput(false);
                passwordDisplay.setAcceptingInput(false);
                loginButton.setAcceptingInput(false);
                exitButton.setAcceptingInput(false);
                doneStageLoading = false;                
                receivedUserInfo = false;
                THGClient.get().setGameState(GameState.STAGELOADING);
                return;
            }     
        } 
        
        if (state == GameState.LOGINFAILED) {
                loginStatus = "Failed to login!";
                THGClient.get().setGameState(GameState.LOGIN);
                return;    
        }
        
        if (state == GameState.STAGELOADING) {
            if (!doneStageLoading) {
                loadingTimer = new THGTimer(STAGELOADINGTIME);
                setPlayer();
                doneStageLoading = true;
                return;
            } else {
                if (loadingTimer.checkTimeExpired()) {
                      doneStageLoading = false;
                      THGClient.get().setGameState(GameState.LOBBY);
                      startButton.setAcceptingInput(true);
                      logoutButton.setAcceptingInput(true);
                      startButton.setFocus(true);        
                      return;
                }
            }
        }     
        
        if  (state == GameState.DISCONNECTED) {                     
            resetToLogin();
        }
    }
        
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException
    {
        GameState state = THGClient.get().getGameState();
        
        switch (state) {
        case GAMELOADING:
            gameLoadingScreen.draw(0, 0);
            break;
        case INTRODUCING:
            gameIntroducingScreen.draw(0, 0);
            g.setLineWidth(5);
            Color oldColor =  g.getColor();
            g.setColor(Color.blue);
            g.draw(textBorder);
            introSkipButton.render(container, g);    
            if (!disableNextButton) introNextButton.render(container, g);  
            if  (enablePrevButton)  introPrevButton.render(container, g);     
            int lineNumber = 0;
            for ( Iterator<String> iter = textToDisplay.iterator(); iter.hasNext();) {             
                 String text = iter.next();
                 Sui.getTheme().getSystemFont().drawString(INTROTEXTX, INTROTEXTY + (LINESPACING * lineNumber), text, Color.blue);
                 ++lineNumber;
            }
            g.setColor(oldColor);
            break;
        case LOGIN:
            loginScreen.draw(0, 0);
            nameInput.render(container, g);
            
            String maskedPassword = getMaskedPassword(passwordInput);
            passwordDisplay.setText(maskedPassword);
            if (passwordInput.hasFocus()) {
                passwordDisplay.setFocus(true);
                passwordDisplay.setCursorPos(maskedPassword.length());
                passwordDisplay.render(container, g);
                passwordInput.setFocus(true);
            } else {
                passwordDisplay.render(container, g);
            }


            loginButton.render(container, g);            
            exitButton.render(container, g);                   
            Sui.getTheme().getSystemMsgFont().drawString(LOGINSTATUSX, LOGINSTATUSY, loginStatus, Color.blue);
            break;
        case LOGINING:  
        case LOGINED:              
        case STAGELOADING:
             WaitingScreen.draw(0, 0);           
            break;
        case LOBBY:
            stageLobbyScreen.draw(0, 0);
            for (Part p : Part.values()) {
                 Image img = lobbyPlayer.getCurrentImage(p.ordinal());   
                 img.draw(LOBBYPLAYERX, LOBBYPLAYERY, 2.5f);
            }

            Font font = g.getFont();
            Color color = g.getColor();
            g.setFont(Sui.getTheme().getUserName42());
            g.setColor(Color.blue);
            g.drawString("Hello "+ THGClient.get().getUserName() + "!", LOBBYPLAYERX - 80, LOBBYPLAYERY + 160);
            startButton.render(container, g);
            logoutButton.render(container, g);
            g.setFont(font);
            g.setColor(color);
            break;
            
        default:
        }       

    }

    private String getMaskedPassword(TextField passwdInput){
        String password = passwdInput.getText();
        password = password.replaceAll("[a-zA-Z1-9\\p{Punct}]", "*");
        return password;
    }

    private void resetToLogin() {    	
        startButton.setAcceptingInput(false);
        logoutButton.setAcceptingInput(false);
        THGClient.get().initializeMyID();
        THGClient.get().setGameState(GameState.LOGIN);        
        loginStatus = "Enter user id and password!";
        nameInput.setAcceptingInput(true);
        passwordInput.setAcceptingInput(true);
        passwordDisplay.setAcceptingInput(true);
        loginButton.setAcceptingInput(true);
        exitButton.setAcceptingInput(true);
        nameInput.setFocus(true);
    }

    private void setPlayer() {
        lobbyPlayer = THGClient.get().getSprite();
    }               
    
    protected void doGameStart() {
        startButton.setAcceptingInput(false);
        logoutButton.setAcceptingInput(false);
        THGClient.get().setGameState(GameState.PAUSED);
        THGClient.get().send(Commands.startCommand(THGClient.get().getMyID()));
        try {
            game.getState(ID).leave(container, game);  
        } catch (SlickException e) {            
            Log.error("Starting game got an error!", e);
        }     
        game.enterState(THGWorld.ID,  new EmptyTransition(), new EmptyTransition());
    }      

    public void processOneMessage() {
        
         ConcurrentLinkedQueue<ByteBuffer> inputQueue = THGClient.get().getInputQueue();
                
        if (inputQueue.isEmpty()) {
            return;
        }
         
        ByteBuffer buff = inputQueue.remove();       
                
        // we get all the packets for one turn at the same time
        while (buff.remaining() > 0) {
            
            Commands.parseCommandBuffer(buff, new CommandListener() {
                    @Override                
                    public void commandHp(int id, int hp){
                  
                    }   
                    
                    @Override                                
                    public void commandAddFood(int id, String name, float x, float y, int attractionPoint, String imgRef) {
                        
                    }

                    @Override
                    public void commandClearDialogueHistory(int id){

                    }

                    @Override
                    public void commandClearMsgLog(int id){

                    }
                    
                    @Override                
                    public void commandBlock(int id, float x, float y){
                  
                    }   
                    
                    @Override                
                    public void commandRequestLevel(int id){

                    }
                    
                    @Override
                    public void commandRequestPassAway(int id){

                    }
                    
                    @Override
                    public void commandPassAway(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int money){

                    }

                    @Override
                    public void commandLevel(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int power) {

                    }
                    
                    @Override                   
                    public void commandPayment(int id, int treasureId, String name, int hp, int mp, int point, int money, String armor, String tool, String weapon){
                        
                    }
                    
                    @Override                   
                    public void commandAnswerQuiz(int id, int placeId, int treasureId, int topicId, String answer) {

                    }
                    
                    @Override                              
                    public void commandQuestIntroduction(int id, int npcId, String npcName, int questID, String questName, String introduction){

                    }

                    @Override
                    public void commandQuestQuestion(int id, int npcId, String npcName, int questID, String questName, String question) {

                    }

                    @Override
                    //For help and clue
                    public void commandQuestInformation(int id, int npcId, String npcName, int questID, String questName, String information, int iType) {

                    }
                    
                    @Override              
                    public void commandChangeQuestStatus(int id, int questID, int status) {

                    }        
                    
                    @Override                
                    public void commandAttackMonster(int id, int placeId, int monsterId){

                    }
                    
                    @Override
                    public void commandTalk(int id, String name, String speaking){
                    }        
                    
                    @Override
                    public void commandTalktoNPC(int npcId, int replyFlag, String dailogue){  
                        
                    }     
                    
                    @Override                    
                    public void commandKill(int id) {
                        // TODO Auto-generated method stub

                    }

    				@Override
    				public void commandPortal(int id, int placeId, String portalName) {
    					
    				}
    				
    				@Override				
    		        public void commandAddPortal(int id, int placeId, String portalName, float x, float y, int isOneWay){
    		        					
    		        }
    				
                    @Override                    
                    public void commandMoveForward(int id, int placeId, float tx, float ty, Direction direction){
                    	
                    }
                                        
                    @Override  
                    public void commandTurn(int id, int placeId, Direction direction) {
                    	
                    }                    
                    
                    @Override  
                    public void commandScore(int id, int score) {

                    }
                    
                    @Override                    
                    public void commandEnterGameBoard(int id, int placeId, String mapFileRef) {
                        
                    }
                    
                    @Override                      
                    public void commandInitializePlayer(
                            int id, int level, int hp, int maxHp,
                            int mp, int maxMp, int exp, int maxExp,
                            int money, String[] dialogues, int outfitCode ) {
                               THGClient.get().setMyID(id);
                               THGClient.get().setDialogues(dialogues);
                               PlayerSprite sprite = new PlayerSprite(outfitCode, 0, 0, Direction.SOUTH);                               
                               sprite.setGameLevel(level);    
                               sprite.setHP(hp);
                               sprite.setMaxHp(maxHp);
                               sprite.setMP(mp);
                               sprite.setMaxMp(maxMp);
                               sprite.setExp(exp);
                               sprite.setMaxExp(maxExp);                               
                               sprite.setMoney(money);                              
                               THGClient.get().setSprite(sprite);
                               receivedUserInfo = true;
                    }
                    
                    @Override
                    public void commandOpenTreasure(int id, int placeId, int treasureId, int topicId){
                        
                    }
                  
                    @Override  
                    public void commandJoinGroup(int id) {

                    }

                    @Override  
                    public void commandLeaveGameBoard(int id, String placeName) {
 

                    }
                    
                    @Override                                         
                    public void commandStart(int id) {

                    }

                    @Override                      
                    public void commandStop(int id) {

                    }

                    @Override                    
                    public void commandAddPlayer(int id, String name, int level, int outfitCode,
                                                float x, float y, int facing)
                    {
                    }
                    
                    @Override
                    public void commandAddNPC(int id, String name, int level, int hp, int mp,
                                                String imgRef, float x, float y, int facing)
                    {

                    }

                    @Override
                    public void commandAddTreasure(int treasureId, int questID, float x, float y) {
                        // TODO Auto-generated method stub

                    }   

                    @Override  
                    public void commandFrame(int id, int frameCount) {
                        // TODO Auto-generated method stub

                    }

                    @Override  
                    public void commandSetID(int id) {
                           THGClient.get().setMyID(id);
                    }

                    @Override
                    public void commandAddMonster(int id, String name, float x, float y, int gameLevel, int hp, int mp, int power,
                                                  Direction direction, String imgRef)
                    {
                        // TODO Auto-generated method stub
                    }
                    
                    @Override  
                    public void commandRemoveFood(int id, float x, float y) {
                        // TODO Auto-generated method stub

                    }

                    @Override  
                    public void commandAddMonsterTomb(int id, float x, float y) {

                    }

                    @Override  
                    public void commandRemoveMonster(int id) {

                    }

                    @Override  
                    public void commandRemoveMonsterTomb(int id, float x, float y) {

                    }

                    @Override  
                    public void commandRemovePlayer(int id) {
                                        
                    }

                    @Override                    
                    public void commandGiveQuestion(int id, int treasureId, int questID){
                        
                    }

                    @Override
                    public void commandGiveMark(int id, int questID, int mark) {

                    }

                    @Override  
                    public void commandAddPlayerTomb(int id, float x, float y) {

                    }

                    @Override  
                    public void commandRemovePlayerTomb(int id, float x, float y) {

                    }

                    @Override                      
                    public void commandLogin(String userName, String password)
                    {
                        // TODO Auto-generated method stub
                    }

                });
        }
    }        
    
    public List<String> readText(String ref) {
        
        List<String> text = new LinkedList<String>();
        
        try {        
                InputStream in = ResourceLoader.getResourceAsStream(ref);
                int byteRead = -1;
                String textLine = "";
               
                while ((byteRead = in.read()) != -1) {                
                      textLine = textLine + (char) byteRead;
                      if ( Sui.getTheme().getSystemFont().getWidth(textLine) > TEXTWIDTH) {
                            text.add(new String(textLine));
                            textLine = "";
                      }                                  
                }
                
                if (!textLine.isEmpty())  text.add(new String(textLine));
               
                in.close();
               
        } catch (FileNotFoundException e1) {
               System.err.println("File not found: " + ref);
               
        } catch (IOException e2) {
               e2.printStackTrace();
        }
        
        return text;
    }     
}
