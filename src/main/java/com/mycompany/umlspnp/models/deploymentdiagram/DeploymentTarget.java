/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.models.common.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Callback;

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
    
    private final ObservableList<State> statesWithoutOperations;
    
    public DeploymentTarget(String name){
        super(name);
        
        innerNodes = FXCollections.observableHashMap();
        states = FXCollections.observableArrayList(
                new Callback<State, Observable[]>() {
                    @Override
                    public Observable[] call(State param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        stateTransitions = FXCollections.observableArrayList(
                new Callback<StateTransition, Observable[]>() {
                    @Override
                    public Observable[] call(StateTransition param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        stateOperations = FXCollections.observableArrayList(
                new Callback<StateOperation, Observable[]>() {
                    @Override
                    public Observable[] call(StateOperation param) {
                        return new Observable[]{
                            param.getStringRepresentation()
                        };
                    }
                });
        
        statesWithoutOperations = FXCollections.observableArrayList();
        initStatesWithoutOperations();
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
    
    public boolean removeState(State removedState){
        return states.remove(removedState);
    }
    
    public void addStateTransition(StateTransition newTransition){
        stateTransitions.add(newTransition);
    }
    
    public void addStateOperation(StateOperation newOperation){
        stateOperations.add(newOperation);
    }

    public ObservableList<State> getStates() {
        return this.states;
    }
    
    public ObservableList<State> getStatesWithoutOperations() {
        return this.statesWithoutOperations;
    }
    
    public void setDefaultState(State newDefaultState){
        for(var state : states){
            if(state.equals(newDefaultState))
                state.setDefault(true);
            else
                state.setDefault(false);
        }
    }
    
    public ObservableList<StateTransition> getStateTransitions(){
        return this.stateTransitions;
    }
    
    public ObservableList<StateOperation> getStateOperations(){
        return this.stateOperations;
    }
    
    private boolean stateHasOperations(State state){
        for(var operation : stateOperations){
            if(state == operation.getState()){
                return true;
            }
        }
        return false;
    }
    
    public void refilterStatesWithoutOperations(){
        this.statesWithoutOperations.setAll(states.filtered(state -> !stateHasOperations(state)));
    }
    
    public void initStatesWithoutOperations(){
        refilterStatesWithoutOperations();
        
        states.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasAdded()) {
                        for (var addedItem : change.getAddedSubList()){
                            var addedState = (State) addedItem;
                            statesWithoutOperations.add(addedState);
                        }
                    }
                    else if (change.wasRemoved()) {
                        for(var removedItem : change.getRemoved()){
                            var removedState = (State) removedItem;
                            statesWithoutOperations.remove(removedState);
                        }
                    }
                }
            }
        });
        
        stateOperations.addListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                refilterStatesWithoutOperations();
            }
        });
    }
}
