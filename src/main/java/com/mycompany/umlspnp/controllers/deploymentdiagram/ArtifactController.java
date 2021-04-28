package com.mycompany.umlspnp.controllers.deploymentdiagram;

import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.deploymentdiagram.ArtifactView;

/**
 *  Controller which handles all functionalities within the artifact (component)
 * and provides a model-view binding.
 * 
 */
public class ArtifactController {
    private final MainModel mainModel;
    private final MainView mainView;

    private final Artifact model;
    private final ArtifactView view;
    
    public ArtifactController(  MainModel mainModel,
                                MainView mainView,
                                Artifact model,
                                ArtifactView view) {
        this.mainModel = mainModel;
        this.mainView = mainView;
        this.model = model;
        this.view = view;
        
        view.getNameProperty().bind(model.getNameProperty());
        artifactMenuInit();
    }
    
    public Artifact getModel() {
        return model;
    }
    
    public ArtifactView getView() {
        return view;
    }
    
    private void artifactMenuInit(){
        var deploymentDiagram = mainModel.getDeploymentDiagram();

        var deletePromptText = String.format("The artifact \"%s\" will be deleted. Proceed?",
                                      Utils.shortenString(model.getNameProperty().getValue(), 50));
        view.createConfirmMenu("Delete", deletePromptText,
                               () -> {deploymentDiagram.removeNode(model.getObjectInfo().getID());});

        view.createStringMenu("Rename", "Rename artifact", "New name",
                              model.getNameProperty(), Utils.SPNP_NAME_RESTRICTION_REGEX);
    }
}
