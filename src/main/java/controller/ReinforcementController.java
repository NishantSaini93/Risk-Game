package controller;


import model.Country;
import model.GameMap;
import view.CardExchangeFrame;
import view.ReinforcementPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the Controller class for reinforcement phase
 * extends the abstract class {@link BaseController}
 * implements {@link ActionListener} for actions performed on Gui part(Reinforcement panel)
 */
public class ReinforcementController extends BaseController<ReinforcementPanel> implements ActionListener {

    /**
     * Constructor for Reinforcement Controller
     * <p>
     * To initialize attributes
     * </p>
     *
     * @param reinforcementPanel the reinforcement panel attached to it
     */
    public ReinforcementController(ReinforcementPanel reinforcementPanel) {
        super(reinforcementPanel);
        model.addObserver(view);
    }

    /**
     * To initialize the values for Reinforcement Panel
     */
    public void initialize() {
        model.currentPlayer.addObserver(view);
        model.currentPlayer.updateReinforcementPanel = true;
        model.currentPlayer.setUnSelectedCards();
        model.currentPlayer.setArmiesForReinforcement();
    }

    /**
     * Invoked on any action performed on Exchange Cards button for Card Section in Reinforcement Panel
     * Invoked on any action performed on Change Armies button for Army Section in Reinforcement Panel
     * Invoked on any action performed on Proceed button to proceed to next phase for the player
     *
     * @param e {@link ActionEvent}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonName = ((JButton) e.getSource()).getName();
        if (buttonName.equalsIgnoreCase("exchangeCards")) {
            model.currentPlayer.updateReinforcementPanel = false;
            CardExchangeFrame cardExchangeFrame = new CardExchangeFrame();
            model.setRecentMove(model.currentPlayer.name + " started card exchange.");
            model.canSave = false;
        } else if (buttonName.equalsIgnoreCase("changeArmies")) {
            int numOfArmies = view.getValueOfArmyComboBox();
            Country selectedCountry = view.getValueOfCountryIndexComboBox();
            model.currentPlayer.reinforce(selectedCountry, numOfArmies);
            model.canSave = false;
        } else if (buttonName.equalsIgnoreCase("proceed")) {
            model.currentPlayer.totalArmies = 0;
            model.currentPlayer.deleteObserver(view);
            model.changePhase(GameMap.Phase.ATTACK);
            return;
        }

    }
}
