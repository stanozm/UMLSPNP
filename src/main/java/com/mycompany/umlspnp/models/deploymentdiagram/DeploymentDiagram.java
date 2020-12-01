/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;


import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 * @author 10ondr
 */
public class DeploymentDiagram {
    private final ObservableList<DeploymentTarget> deploymentTargets;
    
    public DeploymentDiagram(){
        deploymentTargets = FXCollections.observableArrayList();
    }
    
    public void addDeploymentTargetsListChangeListener(ListChangeListener listener){
        deploymentTargets.addListener(listener);
    }
    
    public void addDeploymentTarget(DeploymentTarget newTarget){
        deploymentTargets.add(newTarget);
    }
    
    public boolean deleteDeploymentTarget(int objectID){
        return deploymentTargets.removeIf(item -> 
                (((DeploymentTarget) item).getObjectInfo().getID() == objectID));
    }
}