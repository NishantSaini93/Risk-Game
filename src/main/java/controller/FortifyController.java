package controller;

import model.Country;
import model.GameMap;
import view.FortifyPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Controller for FortifyPanel
 */
public class FortifyController extends BaseController<FortifyPanel> implements ActionListener, ListSelectionListener, ItemListener {
    /**
     * Country selected by player
     */
    Country selectedCountry;
    /**
     * Neighboring country selected by player
     */
    Country selectedNeighbour;
    /**
     * Number of armies player want to fortify
     */
    int armiesToTransfer;

    /**
     * This is the constructor for the Controller
     *
     * @param view View associated with the Controller
     */
    public FortifyController(FortifyPanel view) {
        super(view);
        model.addObserver(view);
        model.currentPlayer.addObserver(view);
    }


    /**
     * Perform action when invoked
     * Get the value from ComboBox of number of armies user want to transfer
     * Update the hashmap of countries(Transfer the armies)
     * Update the value of number of armies in TextField after transfer
     *
     * @param e triggered after pressing button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("proceed")) {
            model.currentPlayer.deleteObserver(view);
            model.changePhase(GameMap.Phase.REINFORCE);
        } else if (e.getActionCommand().equalsIgnoreCase("Transfer")) {
            model.currentPlayer.fortify(armiesToTransfer, selectedCountry, selectedNeighbour);
            model.canSave = false;
            model.changePhase(GameMap.Phase.REINFORCE);
        }

    }

    /**
     * To get the number of armies at a country and its neighbor based on user selection
     * It calls function to get the list of countries to which a player can fortify from a selected country
     * Also calls function to calculate value for comboBox
     *
     * @param e event triggered after list item selected
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        JList<Country> source = (JList<Country>) e.getSource();
        if (e.getValueIsAdjusting()) {
            if (((JList<Country>) e.getSource()).getName().equalsIgnoreCase("Country")) {
                if (selectedCountry != null)
                    selectedCountry.deleteObserver(view);
                selectedCountry = source.getSelectedValue();
                selectedCountry.addObserver(view);
                selectedCountry.updateConnectedCountries();
            } else {
                if (selectedNeighbour != null)
                    selectedNeighbour.deleteObserver(view);
                selectedNeighbour = source.getSelectedValue();
                selectedNeighbour.addObserver(view);
                selectedNeighbour.updateTextFieldForNeighbour();
            }
        }
    }


    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     *
     * @param e event for state change
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            JComboBox jComboBox = (JComboBox) e.getSource();
            armiesToTransfer = (int) jComboBox.getSelectedItem();
        }

    }

    /**
     * Observer for the current player
     */
    public void observeCurrentPlayer() {
        model.currentPlayer.addObserver(view);
    }
}
