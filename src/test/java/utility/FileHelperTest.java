package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Test class to test the functionality of {@link FileHelper} class
 */
public class FileHelperTest {

    /**
     * Variable to test Valid File
     */
    File testFile1, testFile2;
    /**
     * Variable to test Invalid File
     */
    File errorFile;

    /**
     * It set up the environment for tests
     */
    @Before
    public void setUp() {
        testFile1 = new File("maps/test-case/test.map");
        testFile2 = new File("maps/test-case/test1.map");
        errorFile = new File("maps/test-case/404.map");
    }

    /**
     * It remove environment after tests
     */
    @After
    public void tearDown() {
        testFile1 = null;
        testFile2 = null;
    }

    /**
     * Check if the size of continent from the given map file is correct
     * Check if the size of country from the given map file is correct
     * Check if the no. of neighbours for a country is correct
     */
    @Test
    public void loadToConfig() {
        FileHelper.loadToConfig(testFile1);
        Assert.assertEquals(1, GameMap.getInstance().continents.size());
        Assert.assertEquals(3, GameMap.getInstance().countries.size());
        Assert.assertEquals(2, GameMap.getInstance().countryGraph.get(1).size());
    }

    /**
     * Check if the map contains the necessary keywords Continent and Territories
     */
    @Test
    public void loadToConfigKeyword() {
        FileHelper.loadToConfig(errorFile);
        Assert.assertEquals(0, GameMap.getInstance().continents.size());
        Assert.assertEquals(0, GameMap.getInstance().countries.size());
        Assert.assertNull(GameMap.getInstance().countryGraph.get(1));
    }


    /**
     * Checks if the contents of the file are properly loaded to file or not
     */
    @Test
    public void loadToForm() {
        HashMap<String, List<String>> data = FileHelper.loadToForm(testFile2);
        Assert.assertNotNull(data);
        List<String> continents = data.get("continent");
        List<String> countries = data.get("country");
        Assert.assertNotNull(continents);
        Assert.assertNotNull(countries);
        Assert.assertEquals(2, continents.size());
        Assert.assertEquals(8, countries.size());

        data = FileHelper.loadToForm(errorFile);
        Assert.assertNotNull(data);
        continents = data.get("continent");
        countries = data.get("country");
        Assert.assertNotNull(continents);
        Assert.assertNotNull(countries);
        Assert.assertEquals(0, continents.size());
        Assert.assertEquals(0, countries.size());

    }

    /**
     * Write to the map file and check if the contents of the file are as expected by using
     * <code>loadToFrom</code> method of <code>FileHelper</code> class
     */
    @Test
    public void saveMapToFile() {
        File file = new File("maps/test-case/trial.map");
        String[] continents = new String[]{"Asia=4"};
        String[] countries = new String[]{"Nepal,Asia,China", "India,Asia,China", "China,Asia,India,Nepal"};
        FileHelper.saveMapToFile(file, Arrays.asList(continents), Arrays.asList(countries));

        HashMap<String, List<String>> data = FileHelper.loadToForm(file);
        Assert.assertNotNull(data);
        List<String> continentInfo = data.get("continent");
        List<String> countryInfo = data.get("country");
        Assert.assertNotNull(continentInfo);
        Assert.assertNotNull(countryInfo);
        Assert.assertEquals(1, continentInfo.size());
        Assert.assertEquals(3, countryInfo.size());
    }


    /**
     * Check if the all the elements of the map has been cleared or not
     */
    @Test
    public void emptyConfig() {
        FileHelper.emptyConfig();
        GameMap map = GameMap.getInstance();
        Assert.assertTrue(map.countries.isEmpty());
        Assert.assertTrue(map.continents.isEmpty());
        Assert.assertTrue(map.countryGraph.isEmpty());
        Assert.assertTrue(map.players.isEmpty());
        Assert.assertNull(map.currentPlayer);
    }

    /**
     * check if the game is written to file or not
     */
    @Test
    public void saveGameToFile() throws Exception {
        GameMap instance = GameMap.getInstance();
        instance.setDummyData();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String data = gson.toJson(instance);
        FileHelper.saveGameToFile(data, "savetest");
        File file = new File("savedgames/savetest.game");
        Assert.assertNotNull(file);

        FileHelper.loadGame(file);
        Assert.assertEquals(instance.players.size(), 3);
        Assert.assertEquals(instance.countries.size(), 8);
        Assert.assertEquals(instance.continents.size(), 2);
        Assert.assertEquals(instance.players.get(1).countries.size(), 3);
        Assert.assertEquals(instance.players.get(2).countries.size(), 2);
        Assert.assertEquals(instance.countries.get(7).neighbours.size(), 4);
        Assert.assertEquals(instance.countryGraph.size(), 8);
        Assert.assertEquals(instance.countries.get(7).owner, instance.players.get(3));
        Assert.assertEquals(instance.countries.get(5).owner, instance.players.get(1));
        Assert.assertEquals(instance.countries.get(3).owner, instance.players.get(3));
        Assert.assertEquals(instance.countries.get(6).owner, instance.players.get(2));
    }
}