import model.GameMap;
import view.MainFrame;

/**
 * Entry point of the game
 */
public class Game {

    /**
     * Calls the main frame
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting the game...");
        GameMap.getInstance();
        MainFrame frame = new MainFrame();
    }
}
