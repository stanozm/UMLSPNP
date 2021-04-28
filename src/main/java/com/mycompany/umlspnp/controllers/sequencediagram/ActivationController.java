package com.mycompany.umlspnp.controllers.sequencediagram;

import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.sequencediagram.Activation;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.sequencediagram.ActivationView;

/**
 *  Controller which handles all functionalities within the activation
 * and provides a model-view binding.
 * 
 */
public class ActivationController {
    private final MainModel mainModel;
    private final MainView mainView;

    private final Activation model;
    private final ActivationView view;

    public ActivationController(  MainModel mainModel,
                                MainView mainView,
                                Activation model,
                                ActivationView view) {
        this.mainModel = mainModel;
        this.mainView = mainView;
        this.model = model;
        this.view = view;

        activationInit();
        activationMenuInit();
    }
    
    public Activation getModel() {
        return model;
    }
    
    public ActivationView getView() {
        return view;
    }

    private void activationInit() {
        var sequenceDiagram = mainModel.getSequenceDiagram();
        var deploymentDiagram = mainModel.getDeploymentDiagram();
        var sequenceDiagramView = mainView.getSequenceDiagramView();

        var connectionContainer = sequenceDiagramView.getConnectionContainer();
        sequenceDiagramView.registerNodeToSelect(view, (e) -> {
            var startElement = (ActivationView) connectionContainer.getFirstElement();
            if(startElement != null){
                if(startElement.getClass().equals(view.getClass())){
                    var startActivation = sequenceDiagram.getActivation(startElement.getObjectInfo().getID());
                    var startArtifact = startActivation.getLifeline().getArtifact();
                    var newArtifact = model.getLifeline().getArtifact();
                    if(startActivation == model || deploymentDiagram.areNodesConnected(startArtifact, newArtifact)){
                        connectionContainer.setSecondElement(view);
                        return;
                    }
                    else{
                        System.err.println("Unable to create connection. Nodes in deployment diagram are not connected.");
                    }
                }
                else{
                    System.err.println("Unable to create connection. Select suitable destination node.");
                }
                startElement.setSelected(false);
                connectionContainer.clear();
            }
        });
    }

    private void activationMenuInit(){
        var sequenceDiagram = mainModel.getSequenceDiagram();
        var sequenceDiagramView = mainView.getSequenceDiagramView();
        var activationObjectID = view.getObjectInfo().getID();
        var lifeline = sequenceDiagram.getActivation(activationObjectID).getLifeline();
        
        var deletePromptText = String.format("The activation of lifeline \"%s\" will be deleted. Proceed?",
                                       Utils.shortenString(lifeline.nameProperty().getValue(), 50));

        view.createConfirmMenu("Delete", deletePromptText,
                               () -> {sequenceDiagram.removeActivation(activationObjectID);});
        
        view.createMenuItem("Create message", (e) -> {
            sequenceDiagramView.startConnection(view);
        });
    }
}
