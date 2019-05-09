package model;

import com.google.gson.annotations.Expose;

import java.util.*;

/**
 * Class for Country containing access to all the components of country
 */
public class Country extends Observable {
    /**
     * Variable for state
     */
    public Update state = null;
    /**
     * Country id
     */
    @Expose
    public int id;
    /**
     * Country name
     */
    @Expose
    public String name;
    /**
     * Player that owns of country
     */
    @Expose
    public Player owner;
    /**
     * Number of armies at country
     */
    @Expose
    public int numOfArmies;
    /**
     * number of dice roll selected by a human owner
     */
    public int humanDiceSelection = 2;
    /**
     * Number of Dice allowed for a country
     */
    public int numOfDiceAllowed = 0;
    /**
     * flag for distinction
     */
    public int flagForObserver;
    /**
     * HashSet for Countries
     */
    public HashSet<Country> neighbours;
    /**
     * HashSet to save connected Country
     */
    public HashMap<Integer, Country> connectedCountries;
    /**
     * Variable for number of armies at selected connected country
     */
    int numberOfArmiesAtConnectedCountry;

    /**
     * Constructor to set current name and id of country
     *
     * @param id   id of country
     * @param name name of country
     */
    public Country(int id, String name) {
        connectedCountries = new HashMap<>();
        this.id = id;
        this.name = name;
        this.neighbours = new HashSet<>();
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return name + " (" + numOfArmies + ")";
    }

    /**
     * Get the list of Neighbour countries
     *
     * @return list of countries
     */
    public HashSet<Country> getNeighbours() {
        return neighbours;
    }

    /**
     * Get the list of Neighbour countries with different owner
     *
     * @return list of countries
     */
    public HashSet<Country> getNeighboursDiffOwner() {
        HashSet<Country> set = new HashSet<>();
        for (Country neighbour : neighbours) {
            if (!neighbour.owner.equals(owner)) {
                set.add(neighbour);
            }
        }
        return set;
    }

    /**
     * Gives the number of armies at a country
     *
     * @return number of army at that country
     */
    public int getNumberofArmies() {
        return numOfArmies;
    }

    /**
     * Assign army to country in startup phase
     *
     * @param numOfArmies no. of armies
     */
    public void setNumOfArmies(int numOfArmies) {
        GameMap.getInstance().setRecentMove(name + " was assigned " + numOfArmies + " armies.");
        this.numOfArmies = numOfArmies;
        setChanged();
        notifyObservers();
    }

    /**
     * This give updated connected countries based on selected country
     */
    public void updateConnectedCountries() {
        connectedCountries.clear();
        Queue<Country> queueNeighbor = new LinkedList<>();
        queueNeighbor.add(this);
        connectedCountries.put(id, this);
        while (!queueNeighbor.isEmpty()) {
            Country last = queueNeighbor.remove();
            HashSet<Country> listNeighbouring = last.getNeighbours();
            for (Country country : listNeighbouring) {
                if (country.owner.id == owner.id) {
                    if (connectedCountries.get(country.id) == null) {
                        queueNeighbor.add(country);
                        connectedCountries.put(country.id, country);
                    }

                }
            }
            connectedCountries.remove(id);

        }

        flagForObserver = 1;
        setChanged();
        notifyObservers();
    }

    /**
     * This function update the TextField for number of armies at selected connected country
     */
    public void updateTextFieldForNeighbour() {
        numberOfArmiesAtConnectedCountry = this.numOfArmies;
        flagForObserver = 2;
        setChanged();
        notifyObservers();
    }

    /**
     * computes the number of dice allowed during the attack phase
     *
     * @param isOpponent flag to check if it is opponent
     */
    public void updateNumOfDiceAllowed(boolean isOpponent) {
        numOfDiceAllowed = 0;
        int numArmies = this.getNumberofArmies();
        if (isOpponent) {
            if (numArmies >= 2) {
                numOfDiceAllowed = 2;
            } else if (numArmies == 1) {
                numOfDiceAllowed = 1;
            }
            if (owner.isHuman())
                numOfDiceAllowed = Math.min(numOfDiceAllowed, humanDiceSelection);
        } else {
            if (numArmies > 3) {
                numOfDiceAllowed = 3;
            } else {
                numOfDiceAllowed = numArmies - 1;
            }
        }
        state = Update.DICE;
        setChanged();
        notifyObservers();
    }

    /**
     * Change owner of the country
     *
     * @param newOwner player who won the country
     */
    public void changeOwner(Player newOwner) {
        owner.countries.remove(this);
        this.owner = newOwner;
        owner.countries.add(this);
        state = Update.OWNER;
        setChanged();
        notifyObservers();
    }

    /**
     * add and assign the new number of armies
     *
     * @param numOfArmies number of armies to be added
     */
    public void addArmies(int numOfArmies) {
        this.numOfArmies += numOfArmies;
        GameMap.getInstance().notifyChanges();
        state = Update.ARMY;
        setChanged();
        notifyObservers();
    }

    /**
     * deduct and assign the new number of armies
     *
     * @param numOfArmies number of armies to be deducted
     */
    public void deductArmies(int numOfArmies) {
        this.numOfArmies -= numOfArmies;
        GameMap.getInstance().notifyChanges();
        state = Update.ARMY;
        setChanged();
        notifyObservers();
    }

    /**
     * @see java.lang.Enum to check what to observe
     */
    public enum Update {
        ARMY,
        OWNER,
        DICE
    }
}
