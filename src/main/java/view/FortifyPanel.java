package view;

import controller.FortifyController;
import model.Country;
import model.GameMap;
import model.Player;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;

/**
 * Class containing Gui and functionality of Fortify part of Game
 */
public class FortifyPanel extends JPanel implements Observer {
    /**
     * Reference for Fortify Controller
     */
    FortifyController fortifyController;
    /**
     * TextField to display army on selected country
     */
    JTextField jTextFieldNoOfArmiesCountries;
    /**
     * TextField to display army on
     */
    JTextField jTextFieldNoOfArmiesNeighbour;
    /**
     * ScrollPane to display the neighboring countries to which a player can fortify from a selected country
     */
    JScrollPane scrollPaneNeighboringCountries;
    /**
     * ComboBox to show and allow user to select how many armies he want to transfer
     */
    JComboBox jComboBoxNoOfArmies;
    /**
     * Button to transfer army
     */
    JButton jButtonTransfer;
    /**
     * Label for number of armies at neighboring country selected by player
     */
    JLabel jLabelArmiesAtNeighbor;
    /**
     * Label for number of armies at country selected by player
     */
    JLabel jLabelArmiesAtCountries;
    /**
     * Variable to get armies of selected country
     */
    Country updateSelectedCountryArmy;
    /**
     * Variable to get armies of selected connected country
     */
    Country updateConnectedCountryArmy;
    /**
     * Panel for adding components for country list of current player
     */
    private JPanel jPanelCountries;
    /**
     * Panel for adding components for neighboring country list of current player
     */
    private JPanel jPanelNeighbors;
    /**
     * Panel for adding components for displaying armies at country and neighboring country selected by player
     */
    private JPanel jPanelDisplayArmies;
    /**
     * Panel that contains add ComboBox ,Proceed button and Transfer button
     */
    private JPanel jPanelTransferArmy;
    /**
     * Layout for the panel which will contain all other panels
     */
    private GridBagLayout gridBagLayoutMain;
    /**
     * Constraints for panel which will contain all panel
     */
    private GridBagConstraints bagConstraintsMain;
    /**
     * Button that will proceed the game
     */
    private JButton proceedButton;


    /**
     * Constructor
     * It set up the Panels for Number of countries a player have and countries to which the can transfer army
     * Also set up the panels for choosing number of armies to transfer
     */
    public FortifyPanel() {
        fortifyController = new FortifyController(this);

        gridBagLayoutMain = new GridBagLayout();
        bagConstraintsMain = new GridBagConstraints();
        setLayout(gridBagLayoutMain);
        setBackground(Color.LIGHT_GRAY);
        setBorder(new LineBorder(Color.BLACK, 2));

        JLabel jLabelFortify = new JLabel("Fortify");
        jLabelFortify.setFont(new Font("Fortify", Font.BOLD, 20));
        bagConstraintsMain.fill = GridBagConstraints.VERTICAL;
        bagConstraintsMain.gridwidth = 2;
        bagConstraintsMain.gridx = 1;
        bagConstraintsMain.gridy = 0;
        add(jLabelFortify, bagConstraintsMain);

        bagConstraintsMain.fill = GridBagConstraints.VERTICAL;
        jPanelCountries = new JPanel();
        bagConstraintsMain.gridwidth = 1;
        bagConstraintsMain.gridx = 0;
        bagConstraintsMain.gridy = 1;
        add(jPanelCountries, bagConstraintsMain);

        bagConstraintsMain.fill = GridBagConstraints.VERTICAL;
        jPanelNeighbors = new JPanel();
        bagConstraintsMain.gridx = 1;
        bagConstraintsMain.gridy = 1;
        add(jPanelNeighbors, bagConstraintsMain);
        jPanelDisplayArmies = new JPanel();

        bagConstraintsMain.fill = GridBagConstraints.VERTICAL;
        bagConstraintsMain.gridx = 2;
        bagConstraintsMain.gridy = 1;
        add(jPanelDisplayArmies, bagConstraintsMain);
        jPanelTransferArmy = new JPanel();

        bagConstraintsMain.fill = GridBagConstraints.VERTICAL;
        bagConstraintsMain.gridx = 3;
        bagConstraintsMain.gridy = 1;
        add(jPanelTransferArmy, bagConstraintsMain);

        showNeighbouringCountriesFortify();

    }

    /**
     * It shows countries that are owned by currently playing player
     *
     * @param countries countries own by current player
     */
    public void showCountriesFortify(Collection<Country> countries) {
        jPanelCountries.removeAll();
        GridBagLayout gridBagLayoutCountriesPanel = new GridBagLayout();
        GridBagConstraints gridBagConstraintsCountriesPanel = new GridBagConstraints();

        jPanelCountries.setLayout(gridBagLayoutCountriesPanel);
        gridBagConstraintsCountriesPanel.fill = GridBagConstraints.VERTICAL;
        gridBagConstraintsCountriesPanel.gridx = 0;
        gridBagConstraintsCountriesPanel.gridy = 0;
        JLabel jLabelCountries = new JLabel("Countries");
        jPanelCountries.add(jLabelCountries, gridBagConstraintsCountriesPanel);
        JList countriesList = new JList(countries.toArray());
        countriesList.setName("Country");
        JScrollPane scrollPaneCountries = new JScrollPane(countriesList);
        gridBagConstraintsCountriesPanel.gridx = 0;
        gridBagConstraintsCountriesPanel.gridy = 1;
        jPanelCountries.add(scrollPaneCountries, gridBagConstraintsCountriesPanel);
        countriesList.addListSelectionListener(fortifyController);
        jPanelCountries.revalidate();
        revalidate();
        repaint();
    }

    /**
     * It set up the component for displaying neighboring countries
     * It takes current player's countries as a default
     */
    public void showNeighbouringCountriesFortify() {
        jPanelNeighbors.removeAll();
        GridBagLayout gridBagLayoutCountriesPanel = new GridBagLayout();
        GridBagConstraints gridBagConstraintsCountriesPanel = new GridBagConstraints();

        jPanelNeighbors.setLayout(gridBagLayoutCountriesPanel);
        gridBagConstraintsCountriesPanel.fill = GridBagConstraints.VERTICAL;
        gridBagConstraintsCountriesPanel.gridx = 0;
        gridBagConstraintsCountriesPanel.gridy = 0;
        jPanelNeighbors.removeAll();
        jPanelNeighbors.revalidate();
        jPanelNeighbors.repaint();
        JLabel jLabelCountries = new JLabel("Neighbours");
        jPanelNeighbors.add(jLabelCountries, gridBagConstraintsCountriesPanel);
        JList list = new JList();
        scrollPaneNeighboringCountries = new JScrollPane(list);
        gridBagConstraintsCountriesPanel.gridx = 0;
        gridBagConstraintsCountriesPanel.gridy = 1;
        jPanelNeighbors.add(scrollPaneNeighboringCountries, gridBagConstraintsCountriesPanel);
        jPanelNeighbors.revalidate();
        revalidate();
    }


    /**
     * It setup the components to show number of armies in a county and its neighboring countries
     * It also allow to choose number of armies one can transfer from a country to other country
     */
    public void transferFortify() {
        jPanelDisplayArmies.removeAll();
        jPanelTransferArmy.removeAll();

        GridBagLayout gridBagLayoutInnerPanelArmies = new GridBagLayout();
        GridBagConstraints gridBagConstraintsInnerPanelArmies = new GridBagConstraints();
        jPanelDisplayArmies.setLayout(gridBagLayoutInnerPanelArmies);
        gridBagConstraintsInnerPanelArmies.fill = GridBagConstraints.VERTICAL;
        gridBagConstraintsInnerPanelArmies.gridx = 0;
        gridBagConstraintsInnerPanelArmies.gridy = 0;
        jLabelArmiesAtCountries = new JLabel("Army at Country");
        jPanelDisplayArmies.add(jLabelArmiesAtCountries, gridBagConstraintsInnerPanelArmies);
        gridBagConstraintsInnerPanelArmies.gridx = 0;
        gridBagConstraintsInnerPanelArmies.gridy = 1;
        jTextFieldNoOfArmiesCountries = new JTextField(10);
        jTextFieldNoOfArmiesCountries.setEditable(false);
        jPanelDisplayArmies.add(jTextFieldNoOfArmiesCountries, gridBagConstraintsInnerPanelArmies);
        gridBagConstraintsInnerPanelArmies.gridx = 0;
        gridBagConstraintsInnerPanelArmies.gridy = 2;
        jLabelArmiesAtNeighbor = new JLabel("Army at neighbor");
        jPanelDisplayArmies.add(jLabelArmiesAtNeighbor, gridBagConstraintsInnerPanelArmies);
        jTextFieldNoOfArmiesNeighbour = new JTextField(10);
        jTextFieldNoOfArmiesNeighbour.setEditable(false);
        gridBagConstraintsInnerPanelArmies.gridx = 0;
        gridBagConstraintsInnerPanelArmies.gridy = 3;
        jPanelDisplayArmies.add(jTextFieldNoOfArmiesNeighbour, gridBagConstraintsInnerPanelArmies);
        jPanelDisplayArmies.revalidate();

        GridBagLayout gridBagLayoutTransferArmy = new GridBagLayout();
        GridBagConstraints transferPanelConstraints = new GridBagConstraints();
        jPanelTransferArmy.setLayout(gridBagLayoutTransferArmy);
        transferPanelConstraints.fill = GridBagConstraints.VERTICAL;
        jComboBoxNoOfArmies = new JComboBox();
        transferPanelConstraints.gridx = 0;
        transferPanelConstraints.gridy = 0;
        jPanelTransferArmy.add(new JLabel("Select No. of armies to transfer"), transferPanelConstraints);
        transferPanelConstraints.gridx = 0;
        transferPanelConstraints.gridy = 1;
        jPanelTransferArmy.add(jComboBoxNoOfArmies, transferPanelConstraints);
        transferPanelConstraints.gridx = 0;
        transferPanelConstraints.gridy = 2;
        jComboBoxNoOfArmies.setName("ComboBox");
        jComboBoxNoOfArmies.addItemListener(fortifyController);
        jButtonTransfer = new JButton("Transfer");
        jButtonTransfer.addActionListener(fortifyController);
        jPanelTransferArmy.add(jButtonTransfer, transferPanelConstraints);
        jPanelTransferArmy.revalidate();

        proceedButton = new JButton("Proceed");
        proceedButton.setName("proceed");
        proceedButton.addActionListener(fortifyController);
        transferPanelConstraints.gridx = 0;
        transferPanelConstraints.gridy = 3;
        jPanelTransferArmy.add(proceedButton, transferPanelConstraints);

        revalidate();
    }

    /**
     * updates the view with latest content
     */
    public void update() {
        fortifyController.observeCurrentPlayer();
        revalidate();
        repaint();
    }

    /**
     * Update the value of number of armies on currently selected country
     *
     * @param numberOfArmies new value for TextField
     */
    public void updateCountriesArmyTextField(int numberOfArmies) {
        jTextFieldNoOfArmiesCountries.setText(Integer.toString(numberOfArmies));

    }

    /**
     * Update the value of Armies for selected Neighboring country
     *
     * @param numberOfArmies new value for TextField
     */
    public void updateNeighboringCountriesArmyTextField(int numberOfArmies) {
        jTextFieldNoOfArmiesNeighbour.setText(Integer.toString(numberOfArmies));
    }

    /**
     * Update the values of ComboBox to select number of armies to transfer
     *
     * @param numberOfArmies maximum number of army one can transfer
     */
    public void updateJComboboxArmies(int numberOfArmies) {
        jComboBoxNoOfArmies.removeAllItems();
        for (int i = 1; i < numberOfArmies; i++) {
            jComboBoxNoOfArmies.addItem(i);
        }
        jComboBoxNoOfArmies.revalidate();


    }

    /**
     * Updates the list of neighboring countries on the basis of selection
     *
     * @param neighbor contains the id's and names of neighboring countries
     */
    public void updateNeighboringCountries(HashMap<Integer, Country> neighbor) {
        Country[] neighborCountriesList = new Country[neighbor.keySet().size()];
        int index = 0;
        for (Map.Entry getCountries : neighbor.entrySet()) {
            neighborCountriesList[index] = (Country) getCountries.getValue();
            index++;
        }
        JList neighborUpdateList = new JList(neighborCountriesList);
        neighborUpdateList.setName("Neigbhor");
        scrollPaneNeighboringCountries.setViewportView(neighborUpdateList);
        neighborUpdateList.addListSelectionListener(fortifyController);

    }

    /**
     * Function to enable Button of transfer
     */
    public void enableButton() {
        jButtonTransfer.setEnabled(true);
    }

    /**
     * Function to disable the button of transfer
     */
    public void disableButton() {
        jButtonTransfer.setEnabled(false);
        jButtonTransfer.doClick();
    }

    /**
     * Function to Start displaying Neighboring countries panel
     */
    public void setVisibleTrueNeighbourPanel() {
        jPanelNeighbors.setVisible(true);
    }

    /**
     * Function to stop displaying Neighboring countries panel
     */
    public void setVisibleFalseNeighbourPanel() {
        jPanelNeighbors.setVisible(false);
    }

    /**
     * Function to disable ComboBox
     * Hide Neighbor army TextField
     */
    public void setComboBoxAndNeighborTextFieldFalse() {
        jComboBoxNoOfArmies.setEnabled(false);
        jLabelArmiesAtNeighbor.setVisible(false);
        jTextFieldNoOfArmiesNeighbour.setVisible(false);

    }

    /**
     * Function to enable ComboBox
     * Show Neighbor army TextField
     */
    public void setNeighborTextFieldTrue() {
        jLabelArmiesAtNeighbor.setVisible(true);
        jTextFieldNoOfArmiesNeighbour.setVisible(true);

    }

    /**
     * Hide label and TextField for CountriesArmy
     */
    public void disableCountriesArmyLabelAndTextField() {
        jTextFieldNoOfArmiesCountries.setVisible(false);
        jLabelArmiesAtCountries.setVisible(false);
    }

    /**
     * Show label and TextField for CountriesArmy
     */
    public void enableCountriesArmyLabelAndTextField() {
        jTextFieldNoOfArmiesCountries.setVisible(true);
        jLabelArmiesAtCountries.setVisible(true);
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
        if (!GameMap.getInstance().tournamentMode) {
            if (o instanceof Country) {
                Country country = (Country) o;

                if (country.flagForObserver == 1) {
                    enableCountriesArmyLabelAndTextField();
                    updateCountriesArmyTextField(country.numOfArmies);
                    updateSelectedCountryArmy = country;
                    updateNeighboringCountries(country.connectedCountries);
                    if (country.connectedCountries.isEmpty()) {
                        disableButton();
                        setVisibleFalseNeighbourPanel();
                        setComboBoxAndNeighborTextFieldFalse();
                    } else {
                        setVisibleTrueNeighbourPanel();
                        disableButton();
                        setComboBoxAndNeighborTextFieldFalse();
                        updateJComboboxArmies(country.numOfArmies);

                    }
                } else if (country.flagForObserver == 2) {
                    if (updateSelectedCountryArmy.numOfArmies <= 1) {
                        jComboBoxNoOfArmies.setEnabled(false);
                        disableButton();
                    } else {
                        jComboBoxNoOfArmies.setEnabled(true);
                        enableButton();
                    }
                    setNeighborTextFieldTrue();
                    updateNeighboringCountriesArmyTextField(country.numOfArmies);
                    updateConnectedCountryArmy = country;

                }


            } else if (o instanceof GameMap) {
                GameMap gameMap = GameMap.getInstance();
                showCountriesFortify(gameMap.currentPlayer.getCountries());
                transferFortify();
                disableButton();
                setVisibleFalseNeighbourPanel();
                setComboBoxAndNeighborTextFieldFalse();
                disableCountriesArmyLabelAndTextField();
            } else if (o instanceof Player) {
                if (updateSelectedCountryArmy != null && updateConnectedCountryArmy != null) {
                    jTextFieldNoOfArmiesCountries.setText(String.valueOf(updateSelectedCountryArmy.numOfArmies));
                    jTextFieldNoOfArmiesNeighbour.setText(String.valueOf(updateConnectedCountryArmy.numOfArmies));
                }

            }
        }
    }
}
