package view;


import controller.ReinforcementController;
import model.Country;
import model.GameMap;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Gui part of Reinforcement Panel
 * extends {@link JPanel}
 * implements {@link Observer}
 */
public class ReinforcementPanel extends JPanel implements Observer {

    /**
     * Panel for card section
     */
    JPanel cardSection;

    /**
     * Panel for army section
     */
    JPanel armySection;

    /**
     * Reference for Reinforcement controller
     */
    ReinforcementController reinforcementController;

    /**
     * Drop down to select countries for placing armies
     */
    JComboBox<Country> countryList;

    /**
     * Drop down to select armies
     */
    JComboBox<Integer> armyList;

    /**
     * Number of the unselected cards
     */
    int cardsNum;

    /**
     * Cards sets of the player available for exchange
     */
    HashMap<String, Integer> cardSets = new HashMap<>();

    /**
     * Total armies available for reinforcement
     */
    int totalArmies = 0;

    /**
     * Countries player has during reinforcement
     */
    ArrayList<Country> countries = new ArrayList<>();


    /**
     * Constructor
     * <p>
     * To create the Panel for Reinforcement components
     * </p>
     */
    public ReinforcementPanel() {
        reinforcementController = new ReinforcementController(this);
        reinforcementController.initialize();

        setLayout(new GridBagLayout());

        cardSection = new JPanel();
        cardSection.setLayout(new GridBagLayout());
        addCardSection();
        add(cardSection, getGridContraints(0, 0));

        armySection = new JPanel();
        armySection.setLayout(new GridBagLayout());
        addArmySection();
        add(armySection, getGridContraints(0, 1));
    }

    /**
     * To update the details when the cards and armies are updated
     *
     * @param o   the observable object.
     * @param obj an argument passed to the <code>notifyObservers</code>
     */
    public void update(Observable o, Object obj) {
        if (!GameMap.getInstance().tournamentMode) {
            Player player = GameMap.getInstance().currentPlayer;
            boolean selectedObserver = ((Player) player).updateReinforcementPanel;
            if (selectedObserver) {
                cardSets = ((Player) player).unselectedCards;
                totalArmies = ((Player) player).totalArmies;
                countries = ((Player) player).countries;
            }
            addCardSection();
            addArmySection();
        }
    }

    /**
     * To update the Card Section and Army Section of the Reinforcement Panel
     */

    public void update() {
        reinforcementController.initialize();
        addCardSection();
        addArmySection();
        revalidate();
    }

    /**
     * To add CardSection Panel for cards' exchange
     */
    public void addCardSection() {
        if (cardSection != null) {
            cardSection.removeAll();
        } else {
            cardSection = new JPanel();
            cardSection.setLayout(new GridBagLayout());
        }
        addUnselectedCardGrid();
        addButtons();
        cardSection.revalidate();
        cardSection.repaint();
    }


    /**
     * To add Unselected Cards Grid with add buttons in CardSection Panel
     */
    public void addUnselectedCardGrid() {
        cardsNum = 0;
        JPanel cardsUnselected = new JPanel();
        cardsUnselected.setLayout(new GridLayout(3, 1));
        Iterator itForCards = cardSets.entrySet().iterator();
        if (!itForCards.hasNext()) {
            JPanel cardButtonPanelArtillery = new JPanel();
            cardButtonPanelArtillery.setLayout(new GridLayout(1, 1));
            String labelA = "0 ARTILLERY";
            JLabel cardLabelA = new JLabel(labelA);
            cardButtonPanelArtillery.add(cardLabelA);
            cardsUnselected.add(cardButtonPanelArtillery);

            JPanel cardButtonPanelInfantry = new JPanel();
            cardButtonPanelInfantry.setLayout(new GridLayout(1, 1));
            String labelI = "0 INFANTRY";
            JLabel cardLabelI = new JLabel(labelI);
            cardButtonPanelInfantry.add(cardLabelI);
            cardsUnselected.add(cardButtonPanelInfantry);

            JPanel cardButtonPanelCavalry = new JPanel();
            cardButtonPanelCavalry.setLayout(new GridLayout(1, 1));
            String label = "0 CAVALRY";
            JLabel cardLabel = new JLabel(label);
            cardButtonPanelCavalry.add(cardLabel);
            cardsUnselected.add(cardButtonPanelCavalry);

            cardsNum = 0;
        } else {
            while (itForCards.hasNext()) {
                Map.Entry cardPair = (Map.Entry) itForCards.next();
                JPanel cardButtonPanel = new JPanel();
                cardButtonPanel.setLayout(new GridLayout(1, 1));
                String label = cardPair.getValue() + " " + cardPair.getKey();
                cardsNum += (Integer.parseInt(cardPair.getValue().toString()));
                JLabel cardLabel = new JLabel(label);
                cardButtonPanel.add(cardLabel);
                cardsUnselected.add(cardButtonPanel);
            }
        }
        cardSection.add(cardsUnselected, getGridContraints(0, 0));
    }

    /**
     * To add panel for Exchange Cards in CardSection Panel
     */
    public void addButtons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 1));
        JButton exchangeCards = new JButton("Exchange Cards");
        exchangeCards.setName("exchangeCards");
        if (cardsNum == 0) {
            exchangeCards.setEnabled(false);
        } else {
            exchangeCards.setEnabled(true);
        }
        exchangeCards.addActionListener(reinforcementController);
        buttons.add(exchangeCards);
        cardSection.add(buttons, getGridContraints(2, 0));
    }

    /**
     * To add Army Section Panel for armies display and distribution among countries
     */
    public void addArmySection() {

        if (armySection != null) {
            armySection.removeAll();
        } else {
            armySection = new JPanel();
            armySection.setLayout(new GridBagLayout());
        }

        int armiesLeft = totalArmies;
        JPanel armyDisplay = new JPanel();
        JLabel armies = new JLabel("Armies: " + armiesLeft);
        armyDisplay.add(armies);
        armySection.add(armyDisplay, getGridContraints(0, 0));


        JPanel armiesChange = new JPanel(new GridBagLayout());
        List<Country> countriesOfPlayer = countries;
        countryList = new JComboBox<>();
        for (Country country : countriesOfPlayer) {
            countryList.addItem(country);
        }

        armiesChange.add(countryList, getGridContraints(0, 0));
        Integer[] addArmy = new Integer[armiesLeft];
        for (int loopForArmies = 1; loopForArmies <= armiesLeft; loopForArmies++) {
            addArmy[loopForArmies - 1] = loopForArmies;
        }
        armyList = new JComboBox<>(addArmy);
        armyList.setName("armyList");
        armiesChange.add(armyList, getGridContraints(1, 0));


        JButton changeArmies = new JButton("Add Armies");
        changeArmies.setName("changeArmies");
        changeArmies.addActionListener(reinforcementController);
        if (armiesLeft > 0 && cardsNum < 5) {
            changeArmies.setEnabled(true);
        } else {
            changeArmies.setEnabled(false);
        }
        armiesChange.add(changeArmies, getGridContraints(2, 0));
        armySection.add(armiesChange, getGridContraints(0, 2));
        if (cardsNum >= 5) {
            JLabel msgForCards = new JLabel("Cards cannot be greater than 5. Please exchange them.");
            armySection.add(msgForCards, getGridContraints(0, 3));
        }

        JButton proceed = new JButton("Proceed");
        proceed.setName("proceed");
        proceed.addActionListener(reinforcementController);
        if (armiesLeft > 0) {
            proceed.setVisible(false);
        } else {
            proceed.setVisible(true);
        }
        armySection.add(proceed, getGridContraints(0, 4));

        armySection.revalidate();
        armySection.repaint();

    }

    /**
     * To get the Selected Index for the Country List Combo Box in ArmySection Panel
     *
     * @return the index of selected country in the Country ComboBox
     */
    public Country getValueOfCountryIndexComboBox() {
        return (Country) countryList.getSelectedItem();
    }

    /**
     * To get the Selected Index for the Army List Combo Box in ArmySection Panel
     *
     * @return the item selected in the Army ComboBox
     */
    public int getValueOfArmyComboBox() {
        return (int) armyList.getSelectedItem();
    }

    /**
     * Set the constraints for GridBagLayout used
     *
     * @param x value for constraints gridx (row in the grid)
     * @param y value for constraints gridY (col in the grid)
     * @return default constraints (see {@link GridBagConstraints}) with provided x,y values
     */
    public GridBagConstraints getGridContraints(int x, int y) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        return gridBagConstraints;
    }
}
