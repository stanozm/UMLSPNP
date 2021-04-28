package com.mycompany.umlspnp.controllers.sequencediagram;

import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.controllers.BaseController;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.sequencediagram.Activation;
import com.mycompany.umlspnp.models.sequencediagram.Lifeline;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.sequencediagram.LifelineView;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.MapChangeListener;

/**
 *  Controller which handles all functionalities within the lifeline
 * and provides a model-view binding.
 * 
 */
public class LifelineController extends BaseController<Lifeline, LifelineView>{

    private final List<ActivationController> activationControllers;
    
    public LifelineController(  MainModel mainModel,
                                MainView mainView,
                                Lifeline model,
                                LifelineView view) {
        super(mainModel, mainView, model, view);
        
        activationControllers = new ArrayList<>();
        
        lifelineInit();
        lifelineMenuInit();
    }

    private void lifelineInit() {
        var sequenceDiagramView = mainView.getSequenceDiagramView();
        
        view.getNameProperty().bind(model.nameProperty());

        model.addActivationsChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof Activation){
                        var newActivation = (Activation) newNode;

                        var lifelineView = sequenceDiagramView.getLifelineView(newActivation.getLifeline().getObjectInfo().getID());
                        var newActivationView = lifelineView.createActivationView(newActivation.getObjectInfo().getID());

                        var controller = new ActivationController(mainModel, mainView, newActivation, newActivationView);
                        activationControllers.add(controller);
                    }
                }
                else if(change.wasRemoved()){
                    var removedActivation = (Activation) change.getValueRemoved();
                    var lifeline = removedActivation.getLifeline();
                    var lifelineView = sequenceDiagramView.getLifelineView(lifeline.getObjectInfo().getID());
                    if(lifelineView.removeActivationView(removedActivation.getObjectInfo().getID()) != null)
                        activationControllers.removeIf(controller -> controller.getModel().equals(removedActivation));
                }
            }
        });
    }
    
    private void lifelineMenuInit(){
        var sequenceDiagram = mainModel.getSequenceDiagram();
        var lifelineObjectID = view.getObjectInfo().getID();
        
        var deletePromptText = String.format("The lifeline \"%s\" will be deleted. Proceed?",
                                       Utils.shortenString(model.nameProperty().getValue(), 50));

        view.createConfirmMenu("Delete", deletePromptText,
                               () -> {sequenceDiagram.removeLifeline(lifelineObjectID);});
        
        view.createMenuItem("Create activation", (e) -> {
            model.createActivation();
        });
    }
}
