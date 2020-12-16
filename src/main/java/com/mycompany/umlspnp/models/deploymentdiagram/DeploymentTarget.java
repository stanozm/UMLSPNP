/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author 10ondr
 */
public class DeploymentTarget extends NamedNode {
    private final ObservableMap<Number, NamedNode> innerNodes;
    
    // Annotations
    private final ObservableList<State> states;
    private final ObservableList<StateTransition> stateTransitions;
    private final ObservableList<StateOperation> stateOperations;
    
    public DeploymentTarget(String name){
        super(name);
        
        innerNodes = FXCollections.observableHashMap();
        states = FXCollections.observableArrayList();
        stateTransitions = FXCollections.observableArrayList();
        stateOperations = FXCollections.observableArrayList();
    }

    public void addInnerNodesChangeListener(MapChangeListener listener){
        innerNodes.addListener(listener);
    }
    
    public void addStatesChangeListener(ListChangeListener listener){
        states.addListener(listener);
    }
    
    public void addStateTransitionsChangeListener(ListChangeListener listener){
        stateTransitions.addListener(listener);
    }
    
    public void addStateOperationsChangeListener(ListChangeListener listener){
        stateOperations.addListener(listener);
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
    
    public void addState(State newState){
        states.add(newState);
    }
    
    public void addStateTransition(StateTransition newTransition){
        stateTransitions.add(newTransition);
    }
    
    public void addStateOperation(StateOperation newOperation){
        stateOperations.add(newOperation);
    }
}
