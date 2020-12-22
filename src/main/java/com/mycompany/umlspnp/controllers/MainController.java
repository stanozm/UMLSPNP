/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.views.*;
import com.mycompany.umlspnp.models.*;
import com.mycompany.umlspnp.models.deploymentdiagram.*;
import com.mycompany.umlspnp.views.common.layouts.BooleanModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.StringModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.ArtifactView;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import com.mycompany.umlspnp.views.deploymentdiagram.EditOperationEntryModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.EditOperationModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.EditTransitionModalWindow;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

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
        State stateDown = new State("DOWN");
        DT.addState(stateUp);
        DT.addState(stateDown);
        DT.setDefaultState(stateUp);
       
        StateTransition upDownTransition = new StateTransition(stateUp, stateDown, "Failure", 0.5);
        StateTransition downUpTransition = new StateTransition(stateDown, stateUp, "Restart", 0.2);
        DT.addStateTransition(upDownTransition);
        DT.addStateTransition(downUpTransition);
        
        StateOperation operationsUp = new StateOperation(stateUp);
        operationsUp.addOperationEntry("ReadDeviceData", null);
        operationsUp.addOperationEntry("WriteDeviceData", null);
        StateOperation operationsDown = new StateOperation(stateDown);
        operationsDown.addOperationEntry("ReadDeviceData", 50);
        DT.addStateOperation(operationsUp);
        DT.addStateOperation(operationsDown);
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
        
        DTView.getStatesAnnotation().setItems(DT.getStates());
        DTView.getStateTransitionsAnnotation().setItems(DT.getStateTransitions());
        DTView.getStateOperationsAnnotation().setItems(DT.getStateOperations());
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
       
        SeparatorMenuItem separator = new SeparatorMenuItem();
        deploymentTargetView.addMenuItem(separator);
        
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction((e) -> {
            var deploymentDiagram = this.model.getDeploymentDiagram();
            var deploymentTargetObjectID = deploymentTargetView.getObjectInfo().getID();
            var deploymentTarget = deploymentDiagram.getDeploymentTargetRecursive(deploymentTargetObjectID);
            if(deploymentTarget != null){
                var statesView = createStatesProperties(deploymentTarget);
                var stateTransitionsView = createStateTransitionsProperties(deploymentTarget);
                var stateOperationsView = createStateOperationsProperties(deploymentTarget);
                
                ArrayList<EditableListView> sections = new ArrayList();
                sections.add(statesView);
                sections.add(stateTransitionsView);
                sections.add(stateOperationsView);
                
                this.view.createPropertiesModalWindow("\"" + deploymentTarget.getNameProperty().getValue() + "\" properties", sections);
            }
            else{
                System.err.println("Deployment target with id " + deploymentTargetObjectID + " was not found!");
            }
        });
        deploymentTargetView.addMenuItem(menuProperties);

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
    
    private EditableListView createStatesProperties(DeploymentTarget deploymentTarget){
        var states = deploymentTarget.getStates();
        var statesView = new EditableListView("States:", states);
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                deploymentTarget.addState(new State("New state"));
            }
        };

        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (State) statesView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) statesView.getScene().getWindow(), 
                            "Confirm", "The state \"" + selected + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        states.remove(selected);
                    }
                }
            }
        };

        var renameBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (State) statesView.getSelected();
                if(selected != null){
                    StringModalWindow renameWindow = new StringModalWindow((Stage) statesView.getScene().getWindow(), 
                            "Rename state", "Type new name of the state \"" + selected + "\":", selected.nameProperty());
                    renameWindow.showAndWait();
                    statesView.refresh();
                }
            }
        };

        var setDefaultBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (State) statesView.getSelected();
                if(selected != null){
                    deploymentTarget.setDefaultState(selected);
                    statesView.refresh();
                }
            }
        };

        statesView.createButton("Add", addBtnHandler, false);
        statesView.createButton("Remove", removeBtnHandler, true);
        statesView.createButton("Rename", renameBtnHandler, true);
        statesView.createButton("Set default", setDefaultBtnHandler, true);
        
        return statesView;
    }

    
    private EditableListView createStateTransitionsProperties(DeploymentTarget deploymentTarget){
        var states = deploymentTarget.getStates();
        var transitions = deploymentTarget.getStateTransitions();
        var transitionsView = new EditableListView("State Transitions:", transitions);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                // Button is disabled when there are not at least 2 states
                var state1 = states.get(0);
                var state2 = states.get(1);
                deploymentTarget.addStateTransition(new StateTransition(state1, state2, "New Transition", 1.0));
            }
        };
        
        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateTransition) transitionsView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) transitionsView.getScene().getWindow(), 
                            "Confirm", "The transition \"" + selected + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        transitions.remove(selected);
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateTransition) transitionsView.getSelected();
                if(selected != null){
                    EditTransitionModalWindow editWindow = new EditTransitionModalWindow(   (Stage) transitionsView.getScene().getWindow(),
                                                                                            "Edit transition",
                                                                                            selected.nameProperty(),
                                                                                            selected.rateProperty(),
                                                                                            selected.fromStateProperty(),
                                                                                            selected.toStateProperty(),
                                                                                            states
                                                                                            );
                    editWindow.showAndWait();
                    transitionsView.refresh();
                }
            }
        };

        var addBtn = transitionsView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(states).lessThan(2));
        transitionsView.createButton("Remove", removeBtnHandler, true);
        transitionsView.createButton("Edit", editBtnHandler, true);
        return transitionsView;
    }
    
    private EditableListView createStateOperationsProperties(DeploymentTarget deploymentTarget){
        var statesWithNoOperations = deploymentTarget.getStatesWithoutOperations();
        var operations = deploymentTarget.getStateOperations();
        var operationsView = new EditableListView("Supported Operations:", operations);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                // Button is disabled when there is not at least 1 state available
                deploymentTarget.addStateOperation(new StateOperation(statesWithNoOperations.get(0)));
            }
        };
        
        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateOperation) operationsView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) operationsView.getScene().getWindow(), 
                            "Confirm", "The operation \"" + selected + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        operations.remove(selected);
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (StateOperation) operationsView.getSelected();
                if(selected != null){
                    EditableListView operationEntriesView = createStateOperationEntriesProperties(selected);
                    
                    EditOperationModalWindow editWindow = new EditOperationModalWindow(   (Stage) operationsView.getScene().getWindow(),
                                                                                            "Edit operation",
                                                                                            selected.stateProperty(),
                                                                                            statesWithNoOperations,
                                                                                            operationEntriesView
                                                                                            );
                    editWindow.showAndWait();
                    operationsView.refresh();
                }
            }
        };
        
        var addBtn = operationsView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(statesWithNoOperations).lessThan(1));
        operationsView.createButton("Remove", removeBtnHandler, true);
        operationsView.createButton("Edit", editBtnHandler, true);
        return operationsView;
    }

    private EditableListView createStateOperationEntriesProperties(StateOperation operation){
        var operationEntries = operation.getOperationEntries();
        
        var operationEntriesView = new EditableListView("Operations:", operation.getOperationEntries());
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                operationEntries.add(new OperationEntry("New operation", -1));
            }
        };
        
        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (OperationEntry) operationEntriesView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) operationEntriesView.getScene().getWindow(), 
                            "Confirm", "The operation entry \"" + selected + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        operationEntries.remove(selected);
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (OperationEntry) operationEntriesView.getSelected();
                
                EditOperationEntryModalWindow editWindow = 
                        new EditOperationEntryModalWindow( (Stage) operationEntriesView.getScene().getWindow(),
                                                            "Edit Operation Entry",
                                                            selected.nameProperty(),
                                                            selected.speedLimitProperty()
                                                            );
                editWindow.showAndWait();

                operationEntriesView.refresh();
            }
        };

        operationEntriesView.createButton("Add", addBtnHandler, false);
        operationEntriesView.createButton("Remove", removeBtnHandler, true);
        operationEntriesView.createButton("Edit", editBtnHandler, true);
        return operationEntriesView;
    }

}
