package model.strategy;

import model.Country;
import model.GameMap;
import model.Player;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This implement Cheater Strategy
 */
public class CheaterStrategy implements PlayerStrategy {

    /**
     * To add the armies to the respective countries on click of Add button
     *
     * @param context      reference to player using this strategy
     * @param country      country to reinforce
     * @param armySelected number of armies
     */

    @Override
    public void reinforce(Player context, Country country, int armySelected) {
        ArrayList<Country> listOfcountries = context.countries;
        for (int i = 0; i < listOfcountries.size(); i++) {

            int armyAssigned = listOfcountries.get(i).numOfArmies;
            country = listOfcountries.get(i);
            country.addArmies(armyAssigned);
            GameMap.getInstance().setRecentMove(country.owner.name + " reinforced " + country + " with " + armyAssigned + "armies.");

        }
        GameMap.getInstance().changePhase(GameMap.Phase.ATTACK);

    }

    /**
     * Attacks the country by rolling dice.
     * <ol>
     * <li>rolls the dice</li>
     * <li>deducts army for a loss</li>
     * <li>check if game over</li>
     * <li>check if card gained</li>
     * </ol>
     *
     * @param context                     reference to player using this strategy
     * @param selectedCountry             country of the player
     * @param selectedNeighbouringCountry country of the opponent
     * @param isAllOut                    flag to check the mode of the game
     */

    @Override
    public void attack(Player context, Country selectedCountry, Country selectedNeighbouringCountry, boolean isAllOut) {
        ArrayList<Country> listOfcountries = context.countries;
        HashSet<Country> neighbouringCountries = new HashSet<Country>();
        for (int i = 0; i < listOfcountries.size(); i++) {
            selectedCountry = listOfcountries.get(i);
            neighbouringCountries.addAll(listOfcountries.get(i).getNeighboursDiffOwner());
        }

        for (Country neighbour : neighbouringCountries) {
            selectedNeighbouringCountry = neighbour;
            GameMap.getInstance().setRecentMove(context.name + " attacked " + selectedNeighbouringCountry);
            context.countryConquered(selectedCountry, selectedNeighbouringCountry);
        }
        GameMap.getInstance().changePhase(GameMap.Phase.FORTIFY);
    }

    /**
     * Updates the armies of countries in which armies are transferred
     *
     * @param context                reference to player using this strategy
     * @param numberOfArmiesTransfer armies user select to transfer
     * @param countrySelected        country which user select transfer from
     * @param neighborSelected       country which user select transfer to
     */
    @Override
    public void fortify(Player context, int numberOfArmiesTransfer, Country countrySelected, Country neighborSelected) {
        ArrayList<Country> listOfcountries = context.countries;
        for (int i = 0; i < listOfcountries.size(); i++) {
            countrySelected = listOfcountries.get(i);
            HashSet<Country> neighbouringCountries = listOfcountries.get(i).getNeighboursDiffOwner();
            if (!neighbouringCountries.isEmpty()) {
                GameMap.getInstance().setRecentMove(context.name + " fortified " + countrySelected.name + " with " + listOfcountries.get(i).numOfArmies);
                listOfcountries.get(i).addArmies(listOfcountries.get(i).numOfArmies);
            }
        }
        GameMap.getInstance().changePhase(GameMap.Phase.REINFORCE);
    }
}
