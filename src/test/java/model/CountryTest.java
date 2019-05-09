package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Test class for Country Class
 */
public class CountryTest {
    /**
     * Instance of GameMap Class
     */
    GameMap gameMap;

    /**
     * This sets up dummy data by calling setDummyData function in GameMap class
     *
     * @throws Exception throws exception
     */
    @Before
    public void setUp() throws Exception {
        gameMap = GameMap.getInstance();
        gameMap.setDummyData();


    }

    /**
     * Method that tests connected countries from a selected country
     */
    @Test
    public void updateConnectedCountries() {
        gameMap.countries.get(1).updateConnectedCountries();
        HashMap<Integer, Country> neighbor = gameMap.countries.get(1).connectedCountries;
        Assert.assertFalse(neighbor.isEmpty());
        Assert.assertEquals(1, neighbor.size());
        Assert.assertNotNull(neighbor.get(5));
        Assert.assertEquals(5, neighbor.get(5).id);
        Assert.assertEquals("Country 5", neighbor.get(5).name);
        Assert.assertEquals(1, neighbor.get(5).owner.id);
    }


    /**
     * Method that tests if the function is giving the correct no, of neighbours of a country
     */
    @Test
    public void getNeighbours() {
        Assert.assertEquals(3, gameMap.countries.get(1).neighbours.size());
    }

    /**
     * Method that tests the number of armies of a country
     */
    @Test
    public void getNumberofArmies() {
        Assert.assertEquals(10, gameMap.countries.get(1).numOfArmies);
    }

    /**
     * Method that is checking the number of dice allowed for a player according to the number of armies they have
     */
    @Test
    public void updateNumOfDiceAllowedPlayer() {
        gameMap.countries.get(1).updateNumOfDiceAllowed(false);
        //gameMap.countries.get(1) has 10 armies
        Assert.assertEquals(3, gameMap.countries.get(1).numOfDiceAllowed);
        gameMap.countries.get(5).updateNumOfDiceAllowed(false);
        //gameMap.countries.get(5) has 3 armies
        Assert.assertEquals(2, gameMap.countries.get(5).numOfDiceAllowed);
        gameMap.countries.get(6).updateNumOfDiceAllowed(false);
        //gameMap.countries.get(6) has 2 armies
        Assert.assertEquals(1, gameMap.countries.get(6).numOfDiceAllowed);
        //gameMap.countries.get(7) has 1 armies
        gameMap.countries.get(7).updateNumOfDiceAllowed(false);
        Assert.assertEquals(0, gameMap.countries.get(7).numOfDiceAllowed);

    }

    /**
     * Method that is checking the number of dice allowed for an opponent according to the number of armies they have
     */
    @Test
    public void updateNumOfDiceAllowedOpponent() {
        gameMap.countries.get(1).updateNumOfDiceAllowed(true);
        //gameMap.countries.get(1) has 10 armies
        Assert.assertEquals(2, gameMap.countries.get(1).numOfDiceAllowed);
        //gameMap.countries.get(5) has 3 armies
        gameMap.countries.get(5).updateNumOfDiceAllowed(true);
        Assert.assertEquals(2, gameMap.countries.get(5).numOfDiceAllowed);
        gameMap.countries.get(6).updateNumOfDiceAllowed(true);
        Assert.assertEquals(2, gameMap.countries.get(6).numOfDiceAllowed);
        gameMap.countries.get(7).updateNumOfDiceAllowed(true);
        Assert.assertEquals(1, gameMap.countries.get(7).numOfDiceAllowed);
    }

    /**
     * Method that checks if the owner is being changed of a country
     */
    @Test
    public void changeOwner() {
        Player newPlayer = new Player(100, "Rashmi");
        gameMap.countries.get(7).changeOwner(newPlayer);
        Assert.assertTrue(gameMap.countries.get(7).owner == newPlayer);
        Assert.assertEquals(1, newPlayer.countries.size());
        Assert.assertEquals(Country.Update.OWNER, gameMap.countries.get(7).state);
    }


    /**
     * Method that checks the number of armies after adding to the number of armies
     */
    @Test
    public void addArmies() {
        gameMap.countries.get(7).addArmies(5);
        Assert.assertEquals(6, gameMap.countries.get(7).numOfArmies);
    }

    /**
     * Method that checks the number of armies after reducing to the number of armies
     */
    @Test
    public void deductArmies() {
        gameMap.countries.get(1).deductArmies(4);
        Assert.assertEquals(6, gameMap.countries.get(1).numOfArmies);
    }

    /**
     * Test the function to get neighboring countries with different owner id
     */
    @Test
    public void getNeighboursDiffOwner() {
        HashSet<Country> countries = gameMap.countries.get(1).getNeighboursDiffOwner();
        Assert.assertEquals(2, countries.size());
    }

}