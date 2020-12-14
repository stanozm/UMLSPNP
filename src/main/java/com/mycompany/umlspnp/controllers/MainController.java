/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.views.*;
import com.mycompany.umlspnp.models.*;
import com.mycompany.umlspnp.models.deploymentdiagram.*;
import com.mycompany.umlspnp.views.deploymentdiagram.ArtifactView;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author 10ondr
 */
public class MainController {
    private final MainModel model;
    private final MainView view;
    
    public MainController(MainModel mainModel, MainView mainView){
        this.model = mainModel;
        this.view = mainView;
        
        deploymentDiagramInit(this.model.getDeploymentDiagram());
    }

    public MainView getView(){
        return view;
    }
    
    private void deploymentDiagramInit(DeploymentDiagram deployment){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        
        var connectionContainer = deploymentDiagramView.getConnectionContainer();
        connectionContainer.connectionProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null)
                    return;
                
                var firstElementID = connectionContainer.getFirstElementID();
                var secondElementID = connectionContainer.getSecondElementID();
                if(firstElementID != null && secondElementID != null){
                    if(connectionContainer.getFirstElement() instanceof DeploymentTargetView){
                        var firstDT = deployment.getDeploymentTargetRecursive(firstElementID.intValue());
                        var secondDT = deployment.getDeploymentTargetRecursive(secondElementID.intValue());

                        deployment.createCommunicationLink(firstDT, secondDT);
                    }
                    connectionContainer.clear();
                }
            }
        });
        
        
        deployment.addCommunicationLinksChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    if(change.getValueAdded() instanceof CommunicationLink) {
                        var newConnection = (CommunicationLink) change.getValueAdded();
                        var firstID = newConnection.getFirst().getObjectInfo().getID();
                        var secondID = newConnection.getSecond().getObjectInfo().getID();
                        deploymentDiagramView.createConnection(firstID, secondID, newConnection.getObjectInfo().getID());
                    }
                }
                if(change.wasRemoved()){
                    if(change.getValueRemoved() instanceof CommunicationLink) {
                        var removedConnection = (CommunicationLink) change.getValueRemoved();
                        deploymentDiagramView.removeConnection(removedConnection.getObjectInfo().getID());
                    }
                }
            }
        });
        
        
        deployment.addDeploymentTargetsChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    DeploymentTarget newDT = (DeploymentTarget) change.getValueAdded();
                    var newDTView = deploymentDiagramView.createDeploymentTarget(newDT.getObjectInfo().getID());
                    newDTView.getNameProperty().bind(newDT.getNameProperty());
                    deploymentTargetMenuInit(newDTView);
                }
                else if(change.wasRemoved()){
                    DeploymentTarget removedDT = (DeploymentTarget) change.getValueRemoved();
                    deploymentDiagramView.removeDeploymentTargetView(removedDT.getObjectInfo().getID());
                }
            }
        });

        Menu addNodeMenu = new Menu("Add Node");

        MenuItem deviceMenuItem = new MenuItem("Deployment target");
        
        EventHandler<ActionEvent> menuEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(deviceMenuItem)){
                    DeploymentTarget newDT = deployment.createDeploymentTarget();
                    
                    deploymentTargetListenerInit(newDT);
                    deploymentTargetAnnotationsInit(newDT);
                }
            }
        };
        
        deviceMenuItem.setOnAction(menuEventHandler);
        addNodeMenu.getItems().addAll(deviceMenuItem);
        
        deploymentDiagramView.addMenu(addNodeMenu);
    }
    
    private void deploymentTargetAnnotationsInit(DeploymentTarget DT){
        State stateUp = new State("UP");
        stateUp.setDefault(true);
        State stateDown = new State("DOWN");
        DT.addState(stateUp);
        DT.addState(stateDown);

        StateTransition upDownTransition = new StateTransition(stateUp, stateDown, "Failure", 0.5);
        StateTransition downUpTransition = new StateTransition(stateDown, stateUp, "Restart", 0.2);
        DT.addStateTransition(upDownTransition);
        DT.addStateTransition(downUpTransition);
    }
    
    private void deploymentTargetListenerInit(DeploymentTarget DT){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        var DTView = deploymentDiagramView.getDeploymentTargetRecursive(DT.getObjectInfo().getID());
        
        DT.addInnerNodesChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    if(change.getValueAdded() instanceof Artifact) {
                        Artifact newArtifact = (Artifact) change.getValueAdded();
                        var newArtifactView = DTView.CreateArtifact(newArtifact.getObjectInfo().getID());
                        newArtifactView.getNameProperty().bind(newArtifact.getNameProperty());
                        artifactMenuInit(newArtifactView);
                    }
                    else if (change.getValueAdded() instanceof DeploymentTarget) {
                        DeploymentTarget newInnerDT = (DeploymentTarget) change.getValueAdded();
                        var newDTView = deploymentDiagramView.createDeploymentTarget(DTView, newInnerDT.getObjectInfo().getID());
                        newDTView.getNameProperty().bind(newInnerDT.getNameProperty());
                        deploymentTargetMenuInit(newDTView);
                    }
                }
                else if(change.wasRemoved()){
                    if(change.getValueRemoved() instanceof Artifact) {
                        Artifact removedArtifact = (Artifact) change.getValueRemoved();
                        DTView.deleteInnerNode(removedArtifact.getObjectInfo().getID());
                    }
                    else if (change.getValueRemoved() instanceof DeploymentTarget) {
                        DeploymentTarget removedDT = (DeploymentTarget) change.getValueRemoved();
                        DTView.deleteInnerNode(removedDT.getObjectInfo().getID());
                    }
                }
            }
        });
        
        
        DT.addStatesChangeListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasAdded()){
                        List added = change.getAddedSubList();
                        for(var item : added){
                            State newState = (State) item;
                            DTView.getStatesAnnotation().addItem(newState.toString());
                        }
                    }
                    else if (change.wasRemoved()){
                        List removed = change.getRemoved();
                        for(var item : removed){
                            State removedState = (State) item;
                            DTView.getStatesAnnotation().removeItem(removedState.toString());
                        }
                    }
                }
            }
        });        
        
        DT.addStateTransitionsChangeListener(new ListChangeListener(){
            @Override
            public void onChanged(ListChangeListener.Change change) {
                while (change.next()) {
                    if (change.wasAdded()){
                        List added = change.getAddedSubList();
                        for(var item : added){
                            StateTransition newTransition = (StateTransition) item;
                            DTView.getStateTransitionsAnnotation().addItem(newTransition.toString());
                        }
                    }
                    else if (change.wasRemoved()){
                        List removed = change.getRemoved();
                        for(var item : removed){
                            StateTransition removedTransition = (StateTransition) item;
                            DTView.getStateTransitionsAnnotation().removeItem(removedTransition.toString());
                        }
                    }
                }
            }
        });
    }
    
    private void deploymentTargetMenuInit(DeploymentTargetView deploymentTargetView){
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            var deploymentDiagram = this.model.getDeploymentDiagram();
            deploymentDiagram.removeDeploymentTargetRecursive(deploymentTargetView.getObjectInfo().getID());
        });
        deploymentTargetView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemRename = new MenuItem("Rename");
        menuItemRename.setOnAction((e) -> {
            var deploymentDiagram = this.model.getDeploymentDiagram();
            var deploymentTargetObjectID = deploymentTargetView.getObjectInfo().getID();
            var deploymentTarget = deploymentDiagram.getDeploymentTargetRecursive(deploymentTargetObjectID);
            if(deploymentTarget != null){
                this.view.createStringModalWindow("Rename", "New name", deploymentTarget.getNameProperty());
            }
            else{
                System.err.println("Deployment target with id " + deploymentTargetObjectID + " was not found!");
            }
        });
        deploymentTargetView.addMenuItem(menuItemRename);


        MenuItem menuItemAddArtifact = new MenuItem("Add artifact");

        menuItemAddArtifact.setOnAction((e) -> {
            var deploymentDiagram = this.model.getDeploymentDiagram();
            var deploymentTargetObjectID = deploymentTargetView.getObjectInfo().getID();
            var deploymentTarget = deploymentDiagram.getDeploymentTargetRecursive(deploymentTargetObjectID);
            if(deploymentTarget != null){
                deploymentTarget.createArtifact();
            }
            else{
                System.err.println("Deployment target with id " + deploymentTargetObjectID + " was not found!");
            }
        });
        
        deploymentTargetView.addMenuItem(menuItemAddArtifact);
        
        MenuItem menuItemAddDT = new MenuItem("Add deployment target");
        
        menuItemAddDT.setOnAction((e) -> {
            var deploymentDiagram = this.model.getDeploymentDiagram();
            var deploymentTargetObjectID = deploymentTargetView.getObjectInfo().getID();
            var deploymentTarget = deploymentDiagram.getDeploymentTargetRecursive(deploymentTargetObjectID);
            if(deploymentTarget != null){
                var newDT = deploymentTarget.createDeploymentTarget();
                deploymentTargetListenerInit(newDT);
            }
            else{
                System.err.println("Deployment target with id " + deploymentTargetObjectID + " was not found!");
            }
        });
        
        deploymentTargetView.addMenuItem(menuItemAddDT);
        
                
        MenuItem menuItemConnect = new MenuItem("Connect");
        menuItemConnect.setOnAction((e) -> {
            this.view.getDeploymentDiagramView().startConnection(deploymentTargetView);
        });
        deploymentTargetView.addMenuItem(menuItemConnect);

    }
    
    private void artifactMenuInit(ArtifactView artifactView){
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            int parentID = artifactView.getParentDeploymentTargetview().getObjectInfo().getID();
            var deploymentTarget = this.model.getDeploymentDiagram().getDeploymentTargetRecursive(parentID);
            deploymentTarget.deleteInnerNodeRecursive(artifactView.getObjectInfo().getID());
        });
        artifactView.addMenuItem(menuItemDelete);
    }
}
