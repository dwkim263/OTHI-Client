package othi.thg.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.state.transition.EmptyTransition;

import othi.thg.client.GameDefault.GameState;
import othi.thg.client.GameDefault.Terrain;
import othi.thg.client.entities.*;
import othi.thg.client.entities.sprites.FoodSprite;
import othi.thg.client.entities.sprites.MonsterSprite;
import othi.thg.client.entities.sprites.NpcSprite;
import othi.thg.client.entities.sprites.PlayerSprite;
import othi.thg.client.entities.sprites.THGSprite;
import othi.thg.client.entities.sprites.TreasureSprite;
import othi.thg.client.entities.sprites.THGSprite.SpriteState;
import othi.thg.client.gui.*;
import othi.thg.client.pathFinding.DJKPathFinder;
import othi.thg.client.pathFinding.Path;
import othi.thg.common.CommandListener;
import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;

import mdes.slick.sui.*;
import mdes.slick.sui.event.*;
import mdes.slick.sui.suiList.*;

/**
 * getting input from player
 * managing game objects
 * displaying game
 * @author Dong Won Kim
 */
public class THGground extends BasicGameState {
	
	private static final Logger logger = Logger.getLogger(THGground.class.getName());

	public static final int ID = 2;

	private StateBasedGame game;        

	// private static final long PLAYERTURNTICK = 500 * 1000 * 1000;
	// private static final long DEBOUNCEPERIOD = 100 * 1000 * 1000;
	private static final long GAMETURNTICK = 100 * 1000 * 1000; //
	private static final long DEBOUNCEPERIOD = 100 * 1000 * 1000;   
	private static final int FRAMESPERSECOND = 30;
	private static final long NANOMILLIS = 1000 * 1000;

	//map layers
	private static final int COLLISION_LAYER = 6;
	private static final int OBJECT_LAYER = 5;

	//private static final float BOOMVOL = 0.40f;
	private static final float THUDVOL = 0.25f;
	private static final float HARPVOL = 0.25f;    

	private static final int REPLY_ON = 1;    
	private static final int REPLY_OFF = 0;     
	private static final int MAX_DISTANCE = 10;   //find closest sprites

	private Input input;
	private long currentTime;
	private long lastTime = -1;
	private long lastTickTime;
	private long lastInputTime;
	private long responseTime;        

	private volatile boolean isMoveCompleted = true;

	private GameContainer container = null;

	private Image clickedSpotImg;
	private Image talkableCursorImg;  
	private Image attackCursorImg;        
	private Image monsterTombTile = null;
	private Image playerTombTile = null;
	private Audio harp1Sound;
	//    private Audio boomSound;
	private Audio churchbellSound;

	private volatile Map<Integer, FoodSprite> foods = new HashMap<Integer, FoodSprite>();
	private volatile Map<Integer, MonsterSprite> monsters = new HashMap<Integer, MonsterSprite>();	
	private volatile Map<Integer, NpcSprite> npcs = new HashMap<Integer, NpcSprite>();
	private volatile Map<Integer, PlayerSprite> players = new HashMap<Integer, PlayerSprite>();
	private volatile Map<Integer, Portal> portals = new HashMap<Integer, Portal>();	
	private volatile Map<Integer, TreasureSprite> treasures = new HashMap<Integer, TreasureSprite>();

	private int frameCounter = 0;

	private boolean gameInitialized = false;

	/** The width of the display in tiles */
	private int widthInTiles;
	/** The height of the display in tiles */
	private int heightInTiles;

	/** The offset from the centre of the screen to the top edge in tiles */
	private int topOffsetInTile;

	/** The offset from the centre of the screen to the left edge in tiles */
	private int leftOffsetInTile;        

	DJKPathFinder pathfinder = null;

	private int pathIndex = 0;

	private int currentPathIndex = 0;

	private int trialCount = 0;

	//GUI
	private THGSprite mouseOverSprite;

	private TalkableCursor talkableCursor;

	private AttackCursor attackCursor;

	private ClickedSpot clickedSpot;

	private ControlPanel controlPanel = null;

	private StatusPanel statusPanel = null;   


	private Map<Integer, String[]> myTreasures = null;

	//treasureListWindow
	private TreasureListWindow treasureListWindow = null;

	//Answer
	private QuizWindow quizWindow = null;        

	//questListWindow
	private QuestListWindow questListWindow = null;

	//suggest window
	private QuestIntroductionWindow questIntroductionWindow = null;

	private ScrollCloseWindow questInformationWindow = null;

	private List<SuiWindow> windows = new LinkedList<SuiWindow>();   

	//quest-completed notice Window
	private List<NoticeWindow> questCompletedNoticeWindows = new LinkedList<NoticeWindow>();
	private List<NoticeWindow> noticeWindows = new LinkedList<NoticeWindow>();    

	public THGground() {

	}

	@Override    
	public int getID() {
		return ID;            
	}

	private int getMyID() {
		return THGClient.get().getMyID();
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
		this.container.setUpdateOnlyWhenVisible(false);
		// make input
		input = container.getInput();        

		loadResources();

		buildControlPanel();

		buildQuizWindow(); 

		buildQuestIntroductionWindow();

		buildQuestInformationWindow();

		buildQuestListWindow();

		buildTreasureListWindow(); 

		buildStatusPanel();

		talkableCursor = new TalkableCursor(talkableCursorImg);
		attackCursor = new AttackCursor(attackCursorImg);
		clickedSpot = new ClickedSpot(clickedSpotImg);

		// clear players array
		players.clear();

		gameInitialized = false;        
	}  

	/** Sets up resources & GUI. */
	public void buildStatusPanel(){
		try {
			//we'll use a label because we want a specific look to it
			Image windowImg = new Image("gui/rpgwindow.png");          
			statusPanel = new StatusPanel(windowImg);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			Log.error(e);
		}          
	}

	private void buildTreasureListWindow() {
		treasureListWindow = new TreasureListWindow();         
		treasureListWindow.getIntroSuiListForTreasure().addListListener(new SuiListListener() {                    
			@Override                
			public void selectionChanged(SuiListEvent e) {                    
				final NoticeWindow noticeWindow = getNoticeWindow();                
				noticeWindow.setHeight(ScrollWindow.getSCROLLWINDOW_HEIGHT());                
				SuiButton closeBtn = noticeWindow.getCloseButton();
				SuiActionListener closeListener = noticeWindow.getCloseListener();         
				if (closeListener != null) closeBtn.removeActionListener(closeListener);
				//add an action so the button will do something when we click it
				closeListener = new SuiActionListener() {
					@Override
					public void actionPerformed(SuiActionEvent e) {
						Log.info("Button clicked!");
						treasureListWindow.getIntroSuiListForTreasure().clearSelection();
						noticeWindow.setVisible(false);                                 
					}
				};
				closeBtn.addActionListener(closeListener);
				noticeWindow.setCloseListener(closeListener);

				int index = treasureListWindow.getIntroSuiListForTreasure().getSelectedIndex();

				Collection<String[]> treasureList = myTreasures.values();                
				Iterator<String[]> iter = treasureList.iterator();
				int i = 0;
				while (i != index) {
					iter.next();  
					++i;
				}
				String[] treasure = iter.next();                
				String title = treasure[0];
				String objective = treasure[1]; 
				String overview = treasure[2];
				String introduction = treasure[3];
				String conclusion = treasure[4];

				SuiTextArea textArea = noticeWindow.getTextArea();                
				textArea.setText("");
				noticeWindow.setTitle(title);
				i = 5;
				while (i < treasure.length ) {
					textArea.append(treasure[i]);                    
					++i;
				}                              
				noticeWindow.setVisible(true);                    
			}
		});        
		windows.add(treasureListWindow);        
	}

	private void buildQuestListWindow() {     
		questListWindow = new QuestListWindow();

		questListWindow.getQuestNameList().addListListener(new SuiListListener() {
			@Override                
			public void selectionChanged(SuiListEvent e) {    
				int index = questListWindow.getQuestNameList().getSelectedIndex();

				Map<Integer, Quest> myQuests = THGClient.get().getMyQuests();
				if (!myQuests.isEmpty() && index < myQuests.size()) {
					Collection<Quest> questList = myQuests.values();
					Iterator<Quest> iter = questList.iterator();
					int i = 0;
					while (i != index) {
						iter.next();
						++i;
					}

					final NoticeWindow noticeWindow = getNoticeWindow();
					/*
                    noticeWindow.setHeight(ScrollWindow.getSCROLLWINDOW_HEIGHT()/3);
                    noticeWindow.remove(noticeWindow.getCloseButton());
					 */
					noticeWindow.formDefaultComponents();
					SuiButton closeBtn = noticeWindow.getCloseButton();
					SuiActionListener closeListener = noticeWindow.getCloseListener();
					if (closeListener != null) closeBtn.removeActionListener(closeListener);
					//add an action so the button will do something when we click it
					closeListener = new SuiActionListener() {
						@Override
						public void actionPerformed(SuiActionEvent e) {
							Log.info("Close Button clicked!");
							questListWindow.getQuestNameList().clearSelection();
							noticeWindow.setVisible(false);
						}
					};
					closeBtn.addActionListener(closeListener);
					noticeWindow.setCloseListener(closeListener);

					Quest quest = iter.next();

					SuiTextArea textArea = noticeWindow.getTextArea();
					textArea.setText("");
					noticeWindow.setTitle(quest.getName());
					textArea.append("[Guide] " + quest.getGuideName() + SuiTextArea.NEWLINE);
					textArea.append("[Status] " + quest.getState().toString() + SuiTextArea.NEWLINE);
					textArea.append("[Introduction]" + SuiTextArea.NEWLINE);
					textArea.append(quest.getIntroduction());
					String question = quest.getQuestion();
					if (question != null) {
						textArea.append(SuiTextArea.NEWLINE + "[Question]" + SuiTextArea.NEWLINE);
						textArea.append(question);
					}
					noticeWindow.setVisible(true);
				}
			}
		});
		windows.add(questListWindow);
	}

	private void loadResources() {     

		SoundStore.get().init();

		try {
			talkableCursorImg = new Image("gui/talkable_cursor.png");
			attackCursorImg = new Image("gui/attack_target.png");            

			monsterTombTile = new Image("gui/monster_tomb.png");
			playerTombTile = new Image("gui/player_tomb.png");

			//            arrowCursorImg = new Image("gui/arrow_cursor.png");
			clickedSpotImg = new Image("gui/click_spot.png");            

			//            boomSound = SoundStore.get().getOggStream("sounds/shotgun2.ogg");
			churchbellSound = SoundStore.get().getWAV("sounds/churchbell.wav");

			harp1Sound = SoundStore.get().getWAV("sounds/harp-1.wav");

		} catch (SlickException e) {
			// TODO Auto-generated catch block
			Log.error(e);
		} catch (IOException e) {
			Log.error(e);
		}
	}    

	private void buildControlPanel(){
		try {            
			Image panelImg = new Image("gui/controlPanel.png");
			controlPanel = new ControlPanel(Sui.getTheme().getSystemFont(), panelImg);

			MessageLogWindow messageLogWindow = controlPanel.getMessageLogWindow();
			SuiButton showDiaHisWin = controlPanel.getShowDiaHisWin();
			SuiButton helloBtn = controlPanel.getHelloBtn();
			SuiButton helpBtn = controlPanel.getHelpBtn();
			SuiButton byeBtn = controlPanel.getByeBtn();
			SuiButton clearBtn = controlPanel.getClearBtn();
			SuiButton treasuresBtn = controlPanel.getTreasuresBtn();
			SuiButton questBtn = controlPanel.getQuestBtn();
			SuiButton stopBtn = controlPanel.getStopBtn();

			showDiaHisWin.addActionListener(new SuiActionListener() {
				@Override  
				public void actionPerformed(SuiActionEvent e) {  
					MessageLogWindow messageLogWindow = controlPanel.getMessageLogWindow();                    
					if (messageLogWindow.isVisible()) {
						messageLogWindow.setVisible(false);
					} else {
						messageLogWindow.setLocation(controlPanel.getHelloBtn().getX(), controlPanel.getY()- ControlPanel.getDIALOGUE_HIST_HEIGHT());
						messageLogWindow.setVisible(true);
					}           
				}
			});

			//add an action so the button will do something when we click it
			helloBtn.addActionListener(new SuiActionListener() {
				@Override
				public void actionPerformed(SuiActionEvent e) {
					Log.info("Hello Button clicked!");
					THGSprite talkableSprite = getTalkableSpriteAround();
					if (talkableSprite != null) giveGreeating(talkableSprite);
				}
			});

			//add an action so the button will do something when we click it
			helpBtn.addActionListener(new SuiActionListener() {
				@Override
				public void actionPerformed(SuiActionEvent e) {
					Log.info("Help Button clicked!");
					THGClient.get().setSendQuery("Help!", REPLY_ON);
				}
			});

			//add an action so the button will do something when we click it
			byeBtn.addActionListener(new SuiActionListener() {
				@Override
				public void actionPerformed(SuiActionEvent e) {
					Log.info("Bye Button clicked!");
					THGClient.get().setSendQuery("Bye!", REPLY_OFF);
				}
			});

			//add an action so the button will do something when we click it
			clearBtn.addActionListener(new SuiActionListener() {
				@Override
				public void actionPerformed(SuiActionEvent e) {
					Log.info("Clear Button clicked!");
					sendToServer(Commands.clearDialogueHistoryCommand(getMyID()));
				}
			});

			//add an action so the button will do something when we click it
			stopBtn.addActionListener(new SuiActionListener() {
				@Override
				public void actionPerformed(SuiActionEvent e) {
					Log.info("Stop Button clicked!");                
					stopPlaying();
				}
			});            

			//add an action so the button will do something when we click it
			questBtn.addActionListener(new SuiActionListener() {
				@Override
				public void actionPerformed(SuiActionEvent e) {
					Log.info("Quests Button clicked!");
					questListWindow.setVisible(!questListWindow.isVisible());   
				}
			});       

			//add an action so the button will do something when we click it
			treasuresBtn.addActionListener(new SuiActionListener() {
				@Override
				public void actionPerformed(SuiActionEvent e) {
					Log.info("Treasures Button clicked!");                
					treasureListWindow.setVisible(!treasureListWindow.isVisible());   
				}
			});                                  

			//shows the controlPanel
			controlPanel.setVisible(true);           
			windows.add(messageLogWindow);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			Log.error(e);
		}      

	}    

	private void buildQuestIntroductionWindow() {
		questIntroductionWindow = new QuestIntroductionWindow(Sui.getTheme().getSystemFont());
		questIntroductionWindow.formDefaultComponents();
		SuiButton yesBtn = questIntroductionWindow.getYesButton();
		yesBtn.addActionListener(new SuiActionListener() {

			@Override
			public void actionPerformed(SuiActionEvent e) {
				acceptQuest();
				questIntroductionWindow.setVisible(false);
			}
		});

		SuiButton closeBtn = questIntroductionWindow.getCloseButton();
		SuiActionListener closeListener = questIntroductionWindow.getCloseListener();
		if (closeListener != null) closeBtn.removeActionListener(closeListener);

		closeListener = new SuiActionListener() {
			@Override
			public void actionPerformed(SuiActionEvent e) {
				rejectQuest();
				questIntroductionWindow.setVisible(false);
			}
		};
		closeBtn.addActionListener(closeListener);
		questIntroductionWindow.setCloseListener(closeListener);
		questIntroductionWindow.setVisible(false);
		windows.add(questIntroductionWindow);
	}

	private void buildQuestInformationWindow() {
		questInformationWindow = new ScrollCloseWindow(Sui.getTheme().getSystemFont(),
				ScrollWindow.getSCROLLWINDOW_WIDTH(),
				ScrollWindow.getSCROLLWINDOW_HEIGHT()/4);
		questInformationWindow.formDefaultComponents();

		SuiButton okBtn = questInformationWindow.getCloseButton();
		okBtn.setText("OK");

		SuiActionListener closeListener = questInformationWindow.getCloseListener();
		if (closeListener != null) okBtn.removeActionListener(closeListener);

		closeListener = new SuiActionListener() {
			@Override
			public void actionPerformed(SuiActionEvent e) {
				sendOkReply();
				questInformationWindow.setVisible(false);
			}
		};
		okBtn.addActionListener(closeListener);
		questInformationWindow.setCloseListener(closeListener);
		questInformationWindow.setVisible(false);
		windows.add(questInformationWindow);
	}

	private void acceptQuest() {
		THGClient.get().setSendQuery("Yes!", REPLY_ON);
	}

	private void rejectQuest() {
		THGClient.get().setSendQuery("No!", REPLY_OFF);
	}           

	private void sendOkReply() {
		THGClient.get().setSendQuery("Ok!", REPLY_ON);
	}

	public void buildQuizWindow() {
		quizWindow = new QuizWindow(Sui.getTheme().getSystemFont());
		quizWindow.formDefaultComponents();
		SuiTextField qaTextField = quizWindow.getAnswerArea().getTextField();
		final SuiTextArea qaTextArea = quizWindow.getTextArea();

		//add an action so the button will do something when we click it
		qaTextField.addActionListener(new SuiActionListener() {
			@Override
			public void actionPerformed(SuiActionEvent e) {
				Log.info("Answer for question.");
				SuiTextField qaTextField = quizWindow.getAnswerArea().getTextField();
				String text = qaTextField.getText();
				if (text != null && text.length()!= 0) {
					sendToServer(Commands.answerQuizCommand(getMyID(), THGClient.get().getPlaceId(), quizWindow.getTreasureID(), quizWindow.getQuestID(), text));
					quizWindow.setVisible(false);
				} else {
					qaTextArea.append("Give your answer!");
					qaTextField.grabFocus();
				}
			}
		});

		quizWindow.setVisible(false);
		windows.add(quizWindow);
	}

	public void initializeGame()  {           

		initializeMyDialogues();

		initializeMyTreasures();

		initializeMyQuests();                              

		lastTime = currentTime = System.nanoTime();  

		gameInitialized = true;                 
	}         

	private void initializeMyDialogues() {
		String[] dialogues = THGClient.get().getDialogues();
		if (dialogues != null) {
			SuiTextArea messageLogArea = controlPanel.getMessageLogArea();
			messageLogArea.setText("");
			messageLogArea.setLogTexts(dialogues);
			controlPanel.getMessageLogWindow().setVisible(true);          
		} else {
			controlPanel.getMessageLogWindow().setVisible(false);            
		}           
	}

	private void initializeMyTreasures(){      
		myTreasures = THGClient.get().getMyTreasures();

		if (!myTreasures.isEmpty()) {
			treasureListWindow.getTitleListForTreasure().clear();
			treasureListWindow.getIntroListForTreasure().clear();
			for (String[] myTreasure : myTreasures.values()) {
				treasureListWindow.getTitleListForTreasure().add(myTreasure[0].toUpperCase());
				int endIndex = myTreasure[5].length() > 50 ? 50 : myTreasure[5].length() - 1;
				if (endIndex < 0) endIndex = 0;
				treasureListWindow.getIntroListForTreasure().add(myTreasure[5].substring(0, endIndex));
			}
			treasureListWindow.getTitleSuiListForTreasure().setModel(new DefaultListModel(treasureListWindow.getTitleListForTreasure()));
			treasureListWindow.getIntroSuiListForTreasure().setModel(new DefaultListModel(treasureListWindow.getIntroListForTreasure()));
		}
	}    

	private void initializeMyQuests(){
		Map<Integer, Quest> myQuests = THGClient.get().getMyQuests();

		if (!myQuests.isEmpty()) {
			questListWindow.getGuideNames().clear();
			questListWindow.getQuestNames().clear();
			questListWindow.getQuestStates().clear();
			for (Quest myQuest : myQuests.values()) {
				questListWindow.getGuideNames().add(myQuest.getGuideName());
				questListWindow.getQuestNames().add(myQuest.getName());
				questListWindow.getQuestStates().add(myQuest.getState());
			}
			questListWindow.getGuideNameList().setModel(new DefaultListModel(questListWindow.getGuideNames()));
			questListWindow.getQuestNameList().setModel(new DefaultListModel(questListWindow.getQuestNames()));
			questListWindow.getQuestStateList().setModel(new DefaultListModel(questListWindow.getQuestStates()));
		}
	}        

	private void initializeGround(String ref) {        
		TiledMap thgMap = null;
		
		try {      
			thgMap = new TiledMap (ref,"");
		} catch (SlickException e) {
			Log.error(e);
		} 
		
		THGTerrain.get().setThgMap(thgMap);
		THGTerrain.get().setCollisionManger(new Collision(thgMap));
		
		int mapWidth =  thgMap.getWidth();
		int mapHeight = thgMap.getHeight();		

		pathfinder = new DJKPathFinder(mapWidth, mapHeight);   
		
		widthInTiles = container.getWidth() / GameDefault.TILEWIDTH;
		heightInTiles = container.getHeight() / GameDefault.TILEHEIGHT;
		topOffsetInTile = heightInTiles / 2;
		leftOffsetInTile = widthInTiles / 2;

		// force load of shot in init
		// new ShotSprite(0, 0, 0, 0);   		

		Log.info("PlayStage : Window Dimensions in Tiles: "+widthInTiles+"x"+heightInTiles + " number of tilesets size => " + thgMap.tileSets.size());
	}    

	@Override
	public void update(GameContainer container, StateBasedGame game, int timeDelta) throws SlickException  {        
		processOneMessage();       

		updateComponents(container, timeDelta);

		GameState state = THGClient.get().getGameState();

		if (!gameInitialized && state == GameState.PAUSED ) {
			initializeGame();     
			return;
		}   

		if (!players.containsKey(getMyID())) {
			// we are off screen and disabled
			return;
		}                    

		if (state != GameState.PLAYING) {
			return;
		}       

		//compute elapsed times
		currentTime = System.nanoTime();
		long elapsedTickTime = currentTime - lastTickTime;
		long elapsedInputTime = currentTime - lastInputTime;             

		if (elapsedInputTime > DEBOUNCEPERIOD) { 
			processFunctionKey();            
			lastInputTime = currentTime;               
		}

		//send message to talk.
		sendQuery();

		if (elapsedTickTime > GAMETURNTICK) {   
			movePlayer(elapsedTickTime);            
			lastTickTime = currentTime;            
		}
	}
	
	protected void stopPlaying() {     
		sendToServer(Commands.stopCommand(getMyID()));
	}

	protected void startPlaying() {
		try {
			harp1Sound.playAsSoundEffect(1.0f, HARPVOL, false);
		} catch (Exception e) {
			Log.error(e);
		}
		THGClient.get().setGameState(GameState.PLAYING);
	}    

	private void sendToServer(byte[] bytes) {
		THGClient.get().send(bytes);
	}

	private void updateComponents(GameContainer container, int timeDelta){
		controlPanel.update(container, timeDelta);
		statusPanel.update(container, timeDelta);
		for (SuiWindow window: windows) {
			window.update(container, timeDelta); 
		}               
	}

	private void processFunctionKey(){
		if (input.isKeyDown(Input.KEY_F1)) { // yes
			THGClient.get().setSendQuery("Yes.", REPLY_ON);
		} else if (input.isKeyDown(Input.KEY_F2)) { // 
			//THGClient.get().setSendQuery("No.", REPLY_OFF);
		}    
	}  

	private void sendQuery() {
		String message = THGClient.get().getSendQuery();
		int replyRequestFlag = THGClient.get().getReplyRequestFlag();
		THGSprite receiver = THGClient.get().getReceiver();

		if (message != null && receiver != null) {
			message = message + SuiTextArea.NEWLINE;
			if (receiver instanceof PlayerSprite) {
				sendToServer(Commands.talkCommand(receiver.getId(), receiver.getName(), message));
			} else if (receiver instanceof NpcSprite) {
				sendToServer(Commands.talkToNPCCommand(receiver.getId(), replyRequestFlag,  message));
			}            
			THGClient.get().setSendQuery(null, 0);
		} else if (receiver == null){
			THGClient.get().setSendQuery(null, 0);
		}
	}

	private void sendFrameCount() {
		sendToServer(Commands.frameCommand(getMyID(), frameCounter++));     
	}

	private void setAlternatePath(PlayerSprite playerSprite, int playerX, int playerY, Path path){
		int lastIndex = path.getSize()-1;
		int dx = path.getX(lastIndex);
		int dy = path.getY(lastIndex);
		Path alternatePath = findPath(playerX, playerY, dx, dy);
		playerSprite.setPath(alternatePath); 
		Log.info("The trial count of movement is over 10 : " + trialCount);         
	}

	private Direction findInputDirection(PlayerSprite playerSprite, Path path){
		int playerX = (int) playerSprite.getPosX();
		int playerY = (int) playerSprite.getPosY();     

		if (trialCount > 10) {
			setAlternatePath(playerSprite, playerX, playerY, path);                 
		}
		if (currentPathIndex != pathIndex) {
			currentPathIndex = pathIndex;     
			trialCount = 0;
		} else {
			++trialCount;
		}           

		int dx = path.getX(pathIndex);
		int dy = path.getY(pathIndex);

		if  (playerX == dx && playerY == dy ) {
			++pathIndex;
			return null;
		} else {
			return getDirection(playerX, playerY, dx, dy);
		}          
	}

	private void makeTurn(PlayerSprite playerSprite, Direction inputDirection){
		sendToServer(Commands.turnCommand(getMyID(), THGClient.get().getPlaceId(), inputDirection));
		playerSprite.turn(inputDirection);        
	}

	private void makeForward(PlayerSprite playerSprite, Direction inputDirection) {
		float[] newLoc = Commands.step(playerSprite.getPosX(), playerSprite.getPosY(), inputDirection);
		if (THGTerrain.get().isPortalAt(newLoc[0], newLoc[1])) {
			int id = THGTerrain.get().getTileId(newLoc[0], newLoc[1]);
			sendToServer(Commands.portalCommand(getMyID(), THGClient.get().getPlaceId(), portals.get(id).getName()));
		} else {
			sendToServer(Commands.moveForwardCommand(getMyID(), THGClient.get().getPlaceId(), playerSprite.getPosX(), playerSprite.getPosY(), inputDirection));
			playerSprite.walk(inputDirection);     
		}
	}
	
	private void makeMovement(PlayerSprite playerSprite, Direction inputDirection) {
		synchronized (this) {
			if (!playerSprite.isSameDirection(inputDirection)){
				makeTurn(playerSprite, inputDirection);
			} else {
				makeForward(playerSprite, inputDirection);
			}
		}              
	}

	private void movePlayer(long elapsedTickTime){
		Direction inputDirection = null;

		PlayerSprite playerSprite = players.get(getMyID());
		if (isMoveCompleted && (!playerSprite.isAnimating())) {           
			Path path = playerSprite.getPath();
			if (path !=null && pathIndex < path.getSize()) {
				inputDirection = findInputDirection(playerSprite, path);
				if (inputDirection != null) {
					makeMovement(playerSprite, inputDirection);
					isMoveCompleted = false;                    
					responseTime = currentTime;                         
				}
			}
		}

		sendFrameCount();      
	}    

	private void initializePath() {
		pathIndex = 0;        
		pathfinder.reset();
		players.get(getMyID()).setPath(null);  
	}

	private Path findPath(int playerX, int playerY, int dx, int dy){
		initializePath();
		return pathfinder.findPath(playerX , playerY, dx, dy, 
				GameDefault.PATH_SEARCH_DEPTH);
	}

	@Override
	public void mousePressed(int button, int x, int y) {        

		if (button == 0 && y <= ( container.getHeight() - controlPanel.getHeight())) {
			boolean windowHasFocus = false;

			for (SuiWindow window : windows) {
				if (window.hasFocus()) { 
					windowHasFocus = true;
					break;
				}
			}                                

			if (!windowHasFocus) clickLeftButton(x, y);

			for (SuiWindow window : windows) {
				window.setDraggable(false);
			}         

			for (SuiWindow window : windows) {
				if (window.getTitleBar().hasFocus()) { 
					window.setDraggable(true);
					break;
				}
			}                  
		}

		if (button == 1 && y <= ( container.getHeight() - controlPanel.getHeight()))
		{
			boolean windowHasFocus = false;
			for (SuiWindow window : windows) {
				if (window.hasFocus()) { 
					windowHasFocus = true;
					break;
				}
			}         

			if (!windowHasFocus) clickRightButton(x,y);
		}
	}    


	private float[] convertToTiledCoordinate(float playerX, float playerY, int x, int y) {        
		float coordinate[] = new float[2];

		coordinate[0] = ((playerX - container.getWidth() / (2 * GameDefault.TILEWIDTH) ) + x / GameDefault.TILEWIDTH);
		coordinate[1] = ((playerY - container.getHeight() / (2 * GameDefault.TILEHEIGHT) )+ y / GameDefault.TILEHEIGHT);       

		return coordinate;
	}

	private void attackMonster(int monsterId) {
		sendToServer(Commands.attackMonsterCommand( getMyID(), THGClient.get().getPlaceId(), monsterId));
	}

	private void findTreasure(int x, int y){
		TreasureSprite treasure = getTreasureAt(x,y);
		if (treasure != null && !treasure.isOpened()) {
			openTreasure(treasure); 
		}
	}

	private void clickLeftButton(int x, int y) {
		PlayerSprite mySprite = players.get(getMyID());
		if (mySprite == null) return;

		float myX = mySprite.getPosX();
		float myY = mySprite.getPosY();

		float coordinate[] = convertToTiledCoordinate(myX, myY, x, y);
		int tx = (int) coordinate[0];
		int ty = (int) coordinate[1];

		if (!THGTerrain.get().collides(tx, ty)) {
			MonsterSprite monster = getMonsterAt(tx,ty);         
			THGSprite talkableSprite = getTalkableSprite(tx,ty);

			if (monster != null && isWithinRange(tx, ty, 3)) {
				attackMonster(monster.getId());
			} else if (talkableSprite != null) {
				giveGreeating(talkableSprite);         
			} else {    
				clickedSpot.setClick(tx,ty);

				//find the best path                 
				Path path = findPath((int) myX, (int) myY, tx, ty);
				if (path != null) mySprite.setPath(path);
			}
		} else if (THGTerrain.get().isTreasureAt(tx, ty) && isWithinRange(tx, ty, 1) ) {
			findTreasure(tx, ty);
		}
	}

	private void giveGreeating(THGSprite talkableSprite) {
		THGSprite receiver = THGClient.get().getReceiver();
		if (receiver == null || !receiver.equals(talkableSprite)) {
			String message = "Hello " + talkableSprite.getName() + "!";   
			THGClient.get().setReceiver(talkableSprite);
			THGClient.get().setSendQuery(message, REPLY_ON);
		}
	}

	private void clickRightButton(int x, int y) {        
		System.out.println("Pressed Button 1!!!!!");       
	}       

	private void openTreasure(TreasureSprite treasure) {
		treasure.open();
		sendToServer(Commands.openTreasureCommand(getMyID(), THGClient.get().getPlaceId(), treasure.getId(), treasure.getTopicID()));
	}

	public Direction getDirection(int playerX, int playerY, int dx, int dy) {
		Direction direction = null;

		if (playerX == dx && playerY > dy ) {
			direction = Direction.NORTH;
		} else if (playerX == dx && playerY < dy ) {
			direction = Direction.SOUTH;
		} else if (playerX > dx && playerY == dy ) {
			direction = Direction.WEST;
		} else if (playerX < dx && playerY == dy ) {
			direction = Direction.EAST;
		}             
		if (direction == null) {
			return null;
		} else {    
			return direction;            
		}            
	}

	private boolean isMonsterAt(float i, float j) {
		for (MonsterSprite monsterSprite : monsters.values()) {
			if ((monsterSprite != null) && (monsterSprite.isAt(i, j))) {
				return true;
			}
		}
		return false;
	}

	private MonsterSprite getMonsterAt(float i, float j) {
		for (MonsterSprite monsterSprite : monsters.values()) {
			if ((monsterSprite != null) && (monsterSprite.isAt(i, j))) {
				return monsterSprite;
			}
		}
		return null;
	}

	private boolean isPlayerAt(float i, float j) {
		for (PlayerSprite playerSprite : players.values()) {
			if ((playerSprite != null) && !playerSprite.equals(players.get(getMyID())) && (playerSprite.isAt(i, j))) {
				return true;
			}
		}
		return false;
	}

	private TreasureSprite getTreasureAt(float x, float y) {
		if (THGTerrain.get().isTreasureAt(x, y)) {
			int id = THGTerrain.get().getTileId(x, y);
			return treasures.get(id);
		} else {
			return null;
		}

	}

	private PlayerSprite getPlayerAt(float i, float j) {
		for (PlayerSprite playerSprite : players.values()) {
			if ((playerSprite != null) && !playerSprite.equals(players.get(getMyID())) && (playerSprite.isAt(i, j))) {
				return playerSprite;
			}
		}
		return null;
	}

	private THGSprite getTalkableSprite(float i, float j) {
		NpcSprite npc = getNPCAt (i,j);
		if (npc != null) return (THGSprite) npc;

		PlayerSprite playerSprite = getPlayerAt(i,j);
		if (playerSprite != null) return (THGSprite) playerSprite;

		return null;            
	}

	private boolean isWithinRange(float posX, float posY, int distance) {
		PlayerSprite mySprite = players.get(getMyID());
		if (mySprite == null) return false;

		float myX = mySprite.getPosX();
		float myY = mySprite.getPosY();
		float startX = myX - distance;
		float endX = myX + distance;
		float startY = myY - distance;
		float endY = myY + distance;

		return ((posX >= startX) && (posX <= endX) &&  (posY >= startY) && (posY <= endY));
	}

	private boolean isWithinRange(THGSprite mainSprite, THGSprite sprite) {
		float mainX = mainSprite.getPosX();
		float mainY = mainSprite.getPosY();
		float startX = mainX - MAX_DISTANCE;
		float endX = mainX + MAX_DISTANCE;
		float startY = mainY - MAX_DISTANCE;
		float endY = mainY + MAX_DISTANCE;

		float posX = sprite.getPosX();
		float posY = sprite.getPosY();

		return ((posX >= startX) && (posX <= endX) &&  (posY >= startY) && (posY <= endY));
	}

	private THGSprite getTalkableSpriteAround() {
		PlayerSprite mySprite = players.get(getMyID());
		if (mySprite == null) return null;

		NpcSprite closestNpc = getClosestNpc(mySprite);

		PlayerSprite closestPlayer = getClosestPlayer(mySprite);

		return getMoreCloserTHOSprite(mySprite, closestNpc, closestPlayer);
	}

	private NpcSprite getClosestNpc(PlayerSprite mySprite) {

		NpcSprite closestNpc = null;

		// find npc
		for (NpcSprite npc : npcs.values()) {
			if (npc != null) {
				if (isWithinRange(mySprite, npc)) {
					closestNpc = (NpcSprite) getMoreCloserTHOSprite(mySprite, closestNpc, npc);                    
				}
			}
		}
		return closestNpc;
	}

	private PlayerSprite getClosestPlayer(PlayerSprite mySprite) {

		PlayerSprite closestPlayer = null;

		// find players
		for (PlayerSprite playerSprite : players.values()) {
			if (playerSprite != null && playerSprite != mySprite) {
				if (isWithinRange(mySprite, playerSprite)) {
					closestPlayer = (PlayerSprite) getMoreCloserTHOSprite(mySprite, closestPlayer, playerSprite);
				}
			}
		}
		return closestPlayer;
	}

	private THGSprite getMoreCloserTHOSprite(THGSprite mainSprite, THGSprite sprite1, THGSprite sprite2) {
		if (sprite1 == null && sprite2 == null) {
			return null;
		} else if (sprite2 == null) {
			return sprite1;
		} else if (sprite1 == null) {
			return sprite2;
		}

		float mainX = mainSprite.getPosX();
		float mainY = mainSprite.getPosY();

		float pos1X = sprite1.getPosX();
		float pos1Y = sprite1.getPosY();
		float pos2X = sprite2.getPosX();
		float pos2Y = sprite2.getPosY();

		float distance1 = Math.abs(mainX - pos1X) + Math.abs(mainY - pos1Y);
		float distance2 = Math.abs(mainX - pos2X) + Math.abs(mainY - pos2Y);

		if (distance1 > distance2) {
			return sprite2;
		} else {
			return sprite1;
		}
	}

	private NpcSprite getNPCAt(float i, float j) {
		for (NpcSprite npc : npcs.values()) {
			if ((npc != null) && (npc.isAt(i, j))) {
				return npc;
			}
		}
		return null;
	}    

	private void drawBoard(PlayerSprite mySprite, float playerX, float playerY, int startTilePosX, int startTilePosY, int startMapLayer, int endMapLayer){
		// caculate the offset of the player from the edge of the tile. As the player moves around this
		// varies and this tells us how far to offset the tile based rendering to give the smooth
		// motion of scrolling
		int playerTileOffsetX = (int) (((int) playerX - playerX) * GameDefault.TILEWIDTH);
		int playerTileOffsetY = (int) (((int) playerY - playerY) * GameDefault.TILEHEIGHT);             

		for (int mapLayer = startMapLayer; mapLayer < endMapLayer; ++mapLayer) {
			THGTerrain.get().render(playerTileOffsetX - (mySprite.getWidth() / 2),
					playerTileOffsetY - (mySprite.getHeight() / 2),
					startTilePosX,
					startTilePosY,
					widthInTiles+1, heightInTiles+1,mapLayer);                
		}        
	}

	private void drawTerrain(float playerX, float playerY, int startTilePosX, int startTilePosY, Graphics g)
	{		
		TiledMap thgMap = THGTerrain.get().getThgMap();
				
		int beginTerrainMapX = startTilePosX - 1;
		if ( beginTerrainMapX < 0) beginTerrainMapX = 0;
		int beginTerrainMapY = startTilePosY - 1;            
		if ( beginTerrainMapY < 0) beginTerrainMapY = 0;            

		int endTerrainMapX = (int) playerX + leftOffsetInTile + 1;
		if (endTerrainMapX >= thgMap.width) endTerrainMapX = thgMap.width;
		int endTerrainMapY = (int) playerY + topOffsetInTile + 1;
		if (endTerrainMapY >= thgMap.height) endTerrainMapY = thgMap.height;

		for (int x = beginTerrainMapX; x < endTerrainMapX; x++) {
			for (int y = beginTerrainMapY; y < endTerrainMapY; y++) {
				switch (THGTerrain.get().getTerrain(x, y)) {     
				case MONSTERTOMB:
					int xOrigin = x * GameDefault.TILEWIDTH;
					int yOrigin = y * GameDefault.TILEHEIGHT - (GameDefault.TILEHEIGHT / 3); 
					monsterTombTile.draw(xOrigin
							+ ((GameDefault.TILEWIDTH - monsterTombTile
									.getWidth()) / 2),
									yOrigin
									+ ((GameDefault.TILEHEIGHT - monsterTombTile
											.getHeight()) / 2));
					break;
				case PLAYERTOMB:
					xOrigin = x * GameDefault.TILEWIDTH;
					yOrigin = y * GameDefault.TILEHEIGHT - (GameDefault.TILEHEIGHT / 3);
					playerTombTile.draw(xOrigin
							+ ((GameDefault.TILEWIDTH - monsterTombTile
									.getWidth()) / 2),
									yOrigin
									+ ((GameDefault.TILEHEIGHT - monsterTombTile
											.getHeight()) / 2));
					break;
				case TREASURE:
					TreasureSprite treasureSprite = getTreasureAt(x,y);
					if (treasureSprite.isDone()) treasureSprite.close();
					treasureSprite.draw(g);
					break;
				}
			}
		}

         
		/*
		 * draw food
		for (FoodSprite food : foods.values()) {
			if (food != null) {
				food.draw(g);
			}
		}     
		 */
		
		// draw monsters          
		for (MonsterSprite monster : monsters.values()) {
			if (monster != null) {
				monster.draw(g);
			}
		}            

		// draw npc
		for (NpcSprite npc : npcs.values()) {
			if (npc != null) {
				npc.draw(g);
			}
		}

		// draw players
		for (PlayerSprite playerSprite : players.values()) {
			if (playerSprite != null) {
				playerSprite.draw(g);
			}
		}         
	}

	private void drawClickedSpot() {
		// draw spot clicked by left click mouse button.
		if (!clickedSpot.isDone()) {
			clickedSpot.draw();
		}     
	} 

	private void drawCursor(){
		if (hasMouseOverSprite()) {
			if (attackCursor.isEnabled()) attackCursor.draw(); 
			else if (talkableCursor.isEnabled()) talkableCursor.draw();
		}       
	}

	private void drawWindows(Graphics g) throws SlickException{
		//drawing windows
		boolean hasFocusedWindow = false;
		SuiWindow focusedWindow = null;
		SuiWindow previousWindow = null;

		for (SuiWindow window: windows) {
			if (window.isVisible() && !window.getTitleBar().hasFocus()) {
				if (previousWindow != null && !previousWindow.equals(controlPanel.getMessageLogWindow()) &&
						previousWindow.getX() <= window.getX() &&
						previousWindow.getX() + previousWindow.getWidth() >= window.getX() &&
						previousWindow.getY() <= window.getY() &&
						previousWindow.getY() + previousWindow.getHeight() >= window.getY() ) 
				{
					float posX = window.getX();
					float posY = previousWindow.getY() + previousWindow.getHeight() + 20;
					if (posY >= container.getHeight() - controlPanel.getHeight() - 20) {
						posY = previousWindow.getY() - 20;
						if (posY < 0) {
							boolean found = false;
							for (int i=20; i < container.getWidth(); i += 20) {
								for (int j=20; j < container.getHeight(); j += 20) {
									if (i != previousWindow.getX() || j != previousWindow.getY()) {
										posX = i;
										posY = j;
										found = true;
										break;
									} 
								}
								if (found) break;
							}
						}
					}
					window.setLocation(posX, posY);
				}

				window.render(container, g);
				previousWindow = window;               
			} else if (window.isVisible() && window.getTitleBar().hasFocus()) {
				hasFocusedWindow = true;
				focusedWindow = window;
			}
		}            

		if (hasFocusedWindow) {
			focusedWindow.render(container, g);
		} else if (previousWindow != null) {
			previousWindow.setFocusable(true);
		}                    
	}

	public boolean hasMouseOverSprite(){
		if (mouseOverSprite != null) mouseOverSprite.setMouseOver(false);

		int x = input.getMouseX();
		int y = input.getMouseY();

		PlayerSprite playerSprite = players.get(getMyID());
		if (playerSprite == null) return false;

		if (isInWindow(x, y)) return false;

		float playerX = playerSprite.getPosX();
		float playerY = playerSprite.getPosY();

		float coordinate[] = convertToTiledCoordinate(playerX, playerY, x, y);
		float tx = coordinate[0];
		float ty = coordinate[1];

		if (!THGTerrain.get().collides((int) tx, (int) ty) ) {
			mouseOverSprite = (THGSprite) getMonsterAt(tx,ty);

			if (mouseOverSprite != null) {                  
				mouseOverSprite.setMouseOver(true);
				attackCursor.setPosX(x);
				attackCursor.setPosY(y);   
				talkableCursor.setEnabled(false);                
				attackCursor.setEnabled(true);    
				return true;
			} else {
				mouseOverSprite = getTalkableSprite(tx,ty);                
				if (mouseOverSprite != null) {                           
					mouseOverSprite.setMouseOver(true);
					talkableCursor.setPosX(x);
					talkableCursor.setPosY(y);
					attackCursor.setEnabled(false);                
					talkableCursor.setEnabled(true);
					return true;
				}  else {                       
					return false;
				}
			}
		} else {            
			return false;
		}
	}

	private boolean isInWindow(float x, float y) {
		for (SuiWindow window: windows) {
			if (window.isVisible()) {
				float winX = window.getAbsoluteX();
				float winY = window.getAbsoluteY();
				int winWidth = window.getWidth();
				int winHeight = window.getHeight();

				return (x >= winX && x <= (winX + winWidth)) && (y >= winY && y <= (winY + winHeight));
			}
		}
		return false;
	}

	@Override    
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException
	{          
		PlayerSprite mySprite = players.get(getMyID());
		if (!gameInitialized || mySprite == null) { 
			return;      
		}      

		GameState state = THGClient.get().getGameState();

		if (state == GameState.PLAYING) {                        
			float playerX = (int) mySprite.getPosX();
			float playerY = (int) mySprite.getPosY();

			//start position in tile to draw map
			int startTilePosX = (int) playerX - leftOffsetInTile - 1;
			int startTilePosY = (int) playerY - topOffsetInTile - 1;  

			drawBoard(mySprite, playerX, playerY, startTilePosX, startTilePosY, 0, 3);

			// draw entities relative to the player that must appear in the centre of the screen
			float tx = (container.getWidth()/ 2) - (playerX * GameDefault.TILEWIDTH);
			float ty = (container.getHeight()/ 2) - (playerY * GameDefault.TILEHEIGHT);   
			g.translate(tx, ty);

			drawTerrain( playerX, playerY, startTilePosX, startTilePosY, g); 
			drawClickedSpot();            

			g.resetTransform();

			drawBoard(mySprite, playerX, playerY, startTilePosX, startTilePosY, 3, 5);            

			//drawing windows
			drawWindows(g);

			statusPanel.render(container, g);
			controlPanel.render(container, g);

			drawCursor();
		}

		currentTime = System.nanoTime();
		while ((currentTime - lastTime) / NANOMILLIS < (1000 / FRAMESPERSECOND)) {
			try {
				Thread.sleep((1000 / FRAMESPERSECOND) - ((currentTime - lastTime) / NANOMILLIS));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentTime = System.nanoTime();
		} 
		lastTime = currentTime;
	}

	public void processOneMessage() {

		ConcurrentLinkedQueue<ByteBuffer> inputQueue = THGClient.get().getInputQueue();

		if (inputQueue.isEmpty()) {
			return;
		}
		ByteBuffer buff = inputQueue.remove();       

		// we get all the packets for one turn at the same time
		while (buff.remaining() > 0) {
			Commands.parseCommandBuffer(buff,new CommandListener() {            
				@Override                
				public void commandHp(int id, int hp){
					PlayerSprite sprite = (PlayerSprite) players.get(id);
					sprite.setHP(hp);    
				}   

				@Override                                
				public void commandAddFood(int id, String name, float x, float y, int attractionPoint, String imgRef) {
					THGTerrain.get().setTileId(x, y, id);	
					if (!foods.containsKey(id)) {
						foods.put(id, new FoodSprite(id, name, x, y, attractionPoint, imgRef));
					}					
				}                                

				@Override
				public void commandClearDialogueHistory(int id){

				}

				@Override
				public void commandClearMsgLog(int id){
					controlPanel.getMessageLogArea().clearTexts();                       
				}

				@Override                
				public void commandBlock(int id, float x, float y){
					//                        terrainMap[(int)x][(int)y] = Terrain.BLOCK;                                                
					if (id == getMyID()) {
						moveCompleted();
					}                        
				}                

				@Override                
				public void commandRequestLevel(int id){

				}

				@Override
				public void commandRequestPassAway(int id){

				}

				@Override
				public void commandPassAway(int id, int gameLevel, int hp, int maxHp, int mp, 
						int maxMp, int exp, int maxExp, int money){    

					SuiLabel levelLabel = statusPanel.getLevelLabel();  
					SuiLabel moneyLabel = statusPanel.getMoneyLabel();           
					RpgBar hpBar = statusPanel.getHpBar();
					RpgBar expBar = statusPanel.getExpBar();             

					PlayerSprite sprite = (PlayerSprite) players.get(getMyID());
					sprite.setSpriteState(SpriteState.REST);                        
					sprite.setGameLevel(gameLevel);
					levelLabel.setText("Level: "+gameLevel);
					levelLabel.pack();                        

					sprite.setHP(hp);     
					sprite.setMaxHp(maxHp);                             
					hpBar.setPoints(hp, maxHp);
					sprite.setMP(mp);                                
					sprite.setMaxMp(maxMp);                             
					//                        mpBar.setPoints(mp, maxMp);
					sprite.setExp(exp); 
					sprite.setMaxExp(maxExp);                         
					expBar.setPoints(exp, maxExp);                        
					sprite.setMoney(money);  
					moneyLabel.setText("Gold: "+money);
					moneyLabel.pack();

					ScrollCloseWindow noticeWindow = getNoticeWindow();                        
					noticeWindow.setTitle(sprite.getName().toUpperCase());         
					SuiTextArea textArea = noticeWindow.getTextArea();
					textArea.setText("");                        
					textArea.append("You are dead and lost some money if you have!");

					noticeWindow.setVisible(true);      
				}

				@Override
				public void commandLevel(int id, int gameLevel, int hp, int maxHp, int mp,
						int maxMp, int exp, int maxExp, int power) {
					PlayerSprite sprite = (PlayerSprite) players.get(getMyID());
					sprite.setSpriteState(SpriteState.REST);

					ScrollCloseWindow noticeWindow = getNoticeWindow();
					noticeWindow.setTitle(sprite.getName().toUpperCase());                   
					SuiTextArea textArea = noticeWindow.getTextArea();
					textArea.setText("");                        
					if (sprite.getGameLevel() < gameLevel) {
						textArea.append("Your game level is up!!!");
					} else {
						textArea.append("Your game level is down!!!");
					}

					noticeWindow.setVisible(true);      

					SuiLabel levelLabel = statusPanel.getLevelLabel();    
					RpgBar hpBar = statusPanel.getHpBar();
					RpgBar expBar = statusPanel.getExpBar();  

					sprite.setGameLevel(gameLevel);
					levelLabel.setText("Level: "+gameLevel);
					levelLabel.pack();                        

					sprite.setHP(hp);     
					sprite.setMaxHp(maxHp);                             
					hpBar.setPoints(hp, maxHp);
					sprite.setMP(mp);                                
					sprite.setMaxMp(maxMp);                             
					//                        mpBar.setPoints(mp, maxMp);
					sprite.setExp(exp); 
					sprite.setMaxExp(maxExp);                         
					expBar.setPoints(exp, maxExp);                        
					sprite.setPower(power);  
				}

				@Override                   
				public void commandPayment(int id, int treasureId, String name, 
						int hp, int mp, int exp, int money,
						String armor, String tool, String weapon) {
					TreasureSprite treasure = treasures.get(Integer.valueOf(treasureId));
					treasure.setTaken(true);

					PlayerSprite sprite = (PlayerSprite) players.get(getMyID());
					ScrollCloseWindow noticeWindow = getNoticeWindow();                        
					noticeWindow.setTitle(name.toUpperCase());                        
					SuiTextArea textArea = noticeWindow.getTextArea();
					textArea.setText("");
					/*
                        if (hp > 0) {
                            textArea.append("You have got " + String.valueOf(hp) + " HP." + SuiTextArea.NEWLINE );
                            sprite.addHP(hp);    
                            statusPanel.getHpBar().setPoints(sprite.getHP(), sprite.getMaxHp() );
                        } else if (hp < 0) {
                            textArea.append("You have lost " + String.valueOf(-hp) + " HP." + SuiTextArea.NEWLINE);                            
                            sprite.addHP(hp);      
                            statusPanel.getHpBar().setPoints(sprite.getHP(), sprite.getMaxHp() );                            
                            if (sprite.getSpriteState().equals(SpriteState.DEAD)) {
                                passAway();
                            }                                 
                        }

                        if (mp > 0) {
                            textArea.append("You have got " + new String().valueOf(mp) + " MP." + SuiTextArea.NEWLINE);                            
                            sprite.addMP(mp);     
                            mpBar.setPoints(sprite.getMP(), sprite.getMaxMp() ); 
                        } else if (mp < 0) {
                            textArea.append("You have lost " + new String().valueOf(-mp) + " MP." + SuiTextArea.NEWLINE);                             
                            sprite.addMP(mp);        
                            mpBar.setPoints(sprite.getMP(), sprite.getMaxMp() );  
                        }    
					 */
					if (exp > 0) {
						textArea.append("You have got " + String.valueOf(exp) + " experience." + SuiTextArea.NEWLINE);                            
						sprite.addExp(exp);   
						statusPanel.getExpBar().setPoints(sprite.getExp(), sprite.getMaxExp() );
						if (sprite.getSpriteState().equals(SpriteState.LEVELUP)) {
							changeLevel();
						}
					} else if (exp < 0) {
						textArea.append("You have lost " + String.valueOf(-exp) + " experience." + SuiTextArea.NEWLINE);                              
						sprite.addExp(exp);   
						statusPanel.getExpBar().setPoints(sprite.getExp(), sprite.getMaxExp() );                              
						if (sprite.getSpriteState().equals(SpriteState.LEVELDOWN)) {
							changeLevel();
						}                            
					}                        
					if (money > 0) {
						textArea.append("You have got money " + String.valueOf(money) + " Golds." + SuiTextArea.NEWLINE);
						sprite.addMoney(money);  
						statusPanel.getMoneyLabel().setText("Gold: "+sprite.getMoney());
					} else if (money < 0) {
						textArea.append("You have lost money" + String.valueOf(-money) + " Golds." + SuiTextArea.NEWLINE);
						sprite.addMoney(money);  
						statusPanel.getMoneyLabel().setText("Gold: "+sprite.getMoney());                            
					}

					if (armor != null && !armor.isEmpty()) {
						textArea.append("You have the armor ( " + armor.toUpperCase() + " ) in your inventory." + SuiTextArea.NEWLINE);
						/*
                            sprite.addMoney(money);
                            statusPanel.getMoneyLabel().setText("Gold: "+sprite.getMoney());
						 */
					}

					if (tool != null && !tool.isEmpty()) {
						textArea.append("You have the tool ( " + tool.toUpperCase() + " ) in your inventory." + SuiTextArea.NEWLINE);
					}

					if (weapon != null && !weapon.isEmpty()) {
						textArea.append("You have the weapon ( " + weapon.toUpperCase() + " ) in your inventory." + SuiTextArea.NEWLINE);
					}

					noticeWindow.setVisible(true);    
				}                    

				@Override                   
				public void commandAnswerQuiz(int id, int placeId, int treasureId, int topicId, String answer) {

				}                

				@Override                    
				public void commandQuestIntroduction(int id, int guideId, String guideNickName, int questID, String questName, String introduction) {
					//Create my new quest
					Quest quest = new Quest(questID, questName);
					quest.setGuideId(guideId);
					quest.setGuideName(guideNickName);
					quest.setIntroduction(introduction);
					quest.setState(Quest.QuestState.Suggest);
					THGClient.get().getMyQuests().put(new Integer(questID), quest);

					controlPanel.getMessageLogWindow().setVisible(false);

					//Pop up a window to make a decision to do
					questIntroductionWindow.setTreasureID(questID);
					SuiTextArea textArea = questIntroductionWindow.getTextArea();
					textArea.setText("");
					questIntroductionWindow.setTitle(questName.toUpperCase() + " - INTRODUCTION");
					textArea.append(introduction + SuiTextArea.NEWLINE + SuiTextArea.NEWLINE);
					textArea.append("Do you want to learn about this topic?");
					questIntroductionWindow.setVisible(true);

					Map<Integer, Quest> myQuests = THGClient.get().getMyQuests();
					questListWindow.getGuideNames().clear();
					questListWindow.getQuestNames().clear();
					questListWindow.getQuestStates().clear();
					for (Quest myQuest : myQuests.values()) {
						questListWindow.getGuideNames().add(myQuest.getGuideName());
						questListWindow.getQuestNames().add(myQuest.getName());
						questListWindow.getQuestStates().add(myQuest.getState());
					}
					questListWindow.getGuideNameList().setModel(new DefaultListModel(questListWindow.getGuideNames()));
					questListWindow.getQuestNameList().setModel(new DefaultListModel(questListWindow.getQuestNames()));
					questListWindow.getQuestStateList().setModel(new DefaultListModel(questListWindow.getQuestStates()));
				}

				@Override
				public void commandQuestQuestion(int id, int guideId, String guideNickName, int questID, String questName, String question) {
					Quest quest = THGClient.get().getMyQuests().get(questID);
					quest.setQuestion(question);

					controlPanel.getMessageLogWindow().setVisible(false);

					//If you have the question, it means you accepted the quest.
					// So, we change its status from "Suggest" to "Accept" here,
					// not at commandChangeQuestStatus.
					quest.setState(Quest.QuestState.Accept);

					questInformationWindow.setHeight(ScrollWindow.getSCROLLWINDOW_HEIGHT()/4);
					SuiTextArea textArea = questInformationWindow.getTextArea();
					textArea.setText("");
					questInformationWindow.setTitle(questName.toUpperCase() + " - QUESTION");
					textArea.append( question );
					questInformationWindow.setVisible(true);

					Map<Integer, Quest> myQuests = THGClient.get().getMyQuests();
					questListWindow.getGuideNames().clear();
					questListWindow.getQuestNames().clear();
					questListWindow.getQuestStates().clear();
					for (Quest myQuest : myQuests.values()) {
						questListWindow.getGuideNames().add(myQuest.getGuideName());
						questListWindow.getQuestNames().add(myQuest.getName());
						questListWindow.getQuestStates().add(myQuest.getState());
					}
					questListWindow.getGuideNameList().setModel(new DefaultListModel(questListWindow.getGuideNames()));
					questListWindow.getQuestNameList().setModel(new DefaultListModel(questListWindow.getQuestNames()));
					questListWindow.getQuestStateList().setModel(new DefaultListModel(questListWindow.getQuestStates()));
				}

				@Override
				//For help and clue
				public void commandQuestInformation(int id, int guideId, String guideNickName, int questID, String questName, String information, int iType) {
					Quest quest = THGClient.get().getMyQuests().get(questID);
					quest.setGuideId(questID);
					quest.getGuideName();

					controlPanel.getMessageLogWindow().setVisible(false);

					questInformationWindow.setHeight(ScrollWindow.getSCROLLWINDOW_HEIGHT()/4);
					SuiTextArea textArea = questInformationWindow.getTextArea();
					textArea.setText("");
					if (Quest.getInfomationType(iType).equals(Quest.InformationType.Help)) {
						questInformationWindow.setTitle(questName.toUpperCase() + " - " + guideNickName.toUpperCase() );
					} else {
						questInformationWindow.setTitle(questName.toUpperCase() + " - CLUE" );
					}
					textArea.append(information);
					questInformationWindow.setVisible(true);

					Map<Integer, Quest> myQuests = THGClient.get().getMyQuests();
					questListWindow.getGuideNames().clear();
					questListWindow.getQuestNames().clear();
					questListWindow.getQuestStates().clear();
					for (Quest myQuest : myQuests.values()) {
						questListWindow.getGuideNames().add(myQuest.getGuideName());
						questListWindow.getQuestNames().add(myQuest.getName());
						questListWindow.getQuestStates().add(myQuest.getState());
					}
					questListWindow.getGuideNameList().setModel(new DefaultListModel(questListWindow.getGuideNames()));
					questListWindow.getQuestNameList().setModel(new DefaultListModel(questListWindow.getQuestNames()));
					questListWindow.getQuestStateList().setModel(new DefaultListModel(questListWindow.getQuestStates()));
				}

				@Override            
				public void commandChangeQuestStatus(int id, int questID, int status) {
					Quest.QuestState qState = Quest.getState(status);
					if (qState.equals(Quest.QuestState.Reject)) {
						THGClient.get().removeMyQuest(questID);
					} else {
						Quest quest = THGClient.get().getMyQuests().get(questID);
						quest.setState(status);
					}
					Map<Integer, Quest> myQuests = THGClient.get().getMyQuests();
					questListWindow.getGuideNames().clear();
					questListWindow.getQuestNames().clear();
					questListWindow.getQuestStates().clear();
					for (Quest myQuest : myQuests.values()) {
						questListWindow.getGuideNames().add(myQuest.getGuideName());
						questListWindow.getQuestNames().add(myQuest.getName());
						questListWindow.getQuestStates().add(myQuest.getState());
					}
					questListWindow.getGuideNameList().setModel(new DefaultListModel(questListWindow.getGuideNames()));
					questListWindow.getQuestNameList().setModel(new DefaultListModel(questListWindow.getQuestNames()));
					questListWindow.getQuestStateList().setModel(new DefaultListModel(questListWindow.getQuestStates()));
				}

				@Override                    
				/** The QuestID is the TopicID. */ 
				public void commandGiveQuestion( int id, int treasureId, int questID){
					Quest quest = THGClient.get().getMyQuest(questID);
					quest.setState(Quest.QuestState.Question);

					quizWindow.setTreasureID(treasureId);
					quizWindow.setQuestID(questID);
					quizWindow.setSubject(quest.getName());
					quizWindow.setQuestion(quest.getQuestion());
					quizWindow.setQuestionArea();
					quizWindow.setVisible(true);                       
				}

				@Override
				public void commandGiveMark( int id, int questID, int mark){
					Quest quest = THGClient.get().getMyQuest(questID);
					ScrollCloseWindow noticeWindow = getNoticeWindow();
					noticeWindow.setTitle(quest.getName());
					SuiTextArea textArea = noticeWindow.getTextArea();
					textArea.setText("");
					textArea.append("Your marks for the question are " + mark + ".");
					noticeWindow.setVisible(true);
				}

				@Override                
				public void commandAttackMonster(int id, int placeId, int monsterId){

				}

				@Override
				//The echo in talking with ncp is displayed through this.
				public void commandTalk(int id, String name, String reply){
					if (reply.contains("Bye") || reply.contains("No") ) {
						THGClient.get().setReceiver(null);
					} else if (reply.contains("Hello")) {
						THGSprite receiver = THGClient.get().getReceiver();
						THGSprite playerSprite = players.get(id);
						if (receiver == null || !receiver.equals(playerSprite)) {
							THGClient.get().setReceiver(playerSprite);
						}
						controlPanel.getMessageLogWindow().setVisible(true);
					}

					controlPanel.getMessageLogArea().appendLog( reply );
				}

				@Override
				public void commandTalktoNPC(int npcId, int replyFlag, String reply){
					if (reply.contains("Bye")) {
						THGClient.get().setReceiver(null);
					} else if (reply.contains("Hello")) {
						THGSprite receiver = THGClient.get().getReceiver();
						if (receiver == null || !receiver.equals(npcs.get(npcId))) {
							THGClient.get().setReceiver(npcs.get(npcId));
						}             

						controlPanel.getMessageLogArea().appendLog( reply );
						controlPanel.getMessageLogWindow().setVisible(true); 
					} else {
						controlPanel.getMessageLogArea().appendLog( reply );
					}
				}                                                  

				@Override
				public void commandKill(int id) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void commandPortal(int id, int placeId, String portalName) {
					
				}
				
				@Override				
		        public void commandAddPortal(int id, int placeId, String portalName, float x, float y) {
					THGTerrain.get().setTileId(x, y, id);
					if (!portals.containsKey(id)) {
						portals.put(id, new Portal(id, portalName, x, y));
					}
		        }				
				
				@Override
				/** Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 */				
				public void commandMoveForward(int id, int placeId, float tx, float ty, Direction direction) {
					if (id % 1000 == 0) {
						PlayerSprite playerSprite = players.get(id);   

						if (getMyID() != id && playerSprite != null) {
							playerSprite.walk(direction);
						} else if (getMyID() == id) {        				
							moveCompleted();
						}
					} else if (id % 1000 == 2) {
						NpcSprite npcSprite = npcs.get(id);
						if (npcSprite != null) {
							npcSprite.walk(tx, ty, direction);
						}
					} else if (id % 1000 == 3) {
						MonsterSprite monsterSprite =  monsters.get(id);
						if (monsterSprite != null) {
							monsterSprite.walk(tx, ty, direction);	
						}
					}
				}
				
				/** Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 */				
				@Override                    
				public void commandTurn(int id, int placeId, Direction direction) {
					if (id % 1000 == 0) {
						PlayerSprite playerSprite = players.get(id);                          
						if (id != getMyID() && playerSprite != null) {
							playerSprite.turn(direction);
						} else if (id == getMyID() && playerSprite != null) {			
							synchronized (this) {
								isMoveCompleted = true;
							}		
						}
					} else if (id % 1000 == 2) {
						NpcSprite npcSprite = npcs.get(id);
						if (npcSprite != null) {
							npcSprite.turn(direction); 
						}
					} else if (id % 1000 == 3) {
						MonsterSprite monsterSprite =  monsters.get(id);
						if (monsterSprite != null) {
							monsterSprite.turn(direction);
						}
					}										
				}
				

				@Override           
				public void commandScore(int id, int score) {
					if (id == getMyID()) {
						PlayerSprite sprite = THGClient.get().getSprite();
						sprite.setExp(score);
					}
				}

				@Override                       
				public void commandInitializePlayer(int id,
						int level, int hp, int maxHp, int mp, int maxMp, 
						int myExp, int maxExp, int money,
						String[] dialogues, int outfitCode ){                        
				}

				@Override                    
				public void commandEnterGameBoard(int id, int placeId, String mapFileRef) {
					players.clear();
					npcs.clear();
					monsters.clear();
					treasures.clear();    
					foods.clear();
					portals.clear();
					THGClient.get().setReceiver(null);
					initializeGround(mapFileRef);             
					THGClient.get().setPlaceId(placeId);
					THGClient.get().setPlaceMapRef(mapFileRef);
				}   

				@Override
				public void commandOpenTreasure(int id, int placeId, int treasureId, int topicId){

				}

				@Override                    
				public void commandJoinGroup(int id) {

				}

				@Override                    
				public void commandLeaveGameBoard(int id, String placeName) {
					if (id != getMyID()) {
						removePlayer(id);
					} else {
						leaveGameBoard(placeName);
					}
				}                    
				
				@Override                    
				public void commandRemovePlayer(int id) {
					if (id != getMyID()) {
						removePlayer(id);
					}
				}                 				
						
				@Override                                       
				public void commandStart(int id) {
					if (id == getMyID()) startPlaying();
				}

				@Override                    
				public void commandStop(int id) {
					if ((id == getMyID()) || (id == -1)) {
						THGClient.get().setGameState(GameState.STAGELOADING);
						try {
							game.getState(ID).leave(container, game);  
							Log.info("stoping playing!");
						} catch (SlickException e) {            
							Log.error("Got an error while stoping playing!", e);
						}                                    
						game.enterState(THGkeeper.ID,  new EmptyTransition(), new EmptyTransition());
					}
				}

				@Override                    
				public void commandAddPlayer(int id, String name, int level, int outfitCode,
						float x, float y, int facing)
				{
					System.out.println("Add player id ==> " + id + " " + x  + "," + y);                  
					if (id == getMyID()){       
						PlayerSprite sprite = THGClient.get().getSprite();

						//Initialize the values of status panel 
						statusPanel.setLevelText(sprite.getGameLevel());
						statusPanel.setMoneyText(sprite.getMoney());
						statusPanel.setHpBarPoints(sprite.getHP(), sprite.getMaxHp());
						statusPanel.setExpBarPoints(sprite.getExp(), sprite.getMaxExp());

						sprite.setID(id);
						sprite.setName(name);
						sprite.setGameLevel(level);
						sprite.setXY(x, y);                            
						sprite.setFacing(Direction.values()[facing]);       
						players.put(id, sprite);
						initializePath();                                                        
						synchronized (this) {
							isMoveCompleted = true;
						}
					} else {
						PlayerSprite playerSprite =  new PlayerSprite(outfitCode, (int) x, (int) y,
								Direction.values()[facing]);
						playerSprite.setID(id);
						playerSprite.setName(name);
						playerSprite.setGameLevel(level);   
						players.put(id, playerSprite);                            
					}
				}

				@Override
				public void commandAddNPC(int id, String name, int level, int hp, int mp,
						String imgRef, float x, float y, int facing)
				{
					Log.info("Add Human NPC id ==> " + id + " " + x  + "," + y);   
					if (!npcs.containsKey(id)) {   
						NpcSprite npc = new NpcSprite(imgRef, (int) x, (int) y, Direction.values()[facing]);
						npc.setID(id);
						npc.setName(name);       
						npc.setGameLevel(level);
						npc.setHP(hp);      
						npc.setMP(mp); 
						npcs.put(id, npc);                           
					}
				}

				@Override
				public void commandAddTreasure(int id, int questID, float x, float y) {
					THGTerrain.get().setTileId(x, y, id);		
					THGTerrain.get().setCollide(x, y);
					if (!treasures.containsKey(id)) {
						treasures.put(id, new TreasureSprite(id, questID, (int) x, (int) y));
					}
				}   

				@Override                    
				public void commandFrame(int id, int frameCount) {
					// TODO Auto-generated method stub
				}

				@Override                    
				public void commandSetID(int id) {

				}

				@Override                    
				public void commandAddMonster(int id, String name, float x, float y, 
						int gameLevel, int hp, int mp, int power,
						Direction direction, String imgRef) {
					System.out.println("Add monster id=" + id + " " + x   + "," + y);
					MonsterSprite monster =  new MonsterSprite(name, x, y, direction, imgRef);
					monster.setGameLevel(gameLevel);
					monster.setHP(hp);
					monster.setMP(mp);
					monster.setPower(power);                        
					monster.setID(id);
					monsters.put(id, monster);                         
				}

				@Override                    
				public void commandRemoveFood(int id, float x, float y) {
					THGTerrain.get().setTileId(x, y, 0);
					foods.remove(id);
				}

				@Override                    
				public void commandAddMonsterTomb(int id, float x, float y) {
					THGTerrain.get().setTileId(x, y, id);
					try {
						churchbellSound.playAsSoundEffect(1.0f,THUDVOL,false);
					} catch (Exception e) {
						Log.error(e);
					}
				}

				@Override                    
				public void commandRemoveMonster(int id) {
					monsters.remove(id);
				}

				@Override                    
				public void commandRemoveMonsterTomb(int id, float x, float y) {
					THGTerrain.get().setTileId(x, y, 0);
				}

				@Override                    
				public void commandAddPlayerTomb(int id, float x, float y) {
					THGTerrain.get().setTileId(x, y, id);
					try {
						churchbellSound.playAsSoundEffect(1.0f,THUDVOL,false);
					} catch (Exception e) {
						Log.error(e);
					}
				}

				@Override                    
				public void commandRemovePlayerTomb(int id, float x, float y)
				{
					THGTerrain.get().setTileId(x, y, 0);
				}

				@Override                    
				public void commandLogin(String userName, String password)
				{
					// TODO Auto-generated method stub
				}

			});
		}
	}

	private void moveCompleted() {  
		PlayerSprite mySprite = players.get(getMyID());
		Path path = mySprite.getPath();
		if (path != null) {
			++pathIndex;
			if (pathIndex >= path.getSize()) {
				initializePath();
			}   
		}
		synchronized (this) {
			isMoveCompleted = true;
		}

		Log.info("Response time => " + (currentTime - responseTime) * 0.000000001);
	}

	private void passAway() {
		THGClient.get().send(Commands.requestPassAwayCommand(getMyID()));
	}

	private void changeLevel() {
		THGClient.get().send(Commands.requestChangeLevelCommand(getMyID()));
	}
	
	private void removePlayer(int id) {
		players.remove(id); 
	}
	
	private void leaveGameBoard(String placeName) {
		THGClient.get().send(Commands.leaveGameBoardCommand(getMyID(), placeName));
	}

	private NoticeWindow getQuestCompletedNoticeWindow() {    
		NoticeWindow questCompletedNoticeWindow = null;
		if (questCompletedNoticeWindows.isEmpty()) {
			questCompletedNoticeWindow = createQuestCompletedNoticeWindow();  
			questCompletedNoticeWindows.add(questCompletedNoticeWindow); 
			windows.add(questCompletedNoticeWindow);                             
		} else {
			for (NoticeWindow nWindows : questCompletedNoticeWindows) {
				if (!nWindows.isVisible()) questCompletedNoticeWindow = nWindows;
			}
			if (questCompletedNoticeWindow == null) {
				questCompletedNoticeWindow = createQuestCompletedNoticeWindow();  
				questCompletedNoticeWindows.add(questCompletedNoticeWindow); 
				windows.add(questCompletedNoticeWindow);                                    
			}
		}    
		return questCompletedNoticeWindow;
	}                    

	private NoticeWindow createQuestCompletedNoticeWindow() {
		final NoticeWindow questCompletedNoticeWindow = new NoticeWindow(Sui.getTheme().getSystemFont());
		questCompletedNoticeWindow.formDefaultComponents();                         
		return questCompletedNoticeWindow;
	}

	private NoticeWindow getNoticeWindow() {
		NoticeWindow noticeWindow = null;
		if (noticeWindows.isEmpty()) {
			noticeWindow = new NoticeWindow(Sui.getTheme().getSystemFont());
			noticeWindows.add(noticeWindow); 
			windows.add(noticeWindow);                             
		} else {
			for (NoticeWindow nWindows : noticeWindows) {
				if (!nWindows.isVisible()) noticeWindow = nWindows;
			}
			if (noticeWindow == null) {
				noticeWindow = new NoticeWindow(Sui.getTheme().getSystemFont());
				noticeWindows.add(noticeWindow); 
				windows.add(noticeWindow);                                    
			}
		}    
		return noticeWindow;
	}      
}
