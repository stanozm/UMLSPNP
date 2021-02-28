/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.models.common.ConnectionFailure;
import com.mycompany.umlspnp.models.common.OperationEntry;
import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.views.*;
import com.mycompany.umlspnp.models.*;
import com.mycompany.umlspnp.models.common.NamedNode;
import com.mycompany.umlspnp.models.deploymentdiagram.*;
import com.mycompany.umlspnp.views.common.AnnotationOwner;
import com.mycompany.umlspnp.views.common.layouts.BooleanModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.StringModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.ArtifactView;
import com.mycompany.umlspnp.views.deploymentdiagram.CommunicationLinkView;
import com.mycompany.umlspnp.views.deploymentdiagram.DeploymentTargetView;
import com.mycompany.umlspnp.views.deploymentdiagram.EditAllLinkTypesModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditFailureTypeModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.EditOperationEntryModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.EditOperationModalWindow;
import com.mycompany.umlspnp.views.deploymentdiagram.EditTransitionModalWindow;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
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
public class DeploymentDiagramController {
    private final MainModel model;
    private final MainView view;
    
    public DeploymentDiagramController(MainModel mainModel, MainView mainView){
        this.model = mainModel;
        this.view = mainView;
        
        deploymentDiagramInit(this.model.getDeploymentDiagram());
    }

    
    /***  ONLY FOR TESTING  ***/
    private void createSampleAnnotations(DeploymentTarget DT){
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
    
    /***  ONLY FOR TESTING  ***/
    private void createSampleAnnotations(CommunicationLink communicationLink){
        communicationLink.addLinkFailure(new ConnectionFailure("PacketLost", 0.02));
        communicationLink.addLinkFailure(new ConnectionFailure("ConnectionDropped", 0.001));
    }
    
    /***  ONLY FOR TESTING  ***/
    public void createSampleNodes() {
        var deployment = model.getDeploymentDiagram();
        
        var A = deployment.createDeploymentTarget(null);
        A.getNameProperty().setValue("A");
        var AA = deployment.createDeploymentTarget(A);
        AA.getNameProperty().setValue("AA");
        var AAA = deployment.createDeploymentTarget(AA);
        AAA.getNameProperty().setValue("AAA");
        
        var B = deployment.createDeploymentTarget(null);
        B.getNameProperty().setValue("B");
        var BB = deployment.createDeploymentTarget(B);
        BB.getNameProperty().setValue("BB");
        var BBB = deployment.createDeploymentTarget(BB);
        BBB.getNameProperty().setValue("BBB");
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
                if(firstElementID != null){
                    var firstDTView = deploymentDiagramView.getDeploymentTargetView(firstElementID.intValue());
                    firstDTView.setSelected(true);
                    
                    if(secondElementID != null){
                        if(connectionContainer.getFirstElement() instanceof DeploymentTargetView){
                            var firstDT = deployment.getDeploymentTarget(firstElementID.intValue());
                            var secondDT = deployment.getDeploymentTarget(secondElementID.intValue());
                            if(deployment.areNodesConnected(firstDT, secondDT)){
                                System.err.println("Error: Nodes \"" + firstDT.getNameProperty().getValue() + "\" and \"" + 
                                         secondDT.getNameProperty().getValue() + "\" are already connected!");
                            }
                            else{
                                deployment.createCommunicationLink(firstDT, secondDT);
                            }
                        }
                        firstDTView.setSelected(false);
                        connectionContainer.clear();
                    }
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
                        var newConnectionView = deploymentDiagramView.createConnection(firstID, secondID, newConnection.getObjectInfo().getID());
                        
                        communicationLinkMenuInit(newConnectionView);
                        communicationLinkAnnotationsInit(newConnection);
                        createSampleAnnotations(newConnection);
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
        
        
        deployment.addAllNodesChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof DeploymentTarget){
                        var newDT = (DeploymentTarget) newNode;
                        
                        DeploymentTargetView newDTParent = null;
                        if(newDT.getParent() != null)
                            newDTParent = deploymentDiagramView.getDeploymentTargetView(newDT.getParent().getObjectInfo().getID());

                        var newDTView = deploymentDiagramView.createDeploymentTargetView(newDTParent, newDT.getObjectInfo().getID());
                        newDTView.getNameProperty().bind(newDT.getNameProperty());
                        deploymentTargetMenuInit(newDTView);
                        deploymentTargetAnnotationsInit(newDT);
                    }
                    else if(newNode instanceof Artifact){
                        Artifact newArtifact = (Artifact) newNode;
                        var newArtifactParent = deploymentDiagramView.getDeploymentTargetView(newArtifact.getParent().getObjectInfo().getID());
                        var newArtifactView = deploymentDiagramView.CreateArtifact(newArtifactParent, newArtifact.getObjectInfo().getID());
                        newArtifactView.getNameProperty().bind(newArtifact.getNameProperty());
                        artifactMenuInit(newArtifactView);
                    }
                }
                else if(change.wasRemoved()){
                    var removedNode = (NamedNode) change.getValueRemoved();
                    deploymentDiagramView.removeNode(removedNode.getObjectInfo().getID());
                }
            }
        });

        Menu addNodeMenu = new Menu("Add Node");

        MenuItem deviceMenuItem = new MenuItem("Deployment target");
        
        EventHandler<ActionEvent> menuEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(deviceMenuItem)){
                    DeploymentTarget newDT = deployment.createDeploymentTarget(null);
                    
                    createSampleAnnotations(newDT);
                }
            }
        };
        
        deviceMenuItem.setOnAction(menuEventHandler);
        addNodeMenu.getItems().addAll(deviceMenuItem);
        
        deploymentDiagramView.addMenu(addNodeMenu);
    }

    private void deploymentTargetAnnotationsInit(DeploymentTarget DT){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        var DTView = deploymentDiagramView.getDeploymentTargetView(DT.getObjectInfo().getID());
        
        DTView.getStatesAnnotation().setItems(DT.getStates());
        DTView.getStateTransitionsAnnotation().setItems(DT.getStateTransitions());
        DTView.getStateOperationsAnnotation().setItems(DT.getStateOperations());
    }
    
    private void communicationLinkAnnotationsInit(CommunicationLink communicationLink){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        var connectionView = deploymentDiagramView.getConnection(communicationLink.getObjectInfo().getID());
        
        connectionView.getLinkTypeAnnotation().setItems(communicationLink.getLinkTypeList());
        connectionView.getLinkFailuresAnnotation().setItems(communicationLink.getLinkFailures());
    }
    
    private void deploymentTargetMenuInit(DeploymentTargetView deploymentTargetView){
        var deploymentDiagram = this.model.getDeploymentDiagram();
        var deploymentTargetObjectID = deploymentTargetView.getObjectInfo().getID();
        var deploymentTarget = deploymentDiagram.getDeploymentTarget(deploymentTargetObjectID);
        
        if(deploymentTarget == null){
            System.err.println("Deployment target with id " + deploymentTargetObjectID + " was not found!");
            return;
        }
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            deploymentDiagram.removeNode(deploymentTargetObjectID);
        });
        deploymentTargetView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemRename = new MenuItem("Rename");
        menuItemRename.setOnAction((e) -> {
            this.view.createStringModalWindow("Rename", "New name", deploymentTarget.getNameProperty());
        });
        deploymentTargetView.addMenuItem(menuItemRename);

        
        MenuItem menuItemToggleAnnotations = createToggleAnnotationsMenuItem(deploymentTargetView);
        deploymentTargetView.addMenuItem(menuItemToggleAnnotations);
        

        MenuItem menuItemAddArtifact = new MenuItem("Add artifact");

        menuItemAddArtifact.setOnAction((e) -> {
            deploymentDiagram.createArtifact(deploymentTarget);
        });
        
        deploymentTargetView.addMenuItem(menuItemAddArtifact);
        
        MenuItem menuItemAddDT = new MenuItem("Add deployment target");
        
        menuItemAddDT.setOnAction((e) -> {
            deploymentDiagram.createDeploymentTarget(deploymentTarget);
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
            var statesView = createStatesProperties(deploymentTarget);
            var stateTransitionsView = createStateTransitionsProperties(deploymentTarget);
            var stateOperationsView = createStateOperationsProperties(deploymentTarget);

            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(statesView);
            sections.add(stateTransitionsView);
            sections.add(stateOperationsView);

            this.view.createPropertiesModalWindow("\"" + deploymentTarget.getNameProperty().getValue() + "\" properties", sections);
        });
        deploymentTargetView.addMenuItem(menuProperties);

    }
    
    private void communicationLinkMenuInit(CommunicationLinkView communicationLinkView){
        var deploymentDiagram = this.model.getDeploymentDiagram();
        var communicationLinkObjectID = communicationLinkView.getObjectInfo().getID();
        var communicationLink = deploymentDiagram.getCommunicationLink(communicationLinkObjectID);
        
        if(communicationLink == null){
            System.err.println("Communication link with id " + communicationLinkObjectID + " was not found!");
            return;
        }
        
        MenuItem menuItemDelete = new MenuItem("Delete connection");
        menuItemDelete.setOnAction((e) -> {
            deploymentDiagram.removeCommunicationLink(communicationLinkObjectID);
        });
        communicationLinkView.addMenuItem(menuItemDelete);    


        MenuItem menuItemToggleAnnotations = createToggleAnnotationsMenuItem(communicationLinkView);
        communicationLinkView.addMenuItem(menuItemToggleAnnotations);
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        communicationLinkView.addMenuItem(separator);
        
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction((e) -> {
            var linkTypesView = createLinkTypeProperties(communicationLink);
            var failuresView = createFailureTypesProperties(communicationLink);

            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(failuresView);
            sections.add(linkTypesView);

            this.view.createPropertiesModalWindow("[" + communicationLink.getFirst().getNameProperty().getValue() + 
                    " > " + communicationLink.getSecond().getNameProperty().getValue() +  "] properties", sections);

        });
        communicationLinkView.addMenuItem(menuProperties);
    }
    
    private void artifactMenuInit(ArtifactView artifactView){
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            this.model.getDeploymentDiagram().removeNode(artifactView.getObjectInfo().getID());
        });
        artifactView.addMenuItem(menuItemDelete);
    }
    
    private MenuItem createToggleAnnotationsMenuItem(AnnotationOwner view){
        String hideAnnotationsString = "Hide annotations";
        String showAnnotationsString = "Show annotations";
        MenuItem menuItemToggleAnnotations = new MenuItem(hideAnnotationsString);
        menuItemToggleAnnotations.setOnAction((e) -> {
            view.setAnnotationsDisplayed(!view.areAnnotationsDisplayed());
            if(view.areAnnotationsDisplayed()){
                menuItemToggleAnnotations.setText(hideAnnotationsString);
            }
            else{
                menuItemToggleAnnotations.setText(showAnnotationsString);
            }
        });
        return menuItemToggleAnnotations;
    }

    private EditableListView createLinkTypeProperties(CommunicationLink communicationLink){
        var deploymentDiagram = model.getDeploymentDiagram();
        var linkTypes = deploymentDiagram.getAllLinkTypes();
        var linkTypesView = new EditableListView("Link type:", linkTypes);
        
        var selectBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (LinkType) linkTypesView.getSelected();
                if(selected != null){
                    communicationLink.setLinkType(selected);
                }
            }
        };
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                deploymentDiagram.createLinkType("New link type", 1.0);
            }
        };

        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (LinkType) linkTypesView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) linkTypesView.getScene().getWindow(), 
                            "Confirm", "The link type \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        deploymentDiagram.removeLinkType(selected);
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (LinkType) linkTypesView.getSelected();
                if(selected != null){
                    var editWindow = new EditAllLinkTypesModalWindow(   (Stage) linkTypesView.getScene().getWindow(),
                                                                                            "Edit link type",
                                                                                            selected.nameProperty(),
                                                                                            selected.rateProperty());
                    editWindow.showAndWait();
                    linkTypesView.refresh();
                }
            }
        };

        linkTypesView.createButton("Select", selectBtnHandler, true);
        linkTypesView.createButton("Add", addBtnHandler, false);
        
        var removeButton = linkTypesView.createButton("Remove", removeBtnHandler, false);
        removeButton.disableProperty().bind(Bindings.size(linkTypes).lessThan(2));
        
        linkTypesView.createButton("Edit", editBtnHandler, true);
        
        return linkTypesView;
    }
    
    private EditableListView createFailureTypesProperties(CommunicationLink communicationLink){
        var failures = communicationLink.getLinkFailures();
        var failuresView = new EditableListView("Failure types:", failures);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                communicationLink.addLinkFailure(new ConnectionFailure("New failure", 0.01));
            }
        };

        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (ConnectionFailure) failuresView.getSelected();
                if(selected != null){
                    BooleanModalWindow confirmWindow = 
                            new BooleanModalWindow((Stage) failuresView.getScene().getWindow(), 
                            "Confirm", "The failure type \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        failures.remove(selected);
                    }
                }
            }
        };

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (ConnectionFailure) failuresView.getSelected();
                if(selected != null){
                    var editWindow = new EditFailureTypeModalWindow(   (Stage) failuresView.getScene().getWindow(),
                                                                                            "Edit failure type",
                                                                                            selected.nameProperty(),
                                                                                            selected.rateProperty());
                    editWindow.showAndWait();
                    failuresView.refresh();
                }
            }
        };

        failuresView.createButton("Add", addBtnHandler, false);
        failuresView.createButton("Remove", removeBtnHandler, true);
        failuresView.createButton("Edit", editBtnHandler, true);
        
        return failuresView;
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
                            "Confirm", "The state \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
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
                            "Rename state", "Type new name of the state \"" + Utils.shortenString(selected.toString(), 50) + "\":", selected.nameProperty());
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
                            "Confirm", "The transition \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
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
                            "Confirm", "The operation \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
                    confirmWindow.showAndWait();
                    if(confirmWindow.getResult()){
                        deploymentTarget.removeStateOperation(selected);
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
                            "Confirm", "The operation entry \"" + Utils.shortenString(selected.toString(), 50) + "\" will be deleted. Proceed?");
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
