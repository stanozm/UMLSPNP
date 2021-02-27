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
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

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
        
        /*
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
                        var firstDT = deployment.getDeploymentTarget(firstElementID.intValue());
                        var secondDT = deployment.getDeploymentTarget(secondElementID.intValue());
                        if(deployment.areNodesConnected(firstDT, secondDT)){
                            System.err.println("Error: Nodes \"" + firstDT.getNameProperty().getValue() + "\" and \"" + 
                                     firstDT.getNameProperty().getValue() + "\" are already connected!");
                        }
                        else{
                            deployment.createCommunicationLink(firstDT, secondDT);
                        }
                    }
                    connectionContainer.clear();
                }
            }
        });
        */
        
        sequence.addMessagesListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newMessage = (Message) change.getValueAdded();
                    var firstID = newMessage.getFirst().getObjectInfo().getID();
                    var secondID = newMessage.getSecond().getObjectInfo().getID();
                    var newConnectionView = sequenceDiagramView.createMessage(firstID, secondID, newMessage.getObjectInfo().getID());

//                    communicationTargetMenuInit(newConnectionView);
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
                        newLifelineView.getNameProperty().bind(newLifeline.nameProperty());
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
    
    private void lifelineMenuInit(LifelineView lifelineView){
        var sequenceDiagram = this.model.getSequenceDiagram();
        var lifelineObjectID = lifelineView.getObjectInfo().getID();
        var lifeline = sequenceDiagram.getLifeline(lifelineObjectID);
        
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            sequenceDiagram.removeLifeline(lifelineObjectID);
        });
        lifelineView.addMenuItem(menuItemDelete);

        
        var menuAddMessage = createAddMessageMenu(lifeline);
        lifelineView.addMenuItem(menuAddMessage);
    }
    
    private Menu createAddMessageMenu(Lifeline lifeline) {
        var deploymentDiagram = this.model.getDeploymentDiagram();
        var sequenceDiagram = this.model.getSequenceDiagram();
        
        Menu messagesMenu = new Menu("Add message");

        // TODO: What connections (messages) should be possible to make?
        ObservableMap<Lifeline, MenuItem> subitems = FXCollections.observableHashMap();

        var subitemsListener = new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newItem = (MenuItem) change.getValueAdded();
                    messagesMenu.getItems().add(newItem);
                }
                else if(change.wasRemoved()){
                    var removedItem = (MenuItem) change.getValueRemoved();
                    messagesMenu.getItems().remove(removedItem);
                }
            }
        };
        
        subitems.addListener(subitemsListener);
        
        messagesMenu.disableProperty().bind(Bindings.isEmpty(subitems));

        for(var connectedArtifact : lifeline.getArtifact().getConnectedNodes()){
            Lifeline connectedLifeline = sequenceDiagram.getLifeline(connectedArtifact);
            if(connectedLifeline != null) {
                subitems.put(connectedLifeline, createAddMessageSubitem(lifeline, connectedLifeline));
            }
        }
        
        var lifelinesListener = new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    var newLifeline = (Lifeline) change.getValueAdded();
                    if(deploymentDiagram.areNodesConnected(lifeline.getArtifact(), newLifeline.getArtifact())) {
//                        System.err.println(lifeline.nameProperty().getValue() + " is connected to " + newLifeline.nameProperty().getValue());
                        subitems.put(newLifeline, createAddMessageSubitem(lifeline, newLifeline));
                    }
                }
                else if(change.wasRemoved()){
                    var removedLifeline = (Lifeline) change.getValueRemoved();
                    subitems.remove(removedLifeline);
                    if(removedLifeline.equals(lifeline)){
                        subitems.removeListener(subitemsListener);
                        sequenceDiagram.removeLifelinesListener(this);
                    }
                }
            }
        };
        
        sequenceDiagram.addLifelinesListener(lifelinesListener);
        
        return messagesMenu;
    }
    
    private MenuItem createAddMessageSubitem(Lifeline originalLifeline, Lifeline newLifeline) {
        var sequenceDiagram = this.model.getSequenceDiagram();
        
        var nameProperty = newLifeline.nameProperty();
        var subitem = new MenuItem(nameProperty.getValue());
        subitem.textProperty().bind(nameProperty);
        
        subitem.setOnAction((e) -> {
            sequenceDiagram.createMessage(originalLifeline, newLifeline);
        });
        
        return subitem;
    }
}
