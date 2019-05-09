package view;

import controller.PlayerDominationController;
import model.GameMap;
import model.Player;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

import static javax.swing.BoxLayout.Y_AXIS;

/**
 * This class implements view of player domination
 */
public class PlayerDominationView extends JPanel implements Observer {

    /**
     * panel to hold the player information
     **/
    JPanel container;

    /**
     * controller for the view
     */
    PlayerDominationController controller;

    /**
     * Constructor of the class PlayerDominationView
     */
    public PlayerDominationView() {

        controller = new PlayerDominationController(this);

        setBackground(Color.LIGHT_GRAY);
        setBorder(new LineBorder(Color.BLACK, 2));
        setLayout(new BoxLayout(this, Y_AXIS));

        JLabel jLabelCountriesName = new JLabel("Domination");
        add(jLabelCountriesName);

        container = new JPanel();
        container.setBackground(Color.LIGHT_GRAY);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        add(container);
        update(null, null);
    }

    /**
     * update the player information
     *
     * @param players         player data
     * @param countriesOwned  list of countries owned corresponding to player
     * @param percentageOwned list of percentage domination corresponding to player
     * @param continentsOwned list of continents owned corresponding to player
     * @param numOfArmies     list of armies belonging to corresponding player
     */

    public void updateData(HashMap<Integer, Player> players, List<Integer> countriesOwned, List<Double> percentageOwned,
                           List<Integer> continentsOwned, List<Integer> numOfArmies) {
        container.removeAll();
        String[] data = new String[players.size()];
        int index = 0;
        for (Player player : players.values()) {
            data[index] = player.name + ": Countries: " + countriesOwned.get(index)
                    + " (" + String.format("%.2f", percentageOwned.get(index)) + "%)"
                    + " | Continents: " + continentsOwned.get(index)
                    + " | Armies: " + numOfArmies.get(index);
            index++;
        }
        JList<String> list = new JList<>(data);
        JScrollPane jScrollPaneCountries = new JScrollPane(list);
        container.add(jScrollPaneCountries);

        container.revalidate();
        container.repaint();
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        GameMap instance = GameMap.getInstance();
        HashMap<Integer, Player> players = instance.players;
        List<Integer> countriesOwned = instance.getNumberOfCountriesOwnedByPlayers();
        List<Double> percentageOwned = new ArrayList<>(countriesOwned.size());
        for (int i = 0; i < countriesOwned.size(); i++) {
            percentageOwned.add((((double) countriesOwned.get(i)) / instance.countries.size()) * 100);
        }
        List<Integer> continentsOwned = instance.getNumberOfContinentsOwnedByPlayers();
        List<Integer> armiesOwned = instance.getNumberOfArmiesOwnedByPlayers();
        updateData(players, countriesOwned, percentageOwned, continentsOwned, armiesOwned);
    }
}
