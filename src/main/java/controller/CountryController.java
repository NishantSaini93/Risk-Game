package controller;

import model.Country;
import view.CountryPanel;

/**
 * Controller for (@link view.CountryPanel) extends (@link BaseController)
 */

public class CountryController extends BaseController<CountryPanel> {

    /**
     * This is the constructor for the Controller
     *
     * @param view View associated wth the Controller
     */
    public CountryController(CountryPanel view) {
        super(view);
        for (Country country : model.countries.values()) {
            if (view != null && country != null) {
                country.addObserver(view);
            }
        }
    }

    /**
     * Update the list of countries in the view with current player's countries
     */
    public void updateCountryList() {
        view.updateCountries(model.countries);
    }

}
