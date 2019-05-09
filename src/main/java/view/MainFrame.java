package view;

import controller.MainFrameController;
import model.GameMap;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Main window of the game
 */
public class MainFrame extends JFrame implements Observer {

    /**
     * Reference to continentPanel class
     */
    public ContinentPanel continentPanel;
    /**
     * Reference to countryPanel class
     */
    public CountryPanel countryPanel;
    /**
     * Reference to reinforcementPanel class
     */
    public ReinforcementPanel reinforcementPanel;
    /**
     * Reference to attackPanel class
     */
    public AttackPanel attackPanel;
    /**
     * Reference to fortifyPanel class
     */
    public FortifyPanel fortifyPanel;
    /**
     * Label that display current playing player
     */
    public JLabel currentPlayer;
    /**
     * Label that display game mode
     */
    public JLabel gameMode;
    /**
     * Label that display current game
     */
    public JLabel currentGame;
    /**
     * Label that display current round
     */
    public JLabel currentRound;
    /**
     * Label that display current phase
     */
    public JLabel currentPhase;
    /**
     * panel that display recentMove
     */
    public JPanel recentMovesPanel;
    /**
     * scroll container for recent moves
     */
    public JScrollPane messagesPanel;

    /**
     * Panel that will contain all other panel
     */
    private JPanel mainPanel;
    /**
     * Reference for MainFrameController
     */
    private MainFrameController controller;

    /**
     * Constructor for MainFrame class
     * Setup the main panel
     */
    public MainFrame() {
        super("Risk Game - SOEN 6441 - Team 19");

        controller = new MainFrameController(this);
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        mainContainer.add(initPhaseViewPanel());
        mainContainer.add(mainPanel);
        add(mainContainer);

        setUpMenuBar();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 700);
        setVisible(true);
    }

    /**
     * Set up the phase view panel
     *
     * @return phase view panel
     */
    private JPanel initPhaseViewPanel() {
        gameMode = new JLabel("Game Mode: ");
        currentGame = new JLabel("Current Game: ");
        currentRound = new JLabel("Current Round: ");
        currentPlayer = new JLabel("Current Player: ");
        currentPhase = new JLabel("Current Phase: ");
        recentMovesPanel = new JPanel();
        recentMovesPanel.setLayout(new BoxLayout(recentMovesPanel, BoxLayout.Y_AXIS));
        JPanel phaseViewPanel = new JPanel();
        phaseViewPanel.setLayout(new BoxLayout(phaseViewPanel, BoxLayout.Y_AXIS));
        phaseViewPanel.add(gameMode);
        phaseViewPanel.add(currentGame);
        phaseViewPanel.add(currentRound);
        phaseViewPanel.add(currentPhase);
        phaseViewPanel.add(currentPlayer);
        phaseViewPanel.add(new JLabel("Recent Moves: "));
        messagesPanel = new JScrollPane(recentMovesPanel);
        messagesPanel.setPreferredSize(new Dimension(0, 150));
        phaseViewPanel.add(messagesPanel);

        return phaseViewPanel;
    }

    /**
     * Adds all necessary panel components to the main panel
     */
    public void setUpGamePanels() {
        mainPanel.removeAll();
        continentPanel = new ContinentPanel();
        mainPanel.add(continentPanel, getConstraints(0, 1));

        countryPanel = new CountryPanel();
        mainPanel.add(countryPanel, getConstraints(0, 2));

        reinforcementPanel = new ReinforcementPanel();
        mainPanel.add(reinforcementPanel, getConstraints(1, 1));

        attackPanel = new AttackPanel();
        attackPanel.setVisible(false);
        mainPanel.add(attackPanel, getConstraints(1, 1));

        fortifyPanel = new FortifyPanel();
        fortifyPanel.setVisible(false);
        mainPanel.add(fortifyPanel, getConstraints(1, 1));

        PlayerDominationView dominationPanel = new PlayerDominationView();
        mainPanel.add(dominationPanel, getConstraints(1, 2));

        mainPanel.revalidate();
        mainPanel.repaint();
    }


    /**
     * Creates {@link GridBagConstraints} with provided gridX and gridY values
     *
     * @param x value for constraints gridx (row in the grid)
     * @param y value for constraints gridY (col in the grid)
     * @return default constraints (see {@link GridBagConstraints}) with provided x,y values
     */
    private GridBagConstraints getConstraints(int x, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        return constraints;
    }

    /**
     * Adds menu bar to the frame with following options:\n
     * create countryGraph | load countryGraph | exit
     */
    private void setUpMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");

        JMenuItem tournament = new JMenuItem("Tournament Mode");
        tournament.addActionListener(controller);
        menuFile.add(tournament);
        JMenuItem load = new JMenuItem("Load GameMap");
        load.addActionListener(controller);
        menuFile.add(load);
        JMenuItem create = new JMenuItem("Create GameMap");
        menuFile.add(create);
        create.addActionListener(controller);
        JMenuItem edit = new JMenuItem("Edit GameMap");
        menuFile.add(edit);
        JMenuItem saveGame = new JMenuItem("Save Game");
        saveGame.addActionListener(controller);
        menuFile.add(saveGame);
        JMenuItem loadGame = new JMenuItem("Load Game");
        loadGame.addActionListener(controller);
        menuFile.add(loadGame);
        edit.addActionListener(controller);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(controller);
        menuFile.add(exit);

        menuBar.add(menuFile);

        setJMenuBar(menuBar);
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
        if (o instanceof GameMap) {
            if (recentMovesPanel != null) {
                GameMap instance = GameMap.getInstance();
                if (!currentPhase.getText().contains(instance.currentPhase.toString())) {
                    recentMovesPanel.removeAll();
                }
                gameMode.setText("Game Mode: " + (instance.tournamentMode ? "Tournament" : "Normal"));
                currentGame.setText("Current Game: " + (!instance.tournamentMode ? "Map 1 : Game: 1" : "Map:" + instance.mapBeingPlayed + " | Game: " + instance.gameNumberBeingPlayed));
                currentRound.setText("Current Round : " + instance.loopForGameBeingPlayed);
                currentPhase.setText("Current Phase : " + instance.currentPhase.toString());
                currentPlayer.setText("Current Player: " + instance.currentPlayer.name);

                if (instance.recentMove != null)
                    recentMovesPanel.add(new JLabel(instance.recentMove));

                recentMovesPanel.revalidate();
                recentMovesPanel.repaint();
            }
        }
    }
}
