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
import com.mycompany.umlspnp.models.sequencediagram.Activation;
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
import com.mycompany.umlspnp.views.sequencediagram.ActivationView;
import com.mycompany.umlspnp.views.sequencediagram.LifelineView;
import com.mycompany.umlspnp.views.sequencediagram.LoopView;
import com.mycompany.umlspnp.views.sequencediagram.MessageView;
import java.util.ArrayList;
import java.util.Collections;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
    
    /***  ONLY FOR TESTING  ***/
    public void createSampleData() {
        var deployment = model.getDeploymentDiagram();
        var sequence = model.getSequenceDiagram();
        var sequenceView = view.getSequenceDiagramView();
        
        var A = deployment.getDeploymentTarget(1);
        var AA = deployment.getDeploymentTarget(2);
        var AAA = deployment.getDeploymentTarget(3);
        var B = deployment.getDeploymentTarget(4);
        var BB = deployment.getDeploymentTarget(5);
        var BBB = deployment.getDeploymentTarget(6);
        
        var lifelineA = sequence.createLifeline(A);
        var lifelineAA = sequence.createLifeline(AA);
        var lifelineAAA = sequence.createLifeline(AAA);
        var lifelineB = sequence.createLifeline(B);
        
        var activationA = lifelineA.createActivation();
        var activationAA = lifelineAA.createActivation();
        var activationAAA = lifelineAAA.createActivation();
        var activationB = lifelineB.createActivation();

        // Sequence diagram for tree building algorithm
        AA.getNameProperty().setValue("B");
        AAA.getNameProperty().setValue("C");
        B.getNameProperty().setValue("D");

        var lifeA = lifelineA;
        var lifeA_view = sequenceView.getLifelineView(lifeA.getObjectInfo().getID());

        var lifeB = lifelineAA;
        var lifeB_view = sequenceView.getLifelineView(lifeB.getObjectInfo().getID());
        lifeB_view.setTranslateX(300);
        
        var lifeC = lifelineAAA;
        var lifeC_view = sequenceView.getLifelineView(lifeC.getObjectInfo().getID());
        lifeC_view.setTranslateX(600);
        
        var lifeD = lifelineB;
        var lifeD_view = sequenceView.getLifelineView(lifeD.getObjectInfo().getID());
        lifeD_view.setTranslateX(900);

        
        var aA = activationA;
        var aA_view = sequenceView.getActivationView(aA.getObjectInfo().getID());
        aA_view.changeDimensions(aA_view.getWidth(), 400);
        
        var ACTIVATION_BASE_Y = aA_view.getTranslateY();
        
        var aB = activationAA;
        var aB_view = sequenceView.getActivationView(aB.getObjectInfo().getID());
        aB_view.changeDimensions(aB_view.getWidth(), 400);
        
        var aC1 = activationAAA;
        var aC1_view = sequenceView.getActivationView(aC1.getObjectInfo().getID());
        aC1_view.changeDimensions(aC1_view.getWidth(), 150);
        aC1_view.setTranslateY(aC1_view.getTranslateY() + 20);
        
        var aC2 = lifeC.createActivation();
        var aC2_view = sequenceView.getActivationView(aC2.getObjectInfo().getID());
        aC2_view.changeDimensions(aC2_view.getWidth(), 75);
        aC2_view.setTranslateY(aC1_view.getTranslateY() + aC1_view.getHeight() + 40);
        
        var aD1 = activationB;
        var aD1_view = sequenceView.getActivationView(aD1.getObjectInfo().getID());
        aD1_view.changeDimensions(aD1_view.getWidth(), 30);
        aD1_view.setTranslateY(aD1_view.getTranslateY() + 40);
        
        var aD2 = lifeD.createActivation();
        var aD2_view = sequenceView.getActivationView(aD2.getObjectInfo().getID());
        aD2_view.changeDimensions(aD2_view.getWidth(), 75);
        aD2_view.setTranslateY(aD1_view.getTranslateY() + aD1_view.getHeight() + 40);
        
        var aD3 = lifeD.createActivation();
        var aD3_view = sequenceView.getActivationView(aD3.getObjectInfo().getID());
        aD3_view.changeDimensions(aD3_view.getWidth(), 30);
        aD3_view.setTranslateY(aD2_view.getTranslateY() + aD2_view.getHeight() + 30);
        
        
        var mess1 = sequence.createMessage(activationA, activationAA);
        mess1.nameProperty().setValue("1");
        
        var mess1_1 = sequence.createMessage(activationAA, activationAAA);
        var mess1_1_view = sequenceView.getConnection(mess1_1.getObjectInfo().getID());
        mess1_1_view.getSourceConnectionSlot().setTranslateY(aC1_view.getTranslateY() - ACTIVATION_BASE_Y);
        mess1_1.nameProperty().setValue("11");
        
        var mess1_1_1 = sequence.createMessage(activationAAA, activationB);
        var mess1_1_1_view = sequenceView.getConnection(mess1_1_1.getObjectInfo().getID());
        mess1_1_1_view.getSourceConnectionSlot().setTranslateY((aD1_view.getTranslateY() - ACTIVATION_BASE_Y) - (aC1_view.getTranslateY() - ACTIVATION_BASE_Y));
        mess1_1_1.nameProperty().setValue("111");
        
        var mess1_1_2 = sequence.createMessage(aC1, aC1);
        var mess1_1_2_view = sequenceView.getConnection(mess1_1_2.getObjectInfo().getID());
        mess1_1_2_view.getDestinationConnectionSlot().setTranslateY(mess1_1_1_view.getSourceConnectionSlot().getTranslateY() + 30);
        mess1_1_2.nameProperty().setValue("112");
        
        var mess1_1_3 = sequence.createMessage(aC1, aD2);
        var mess1_1_3_view = sequenceView.getConnection(mess1_1_3.getObjectInfo().getID());
        mess1_1_3_view.getSourceConnectionSlot().setTranslateY((aD2_view.getTranslateY() - ACTIVATION_BASE_Y) - (aC1_view.getTranslateY() - ACTIVATION_BASE_Y));
        mess1_1_3.nameProperty().setValue("113");
        
        var mess1_1_3_1 = sequence.createMessage(aD2, aD2);
        var mess1_1_3_1_view = sequenceView.getConnection(mess1_1_3_1.getObjectInfo().getID());
        mess1_1_3_1_view.getDestinationConnectionSlot().setTranslateY(20);
        mess1_1_3_1.nameProperty().setValue("1131");
        
        var mess1_2 = sequence.createMessage(aB, aC2);
        var mess1_2_view = sequenceView.getConnection(mess1_2.getObjectInfo().getID());
        mess1_2_view.getSourceConnectionSlot().setTranslateY((aC2_view.getTranslateY() - ACTIVATION_BASE_Y));
        mess1_2.nameProperty().setValue("12");
        
        var mess1_2_1 = sequence.createMessage(aC2, aD3);
        var mess1_2_1_view = sequenceView.getConnection(mess1_2_1.getObjectInfo().getID());
        mess1_2_1_view.getSourceConnectionSlot().setTranslateY((aD3_view.getTranslateY() - ACTIVATION_BASE_Y) - (aC2_view.getTranslateY() - ACTIVATION_BASE_Y) + 10);
        mess1_2_1_view.getDestinationConnectionSlot().setTranslateY(mess1_2_1_view.getSourceConnectionSlot().getTranslateY());
        mess1_2_1.nameProperty().setValue("121");
        
        var mess2 = sequence.createMessage(aA, aB);
        var mess2_view = sequenceView.getConnection(mess2.getObjectInfo().getID());
        mess2_view.getSourceConnectionSlot().setTranslateY((aA_view.getTranslateY() - ACTIVATION_BASE_Y) + (aA_view.getHeight()) - 40);
        mess2_view.getDestinationConnectionSlot().setTranslateY(mess2_view.getSourceConnectionSlot().getTranslateY());
        mess2.nameProperty().setValue("2");
        
        var mess2_1 = sequence.createMessage(aB, aB);
        var mess2_1_view = sequenceView.getConnection(mess2_1.getObjectInfo().getID());
        mess2_1_view.getDestinationConnectionSlot().setTranslateY(mess2_view.getSourceConnectionSlot().getTranslateY() + 15);
        mess2_1.nameProperty().setValue("21");
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
                    var firstActivationView = sequenceDiagramView.getActivationView(firstElementID.intValue());
                    firstActivationView.setSelected(true);
                    
                    if(secondElementID != null){
                        if(connectionContainer.getFirstElement() instanceof ActivationView){
                            var firstActivation = sequence.getActivation(firstElementID.intValue());
                            var secondActivation = sequence.getActivation(secondElementID.intValue());
                            sequence.createMessage(firstActivation, secondActivation);
                        }
                        firstActivationView.setSelected(false);
                        connectionContainer.clear();
                    }
                }
            }
        });
        
        sequence.getSortedMessages().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                sequence.getSortedMessages().forEach(message -> {
                    message.setOrder(sequence.getSortedMessages().indexOf(message));
                });
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

                    messageInit(newMessage, newMessageView);
                    messageMenuInit(newMessage, newMessageView);
                    messageAnnotationsInit(newMessage, newMessageView);

                    sortMessages(); // Does not work - should be async after view init. However that results in yet in another JavaFX crash
//                    createSampleAnnotations(newConnection);
                }
                if(change.wasRemoved()){
                    var removedMessage = (Message) change.getValueRemoved();
                    sequenceDiagramView.removeMessage(removedMessage.getObjectInfo().getID());
                    sortMessages();
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

        sequenceDiagramView.getHighestLifelineProperty().addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                if(newValue == null)
                    sequence.setHighestLevelLifeline(Integer.MIN_VALUE);
                else
                    sequence.setHighestLevelLifeline(((LifelineView) newValue).getObjectInfo().getID());
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
                    
                    sequence.removeLifeline(removedNode);
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
        var sequenceDiagramView = view.getSequenceDiagramView();
        
        newLifelineView.getNameProperty().bind(newLifeline.nameProperty());

        newLifeline.addActivationsChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newNode = change.getValueAdded();
                    if(newNode instanceof Activation){
                        var newActivation = (Activation) newNode;

                        var lifelineView = sequenceDiagramView.getLifelineView(newActivation.getLifeline().getObjectInfo().getID());
                        var newActivationView = lifelineView.createActivationView(newActivation.getObjectInfo().getID());
                        
                        activationInit(newActivationView, newActivation);
                        activationMenuInit(newActivationView);
                        //deploymentTargetAnnotationsInit(newDT);
                    }
                }
                else if(change.wasRemoved()){
                    var removedActivation = (Activation) change.getValueRemoved();
                    var lifeline = removedActivation.getLifeline();
                    var lifelineView = sequenceDiagramView.getLifelineView(lifeline.getObjectInfo().getID());
                    lifelineView.removeActivationView(removedActivation.getObjectInfo().getID());
                }
            }
        });
    }
    
    private void lifelineMenuInit(LifelineView lifelineView){
        var sequenceDiagram = this.model.getSequenceDiagram();
        var lifelineObjectID = lifelineView.getObjectInfo().getID();
        var lifeline = sequenceDiagram.getLifeline(lifelineObjectID);
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) lifelineView.getScene().getWindow(), 
                        "Confirm", "The lifeline \"" + Utils.shortenString(lifelineView.getNameProperty().getValue(), 50) + "\" will be deleted. Proceed?");
            confirmWindow.showAndWait();
            if(confirmWindow.getResult()){
                sequenceDiagram.removeLifeline(lifelineObjectID);
            }
        });
        lifelineView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemCreateActivation = new MenuItem("Create activation");
        menuItemCreateActivation.setOnAction((e) -> {
            lifeline.createActivation();
        });
        lifelineView.addMenuItem(menuItemCreateActivation);
    }
    
    private void activationInit(ActivationView newActivationView, Activation newActivation) {
        var sequenceDiagram = model.getSequenceDiagram();
        var deploymentDiagram = model.getDeploymentDiagram();
        var sequenceDiagramView = view.getSequenceDiagramView();

        var connectionContainer = sequenceDiagramView.getConnectionContainer();
        
        sequenceDiagramView.registerNodeToSelect(newActivationView, (e) -> {
            var startElement = (ActivationView) connectionContainer.getFirstElement();
            if(startElement != null){
                if(startElement.getClass().equals(newActivationView.getClass())){
                    var startActivation = sequenceDiagram.getActivation(startElement.getObjectInfo().getID());
                    var startArtifact = startActivation.getLifeline().getArtifact();
                    var newArtifact = newActivation.getLifeline().getArtifact();
                    if(startActivation == newActivation || deploymentDiagram.areNodesConnected(startArtifact, newArtifact)){
                        connectionContainer.setSecondElement(newActivationView);
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
    
    private void activationMenuInit(ActivationView activationView){
        var sequenceDiagram = this.model.getSequenceDiagram();
        var activationObjectID = activationView.getObjectInfo().getID();
        var lifeline = sequenceDiagram.getActivation(activationObjectID).getLifeline();
        
        MenuItem menuItemDelete = new MenuItem("Delete activation");
        menuItemDelete.setOnAction((e) -> {
            BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) activationView.getScene().getWindow(), 
                        "Confirm", "The activation of lifeline \"" + Utils.shortenString(lifeline.nameProperty().getValue(), 50) + "\" will be deleted. Proceed?");
            confirmWindow.showAndWait();
            if(confirmWindow.getResult()){
                sequenceDiagram.removeActivation(activationObjectID);
            }
        });
        activationView.addMenuItem(menuItemDelete);
        
        MenuItem menuItemConnect = new MenuItem("Create message");
        menuItemConnect.setOnAction((e) -> {
            this.view.getSequenceDiagramView().startConnection(activationView);
        });
        activationView.addMenuItem(menuItemConnect);
    }
    
    private void sortMessages() {
        var sequenceDiagram = this.model.getSequenceDiagram();
        var sequenceDiagramView = this.view.getSequenceDiagramView();

        Collections.sort(sequenceDiagram.getSortedMessages(), (m1, m2) -> {
            Double first = sequenceDiagramView.getConnection(m1.getObjectInfo().getID()).getSourceConnectionSlot().getLocalToSceneTransform().getTy();
            Double second = sequenceDiagramView.getConnection(m2.getObjectInfo().getID()).getSourceConnectionSlot().getLocalToSceneTransform().getTy();
            return first.compareTo(second);
        });
    }
    
    private void messageInit(Message message, MessageView messageView){
        messageView.getSourceConnectionSlot().localToSceneTransformProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                sortMessages();
            }
        });

        // Needs to be run async, otherwise it breaks because of not fully initialized view
        Platform.runLater(() -> {
            message.orderProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                    if(message.isLeafMessage()) {
                        messageView.getExecutionTimeAnnotation().setDisplayed(true);
                        messageView.getFailureTypesAnnotation().setDisplayed(true);
                    }
                    else {
                        messageView.getExecutionTimeAnnotation().setDisplayed(false);
                        messageView.getFailureTypesAnnotation().setDisplayed(false);
                    }
                }
            });
        });

        messageView.nameProperty().bind(message.orderProperty().asString().concat(". ").concat(message.nameProperty()));
    }
    
    private void messageMenuInit(Message message, MessageView messageView){
        var sequenceDiagram = this.model.getSequenceDiagram();
        var messageObjectID = messageView.getObjectInfo().getID();
        
        if(message == null){
            System.err.println("Message with id " + messageObjectID + " was not found!");
            return;
        }
        
        MenuItem menuItemRename = new MenuItem("Rename");
        menuItemRename.setOnAction((e) -> {
            this.view.createStringModalWindow("Rename", "New name", message.nameProperty(), null);
        });
        messageView.addMenuItem(menuItemRename);
        
        MenuItem menuItemDelete = new MenuItem("Delete message");
        menuItemDelete.setOnAction((e) -> {
            BooleanModalWindow confirmWindow = 
                        new BooleanModalWindow((Stage) messageView.getScene().getWindow(), 
                        "Confirm", "The message \"" + Utils.shortenString(message.nameProperty().getValue(), 50) + "\"will be deleted. Proceed?");
            confirmWindow.showAndWait();
            if(confirmWindow.getResult()){
                sequenceDiagram.removeMessage(messageObjectID);
            }
        });
        messageView.addMenuItem(menuItemDelete);    

        // TODO dupliace with code in deployment controller
        MenuItem menuItemToggleAnnotations = createToggleAnnotationsMenuItem(messageView);
        messageView.addMenuItem(menuItemToggleAnnotations);
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        messageView.addMenuItem(separator);
        
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction((e) -> {
            boolean messageIsLeaf = message.isLeafMessage();

            var executionTimeView = createExecutionTimeProperties(message);
            var messageSizeView = createMessageSizeProperties(message);
            var operationTypeView = createOperationTypeProperties(message);
            var failureTypesView = createMessageFailureTypesProperties(message);
            
            ArrayList<EditableListView> sections = new ArrayList();
            if(messageIsLeaf)
                sections.add(executionTimeView);
            sections.add(messageSizeView);
            sections.add(operationTypeView);
            if(messageIsLeaf)
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
