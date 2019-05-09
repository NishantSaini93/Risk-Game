package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This test the Continent class
 */
public class ContinentTest {

    /**
     * Continent with no owner
     */
    Continent continent1;
    /**
     * Continent with owner set
     */
    Continent continent2;
    /**
     * Owner 1
     */
    Player player1;
    /**
     * Owner 2
     */
    Player player2;

    /**
     * It sets up the environment for test cases
     */
    @Before
    public void setUp() {
        player1 = new Player(1, "P1");
        player2 = new Player(2, "P2");

        Country country1 = new Country(1, "C1");
        country1.owner = player1;
        Country country2 = new Country(2, "C2");
        country2.owner = player2;
        Country country3 = new Country(3, "C3");
        country3.owner = player1;


        continent1 = new Continent(1, "C1", 2);
        continent1.countries.add(country1);
        continent1.countries.add(country2);
        continent2 = new Continent(2, "C2", 2);
        continent2.countries.add(country1);
        continent2.countries.add(country3);

    }

    /**
     * Test for the owner of continent
     */
    @Test
    public void isOwnedBy() {
        Assert.assertFalse(continent1.isOwnedBy(player1));
        Assert.assertTrue(continent2.isOwnedBy(player1));
    }

}