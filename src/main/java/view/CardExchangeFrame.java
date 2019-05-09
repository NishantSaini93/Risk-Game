package view;

import controller.CardExchangeController;
import model.Card;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Gui part of Exchange View Frame
 * extends {@link JFrame}
 * implements {@link Observer}
 */

public class CardExchangeFrame extends JFrame implements Observer {

    /**
     * Panel for card section
     */
    JPanel cardSection;

    /**
     * Reference for Reinforcement controller
     */
    CardExchangeController cardExchangeController;

    /**
     * Number of the unselected cards
     */
    int unselectedCardsNum;

    /**
     * Array for selected cards
     */
    Card[] selectedCardsArray;

    /**
     * Cards sets of the player available for exchange
     */
    HashMap<String, Integer> unselectedCards;

    /**
     * Cards selected by the player for exchange
     */
    ArrayList<Card> selectedCards;

    /**
     * Total armies available for reinforcement
     */
    int totalArmies;

    /**
     * Constructor
     * <p>
     * To create the Frame for Exchanging cards in Reinforcement phase
     * </p>
     */
    public CardExchangeFrame() {
        super("Card Exchange View");
        cardExchangeController = new CardExchangeController(this);
        cardExchangeController.initialize();
        cardSection = new JPanel();
        cardSection.setLayout(new GridBagLayout());
        addCardSection();
        add(cardSection);


        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    /**
     * To update the details when the cards and armies are updated
     *
     * @param player the observable object.
     * @param obj    an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(java.util.Observable player, Object obj) {
        if (player instanceof Player) {
            unselectedCards = ((Player) player).unselectedCards;
            selectedCards = ((Player) player).selectedCards;
            totalArmies = ((Player) player).totalArmies;
            addCardSection();
        }
    }


    /**
     * To add CardSection Panel for cards' selection, updation and resetting
     */
    public void addCardSection() {
        if (cardSection != null) {
            cardSection.removeAll();
        } else {
            cardSection = new JPanel();
            cardSection.setLayout(new GridBagLayout());
        }

        selectedCardsArray = new Card[0];
        addUnselectedCardGrid();
        addSelectedCardGrid();
        addButtons();
        cardSection.revalidate();
        cardSection.repaint();
    }

    /**
     * To add Selected Cards Grid in CardSection Panel
     */
    public void addSelectedCardGrid() {
        if (selectedCards.size() > 0) {
            selectedCardsArray = new Card[selectedCards.size()];
            selectedCards.toArray(selectedCardsArray);
            JPanel cardsSelected = new JPanel();
            cardsSelected.setLayout(new GridLayout(3, 1));
            for (Card card : selectedCards) {
                JPanel cardButtonPanel = new JPanel();
                cardButtonPanel.setLayout(new GridLayout(1, 1));
                JLabel cardLabel = new JLabel(card.type.toString());
                cardButtonPanel.add(cardLabel);
                cardsSelected.add(cardButtonPanel);
            }
            cardSection.add(cardsSelected, getGridContraints(1, 0));
        }
    }

    /**
     * To add Unselected Cards Grid with add buttons in CardSection Panel
     */
    public void addUnselectedCardGrid() {
        unselectedCardsNum = 0;
        JPanel cardsUnselected = new JPanel();
        cardsUnselected.setLayout(new GridLayout(3, 1));
        Iterator itForCards = unselectedCards.entrySet().iterator();
        if (!itForCards.hasNext()) {
            JPanel cardButtonPanelArtillery = new JPanel();
            cardButtonPanelArtillery.setLayout(new GridLayout(1, 2));
            String labelA = "0 ARTILLERY";
            JLabel cardLabelA = new JLabel(labelA);
            cardButtonPanelArtillery.add(cardLabelA);
            JButton addA = new JButton("Add");
            addA.setEnabled(false);
            cardButtonPanelArtillery.add(addA);
            cardsUnselected.add(cardButtonPanelArtillery);

            JPanel cardButtonPanelInfantry = new JPanel();
            cardButtonPanelInfantry.setLayout(new GridLayout(1, 1));
            String labelI = "0 INFANTRY";
            JLabel cardLabelI = new JLabel(labelI);
            cardButtonPanelInfantry.add(cardLabelI);
            JButton addI = new JButton("Add");
            addI.setEnabled(false);
            cardButtonPanelInfantry.add(addI);
            cardsUnselected.add(cardButtonPanelInfantry);

            JPanel cardButtonPanelCavalry = new JPanel();
            cardButtonPanelCavalry.setLayout(new GridLayout(1, 1));
            String label = "0 CAVALRY";
            JLabel cardLabel = new JLabel(label);
            cardButtonPanelCavalry.add(cardLabel);
            JButton addC = new JButton("Add");
            addC.setEnabled(false);
            cardButtonPanelCavalry.add(addC);
            cardsUnselected.add(cardButtonPanelCavalry);
        } else {
            while (itForCards.hasNext()) {
                Map.Entry cardPair = (Map.Entry) itForCards.next();
                JPanel cardButtonPanel = new JPanel();
                cardButtonPanel.setLayout(new GridLayout(1, 2));
                String label = cardPair.getValue() + " " + cardPair.getKey();
                unselectedCardsNum += (Integer.parseInt(cardPair.getValue().toString()));
                JLabel cardLabel = new JLabel(label);
                cardButtonPanel.add(cardLabel);
                JButton add = new JButton("Add");
                add.addActionListener(cardExchangeController);
                add.setName("Add" + cardPair.getKey());
                if (Integer.valueOf(label.substring(0, 1)) < 1) {
                    add.setEnabled(false);
                } else {
                    add.setEnabled(true);
                }
                cardButtonPanel.add(add);
                cardsUnselected.add(cardButtonPanel);
            }
        }
        cardSection.add(cardsUnselected, getGridContraints(0, 0));
    }

    /**
     * To add panel for Update and Reset buttons in CardSection Panel
     */
    public void addButtons() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(2, 1));
        JButton update = new JButton("Update");
        update.setName("Update");
        update.addActionListener(cardExchangeController);
        if (selectedCardsArray != null && selectedCardsArray.length > 2
                && ((selectedCardsArray[0].type == selectedCardsArray[1].type && selectedCardsArray[0].type == selectedCardsArray[2].type && selectedCardsArray[1].type == selectedCardsArray[2].type)
                || (!(selectedCardsArray[0].type == selectedCardsArray[1].type) && !(selectedCardsArray[0].type == selectedCardsArray[2].type) && !(selectedCardsArray[1].type == selectedCardsArray[2].type)))) {
            update.setEnabled(true);
        } else {
            update.setEnabled(false);
        }
        buttons.add(update);
        JButton reset = new JButton("Reset");
        reset.setName("Reset");
        reset.addActionListener(cardExchangeController);
        buttons.add(reset);
        cardSection.add(buttons, getGridContraints(2, 0));
        JLabel whiteSpace1 = new JLabel("                    ");
        cardSection.add(whiteSpace1, getGridContraints(0, 1));
        JLabel armies = new JLabel("Armies: " + totalArmies);
        cardSection.add(armies, getGridContraints(0, 2));
        JLabel whiteSpace2 = new JLabel("                    ");
        cardSection.add(whiteSpace2, getGridContraints(0, 3));
        JButton exit = new JButton("Exit");
        exit.setName("exit");
        exit.addActionListener(cardExchangeController);
        cardSection.add(exit, getGridContraints(0, 4));

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
