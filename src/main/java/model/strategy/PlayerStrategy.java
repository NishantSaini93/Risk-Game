package model.strategy;

import model.Country;
import model.Player;

/**
 * Strategy interface for player
 */
public interface PlayerStrategy {

    /**
     * To add the armies to the respective countries on click of Add button
     *
     * @param context      reference to player using this strategy
     * @param country      country to reinforce
     * @param armySelected number of armies
     */
    void reinforce(Player context, Country country, int armySelected);

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
    void attack(Player context, Country selectedCountry, Country selectedNeighbouringCountry, boolean isAllOut);

    /**
     * Updates the armies of countries in which armies are transferred
     *
     * @param context                reference to player using this strategy
     * @param numberOfArmiesTransfer armies user select to transfer
     * @param countrySelected        country which user select transfer from
     * @param neighborSelected       country which user select transfer to
     */
    void fortify(Player context, int numberOfArmiesTransfer, Country countrySelected, Country neighborSelected);

    /**
     * enum for the types of strategies
     */
    enum Strategy {
        HUMAN("Human"),
        AGGRESSIVE("Aggressive"),
        BENEVOLENT("Benevolent"),
        RANDOM("Random"),
        CHEATER("Cheater");

        /**
         * String representation for the enum value
         */
        private String name;

        Strategy(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
