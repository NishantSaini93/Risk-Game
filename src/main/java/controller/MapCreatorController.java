package controller;

import utility.FileHelper;
import utility.MapHelper;
import view.MapCreatorFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * This is the Controller for the MapCreator. see {@link BaseController}
 * implements {@link ActionListener} for MenuItems
 */
public class MapCreatorController extends BaseController<MapCreatorFrame> implements ActionListener {

    /**
     * File to save the content into
     */
    File saveFile;


    /**
     * This is the constructor for the Controller
     *
     * @param view View associated with the Controller
     */
    public MapCreatorController(MapCreatorFrame view) {
        super(view);
    }

    /**
     * load contents from file to edit
     *
     * @param file to load contents from
     */
    public void loadDataFromFile(File file) {
        this.saveFile = file;
        HashMap<String, List<String>> data = FileHelper.loadToForm(file);

        List<String> countryData = data.get("country");
        List<String> continentData = data.get("continent");

        view.fillFormWithData(file.getName().split("\\.")[0], continentData, countryData);
    }

    /**
     * Invoked when an action occurs.
     *
     * @param event action event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        if (((Component) event.getSource()).getName().equals("numOfContinents")
                || ((Component) event.getSource()).getName().equals("numOfCountries")) {

            handleValueChange(((JComboBox<Integer>) event.getSource()));

        } else if (((Component) event.getSource()).getName().equals("Save")) {

            saveToFile();
        }

    }

    /**
     * save the form data to file
     */
    public void saveToFile() {
        try {
            String mapName = view.mapName.getText();
            HashSet<String> continentCheckList = new HashSet<>();
            HashSet<String> countryCheckList = new HashSet<>();
            HashSet<String> neighboursCheckList = new HashSet<>();

            ArrayList<String> continents = new ArrayList<>();
            for (JTextField field : view.getContinentFields()) {
                String text = field.getText().trim();
                if (!text.isEmpty()) {
                    String[] data = text.replace(" ", "").split("=");

                    if (data.length != 2) throw new IllegalStateException("CV not provided");
                    int cv = Integer.valueOf(data[1]);

                    if (!continentCheckList.add(data[0]))
                        throw new IllegalArgumentException("Same continent name added twice");

                    continents.add(text);
                }
            }

            ArrayList<String> countries = new ArrayList<>();
            for (JTextField field : view.getCountryFields()) {
                String text = field.getText().trim();
                if (!text.isEmpty()) {
                    String[] data = text.replace(" ", "").split(",");


                    if (data.length <= 1)
                        throw new IllegalStateException("No continent provided for a country");
                    if (data.length <= 2)
                        throw new IllegalStateException("No neighbours provided for a country");
                    if (!countryCheckList.add(data[0]))
                        throw new IllegalArgumentException("Same country name added twice");
                    if (!continentCheckList.contains(data[1]))
                        throw new IllegalStateException("Invalid continent associated with a country.");

                    for (int i = 2; i < data.length; i++) {
                        neighboursCheckList.add(data[i]);
                    }
                    countries.add(text);
                }
            }

            if (countryCheckList.size() != neighboursCheckList.size())
                throw new IllegalStateException("Not all the country has a neighbour");

            validateFormData(continents, countries);

            if (saveFile == null) {
                File dir = new File("maps");
                dir.mkdirs();
                saveFile = new File("maps/" + mapName + ".map");
            }
            FileHelper.saveMapToFile(saveFile, continents, countries);
            view.dispose();
        } catch (NumberFormatException nfe) {
            view.showWarning("Not a valid number");
        } catch (IllegalStateException ise) {
            view.showWarning(ise.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            view.showWarning(e.getMessage());
        }
    }

    /**
     * Validates the form data to check if its a valid game map
     *
     * @param continents list of continent items
     * @param countries  list of country items
     * @throws IllegalStateException for invalid map
     */
    public void validateFormData(List<String> continents, List<String> countries) throws IllegalStateException {
        if (continents.isEmpty() || countries.isEmpty())
            throw new IllegalStateException("No data provided");

        model.clearInformation();
        for (String continent : continents) {
            String[] data = continent.replace(" ", "").split("=");
            model.saveContinent(data[0], Integer.valueOf(data[1]));
        }
        for (String countryItem : countries) {
            model.saveCountry(Arrays.asList(countryItem.replace(" ", "").split(",")));
        }

        if (!MapHelper.validateMap() || !MapHelper.validateContinentGraph()) {
            throw new IllegalStateException("Map could not be verified as connected graph / sub-graph");
        }
    }

    /**
     * helper function to listen to text-fields value changes
     *
     * @param field {@link JTextField} of which the text needs to be parsed
     */
    private void handleValueChange(JComboBox<Integer> field) {
        if (field.getName().equals("numOfContinents") || field.getName().equals("numOfCountries")) {
            int number = ((Integer) field.getSelectedItem());

            if (field.getName().equals("numOfContinents")) {
                view.updateContinentFields(number);
            } else {
                view.updateCountryFields(number);
            }
        }
    }
}

