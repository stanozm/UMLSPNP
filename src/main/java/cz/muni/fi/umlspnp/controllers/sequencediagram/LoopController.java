package cz.muni.fi.umlspnp.controllers.sequencediagram;

import cz.muni.fi.umlspnp.controllers.BaseController;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.sequencediagram.Loop;
import cz.muni.fi.umlspnp.views.MainView;
import cz.muni.fi.umlspnp.views.sequencediagram.LoopView;

/**
 *  Controller which handles all functionalities within the loop fragment
 * and provides a model-view binding.
 *
 */

public class LoopController extends BaseController<Loop, LoopView>{
    
    public LoopController(  MainModel mainModel,
                               MainView mainView,
                               Loop model,
                               LoopView view) {
        super(mainModel, mainView, model, view);

        view.getNameProperty().bind(model.nameProperty());
        loopMenuInit();
    }

    private void loopMenuInit(){
        view.clearMenuItems();
        var sequence = this.mainModel.getSequenceDiagram();
        var loopObjectID = model.getObjectInfo().getID();

        var deletePromptText = "The loop will be deleted. Proceed?";
        view.createConfirmMenu("Delete", deletePromptText,
                               () -> {sequence.removeLoop(loopObjectID);});

        view.createIntegerMenu( "Change iterations",
                                "Change loop iterations",
                                "Iterations",
                                2,
                                null,
                                model.iterationsProperty());
        
        view.createDoubleMenu( "Change restart rate",
                        "Change restart rate",
                        "Restart rate",
                        0.0,
                        null,
                        model.restartRateProperty());
    }
}
