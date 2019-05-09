package model.strategy;

import model.Country;
import model.GameMap;
import model.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This implements the aggressive strategy
 */
public class AggressiveStrategy implements PlayerStrategy {

    /**
     * To add the armies to the respective countries on click of Add button
     *
     * @param context      reference to player using this strategy
     * @param country      country to reinforce
     * @param armySelected number of armies
     */
    @Override
    public void reinforce(Player context, Country country, int armySelected) {
        context.setUnSelectedCards();
        context.setArmiesForReinforcement();
        if (context.unselectedCards.size() > 4) {
            context.exchangeCardsAutomatically();
        }
        armySelected = context.totalArmies;
        country = context.getStrongestCountry();
        country.addArmies(armySelected);
        GameMap.getInstance().setRecentMove(country.owner.name + " reinforced " +
                country + " with " + armySelected + " armies.");
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
        List<Country> countriesList = context.getStrongestCountries(null);
        for (Country country : countriesList) {
            if (country.numOfArmies > 1 && country.getNeighboursDiffOwner().size() != 0) {
                selectedCountry = country;
                break;
            }
        }

        boolean countryChanged = true;
        Random rand = new Random();
        if (selectedCountry != null) {
            while (selectedCountry.numOfArmies > 1 && !selectedCountry.getNeighboursDiffOwner().isEmpty()) {
                if (countryChanged) {
                    int index = rand.nextInt(selectedCountry.getNeighboursDiffOwner().size());
                    selectedNeighbouringCountry = (Country) selectedCountry.getNeighboursDiffOwner().toArray()[index];

                    GameMap.getInstance().setRecentMove(context.name + " started AllOut attack with " + selectedCountry + " " + selectedCountry.numOfArmies
                            + " armies on " + selectedNeighbouringCountry + " " + selectedNeighbouringCountry.numOfArmies + " armies of " + selectedNeighbouringCountry.owner.name);

                    countryChanged = false;
                }

                context.performAttackSteps(selectedCountry, selectedNeighbouringCountry, true);

                if (selectedNeighbouringCountry.owner.equals(selectedCountry.owner) && selectedCountry.numOfArmies > 1) {
                    int armies = 1 + rand.nextInt(selectedCountry.numOfArmies - 1);
                    GameMap.getInstance().updateArmiesOfCountries(armies, selectedCountry, selectedNeighbouringCountry);
                    context.gainCard();
                    countryChanged = true;
                }
            }
        }
        GameMap.getInstance().changePhase(GameMap.Phase.FORTIFY);
    }

    /**
     * Updates the armies of countries in which armies are transferred
     *
     * @param context                reference to player using this strategy
     * @param numberOfArmiesTransfer armies user select to transfer
     * @param strongestCountry       country which user select transfer from
     * @param secondStrongestCountry country which user select transfer to
     */
    @Override
    public void fortify(Player context, int numberOfArmiesTransfer, Country strongestCountry, Country secondStrongestCountry) {
        int numberOfCountriesPlayerHas = context.countries.size();
        int count = 1;
        while (count <= numberOfCountriesPlayerHas) {
            List<Country> listOfCountries = context.getStrongestCountries(count);
            strongestCountry = listOfCountries.get(count - 1);
            strongestCountry.updateConnectedCountries();
            HashMap<Integer, Country> listOfCountriesConnected = strongestCountry.connectedCountries;
            if (listOfCountriesConnected.size() != 0) {
                secondStrongestCountry = context.strongestInConnectedCountries(listOfCountriesConnected);

                if (strongestCountry.numOfArmies != 1 && secondStrongestCountry != null) {
                    context.fortifySteps(strongestCountry, secondStrongestCountry);
                    break;
                }
            }
            count++;
        }
        GameMap.getInstance().changePhase(GameMap.Phase.REINFORCE);
    }
}
