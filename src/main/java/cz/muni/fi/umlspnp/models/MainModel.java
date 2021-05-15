package cz.muni.fi.umlspnp.models;

import com.google.gson.annotations.Expose;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentDiagram;
import cz.muni.fi.umlspnp.models.sequencediagram.SequenceDiagram;

/**
 *  Model maintaining global application information.
 *
 */
public class MainModel {
    @Expose(serialize = true)
    private DeploymentDiagram deploymentDiagram;
    @Expose(serialize = true)
    private SequenceDiagram sequenceDiagram;
    
    public MainModel(){
        deploymentDiagram = new DeploymentDiagram();
        deploymentDiagram.createSampleData();
        sequenceDiagram = new SequenceDiagram();
    }
    
    public void setDeploymentDiagram(DeploymentDiagram dd){
        deploymentDiagram = dd;
    }

    public DeploymentDiagram getDeploymentDiagram(){
        return deploymentDiagram;
    }
    
    public void setSequenceDiagram(SequenceDiagram sd){
        sequenceDiagram = sd;
    }
    
    public SequenceDiagram getSequenceDiagram(){
        return sequenceDiagram;
    }
    
    public void clear() {
        sequenceDiagram.clear();
        deploymentDiagram.clear();
    }
}
