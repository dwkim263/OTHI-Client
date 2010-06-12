package othi.thg.client;


import java.io.*;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import othi.thg.client.GameDefault.GameState;
import othi.thg.client.entities.Quest;
import othi.thg.client.entities.sprites.PlayerSprite;
import othi.thg.client.entities.sprites.THGSprite;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;


/**
 * Managing communication between client and server 
 * @author Dong Won Kim
 */
public class THGClient implements SimpleClientListener {
    private SimpleClient simpleClient = null;
    
    /** Map that associates a channel name with a {@link ClientChannel}. */
    protected final Map<String, ClientChannel> channelsByName = new HashMap<String, ClientChannel>();
    
    ConcurrentLinkedQueue<ByteBuffer> channelQueue = null;

    /** Sequence generator for counting channels. */
    protected final AtomicInteger channelNumberSequence = new AtomicInteger(1);        
    
    private PlayerSprite sprite = null;
    
    private int myID = -1;
    
    private ConcurrentLinkedQueue<ByteBuffer> inputQueue =  null;
        
    private String userName;
    
    private String userPassword;
    
    private int stageID = 1;
            
    private String placeMapRef;
    
    private int placeId;
    
    private int questID = 0;        
    
    private Map<Integer, Quest> myQuests = new HashMap<Integer, Quest>();

    private Map<Integer, String[]> myTreasures = new HashMap<Integer, String[]>();    

    private GameState state = null;

    private boolean loginFailed = false;
    
    private String[] dialogues = null;

    volatile String query;

    volatile int replyRequestFlag;
    
    THGSprite receiver;
    
    private static THGClient  myTHOClient = null;
    
    public static THGClient get() {
        
        if (myTHOClient == null) {
                myTHOClient = new THGClient();
        }
         return myTHOClient;
   }

    // make constructor private so no one except the getParty() can call it
    private THGClient() {
          state = GameState.GAMELOADING;    
    }

    public boolean isLoginFailed() {
        return loginFailed;
    }

    public void setLoginFailed(boolean loginFailed) {
        this.loginFailed = loginFailed;
    }
    
    protected void setDialogues(String[] d) {
        if ( d.length != 0) dialogues = d;        
    }
    
    protected String[] getDialogues() {
        if (dialogues == null) return null;
        else return dialogues;
    }
        
    protected void setGameState(GameState s) {
        state = s;
    }
    
    protected GameState getGameState() {
        return state;
    }
    
    protected ConcurrentLinkedQueue<ByteBuffer> getInputQueue() {
         if ( inputQueue == null) {
              inputQueue =  new ConcurrentLinkedQueue<ByteBuffer>();
         } 
         return inputQueue;
    }
    
    protected SimpleClient getSimpleClient() {
         if (simpleClient == null) {
                simpleClient = new SimpleClient(this);
         }
         return simpleClient;
    }

    public void removeMyQuest(int questID) {
        myQuests.remove(Integer.valueOf(questID));
    }

    public void setMyQuests(Map<Integer, Quest> myQuests) {
        this.myQuests = myQuests;
    }

    public void setMyTreasures(Map<Integer, String[]> myTreasures) {
        this.myTreasures = myTreasures;
    }

    public Map<Integer, Quest> getMyQuests() {
        return myQuests;
    }

    public Quest getMyQuest(int questID) {
        if (myQuests == null) return null;
        return myQuests.get(questID);
    }

    public Map<Integer, String[]> getMyTreasures() {
        return myTreasures;
    }
    
    protected int getMyID() {
        return myID;
    }   

    protected void setMyID(int id) {       
    	if (myID == -1) {
            myID = id;
    	}
    }
          
    protected void initializeMyID() {       
    	myID = -1;
    }
    
    protected void setUserName(String username) {
        userName = username;
    }

    protected String getUserName() {
        return userName;
    }

    protected void setUserPassword(String userpass) {
        userPassword = userpass;
    }
    
    public void setStageID(int s) {
        stageID = s;
    }
    
    public int getStageID() {
        return stageID;
    }    
    
    public void setQuestID (int questid) {
        questID = questid;
    }
    
    public int getQuestID (){
        return questID;
    }
    
    public void setSprite(PlayerSprite s) {
        sprite = s;
    }

    public PlayerSprite getSprite() {
         return sprite;
    }

    public String getPlaceMapRef() {
        return placeMapRef;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceMapRef(String placeMapRef) {
        this.placeMapRef = placeMapRef;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }
    
    protected void setSendQuery(String message, int replyRequestFlag){
        if (message == null) {
            query = null;
            this.replyRequestFlag = 0;           
        } else if (query == null) {
            query = message;
            this.replyRequestFlag = replyRequestFlag;
        }
    }
    
    protected String getSendQuery(){
        if (query == null) return null;
        else return query;
    }

    protected int getReplyRequestFlag() {
        return replyRequestFlag;
    }

    protected void setReceiver(THGSprite sprite) {
        receiver = sprite;
    }
    
    protected THGSprite getReceiver() {
        if (receiver == null) return null;
        return receiver;
    }
    
    protected void doLogin() {
        Properties loginProperties = new Properties();
        loginProperties.setProperty("host",TreasureHuntGame.GAMEHOST);
        loginProperties.setProperty("port",TreasureHuntGame.GAMEPORT);
        try {
            getSimpleClient().login(loginProperties);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }  
        
    public void send(byte[] bytes) {
        try {                      
            getSimpleClient().send(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(ClientChannel channel, byte[] bytes) {
        try {
            channel.send(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {

       return new PasswordAuthentication(userName, userPassword.toCharArray());
    }

    @Override
    public void loggedIn() {
        setLoginFailed(false);      
        setGameState(GameState.LOGINED);
    }

    @Override
    public void loginFailed(String arg0) {
        setLoginFailed(true);
        setGameState(GameState.LOGINFAILED);
    }

    /*
    @Override    
    public void receivedMessage(byte[] arg0) {
         ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(arg0));
         Object m = ois.readObject();
         handleMessage(m);
    }
    
    
    public void handleMessage(Object obj) {
        if (msg instanceof FireRocketMessage) {
            handleRocketMessage((FireRocketMessage)msg);
        }
        else {
           System.err.println("Unknown message: "+o);
        }
    }
    
    */
    
    @Override    
    public void receivedMessage(ByteBuffer message) {
       // ByteBuffer buff = ByteBuffer.allocate(arg0.length);
       // buff.put(arg0);
       // buff.flip();
        getInputQueue().add(message);
    }

    /**
    * {@inheritDoc}
    * <p>
    * Returns a listener that formats and displays received channel
    * messages in the output text pane.
    */
    @Override
    public ClientChannelListener joinedChannel(ClientChannel channel) {
        channelsByName.put(channel.getName(), channel);
        //appendOutput("Joined to channel " + channel.getName());
        //channelSelectorModel.addElement(channel.getName());        
        return new THOChannelListener();
    }
     
    @Override    
    public void disconnected(boolean arg0, String arg1) {
               System.out.println("The reason of disconnection is " + arg1);
               setGameState(GameState.DISCONNECTED);      
    }
    
    @Override    
    public void reconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reconnecting() {
        // TODO Auto-generated method stub

    }
    
   
    /**
    * A simple listener for channel events.
    */
    public class THOChannelListener  implements ClientChannelListener
    {
        /**
        * An example of per-channel state, recording the number of
        * channel joins when the client joined this channel.
        */
        private final int channelNumber;
        /**
        * Creates a new {@code HelloChannelListener}. Note that
        * the listener will be given the channel on its callback
        * methods, so it does not need to record the channel as
        * state during the join.
        */
        public THOChannelListener() {
            channelNumber = channelNumberSequence.getAndIncrement();
        }
        /**
        * {@inheritDoc}
        * <p>
        * Displays a message when this client leaves a channel.
        */
        @Override
        public void leftChannel(ClientChannel channel) {
            //appendOutput("Removed from channel " + channel.getName());
        }
        
        protected ConcurrentLinkedQueue<ByteBuffer> getChannelQueue() {
             if ( channelQueue == null) {
                  channelQueue =  new ConcurrentLinkedQueue<ByteBuffer>();
             } 
             return channelQueue;
        }        
        /**
        * {@inheritDoc}
        * <p>
        * Formats and displays messages received on a channel.
        */
        @Override        
        public void receivedMessage(ClientChannel channel, ByteBuffer message)
        {
           // appendOutput("[" + channel.getName() + "/ " + channelNumber +
           // "] " + sender + ": " + decodeString(message));
           // ByteBuffer buff = ByteBuffer.allocate(message.length);
           // buff.put(message);
           // buff.flip();      
           getChannelQueue().add(message);
        }
    }
    
}
