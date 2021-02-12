/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.models.deploymentdiagram;

import com.mycompany.umlspnp.common.ElementContainer;
import com.mycompany.umlspnp.models.common.*;
import java.util.ArrayList;
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
    private final ElementContainer allElements = ElementContainer.getInstanceModel();
    
    private final ObservableMap<Number, NamedNode> innerNodes;
    private final ObservableMap<Number, CommunicationLink> innerConnections;
    
    private final DeploymentTarget DTparent;
    
    
    // Annotations
    private final ObservableList<State> states;
    private final ObservableList<StateTransition> stateTransitions;
    private final ObservableList<StateOperation> stateOperations;
    
    private final ObservableList<State> statesWithoutOperations;
    
    public DeploymentTarget(String name, DeploymentTarget parent){
        super(name);
        
        innerNodes = FXCollections.observableHashMap();
        innerConnections = FXCollections.observableHashMap();
        this.DTparent = parent;
        
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
   
    public DeploymentTarget getParent(){
        return DTparent;
    }
    
    private void cleanup(){
        var connections = new ArrayList<CommunicationLink>(innerConnections.values());
        
        for(var connection : connections){
            connection.cleanup();
            allElements.removeConnection(connection.getObjectInfo().getID());
        }
    }

    public void cleanupRecursive(){
        for(var item : innerNodes.values()){
            if(item instanceof DeploymentTarget){
                var removedDeploymentTarget = (DeploymentTarget) item;
                removedDeploymentTarget.cleanupRecursive();
            }
            allElements.removeNode(item.getObjectInfo().getID());
        }
        
        cleanup();
    }
    
    public boolean removeInnerNode(int objectID){
        var removed = innerNodes.remove(objectID);
        return removed != null;
    }

    public void addInnerNodesChangeListener(MapChangeListener listener){
        innerNodes.addListener(listener);
    }

    public void addInnerNode(NamedNode newInnerNode){
        innerNodes.put(newInnerNode.getObjectInfo().getID(), newInnerNode);
        newInnerNode.getObjectInfo().setGroupID(this.getObjectInfo().getGroupID());
    }
    
    public void removeConnection(CommunicationLink removedConnection){
        innerConnections.remove(removedConnection.getObjectInfo().getID());
    }
    
    public void addInnerConnectionsChangeListener(MapChangeListener listener){
        innerConnections.addListener(listener);
    }

    public void addInnerConnection(CommunicationLink newConnection){
        innerConnections.put(newConnection.getObjectInfo().getID(), newConnection);
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
