package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * This test the GameMap Class
 */
public class GameMapTest {
    /**
     * Reference for GameMap Class
     */
    GameMap gameMap;

    /**
     * This setup the environment for test cases
     */
    @Before
    public void setUp() {
        gameMap = GameMap.getInstance();
        gameMap.clearInformation();
    }

    /**
     * checks if the size of the continent is correct
     * when inserting empty value
     */
    @Test
    public void saveContinent() {
        gameMap.saveContinent("", 0);
        gameMap.saveContinent("Asia", 12);
        gameMap.saveContinent("North America", 100);
        Assert.assertEquals(2, gameMap.continents.size());


    }

    /**
     * checks if the continent contains key and name
     */
    @Test
    public void saveContinentCheckName() {
        gameMap.saveContinent("", 0);
        gameMap.saveContinent("Asia", 12);
        gameMap.saveContinent("North America", 100);
        Assert.assertTrue(gameMap.continents.containsKey(1));
        Assert.assertTrue(gameMap.continents.containsKey(2));
        Assert.assertEquals("Asia", gameMap.continents.get(1).name);
        Assert.assertEquals("North America", gameMap.continents.get(2).name);
    }

    /**
     * checks if the continent contains the correct continent value
     */
    @Test
    public void saveContinentCheckCV() {
        gameMap.saveContinent("", 0);
        gameMap.saveContinent("Asia", 12);
        gameMap.saveContinent("North America", 100);
        Assert.assertTrue(gameMap.continents.containsKey(1));
        Assert.assertTrue(gameMap.continents.containsKey(2));
        Assert.assertEquals(100, gameMap.continents.get(2).controlValue);
    }

    /**
     * checks if the continent contains duplicate name
     * if duplicate then do not increase size of continents
     */
    @Test
    public void saveContinentDuplicateContinent() {
        gameMap.saveContinent("", 0);
        gameMap.saveContinent("Asia", 12);
        gameMap.saveContinent("North America", 100);
        gameMap.saveContinent("Asia", 100);
        Assert.assertEquals(2, gameMap.continents.size());
        Assert.assertEquals(12, gameMap.continents.get(1).controlValue);
        Assert.assertNull(gameMap.continents.get(3));
    }

    /**
     * Check if the country is being added to gameMap by checking the size
     */
    @Test
    public void saveCountry() {
        gameMap.saveContinent("", 0);
        gameMap.saveContinent("Asia", 12);
        gameMap.saveContinent("North America", 100);
        String[] territories = new String[]{"Nepal", "Asia", "India", "China"};
        gameMap.saveCountry(Arrays.asList(territories));
        Assert.assertEquals(3, gameMap.countries.size());

    }

    /**
     * Check if the country is being added to gameMap by checking the size
     * Validate before inserting inside map
     */
    @Test
    public void saveCountryWithoutContinent() {
        gameMap.saveContinent("", 0);
        gameMap.saveContinent("Asia", 12);
        gameMap.saveContinent("North America", 100);
        String[] territories = new String[]{"Nepal", "Europe", "India", "China"};
        gameMap.saveCountry(Arrays.asList(territories));
        Assert.assertFalse(gameMap.continents.entrySet().contains("Europe"));
        Assert.assertEquals(0, gameMap.countries.size());

    }

    /**
     * test for the number of armies of player calculation
     */
    @Test
    public void getNumberOfArmiesOwnedByPlayers() {
        gameMap.setDummyData();
        List<Integer> armies = gameMap.getNumberOfArmiesOwnedByPlayers();
        Assert.assertEquals(3, armies.size());
        Assert.assertEquals(Integer.valueOf(27), armies.get(0));
        Assert.assertEquals(Integer.valueOf(22), armies.get(1));
        Assert.assertEquals(Integer.valueOf(71), armies.get(2));
    }

    /**
     * test for the number of countries of player calculation
     */
    @Test
    public void getNumberOfCountriesOwnedByPlayers() {
        gameMap.setDummyData();
        List<Integer> countries = gameMap.getNumberOfCountriesOwnedByPlayers();
        Assert.assertEquals(3, countries.size());
        Assert.assertEquals(Integer.valueOf(3), countries.get(0));
        Assert.assertEquals(Integer.valueOf(2), countries.get(1));
        Assert.assertEquals(Integer.valueOf(3), countries.get(2));
    }

    /**
     * test for the number of armies of player calculation
     */

    @Test
    public void getNumberOfContinentsOwnedByPlayers() {
        gameMap.setDummyData();
        List<Integer> data = gameMap.getNumberOfContinentsOwnedByPlayers();
        Assert.assertEquals(3, data.size());
        Assert.assertEquals(Integer.valueOf(2), data.get(0));
        Assert.assertEquals(Integer.valueOf(2), data.get(1));
        Assert.assertEquals(Integer.valueOf(2), data.get(2));
    }

    /**
     * This check the ending of game
     */
    @Test
    public void checkGameEnd() {
        gameMap.setDummyData();
        for (Country country : gameMap.countries.values()) {
            country.changeOwner(gameMap.currentPlayer);
        }
        gameMap.checkGameEnd();
        Assert.assertTrue(gameMap.gameEnded);
    }

    /**
     * This check change of phase
     */
    @Test
    public void changePhase() {
        gameMap.changePhase(GameMap.Phase.FORTIFY);
        Assert.assertEquals(GameMap.Phase.FORTIFY, gameMap.currentPhase);
    }

    /**
     * This check is the armies get updated or not through updateArmiesOfCountries function or not
     */
    @Test
    public void updateArmiesOfCountries() {
        gameMap.setDummyData();
        int numberArmiesToTransfer = 2;
        gameMap.updateArmiesOfCountries(numberArmiesToTransfer, gameMap.countries.get(1), gameMap.countries.get(2));
        Assert.assertEquals(gameMap.countries.get(1).numOfArmies, 8);
        Assert.assertEquals(gameMap.countries.get(2).numOfArmies, 22);
    }

    /**
     * Checks the initial value of armies set for each player according to risk game rules
     */
    @Test
    public void distributeInitialArmiesRandomly() {
        gameMap.setDummyData();
        gameMap.distributeInitialArmiesRandomly(gameMap.players.values());
        for (int playerId : gameMap.players.keySet()) {
            int totalArmies = 0;
            for (int i = 0; i < gameMap.players.get(playerId).countries.size(); i++) {
                totalArmies += gameMap.players.get(playerId).countries.get(i).numOfArmies;
            }
            Assert.assertEquals(35, totalArmies);
        }
    }

    /**
     * provides initial number of army based on number of players
     */
    @Test
    public void getInitialArmy() {
        gameMap.setDummyData();
        Assert.assertEquals(35, gameMap.getInitialArmy());
    }

    /**
     * test tournament cycle updates
     */
    @Test
    public void updateTournamentGameMapValues() {
        gameMap.gameNumbers.put(1, 2);
        gameMap.gameNumbers.put(2, 3);
        gameMap.maps.put(1, new File("maps/build2.map"));
        gameMap.maps.put(2, new File("maps/bla.map"));
        boolean isEnd;

        //For the first map's first game
        isEnd = gameMap.updateTournamentGameMapValues(true);
        Assert.assertFalse(isEnd);
        Assert.assertEquals(1, gameMap.gameNumberBeingPlayed);
        Assert.assertEquals(1, gameMap.mapBeingPlayed);

        //For the first map's second game
        isEnd = gameMap.updateTournamentGameMapValues(false);
        Assert.assertFalse(isEnd);
        Assert.assertEquals(2, gameMap.gameNumberBeingPlayed);
        Assert.assertEquals(1, gameMap.mapBeingPlayed);

        //For the second map's first game
        isEnd = gameMap.updateTournamentGameMapValues(false);
        Assert.assertFalse(isEnd);
        Assert.assertEquals(1, gameMap.gameNumberBeingPlayed);
        Assert.assertEquals(2, gameMap.mapBeingPlayed);

        //For the second map's second game
        isEnd = gameMap.updateTournamentGameMapValues(false);
        Assert.assertFalse(isEnd);
        Assert.assertEquals(2, gameMap.gameNumberBeingPlayed);
        Assert.assertEquals(2, gameMap.mapBeingPlayed);

        //For the second map's third game
        isEnd = gameMap.updateTournamentGameMapValues(false);
        Assert.assertFalse(isEnd);
        Assert.assertEquals(3, gameMap.gameNumberBeingPlayed);
        Assert.assertEquals(2, gameMap.mapBeingPlayed);

        //Tournament Ended---click on cross to run this case
        isEnd = gameMap.updateTournamentGameMapValues(false);
        Assert.assertEquals(3, gameMap.gameNumberBeingPlayed);
        Assert.assertEquals(2, gameMap.mapBeingPlayed);
        Assert.assertTrue(isEnd);
    }
}