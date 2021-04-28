package cz.muni.fi.umlspnp.controllers;

import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.views.MainView;

/**
 * A base class for all controllers.
 *
 * @param <T1> Data type of the model.
 * @param <T2> Data type of the view.
 */
public abstract class BaseController<T1, T2> {
    protected final MainModel mainModel;
    protected final MainView mainView;

    protected final T1 model;
    protected final T2 view;
    
    protected BaseController(  MainModel mainModel,
                               MainView mainView,
                               T1 model,
                               T2 view) {
        this.mainModel = mainModel;
        this.mainView = mainView;
        this.model = model;
        this.view = view;
    }
    
    public T1 getModel() {
        return model;
    }
    
    public T2 getView() {
        return view;
    }
}
