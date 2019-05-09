package view;

import controller.AttackController;
import model.Country;
import model.GameMap;
import model.Player;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

/**
 * View for attackPanel extends {@link JPanel}
 */
public class AttackPanel extends JPanel implements Observer {
    /**
     * Panel for displaying the dice
     */
    public JPanel dicePanel;
    /**
     * Panel for displaying the result of dice roll
     */
    public JPanel resultPanel;
    /**
     * Panel for moving the army
     */
    public JPanel moveArmyPanel;
    /**
     * Select box to choose between game mode
     */
    public JComboBox<String> selectMode;
    /**
     * Select box for choosing no. of dice to roll for player
     */
    public JComboBox<Integer> playerDice;
    /**
     * Select box for choosing no. of dice to roll for opponent
     */
    public JComboBox<Integer> opponentDice;
    /**
     * Select box for choosing no. of armies to transfer
     */
    public JComboBox<Integer> armyToMove;
    /**
     * shows the result of dice roll of player
     */
    public JLabel resultPlayer;
    /**
     * shows the result of dice roll of opponent
     */
    public JLabel resultOpponent;
    /**
     * Controller for AttackPanel
     */
    AttackController attackController;
    /**
     * Panel for displaying Countries owned by current player
     */
    private JPanel countryPanel;
    /**
     * Select box to choose between players' country for attacking
     */
    private JComboBox<Country> countries;
    /**
     * Select box to choose between players' neighbouring countries to attack
     */
    private JComboBox<Country> neighbouringCountries;
    /**
     * Panel for displaying neighboring countries to selected country to which fortify can done
     */
    private JPanel neighbouringPanel;
    /**
     * button to move the armies
     */
    private JButton moveButton;
    /**
     * Button to attack neighbouring countries
     */
    private JButton attackButton;

    /**
     * Button to proceed to next part of game
     */
    private JButton proceedButton;

    /**
     * Constructor
     * <p>
     * Sets up country panel and neighbouring country panel using {@link JPanel}
     * Updates the country list in the view using {@link AttackController} updateCountryList function
     */
    public AttackPanel() {
        attackController = new AttackController(this);

        setBackground(Color.LIGHT_GRAY);
        setBorder(new LineBorder(Color.BLACK, 2));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel countriesHolder = new JPanel();
        countriesHolder.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        countryPanel = new JPanel();
        countryPanel.setBorder(new LineBorder(Color.black, 1));
        countryPanel.setLayout(new BoxLayout(countryPanel, BoxLayout.Y_AXIS));
        countries = new JComboBox<>();
        countries.setName("selectCountry");
        countries.addItemListener(attackController);
        countryPanel.add(countries);

        neighbouringPanel = new JPanel();
        neighbouringPanel.setBorder(new LineBorder(Color.black, 1));
        neighbouringPanel.setLayout(new BoxLayout(neighbouringPanel, BoxLayout.Y_AXIS));
        neighbouringCountries = new JComboBox<>();
        neighbouringCountries.setName("selectNeighbourCountry");
        neighbouringCountries.addItemListener(attackController);
        neighbouringPanel.add(neighbouringCountries);
        countriesHolder.add(countryPanel);
        countriesHolder.add(neighbouringPanel);


        JPanel optionsHolder = new JPanel();
        optionsHolder.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        selectMode = new JComboBox<>(new String[]{"Choose Dice", "All out"});
        selectMode.addItemListener(attackController);
        selectMode.setName("mode");

        dicePanel = new JPanel();
        dicePanel.setLayout(new BoxLayout(dicePanel, BoxLayout.Y_AXIS));
        playerDice = new JComboBox();
        opponentDice = new JComboBox();
        dicePanel.add(new JLabel("Player:"));
        dicePanel.add(playerDice);
        dicePanel.add(new JLabel("Opponent:"));
        dicePanel.add(opponentDice);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPlayer = new JLabel();
        resultOpponent = new JLabel();
        resultPanel.add(new JLabel("Player:"));
        resultPanel.add(resultPlayer);
        resultPanel.add(new JLabel("Opponent:"));
        resultPanel.add(resultOpponent);

        optionsHolder.add(selectMode);
        optionsHolder.add(dicePanel);
        optionsHolder.add(resultPanel);

        armyToMove = new JComboBox();
        moveButton = new JButton("Move");
        moveButton.setName("move");
        moveButton.addActionListener(attackController);
        moveArmyPanel = new JPanel();
        moveArmyPanel.add(new JLabel("Army to move:"));
        moveArmyPanel.add(armyToMove);
        moveArmyPanel.add(moveButton);


        attackButton = new JButton("Attack");
        attackButton.setName("attack");
        attackButton.addActionListener(attackController);


        add(countriesHolder);
        add(optionsHolder);
        add(attackButton);
        add(moveArmyPanel);


        proceedButton = new JButton("Proceed");
        proceedButton.setName("proceed");
        proceedButton.addActionListener(attackController);
        add(proceedButton);


        moveArmyPanel.hide();

    }

    /**
     * Adds list of countries to the country panel
     *
     * @param countries collection of countries
     */
    public void showCountries(Collection<Country> countries) {
        this.countries.removeAllItems();
        for (Country country : countries) {
            this.countries.addItem(country);
        }
        revalidate();
    }

    /**
     * Initialize the players and gamesetup
     */
    public void update() {
        attackController.initialize();
        showCountries(GameMap.getInstance().currentPlayer.getCountriesAllowedToAttack());
        selectMode.setSelectedIndex(0);
        resultOpponent.setText("");
        resultPlayer.setText("");
        moveArmyPanel.hide();
    }

    /**
     * Adds list of countries to the neighbouring panel
     *
     * @param countries collection of countries
     */
    public void updateNeighbouringCountries(Collection<Country> countries) {
        neighbouringCountries.removeAllItems();
        for (Country country : countries) {
            if (country.owner.id != attackController.model.currentPlayer.id) {
                neighbouringCountries.addItem(country);
            }
        }
        if (neighbouringCountries.getItemCount() == 0) {
            attackButton.hide();
            dicePanel.hide();
        } else {
            attackButton.show();
            dicePanel.show();
        }
        revalidate();
    }

    /**
     * updates the dropdown list for choosing no. of dice to roll for player
     *
     * @param numOfDiceAllowed number of dice allowed to roll
     * @param allOut           flag to check the mode of game
     */
    public void updatePlayerDiceDropdown(int numOfDiceAllowed, boolean allOut) {
        if (!allOut) {
            this.playerDice.removeAllItems();

        }
        int counter = (allOut == true) ? numOfDiceAllowed : 0;
        for (int i = counter; i < numOfDiceAllowed; i++) {
            playerDice.addItem(i + 1);
        }
    }

    /**
     * updates the dropdown list for choosing no. of dice to roll for opponent
     *
     * @param numOfDiceAllowed number of dice allowed to roll
     * @param allOut           flag to check the mode of game
     */
    public void updateOpponentDiceDropdown(int numOfDiceAllowed, boolean allOut) {
        if (!allOut) {
            this.opponentDice.removeAllItems();
        }
        int counter = (allOut) ? numOfDiceAllowed : 0;
        for (int i = counter; i < Math.min(2, numOfDiceAllowed); i++) {
            opponentDice.addItem(i + 1);
        }
    }

    /**
     * gives the option to move army from one country to another
     *
     * @param minArmyToMove mininum number of army you have to moce
     * @param maxArmyToMove maximum number of army you can move
     */
    public void updateMoveArmyPanel(int minArmyToMove, int maxArmyToMove) {
        this.armyToMove.removeAllItems();
        for (int i = minArmyToMove; i <= maxArmyToMove; i++) {
            armyToMove.addItem(i);
        }
    }

    /**
     * sets the result of the dice roll of player
     *
     * @param diceValues result of dice roll
     */
    public void updateResultPlayer(String diceValues) {
        resultPlayer.setText(diceValues);
    }

    /**
     * sets the result of the dice roll of opponent
     *
     * @param diceValues result of dice roll
     */
    public void updateResultOpponent(String diceValues) {
        resultOpponent.setText(diceValues);
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
                Country country = ((Country) o);
                if (country.id == attackController.selectedCountry.id) {
                    switch (country.state) {
                        case ARMY:
                            country.updateNumOfDiceAllowed(false);

                            if (country.numOfArmies == 1) {
                                showCountries(attackController.model.currentPlayer.getCountriesAllowedToAttack());
                                updateNeighbouringCountries(attackController.selectedCountry.neighbours);
                            }
                            break;
                        case DICE:
                            boolean isAllOut = selectMode.getSelectedItem().equals("All out") ? true : false;
                            updatePlayerDiceDropdown(country.numOfDiceAllowed, isAllOut);
                            break;
                    }
                } else {
                    switch (country.state) {
                        case OWNER:
                            moveArmyPanel.show();
                            attackButton.hide();
                            break;
                        case ARMY:
                            country.updateNumOfDiceAllowed(true);
                        default:
                            boolean isAllOut = selectMode.getSelectedItem().equals("All out") ? true : false;
                            updateOpponentDiceDropdown(country.numOfDiceAllowed, isAllOut);
                            break;
                    }
                }
            } else if (o instanceof Player) {
                Player player = ((Player) o);
                updateResultPlayer(player.diceValuesPlayer.toString());
                updateResultOpponent(player.diceValuesOpponent.toString());
                updateMoveArmyPanel(player.latestDiceRolled, player.numArmiesAllowedToMove);

            } else if (o instanceof GameMap) {
                GameMap map = ((GameMap) o);
                if (map.gameEnded && !map.tournamentMode) {
                    showAlert();
                }
            }
        }

    }

    /**
     * shows game end alert to user
     */
    private void showAlert() {
        int action = JOptionPane.showOptionDialog(null, GameMap.getInstance().currentPlayer.name + " won!!!", "Game Ended", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if (action == JOptionPane.OK_OPTION) {
            System.exit(0);
        }
    }


}
