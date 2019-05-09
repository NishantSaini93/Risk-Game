package model;

import com.google.gson.annotations.Expose;
import model.strategy.*;
import utility.FileHelper;

import java.util.*;

/**
 * Class containing attributes and functions of a player object
 * extends {@link Observable}
 */
public class Player extends Observable {

    /**
     * id for player
     */
    @Expose
    public int id;
    /**
     * Name for player
     */
    @Expose
    public String name;
    /**
     * Cards hold by player
     */
    @Expose
    public ArrayList<Card> cards;
    /**
     * strategy enum for serialization
     */
    @Expose
    public PlayerStrategy.Strategy strategy;
    /**
     * Strategy for the player
     */
    public PlayerStrategy playerStrategy;

    /**
     * HashMap for countries
     */
    public ArrayList<Country> countries;

    /**
     * Armies on trading card
     */
    public int updateArmiesForCards;

    /**
     * Reinforcement Panel will be updated or not on change
     */
    public boolean updateReinforcementPanel;

    /**
     * Total armies player gets during reinforcement
     */
    public int totalArmies;

    /**
     * Cards not selected by the player yet
     */
    public HashMap<String, Integer> unselectedCards;

    /**
     * Cards selected by the player
     */
    public ArrayList<Card> selectedCards;

    /**
     * dice values of player
     */
    public ArrayList<Integer> diceValuesPlayer;
    /**
     * dice values of opponent
     */
    public ArrayList<Integer> diceValuesOpponent;
    /**
     * the last dice rolled by the player
     */
    public int latestDiceRolled;
    /**
     * number of armies allowed to move if player wins
     */
    public int numArmiesAllowedToMove;
    /**
     * flag to check if player has conquered any country during the attack phase
     */
    public boolean hasConquered;
    /**
     * comparator function for sorting
     */
    private Comparator<Integer> diceComparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    };

    /**
     * Constructor
     * <p>
     * Sets up the various attributes of the player
     * </p>
     *
     * @param id       id of player
     * @param name     name of player
     * @param strategy playerStrategy for the player
     */
    public Player(int id, String name, PlayerStrategy.Strategy strategy) {
        this.id = id;
        this.name = name;
        this.strategy = strategy;
        assignStrategy(strategy);
        cards = new ArrayList<>();
        countries = new ArrayList<>();
        diceValuesPlayer = new ArrayList<>();
        diceValuesOpponent = new ArrayList<>();
        updateArmiesForCards = 5;
        selectedCards = new ArrayList<>();
        updateReinforcementPanel = false;
    }

    /**
     * Constructor
     * <p>
     * Sets up the various attributes of the player
     * </p>
     *
     * @param id   id of player
     * @param name name of player
     */
    public Player(int id, String name) {
        this(id, name, PlayerStrategy.Strategy.HUMAN);
    }

    /**
     * return Name and strategy
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        return name + "(" + strategy + ")";
    }

    /**
     * assigns Strategy by enum value
     *
     * @param strategy playerStrategy for player
     */
    public void assignStrategy(PlayerStrategy.Strategy strategy) {
        switch (strategy) {
            case AGGRESSIVE:
                playerStrategy = new AggressiveStrategy();
                break;
            case CHEATER:
                playerStrategy = new CheaterStrategy();
                break;
            case BENEVOLENT:
                playerStrategy = new BenevolentStrategy();
                break;
            case RANDOM:
                playerStrategy = new RandomStrategy();
                break;
            case HUMAN:
            default:
                playerStrategy = new HumanStrategy();
                break;
        }
    }

    /**
     * Checks if player is human or not
     *
     * @return true if Human else false
     */
    public boolean isHuman() {
        return strategy == PlayerStrategy.Strategy.HUMAN;
    }

    /**
     * Checks the country who has the largest army
     *
     * @return strongestCountry country which has the largest number of army
     */
    public Country getStrongestCountry() {
        Country strongestCountry = null;
        int largestArmy = 0;
        for (Country country : countries) {
            if (largestArmy < country.getNumberofArmies()) {
                largestArmy = country.getNumberofArmies();
                strongestCountry = country;
            }
        }
        return strongestCountry;
    }

    /**
     * Finds the country having lowest army
     *
     * @return weakest country
     */
    public Country getWeakestCountry() {
        Country weakestCountry = null;
        int smallestArmy = Integer.MAX_VALUE;
        for (Country country : countries) {
            if (smallestArmy > country.getNumberofArmies()) {
                smallestArmy = country.getNumberofArmies();
                weakestCountry = country;
            }
        }
        return weakestCountry;
    }

    /**
     * Sorts the countries according to the num of armies to get the strongest countries
     *
     * @param count number of countries you want
     * @return countries returns the list of countries according to the count
     */
    public List<Country> getStrongestCountries(Integer count) {
        countries.sort(new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {
                return (o2.numOfArmies - o1.numOfArmies);
            }
        });
        if (count == null) {
            return countries;
        }
        return countries.subList(0, count);

    }

    /**
     * Initializes countries to current player
     *
     * @param country instance of Country
     */
    public void initializeCountryToPlayer(Country country) {
        this.countries.add(country);
        if (GameMap.getInstance().check) {
            updateView();
        }

    }

    /**
     * Adds random cards
     */
    public void addRandomCard() {
        cards.add(new Card());
        GameMap.getInstance().cardStack -= 1;
    }

    /**
     * calculate the total armies from owned countries
     *
     * @return total army count
     */
    public int getTotalArmies() {
        int count = 0;
        for (Country country : countries) {
            count += country.numOfArmies;
        }
        return count;
    }

    /**
     * To calculate the total armies from the countries and continents own by the player for reinforcement phase
     *
     * @param countries  list of countries in the instance
     * @param continents list of continents in the instance
     * @return the calculated total armies
     */
    public int getTotalArmiesReinforce(HashMap<Integer, Country> countries, HashMap<Integer, Continent> continents) {
        int playerCountries = 0;
        for (Map.Entry<Integer, Country> entry : countries.entrySet()) {
            Country country = entry.getValue();
            if (country.owner.id == this.id) {
                playerCountries++;
            }
        }

        int playerContinentsControlVal = 0;
        boolean hasContinent;
        for (Map.Entry<Integer, Continent> entry : continents.entrySet()) {
            hasContinent = entry.getValue().isOwnedBy(this);
            if (hasContinent) {
                playerContinentsControlVal += entry.getValue().controlValue;
            }
        }

        int totalArmLocal = (playerCountries / 3) + playerContinentsControlVal;
        if (totalArmLocal < 3) {
            totalArmLocal = 3;
        }
        return totalArmLocal;
    }

    /**
     * To get the number of INFANTRY, ARTILLERY and CAVALRY cards the player has
     *
     * @return hashmap with values as the number of each INFANTRY, ARTILLERY and CAVALRY cards
     */
    public HashMap<String, Integer> getCardSetsOfPlayer() {
        ArrayList<Card> cards = this.cards;
        HashMap<String, Integer> cardSets = new HashMap<>();
        int infantry = 0;
        int artillery = 0;
        int cavalry = 0;
        for (Card card : cards) {
            if (card.type == Card.TYPE.INFANTRY) {
                infantry++;
            } else if (card.type == Card.TYPE.ARTILLERY) {
                artillery++;
            } else if (card.type == Card.TYPE.CAVALRY) {
                cavalry++;
            }
            cardSets.put("INFANTRY", infantry);
            cardSets.put("ARTILLERY", artillery);
            cardSets.put("CAVALRY", cavalry);
        }
        return cardSets;
    }

    /**
     * To set the list of cards available of a player in the unselectedCards attribute
     */
    public void setUnSelectedCards() {
        unselectedCards = getCardSetsOfPlayer();
        updateView();
    }

    /**
     * To set the total armies getting from getTotalArmies() method in totalArmies attribute
     */
    public void setArmiesForReinforcement() {
        if (totalArmies == 0)
            totalArmies = getTotalArmiesReinforce(GameMap.getInstance().countries, GameMap.getInstance().continents);
        updateView();
    }

    /**
     * To set the total armies getting from getTotalArmies() method in totalArmies attribute
     *
     * @param cardName name of card
     */
    public void addInSelectedCards(String cardName) {
        if (selectedCards.size() < 3) {
            if (unselectedCards.get(cardName) != null) {
                unselectedCards.replace(cardName, unselectedCards.get(cardName) - 1);
            }
            Card card = null;
            if (cardName.equalsIgnoreCase("ARTILLERY")) {
                card = new Card(Card.TYPE.ARTILLERY);
            } else if (cardName.equalsIgnoreCase("INFANTRY")) {
                card = new Card(Card.TYPE.INFANTRY);
            } else if (cardName.equalsIgnoreCase("CAVALRY")) {
                card = new Card(Card.TYPE.CAVALRY);
            }
            if (card != null) {
                GameMap.getInstance().setRecentMove(name + " added " + cardName + " card for exchange.");
                selectedCards.add(card);
            }
        }
        updateView();
    }

    /**
     * To reset the selected cards
     */
    public void resetSelectedCards() {
        GameMap.getInstance().setRecentMove(name + " reset his/her selected cards.");
        for (Card card : selectedCards) {
            if (unselectedCards.get(card.type.toString()) != null) {
                unselectedCards.replace(card.type.toString(), unselectedCards.get(card.type.toString()) + 1);
            }
        }
        selectedCards.clear();
        updateView();
    }

    /**
     * To empty the selected cards attribute
     */
    public void emptySelectedCards() {
        selectedCards.clear();
        updateView();
    }

    /**
     * To get the updated total armies when a set of three cards are changed
     *
     * @param totalArmiesLocal number of armies
     * @return the updated armies
     */
    public int exchangeCardsForArmies(int totalArmiesLocal) {
        GameMap.getInstance().setRecentMove(name + " exchanged cards for " + updateArmiesForCards + " armies.");
        FileHelper.writeLog(name + " exchanged cards for " + updateArmiesForCards + " armies.");
        totalArmies = totalArmiesLocal + this.updateArmiesForCards;
        this.updateArmiesForCards += 5;
        ArrayList<Card> removeCards = new ArrayList<>();
        for (Card cardSelected : selectedCards) {
            for (Card cardRemove : new ArrayList<>(cards)) {
                if (cardRemove.type == cardSelected.type && !removeCards.contains(cardRemove)) {
                    cards.remove(cardRemove);
                    break;
                }
            }
        }
        emptySelectedCards();
        return totalArmies;
    }

    /**
     * Function to update view
     */
    public void updateView() {
        setChanged();
        notifyObservers(this);
    }

    /**
     * Get the list of all countries owned by the player
     *
     * @return list of countries
     */
    public List<Country> getCountries() {
        return countries;
    }

    /**
     * Get the list of all countries owned by player which are eligible to attack
     *
     * @return list of countries
     */
    public List<Country> getCountriesAllowedToAttack() {
        ArrayList<Country> countriesAllowedToAttack = new ArrayList<>();
        for (Country country : countries) {
            if (country.getNumberofArmies() > 1) {
                countriesAllowedToAttack.add(country);
            }

        }
        return countriesAllowedToAttack;
    }

    /**
     * Rolls the dice and sorts their values
     *
     * @param playerNumDiceSelected   number of valid dice rolls allowed for player
     * @param opponentNumDiceSelected number of valid dice rolls allowed for opponent
     * @param isAllOut                a flag to check if the option is AllOut game mode
     */
    public void rollDice(int playerNumDiceSelected, int opponentNumDiceSelected, boolean isAllOut) {
        diceValuesPlayer.clear();
        diceValuesOpponent.clear();
        Random r = new Random();
        for (int i = 0; i < playerNumDiceSelected; i++) {
            diceValuesPlayer.add(r.nextInt(6) + 1);
        }

        for (int i = 0; i < opponentNumDiceSelected; i++) {
            diceValuesOpponent.add(r.nextInt(6) + 1);
        }

        Collections.sort(diceValuesPlayer, diceComparator);
        Collections.sort(diceValuesOpponent, diceComparator);
        if (!isAllOut) {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * steps to do when a country is conquered by attacker
     *
     * @param selectedCountry             country of the player
     * @param selectedNeighbouringCountry country of the opponent
     */
    public void countryConquered(Country selectedCountry, Country selectedNeighbouringCountry) {
        GameMap.getInstance().setRecentMove(name + " conquered " + selectedNeighbouringCountry);
        hasConquered = true;
        latestDiceRolled = diceValuesPlayer.size();
        numArmiesAllowedToMove = selectedCountry.numOfArmies - 1;
        winCards(selectedNeighbouringCountry.owner);
        selectedNeighbouringCountry.changeOwner(this);
        GameMap.getInstance().checkGameEnd();
        setChanged();
        notifyObservers();
    }

    /**
     * Check which country won and do the necessary deduction of armies and addition of cards
     *
     * @param selectedCountry             country of the player
     * @param selectedNeighbouringCountry country of the opponent
     */
    public void checkVictory(Country selectedCountry, Country selectedNeighbouringCountry) {
        int numConsideredDice = Math.min(diceValuesPlayer.size(), diceValuesOpponent.size());
        for (int i = 0; i < numConsideredDice; i++) {
            if (diceValuesPlayer.get(i) > diceValuesOpponent.get(i)) {
                GameMap.getInstance().setRecentMove(name + " won dice roll " + diceValuesPlayer.get(i)
                        + " to " + diceValuesOpponent.get(i));
                selectedNeighbouringCountry.deductArmies(1);
                int noArmies = selectedNeighbouringCountry.getNumberofArmies();
                if (noArmies == 0) {
                    countryConquered(selectedCountry, selectedNeighbouringCountry);
                }
            } else {
                GameMap.getInstance().setRecentMove(name + " lost dice roll " + diceValuesPlayer.get(i)
                        + " to " + diceValuesOpponent.get(i));
                selectedCountry.deductArmies(1);
            }
        }
    }

    /**
     * move cards from previous owner to current player if previous owner has zero countries
     *
     * @param prevOwner previous owner of a country
     */
    public void winCards(Player prevOwner) {
        if (prevOwner.countries.size() == 0) {
            GameMap.getInstance().setRecentMove(name + " won " + prevOwner.cards.size() + " from " +
                    prevOwner.name);
            for (Card card : prevOwner.cards) {
                cards.add(card);
            }
            prevOwner.cards.clear();
        }

    }

    /**
     * if a player has won any country during the attack add random card to the player
     */
    public void gainCard() {
        if (hasConquered) {
            if (GameMap.getInstance().cardStack > 0) {
                GameMap.getInstance().setRecentMove(name + " got a card for conquering atleast one country.");
                addRandomCard();
                hasConquered = false;
            } else {
                GameMap.getInstance().setRecentMove(name + " did not receive a card because no card available in stack.");
            }
        }
    }

    /**
     * To add the armies to the respective countries on click of Add button
     *
     * @param country      country to reinforce
     * @param armySelected number of armies
     */
    public synchronized void reinforce(Country country, int armySelected) {
        // in case of non-human player---make a default country and army to 0
        playerStrategy.reinforce(this, country, armySelected);
        updateView();
    }

    /**
     * Steps to be followed when allout mode is active
     *
     * @param selectedCountry             attacking country
     * @param selectedNeighbouringCountry defending country
     * @param isAllOut                    true if allOut mode else false
     */
    public void performAttackSteps(Country selectedCountry, Country selectedNeighbouringCountry, boolean isAllOut) {
        selectedCountry.updateNumOfDiceAllowed(false);
        selectedNeighbouringCountry.updateNumOfDiceAllowed(true);
        rollDice(selectedCountry.numOfDiceAllowed, selectedNeighbouringCountry.numOfDiceAllowed, isAllOut);
        checkVictory(selectedCountry, selectedNeighbouringCountry);
    }

    /**
     * Attack phase of the game
     *
     * @param selectedCountry             country of the player
     * @param selectedNeighbouringCountry country of the opponent
     * @param isAllOut                    flag to check the mode of the game
     */
    public synchronized void attack(Country selectedCountry, Country selectedNeighbouringCountry, boolean isAllOut) {
        playerStrategy.attack(this, selectedCountry, selectedNeighbouringCountry, isAllOut);
    }

    /**
     * Updates the armies of countries in which armies are transferred
     *
     * @param numberOfArmiesTransfer armies user select to transfer
     * @param countrySelected        country which user select transfer from
     * @param neighborSelected       country which user select transfer to
     */
    public synchronized void fortify(int numberOfArmiesTransfer, Country countrySelected, Country neighborSelected) {
        playerStrategy.fortify(this, numberOfArmiesTransfer, countrySelected, neighborSelected);

        setChanged();
        notifyObservers();
    }

    /**
     * Gets the strongest country among the list of countries provided
     *
     * @param listOfCountriesConnected hashmap of countries
     * @return the strongest country among the countries provided
     */
    public Country strongestInConnectedCountries(HashMap<Integer, Country> listOfCountriesConnected) {
        Iterator it = listOfCountriesConnected.entrySet().iterator();
        int largestArmy = 0;
        Country strongestCountry = null;
        while (it.hasNext()) {
            Map.Entry<Integer, Country> entry = (Map.Entry) it.next();
            Country country = entry.getValue();
            if (largestArmy < country.getNumberofArmies()) {
                largestArmy = country.getNumberofArmies();
                strongestCountry = country;
            }


        }
        return strongestCountry;
    }

    /**
     * performs the fortify steps of transfering army from one country to another
     *
     * @param countryToTransferFrom country from which we want to transfer
     * @param countryToTransferTo   country to which we want to transfer to
     */
    public void fortifySteps(Country countryToTransferFrom, Country countryToTransferTo) {
        GameMap.getInstance().setRecentMove(name + " tried to fortify From :" + countryToTransferFrom + " To : " + countryToTransferTo);

        int numberOfArmiesTransfer = countryToTransferFrom.numOfArmies - 1;

        GameMap.getInstance().setRecentMove(name + " fortified " + countryToTransferTo + " with " + numberOfArmiesTransfer
                + " armies from " + countryToTransferFrom);
        countryToTransferFrom.deductArmies(numberOfArmiesTransfer);
        countryToTransferTo.addArmies(numberOfArmiesTransfer);

    }

    /**
     * To exchange cards automatically
     */
    public void exchangeCardsAutomatically() {
        ArrayList<String> cardListToBeSelected = new ArrayList<>();
        boolean threeCards = false;
        for (Map.Entry pairCards : unselectedCards.entrySet()) {
            int cardNum = Integer.parseInt(pairCards.getValue().toString());
            if (cardNum >= 3) {
                for (int i = 0; i < 3; i++) {
                    addInSelectedCards(pairCards.getKey().toString());
                }
                threeCards = true;
                break;
            }
            cardListToBeSelected.add(pairCards.getKey().toString());
        }
        if (!threeCards) {
            for (String cardToBeSelected : cardListToBeSelected) {
                addInSelectedCards(cardToBeSelected);
            }
        }
        exchangeCardsForArmies(totalArmies);
    }
}
