/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;


import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class DeploymentDiagram {
    private final ObservableMap<Number, DeploymentTarget> deploymentTargets;
    
    public DeploymentDiagram(){
        deploymentTargets = FXCollections.observableHashMap();
    }
    
    public void addDeploymentTargetsChangeListener(MapChangeListener listener){
        deploymentTargets.addListener(listener);
    }
    
    public void addDeploymentTarget(DeploymentTarget newTarget){
        deploymentTargets.put(newTarget.getObjectInfo().getID(), newTarget);
    }
    
    public boolean deleteDeploymentTarget(int objectID){
        if(deploymentTargets.containsKey(objectID)){
            deploymentTargets.remove(objectID);
            return true;
        }
        return false;
    }
    
    public DeploymentTarget getDeploymentTarget(int objectID){
        return deploymentTargets.get(objectID);
    }
}