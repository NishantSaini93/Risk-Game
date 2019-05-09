package controller;

import view.ContinentPanel;

/**
 * Controller for {@link view.ContinentPanel} extends {@link BaseController}
 */
public class ContinentController extends BaseController<ContinentPanel> {

    /**
     * This is the constructor for the Controller
     *
     * @param view View associated with the Controller
     */
    public ContinentController(ContinentPanel view) {
        super(view);
        model.addObserver(view);
    }

    /**
     * Update the list of countries in the view with current player's countries
     */
    public void updateContinentList() {
        view.updateContinentList(model.continents.values());
    }
}
