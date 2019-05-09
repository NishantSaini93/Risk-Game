package utility;

import model.Continent;
import model.Country;
import model.GameMap;
import model.Node;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

public class MapHelperTest {
    /**
     * Reference to GameMap
     */
    GameMap gameMap;
    /**
     * Variable containing graph to test BFS
     */
    HashMap<Integer, model.Node> nodeHashMap;

    /**
     * Setup the country and their respective neighbour in countryGraph
     * Setup continent and their respective countries in countryGraph
     * nodeHashMap contains all the countryNodes(Countries)
     */
    @Before
    public void setUp() {
        nodeHashMap = new HashMap<>();
        gameMap = GameMap.getInstance();

        for (int i = 1; i <= 5; i++) {
            Node countryNode = new Node(i);
            Country country = new Country(i, "Country" + i);
            gameMap.countries.put(i, country);
            nodeHashMap.put(i, countryNode);
        }

        gameMap.continents.put(1, new Continent(1, "Asia", 56));
        gameMap.continents.put(2, new Continent(2, "Europe", 50));

        HashSet<Country> n1 = new HashSet<>();
        n1.add(gameMap.countries.get(2));
        n1.add(gameMap.countries.get(4));
        n1.add(gameMap.countries.get(5));
        gameMap.countryGraph.put(1, n1);

        HashSet<Country> n2 = new HashSet<>();
        n2.add(gameMap.countries.get(1));
        n2.add(gameMap.countries.get(5));
        n2.add(gameMap.countries.get(3));
        gameMap.countryGraph.put(2, n2);

        HashSet<Country> n3 = new HashSet<>();
        n2.add(gameMap.countries.get(2));
        gameMap.countryGraph.put(3, n3);

        HashSet<Country> n4 = new HashSet<>();
        n2.add(gameMap.countries.get(1));
        gameMap.countryGraph.put(4, n4);

        HashSet<Country> n5 = new HashSet<>();
        n2.add(gameMap.countries.get(1));
        n2.add(gameMap.countries.get(2));
        gameMap.countryGraph.put(5, n5);

        gameMap.continents.get(1).countries.add(gameMap.countries.get(1));
        gameMap.continents.get(1).countries.add(gameMap.countries.get(2));
        gameMap.continents.get(1).countries.add(gameMap.countries.get(3));
        gameMap.continents.get(2).countries.add(gameMap.countries.get(4));
        gameMap.continents.get(2).countries.add(gameMap.countries.get(5));
    }


    /**
     * Checks if all the countries are connected
     */
    @Test
    public void bfsCheckConnection() {
        boolean result = MapHelper.bfs(nodeHashMap, nodeHashMap.get(1));
        Assert.assertTrue(result);
    }

    /**
     * Removes the connection from two countries of countryGraph
     * Checks if the map is still connected
     */
    @Test
    public void bfsNotConnected() {
        gameMap.countryGraph.get(3).remove(gameMap.countries.get(2));
        gameMap.countryGraph.get(2).remove(gameMap.countries.get(3));

        boolean result = MapHelper.bfs(nodeHashMap, nodeHashMap.get(1));
        Assert.assertFalse(result);
    }

    /**
     * Checks if the nodeGraph is properly created from given list of countries
     */
    @Test
    public void createNodeGraphFromCountries() {
        HashMap<Integer, Node> nodeGraphFromCountries = MapHelper.createNodeGraphFromCountries(gameMap.countries.values());
        Assert.assertEquals(5, nodeGraphFromCountries.size());
        Assert.assertTrue(nodeGraphFromCountries.containsKey(1));
        Assert.assertTrue(nodeGraphFromCountries.containsKey(2));
        Assert.assertTrue(nodeGraphFromCountries.containsKey(3));
        Assert.assertTrue(nodeGraphFromCountries.containsKey(4));
        Assert.assertTrue(nodeGraphFromCountries.containsKey(5));
        Assert.assertFalse(nodeGraphFromCountries.containsKey(0));

    }

    /**
     * Checks if all the countries inside a continent is not connected
     */
    @Test
    public void bfsNotConnectedInContinent() {
        HashMap<Integer, Node> nodeGraphFromCountries = MapHelper.createNodeGraphFromCountries(gameMap.continents.get(2).countries);
        boolean result = MapHelper.bfs(nodeGraphFromCountries, nodeGraphFromCountries.get(5));
        Assert.assertFalse(result);
    }

    /**
     * Checks if all the countries inside the continent are connected
     */
    @Test
    public void bfsConnectedInContinent() {
        HashMap<Integer, Node> nodeGraphFromCountries = MapHelper.createNodeGraphFromCountries(gameMap.continents.get(1).countries);
        boolean result = MapHelper.bfs(nodeGraphFromCountries, nodeGraphFromCountries.get(1));
        Assert.assertTrue(result);
    }


}