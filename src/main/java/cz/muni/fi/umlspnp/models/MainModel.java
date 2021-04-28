package cz.muni.fi.umlspnp.models;

import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import cz.muni.fi.umlspnp.models.sequencediagram.SequenceDiagram;

/**
 *  Model maintaining global application information.
 *
 */
public class MainModel {
    private final DeploymentDiagram deploymentDiagram;
    private final SequenceDiagram sequenceDiagram;
    
    public MainModel(){
        deploymentDiagram = new DeploymentDiagram();
        sequenceDiagram = new SequenceDiagram();
    }
    
    public DeploymentDiagram getDeploymentDiagram(){
        return deploymentDiagram;
    }
    
    public SequenceDiagram getSequenceDiagram(){
        return sequenceDiagram;
    }
}
