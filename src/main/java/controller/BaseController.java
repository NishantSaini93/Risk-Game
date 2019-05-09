package controller;

import model.GameMap;

/**
 * This is the abstract class for controller. All the controllers should extend this class.
 *
 * @param <T> this is the type of View
 */
public abstract class BaseController<T> {
    /**
     * View for controller provided by template variable T
     */
    public T view;
    /**
     * Model for game
     */
    public GameMap model;

    /**
     * This is the constructor for the Controller
     *
     * @param view View associated with the Controller
     */
    public BaseController(T view) {
        this.view = view;
        this.model = GameMap.getInstance();
    }

    /**
     * Getter method for the view
     *
     * @return Returns the associated View
     */
    public T getView() {
        return view;
    }
}
