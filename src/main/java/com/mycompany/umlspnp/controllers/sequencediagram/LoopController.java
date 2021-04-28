package com.mycompany.umlspnp.controllers.sequencediagram;

import com.mycompany.umlspnp.controllers.BaseController;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.sequencediagram.Loop;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.sequencediagram.LoopView;

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
    }
}
