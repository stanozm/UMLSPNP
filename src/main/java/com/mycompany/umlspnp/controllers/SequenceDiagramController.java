/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.umlspnp.controllers;

import com.mycompany.umlspnp.models.MainModel;
import com.mycompany.umlspnp.models.deploymentdiagram.Artifact;
import com.mycompany.umlspnp.models.sequencediagram.Lifeline;
import com.mycompany.umlspnp.models.sequencediagram.Message;
import com.mycompany.umlspnp.models.sequencediagram.SequenceDiagram;
import com.mycompany.umlspnp.views.MainView;
import com.mycompany.umlspnp.views.sequencediagram.LifelineView;
import com.mycompany.umlspnp.views.sequencediagram.MessageView;
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
//                    communicationLinkAnnotationsInit(newConnection);
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

        addNodeMenu.getItems().addAll(lifelineMenu);
        sequenceDiagramView.addMenu(addNodeMenu);
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
            this.view.createStringModalWindow("Rename", "New name", messageView.nameProperty());
        });
        messageView.addMenuItem(menuItemRename);
        
        MenuItem menuItemDelete = new MenuItem("Delete message");
        menuItemDelete.setOnAction((e) -> {
            sequenceDiagram.removeMessage(messageObjectID);
        });
        messageView.addMenuItem(menuItemDelete);    


//        MenuItem menuItemToggleAnnotations = createToggleAnnotationsMenuItem(messageView);
//        messageView.addMenuItem(menuItemToggleAnnotations);
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        messageView.addMenuItem(separator);
        
        MenuItem menuProperties = new MenuItem("Properties");
        menuProperties.setOnAction((e) -> {
//            var linkTypesView = createLinkTypeProperties(communicationLink);
//            var failuresView = createFailureTypesProperties(communicationLink);
//
//            ArrayList<EditableListView> sections = new ArrayList();
//            sections.add(failuresView);
//            sections.add(linkTypesView);
//
//            this.view.createPropertiesModalWindow("[" + communicationLink.getFirst().getNameProperty().getValue() + 
//                    " > " + communicationLink.getSecond().getNameProperty().getValue() +  "] properties", sections);

        });
        messageView.addMenuItem(menuProperties);
    }
}
