package controller;

import view.CardExchangeFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the Controller class for card exchange frame
 * extends the abstract class {@link BaseController}
 * implements {@link ActionListener} for actions performed on Gui part(CardExchange Frame)
 */
public class CardExchangeController extends BaseController<CardExchangeFrame> implements ActionListener {
    /**
     * Constructor for Card Exchange Controller
     * <p>
     * To initialize attributes
     * </p>
     *
     * @param cardExchangeFrame the card exchange view attached to it
     */
    public CardExchangeController(CardExchangeFrame cardExchangeFrame) {
        super(cardExchangeFrame);

    }

    /**
     * To initialize the values for cards exchange frame
     */
    public void initialize() {
        model.currentPlayer.addObserver(view);
        model.currentPlayer.setUnSelectedCards();
        model.currentPlayer.setArmiesForReinforcement();
        model.currentPlayer.emptySelectedCards();

    }


    /**
     * Invoked on any action performed on Add, Update, Reset and Exit buttons for Card Section in Exchange Cards View
     *
     * @param e {@link ActionEvent}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonName = ((JButton) e.getSource()).getName();

        if (buttonName.substring(0, 3).equalsIgnoreCase("ADD")) {
            String cardName = buttonName.substring(3);
            model.currentPlayer.addInSelectedCards(cardName);
        } else if (buttonName.equalsIgnoreCase("Update")) {
            model.currentPlayer.exchangeCardsForArmies(model.currentPlayer.totalArmies);
        } else if (buttonName.equalsIgnoreCase("Reset")) {
            model.currentPlayer.resetSelectedCards();
        } else if (buttonName.equalsIgnoreCase("exit")) {
            model.setRecentMove(model.currentPlayer.name + " closed card exchange.");
            view.setVisible(false);
            model.currentPlayer.emptySelectedCards();
            model.currentPlayer.updateReinforcementPanel = true;
            model.currentPlayer.setArmiesForReinforcement();
            initialize();
        }

    }
}
