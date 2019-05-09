package controller;


import model.Country;
import model.GameMap;
import model.Player;
import model.strategy.PlayerStrategy;
import view.StartUpFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class contain functionality to start up game
 */
public class StartUpController extends BaseController<StartUpFrame> implements ActionListener, ItemListener {
    /**
     * HashSet for number of submitted player
     */
    HashSet<Player> completedPlayers;

    /**
     * This is controller constructor for Startup
     *
     * @param view associated with controller
     */
    public StartUpController(StartUpFrame view) {
        super(view);
        completedPlayers = new HashSet<>();
        model.addObserver(view);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e event for the action performed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("submit")) {
            model.players.clear();
            completedPlayers.clear();
            int index = 1;
            for (Component component : view.playersPanel.getComponents()) {
                Component[] c = ((JPanel) component).getComponents();
                String name = ((JTextField) c[0]).getText();
                PlayerStrategy.Strategy strategy = (PlayerStrategy.Strategy) ((JComboBox<PlayerStrategy.Strategy>) c[1]).getSelectedItem();
                Player player = new Player(index, name, strategy);
                model.players.put(player.id, player);
                if (strategy != PlayerStrategy.Strategy.HUMAN)
                    completedPlayers.add(player);
                index++;
            }

            model.loopForGameBeingPlayed = 0;
            model.setPlayersForCountingLoop(model.players);

            model.assignCountriesToPlayers();
            List<Player> completedList = new ArrayList<>(completedPlayers);
            model.distributeInitialArmiesRandomly(completedList);

            if (!changeCurrentPlayer()) {
                model.deleteObserver(view);
                view.dispose();
                model.changePhase(GameMap.Phase.REINFORCE);
            }
        } else if (e.getActionCommand().equalsIgnoreCase("assign")) {
            if (view.playerCountries.getSelectedItem() != null) {
                Country country = (Country) view.playerCountries.getSelectedItem();
                int armies = ((Integer) view.numOfArmies.getSelectedItem());
                country.setNumOfArmies(armies);
            }
            if (view.playerCountries.getItemCount() == 1)
                completedPlayers.add(model.currentPlayer);

            if (!changeCurrentPlayer()) {
                model.deleteObserver(view);
                view.dispose();
                model.changePhase(GameMap.Phase.REINFORCE);
            }
        }
    }

    /**
     * Invoked when an item has been selected or deselected by the user.
     * The code written for this method performs the operations
     * that need to occur when an item is selected (or deselected).
     *
     * @param e event
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Integer count = (Integer) ((JComboBox<Integer>) e.getSource()).getSelectedItem();
            view.updatePlayersPanel(count);
        }
    }

    /**
     * Function for current player change
     *
     * @return true, false
     */
    private boolean changeCurrentPlayer() {
        if (completedPlayers.size() == model.players.size())
            return false;

        model.changeToNextPlayer(false);
        if (!completedPlayers.contains(model.currentPlayer)) {
            return true;
        } else {
            return changeCurrentPlayer();
        }
    }
}
