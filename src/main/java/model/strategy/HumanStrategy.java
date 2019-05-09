package model.strategy;

import model.Country;
import model.GameMap;
import model.Player;

/**
 * This implement human strategy
 */
public class HumanStrategy implements PlayerStrategy {

    /**
     * To add the armies to the respective countries on click of Add button
     *
     * @param context      reference to player using this strategy
     * @param country      country to reinforce
     * @param armySelected number of armies
     */
    @Override
    public void reinforce(Player context, Country country, int armySelected) {
        GameMap.getInstance().setRecentMove(country.owner.name + " reinforced " +
                country + " with " + armySelected + " armies.");
        country.addArmies(armySelected);
        context.totalArmies -= armySelected;
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
        if (isAllOut) {
            GameMap.getInstance().setRecentMove(context.name + " started AllOut attack with " + selectedCountry
                    + " on " + selectedNeighbouringCountry);
            while (!context.countries.contains(selectedNeighbouringCountry) && selectedCountry.numOfArmies > 1) {
                context.performAttackSteps(selectedCountry, selectedNeighbouringCountry, true);
                attack(context, selectedCountry, selectedNeighbouringCountry, true);
            }
        } else {
            GameMap.getInstance().setRecentMove(context.name + " started Normal attack with " + selectedCountry
                    + " on " + selectedNeighbouringCountry);
            context.checkVictory(selectedCountry, selectedNeighbouringCountry);
        }
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
        GameMap.getInstance().setRecentMove(context.name + " tried to fortify From :" + countrySelected + " To : " + neighborSelected);

        GameMap.getInstance().setRecentMove(context.name + " fortified " + neighborSelected + " with " + numberOfArmiesTransfer
                + " armies from " + countrySelected);
        countrySelected.deductArmies(numberOfArmiesTransfer);
        neighborSelected.addArmies(numberOfArmiesTransfer);
    }
}
