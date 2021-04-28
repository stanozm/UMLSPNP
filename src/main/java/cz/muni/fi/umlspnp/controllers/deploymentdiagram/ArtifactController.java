package cz.muni.fi.umlspnp.controllers.deploymentdiagram;

import cz.muni.fi.umlspnp.common.Utils;
import cz.muni.fi.umlspnp.controllers.BaseController;
import cz.muni.fi.umlspnp.models.MainModel;
import cz.muni.fi.umlspnp.models.deploymentdiagram.Artifact;
import cz.muni.fi.umlspnp.views.MainView;
import cz.muni.fi.umlspnp.views.deploymentdiagram.ArtifactView;

/**
 *  Controller which handles all functionalities within the artifact (component)
 * and provides a model-view binding.
 * 
 */
public class ArtifactController extends BaseController<Artifact, ArtifactView>{

    public ArtifactController(  MainModel mainModel,
                                MainView mainView,
                                Artifact model,
                                ArtifactView view) {
        super(mainModel, mainView, model, view);
        
        view.getNameProperty().bind(model.getNameProperty());
        artifactMenuInit();
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
