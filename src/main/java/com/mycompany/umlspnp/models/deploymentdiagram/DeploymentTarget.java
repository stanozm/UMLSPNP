/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.*;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class DeploymentTarget extends NamedNode {
    private final ObservableMap<Number, NamedNode> innerNodes;
    
    public DeploymentTarget(String name){
        super(name);
        
        innerNodes = FXCollections.observableHashMap();
    }

    public void addInnerNodesChangeListener(MapChangeListener listener){
        innerNodes.addListener(listener);
    }
    
    public Artifact createArtifact(){
        var newArtifact = new Artifact("New artifact");
        addInnerNode(newArtifact);
        return newArtifact;
    }

    public DeploymentTarget createDeploymentTarget(){
        var newDT = new DeploymentTarget("New deployment target");
        addInnerNode(newDT);
        return newDT;
    }
    
    public void addInnerNode(NamedNode newInnerNode){
        innerNodes.put(newInnerNode.getObjectInfo().getID(), newInnerNode);
    }
    
    public boolean deleteInnerNodeRecursive(int objectID){
        if(innerNodes.containsKey(objectID)){
            innerNodes.remove(objectID);
            return true;
        }
        for(var item : innerNodes.values()){
            if(item instanceof DeploymentTarget){
                if(((DeploymentTarget) item).deleteInnerNodeRecursive(objectID))
                    return true;
            }
        }
        return false;
    }
    
    public NamedNode getInnerNode(int objectID){
        return innerNodes.get(objectID);
    }

    public NamedNode getInnerNodeRecursive(int objectID){
        var node = getInnerNode(objectID);
        if(node != null)
            return node;
        for(var item : innerNodes.values()){
            if(item instanceof DeploymentTarget){
                var innerNode = ((DeploymentTarget) item).getInnerNodeRecursive(objectID);
                if(innerNode != null)
                    return innerNode;
            }
        }
        return null;
    }
}
