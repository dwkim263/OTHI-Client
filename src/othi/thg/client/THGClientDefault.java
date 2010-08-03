package othi.thg.client;

import othi.thg.client.gui.ControlPanel;

/**
 * defining default value of game
 * @author Dong Won Kim
 */
public class THGClientDefault {

	private static final String SCREEN_SIZE = "800x600";

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;        

	static {
		String[] dim = SCREEN_SIZE.split("x");
		SCREEN_WIDTH = Integer.parseInt(dim[0]);                
		SCREEN_HEIGHT = Integer.parseInt(dim[1]); 
	}       

	public static final int TILEWIDTH = 32;
	public static final int TILEHEIGHT = 32;

	public static final float CONTROL_PRECISION = 0.1f;

	public static final int PATH_SEARCH_DEPTH = 50;

	//Strings of SetBinding
	public static final String TOPIC_LIST = "TopicList.";
	public static final String BOARD_LIST = "BoardList.";
	public static final String COMPLETED_QUEST = "CompetedQuest.";    
	public static final String NPCDIALOGUE = "NpcDialogue.";
	public static final String STATION = "Station.";
	public static final String FOOD = "Food.";
	public static final String GAMEBOARD = "GameBoard.";
	public static final String GAMEMONSTER = "GameMonster.";    
	public static final String HELP = "Help.";
	public static final String HELP_LIST = "Help.";
	public static final String HUMAN_LIST = "HumanList.";
	public static final String GUIDE_NPC = "GuideNpc.";
	public static final String ITEM_DOCUMENT = "ItemDocument.";
	public static final String ITEM_FOOD = "ItemFood.";    
	public static final String ITEM_HELP = "ItemHelp.";    
	public static final String ITEM_QUIZ = "ItemQuiz.";    
	
	//public static final String ITEM_TREASURE = "ItemTreasure.";
	public static final String KNOWNNPC = "KnownNPC.";
	public static final String LEVEL = "Level.";
	public static final String MASTER_CONTROL = "MasterControl.";
	public static final String MEDIATOR_LIST = "MediatorList.";
	public static final String MONSTER = "Monster.";
	public static final String MONSTER_CONTROL_TASK = "MonsterControlTask.";
	public static final String MONSTER_TASK_LIST = "MonsterTaskList.";

	public static final String MYDIALOGUE = "MyDialogue.";
	public static final String MYDOCUMENT = "MyDocument.";
	public static final String MYQUEST = "MyQuest.";    
	public static final String MYQUIZ = "MyQuiz.";    
	public static final String MYTREASURE = "MyTreasure.";

	public static final String NPC = "Npc.";
	public static final String NPC_CONTROL_TASK = "NPCControlTask.";
	public static final String NPC_TASK_LIST = "NPCTaskList.";
	public static final String PLACE = "Place.";

	public static final String PLAYER_ARMOR = "PlayerArmor.";
	public static final String PLAYER_COMPETENCE = "PlayerCompetence.";
	public static final String PLAYER_INVENTORY = "PlayerInventory.";
	public static final String PLAYER_LOGBOOK = "PlayerLogbook.";
	public static final String PLAYER_LISTENER = "PlayerListener.";
	public static final String PLAYER_WEAPON = "PlayerWeapon.";

	public static final String PORTAL = "Portal.";

	public static final String QUEST_LIST = "Quest.";

	public static final String TERRAIN_MAP = "TerrainMap.";
	public static final String THOWORLD = "THOWorld.";
	public static final String TREASURE = "Treasure.";
	public static final String USERPREFIX = "Player.";        

	public static enum GameState {
		GAMELOADING, INTRODUCING, LOGIN, LOGINING, LOGINED,LOGINFAILED,STAGELOADING, LOBBY, PAUSED, PLAYING, DISCONNECTED
	};


	public static enum EntityType {
		PLAYER, MONSTER, NPC
	}

	public static enum Action {
		ADD, MOVE, TURN, SHOOT, KILL, REMOVE
	}

	// Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 
	public static enum Terrain {
		EMPTY, TREASURE, NPC, MONSTER, FOOD, PORTAL, PLAYERTOMB, MONSTERTOMB
	}

	public static enum ItemGroup {
		GENERAL, GOLD, SWORD, SHIELD, FOOD
	}

	/*
    public static final String[] Layer = {
         "0_floor", "1_terrain", "2_object", "3_roof", "4_roof_add", "collision","protection"
    };
	 */       

	private static THGClientDefault myClientDafault = null;      

	/** Returns the GameScreen object */
	public static THGClientDefault get() {    
		if (myClientDafault == null) {
			myClientDafault = new THGClientDefault();
		}	
		return myClientDafault;
	}

	public THGClientDefault() {

	}

	/** Returns screen width in tiles */
	public int getViewWidth() {
		return SCREEN_WIDTH / TILEWIDTH;
	}

	/** Returns screen height in tiles */
	public int getViewHeight() {
		return getViewHeightInPixels() / TILEHEIGHT;
	}

	public int getViewWidthInPixels() {
		return SCREEN_WIDTH;
	}

	/** Returns screen height in pixels */
	public int getViewHeightInPixels() {
		return SCREEN_HEIGHT - ControlPanel.PANEL_HEIGHT;            
	}

	public float getTopOffset() {
		return getViewHeight() / 2;
	}

	public float getLeftOffset() {
		return getViewWidth() / 2;
	}

}
