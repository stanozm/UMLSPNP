/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.*;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class DeploymentTarget extends NamedNode {
    private List<DeploymentTarget> innerTargets;
    private final ObservableMap<Number, Artifact> artifacts;
    
    public DeploymentTarget(String name){
        super(name);
        
        artifacts = FXCollections.observableHashMap();
    }

    public void addArtifactsChangeListener(MapChangeListener listener){
        artifacts.addListener(listener);
    }
    
    public void addArtifact(Artifact newArtifact){
        artifacts.put(newArtifact.getObjectInfo().getID(), newArtifact);
    }
    
    public boolean deleteArtifact(int objectID){
        if(artifacts.containsKey(objectID)){
            artifacts.remove(objectID);
            return true;
        }
        return false;
    }
}
