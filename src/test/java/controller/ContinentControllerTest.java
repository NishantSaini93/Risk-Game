package controller;

import model.GameMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import view.ContinentPanel;

/**
 * Test class for ContinentController
 */
public class ContinentControllerTest {
    /**
     * Variable for testing view
     */
    @Mock
    ContinentPanel view;
    /**
     * Reference for ContinentController class
     */
    ContinentController controller;
    /**
     * Reference for GameMap class
     */
    GameMap gameMap;

    /**
     * It set up the environment for tests
     *
     * @throws Exception when exception occurs
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        controller = new ContinentController(view);
        gameMap = GameMap.getInstance();
        gameMap.setDummyData();
    }


    /**
     * check if view method is called or not from controller method
     */
    @Test
    public void updateContinentList() {
        controller.updateContinentList();
        Mockito.verify(view).updateContinentList(gameMap.continents.values());
    }
}
