package controller;

import model.GameMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import view.CountryPanel;

/**
 * test class for CountryController
 */
public class CountryControllerTest {
    /**
     * Variable for testing view
     */
    @Mock
    CountryPanel view;
    /**
     * Reference for CountryController class
     */
    CountryController controller;
    /**
     * Reference to GameMap class
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
        controller = new CountryController(view);
        gameMap = GameMap.getInstance();
        gameMap.setDummyData();
    }


    /**
     * check if view method is called or not from controller method
     */
    @Test
    public void updateCountries() {
        controller.updateCountryList();
        Mockito.verify(view).updateCountries(gameMap.countries);
    }
}
