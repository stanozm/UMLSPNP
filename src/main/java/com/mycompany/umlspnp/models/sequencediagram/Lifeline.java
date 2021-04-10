/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.sequencediagram;

import com.mycompany.umlspnp.models.common.BasicNode;
import com.mycompany.umlspnp.models.common.OperationEntry;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.deploymentdiagram.DeploymentTarget;
import java.util.ArrayList;
import java.util.Collection;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class Lifeline extends BasicNode {
    private final Artifact artifact;
    private final ObservableMap<Number, Activation> activations;
    
    // Only available while transforming to SPNP
    private ArrayList<Message> sortedMessages = null;
    
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
        if(this.artifact instanceof DeploymentTarget){
            var dt = (DeploymentTarget) this.artifact;
            return dt.getAllOperationEntries();
        }
        return null;
    }
    
    public void setSortedMessages(ArrayList<Message> sortedMessages) {
        this.sortedMessages = sortedMessages;
    }

    public ArrayList<Message> getSortedMessages() {
        return sortedMessages;
    }
}
