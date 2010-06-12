package othi.thg.client;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;


/**
 * main program
 * @author Dong Won Kim
 */
public class TreasureHuntGame extends StateBasedGame {
    
 //   public static String GAMEHOST = "142.3.200.90"; //Hercules
 //   public static String GAMEHOST = "142.3.40.182"; //my office
    public static String GAMEHOST = "localhost";
    public static String GAMEPORT = "13134";

    public TreasureHuntGame() {
        super("OTHI - Online Treasure Hunt for Inquiry-based learning!");
    }
    
    public TreasureHuntGame(String arg0) {
        super(arg0);
    }      
    
    @Override
    public void initStatesList(GameContainer container) {
        addState(new THGkeeper());
        addState(new THGground());
    }

    public static void main(String[] args) {
        if (args.length > 0) {
               GAMEHOST = args[0];
            if (args.length > 1) {
                GAMEPORT = args[1];
            }
        }                 
        try {
            AppGameContainer container = new AppGameContainer(new TreasureHuntGame());

            container.setDisplayMode(GameDefault.SCREEN_WIDTH, GameDefault.SCREEN_HEIGHT, false);
            container.start();           
        } catch (SlickException e){
            e.printStackTrace();
        }   
    }
}
