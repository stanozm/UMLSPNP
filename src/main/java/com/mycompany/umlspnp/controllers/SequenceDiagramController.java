/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.common.Utils;
import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.common.ConnectionFailure;
import com.mycompany.umlspnp.models.common.OperationEntry;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.sequencediagram.ExecutionTime;
import com.mycompany.umlspnp.models.sequencediagram.Lifeline;
import com.mycompany.umlspnp.models.sequencediagram.Loop;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import com.mycompany.umlspnp.models.sequencediagram.MessageSize;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.common.AnnotationOwner;
import com.mycompany.umlspnp.views.common.layouts.BooleanModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditFailureTypeModalWindow;
import com.mycompany.umlspnp.views.common.layouts.EditableListView;
import com.mycompany.umlspnp.views.common.layouts.IntegerModalWindow;
import com.mycompany.umlspnp.views.sequencediagram.LifelineView;
import com.mycompany.umlspnp.views.sequencediagram.LoopView;
import com.mycompany.umlspnp.views.sequencediagram.MessageView;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
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
public class SequenceDiagramController {
    private final MainModel model;
    private final MainView view;
    
    public SequenceDiagramController(MainModel mainModel, MainView mainView){
        this.model = mainModel;
        this.view = mainView;
        
        sequenceDiagramInit(this.model.getSequenceDiagram());
    }
    
    private void sequenceDiagramInit(SequenceDiagram sequence){
        var sequenceDiagramView = view.getSequenceDiagramView();
        
        var connectionContainer = sequenceDiagramView.getConnectionContainer();
        connectionContainer.connectionProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null)
                    return;
                
                var firstElementID = connectionContainer.getFirstElementID();
                var secondElementID = connectionContainer.getSecondElementID();
                if(firstElementID != null){
                    var firstLifelineView = sequenceDiagramView.getLifelineView(firstElementID.intValue());
                    firstLifelineView.setSelected(true);
                    
                    if(secondElementID != null){
                        if(connectionContainer.getFirstElement() instanceof LifelineView){
                            var firstLifeline = sequence.getLifeline(firstElementID.intValue());
                            var secondLifeline = sequence.getLifeline(secondElementID.intValue());
                            sequence.createMessage(firstLifeline, secondLifeline);
                        }
                        firstLifelineView.setSelected(false);
                        connectionContainer.clear();
                    }
                }
            }
        });
        
        sequence.addMessagesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newMessage = (Message) change.getValueAdded();
                    var firstID = newMessage.getFirst().getObjectInfo().getID();
                    var secondID = newMessage.getSecond().getObjectInfo().getID();
                    var newMessageView = sequenceDiagramView.createMessage(firstID, secondID, newMessage.getObjectInfo().getID());

                    messageMenuInit(newMessageView);
                    messageAnnotationsInit(newMessage, newMessageView);
//                    createSampleAnnotations(newConnection);
                }
                if(change.wasRemoved()){
                    var removedMessage = (Message) change.getValueRemoved();
                    sequenceDiagramView.removeMessage(removedMessage.getObjectInfo().getID());
                }
            }
        });
        
        
        sequence.addLifelinesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof Lifeline){
                        var newLifeline = (Lifeline) newNode;

                        var newLifelineView = sequenceDiagramView.createLifelineView(newLifeline.getObjectInfo().getID());
                        lifelineInit(newLifelineView, newLifeline);
                        lifelineMenuInit(newLifelineView);
                        //deploymentTargetAnnotationsInit(newDT);
                    }
                }
                else if(change.wasRemoved()){
                    var removedLifeline = (Lifeline) change.getValueRemoved();
                    sequenceDiagramView.removeLifelineView(removedLifeline.getObjectInfo().getID());
                }
            }
        });

        Menu addNodeMenu = new Menu("Add Node");
        Menu lifelineMenu = new Menu("Lifeline");
        
        ObservableMap<Artifact, MenuItem> lifelineSubmenus = FXCollections.observableHashMap();
        
        var deployment = this.model.getDeploymentDiagram();
        deployment.addAllNodesChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = (Artifact) change.getValueAdded();
                    var newMenuItem = createLifelineSubmenu(sequence, newNode);
                    lifelineSubmenus.put(newNode, newMenuItem);
                }
                else if(change.wasRemoved()){
                    var removedNode = (Artifact) change.getValueRemoved();
                    lifelineSubmenus.remove(removedNode);
                }
            }
        });
        
        sequence.addLifelinesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newLifeline = (Lifeline) change.getValueAdded();
                    lifelineSubmenus.remove(newLifeline.getArtifact());
                }
                else if(change.wasRemoved()){
                    var removedLifeline = (Lifeline) change.getValueRemoved();
                    var removedNodeArtifact = removedLifeline.getArtifact();
                    if(deployment.getNode(removedNodeArtifact.getObjectInfo().getID()) != null){
                        var newMenuItem = createLifelineSubmenu(sequence, removedNodeArtifact);
                        lifelineSubmenus.put(removedNodeArtifact, newMenuItem);
                    }
                }
            }
        });

        lifelineSubmenus.addListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newItem = (MenuItem) change.getValueAdded();
                    lifelineMenu.getItems().add(newItem);
                }
                else if(change.wasRemoved()){
                    var removedItem = (MenuItem) change.getValueRemoved();
                    lifelineMenu.getItems().remove(removedItem);
                }
            }
        
        });
        
        lifelineMenu.disableProperty().bind(Bindings.isEmpty(lifelineSubmenus));


        var loopMenuItem = new MenuItem("Add loop");

        loopMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(loopMenuItem)){
                    sequence.createLoop();
                }
            }
        });
        
        var loopChangeListener = new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newLoop = (Loop) change.getValueAdded();
                    var newLoopView = sequenceDiagramView.createLoop(newLoop.getObjectInfo().getID());

                    loopInit(newLoop, newLoopView);
                }
                if(change.wasRemoved()){
                    var removedLoop = (Loop) change.getValueRemoved();
                    sequenceDiagramView.removeLoop(removedLoop.getObjectInfo().getID());
                }
            }
        };
        sequence.addLoopsChangeListener(loopChangeListener);
        
        addNodeMenu.getItems().addAll(lifelineMenu, loopMenuItem);
        sequenceDiagramView.addMenu(addNodeMenu);
    }
    
    private void loopInit(Loop loop, LoopView loopView){
        var sequence = this.model.getSequenceDiagram();
        var loopObjectID = loop.getObjectInfo().getID();
        
        loopView.getNameProperty().bind(loop.nameProperty());
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            sequence.removeLoop(loopObjectID);
        });
        loopView.addMenuItem(menuItemDelete);

        
        MenuItem menuItemIterations = new MenuItem("Change iterations");
        menuItemIterations.setOnAction((e) -> {
            var iterationsWindow = new IntegerModalWindow(   (Stage) loopView.getScene().getWindow(),
                                                                                    "Edit loop iterations",
                                                                                    "Iterations",
                                                                                    2,
                                                                                    null,
                                                                                    loop.iterationsProperty());
            iterationsWindow.showAndWait();
        });
        loopView.addMenuItem(menuItemIterations);
    }
    
    private MenuItem createLifelineSubmenu(SequenceDiagram sequence, Artifact artifact){
        var submenu = new MenuItem(artifact.getNameProperty().getValue());
        submenu.textProperty().bind(artifact.getNameProperty());
        
        EventHandler<ActionEvent> submenuEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent tt) {
                if(tt.getSource().equals(submenu)){
                    sequence.createLifeline(artifact);
                }
            }
        };
        submenu.setOnAction(submenuEventHandler);
        
        return submenu;
    }
    
    private void lifelineInit(LifelineView newLifelineView, Lifeline newLifeline) {
        var sequenceDiagram = model.getSequenceDiagram();
        var deploymentDiagram = model.getDeploymentDiagram();
        var sequenceDiagramView = view.getSequenceDiagramView();

        newLifelineView.getNameProperty().bind(newLifeline.nameProperty());

        var connectionContainer = sequenceDiagramView.getConnectionContainer();
        
        sequenceDiagramView.registerNodeToSelect(newLifelineView, (e) -> {
            var startElement = (LifelineView) connectionContainer.getFirstElement();
            if(startElement != null){
                if(startElement != newLifelineView && startElement.getClass().equals(newLifelineView.getClass())){
                    var startLifeline = sequenceDiagram.getLifeline(startElement.getObjectInfo().getID());
                    var startArtifact = startLifeline.getArtifact();
                    var newLifelineArtifact = newLifeline.getArtifact();
                    if(deploymentDiagram.areNodesConnected(startArtifact, newLifelineArtifact)){
                        connectionContainer.setSecondElement(newLifelineView);
                        return;
                    }
                    else{
                        System.err.println("Unable to create connection. Nodes in deployment diagram are not connected.");
                    }
                }
                else{
                    System.err.println("Unable to create connection. Select suitable destination node.");
                }
                startElement.setSelected(false);
                connectionContainer.clear();
            }
        });
    }
    
    private void lifelineMenuInit(LifelineView lifelineView){
        var sequenceDiagram = this.model.getSequenceDiagram();
        var lifelineObjectID = lifelineView.getObjectInfo().getID();
        var lifeline = sequenceDiagram.getLifeline(lifelineObjectID);
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            sequenceDiagram.removeLifeline(lifelineObjectID);
        });
        lifelineView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemConnect = new MenuItem("Create message");
        menuItemConnect.setOnAction((e) -> {
            this.view.getSequenceDiagramView().startConnection(lifelineView);
        });
        lifelineView.addMenuItem(menuItemConnect);
    }
    
    
    private void messageMenuInit(MessageView messageView){
        var sequenceDiagram = this.model.getSequenceDiagram();
        var messageObjectID = messageView.getObjectInfo().getID();
        var message = sequenceDiagram.getMessage(messageObjectID);
        
        if(message == null){
            System.err.println("Message with id " + messageObjectID + " was not found!");
            return;
        }
        
        MenuItem menuItemRename = new MenuItem("Rename");
        menuItemRename.setOnAction((e) -> {
            this.view.createStringModalWindow("Rename", "New name", message.nameProperty());
        });
        messageView.nameProperty().bind(message.nameProperty());
        messageView.addMenuItem(menuItemRename);
        
        MenuItem menuItemDelete = new MenuItem("Delete message");
        menuItemDelete.setOnAction((e) -> {
            sequenceDiagram.removeMessage(messageObjectID);
        });
        messageView.addMenuItem(menuItemDelete);    

        // TODO dupliace with code in deployment controller
        MenuItem menuItemToggleAnnotations = createToggleAnnotationsMenuItem(messageView);
        messageView.addMenuItem(menuItemToggleAnnotations);
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        messageView.addMenuItem(separator);
        
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction((e) -> {
            var executionTimeView = createExecutionTimeProperties(message);
            var messageSizeView = createMessageSizeProperties(message);
            var operationTypeView = createOperationTypeProperties(message);
            var failureTypesView = createMessageFailureTypesProperties(message);
            
            ArrayList<EditableListView> sections = new ArrayList();
            sections.add(executionTimeView);
            sections.add(messageSizeView);
            sections.add(operationTypeView);
            sections.add(failureTypesView);
            
            this.view.createPropertiesModalWindow("\"" + message.nameProperty().getValue() + "\" properties", sections);

        });
        messageView.addMenuItem(menuProperties);
    }
    
    private void messageAnnotationsInit(Message message, MessageView messageView){
        messageView.getExecutionTimeAnnotation().setItems(message.getExecutionTimeList());
        messageView.getOperationTypeAnnotation().setItems(message.getOperationTypeList());
        messageView.getFailureTypesAnnotation().setItems(message.getMessageFailures());
        messageView.getMessageSizeAnnotation().setItems(message.getMessageSizeList());
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
    
    private EditableListView createExecutionTimeProperties(Message message){
        var executionTimeList = message.getExecutionTimeList();
        var executionTimeView = new EditableListView("Execution time:", executionTimeList);

        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (ExecutionTime) executionTimeView.getSelected();
                if(selected != null){
                    var editWindow = new IntegerModalWindow(   (Stage) executionTimeView.getScene().getWindow(),
                                                                                            "Edit execution time",
                                                                                            "Execution time",
                                                                                            0,
                                                                                            null,
                                                                                            selected.executionTimeProperty());
                    editWindow.showAndWait();
                    executionTimeView.refresh();
                }
            }
        };

        executionTimeView.createButton("Edit", editBtnHandler, true);
        
        return executionTimeView;
    }
    
    private EditableListView createMessageSizeProperties(Message message){
        var messageSizeList = message.getMessageSizeList();
        var messageSizeView = new EditableListView("Message size:", messageSizeList);

        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                message.setMessageSize(0);
            }
        };

        var removeBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) messageSizeView.getScene().getWindow(), 
                        "Confirm", "The message size will be deleted. Proceed?");
                confirmWindow.showAndWait();
                if(confirmWindow.getResult()){
                    message.removeMessageSize();
                }
            }
        };
        
        var editBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (MessageSize) messageSizeView.getSelected();
                if(selected != null){
                    var editWindow = new IntegerModalWindow(   (Stage) messageSizeView.getScene().getWindow(),
                                                                                            "Edit message size",
                                                                                            "Message size",
                                                                                            0,
                                                                                            null,
                                                                                            selected.messageSizeProperty());
                    editWindow.showAndWait();
                    messageSizeView.refresh();
                }
            }
        };

        var addBtn = messageSizeView.createButton("Add", addBtnHandler, false);
        addBtn.disableProperty().bind(Bindings.size(message.getMessageSizeList()).greaterThan(0));

        var removeBtn = messageSizeView.createButton("Remove", removeBtnHandler, false);
        removeBtn.disableProperty().bind(Bindings.size(message.getMessageSizeList()).lessThan(1));
        
        messageSizeView.createButton("Edit", editBtnHandler, true);
        
        return messageSizeView;
    }
    
    // TODO duplicate in deployment controller
    private EditableListView createMessageFailureTypesProperties(Message message){
        var failures = message.getMessageFailures();
        var failuresView = new EditableListView("Failure types:", failures);
        
        var addBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                message.addMessageFailure(new ConnectionFailure("New failure", 0.01));
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
    
    private EditableListView createOperationTypeProperties(Message message){
        var operationEntriesList = message.getOperationEntries();
        var operationEntriesView = new EditableListView("Operation type:", operationEntriesList);

        var selectBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                var selected = (OperationEntry) operationEntriesView.getSelected();
                if(selected != null){
                    message.setOperationType(selected);
                }
            }
        };
        
        var clearBtnHandler = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                message.removeOperationType();
            }
        };

        operationEntriesView.createButton("Select", selectBtnHandler, true);
        
        var clearBtn = operationEntriesView.createButton("Clear", clearBtnHandler, false);
        clearBtn.disableProperty().bind(Bindings.size(message.getOperationTypeList()).lessThan(1));
        
        return operationEntriesView;
    }
}
