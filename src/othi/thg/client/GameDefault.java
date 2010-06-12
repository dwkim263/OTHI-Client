package othi.thg.client;

/**
 * defining default value of game
 * @author Dong Won Kim
 */
public class GameDefault {

    private static final String SCREEN_SIZE = "800x600";
    
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;        
    
    static {
		String[] dim = SCREEN_SIZE.split("x");
		SCREEN_WIDTH = Integer.parseInt(dim[0]);                
		SCREEN_HEIGHT = Integer.parseInt(dim[1]); 
    }       
    
    private static  final  String VIEW_SIZE = "750x562";    
    
    public static int VIEW_WIDTH;
    public static int VIEW_HEIGHT;        
    
    static {
		String[] dim = VIEW_SIZE.split("x");
		VIEW_WIDTH = Integer.parseInt(dim[0]);
		VIEW_HEIGHT = Integer.parseInt(dim[1]); 
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
//    public static final String ITEM_TREASURE = "ItemTreasure.";
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

    /**
     * The targeted center of view X coordinate (truncated).
     */
    private int	x;

    /**
     * The targeted center of view Y coordinate (truncated).
     */
    private int	y;

    /** Actual size of the screen in pixels */
    private int sw, sh;

    /** Actual size of the world in world units */
    protected int ww, wh;
    
    private static GameDefault screen;
    
    /**
     * Set the default [singleton] screen.
     *
     * @param	screen		The screen.
     */
    public static void setDefaultScreen(GameDefault screen) {
            GameDefault.screen = screen;
    }

    /** Returns the GameScreen object */
    public static GameDefault get() {    
            return screen;
    }

    /** Returns screen width in world units */
    public float getWidth() {
            return sw / TILEWIDTH;
    }

    /** Returns screen height in world units */
    public float getHeight() {
            return sh / TILEHEIGHT;
    }

 
    public int getWidthInPixels() {
            return sw;
    }

    /** Returns screen height in pixels */
    public int getHeightInPixels() {
            return sh;            
    }

    public GameDefault() {
        
    }
}
