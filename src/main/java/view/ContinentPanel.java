package view;

import controller.ContinentController;
import model.Continent;
import model.GameMap;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

/**
 * View for the continents extends {@link JPanel}
 */
public class ContinentPanel extends JPanel implements Observer {

    /**
     * controller for the view
     */
    ContinentController controller;
    /**
     * panel to display the list of continents
     */
    JPanel contentPanel;

    /**
     * Constructor
     * <p>Sets up the panel for continent list</p>
     */
    public ContinentPanel() {
        controller = new ContinentController(this);

        setBackground(Color.LIGHT_GRAY);
        setBorder(new LineBorder(Color.BLACK, 2));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Continents"));

        contentPanel = new JPanel();
        contentPanel.setBackground(Color.LIGHT_GRAY);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        add(contentPanel);

        controller.updateContinentList();
    }

    /**
     * Update the continent list for display
     *
     * @param continents list of continents to display
     */
    public void updateContinentList(Collection<Continent> continents) {
        contentPanel.removeAll();
        if (continents != null) {
            for (Continent continent : continents) {
                JLabel label = new JLabel();
                label.setName("label");
                String text = continent.id + ". " + continent.name;
                text += " | CV = " + continent.controlValue;
                text += " | countries = ";
                text += continent.countries.size();

                label.setText(text);
                contentPanel.add(label);
            }
        }
        contentPanel.revalidate();
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notifies of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        updateContinentList(GameMap.getInstance().continents.values());
    }
}
