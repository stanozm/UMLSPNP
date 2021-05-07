package cz.muni.fi.umlspnp.controllers.sequencediagram;

import cz.muni.fi.umlspnp.common.Utils;
import cz.muni.fi.umlspnp.controllers.BaseController;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.sequencediagram.Activation;
import cz.muni.fi.umlspnp.views.MainView;
import cz.muni.fi.umlspnp.views.sequencediagram.ActivationView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *  Controller which handles all functionalities within the activation
 * and provides a model-view binding.
 * 
 */
public class ActivationController extends BaseController<Activation, ActivationView>{

    public ActivationController(  MainModel mainModel,
                                MainView mainView,
                                Activation model,
                                ActivationView view) {
        super(mainModel, mainView, model, view);

        activationInit();
        activationMenuInit();
        
        model.getLifeline().nameProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                activationMenuInit();
            }
        });
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
        view.clearMenuItems();
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
