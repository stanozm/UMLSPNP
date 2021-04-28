package cz.muni.fi.umlspnp.models.sequencediagram;

import cz.muni.fi.umlspnp.models.BasicNode;
import cz.muni.fi.umlspnp.models.OperationEntry;
import cz.muni.fi.umlspnp.models.deploymentdiagram.Artifact;
import cz.muni.fi.umlspnp.models.deploymentdiagram.DeploymentTarget;
import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *  Lifeline which contains activations with messages as 
 * specified by the sequence diagram specification.
 * It is uniquely tied with a specific node (Deployment Target or Artifact) in the deployment target.
 *
 */
public class Lifeline extends BasicNode {
    private final Artifact artifact;
    private final ObservableMap<Number, Activation> activations;
    
    // Only available while transforming to SPNP
    private ArrayList<Activation> sortedActivations = null;
    
    public Lifeline(Artifact linkedArtifact){
        this.artifact = linkedArtifact;
        
        this.activations = FXCollections.observableHashMap();
    }
    
    public Activation createActivation(){
        var newActivation = new Activation(this);
        activations.put(newActivation.getObjectInfo().getID(), newActivation);
        return newActivation;
    }

    public boolean removeActivation(int objectID){
        var activation = getActivation(objectID);
        if(activation == null)
            return false;
        return activations.remove(objectID) != null;
    }
    
    public Activation getActivation(int objectID) {
        return activations.get(objectID);
    }
    
    public Collection<Activation> getActivations(){
        return activations.values();
    }
    
    public void addActivationsChangeListener(MapChangeListener listener){
        activations.addListener(listener);
    }
    
    public Artifact getArtifact(){
        return this.artifact;
    }
    
    public StringProperty nameProperty(){
        return this.artifact.getNameProperty();
    }

    public ObservableList<OperationEntry> getOperationEntries(){
        DeploymentTarget dt;
        if(this.artifact instanceof DeploymentTarget)
            dt = (DeploymentTarget) this.artifact;
        else
            dt = this.artifact.getParent();

        if(dt == null)
            return null;
        return dt.getAllOperationEntries();
    }
    
    public void setSortedActivations(ArrayList<Activation> sortedActivations) {
        this.sortedActivations = sortedActivations;
    }

    public ArrayList<Activation> getSortedActivations() {
        return sortedActivations;
    }
}
