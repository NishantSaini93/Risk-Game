package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import model.GameMap;
import utility.FileHelper;
import utility.MapHelper;
import view.MainFrame;
import view.MapCreatorFrame;
import view.StartUpFrame;
import view.TournamentModeFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * This is the Controller for the MainFrame. see {@link BaseController}
 * implements {@link ActionListener} for MenuItems
 */
public class MainFrameController extends BaseController<MainFrame> implements
        ActionListener,
        Observer {

    /**
     * Controller for MainFrame
     *
     * @param view MainFrame view
     */
    public MainFrameController(MainFrame view) {
        super(view);
        model.addObserver(view);
        model.addObserver(this);
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
        if (model.currentPhase != model.previousPhase && model.stateHasChanged) {
            model.stateHasChanged = false;
            model.canSave = true;
            if (!model.gameEnded) {
                switch (model.currentPhase) {
                    case REINFORCE:
                        if (model.previousPhase == GameMap.Phase.STARTUP) {
                            view.setUpGamePanels();
                            model.resetCurrentPlayer();
                        } else if (model.previousPhase == GameMap.Phase.FORTIFY) {
                            model.changeToNextPlayer(true);
                            view.fortifyPanel.setVisible(false);
                            view.reinforcementPanel.setVisible(!model.tournamentMode);
                            view.reinforcementPanel.update();
                        }
                        if (!model.currentPlayer.isHuman()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    model.currentPlayer.reinforce(null, 0);
                                }
                            }).start();
                        }
                        break;
                    case ATTACK:
                        view.reinforcementPanel.setVisible(false);
                        view.attackPanel.setVisible(!model.tournamentMode);
                        view.attackPanel.revalidate();
                        view.attackPanel.update();
                        if (!model.currentPlayer.isHuman()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    model.currentPlayer.attack(null, null, false);
                                }
                            }).start();
                        }
                        break;
                    case FORTIFY:
                        view.attackPanel.setVisible(false);
                        view.fortifyPanel.setVisible(!model.tournamentMode);
                        view.fortifyPanel.update();
                        if (!model.currentPlayer.isHuman()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    model.currentPlayer.fortify(0, null, null);
                                }
                            }).start();
                        }
                        break;
                }
            }
        }
    }

    /**
     * This function is checking if the action is
     * Load countryGraph : loads the countryGraph using {@link JFileChooser}
     * Create countryGraph : creates the countryGraph and does validation
     * Exit : exits the game
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        boolean isLoadMap, isEditMap;
        isLoadMap = event.getActionCommand().equalsIgnoreCase("Load GameMap");
        isEditMap = event.getActionCommand().equalsIgnoreCase("Edit GameMap");
        if (isEditMap || isLoadMap) {
            File dir = new File("maps");
            dir.mkdir();
            JFileChooser file = new JFileChooser(dir);
            int confirmValue = file.showOpenDialog(null);

            if (confirmValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = file.getSelectedFile();
                if (isLoadMap) {
                    try {
                        FileHelper.loadToConfig(selectedFile);
                        if (MapHelper.validateContinentGraph() && MapHelper.validateMap()) {
                            new StartUpFrame();
                        } else {
                            FileHelper.emptyConfig();
                            JOptionPane.showMessageDialog(null, "File Validation Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                            System.out.println("File validation failed");
                        }
                    } catch (IllegalStateException exception) {

                        FileHelper.emptyConfig();
                        JOptionPane.showMessageDialog(null, exception.getMessage(), "Error Message", JOptionPane.ERROR_MESSAGE);
                        System.out.println("File validation failed : " + exception.getMessage());
                    }
                } else {
                    new MapCreatorFrame("Edit Map", selectedFile);
                }
            }
        } else if (event.getActionCommand().equalsIgnoreCase("Create GameMap")) {
            new MapCreatorFrame("Create Map");
        } else if (event.getActionCommand().equalsIgnoreCase("Save Game")) {
            if (model.canSave) {
                try {
                    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                    String data = gson.toJson(GameMap.getInstance());
                    FileHelper.saveGameToFile(data);
                    JOptionPane.showMessageDialog(null, "Game Saved", "", JOptionPane.ERROR_MESSAGE);
                } catch (JsonParseException e) {
                    System.out.println("Error saving game: " + e.getLocalizedMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please complete the current phase to save the game.", "Cannot save the game at this point", JOptionPane.ERROR_MESSAGE);
            }
        } else if (event.getActionCommand().equalsIgnoreCase("Load Game")) {
            File dir = new File("savedgames");
            dir.mkdir();
            JFileChooser file = new JFileChooser(dir);
            int confirmValue = file.showOpenDialog(null);

            if (confirmValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = file.getSelectedFile();
                try {
                    FileHelper.loadGame(selectedFile);
                    view.setUpGamePanels();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error loading saved game: " + e.getLocalizedMessage());
                }
            }
        } else if (event.getActionCommand().equalsIgnoreCase("exit")) {
            System.exit(0);
        } else if (event.getActionCommand().equalsIgnoreCase("Tournament Mode")) {
            new TournamentModeFrame();


        }
    }
}
