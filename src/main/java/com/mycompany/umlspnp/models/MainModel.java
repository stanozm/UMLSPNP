package com.mycompany.umlspnp.models;

import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;

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
