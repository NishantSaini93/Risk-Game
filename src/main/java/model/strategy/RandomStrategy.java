package model.strategy;

import model.Country;
import model.GameMap;
import model.Player;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * This class implements Random Strategy
 */
public class RandomStrategy implements PlayerStrategy {

    /**
     * To add the armies to the respective countries on click of Add button
     *
     * @param context      reference to player using this strategy
     * @param country      country to reinforce
     * @param armySelected number of armies
     */
    @Override
    public void reinforce(Player context, Country country, int armySelected) {
        Random random = new Random();
        context.setUnSelectedCards();
        context.setArmiesForReinforcement();
        if (context.unselectedCards.size() > 4) {
            context.exchangeCardsAutomatically();
        }
        armySelected = context.totalArmies;
        while (armySelected > 0) {
            country = context.countries.get(random.nextInt(context.countries.size()));
            int armyAssigned;
            if (armySelected == 1) {
                armyAssigned = 1;
            } else {
                armyAssigned = random.nextInt(armySelected - 1) + 1;
            }
            country.addArmies(armyAssigned);
            armySelected -= armyAssigned;
            GameMap.getInstance().setRecentMove(country.owner.name + " reinforced " +
                    country + " with " + armyAssigned + " armies.");
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
        int count = 0;
        while (count < 5) {
            count++;
            Random rand = new Random();
            selectedCountry = context.countries.get(rand.nextInt(context.countries.size()));
            if (selectedCountry.getNumberofArmies() == 1 || selectedCountry.getNeighboursDiffOwner().size() == 0) {
                continue;
            }
            int index = rand.nextInt(selectedCountry.getNeighboursDiffOwner().size());

            selectedNeighbouringCountry = (Country) selectedCountry.getNeighboursDiffOwner().toArray()[index];

            int limit = 1 + rand.nextInt(10);
            for (int i = 0; i < limit; i++) {
                GameMap.getInstance().setRecentMove(context.name + " started Normal attack with " + selectedCountry
                        + " on " + selectedNeighbouringCountry);

                if (selectedNeighbouringCountry.owner.isHuman()) {
                    String[] options;
                    if (selectedNeighbouringCountry.numOfArmies > 2)
                        options = new String[]{"1", "2"};
                    else
                        options = new String[]{"1"};

                    int diceSelection = 1 + JOptionPane.showOptionDialog(null, "Select the number of dice to roll.", "",
                            JOptionPane.YES_NO_OPTION, 1, null, options, 1);
                    selectedNeighbouringCountry.humanDiceSelection = diceSelection;
                }

                context.performAttackSteps(selectedCountry, selectedNeighbouringCountry, false);
                if (selectedNeighbouringCountry.owner.equals(selectedCountry.owner) && selectedCountry.getNumberofArmies() >= 1) {
                    int armies = 1 + rand.nextInt(selectedCountry.numOfArmies - 1);
                    GameMap.getInstance().updateArmiesOfCountries(armies, selectedCountry, selectedNeighbouringCountry);
                    context.gainCard();
                    break;
                }
            }
            break;
        }
        GameMap.getInstance().changePhase(GameMap.Phase.FORTIFY);
    }

    /**
     * Updates the armies of countries in which armies are transferred
     *
     * @param context                reference to player using this strategy
     * @param numberOfArmiesTransfer armies user select to transfer
     * @param getFirstCountry        country which user select transfer from
     * @param secondRandomCountry    country which user select transfer to
     */
    @Override
    public void fortify(Player context, int numberOfArmiesTransfer, Country getFirstCountry, Country
            secondRandomCountry) {
        ArrayList<Country> countriesOwnedByCurrentPlayer = new ArrayList<>(context.countries);
        Random r1 = new Random(Calendar.getInstance().getTimeInMillis());
        getFirstCountry = countriesOwnedByCurrentPlayer.get(r1.nextInt(countriesOwnedByCurrentPlayer.size()));
        countriesOwnedByCurrentPlayer.remove(getFirstCountry);
        if (getFirstCountry != null && getFirstCountry.numOfArmies > 1 && !countriesOwnedByCurrentPlayer.isEmpty()) {
            int index = r1.nextInt(countriesOwnedByCurrentPlayer.size());
            Country getSecondArmyCountry = countriesOwnedByCurrentPlayer.get(index);
            GameMap.getInstance().setRecentMove(context.name + " tried to fortify From :" + getFirstCountry + " To : " + getSecondArmyCountry);
            getFirstCountry.updateConnectedCountries();
            HashMap<Integer, Country> listOfCountriesConnected = getFirstCountry.connectedCountries;
            if (listOfCountriesConnected.get(getSecondArmyCountry.id) != null) {
                numberOfArmiesTransfer = r1.nextInt(getFirstCountry.numOfArmies - 1);
                GameMap.getInstance().setRecentMove(context.name + " fortified " + getFirstCountry + " with " + numberOfArmiesTransfer
                        + " armies from " + getSecondArmyCountry);
                getFirstCountry.deductArmies(numberOfArmiesTransfer);
                getSecondArmyCountry.addArmies(numberOfArmiesTransfer);
            }
        }
        GameMap.getInstance().changePhase(GameMap.Phase.REINFORCE);
    }
}
