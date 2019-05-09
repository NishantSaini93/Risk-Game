package view;

import controller.CountryController;
import model.Country;
import model.GameMap;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import static javax.swing.BoxLayout.Y_AXIS;

/**
 * Class containing functions and GUI for the country panel
 **/

public class CountryPanel extends JPanel implements Observer {

    /**
     * controller for the view
     */
    CountryController controller1;

    /**
     * panel to display the list of countries
     */
    JPanel contentPanel1;

    /**
     * Constructor
     * Sets up the panel for country list
     */
    public CountryPanel() {
        controller1 = new CountryController(this);

        setBackground(Color.LIGHT_GRAY);
        setBorder(new LineBorder(Color.BLACK, 2));
        setLayout(new BoxLayout(this, Y_AXIS));

        JLabel jLabelCountriesName = new JLabel("Countries");
        add(jLabelCountriesName);

        contentPanel1 = new JPanel();
        contentPanel1.setBackground(Color.LIGHT_GRAY);
        contentPanel1.setLayout(new BoxLayout(contentPanel1, BoxLayout.Y_AXIS));

        add(contentPanel1);

        controller1.updateCountryList();

    }

    /**
     * update the countries list and owner name in panel
     *
     * @param countries contain data of all countries
     */

    public void updateCountries(HashMap<Integer, Country> countries) {
        contentPanel1.removeAll();
        int index = 0;
        String[] countriesAll = new String[countries.size()];
        if (countries != null) {
            for (Country country : countries.values()) {
                countriesAll[index] = country.id + ". " + country.name + " (" + country.numOfArmies + " - " + country.owner.name + ")";
                index++;
            }
        }
        JList list = new JList(countriesAll);
        JScrollPane jScrollPaneCountries = new JScrollPane(list);
        contentPanel1.add(jScrollPaneCountries);

        contentPanel1.revalidate();
        contentPanel1.repaint();
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
        updateCountries(GameMap.getInstance().countries);
    }
}
