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
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
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
        
        deployment.addDeploymentTargetsChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    DeploymentTarget newDT = (DeploymentTarget) change.getValueAdded();
                    var newDTView = deploymentDiagramView.CreateDeploymentTarget(newDT.getObjectInfo().getID());
                    newDTView.getNameProperty().bind(newDT.getNameProperty());
                    deploymentTargetMenuInit(newDTView);
                }
                else if(change.wasRemoved()){
                    DeploymentTarget removedDT = (DeploymentTarget) change.getValueRemoved();
                    deploymentDiagramView.deleteDeploymentTargetView(removedDT.getObjectInfo().getID());
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

                    //var dt2 = deploymentDiagramView.CreateDeploymentTarget();
                    //var c = deploymentDiagramView.CreateConnection(dt1, dt2);
                }
            }
        };
        
        deviceMenuItem.setOnAction(menuEventHandler);
        addNodeMenu.getItems().addAll(deviceMenuItem);
        
        deploymentDiagramView.addMenu(addNodeMenu);
    }
    
    private void deploymentTargetListenerInit(DeploymentTarget DT){
        var deploymentDiagramView = view.getDeploymentDiagramView();
        
        DT.addInnerNodesChangeListener(new MapChangeListener(){
            @Override
            public void onChanged(MapChangeListener.Change change) {
                if(change.wasAdded()){
                    if(change.getValueAdded() instanceof Artifact) {
                        Artifact newArtifact = (Artifact) change.getValueAdded();
                        var DTView = deploymentDiagramView.getDeploymentTargetRecursive(DT.getObjectInfo().getID());
                        var newArtifactView = DTView.CreateArtifact(newArtifact.getObjectInfo().getID());
                        newArtifactView.getNameProperty().bind(newArtifact.getNameProperty());
                        artifactMenuInit(newArtifactView);
                    }
                    else if (change.getValueAdded() instanceof DeploymentTarget) {
                        DeploymentTarget newInnerDT = (DeploymentTarget) change.getValueAdded();
                        var DTView = deploymentDiagramView.getDeploymentTargetRecursive(DT.getObjectInfo().getID());
                        var newDTView = DTView.CreateDeploymentTarget(newInnerDT.getObjectInfo().getID());
                        newDTView.getNameProperty().bind(newInnerDT.getNameProperty());
                        deploymentTargetMenuInit(newDTView);
                    }
                }
                else if(change.wasRemoved()){
                    if(change.getValueRemoved() instanceof Artifact) {
                        Artifact removedArtifact = (Artifact) change.getValueRemoved();
                        var DTView = deploymentDiagramView.getDeploymentTargetRecursive(DT.getObjectInfo().getID());
                        DTView.deleteInnerNode(removedArtifact.getObjectInfo().getID());
                    }
                    else if (change.getValueRemoved() instanceof DeploymentTarget) {
                        DeploymentTarget removedDT = (DeploymentTarget) change.getValueRemoved();
                        var DTView = deploymentDiagramView.getDeploymentTargetRecursive(DT.getObjectInfo().getID());
                        DTView.deleteInnerNode(removedDT.getObjectInfo().getID());
                    }
                }
            }
        });
    }
    
    private void deploymentTargetMenuInit(DeploymentTargetView deploymentTargetView){
        MenuItem menuItemDelete = new MenuItem("Delete");
        menuItemDelete.setOnAction((e) -> {
            var deploymentDiagram = this.model.getDeploymentDiagram();
            deploymentDiagram.deleteDeploymentTargetRecursive(deploymentTargetView.getObjectInfo().getID());
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
