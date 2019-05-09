package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This test the player class
 */
public class PlayerTest {

    /**
     * Instance of GameMap Class
     */
    GameMap gameMap;

    /**
     * HashMap for countries(the country's id as the key and country object as the value)
     */
    HashMap<Integer, Country> countries;

    /**
     * List for countries in the continent
     */
    ArrayList<Country> countriesInContinent;

    /**
     * HashMap for players(the player's id as the key and player object as the value)
     */
    HashMap<Integer, Player> players;

    /**
     * List for cards
     */
    ArrayList<Card> cards;

    /**
     * HashMap for continents(the continent's id as the key and continent object as the value)
     */
    HashMap<Integer, Continent> continents;

    /**
     * This sets up dummy data by calling setDummyData function in GameMap class
     *
     * @throws Exception throws exception
     */
    @Before
    public void setUp() throws Exception {
        gameMap = GameMap.getInstance();
        gameMap.setDummyData();

        continents = new HashMap<>();
        countries = new HashMap<>();
        countriesInContinent = new ArrayList<>();
        players = new HashMap<>();


        Player player;
        for (int loopForPlayers = 1; loopForPlayers < 4; loopForPlayers++) {
            player = new Player(loopForPlayers, "player" + loopForPlayers);
            players.put(loopForPlayers, player);
        }


        int loopForContinent = 1;
        Integer[] controlValues = new Integer[]{7, 10};
        for (int i = 1; i < 30; i++) {
            Country country = new Country(i, "Country" + i);
            if (i < 13) {
                country.owner = players.get(1);
            } else if (i < 17) {
                country.owner = players.get(2);
            } else {
                country.owner = players.get(3);
            }
            countriesInContinent.add(country);
            countries.put(i, country);
            if (i == 12 || i == 29) {
                Continent continent = new Continent(loopForContinent, "Continent" + loopForContinent, controlValues[loopForContinent - 1]);
                continent.countries = countriesInContinent;
                continents.put(loopForContinent, continent);
                countriesInContinent = new ArrayList<>();
                loopForContinent++;
            }
        }

        cards = new ArrayList<Card>();
        cards.add(new Card(Card.TYPE.CAVALRY));
        cards.add(new Card(Card.TYPE.CAVALRY));
        cards.add(new Card(Card.TYPE.CAVALRY));
        cards.add(new Card(Card.TYPE.INFANTRY));
        cards.add(new Card(Card.TYPE.ARTILLERY));
        cards.add(new Card(Card.TYPE.ARTILLERY));
        cards.add(new Card(Card.TYPE.ARTILLERY));
        cards.add(new Card(Card.TYPE.ARTILLERY));
        players.get(1).cards = cards;

        cards = new ArrayList<Card>();
        cards.add(new Card(Card.TYPE.CAVALRY));
        cards.add(new Card(Card.TYPE.CAVALRY));
        cards.add(new Card(Card.TYPE.ARTILLERY));
        players.get(2).cards = cards;
    }

    /**
     * Check the function calculating the total armies on start of every reinforcement phase
     */
    @Test
    public void getTotalArmiesReinforce() {
        int totalArmies;
        // to check armies for player having countries less than 9
        //---will be given 3 armies
        Player currentPlayerLocal = gameMap.currentPlayer;
        gameMap.currentPlayer = players.get(2);
        totalArmies = gameMap.currentPlayer.getTotalArmiesReinforce(countries, continents);
        assertEquals(3, totalArmies);

        //to check armies for player having countries equal to 13 but not complete continent
        //---will be given 13/3=4 armies
        gameMap.currentPlayer = players.get(3);
        totalArmies = gameMap.currentPlayer.getTotalArmiesReinforce(countries, continents);
        assertEquals(4, totalArmies);

        //to check armies for player having countries equal to 12 but have a complete continent with control value 7
        //---will be given 12/3+7=11 armies
        gameMap.currentPlayer = players.get(1);
        totalArmies = gameMap.currentPlayer.getTotalArmiesReinforce(countries, continents);
        assertEquals(11, totalArmies);

        gameMap.currentPlayer = currentPlayerLocal;
    }

    /**
     * Check the function which converts the cards' list to HashMap(the card's name as the key and the card's number as the value)
     */
    @Test
    public void getCardSetsOfPlayer() {

        HashMap<String, Integer> cardsToBeTested;

        Player currentPlayerLocal = gameMap.currentPlayer;
        gameMap.currentPlayer = players.get(1);
        cardsToBeTested = gameMap.currentPlayer.getCardSetsOfPlayer();
        assertEquals(3, cardsToBeTested.size());//size will always be 3
        assertEquals(3, cardsToBeTested.get("CAVALRY").intValue());
        assertEquals(1, cardsToBeTested.get("INFANTRY").intValue());
        assertEquals(4, cardsToBeTested.get("ARTILLERY").intValue());

        gameMap.currentPlayer = players.get(2);
        cardsToBeTested = gameMap.currentPlayer.getCardSetsOfPlayer();
        assertEquals(3, cardsToBeTested.size());//size will always be 3
        assertEquals(2, cardsToBeTested.get("CAVALRY").intValue());
        assertEquals(0, cardsToBeTested.get("INFANTRY").intValue());
        assertEquals(1, cardsToBeTested.get("ARTILLERY").intValue());

        gameMap.currentPlayer = currentPlayerLocal;
    }

    /**
     * Check the function updating the total armies when the set of three cards are exchanged
     */
    @Test
    public void exchangeCardsForArmies() {
        int totalArmies;

        //to check armies for player having countries equal to 13 but not complete continent
        //---will be given 13/3=4 armies
        Player currentPlayerLocal = gameMap.currentPlayer;
        gameMap.currentPlayer = players.get(3);
        totalArmies = gameMap.currentPlayer.getTotalArmiesReinforce(countries, continents);
        gameMap.currentPlayer.setArmiesForReinforcement();
        assertEquals(4, totalArmies);

        //initially the armies were 4
        //on every set of 3 cards are exchanged---5 armies will be allotted on first exchange
        //---will be given 4+5=9 armies
        totalArmies = gameMap.currentPlayer.exchangeCardsForArmies(totalArmies);
        assertEquals(9, totalArmies);

        //now the armies were 9
        //on every set of 3 cards are exchanged---10 armies will be allotted on second exchange
        //---will be given 9+10=19 armies
        totalArmies = gameMap.currentPlayer.exchangeCardsForArmies(totalArmies);
        assertEquals(19, totalArmies);

        //now the armies were 19
        //on every set of 3 cards are exchanged---15 armies will be allotted on third exchange
        //---will be given 19+15=34 armies
        gameMap.currentPlayer = players.get(3);
        totalArmies = gameMap.currentPlayer.exchangeCardsForArmies(totalArmies);
        assertEquals(34, totalArmies);

        gameMap.currentPlayer = currentPlayerLocal;
    }

    /**
     * check the size of the countries owned by the current player which are eligible to attack
     */
    @Test
    public void getCountriesAllowedToAttack() {
        Assert.assertEquals(3, gameMap.currentPlayer.getCountriesAllowedToAttack().size());
        //check to see if the number of armies is greater than 1
        for (Country country : gameMap.currentPlayer.getCountriesAllowedToAttack()) {
            Assert.assertTrue(country.numOfArmies > 1);
        }
    }

    /**
     * For allout mode
     * <p>
     * gets the number of dice allowed for player and opponent
     * calls the function rollDice
     * Check the number of diceValues rolled during the process
     */
    @Test
    public void rollDiceAllOut() {
        int playerNumOfDiceAllowed;
        int opponentNumOfDiceAllowed;
        boolean isAllOut = true;

        gameMap.countries.get(1).updateNumOfDiceAllowed(false);
        gameMap.countries.get(2).updateNumOfDiceAllowed(true);
        playerNumOfDiceAllowed = gameMap.countries.get(1).numOfDiceAllowed;
        opponentNumOfDiceAllowed = gameMap.countries.get(2).numOfDiceAllowed;
        gameMap.currentPlayer.rollDice(playerNumOfDiceAllowed, opponentNumOfDiceAllowed, isAllOut);
        assertEquals(3, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(2, gameMap.currentPlayer.diceValuesOpponent.size());

        gameMap.countries.get(1).updateNumOfDiceAllowed(true);
        gameMap.countries.get(2).updateNumOfDiceAllowed(false);
        playerNumOfDiceAllowed = gameMap.countries.get(2).numOfDiceAllowed;
        opponentNumOfDiceAllowed = gameMap.countries.get(1).numOfDiceAllowed;
        gameMap.currentPlayer.rollDice(playerNumOfDiceAllowed, opponentNumOfDiceAllowed, isAllOut);
        assertEquals(3, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(2, gameMap.currentPlayer.diceValuesOpponent.size());

        gameMap.countries.get(1).updateNumOfDiceAllowed(true);
        gameMap.countries.get(7).updateNumOfDiceAllowed(false);
        playerNumOfDiceAllowed = gameMap.countries.get(7).numOfDiceAllowed;
        opponentNumOfDiceAllowed = gameMap.countries.get(1).numOfDiceAllowed;
        gameMap.currentPlayer.rollDice(playerNumOfDiceAllowed, opponentNumOfDiceAllowed, isAllOut);
        assertEquals(0, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(2, gameMap.currentPlayer.diceValuesOpponent.size());

        gameMap.countries.get(7).updateNumOfDiceAllowed(true);
        gameMap.countries.get(1).updateNumOfDiceAllowed(false);
        playerNumOfDiceAllowed = gameMap.countries.get(1).numOfDiceAllowed;
        opponentNumOfDiceAllowed = gameMap.countries.get(7).numOfDiceAllowed;
        gameMap.currentPlayer.rollDice(playerNumOfDiceAllowed, opponentNumOfDiceAllowed, isAllOut);
        assertEquals(3, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(1, gameMap.currentPlayer.diceValuesOpponent.size());
    }

    /**
     * For normal mode
     * <p>
     * gets the number of dice allowed for player and opponent
     * calls the function rollDice
     * Check the number of diceValues rolled during the process
     */
    @Test
    public void rollDice() {
        boolean isAllOut = false;

        gameMap.currentPlayer.rollDice(3, 2, isAllOut);
        assertEquals(3, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(2, gameMap.currentPlayer.diceValuesOpponent.size());


        gameMap.currentPlayer.rollDice(3, 1, isAllOut);
        assertEquals(3, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(1, gameMap.currentPlayer.diceValuesOpponent.size());

        gameMap.currentPlayer.rollDice(2, 2, isAllOut);
        assertEquals(2, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(2, gameMap.currentPlayer.diceValuesOpponent.size());

        gameMap.currentPlayer.rollDice(2, 1, isAllOut);
        assertEquals(2, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(1, gameMap.currentPlayer.diceValuesOpponent.size());

        gameMap.currentPlayer.rollDice(1, 2, isAllOut);
        assertEquals(1, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(2, gameMap.currentPlayer.diceValuesOpponent.size());

        gameMap.currentPlayer.rollDice(1, 1, isAllOut);
        assertEquals(1, gameMap.currentPlayer.diceValuesPlayer.size());
        assertEquals(1, gameMap.currentPlayer.diceValuesOpponent.size());
    }

    /**
     * Given a selected country and selected neighbour country
     * Check the diceValues of player and opponent
     * check the num of dice to be considered
     * Check if the correct number of armies has been deducted for player and opponent
     */
    @Test
    public void checkVictory() {
        List<Country> countriesAllowedToAttack = gameMap.currentPlayer.getCountriesAllowedToAttack();
        Country selectedCountry = countriesAllowedToAttack.get(1);
        Country selectedNeighbour = gameMap.countries.get(6);

        int[] diceValuesP = {6, 5, 1};
        for (int diceValue : diceValuesP) {
            gameMap.currentPlayer.diceValuesPlayer.add(diceValue);
        }
        int[] diceValuesO = {6, 1};
        for (int diceValue : diceValuesO) {
            gameMap.currentPlayer.diceValuesOpponent.add(diceValue);
        }

        int numConsideredDice = Math.min(gameMap.currentPlayer.diceValuesPlayer.size(), gameMap.currentPlayer.diceValuesOpponent.size());

        assertEquals(2, numConsideredDice);

        gameMap.currentPlayer.checkVictory(selectedCountry, selectedNeighbour);
        assertEquals(2, selectedCountry.numOfArmies);
        assertEquals(1, selectedNeighbour.numOfArmies);

    }

    /**
     * Given a selected country and selected neighbour country
     * Check the diceValues of player and opponent
     * check the num of dice to be considered
     * Check if the correct number of armies has been deducted for player and opponent
     * Check if owner changes when country is captured by another player
     */
    @Test
    public void checkVictoryOwnerChanged() {
        Country selectedCountry = gameMap.countries.get(8);
        HashSet<Country> neighboursOfSelectedCountry = selectedCountry.getNeighbours();
        Country selectedNeighbour = null;
        for (Country neighbour : neighboursOfSelectedCountry) {
            selectedNeighbour = neighbour;

        }

        int[] diceValuesP = {6, 5, 1};
        for (int diceValue : diceValuesP) {
            gameMap.currentPlayer.diceValuesPlayer.add(diceValue);
        }
        int[] diceValuesO = {1};
        for (int diceValue : diceValuesO) {
            gameMap.currentPlayer.diceValuesOpponent.add(diceValue);
        }

        int numDiceAllowed = Math.min(gameMap.currentPlayer.diceValuesPlayer.size(), gameMap.currentPlayer.diceValuesOpponent.size());

        assertEquals(1, numDiceAllowed);

        gameMap.currentPlayer.checkVictory(selectedCountry, selectedNeighbour);
        assertEquals(14, selectedCountry.numOfArmies);
        assertEquals(0, selectedNeighbour.numOfArmies);
        assertEquals(selectedCountry.owner, selectedNeighbour.owner);

    }

    /**
     * Check the number of cards when a player wins countries in a round
     */
    @Test
    public void gainCard() {
        assertEquals(0, gameMap.currentPlayer.cards.size());
        gameMap.currentPlayer.hasConquered = true;
        gameMap.currentPlayer.gainCard();
        assertEquals(1, gameMap.currentPlayer.cards.size());
    }

    /**
     * check if the cards are transfered to another player if all the countries of player is captured
     */
    @Test
    public void winCards() {
        gameMap.players.get(3).addRandomCard();
        gameMap.players.get(3).addRandomCard();
        gameMap.players.get(3).addRandomCard();
        for (Country country : gameMap.players.get(3).countries) {
            gameMap.currentPlayer.countries.add(country);
        }
        gameMap.players.get(3).countries.clear();
        gameMap.currentPlayer.winCards(gameMap.players.get(3));
        assertEquals(0, gameMap.players.get(3).cards.size());
        assertEquals(3, gameMap.currentPlayer.cards.size());
    }

    /**
     * Checks that the country is conquered or not
     */
    @Test
    public void countryConquered() {
        List<Country> countriesAllowedToAttack = gameMap.currentPlayer.getCountriesAllowedToAttack();
        Country selectedCountry = countriesAllowedToAttack.get(1);
        Country selectedNeighbour = gameMap.countries.get(6);
        gameMap.currentPlayer.countryConquered(selectedCountry, selectedNeighbour);
        Assert.assertTrue(gameMap.currentPlayer.hasConquered);
        Assert.assertEquals(selectedNeighbour.owner, selectedCountry.owner);
    }

    /**
     * CHecks that we are getting strongest country or not
     */
    @Test
    public void getStrongestCountry() {
        Country strongestCountry = gameMap.currentPlayer.getStrongestCountry();
        Assert.assertEquals(8, strongestCountry.id);
    }

    /**
     * Checks there is proper fortification or not
     */
    @Test
    public void fortifySteps() {
        Country country1 = new Country(1, "China");
        Country country2 = new Country(2, "India");
        country1.numOfArmies = 6;
        country2.numOfArmies = 8;
        GameMap.getInstance().currentPlayer.fortifySteps(country1, country2);
        Assert.assertEquals(country1.numOfArmies, 1);
        Assert.assertEquals(country2.numOfArmies, 13);


    }

    /**
     * Checks that we are getting strongest countries list in descending order
     */
    @Test
    public void getStrongestCountries() {
        List<Country> strongestCountries = gameMap.currentPlayer.getStrongestCountries(2);
        Assert.assertEquals(8, strongestCountries.get(0).id);
        Assert.assertEquals(1, strongestCountries.get(1).id);
    }

    /**
     * Checks that we are getting weakest country or not
     */
    @Test
    public void getWeakestCountry() {
        Country weakestCountry = gameMap.currentPlayer.getWeakestCountry();
        Assert.assertEquals(5, weakestCountry.id);
    }

    /**
     * checking addition of random card
     */
    @Test
    public void addRandomCard() {
        gameMap.currentPlayer.addRandomCard();
        Assert.assertEquals(7, gameMap.getInstance().cardStack);
    }

    /**
     * gets the strongest country among the connected countries
     */
    @Test
    public void strongestInConnectedCountries() {
        gameMap.setDummyData();
        gameMap.countries.get(3).updateConnectedCountries();
        Country strongestCountry = gameMap.currentPlayer.strongestInConnectedCountries(gameMap.countries.get(3).connectedCountries);
        Assert.assertEquals(4, strongestCountry.id);
    }

}